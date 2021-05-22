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

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.Assert.ExceptionType.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static java.util.Collections.*;

import java.util.List;
import java.util.Map;

import com.alibaba.citrus.service.form.Validator;
import com.alibaba.citrus.service.form.configuration.FieldConfig;
import com.alibaba.citrus.service.form.configuration.FormConfig;
import com.alibaba.citrus.service.form.configuration.GroupConfig;

/**
 * ����һ��form group�Ķ�����Ϣ��
 * 
 * @author Michael Zhou
 */
public class GroupConfigImpl extends AbstractConfig<GroupConfig> implements GroupConfig {
    private FormConfig formConfig;
    private String parentGroup;
    private String name;
    private String key;
    private Map<String, FieldConfigImpl> fields; // field name to fieldConfig
    private Map<String, FieldConfigImpl> fieldsByKey; // field key to fieldConfig
    private List<Import> imports; // imported groups
    private List<Import> importList; // unmodifiable imported groups
    private List<FieldConfig> fieldList; // unmodifiable field list
    private Boolean trimmingByDefault;
    private Boolean postOnly;

    /**
     * ȡ��group������form config��
     */
    public FormConfig getFormConfig() {
        return formConfig;
    }

    /**
     * ����group������form config��
     */
    public void setFormConfig(FormConfig formConfig) {
        this.formConfig = formConfig;
    }

    /**
     * ȡ��group name��
     */
    public String getName() {
        return name;
    }

    /**
     * ����group name��
     */
    public void setName(String name) {
        this.name = trimToNull(name);
    }

    /**
     * ȡ��group key��
     */
    public String getKey() {
        return key;
    }

    /**
     * ����group key��
     */
    public void setKey(String key) {
        this.key = trimToNull(key);
    }

    /**
     * ȡ��parent group��
     */
    public String getParentGroup() {
        return parentGroup;
    }

    /**
     * ����parent group������parent group�е����ݶ��ᱻ���뵽��ǰgroup�С�
     */
    public void setParentGroup(String parentGroup) {
        this.parentGroup = trimToNull(parentGroup);
    }

    /**
     * ȡ��Ĭ�ϵ�trimmingѡ�
     */
    public boolean isTrimmingByDefault() {
        return trimmingByDefault == null ? true : trimmingByDefault.booleanValue();
    }

    /**
     * ����Ĭ�ϵ�trimmingѡ�
     */
    public void setTrimmingByDefault(boolean trimmingByDefault) {
        this.trimmingByDefault = trimmingByDefault;
    }

    /**
     * Group�Ƿ�����post������ȡ�����ݡ�
     */
    public boolean isPostOnly() {
        if (postOnly == null) {
            return formConfig == null ? true : formConfig.isPostOnlyByDefault();
        } else {
            return postOnly.booleanValue();
        }
    }

    /**
     * ����group�Ƿ�����post������ȡ�����ݡ�
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    /**
     * ȡ������field config���б�
     */
    public List<FieldConfig> getFieldConfigList() {
        if (fieldList == null) {
            return emptyList();
        } else {
            return fieldList;
        }
    }

    /**
     * ȡ��ָ�����Ƶ�field config�����ƴ�Сд�����С� ���δ�ҵ����򷵻�<code>null</code>��
     */
    public FieldConfig getFieldConfig(String fieldName) {
        if (fields == null) {
            return null;
        } else {
            return fields.get(caseInsensitiveName(fieldName));
        }
    }

    /**
     * ȡ��ָ��key��Ӧ��field config�����δ�ҵ����򷵻�<code>null</code>��
     */
    public FieldConfig getFieldConfigByKey(String fieldKey) {
        return assertNotNull(fieldsByKey, ILLEGAL_STATE, "fieldsByKey not inited").get(fieldKey);
    }

    /**
     * ����һ��field configs��
     */
    public void setFieldConfigImplList(List<FieldConfigImpl> fieldConfigList) {
        if (fieldConfigList != null) {
            fields = createLinkedHashMap();

            for (FieldConfigImpl fieldConfig : fieldConfigList) {
                addFieldConfig(fieldConfig, true); // ��Сд�����У�
            }
        }
    }

    /**
     * ���һ��field config��
     */
    private void addFieldConfig(FieldConfigImpl fieldConfig, boolean checkDuplicate) {
        if (fields == null) {
            fields = createLinkedHashMap();
        }

        String fieldName = caseInsensitiveName(fieldConfig.getName()); // ��Сд�����У�

        if (checkDuplicate) {
            assertTrue(!fields.containsKey(fieldName), "Duplicated field name: \"%s.%s\"", getName(),
                    fieldConfig.getName());
        }

        fields.put(fieldName, fieldConfig);
    }

    /**
     * ȡ�����е�imports��
     */
    public List<Import> getImports() {
        if (importList == null) {
            return emptyList();
        } else {
            return importList;
        }
    }

    /**
     * ��������group���ֶΡ����fieldNameΪnull������������group��ͬextends����
     */
    public void setImports(List<Import> imports) {
        if (imports != null) {
            this.imports = createArrayList(imports);
            this.importList = unmodifiableList(this.imports);
        }
    }

    /**
     * ��չ��ǰgroup����ָ��group�е����ݸ��Ƶ���ǰgroup�С�
     */
    void extendsFrom(GroupConfigImpl parentGroupConfig) {
        if (trimmingByDefault == null && parentGroupConfig.trimmingByDefault != null) {
            trimmingByDefault = parentGroupConfig.trimmingByDefault;
        }

        if (postOnly == null && parentGroupConfig.postOnly != null) {
            postOnly = parentGroupConfig.postOnly;
        }

        extendsOrImports(parentGroupConfig, null, false);
    }

    /**
     * ��ָ��group�е����ݸ��Ƶ���ǰgroup�С�
     */
    void importsFrom(GroupConfigImpl srcGroupConfig, String fieldName) {
        extendsOrImports(srcGroupConfig, fieldName, true);
    }

    /**
     * ��չ������fields��
     */
    private void extendsOrImports(GroupConfigImpl srcGroupConfig, String fieldName, boolean checkDuplicate) {
        if (fieldName == null) {
            // merge/import all
            for (FieldConfig srcFieldConfig : srcGroupConfig.getFieldConfigList()) {
                mergeField((FieldConfigImpl) srcFieldConfig, checkDuplicate);
            }
        } else {
            // merge/import single field
            FieldConfig srcFieldConfig = srcGroupConfig.getFieldConfig(fieldName);
            assertNotNull(srcFieldConfig, "Field \"%s.%s\" not found", srcGroupConfig.getName(), fieldName);
            mergeField((FieldConfigImpl) srcFieldConfig, checkDuplicate);
        }
    }

    /**
     * �ϲ�field��
     */
    private void mergeField(FieldConfigImpl srcFieldConfig, boolean checkDuplicate) {
        FieldConfigImpl copy = (FieldConfigImpl) getFieldConfig(srcFieldConfig.getName());

        if (copy == null) {
            // �����ǰgroup��δ����ͬ����field���򴴽�֮
            copy = new FieldConfigImpl();
            copy.setGroupConfig(this);
        }

        copy.mergeWith(srcFieldConfig);

        // �����ǰgroup���Ѿ�������ͬ����field����ô��
        // ��checkDuplicate==falseʱ���ϲ�field��extends group�����Σ���
        // ��checkDuplicate==trueʱ������imports field�����Σ�
        addFieldConfig(copy, checkDuplicate);
    }

    /**
     * ��ʼ��group config��
     * <p>
     * ��ͬ��<code>init()</code>�������˷����Ǳ�<code>formConfig.init()</code>���á�
     * </p>
     */
    void init2() throws Exception {
        assertNotNull(fields, "no fields");

        fieldsByKey = createHashMap();
        fieldList = createArrayList(fields.size());

        for (Map.Entry<String, FieldConfigImpl> entry : fields.entrySet()) {
            String caseInsensitiveName = entry.getKey();
            FieldConfigImpl fieldConfig = entry.getValue();

            // ���ò��ظ���key
            for (int i = 1; i <= caseInsensitiveName.length(); i++) {
                String key = caseInsensitiveName.substring(0, i);

                if (!fieldsByKey.containsKey(key)) {
                    fieldConfig.setKey(key);
                    fieldsByKey.put(key, fieldConfig);
                    break;
                }
            }

            // ����field.group
            fieldConfig.setGroupConfig(this);

            // ����fieldList
            fieldList.add(fieldConfig);
        }

        fieldList = unmodifiableList(fieldList);

        // ��ʼ������validators
        for (FieldConfig fieldConfig : fieldList) {
            for (Validator validator : fieldConfig.getValidators()) {
                validator.init(fieldConfig);
            }
        }
    }

    /**
     * ת���������Ķ����ַ�����
     */
    @Override
    public String toString() {
        return "GroupConfig[name: " + getName() + ", fields: " + getFieldConfigList().size() + "]";
    }

    /**
     * ����import����group�е�field����Ϣ��
     */
    public static final class ImportImpl implements Import {
        private final String groupName;
        private final String fieldName;

        public ImportImpl(String groupName, String fieldName) {
            this.groupName = trimToNull(groupName);
            this.fieldName = trimToNull(fieldName);
        }

        public String getGroupName() {
            return groupName;
        }

        public String getFieldName() {
            return fieldName;
        }

        @Override
        public String toString() {
            return fieldName == null ? groupName : groupName + "." + fieldName;
        }
    }
}
