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

import static com.alibaba.citrus.util.StringUtil.*;

import com.alibaba.citrus.service.moduleloader.ModuleLoaderService;

/**
 * ӳ�䵽ģ������<code>MappingRule</code>��
 * <p>
 * ��<code>MappingRule</code>�п��ܵ���<code>ModuleLoaderService</code>��ȷ��ģ���Ƿ���ڡ�
 * </p>
 * 
 * @author Michael Zhou
 */
public abstract class AbstractModuleMappingRule extends AbstractMappingRule {
    public static final String MODULE_NAME_SEPARATOR = ".";

    private ModuleLoaderService moduleLoaderService;
    private String moduleType;

    public ModuleLoaderService getModuleLoaderService() {
        return moduleLoaderService;
    }

    public void setModuleLoaderService(ModuleLoaderService moduleLoaderService) {
        this.moduleLoaderService = moduleLoaderService;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = trimToNull(moduleType);
    }

    /**
     * ��ָ�����ƹ�񻯳ɷ���class�����淶�����ƣ���ȥ��׺�������ַ���Ϊ��д��
     * 
     * @param className Ҫ��񻯵�����
     * @return ��񻯺����������������Ƿ����򷵻�<code>null</code>
     */
    protected static String normalizeClassName(String className) {
        className = trimToNull(className);

        if (className == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder(className);

        // ��ȥ��׺
        int index = className.lastIndexOf(EXTENSION_SEPARATOR);

        if (index != -1) {
            buf.setLength(index);
        }

        // ���ַ���д
        if (buf.length() == 0) {
            return null;
        } else {
            buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));
        }

        return buf.toString();
    }
}
