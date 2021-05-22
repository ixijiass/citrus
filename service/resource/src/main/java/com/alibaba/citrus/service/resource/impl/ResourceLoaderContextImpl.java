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

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.alibaba.citrus.service.resource.Resource;
import com.alibaba.citrus.service.resource.ResourceFilterChain;
import com.alibaba.citrus.service.resource.ResourceLoaderContext;
import com.alibaba.citrus.service.resource.ResourceLoadingOption;
import com.alibaba.citrus.service.resource.ResourceLoadingService;
import com.alibaba.citrus.service.resource.ResourceMatchResult;
import com.alibaba.citrus.service.resource.ResourceNotFoundException;
import com.alibaba.citrus.service.resource.ResourceTrace;
import com.alibaba.citrus.service.resource.ResourceTraceElement;
import com.alibaba.citrus.util.internal.regex.MatchResultSubstitution;

/**
 * ����һ��resource���ҵĹ��̡�
 * 
 * @author Michael Zhou
 */
class ResourceLoaderContextImpl extends AbstractResourceLoadingContext<Resource> implements ResourceLoaderContext,
        ResourceFilterChain {
    // ������
    private final String configLocation;
    private final String beanName;
    private final ResourceFilterMapping[] filterMappings;
    private final BestFiltersMatcher filtersMatcher;

    // ����
    private List<ResourceTraceElement> trace;

    /**
     * ����һ��context��
     */
    public ResourceLoaderContextImpl(String resourceName, Set<ResourceLoadingOption> options,
                                     ResourceFilterMapping[] filterMappings, ResourceMapping[] mappings,
                                     ResourceLoadingService parent, Logger log, String configLocation, String beanName) {
        super(resourceName, options, mappings, parent, log);

        this.configLocation = configLocation;
        this.beanName = beanName;
        this.filterMappings = assertNotNull(filterMappings, "filterMappings");
        this.filtersMatcher = new BestFiltersMatcher();
    }

    /**
     * ��ʼһ�����ҹ��̡�
     */
    public Resource getResource() throws ResourceNotFoundException {
        if (filtersMatcher.matches(resourceName)) {
            ResourceFilterMapping filterMapping = filtersMatcher.bestMatchPettern;

            lastMatchedPattern = filterMapping;
            lastSubstitution = new MatchResultSubstitution(filtersMatcher.bestMatchResult);

            log.debug("Resource \"{}\" matched resource-filters pattern: \"{}\"", resourceName,
                    filterMapping.getPatternName());

            ResourceFilterChain root = this; // as ResourceFilterChain
            ResourceMatchResult matchResult = this; // as ResourceMatchResult
            ResourceFilterChain chain = filterMapping.getResourceFilterChain(root);

            return chain.doFilter(matchResult, options); // with filters
        } else {
            return doLoad(resourceName, options); // no filters
        }
    }

    /**
     * ����һ�����ҹ����̡�
     */
    public ResourceTrace getTrace() throws ResourceNotFoundException {
        trace = createLinkedList();

        try {
            doLoad(resourceName, options); // Խ��filters
        } catch (ResourceNotFoundException e) {
            // ignore
        }

        return new ResourceTrace(trace);
    }

    /**
     * ʵ��<code>ResourceFilterChain.doFilter()</code>������filter�����ս�㡣
     */
    public Resource doFilter(ResourceMatchResult filterMatchResult, Set<ResourceLoadingOption> options)
            throws ResourceNotFoundException {
        return doLoad(filterMatchResult.getResourceName(), options);
    }

    /**
     * Trace mode����¼resource name�ı�Ǩ��
     */
    @Override
    protected void visitMapping(ResourceMapping mapping) {
        if (trace != null) {
            trace.add(new ResourceTraceElement(configLocation, beanName, lastMatchedPattern.getPatternType(),
                    lastMatchedPattern.getPatternName(), resourceName));
        }
    }

    /**
     * ȡ��parent��resource��������trace mode�У���ͬʱ���parent��trace��Ϣ��
     */
    @Override
    protected Resource loadParentResource(String resourceName, Set<ResourceLoadingOption> options)
            throws ResourceNotFoundException {
        if (parent != null) {
            try {
                return parent.getResource(resourceName, options);
            } finally {
                if (trace != null) {
                    ResourceTrace parentTrace = parent.trace(resourceName, options);

                    if (parentTrace != null) {
                        for (ResourceTraceElement element : parentTrace) {
                            trace.add(element);
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * ����mappingȡ����Դ��
     */
    @Override
    protected Resource loadMappedResource(ResourceLoaderMapping mapping, Set<ResourceLoadingOption> options) {
        return mapping.getResource(this, options);
    }

    /**
     * ʵ��<code>ResourceLoaderContext.getResource()</code>��
     */
    public Resource getResource(String newResourceName, Set<ResourceLoadingOption> newOptions) {
        return loadContextResource(newResourceName, newOptions);
    }

    @Override
    public String toString() {
        return "ResourceLoaderContext[" + resourceName + "]";
    }

    /**
     * �ҳ���ƥ���&lt;resource-filters&gt;��
     */
    private class BestFiltersMatcher extends BestMatcher<ResourceFilterMapping> {
        private int i;

        @Override
        protected void init() {
            this.i = 0;
        }

        @Override
        protected ResourceFilterMapping nextPattern() {
            if (i < filterMappings.length) {
                return filterMappings[i++];
            } else {
                return null;
            }
        }

        @Override
        protected boolean accept(ResourceFilterMapping pattern) {
            return true;
        }
    }
}
