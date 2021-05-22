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
package com.alibaba.citrus.service.mappingrule.support;

import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.configuration.ProductionModeAware;
import com.alibaba.citrus.service.mappingrule.MappingRule;
import com.alibaba.citrus.service.mappingrule.MappingRuleException;
import com.alibaba.citrus.springext.support.BeanSupport;

public abstract class AbstractMappingRule extends BeanSupport implements MappingRule, ProductionModeAware {
    /** ��ת�������Ƶķָ����� */
    public static final String NAME_SEPARATOR = ",/";

    /** ��ת�������Ƶĺ�׺�ָ����� */
    public static final String EXTENSION_SEPARATOR = ".";

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private Boolean cacheEnabled;
    private boolean productionMode = true;
    private Map<String, String> cache;

    public Boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(Boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * ȡ��Ĭ�ϵ�<code>cacheEnabled</code>ֵ��
     * <p>
     * Ĭ�������ȡ���ڵ�ǰ�Ƿ�Ϊ����ģʽ����<code>productionMode</code>Ϊ<code>true</code>
     * ʱ����cache��������Ըı����Ϊ��
     * </p>
     */
    protected boolean isCacheEnabledByDefault() {
        return isProductionMode();
    }

    public boolean isProductionMode() {
        return productionMode;
    }

    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }

    @Override
    protected final void init() throws Exception {
        if (cacheEnabled == null) {
            cacheEnabled = isCacheEnabledByDefault();
        }

        if (cacheEnabled) {
            cache = createConcurrentHashMap();
        }

        initMappingRule();

        log.info("Initialized {} with cache {}", getBeanDescription(), cacheEnabled ? "enabled" : "disabled");
    }

    protected void initMappingRule() throws Exception {
    }

    public final String getMappedName(String name) {
        name = trimToNull(name);

        if (name == null) {
            return null;
        }

        String mappedName = null;

        if (isCacheEnabled()) {
            mappedName = cache.get(name);

            // ���cache���Ѿ���ֵ�ˣ���ֱ�ӷ��ء�
            // ע�⣬cache�еĿ��ַ���ֵ����null��
            if (mappedName != null) {
                return trimToNull(mappedName);
            }
        }

        log.trace("doMapping(\"{}\")", name);

        mappedName = doMapping(name);

        log.debug("doMapping(\"{}\") returned: ", name, mappedName);

        // ע�⣬����cacheֵΪnull�Ľ������nullת�ɿ��ַ��������棩
        if (isCacheEnabled()) {
            cache.put(name, trimToEmpty(mappedName));
        }

        return mappedName;
    }

    /**
     * ��ָ������ӳ���ָ�����͵����ơ����ӳ��ʧ�ܣ��򷵻�<code>null</code>��
     */
    protected abstract String doMapping(String name);

    /**
     * �׳��쳣����ʾҪת�������ƷǷ���ת��ʧ�ܡ�
     */
    protected static String throwInvalidNameException(String name) {
        return throwInvalidNameException(name, null);
    }

    /**
     * �׳��쳣����ʾҪת�������ƷǷ���ת��ʧ�ܡ�
     */
    protected static String throwInvalidNameException(String name, Exception e) {
        throw new MappingRuleException("Failed to do mapping for name \"" + name + "\"", e);
    }
}
