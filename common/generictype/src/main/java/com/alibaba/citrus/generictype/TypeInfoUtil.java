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

import static com.alibaba.citrus.generictype.TypeInfo.*;
import static com.alibaba.citrus.util.Assert.*;

import java.util.Iterator;
import java.util.Map;

/**
 * ����ʹ��{@link TypeInfo}�Ĺ��ߡ�
 * 
 * @author Michael Zhou
 */
public class TypeInfoUtil {
    public final static TypeInfo TYPE_VAR_MAP_KEY = factory.getType(Map.class.getTypeParameters()[0]);
    public final static TypeInfo TYPE_VAR_MAP_VALUE = factory.getType(Map.class.getTypeParameters()[1]);
    public final static TypeInfo TYPE_VAR_ITERABLE_ELEMENT = factory.getType(Iterable.class.getTypeParameters()[0]);
    public final static TypeInfo TYPE_VAR_ITERATOR_ELEMENT = factory.getType(Iterator.class.getTypeParameters()[0]);

    /**
     * ȡ��ָ�����͵�ʵ�ʲ�����
     * <p>
     * ���磺<code>class MyClass extends BaseClass&lt;Integer&gt;</code>����ô���ã�
     * <code>resolveParameter(MyClass.class, BaseClass.class, 0)</code>������
     * <code>Integer</code>��
     * </p>
     */
    public static TypeInfo resolveParameter(Class<?> contextClass, Class<?> parentClass, int index) {
        assertNotNull(contextClass, "contextClass");
        assertNotNull(parentClass, "parentClass");
        assertTrue(index < parentClass.getTypeParameters().length, "index out of bound");

        GenericDeclarationInfo context = factory.getGenericDeclaration(contextClass);
        TypeInfo param = factory.getType(parentClass.getTypeParameters()[index]);

        return param.resolve(context);
    }

    /**
     * ȡ��{@link Map}��key���͡�
     */
    public static TypeInfo resolveMapKey(Class<?> mapClass) {
        return resolveMapKey(factory.getClassType(mapClass));
    }

    /**
     * ȡ��{@link Map}��key���͡�
     */
    public static TypeInfo resolveMapKey(TypeInfo mapType) {
        assertTrue(
                mapType != null && mapType instanceof ClassTypeInfo && Map.class.isAssignableFrom(mapType.getRawType()),
                "mapType: %s", mapType);

        return TYPE_VAR_MAP_KEY.resolve((ClassTypeInfo) mapType);
    }

    /**
     * ȡ��{@link Map}��value���͡�
     */
    public static TypeInfo resolveMapValue(Class<?> mapClass) {
        return resolveMapValue(factory.getClassType(mapClass));
    }

    /**
     * ȡ��{@link Map}��value���͡�
     */
    public static TypeInfo resolveMapValue(TypeInfo mapType) {
        assertTrue(
                mapType != null && mapType instanceof ClassTypeInfo && Map.class.isAssignableFrom(mapType.getRawType()),
                "mapType: %s", mapType);

        return TYPE_VAR_MAP_VALUE.resolve((ClassTypeInfo) mapType);
    }

    /**
     * ȡ��{@link Iterable}��element���͡�
     */
    public static TypeInfo resolveIterableElement(Class<?> iterableClass) {
        return resolveIterableElement(factory.getClassType(iterableClass));
    }

    /**
     * ȡ��{@link Iterable}��element���͡�
     */
    public static TypeInfo resolveIterableElement(TypeInfo iterableType) {
        assertTrue(
                iterableType != null && iterableType instanceof ClassTypeInfo
                        && Iterable.class.isAssignableFrom(iterableType.getRawType()), "iterableType: %s", iterableType);

        return TYPE_VAR_ITERABLE_ELEMENT.resolve((ClassTypeInfo) iterableType);
    }

    /**
     * ȡ��{@link Iterator}��element���͡�
     */
    public static TypeInfo resolveIteratorElement(Class<?> iteratorClass) {
        return resolveIteratorElement(factory.getClassType(iteratorClass));
    }

    /**
     * ȡ��{@link Iterator}��element���͡�
     */
    public static TypeInfo resolveIteratorElement(TypeInfo iteratorType) {
        assertTrue(
                iteratorType != null && iteratorType instanceof ClassTypeInfo
                        && Iterator.class.isAssignableFrom(iteratorType.getRawType()), "iteratorType: %s", iteratorType);

        return TYPE_VAR_ITERATOR_ELEMENT.resolve((ClassTypeInfo) iteratorType);
    }
}
