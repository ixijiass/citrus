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

import com.alibaba.citrus.service.form.Validator;

/**
 * ����һ��form field�Ķ�����Ϣ��
 * <p>
 * Form field�����ǲ��ɸ��ĵġ�
 * </p>
 * 
 * @author Michael Zhou
 */
public interface FieldConfig {
    /**
     * ȡ��field������group config��
     */
    GroupConfig getGroupConfig();

    /**
     * ȡ��field name��
     */
    String getName();

    /**
     * ȡ��field key��
     */
    String getKey();

    /**
     * ȡ��������ʾfield�����ơ�
     */
    String getDisplayName();

    /**
     * ȡ��trimmingѡ�
     */
    boolean isTrimming();

    /**
     * ȡ��bean property���ơ�
     */
    String getPropertyName();

    /**
     * ȡ�õ���Ĭ��ֵ��
     */
    String getDefaultValue();

    /**
     * ȡ��һ��Ĭ��ֵ��
     */
    String[] getDefaultValues();

    /**
     * ȡ��validator�б�
     */
    List<Validator> getValidators();
}
