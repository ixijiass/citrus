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
package com.alibaba.citrus.generictype;

import java.lang.reflect.Field;

/**
 * ����һ��{@link Field}�ֶε���Ϣ��
 * 
 * @author Michael Zhou
 */
public interface FieldInfo {
    /**
     * ȡ���ֶΡ�
     */
    Field getField();

    /**
     * ȡ���ֶ����ڵ����͡�
     */
    TypeInfo getDeclaringType();

    /**
     * ȡ���ֶεķ������η���
     */
    int getModifiers();

    /**
     * ȡ���ֶ����͡�
     */
    TypeInfo getType();

    /**
     * ȡ���ֶε����ơ�
     */
    String getName();

    /**
     * ��ָ���������з����ֶε�ʵ�����͡�
     * <p>
     * �൱��{@link resolve(context, true)}��
     * </p>
     */
    FieldInfo resolve(GenericDeclarationInfo context);

    /**
     * ��ָ���������з����ֶε�ʵ�����͡�
     * <p>
     * ���<code>includeBaseType==false</code>����ô�������ͱ���ʱ��������ȡ����baseType��
     * </p>
     * <p>
     * �μ���{@link TypeInfo.resolve()}��
     * </p>
     */
    FieldInfo resolve(GenericDeclarationInfo context, boolean includeBaseType);
}
