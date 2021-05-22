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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import com.alibaba.citrus.codegen.util.MethodSignature;

/**
 * ����һ��{@link Method}��{@link Constructor}����Ϣ��
 * 
 * @author Michael Zhou
 */
public interface MethodInfo extends GenericDeclarationInfo {
    /**
     * �Ƿ�Ϊ���캯����
     */
    boolean isConstructor();

    /**
     * ȡ�÷�����������Ƿ������򷵻�<code>null</code>��
     */
    Method getMethod();

    /**
     * ȡ�ù��캯����������ǹ��캯�����򷵻�<code>null</code>��
     */
    Constructor<?> getConstructor();

    /**
     * ȡ�õ�ǰ�������ڵ����͡�
     */
    TypeInfo getDeclaringType();

    /**
     * ȡ�õ�ǰ�������캯����ǩ����
     * <p>
     * ǩ��ֻ�������������ƺͲ�����Ϣ��
     * </p>
     */
    MethodSignature getSignature();

    /**
     * ȡ�÷������캯���ķ������η���
     */
    int getModifiers();

    /**
     * ȡ�÷������͡�
     */
    TypeInfo getReturnType();

    /**
     * ȡ�÷��������ƣ����ڹ��캯�����򷵻�<code>&lt;init&gt;</code>��
     */
    String getName();

    /**
     * ȡ�ò������ͱ�
     */
    List<TypeInfo> getParameterTypes();

    /**
     * ȡ���쳣���ͱ�
     */
    List<TypeInfo> getExceptionTypes();

    /**
     * ȡ����Ч�쳣���ͱ����Ǵ�<code>RuntimeException</code>��<code>Error</code>�������쳣��<br>
     * ���޳����ص��쳣������<code>Exception</code>��<code>IOException</code>ͬʱ���֣���ɾ��
     * <code>IOException</code>��
     */
    List<TypeInfo> getEffectiveExceptionTypes();

    /**
     * ��ָ���������з��������ķ���ֵ���������͡��쳣���͵�ʵ�����͡�
     * <p>
     * �൱��{@link resolve(context, true)}��
     * </p>
     */
    MethodInfo resolve(GenericDeclarationInfo context);

    /**
     * ��ָ���������з��������ķ���ֵ���������͡��쳣���͵�ʵ�����͡�
     * <p>
     * ���<code>includeBaseType==false</code>����ô�������ͱ���ʱ��������ȡ����baseType�� ��
     * </p>
     * <p>
     * �μ���{@link TypeInfo.resolve()}��
     * </p>
     */
    MethodInfo resolve(GenericDeclarationInfo context, boolean includeBaseType);
}
