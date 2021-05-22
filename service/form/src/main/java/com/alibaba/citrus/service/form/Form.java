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

import org.springframework.beans.TypeConverter;

import com.alibaba.citrus.service.form.configuration.FormConfig;

/**
 * ����һ���û��ύ��form��Ϣ��
 * <p>
 * ע�⣺form�������̰߳�ȫ�ģ����ܱ����̹߳���
 * </p>
 * 
 * @author Michael Zhou
 */
public interface Form {
    /**
     * ȡ��form��������Ϣ��
     */
    FormConfig getFormConfig();

    /**
     * ȡ������ת�����͵�converter��
     */
    TypeConverter getTypeConverter();

    /**
     * �Ƿ�ǿ��Ϊֻ����post����
     */
    boolean isForcePostOnly();

    /**
     * �ж�form�Ƿ�ͨ����֤��
     */
    boolean isValid();

    /**
     * ��ʼ��form����form�ָ��ɡ�δ��֤��״̬����󣬵����߿�����������ֵ���ֹ���֤����
     */
    void init();

    /**
     * ��request��ʼ��form������requestΪ<code>null</code>����form���óɡ�δ��֤��״̬��������֤����
     */
    void init(HttpServletRequest request);

    /**
     * ��֤����������֤����ǰ������group instance��
     */
    void validate();

    /**
     * ȡ�ô���form��key��
     */
    String getKey();

    /**
     * ȡ������group���б�
     */
    Collection<Group> getGroups();

    /**
     * ȡ������ָ�����Ƶ�group���б�group���ƴ�Сд�����С�
     */
    Collection<Group> getGroups(String groupName);

    /**
     * ȡ��Ĭ�ϵ�group instance�������group instance�����ڣ��򴴽�֮��Group���ƴ�Сд�����С�
     */
    Group getGroup(String groupName);

    /**
     * ȡ��group instance�������group instance�����ڣ��򴴽�֮��Group���ƴ�Сд�����С�
     */
    Group getGroup(String groupName, String instanceKey);

    /**
     * ȡ��group instance�������group instance�����ڣ�����<code>create == true</code>
     * ���򴴽�֮��Group���ƴ�Сд�����С�
     */
    Group getGroup(String groupName, String instanceKey, boolean create);
}
