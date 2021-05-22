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
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ��{@link GenericDeclaration}��Ӧ������������ͱ����������������������ࣺ
 * <ul>
 * <li>{@link ClassTypeInfo}������{@link Class}</li>
 * <li>{@link MethodInfo}������{@link Method}��{@link Constructor}</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public interface GenericDeclarationInfo {
    /**
     * �жϵ�ǰ�����Ƿ�Ϊgeneric�������༴���Ƿ�������ͱ����磺<code>List&lt;E&gt;</code>��
     */
    boolean isGeneric();

    /**
     * ȡ�����Ͳ�����
     * <p>
     * ���磬<code>Map&lt;K, V&gt;</code>�Ĳ�����Ϊ<code>[K, V]</code>��<br>
     * �������generic���ͣ��򷵻ؿ��б�
     * </p>
     */
    List<TypeVariableInfo> getTypeParameters();

    /**
     * ȡ�����Ͳ�������Ӧ��ʵ�����͡�
     * <p>
     * ���磬{@link RawTypeInfo}��<code>MyClass&lt;A extends Number, B&gt;</code>
     * ��Ӧ��ʵ�ʲ�������Ϊ <code>[Number, Object]</code>��<br>
     * {@link ParameterizedTypeInfo}��<code>List&lt;E=Integer&gt;</code>
     * ��Ӧ��ʵ�ʲ�������Ϊ<code>[Integer]</code>��
     * </p>
     * <p>
     * �������generic���ͣ��򷵻ؿ��б�
     * </p>
     */
    List<TypeInfo> getActualTypeArguments();

    /**
     * ȡ��ָ���������Ƶ�ʵ�����͡�
     * <p>
     * ���磬{@link RawTypeInfo}��<code>MyClass&lt;A extends Number, B&gt;</code>
     * ������<code>A</code>��Ӧ��ʵ�ʲ�������Ϊ <code>Number</code>��<br>
     * {@link ParameterizedTypeInfo}��<code>List&lt;E=Integer&gt;</code> ����
     * <code>E</code>��Ӧ��ʵ�ʲ�������Ϊ<code>Integer</code>��
     * </p>
     * <p>
     * ��������������ڣ��򷵻�<code>null</code>��
     * </p>
     */
    TypeInfo getActualTypeArgument(String name);
}
