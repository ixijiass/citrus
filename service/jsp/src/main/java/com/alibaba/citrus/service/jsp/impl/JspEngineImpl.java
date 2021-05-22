/*
 * Copyright 2010 Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.alibaba.citrus.service.jsp.impl;

import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.FileUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.alibaba.citrus.service.AbstractService;
import com.alibaba.citrus.service.jsp.JspEngine;
import com.alibaba.citrus.service.template.TemplateContext;
import com.alibaba.citrus.service.template.TemplateException;
import com.alibaba.citrus.service.template.TemplateNotFoundException;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * Jspģ�������ʵ�֡�
 * 
 * @author Michael Zhou
 */
public class JspEngineImpl extends AbstractService<JspEngine> implements JspEngine, ResourceLoaderAware,
        InitializingBean {
    private final ServletContext servletContext;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private ResourceLoader resourceLoader;
    private String contextRoot;
    private String path;

    /**
     * ����jsp���档
     * <p>
     * ��Ҫע����ǣ���������jsp����Ĳ��������ǡ�ȫ�֡�������ģ������ǡ�request��������ġ���һ�����
     * <code>RequestContextChainingService</code>����֤��
     * </p>
     */
    public JspEngineImpl(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) {
        this.servletContext = assertNotNull(servletContext, "servletContext");
        this.request = assertProxy(assertNotNull(request, "request"));
        this.response = assertProxy(assertNotNull(response, "response"));
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setPath(String path) {
        this.path = trimToNull(path);
    }

    @Override
    protected void init() throws Exception {
        assertNotNull(resourceLoader, "resourceLoader");

        // ȡ������·������ԣ���
        if (path == null) {
            path = "/templates";
        }

        // ���·������"/"��β��
        path = normalizeAbsolutePath(path + "/");

        // ȡ��webroot��Ŀ¼��URL
        URL url = servletContext.getResource("/");

        if (url != null) {
            contextRoot = url.toExternalForm();
        } else {
            // ���ȡ����webroot��Ŀ¼��������ȡ��web.xml��URL���Դ�Ϊ��׼�����������webroot��URL��
            url = servletContext.getResource("/WEB-INF/web.xml");

            if (url != null) {
                String urlstr = url.toExternalForm();

                if (urlstr.endsWith("/WEB-INF/web.xml")) {
                    contextRoot = urlstr.substring(0, urlstr.length() - "WEB-INF/web.xml".length());
                }
            }
        }

        if (contextRoot == null) {
            throw new IllegalArgumentException("Could not find WEBROOT.  Are you sure you are in webapp?");
        }

        if (!contextRoot.endsWith("/")) {
            contextRoot += "/";
        }

        if (getLogger().isDebugEnabled()) {
            MapBuilder mb = new MapBuilder();

            mb.append("path", path);
            mb.append("contextRoot", contextRoot);

            getLogger().debug(new ToStringBuilder().append("Initialized JSP Template Engine").append(mb).toString());
        }
    }

    /**
     * ȡ��Ĭ�ϵ�ģ������׺�б�
     * <p>
     * ��<code>TemplateService</code>û��ָ������ǰengine��mappingʱ����ȡ�ñ����������صĺ�׺���б�
     * </p>
     */
    public String[] getDefaultExtensions() {
        return new String[] { "jsp" };
    }

    /**
     * �ж�ģ���Ƿ���ڡ�
     */
    public boolean exists(String templateName) {
        return getPathWithinServletContextInternal(templateName) != null;
    }

    /**
     * ��Ⱦģ�壬�����ַ�������ʽȡ����Ⱦ�Ľ����
     * 
     * @param template ģ����
     * @param context template context
     * @return ģ����Ȼ�Ľ���ַ���
     * @throws TemplateException ��Ⱦʧ��
     */
    public String getText(String template, TemplateContext context) throws TemplateException, IOException {
        // ȡ��JSP�����webapp��·����
        String relativeTemplateName = getPathWithinServletContext(template);

        // ȡ��JSP��RequestDispatcher��
        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(relativeTemplateName);

        if (dispatcher == null) {
            throw new TemplateNotFoundException("Could not dispatch to JSP template " + template);
        }

        try {
            // ��template context���䵽request
            HttpServletRequest requestWrapper = new TemplateContextAdapter(request, context);

            // ������jsp���޸�content type��locale��charset����Ӧ����ģ���ⲿ������
            HttpServletResponse responseWrapper = new JspResponse(response);

            dispatcher.include(requestWrapper, responseWrapper);
        } catch (ServletException e) {
            throw new TemplateException(e);
        }

        return "";
    }

    /**
     * ��Ⱦģ�壬������Ⱦ�Ľ���͵��ֽ�������С�
     */
    public void writeTo(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException,
            IOException {
        getText(templateName, context);
    }

    /**
     * ��Ⱦģ�壬������Ⱦ�Ľ���͵��ַ�������С�
     */
    public void writeTo(String templateName, TemplateContext context, Writer writer) throws TemplateException,
            IOException {
        getText(templateName, context);
    }

    /**
     * ȡ�������servletContext��ģ��·�������·���ɱ�
     * <code>javax.servlet.RequestDispatcher</code> ʹ�ã��Ա��ҵ�jsp��ʵ����
     */
    public String getPathWithinServletContext(String templateName) throws TemplateNotFoundException {
        String path = getPathWithinServletContextInternal(templateName);

        if (path == null) {
            throw new TemplateNotFoundException("Template " + templateName + " not found");
        }

        return path;
    }

    private String getPathWithinServletContextInternal(String templateName) {
        assertInitialized();

        String resourceName = path + (templateName.startsWith("/") ? templateName.substring(1) : templateName);
        Resource resource = resourceLoader.getResource(resourceName);
        String path = null;

        if (resource != null && resource.exists()) {
            try {
                String url = resource.getURL().toExternalForm();

                if (url.startsWith(contextRoot)) {
                    path = url.substring(contextRoot.length() - 1); // ����slash:/
                }
            } catch (IOException e) {
                // ignore
            }
        }

        return path;
    }

    @Override
    public String toString() {
        return "JspEngine[" + path + "]";
    }
}
