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
package com.alibaba.citrus.service.form.configuration;

import java.util.List;

/**
 * ����һ��form group�Ķ�����Ϣ��
 * <p>
 * Form group�����ǲ��ɸ��ĵġ�
 * </p>
 * 
 * @author Michael Zhou
 */
public interface GroupConfig {
    /**
     * ȡ��group������form config��
     */
    FormConfig getFormConfig();

    /**
     * ȡ��group name��
     */
    String getName();

    /**
     * ȡ��parent group��
     */
    String getParentGroup();

    /**
     * ȡ��group key��
     */
    String getKey();

    /**
     * ȡ��Ĭ�ϵ�trimmingѡ�
     */
    boolean isTrimmingByDefault();

    /**
     * Group�Ƿ�����post������ȡ�����ݡ�
     */
    boolean isPostOnly();

    /**
     * ȡ������field config���б�
     */
    List<FieldConfig> getFieldConfigList();

    /**
     * ȡ��ָ�����Ƶ�field config�����ƴ�Сд�����С� ���δ�ҵ����򷵻�<code>null</code>��
     */
    FieldConfig getFieldConfig(String fieldName);

    /**
     * ȡ��ָ��key��Ӧ��field config�����δ�ҵ����򷵻�<code>null</code>��
     */
    FieldConfig getFieldConfigByKey(String fieldKey);

    /**
     * ȡ�����е�imports��
     */
    List<Import> getImports();

    /**
     * ����import����group�е�field����Ϣ��
     */
    interface Import {
        String getGroupName();

        String getFieldName();
    }
}
