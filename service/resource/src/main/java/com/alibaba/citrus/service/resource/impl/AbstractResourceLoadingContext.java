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
import static com.alibaba.citrus.util.FileUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static java.util.Collections.*;

import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import org.slf4j.Logger;

import com.alibaba.citrus.service.resource.ResourceLoadingOption;
import com.alibaba.citrus.service.resource.ResourceLoadingService;
import com.alibaba.citrus.service.resource.ResourceMatchResult;
import com.alibaba.citrus.service.resource.ResourceNotFoundException;
import com.alibaba.citrus.util.Assert;
import com.alibaba.citrus.util.internal.regex.MatchResultSubstitution;

/**
 * ���Һ�װ��resource���߼���
 * 
 * @author Michael Zhou
 */
abstract class AbstractResourceLoadingContext<R> implements ResourceMatchResult {
    private final static Set<ResourceLoadingOption> EMPTY_OPTIONS = emptySet();

    // ������
    protected final Logger log;
    protected final ResourceLoadingService parent;
    private final String originalResourceName;
    private final Set<ResourceLoadingOption> originalOptions;
    private final ResourceMapping[] mappings;
    private final BestResourcesMatcher resourcesMatcher;

    // ����
    private List<ResourceMapping> visitedMappings;
    protected String resourceName; // ��ǰ����ƥ���resourceName
    protected Set<ResourceLoadingOption> options; // ��ǰ����ʹ�õ�options
    protected ResourcePattern lastMatchedPattern; // �����ƥ��
    protected MatchResultSubstitution lastSubstitution; // �������ƥ���Ӧ���滻����

    /**
     * ����һ��context��
     */
    public AbstractResourceLoadingContext(String resourceName, Set<ResourceLoadingOption> options,
                                          ResourceMapping[] mappings, ResourceLoadingService parent, Logger log) {
        // ������
        this.log = assertNotNull(log, "logger");
        this.parent = parent;
        this.originalResourceName = normalizeAbsolutePath(assertNotNull(trimToNull(resourceName), "resourceName"));
        this.originalOptions = defaultIfNull(options, EMPTY_OPTIONS);
        this.mappings = assertNotNull(mappings, "mappings");
        this.resourcesMatcher = new BestResourcesMatcher();

        // ����
        this.resourceName = originalResourceName;
        this.options = originalOptions;
    }

    /**
     * ʵ��<code>ResourceMatchResult.getResourceName()</code>��
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * ʵ��<code>ResourceMatchResult.substitute()</code>��
     */
    public String substitute(String substitution) {
        return resourceName.substring(0, lastSubstitution.getMatch().start())
                + lastSubstitution.substitute(substitution) + resourceName.substring(lastSubstitution.getMatch().end());
    }

    /**
     * Ѱ����Դ����ʵ�߼�����filter chain���ã���getResourceֱ�ӵ��ã�����û��filter������list���á�
     */
    protected R doLoad(String newResourceName, Set<ResourceLoadingOption> newOptions) throws ResourceNotFoundException {
        resourceName = newResourceName;
        options = newOptions;

        log.trace("Looking for resource: name={}", resourceName);

        R resource = null;
        ResourceNotFoundException chainingException = null;

        if (visitedMappings == null) {
            visitedMappings = createLinkedList();
        }

        if (findBestMatch()) {
            // findBestMatch() ���1. �ҵ�alias����û���ҵ����յ�resource mapping
            if (lastMatchedPattern instanceof ResourceAlias) {
                if (parent != null) {
                    log.trace("Resource \"{}\" not found.  Trying to find it in super ResourceLoadingService",
                            resourceName);

                    try {
                        resource = loadParentResource(resourceName, options);
                    } catch (ResourceNotFoundException e) {
                        // alias���ı�resourceName���ʱ����쳣��Ϊcaused by�쳣
                        chainingException = e;
                    }
                }
            } else {
                // findBestMatch() ���2, 3. �ҵ�resource mapping
                ResourceLoaderMapping mapping = (ResourceLoaderMapping) lastMatchedPattern;

                resource = loadMappedResource(mapping, options);

                if (resource == null) {
                    // ����resourceName���ı䣬�򱣴��쳣��Ϊcaused by�쳣
                    if (!isEquals(resourceName, originalResourceName)) {
                        logResourceNotFound(resourceName);
                        chainingException = new ResourceNotFoundException(String.format(
                                "Could not find resource \"%s\"", resourceName));
                    }
                }
            }
        }

        // findBestMatch() ���4. ʲôҲû�ҵ�
        else {
            if (parent != null) {
                log.trace("Resource \"{}\" not found.  " + "Trying to find it in super ResourceLoadingService",
                        resourceName);

                // ֱ���׳��쳣����Ϊ�͸�parent��resourceName��δ�ı䣬û��Ҫ�����ظ����쳣��Ϣ
                resource = loadParentResource(resourceName, options);
            }
        }

        if (resource == null) {
            logResourceNotFound(originalResourceName);
            throw new ResourceNotFoundException(String.format("Could not find resource \"%s\"", originalResourceName),
                    chainingException);
        }

        log.debug("Found resource \"{}\": {}", originalResourceName, resource);

        return resource;
    }

    /**
     * �������ƥ���&lt;resource&gt;��&lt;resource-alias&gt;��
     */
    private boolean findBestMatch() throws ResourceNotFoundException {
        if (resourcesMatcher.matches(resourceName)) {
            ResourceMapping resourceMapping = resourcesMatcher.bestMatchPettern;

            lastMatchedPattern = resourceMapping;
            lastSubstitution = new MatchResultSubstitution(resourcesMatcher.bestMatchResult);

            // �ݹ����resource alias��ֱ��һ��resource mapping��ƥ��Ϊֹ
            if (resourceMapping instanceof ResourceAlias) {
                ResourceAlias alias = (ResourceAlias) resourceMapping;

                // ����alias�滻������µ�resourceName
                String newResourceName = substitute(alias.getName());

                if (log.isDebugEnabled()) {
                    log.debug("Resource \"{}\" matched resource-alias pattern: \"{}\".  "
                            + "Use a new resourceName: \"{}\"", new Object[] { resourceName, alias.getPatternName(),
                            newResourceName });
                }

                visitMapping(alias);
                visitedMappings.add(alias);

                resourceName = newResourceName;

                // �ݹ�ƥ��alias
                findBestMatch();

                // ����1. findBestMatch()==false, ƥ����һ��alias����û�ҵ��ɼ���ƥ����򷵻����ƥ���alias��
                // ����2. findBestMatch()==true, ƥ����һ��alias�����ݹ��ҵ��˾����滻�����ƥ����򷵻����յ�ƥ�䡣
                // �����������ͣ�������true
            }

            // �����ƥ�����resource loader mapping���򷵻�֮��
            else if (resourceMapping instanceof ResourceLoaderMapping) {
                ResourceLoaderMapping mapping = (ResourceLoaderMapping) resourceMapping;

                log.debug("Resource \"{}\" matched pattern: \"{}\"", resourceName, mapping.getPatternName());

                visitMapping(mapping);
                visitedMappings.add(mapping);

                // ����3. ƥ����һ��resource������true
            }

            return true;
        }

        // ����4. ʲôҲû��ƥ�䡣
        return false;
    }

    /**
     * �ص�����������ĳ��mapping��
     */
    protected abstract void visitMapping(ResourceMapping mapping);

    /**
     * ����parent resource loading serviceȡ����Դ��
     */
    protected abstract R loadParentResource(String resourceName, Set<ResourceLoadingOption> options)
            throws ResourceNotFoundException;

    /**
     * ����mappingȡ����Դ��
     */
    protected abstract R loadMappedResource(ResourceLoaderMapping mapping, Set<ResourceLoadingOption> options);

    /**
     * ��<code>ResourceLoaderContext.getResource()</code>��
     * <code>ResourceListerContext.list()</code>���õ�ͨ�÷�����
     */
    protected final R loadContextResource(String newResourceName, Set<ResourceLoadingOption> newOptions) {
        assertTrue(!visitedMappings.isEmpty(), Assert.ExceptionType.ILLEGAL_STATE,
                "getResource() can only be called within a ResourceLoader");

        try {
            // �����ǰresourceName���µ�resourceName��ͬ����ֱ�ӵ���parent service
            if (resourceName.equals(newResourceName)) {
                if (parent == null) {
                    log.debug("No parent ResourceLoadingService exists for loading resource \"{}\"", newResourceName);
                    return null;
                } else {
                    return loadParentResource(newResourceName, newOptions);
                }
            } else {
                // ���µ�����װ����Դ
                log.trace("Trying to find resource \"{}\" using a new resourceName: \"{}\"", resourceName,
                        newResourceName);

                return doLoad(newResourceName, newOptions);
            }
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private void logResourceNotFound(String resourceName) {
        log.trace("Resource \"{}\" not found", resourceName);
    }

    /**
     * �ҳ�����ص�ƥ�䡣
     * <p>
     * �㷨���Ȱ�pattern��ض������ٰ�ƥ�䳤������
     * </p>
     */
    protected static abstract class BestMatcher<P extends ResourcePattern> {
        protected String resourceName;
        protected P bestMatchPettern;
        protected MatchResult bestMatchResult;
        private int bestMatchRelevancy;
        private int bestMatchLength;

        protected abstract void init();

        protected abstract boolean accept(P pattern);

        protected abstract P nextPattern();

        public final boolean matches(String resourceName) {
            // ��Ϊ�˶���ᱻ��θ��ã�����ʹ��ǰ�����ʼ������
            this.resourceName = assertNotNull(resourceName, "resourceName");
            this.bestMatchPettern = null;
            this.bestMatchResult = null;
            this.bestMatchRelevancy = -1;
            this.bestMatchLength = -1;

            init();

            for (P pattern = nextPattern(); pattern != null; pattern = nextPattern()) {
                Matcher matcher = pattern.getPattern().matcher(resourceName);

                if (matcher.find() && accept(pattern)) {
                    int relevancy = pattern.getRelevancy();
                    int length = matcher.group().length();

                    if (relevancy > bestMatchRelevancy || relevancy == bestMatchRelevancy && length > bestMatchLength) {
                        bestMatchPettern = pattern;
                        bestMatchResult = matcher;
                        bestMatchRelevancy = relevancy;
                        bestMatchLength = length;
                    }
                }
            }

            return bestMatchLength >= 0;
        }
    }

    /**
     * �ҳ���ƥ���&lt;resource&gt;��&lt;resource-alias&gt;��
     */
    private class BestResourcesMatcher extends BestMatcher<ResourceMapping> {
        private int i;

        @Override
        protected void init() {
            this.i = 0;
            assertNotNull(visitedMappings, "visitedMappings");
        }

        @Override
        protected ResourceMapping nextPattern() {
            if (i < mappings.length) {
                return mappings[i++];
            } else {
                return null;
            }
        }

        @Override
        protected boolean accept(ResourceMapping pattern) {
            // visitedMappingsΪ�գ���ʾ�ǵ�һ��ƥ�䣬��ʱ������internal mappings
            if (visitedMappings.isEmpty() && pattern.isInternal()) {
                return false;
            }

            // ��ȥ�Ѿ�ƥ�����match����ֹ��ѭ��
            if (visitedMappings.contains(pattern)) {
                return false;
            }

            return true;
        }
    }
}
