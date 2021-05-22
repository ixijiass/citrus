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

import javax.servlet.http.HttpServletRequest;

import com.alibaba.citrus.service.form.configuration.FieldConfig;
import com.alibaba.citrus.service.requestcontext.util.ValueList;

/**
 * �����û����ύ���е�һ��field��
 * <p>
 * ע�⣺field�������̰߳�ȫ�ģ����ܱ����̹߳���
 * </p>
 * 
 * @author Michael Zhou
 */
public interface Field extends ValueList, CustomErrors {
    /**
     * ȡ��field��������Ϣ��
     */
    FieldConfig getFieldConfig();

    /**
     * ȡ�ð�����field��group��
     */
    Group getGroup();

    /**
     * �ж�field�Ƿ�ͨ����֤��
     */
    boolean isValid();

    /**
     * ȡ����form��Ψһ�����field��key��
     * <p>
     * �ɹ̶�ǰ׺<code>"_fm"</code>������group������д������group instance
     * fieldKey���ټ���field������д���ɡ����磺<code>_fm.m._0.n</code>��
     * </p>
     */
    String getKey();

    /**
     * ȡ����form��Ψһ�����field��key�����û��ύ�ı���δ������field����Ϣʱ��ȡ���key��ֵ��Ϊ��field��ֵ��
     * <p>
     * �����checkbox֮���HTML�ؼ��ر����á�
     * </p>
     * <p>
     * Key�ĸ�ʽΪ��<code>_fm.groupKey.instanceKey.fieldKey.absent</code>��
     * </p>
     */
    String getAbsentKey();

    /**
     * ȡ����form�к͵�ǰfield�󶨵ĸ�����key��
     * <p>
     * Key�ĸ�ʽΪ��<code>_fm.groupKey.instanceKey.fieldKey.attach</code>��
     * </p>
     */
    String getAttachmentKey();

    /**
     * ȡ�ó�����Ϣ��
     */
    String getMessage();

    /**
     * ��ʼ��fieldֵ��������֤���ֶΡ����У�<code>request</code>������<code>null</code>��
     */
    void init(HttpServletRequest request);

    /**
     * ȡ��field name���൱��<code>getFieldConfig().getName()</code>��
     */
    String getName();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>""</code>��
     */
    String getStringValue();

    /**
     * ȡ��������ʾfield�����ƣ��൱��<code>getFieldConfig().getDisplayName()</code>��
     */
    String getDisplayName();

    /**
     * ȡ��Ĭ��ֵ���൱��<code>getFieldConfig().getDefaultValue()</code>��
     */
    String getDefaultValue();

    /**
     * ȡ��Ĭ��ֵ���൱��<code>getFieldConfig().getDefaultValues()</code>��
     */
    String[] getDefaultValues();

    /**
     * ��Ӳ�����/����ֵ��
     */
    void addValue(Object value);

    /**
     * ���ø�����
     */
    Object getAttachment();

    /**
     * ���ñ����ĸ�����
     */
    String getAttachmentEncoded();

    /**
     * �Ƿ����������
     */
    boolean hasAttachment();

    /**
     * ���ø�����
     * <p>
     * ע�⣬��attachment�Ѿ�����ʱ���÷���������Ч����ǿ�����룬���ȵ���<code>clearAttachment()</code>��
     * </p>
     */
    void setAttachment(Object attachment);

    /**
     * ���������
     */
    void clearAttachment();
}
