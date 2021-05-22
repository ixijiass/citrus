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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.citrus.service.form.configuration.GroupConfig;

/**
 * �����û����ύ���е�һ���ֶΡ�
 * <p>
 * ע�⣺group�������̰߳�ȫ�ģ����ܱ����̹߳���
 * </p>
 * 
 * @author Michael Zhou
 */
public interface Group {
    /**
     * ȡ��group��������Ϣ��
     */
    GroupConfig getGroupConfig();

    /**
     * ȡ�ð�����group��form��
     */
    Form getForm();

    /**
     * ȡ��group name���൱��<code>getGroupConfig().getName()</code>
     */
    String getName();

    /**
     * ȡ�ô���group��key��
     * <p>
     * �ɹ̶�ǰ׺<code>"_fm"</code>������group������д���ټ���group instance key���ɡ����磺
     * <code>_fm.m._0</code>��
     * </p>
     */
    String getKey();

    /**
     * ȡ�ñ�ʶ��ǰgroup��instance key��
     */
    String getInstanceKey();

    /**
     * �ж�group�Ƿ�ͨ����֤��
     */
    boolean isValid();

    /**
     * �ж���group�Ƿ���ֵ������֤������������£�<code>isValidated()</code>Ϊ<code>true</code>��
     * <ol>
     * <li>�û��ύ������ǰgroup�ֶεı�����ʱ��Ӧ��group����ʼ������֤��</li>
     * <li>�������<code>validate()</code>���������ַ�ʽ�£�group�е��ֶ�ֵ�����ɳ��������ã�Ч����ͬ�û��ύ��һ����</li>
     * </ol>
     */
    boolean isValidated();

    /**
     * ��ʼ��group��
     */
    void init();

    /**
     * ��ʼ��group�� ���У� <code>request</code>������<code>null</code>�����
     * <code>request</code>��Ϊ<code>null</code>����ͬʱ��֤����
     */
    void init(HttpServletRequest request);

    /**
     * ��֤����������֤����ǰ���ֶ�ֵ��
     * <p>
     * ע�⣬�˷���������<code>isValidated()</code>Ϊ<code>true</code>��
     * </p>
     */
    void validate();

    /**
     * ȡ������fields���б�
     */
    Collection<Field> getFields();

    /**
     * ȡ��ָ�����Ƶ�field��field���ƣ���Сд�����У�
     */
    Field getField(String fieldName);

    /**
     * �������е�����ֵ���뵽fields�С�
     * <p>
     * ����<code>isValidated()</code>Ϊ<code>true</code>��group���÷�����Ч��
     * </p>
     */
    void mapTo(Object object);

    /**
     * ��group�е�ֵ����ָ������
     * <p>
     * ����<code>isValidated()</code>Ϊ<code>false</code>��group���÷�����Ч��
     * </p>
     */
    void setProperties(Object object);
}
