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
package com.alibaba.citrus.service.form.impl.configuration;

import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static java.util.Collections.*;

import java.util.List;

import com.alibaba.citrus.service.form.Validator;
import com.alibaba.citrus.service.form.configuration.FieldConfig;
import com.alibaba.citrus.service.form.configuration.GroupConfig;

/**
 * ����һ��form field�Ķ�����Ϣ��
 * 
 * @author Michael Zhou
 */
public class FieldConfigImpl extends AbstractConfig<FieldConfig> implements FieldConfig {
    private GroupConfig groupConfig;
    private String name;
    private String key;
    private String displayName;
    private String[] defaultValues;
    private Boolean trimming;
    private String propertyName;
    private List<Validator> validators;
    private List<Validator> validatorList;

    /**
     * ȡ��field������group config��
     */
    public GroupConfig getGroupConfig() {
        return groupConfig;
    }

    /**
     * ����field������group config��
     */
    public void setGroupConfig(GroupConfig groupConfig) {
        this.groupConfig = groupConfig;
    }

    /**
     * ȡ��field name��
     */
    public String getName() {
        return name;
    }

    /**
     * ����field name��
     */
    public void setName(String name) {
        this.name = trimToNull(name);
    }

    /**
     * ȡ��field key��
     */
    public String getKey() {
        return key;
    }

    /**
     * ����field key��
     */
    public void setKey(String key) {
        this.key = trimToNull(key);
    }

    /**
     * ȡ��������ʾfield�����ơ�
     */
    public String getDisplayName() {
        return displayName == null ? getName() : displayName;
    }

    /**
     * ����������ʾfield�����ơ�
     */
    public void setDisplayName(String displayName) {
        this.displayName = trimToNull(displayName);
    }

    /**
     * ȡ��trimmingѡ�
     */
    public boolean isTrimming() {
        if (trimming == null) {
            return groupConfig == null ? true : getGroupConfig().isTrimmingByDefault();
        } else {
            return trimming.booleanValue();
        }
    }

    /**
     * ����trimmingѡ�
     */
    public void setTrimming(boolean trimming) {
        this.trimming = trimming;
    }

    /**
     * ȡ��bean property���ơ�
     */
    public String getPropertyName() {
        return propertyName == null ? getName() : propertyName;
    }

    /**
     * ����bean property���ơ�
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = trimToNull(propertyName);
    }

    /**
     * ȡ�õ���Ĭ��ֵ��
     */
    public String getDefaultValue() {
        if (!isEmptyArray(defaultValues)) {
            return defaultValues[0];
        } else {
            return null;
        }
    }

    /**
     * ȡ��һ��Ĭ��ֵ��
     */
    public String[] getDefaultValues() {
        if (!isEmptyArray(defaultValues)) {
            return defaultValues.clone();
        } else {
            return EMPTY_STRING_ARRAY;
        }
    }

    /**
     * ����Ĭ��ֵ��
     */
    public void setDefaultValues(String[] defaultValues) {
        if (!isEmptyArray(defaultValues)) {
            this.defaultValues = defaultValues.clone();
        }
    }

    /**
     * ȡ��validator�б�
     */
    public List<Validator> getValidators() {
        if (validatorList == null) {
            return emptyList();
        } else {
            return validatorList;
        }
    }

    /**
     * ����һ��validator��
     */
    public void setValidators(List<Validator> validators) {
        if (validators != null) {
            initValidatorList();
            this.validators.addAll(validators);
        }
    }

    private void initValidatorList() {
        validators = createArrayList();
        validatorList = unmodifiableList(validators);
    }

    /**
     * ��ָ��field�е����ݸ��Ƶ���ǰfield�С�
     */
    void mergeWith(FieldConfigImpl src) {
        if (name == null) {
            setName(src.name);
        }

        if (displayName == null) {
            setDisplayName(src.displayName);
        }

        if (isEmptyArray(defaultValues)) {
            setDefaultValues(src.defaultValues);
        }

        if (trimming == null) {
            trimming = src.trimming;
        }

        if (propertyName == null) {
            setPropertyName(src.propertyName);
        }

        if (validators == null) {
            initValidatorList();
        }

        for (Validator validator : src.getValidators()) {
            validators.add(validator.clone());
        }
    }

    /**
     * ת���������Ķ����ַ�����
     */
    @Override
    public String toString() {
        String groupName = groupConfig == null ? null : groupConfig.getName();
        return "FieldConfig[group: " + groupName + ", name: " + getName() + ", validators: " + getValidators().size()
                + "]";
    }
}
