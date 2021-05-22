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

import org.springframework.beans.PropertyEditorRegistrar;

import com.alibaba.citrus.service.form.FormService;

/**
 * ����һ��form�Ķ�����Ϣ��
 * <p>
 * Form�����ǲ��ɸ��ĵġ�
 * </p>
 * 
 * @author Michael Zhou
 */
public interface FormConfig {
    /**
     * ȡ�ô�����form��service��
     */
    FormService getFormService();

    /**
     * ����ת������ʱ���Ƿ񲻱������Ƿ���Ĭ��ֵ��
     */
    boolean isConverterQuiet();

    /**
     * Group�Ƿ�Ĭ�ϱ����post������ȡ�����ݡ�
     */
    boolean isPostOnlyByDefault();

    /**
     * ȡ��message code��ǰ׺��
     * <p>
     * Validator���Դ�spring <code>MessageSource</code>
     * ��ȡ��message���ݡ���������message��codeΪ��
     * <code>messageCodePrefix.groupName.fieldName.validatorId</code>��
     * </p>
     * <p>
     * Ĭ�ϵ�ǰ׺Ϊ��<code>form.</code>��
     * </p>
     */
    String getMessageCodePrefix();

    /**
     * ȡ������group config���б�
     */
    List<GroupConfig> getGroupConfigList();

    /**
     * ȡ��ָ�����Ƶ�group config�����ƴ�Сд�����С� ���δ�ҵ����򷵻�<code>null</code>��
     */
    GroupConfig getGroupConfig(String groupName);

    /**
     * ȡ�ú�ָ��key���Ӧ��group config�����δ�ҵ����򷵻�<code>null</code>
     */
    GroupConfig getGroupConfigByKey(String groupKey);

    /**
     * ȡ��<code>PropertyEditor</code>ע������
     * <p>
     * <code>PropertyEditor</code>�����ַ���ֵת����bean property�����ͣ���֮��
     * </p>
     */
    PropertyEditorRegistrar getPropertyEditorRegistrar();
}
