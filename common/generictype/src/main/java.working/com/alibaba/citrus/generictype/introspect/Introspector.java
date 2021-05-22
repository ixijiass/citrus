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
package com.alibaba.citrus.generictype.introspect;

import static com.alibaba.citrus.util.Assert.*;

import java.util.List;
import java.util.Map;

import com.alibaba.citrus.generictype.TypeInfo;

/**
 * ��������һ�����͵ķ�����Ϣ�Ĺ��ߡ�
 * 
 * @author Michael Zhou
 */
public abstract class Introspector {
    private final static Factory factory = newFactory();

    /**
     * ȡ�����Ͷ�Ӧ��{@link Introspector}��
     */
    public static Introspector getInstance(TypeInfo type) {
        return factory.getInstance(type);
    }

    /**
     * ȡ�����е�properties��Ϣ��
     */
    public abstract Map<String, List<PropertyInfo>> getProperties();

    /**
     * ����ָ�����ơ����͡���д���Ե�simple property��
     * <p>
     * ���δָ�����ͣ���ʾ�������͡������д����Ϊfalse����ʾ�����д���ԡ�
     * </p>
     * <p>
     * ����Ҳ������򷵻�<code>null</code>��
     * </p>
     */
    public final SimplePropertyInfo findSimpleProperty(String propertyName, Class<?> type, boolean readable,
                                                       boolean writable) {
        return findProperty(SimplePropertyInfo.class, propertyName, type, readable, writable);
    }

    /**
     * ����ָ�����ơ����͡���д���Ե�indexed property��
     * <p>
     * ���δָ�����ͣ���ʾ�������͡������д����Ϊfalse����ʾ�����д���ԡ�
     * </p>
     * <p>
     * ����Ҳ������򷵻�<code>null</code>��
     * </p>
     */
    public final IndexedPropertyInfo findIndexedProperty(String propertyName, Class<?> type, boolean readable,
                                                         boolean writable) {
        return findProperty(IndexedPropertyInfo.class, propertyName, type, readable, writable);
    }

    /**
     * ����ָ�����ơ����͡���д���Ե�mapped property��
     * <p>
     * ���δָ�����ͣ���ʾ�������͡������д����Ϊfalse����ʾ�����д���ԡ�
     * </p>
     * <p>
     * ����Ҳ������򷵻�<code>null</code>��
     * </p>
     */
    public final MappedPropertyInfo findMappedProperty(String propertyName, Class<?> type, boolean readable,
                                                       boolean writable) {
        return findProperty(MappedPropertyInfo.class, propertyName, type, readable, writable);
    }

    /**
     * ����ָ�����ơ����͡���д���Ե�property��
     * <p>
     * ���δָ�����ͣ���ʾ�������͡������д����Ϊfalse����ʾ�����д���ԡ�
     * </p>
     * <p>
     * ����Ҳ������򷵻�<code>null</code>��
     * </p>
     */
    private <T extends PropertyInfo> T findProperty(Class<T> propertyType, String propertyName, Class<?> type,
                                                    boolean readable, boolean writable) {
        assertNotNull(propertyType, "propertyType");

        List<PropertyInfo> props = getProperties().get(propertyName);

        if (props != null) {
            for (PropertyInfo prop : props) {
                if (!propertyType.isInstance(prop)) {
                    continue;
                }

                if (type != null && !type.isAssignableFrom(prop.getType().getRawType())) {
                    continue;
                }

                if (readable && !prop.isReadable()) {
                    continue;
                }

                if (writable && !prop.isWritable()) {
                    continue;
                }

                return propertyType.cast(prop);
            }
        }

        return null;
    }

    /**
     * ���ڴ���{@link Introspector}��
     */
    protected static interface Factory {
        Introspector getInstance(TypeInfo type);
    }

    /**
     * ����factory����������compileʱ������impl package��
     */
    private static Factory newFactory() {
        String factoryImplName = Factory.class.getPackage().getName() + ".impl.IntrospectorImpl$FactoryImpl";
        Factory factoryImpl = null;

        try {
            factoryImpl = (Factory) Factory.class.getClassLoader().loadClass(factoryImplName).newInstance();
        } catch (Exception e) {
            unexpectedException(e, "Failed to create Introspector.Factory");
        }

        return factoryImpl;
    }
}
