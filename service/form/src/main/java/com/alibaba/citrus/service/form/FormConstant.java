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

/**
 * ����form service�ĳ�����
 * 
 * @author Michael Zhou
 */
public final class FormConstant {
    /**
     * ��request attribute�У����û��ύ��form�У���Ϊform key��ǰ׺��
     */
    public final static String FORM_KEY_PREFIX = "_fm";

    /**
     * �ָ�field key�����ֵķָ�����
     */
    public final static char FIELD_KEY_SEPARATOR = '.';

    /**
     * Ĭ�ϵ�group instance key��
     */
    public final static String DEFAULT_GROUP_INSTANCE_KEY = "_0";

    /**
     * ���û��ύ�ı���δ����ָ��field��Ϣʱ��ȡ��ֵ��Ϊfield��ֵ��
     */
    public final static String FORM_FIELD_ABSENT_KEY = ".absent";

    /**
     * ����ĳ��field����ĸ�����key�ĺ�׺��
     */
    public final static String FORM_FIELD_ATTACHMENT_KEY = ".attach";

    /**
     * ����message code��Ĭ��ǰ׺��
     */
    public final static String FORM_MESSAGE_CODE_PREFIX = "form.";
}
