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
package com.alibaba.citrus.service.resource.support;

import static com.alibaba.citrus.service.resource.ResourceLoadingService.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import com.alibaba.citrus.service.resource.ResourceLoadingService;
import com.alibaba.citrus.service.resource.ResourceNotFoundException;
import com.alibaba.citrus.springext.ResourceLoadingExtender;

/**
 * ��<code>ResourceLoadingService</code>���ϵ�Spring <code>ApplicationContext</code>
 * �С�
 * 
 * @author Michael Zhou
 */
public class ResourceLoadingSupport implements ResourceLoadingExtender, ApplicationListener {
    private final static Logger log = LoggerFactory.getLogger(ResourceLoadingSupport.class);
    private final ApplicationContext factory;
    private final String resourceLoadingServiceName;
    private final ResourcePatternResolver resolver;
    private ResourceLoadingService resourceLoadingService;
    private boolean contextRefreshed = false;
    private boolean complained = false;

    /**
     * ����<code>ResourceLoadingSupport</code>����ָ��
     * <code>ResourceLoadingService</code>���ڵ�bean factory��
     */
    public ResourceLoadingSupport(ApplicationContext factory) {
        this(factory, null);
    }

    /**
     * ����<code>ResourceLoadingSupport</code>����ָ��
     * <code>ResourceLoadingService</code>���ڵ�bean factory���Լ�
     * <code>ResourceLoadingService</code>�����ơ�
     */
    public ResourceLoadingSupport(ApplicationContext factory, String resourceLoadingServiceName) {
        this.factory = assertNotNull(factory, "beanFactory");
        this.resourceLoadingServiceName = defaultIfNull(trimToNull(resourceLoadingServiceName),
                "resourceLoadingService");
        this.resolver = new ResourceLoadingServicePatternResolver();
    }

    /**
     * ��applicatioon context��refresh�󣬵��ô˷�����ʼ����
     */
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            contextRefreshed = true;
            resourceLoadingService = getResourceLoadingServiceFromContext();
        }
    }

    /**
     * ȡ��<code>ResourceLoadingService</code>�������δ��ʼ���򲻴��ڣ��򷵻�<code>null</code>��
     */
    public ResourceLoadingService getResourceLoadingService() {
        if (contextRefreshed) {
            return resourceLoadingService;
        }

        return getResourceLoadingServiceFromContext();
    }

    private ResourceLoadingService getResourceLoadingServiceFromContext() {
        try {
            return (ResourceLoadingService) factory.getBean(resourceLoadingServiceName);
        } catch (IllegalStateException e) {
            // beanFactoryδ׼���ã���һ��parent factory�������ȡ����ResourceLoadingService������null��������־
            ApplicationContext parent = factory.getParent();

            if (parent != null) {
                try {
                    return (ResourceLoadingService) parent.getBean(resourceLoadingServiceName);
                } catch (Exception ee) {
                }
            }
        } catch (NoSuchBeanDefinitionException e) {
            if (!complained) {
                complained = true;
                log.warn("ResourceLoadingService does not exists: beanName={}", resourceLoadingServiceName);
            }
        }

        return null;
    }

    /**
     * ȡ��ָ��·���������������Դ����
     * <p>
     * �������<code>null</code>��ʾʹ��ԭ����װ�ػ�����ȡ����Դ��
     * </p>
     */
    public Resource getResourceByPath(String path) {
        ResourceLoadingService resourceLoadingService = getResourceLoadingService();

        if (resourceLoadingService == null) {
            // ���resource loading service�����ڣ��򷵻�null������ԭ����װ�ػ�����ȡ����Դ��
            return null;
        }

        com.alibaba.citrus.service.resource.Resource resource;

        try {
            resource = resourceLoadingService.getResource(path, FOR_CREATE);
        } catch (IllegalStateException e) {
            // resourceLoadingServiceδ׼���ã��п������ڳ�ʼ��resource loading service�Ĺ����У�
            // ĳ��loader��filterͨ��spring resource loaderע��resource���Ӷ������ݹ���á�
            // ��ʱ����null������ԭ����װ�ػ�����ȡ����Դ��
            return null;
        } catch (ResourceNotFoundException e) {
            return new NonExistResource(path, e);
        }

        return new ResourceAdapter(path, resource, this);
    }

    /**
     * ȡ����������resource pattern�Ľ�������
     */
    public ResourcePatternResolver getResourcePatternResolver() {
        return resolver;
    }

    /**
     * ��<code>ResourceLoadingService</code>������resource pattern��
     */
    private class ResourceLoadingServicePatternResolver extends PathMatchingResourcePatternResolver {
        public ResourceLoadingServicePatternResolver() {
            super(factory);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern)
                throws IOException {
            ResourceLoadingService resourceLoadingService = getResourceLoadingService();

            // ���resource loading service�����ڣ�����resource���Ǵ�resource loading serviceȡ�õģ�
            // �����ԭ����װ�ػ�����ȡ����Դ��
            if (resourceLoadingService == null || !(rootDirResource instanceof ResourceAdapter)) {
                return super.doFindPathMatchingFileResources(rootDirResource, subPattern);
            }

            ResourceAdapter rootResource = (ResourceAdapter) rootDirResource;
            String path = rootResource.getPathWithinContext();

            if (!path.endsWith("/")) {
                path += "/";
            }

            String fullPattern = path + subPattern;
            Set<Resource> result = createLinkedHashSet();

            findMatchingResources(resourceLoadingService, fullPattern, path, result);

            return result;
        }

        private void findMatchingResources(ResourceLoadingService resourceLoadingService, String fullPattern,
                                           String dir, Set<Resource> result) throws IOException {
            String[] candidates;

            try {
                candidates = resourceLoadingService.list(dir);
            } catch (ResourceNotFoundException e) {
                return;
            }

            boolean dirDepthNotFixed = fullPattern.indexOf("**") != -1;

            for (String name : candidates) {
                String currPath = dir + name;

                if (currPath.endsWith("/")
                        && (dirDepthNotFixed || StringUtils.countOccurrencesOf(currPath, "/") <= StringUtils
                                .countOccurrencesOf(fullPattern, "/"))) {
                    findMatchingResources(resourceLoadingService, fullPattern, currPath, result);
                }

                if (getPathMatcher().match(fullPattern, currPath)) {
                    try {
                        result.add(new ResourceAdapter(currPath, resourceLoadingService.getResource(currPath)));
                    } catch (ResourceNotFoundException e) {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * һ�������resource��������Դδ�ҵ���
     */
    private static class NonExistResource extends AbstractResource implements ContextResource {
        private final String location;
        private final IOException ioe;
        private final String description;

        public NonExistResource(String location, ResourceNotFoundException e) {
            this.location = location;
            this.ioe = new IOException("Resource Not Found [" + location + "]");
            this.ioe.initCause(e);
            this.description = "NonExistResource[" + location + "]";
        }

        public String getPathWithinContext() {
            return location;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public URL getURL() throws IOException {
            throw (IOException) ioe.fillInStackTrace();
        }

        @Override
        public File getFile() throws IOException {
            throw (IOException) ioe.fillInStackTrace();
        }

        public InputStream getInputStream() throws IOException {
            throw (IOException) ioe.fillInStackTrace();
        }

        @Override
        public Resource createRelative(String relativePath) throws IOException {
            throw (IOException) ioe.fillInStackTrace();
        }
    }
}
