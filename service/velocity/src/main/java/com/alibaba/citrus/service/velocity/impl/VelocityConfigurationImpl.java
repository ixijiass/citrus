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
package com.alibaba.citrus.service.velocity.impl;

import static com.alibaba.citrus.service.velocity.impl.PreloadedResourceLoader.*;
import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.FileUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static org.apache.velocity.runtime.RuntimeConstants.*;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.event.EventHandler;
import org.slf4j.Logger;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import com.alibaba.citrus.service.velocity.VelocityConfiguration;
import com.alibaba.citrus.service.velocity.VelocityPlugin;
import com.alibaba.citrus.service.velocity.support.RenderableHandler;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ����һ��velocity engine�����á�
 * 
 * @author Michael Zhou
 */
public class VelocityConfigurationImpl implements VelocityConfiguration {
    private final Logger log;
    private final ExtendedProperties properties = new ExtendedProperties();
    private final Map<String, Resource> preloadedResources = createHashMap();
    private final CloneableEventCartridge eventCartridge = new CloneableEventCartridge();
    private Object[] plugins;
    private ResourceLoader loader;
    private boolean productionMode = true;

    // resource loader
    private String path;
    private boolean cacheEnabled = true;
    private int modificationCheckInterval = 2;

    // strict ref
    private boolean strictReference = true;

    // template charset encoding
    private String charset;

    // global macros
    private String[] macros;

    /**
     * ����һ��velocity���á�
     */
    public VelocityConfigurationImpl(Logger log) {
        this.log = assertNotNull(log, "log");
    }

    public ExtendedProperties getProperties() {
        return properties;
    }

    public CloneableEventCartridge getEventCartridge() {
        return eventCartridge;
    }

    public ResourceLoader getResourceLoader() {
        return loader;
    }

    /**
     * ����resource loader��
     */
    public void setResourceLoader(ResourceLoader loader) {
        this.loader = loader;
    }

    public boolean isProductionMode() {
        return productionMode;
    }

    /**
     * ��������ģʽ��Ĭ��Ϊ<code>true</code>��
     */
    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }

    /**
     * ��������ģ��ĸ�Ŀ¼��Ĭ��Ϊ<code>/templates</code>��
     */
    public void setPath(String path) {
        this.path = trimToNull(path);
    }

    /**
     * �Ƿ���ģ�建�档������ģʽ�£���ģʽ����ǿ�п�����
     */
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * ���ü��ģ�屻�޸ĵļ�����룩��Ĭ��Ϊ2�롣
     */
    public void setModificationCheckInterval(int modificationCheckInterval) {
        this.modificationCheckInterval = modificationCheckInterval;
    }

    /**
     * ����strict referenceģʽ��Ĭ��Ϊ<code>true</code>��
     */
    public void setStrictReference(boolean strictReference) {
        this.strictReference = strictReference;
    }

    /**
     * ����ģ����ַ������롣
     */
    public void setTemplateEncoding(String charset) {
        this.charset = trimToNull(charset);
    }

    /**
     * ����ȫ�ֺ�����ƣ��ɰ���ͨ�����
     */
    public void setGlobalMacros(String[] macros) {
        this.macros = macros;
    }

    /**
     * ����plugins��
     */
    public void setPlugins(Object[] plugins) {
        this.plugins = plugins;
    }

    /**
     * ���ø߼����á�
     */
    public void setAdvancedProperties(Map<String, Object> configuration) {
        this.properties.clear();

        for (Map.Entry<String, Object> entry : configuration.entrySet()) {
            this.properties.setProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * ��ʼ��configuration��
     */
    public void init() throws Exception {
        assertNotNull(loader, "resourceLoader");

        removeReservedProperties();

        initPlugins();
        initLogger();
        initMacros();
        initResourceLoader(); // ������initMacros�Ľ��
        initEventHandlers();
        initMiscs();
    }

    private void addHandler(EventHandler handler) {
        assertTrue(eventCartridge.addEventHandler(handler), "Unknown event handler type: %s", handler.getClass());
    }

    /**
     * ɾ��������properties����Щproperties�û������޸ġ�
     */
    private void removeReservedProperties() {
        Set<String> keysToRemove = createHashSet();

        // Remove resource loader settings
        keysToRemove.add(RESOURCE_LOADER);

        for (Iterator<?> i = properties.getKeys(); i.hasNext();) {
            Object key = i.next();

            if (key instanceof String && ((String) key).contains(RESOURCE_LOADER)) {
                keysToRemove.add((String) key);
            }
        }

        // Remove log settings
        keysToRemove.add(RUNTIME_LOG);
        keysToRemove.add(RUNTIME_LOG_LOGSYSTEM);
        keysToRemove.add(RUNTIME_LOG_LOGSYSTEM_CLASS);

        // Remove macros
        keysToRemove.add(VM_LIBRARY);

        // Remove event handlers: ���Ƴ�eventhandler.xxx.class��������������
        for (Iterator<?> i = properties.getKeys(); i.hasNext();) {
            Object key = i.next();

            if (key instanceof String && ((String) key).startsWith("eventhandler.")
                    && ((String) key).endsWith(".class")) {
                keysToRemove.add((String) key);
            }
        }

        // remove others
        keysToRemove.add(INPUT_ENCODING);
        keysToRemove.add(VM_LIBRARY_AUTORELOAD);
        keysToRemove.add(RUNTIME_REFERENCES_STRICT);

        // do removing
        for (String key : keysToRemove) {
            if (properties.containsKey(key)) {
                log.warn("Removed reserved property: {} = {}", key, properties.get(key));
                properties.clearProperty(key);
            }
        }
    }

    /**
     * ��ʼ��plugins��
     */
    private void initPlugins() throws Exception {
        if (plugins != null) {
            for (Object plugin : plugins) {
                if (plugin instanceof VelocityPlugin) {
                    ((VelocityPlugin) plugin).init(this);
                }
            }
        }
    }

    /**
     * ��ʼ��resource loader��
     * <p>
     * �̶�ʹ��ResourceLoadingService/Spring
     * ResourceLoader��װ����Դ���������ϻ����Ѿ������㹻������ԣ����Բ��������û���velocity��������resource
     * loader��
     * </p>
     */
    private void initResourceLoader() {
        path = defaultIfNull(path, "/templates");

        if (productionMode) {
            cacheEnabled = true;
        }

        properties.setProperty(RESOURCE_LOADER, "spring");

        // Spring resource loader
        String prefix = "spring." + RESOURCE_LOADER + ".";

        properties.setProperty(prefix + "description", "Spring Resource Loader Adapter");
        properties.setProperty(prefix + "class", SpringResourceLoaderAdapter.class.getName());
        properties.setProperty(prefix + "path", path);
        properties.setProperty(prefix + "cache", String.valueOf(cacheEnabled));
        properties.setProperty(prefix + "modificationCheckInterval", String.valueOf(modificationCheckInterval));

        // Preloaded resource loader
        prefix = "preloaded." + RESOURCE_LOADER + ".";

        properties.setProperty(prefix + "description", "Preloaded Resource Loader");
        properties.setProperty(prefix + "class", PreloadedResourceLoader.class.getName());
        properties.setProperty(prefix + "cache", String.valueOf(cacheEnabled));
        properties.setProperty(prefix + "modificationCheckInterval", String.valueOf(modificationCheckInterval));
        properties.setProperty(prefix + PRELOADED_RESOURCES_KEY, preloadedResources);

        if (!preloadedResources.isEmpty()) {
            properties.addProperty(RESOURCE_LOADER, "preloaded");
        }
    }

    /**
     * ��ʼ����־ϵͳ��
     */
    private void initLogger() {
        properties.setProperty(RUNTIME_LOG_LOGSYSTEM, new Slf4jLogChute(log));
    }

    /**
     * ��������ȫ��macros��
     */
    private void initMacros() throws Exception {
        ResourcePatternResolver resolver;

        if (loader instanceof ResourcePatternResolver) {
            resolver = (ResourcePatternResolver) loader;
        } else {
            resolver = new PathMatchingResourcePatternResolver(loader);
        }

        if (macros != null) {
            for (String macro : macros) {
                resolveMacro(resolver, macro);
            }
        }

        // Velocity default: VM_global_library.vm
        resolveMacro(resolver, VM_LIBRARY_DEFAULT);

        // Plugin macros
        if (plugins != null) {
            for (Object plugin : plugins) {
                if (plugin instanceof VelocityPlugin) {
                    addMacroResources(null, ((VelocityPlugin) plugin).getMacros());
                }
            }
        }

        if (!properties.containsKey(VM_LIBRARY)) {
            properties.setProperty(VM_LIBRARY, EMPTY_STRING);
        }
    }

    private void resolveMacro(ResourcePatternResolver resolver, String macro) {
        String path = normalizeAbsolutePath(this.path + "/");
        String pattern = normalizeAbsolutePath(path + macro);
        Resource[] resources;

        try {
            resources = resolver.getResources(pattern);
        } catch (IOException e) {
            resources = null;
        }

        addMacroResources(path, resources);
    }

    private void addMacroResources(String path, Resource[] resources) {
        if (resources != null) {
            // ������vector������VelocimacroFactory�ϴ��������ֵ
            @SuppressWarnings("unchecked")
            Set<String> macros = createHashSet(properties.getVector(VM_LIBRARY));

            for (Resource resource : resources) {
                if (resource.exists()) {
                    String templateName = null;

                    // ���ڶ���resource����ServletResource��ResourceAdapter�ȣ������Դ���ȡ��ԭʼ��resourceName
                    if (path != null && resource instanceof ContextResource) {
                        String resourceName = ((ContextResource) resource).getPathWithinContext();

                        if (resourceName.startsWith(path)) {
                            templateName = resourceName.substring(path.length());
                        }
                    }

                    // ���ڲ���ȡ��resourceName�ģ�ʹ�������װ�ػ��ơ�
                    if (templateName == null) {
                        templateName = getTemplateNameOfPreloadedResource(resource);
                    }

                    if (!macros.contains(templateName)) {
                        properties.addProperty(VM_LIBRARY, templateName);
                        macros.add(templateName);
                    }
                }
            }
        }
    }

    private String getTemplateNameOfPreloadedResource(Resource resource) {
        URL url;

        try {
            url = resource.getURL();
        } catch (IOException e) {
            url = null;
        }

        String templateNameBase;

        if (url != null) {
            templateNameBase = "globalVMs/" + StringUtils.getFilename(url.getPath());
        } else {
            templateNameBase = "globalVMs/globalVM.vm";
        }

        String templateName = templateNameBase;

        // ��ֹ�����ظ���resource����
        for (int i = 1; preloadedResources.containsKey(templateName)
                && !resource.equals(preloadedResources.get(templateName)); i++) {
            templateName = templateNameBase + i;
        }

        preloadedResources.put(templateName, resource);

        return templateName;
    }

    private void initEventHandlers() {
        // ׼��eventCartridge��������Ĭ�ϵ�handler
        boolean hasRenderableHandler = false;

        if (!isEmptyArray(plugins)) {
            for (Object plugin : plugins) {
                if (plugin instanceof RenderableHandler) {
                    hasRenderableHandler = true;
                    break;
                }
            }
        }

        if (!hasRenderableHandler) {
            addHandler(new RenderableHandler());
        }

        if (!isEmptyArray(plugins)) {
            for (Object plugin : plugins) {
                if (plugin instanceof EventHandler) {
                    addHandler((EventHandler) plugin);
                }
            }
        }
    }

    /**
     * ��ʼ�����
     */
    private void initMiscs() {
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        setDefaultProperty(RESOURCE_MANAGER_LOGWHENFOUND, "false");
        setDefaultProperty(INPUT_ENCODING, charset);
        setDefaultProperty(OUTPUT_ENCODING, DEFAULT_CHARSET);
        setDefaultProperty(PARSER_POOL_SIZE, "50");
        setDefaultProperty(UBERSPECT_CLASSNAME, CustomizedUberspectImpl.class.getName());
        setDefaultProperty(VM_ARGUMENTS_STRICT, "true");
        setDefaultProperty(VM_PERM_INLINE_LOCAL, "true");
        setDefaultProperty(SET_NULL_ALLOWED, "true");

        // auto-reload macros
        if (productionMode) {
            properties.setProperty(VM_LIBRARY_AUTORELOAD, "false");
        } else {
            properties.setProperty(VM_LIBRARY_AUTORELOAD, "true");
        }

        // strict ref
        properties.setProperty(RUNTIME_REFERENCES_STRICT, String.valueOf(strictReference));
    }

    /**
     * ����Ĭ��ֵ�����ֵ�Ѵ��ڣ��򲻸��ǡ�
     */
    private void setDefaultProperty(String key, Object value) {
        if (!properties.containsKey(key)) {
            properties.setProperty(key, value);
        }
    }

    @Override
    public String toString() {
        return new MapBuilder().setSortKeys(true).setPrintCount(true).appendAll(properties).toString();
    }
}
