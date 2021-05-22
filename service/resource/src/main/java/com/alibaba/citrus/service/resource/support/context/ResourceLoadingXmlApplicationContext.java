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
package com.alibaba.citrus.service.resource.support.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.alibaba.citrus.service.resource.support.ResourceLoadingSupport;
import com.alibaba.citrus.springext.support.context.AbstractXmlApplicationContext;

/**
 * ��resource loading service��װ�������ļ�<code>ApplicationContext</code>ʵ�֡�
 * 
 * @author Michael Zhou
 * @see AbstractXmlApplicationContext
 */
public class ResourceLoadingXmlApplicationContext extends AbstractXmlApplicationContext {
    private Resource configResource;

    /**
     * ��һ���ֳɵ�<code>Resource</code>�д���spring����������ʼ����
     */
    public ResourceLoadingXmlApplicationContext(Resource resource) throws BeansException {
        this(resource, null);
    }

    /**
     * ��һ���ֳɵ�<code>Resource</code>�д���spring����������ʼ����
     */
    public ResourceLoadingXmlApplicationContext(Resource resource, ApplicationContext parentContext)
            throws BeansException {
        super(parentContext);
        this.configResource = resource;
        setResourceLoadingExtender(new ResourceLoadingSupport(this));
        refresh();
    }

    /**
     * ��һ�������ļ����У�����spring����������ʼ����
     * <p>
     * ����<code>parentContext</code>�ж�����<code>ResourceLoadingService</code>����ô
     * <code>configLocations</code>�Լ����е�imports������װ�ء�
     * </p>
     */
    public ResourceLoadingXmlApplicationContext(String[] configLocations, ApplicationContext parentContext) {
        super(parentContext);
        setConfigLocations(configLocations);
        setResourceLoadingExtender(new ResourceLoadingSupport(this));
        refresh();
    }

    public void setConfigResource(Resource configResource) {
        this.configResource = configResource;
    }

    @Override
    protected Resource[] getConfigResources() {
        if (configResource == null) {
            return null;
        } else {
            return new Resource[] { configResource };
        }
    }
}
