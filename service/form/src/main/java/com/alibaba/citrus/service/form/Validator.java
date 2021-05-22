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
package com.alibaba.citrus.service.form;

import com.alibaba.citrus.service.form.configuration.FieldConfig;

/**
 * ������֤������֤����
 * 
 * @author Michael Zhou
 */
public interface Validator extends Cloneable {
    /**
     * ��GroupConfig����ʼ������Ժ󱻵��ã���ʱ��ȡ��ͬ����������fields��
     */
    void init(FieldConfig fieldConfig) throws Exception;

    /**
     * ȡ��validator��ID��ͨ����ID�����ҵ�ָ����validator��
     */
    String getId();

    /**
     * ȡ�ó�����Ϣ��
     */
    String getMessage(Context context);

    /**
     * ��֤һ���ֶΡ�
     */
    boolean validate(Context context);

    /**
     * ���ɸ�����
     */
    Validator clone();

    /**
     * Я����validator��֤���������������Ϣ��
     */
    interface Context {
        /**
         * ȡ�õ�ǰfield��
         */
        Field getField();

        /**
         * ȡ��ָ�����Ƶ�field��
         */
        Field getField(String fieldName);

        /**
         * ȡ��ֵ��
         */
        Object getValue();

        /**
         * ȡ��ָ�����͵�ֵ��
         */
        <T> T getValueAsType(Class<T> type);

        /**
         * ȡ�ô�����Ϣ��
         */
        String getMessage();

        /**
         * ���ô�����Ϣ��
         */
        void setMessage(String message);

        /**
         * ȡ������������ʽ�������Ķ���
         */
        MessageContext getMessageContext();
    }
}
