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
package com.alibaba.citrus.service.resource.impl;

import java.util.Set;

import org.slf4j.Logger;

import com.alibaba.citrus.service.resource.ResourceListerContext;
import com.alibaba.citrus.service.resource.ResourceLoadingOption;
import com.alibaba.citrus.service.resource.ResourceLoadingService;
import com.alibaba.citrus.service.resource.ResourceNotFoundException;

/**
 * ����һ��resource�б�Ĳ��ҵĹ��̡�
 * 
 * @author Michael Zhou
 */
public class ResourceListerContextImpl extends AbstractResourceLoadingContext<String[]> implements
        ResourceListerContext {
    /**
     * ����һ��context��
     */
    public ResourceListerContextImpl(String resourceName, Set<ResourceLoadingOption> options,
                                     ResourceMapping[] mappings, ResourceLoadingService parent, Logger log) {
        super(resourceName, options, mappings, parent, log);
    }

    /**
     * ��ʼһ�����ҹ��̡�
     */
    public String[] list() throws ResourceNotFoundException {
        return doLoad(resourceName, options);
    }

    /**
     * �ص�����������ĳ��mapping��
     */
    @Override
    protected void visitMapping(ResourceMapping mapping) {
    }

    /**
     * ����parent resource loading serviceȡ����Դ��
     */
    @Override
    protected String[] loadParentResource(String resourceName, Set<ResourceLoadingOption> options)
            throws ResourceNotFoundException {
        if (parent != null) {
            return parent.list(resourceName, options);
        }

        return null;
    }

    /**
     * ����mappingȡ����Դ��
     */
    @Override
    protected String[] loadMappedResource(ResourceLoaderMapping mapping, Set<ResourceLoadingOption> options) {
        return mapping.list(this, options);
    }

    /**
     * ʵ��<code>ResourceListerContext.list()</code>��
     */
    public String[] list(String newResourceName, Set<ResourceLoadingOption> newOptions) {
        return loadContextResource(newResourceName, newOptions);
    }

    @Override
    public String toString() {
        return "ResourceListerContext[" + resourceName + "]";
    }
}
