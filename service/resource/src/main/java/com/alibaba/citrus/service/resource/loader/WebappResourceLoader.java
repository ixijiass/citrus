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
package com.alibaba.citrus.service.resource.loader;

import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import com.alibaba.citrus.service.resource.Resource;
import com.alibaba.citrus.service.resource.ResourceLister;
import com.alibaba.citrus.service.resource.ResourceListerContext;
import com.alibaba.citrus.service.resource.ResourceLoaderContext;
import com.alibaba.citrus.service.resource.ResourceLoadingOption;
import com.alibaba.citrus.service.resource.ResourceLoadingService;
import com.alibaba.citrus.service.resource.ResourceMatchResult;
import com.alibaba.citrus.service.resource.support.URLResource;

/**
 * ����װ��webapp�µ���Դ��
 * <p>
 * �ڷ�web�����£���loaderҲ�ɴ��ڣ�ֻ��������<code>servletContext==null</code>
 * ������Ҳ�����Դ��������ƣ���Ϊ���÷�web������web�������Թ�����ͬ�������ļ���
 * </p>
 * 
 * @author Michael Zhou
 */
public class WebappResourceLoader implements ResourceLister, ServletContextAware {
    private ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * ��ʼ��loader�����趨loader���ڵ�<code>ResourceLoadingService</code>��ʵ����
     */
    public void init(ResourceLoadingService resourceLoadingService) {
    }

    public Resource getResource(ResourceLoaderContext context, Set<ResourceLoadingOption> options) {
        return getServletResource(getNewResourceName(context), context, options);
    }

    public String[] list(ResourceListerContext context, Set<ResourceLoadingOption> options) {
        String path = getNewResourceName(context);
        Resource resource = getServletResource(path, context, options);

        if (resource == null) {
            return null; // Ŀ¼�����ڣ��򷵻�null
        }

        boolean isDirectory = resource.getURL().getPath().endsWith("/");

        if (!path.endsWith("/")) {
            path += "/";
        }

        @SuppressWarnings("unchecked")
        Set<String> nameSet = servletContext.getResourcePaths(path);

        if (nameSet == null || nameSet.isEmpty()) {
            if (isDirectory) {
                return EMPTY_STRING_ARRAY; // Ŀ¼���ڣ��������ļ������ؿ�����
            } else {
                return null;
            }
        }

        List<String> names = createArrayList(nameSet.size());

        for (String name : nameSet) {
            int startIndex = path.length();
            int endIndex = name.length();

            if (endIndex > startIndex) {
                name = name.substring(startIndex, endIndex);
            }

            names.add(name);
        }

        return names.toArray(new String[names.size()]);
    }

    private String getNewResourceName(ResourceMatchResult context) {
        return context.substitute(EMPTY_STRING);
    }

    private Resource getServletResource(String resourceName, ResourceMatchResult context,
                                        Set<ResourceLoadingOption> options) {
        URL resourceURL = null;

        if (servletContext != null) {
            try {
                resourceURL = servletContext.getResource(resourceName);
            } catch (MalformedURLException e) {
                // ignore
            }
        }

        Resource resource = null;

        if (resourceURL != null) {
            resource = new URLResource(resourceURL);
        }

        // ����webapp��Դ��������ҵ������Կ϶���Դ���ڣ�����Ҫ�ټ��������ԡ�
        // Ȼ����httpunit�����£�����������ܲ�����ѭ��
        // Ϊ�˱��������ͬʱ���һ���ļ��Ĵ����ԡ�
        if (resource != null && resource.getFile() != null && !resource.getFile().exists()) {
            resource = null;
        }

        return resource;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + servletContext + "]";
    }
}
