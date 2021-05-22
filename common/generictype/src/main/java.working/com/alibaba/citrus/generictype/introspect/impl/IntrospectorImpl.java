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
package com.alibaba.citrus.generictype.introspect.impl;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.citrus.generictype.TypeInfo;
import com.alibaba.citrus.generictype.introspect.Introspector;
import com.alibaba.citrus.generictype.introspect.PropertyInfo;

/**
 * ʵ��{@link Introspector}��
 * 
 * @author Michael Zhou
 */
public class IntrospectorImpl extends Introspector {
    private final TypeInfo type;
    private final Map<String, List<PropertyInfo>> props;

    /**
     * ����һ��{@link Introspector}ʵ����
     */
    IntrospectorImpl(TypeInfo type) {
        this.type = assertNotNull(type, "type");
        this.props = new TypeScanner().scan();
    }

    private TypeVisitor[] getVisitors() {
        return new TypeVisitor[] { new SimplePropertiesFinder(), new IndexedPropertiesFinder(),
                new MappedPropertiesFinder(), new ArrayPropertiesFinder(), new MapPropertiesFinder() };
    }

    @Override
    public Map<String, List<PropertyInfo>> getProperties() {
        return props;
    }

    /**
     * ɨ�貢�������͡�
     */
    private class TypeScanner {
        private final TypeVisitor[] visitors = getVisitors();
        private final Map<MethodSignature, Method> methods = createHashMap();

        /**
         * ����һ���࣬�������ͽӿڡ�
         */
        public Map<String, List<PropertyInfo>> scan() {
            boolean first = true;

            // ��ʼ
            for (TypeVisitor visitor : visitors) {
                visitor.visit();
            }

            // ����
            for (TypeInfo t : type.getSupertypes()) {
                scanType(t, true, first);
                first = false;
            }

            // ����
            for (TypeVisitor visitor : visitors) {
                visitor.visitEnd();
            }

            // �ռ����
            PropertiesMap props = new PropertiesMap();

            for (TypeVisitor visitor : visitors) {
                if (visitor instanceof PropertiesFinder) {
                    props.addAll(((PropertiesFinder) visitor).getProperties());
                }
            }

            return unmodifiableMap(props);
        }

        /**
         * ����һ���ࡣ
         */
        private void scanType(TypeInfo type, boolean scanFields, boolean scanConstructors) {
            for (TypeVisitor visitor : visitors) {
                visitor.visitType(type);
            }

            Class<?> clazz = type.getRawType();

            // ɨ��class�����е�ÿһ��field���������������ʲô��
            if (scanFields) {
                for (Field field : clazz.getDeclaredFields()) {
                    for (TypeVisitor visitor : visitors) {
                        visitor.visitField(field);
                    }
                }
            }

            // ֻɨ���һ��class�е�constructor���������������ʲô��
            if (scanConstructors) {
                for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                    for (TypeVisitor visitor : visitors) {
                        visitor.visitConstructor(constructor);
                    }
                }
            }

            // ɨ��class�����е�ÿһ��method���������������ʲô��
            // Ȼ��������public��protected method��ֻ��������Ǹ�ʵ�ֻᱻ���ʡ�
            for (Method method : clazz.getDeclaredMethods()) {
                MethodSignature sig = new MethodSignature(method);
                boolean accessible = (method.getModifiers() & (PUBLIC | PROTECTED)) != 0;

                if (!accessible || !methods.containsKey(sig)) {
                    if (accessible) {
                        methods.put(sig, method);
                    }

                    for (TypeVisitor visitor : visitors) {
                        visitor.visitMethod(method);
                    }
                }
            }
        }

    }

    /**
     * ����һ��Properties��ӳ���
     */
    private static class PropertiesMap extends HashMap<String, List<PropertyInfo>> {
        private static final long serialVersionUID = 3899442980552826145L;

        public void addAll(Map<String, List<PropertyInfo>> props) {
            if (props != null) {
                for (Map.Entry<String, List<PropertyInfo>> entry : props.entrySet()) {
                    String propName = assertNotNull(entry.getKey(), "property name is null: %s", entry);
                    List<PropertyInfo> propsWithSameName = super.get(propName);

                    if (propsWithSameName == null) {
                        propsWithSameName = createLinkedList();
                        super.put(propName, propsWithSameName);
                    }

                    propsWithSameName.addAll(entry.getValue());
                }
            }
        }

        @Override
        public String toString() {
            String[] names = keySet().toArray(new String[size()]);

            sort(names);

            StringBuilder buf = new StringBuilder();
            int i = 0;

            buf.append("Properties:\n");

            for (String name : names) {
                List<PropertyInfo> props = get(name);

                for (PropertyInfo prop : props) {
                    buf.append("\n--- ").append(++i).append(" ------------------------------\n");
                    buf.append(prop).append("\n");
                }
            }

            buf.append("==============================\n");

            return buf.toString();
        }
    }

    /**
     * ����{@link Introspector}�Ĺ�����
     */
    public static class FactoryImpl implements Factory {
        public Introspector getInstance(TypeInfo type) {
            return new IntrospectorImpl(type);
        }
    }
}
