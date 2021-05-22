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

/**
 * ����һ������{@link Class}�����͡������������ࣺ
 * <ul>
 * <li>{@link RawTypeInfo}</li>
 * <li>{@link ParameterizedTypeInfo}</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public interface ClassTypeInfo extends TypeInfo, GenericDeclarationInfo {
    /**
     * ��ָ���������з���ʵ�����͡�
     * <p>
     * ע��{@link ClassTypeInfo.resolve()}����{@link TypeInfo}������
     * {@link ClassTypeInfo}��
     * </p>
     */
    ClassTypeInfo resolve(GenericDeclarationInfo context);

    /**
     * ��ָ���������з���ʵ�����͡�
     * <p>
     * ע��{@link ClassTypeInfo.resolve()}����{@link TypeInfo}������
     * {@link ClassTypeInfo}��
     * </p>
     */
    ClassTypeInfo resolve(GenericDeclarationInfo context, boolean includeBaseType);

    /**
     * ȡ��ָ�����Ƶ��ֶΡ�
     */
    FieldInfo getField(String name);

    /**
     * ȡ��ָ�������е�ָ�����Ƶ��ֶΡ�
     * <p>
     * ָ�����ͱ���Ϊ��ǰ���ͻ��丸�ࡣ
     * </p>
     */
    FieldInfo getField(ClassTypeInfo declaringType, String name);

    /**
     * ȡ��ָ���������Ӧ�Ĺ��캯����
     */
    MethodInfo getConstructor(Class<?>... paramTypes);

    /**
     * ȡ��ָ�����ƺͲ������Ӧ�ķ�����
     */
    MethodInfo getMethod(String methodName, Class<?>... paramTypes);
}
