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
package com.alibaba.citrus.turbine.pipeline.valve;

import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.turbine.util.TurbineUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Element;

import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.PipelineException;
import com.alibaba.citrus.service.pipeline.support.AbstractValve;
import com.alibaba.citrus.service.pipeline.support.AbstractValveDefinitionParser;
import com.alibaba.citrus.service.requestcontext.buffered.BufferedRequestContext;
import com.alibaba.citrus.turbine.TurbineRunData;
import com.alibaba.citrus.util.ServletUtil;
import com.alibaba.citrus.util.internal.regex.Substitution;
import com.alibaba.citrus.util.io.StreamUtil;

/**
 * ��resource loader��װ����Դ����ֱ����ʾ��valve��
 * 
 * @author Michael Zhou
 * @author dux.fangl
 */
public class GetResourceValve extends AbstractValve implements ResourceLoaderAware {
    private static final String DEFAULT_SUBSTITUTION_NAME = "subst";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private BufferedRequestContext bufferedRequestContext;

    private ResourceLoader loader;

    private String substName;
    private String resourceName;

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.loader = resourceLoader;
    }

    /**
     * ��<code>PipelineContext</code>�е�<code>Substitution</code>�����ƣ�Ĭ��Ϊ
     * <code>subst</code>��
     * <p>
     * ���substitution����ͨ������ĳ��conditionƥ���ŵ�context�еġ�
     * </p>
     */
    public void setSubst(String substName) {
        this.substName = trimToNull(substName);
    }

    /**
     * ����resource���ƣ����԰���<code>$1</code>��<code>$2</code>�������滻����
     */
    public void setName(String resourceName) {
        this.resourceName = trimToNull(resourceName);
    }

    public void invoke(PipelineContext pipelineContext) throws Exception {
        TurbineRunData rundata = getTurbineRunData(request);
        Substitution subst = getSubstitution(pipelineContext, DEFAULT_SUBSTITUTION_NAME);

        String resourceName;

        if (subst != null && this.resourceName != null) {
            resourceName = trimToNull(subst.substitute(this.resourceName));
        } else {
            resourceName = ServletUtil.getResourcePath(rundata.getRequest());
        }

        try {
            resourceName = URI.create(resourceName).normalize().toString();

            if (resourceName.contains("../")) {
                resourceName = null;
            }
        } catch (IllegalArgumentException e) {
            resourceName = null;
        }

        Resource resource = null;

        if (resourceName != null) {
            resource = loader.getResource(resourceName);
        }

        if (resource == null || !resource.exists()) {
            throw new com.alibaba.citrus.webx.ResourceNotFoundException("Could not find resource: " + resourceName);
        }

        InputStream istream = null;
        OutputStream ostream = null;

        try {
            URLConnection connection = resource.getURL().openConnection();
            String contentType = connection.getContentType();

            if (contentType != null) {
                rundata.getResponse().setContentType(contentType);
            }

            istream = connection.getInputStream();

            // �����Ѿ�ȡ������������ʼ�����
            bufferedRequestContext.setBuffering(false);

            ostream = rundata.getResponse().getOutputStream();

            StreamUtil.io(istream, ostream, true, false);
        } catch (IOException e) {
            throw new PipelineException("Failed reading resource: " + resource);
        } finally {
            if (ostream != null) {
                try {
                    ostream.flush();
                } catch (IOException e) {
                }
            }
        }

        pipelineContext.invokeNext();
    }

    private Substitution getSubstitution(PipelineContext pipelineContext, String defaultName) {
        String substName = defaultIfNull(this.substName, defaultName);
        return (Substitution) pipelineContext.getAttribute(substName);
    }

    public static class DefinitionParser extends AbstractValveDefinitionParser<GetResourceValve> {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            attributesToProperties(element, builder, "subst", "name");
        }
    }
}
