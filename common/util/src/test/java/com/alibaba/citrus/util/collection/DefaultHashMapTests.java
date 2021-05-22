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
package com.alibaba.citrus.util.collection;

import static com.alibaba.citrus.util.CollectionUtil.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.alibaba.citrus.util.collection.DefaultHashMapTests.Constructors;
import com.alibaba.citrus.util.collection.DefaultHashMapTests.MapBasic;
import com.alibaba.citrus.util.collection.DefaultHashMapTests.MapEntrySet;
import com.alibaba.citrus.util.collection.DefaultHashMapTests.MapKeySet;
import com.alibaba.citrus.util.collection.DefaultHashMapTests.MapValues;

/**
 * ����<code>DefaultHashMap</code>��.
 * 
 * @author Michael Zhou
 */
@RunWith(Suite.class)
@SuiteClasses({ Constructors.class, MapBasic.class, MapEntrySet.class, MapKeySet.class, MapValues.class })
public class DefaultHashMapTests {
    private static int getThreshold(DefaultHashMap<Object, Object> map) {
        return map.getThreshold();
    }

    private static int getCapacity(DefaultHashMap<Object, Object> map) {
        return map.getCapacity();
    }

    public static class MapBasic extends AbstractMapTests {
        @Override
        protected Map<Object, Object> createMap() {
            return new DefaultHashMap<Object, Object>();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Map<Object, Object> cloneMap(Map<Object, Object> map) {
            return (Map<Object, Object>) ((DefaultHashMap<Object, Object>) map).clone();
        }

        @Override
        protected int getThreshold(Map<Object, Object> map) {
            return DefaultHashMapTests.getThreshold((DefaultHashMap<Object, Object>) map);
        }

        @Override
        protected int getCapacity(Map<Object, Object> map) {
            return DefaultHashMapTests.getCapacity((DefaultHashMap<Object, Object>) map);
        }
    }

    public static class MapEntrySet extends AbstractMapViewTests {
        @Override
        protected Map<Object, Object> createMap() {
            return new DefaultHashMap<Object, Object>();
        }

        @Override
        protected Collection<?> getView(Map<Object, Object> map) {
            return map.entrySet();
        }

        @Override
        protected Collection<?> createCollectionToCompareWith() {
            return createHashSet();
        }

        @Override
        protected Object createItem(Object key, Object value) {
            return new DefaultMapEntry<Object, Object>(key, value);
        }
    }

    public static class MapKeySet extends AbstractMapViewTests {
        @Override
        protected Map<Object, Object> createMap() {
            return new DefaultHashMap<Object, Object>();
        }

        @Override
        protected Collection<?> getView(Map<Object, Object> map) {
            return map.keySet();
        }

        @Override
        protected Collection<?> createCollectionToCompareWith() {
            return createHashSet();
        }

        @Override
        protected Object createItem(Object key, Object value) {
            return key;
        }
    }

    public static class MapValues extends AbstractMapViewTests {
        @Override
        protected Map<Object, Object> createMap() {
            return new DefaultHashMap<Object, Object>();
        }

        @Override
        protected Collection<?> getView(Map<Object, Object> map) {
            return map.values();
        }

        @Override
        protected Collection<?> createCollectionToCompareWith() {
            return null;
        }

        @Override
        protected Object createItem(Object key, Object value) {
            return value;
        }
    }

    public static class Constructors {
        private DefaultHashMap<Object, Object> map;

        /**
         * ���Թ��캯��(initialCapacity, loadFactor).
         */
        @Test
        public void constructor1() {
            assertConstructorException(-1, .75f);
            assertConstructorException(16, 0);
            assertConstructorException(16, -1);

            map = new DefaultHashMap<Object, Object>(0, .75f);
            assertParameters(0, 1);

            map = new DefaultHashMap<Object, Object>(16, 1);
            assertParameters(16, 16);

            map = new DefaultHashMap<Object, Object>(16, 1.000001f);
            assertParameters(16, 16);

            map = new DefaultHashMap<Object, Object>(16, 2);
            assertParameters(32, 16);
        }

        /**
         * ���Թ��캯��(initialCapacity).
         */
        @Test
        public void constructor2() {
            assertConstructorException(-1);

            map = new DefaultHashMap<Object, Object>(0);
            assertParameters(0, 1);
        }

        /**
         * ���Թ��캯��(�޲���).
         */
        @Test
        public void constructor3() {
            map = new DefaultHashMap<Object, Object>();
            assertParameters(12, 16);
        }

        /**
         * ���Թ��캯��(Map).
         */
        @Test
        public void constructor4() {
            Map<Object, Object> anotherMap = new java.util.HashMap<Object, Object>();

            anotherMap.put("aaa", "111");
            anotherMap.put("bbb", "222");
            anotherMap.put("ccc", "333");

            map = new DefaultHashMap<Object, Object>(anotherMap);
            assertParameters(12, 16);
            assertEquals(3, map.size());
            assertEquals("111", map.get("aaa"));
            assertEquals("222", map.get("bbb"));
            assertEquals("333", map.get("ccc"));
        }

        /**
         * �����ڲ�����: threshold��capacity.
         * 
         * @param expectedThreshold ��������ֵ
         * @param expectedCapacity ����������
         */
        private void assertParameters(int expectedThreshold, int expectedCapacity) {
            assertEquals(expectedThreshold, getThreshold(map));
            assertEquals(expectedCapacity, getCapacity(map));
        }

        /**
         * ���Բ���ͼ�õ�Exception.
         * 
         * @param initialCapacity ��ʼ����
         * @param loadFactor ����ϵ��
         */
        private void assertConstructorException(int initialCapacity, float loadFactor) {
            try {
                new DefaultHashMap<Object, Object>(initialCapacity, loadFactor);
                fail("should throw an IllegalArgumentException");
            } catch (IllegalArgumentException e) {
            }
        }

        /**
         * ���Բ���ͼ�õ�Exception.
         * 
         * @param initialCapacity ��ʼ����
         */
        private void assertConstructorException(int initialCapacity) {
            try {
                new DefaultHashMap<Object, Object>(initialCapacity);
                fail("should throw an IllegalArgumentException");
            } catch (IllegalArgumentException e) {
            }
        }
    }
}
