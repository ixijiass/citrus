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
package com.alibaba.citrus.service.freemarker.impl;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static freemarker.core.Configurable.*;
import static freemarker.template.Configuration.*;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.core.io.ResourceLoader;

import com.alibaba.citrus.service.freemarker.FreeMarkerConfiguration;
import com.alibaba.citrus.service.freemarker.FreeMarkerPlugin;
import com.alibaba.citrus.service.freemarker.support.DefaultBeansWrapper;
import com.alibaba.citrus.service.template.TemplateException;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

import freemarker.cache.StrongCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

/**
 * ����һ��freemarker engine�����á�
 * 
 * @author Michael Zhou
 */
public class FreeMarkerConfigurationImpl implements FreeMarkerConfiguration {
    private final Logger log;
    private final Configuration configuration = new Configuration();
    private final Map<String, String> properties = createHashMap();
    private boolean productionMode = true;
    private ResourceLoader loader;
    private TemplateLoader templateLoader;
    private String path;
    private String charset;
    private FreeMarkerPlugin[] plugins;

    /**
     * ����һ��freemarker���á�
     */
    public FreeMarkerConfigurationImpl(Logger log) {
        this.log = assertNotNull(log, "log");
    }

    /**
     * ȡ������װ��ģ���loader��
     */
    public TemplateLoader getTemplateLoader() {
        return templateLoader;
    }

    /**
     * ȡ��freemarker�����á�
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    public Map<String, String> getProperties() {
        return properties;
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
     * ����ģ����ַ������롣
     */
    public void setTemplateEncoding(String charset) {
        this.charset = trimToNull(charset);
    }

    /**
     * ���ø߼����á�
     */
    public void setAdvancedProperties(Map<String, String> configuration) {
        this.properties.clear();
        this.properties.putAll(configuration);
    }

    /**
     * ����plugins��
     */
    public void setPlugins(FreeMarkerPlugin[] plugins) {
        this.plugins = plugins;
    }

    /**
     * ��ʼ��configuration��
     */
    public void init() {
        removeReservedProperties();

        initProperties();
        initPlugins();
        initWrapper();
    }

    /**
     * ɾ��������properties����Щproperties�û������޸ġ�
     */
    private void removeReservedProperties() {
        Set<String> keysToRemove = createHashSet();

        keysToRemove.add(DEFAULT_ENCODING_KEY);
        keysToRemove.add(LOCALIZED_LOOKUP_KEY);

        // do removing
        for (String key : keysToRemove) {
            if (properties.containsKey(key)) {
                log.warn("Removed reserved property: {} = {}", key, properties.get(key));
                properties.remove(key);
            }
        }
    }

    private void initProperties() {
        assertNotNull(loader, "resourceLoader");

        // ģ���ַ�������
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        path = defaultIfNull(path, "/templates");
        templateLoader = new SpringResourceLoaderAdapter(loader, path);

        configuration.setTemplateLoader(templateLoader);

        // Ĭ��ʹ��StrongCacheStorage
        setDefaultProperty(CACHE_STORAGE_KEY, StrongCacheStorage.class.getName());

        // �쳣������
        setDefaultProperty(TEMPLATE_EXCEPTION_HANDLER_KEY, "rethrow");

        // ����Ĭ��ѡ��
        setDefaultProperty(DEFAULT_ENCODING_KEY, charset);
        setDefaultProperty(OUTPUT_ENCODING_KEY, DEFAULT_CHARSET);
        setDefaultProperty(LOCALIZED_LOOKUP_KEY, "false");

        // ����ѡ��
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = trimToNull(entry.getValue());

            if (value != null) {
                try {
                    configuration.setSetting(key, value);
                } catch (freemarker.template.TemplateException e) {
                    throw new TemplateException("invalid key and value: " + key + " = " + value, e);
                }
            }
        }
    }

    private void initPlugins() {
        if (plugins != null) {
            for (FreeMarkerPlugin plugin : plugins) {
                plugin.init(this);
            }
        }
    }

    private void initWrapper() {
        // ����ObjectWrapper��ʹ֧֮��TemplateContext����
        configuration.setObjectWrapper(new DefaultBeansWrapper(configuration.getObjectWrapper()));
    }

    /**
     * ����Ĭ��ֵ�����ֵ�Ѵ��ڣ��򲻸��ǡ�
     */
    private void setDefaultProperty(String key, String value) {
        if (properties.get(key) == null) {
            properties.put(key, value);
        }
    }

    @Override
    public String toString() {
        return new MapBuilder().setSortKeys(true).setPrintCount(true).appendAll(properties).toString();
    }
}
