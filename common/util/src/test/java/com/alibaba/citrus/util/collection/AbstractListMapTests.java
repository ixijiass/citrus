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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * <code>ListMapTest</code>����<code>ListMap</code>�Ļ�����Ϊ.
 * 
 * @author Michael Zhou
 */
public abstract class AbstractListMapTests extends AbstractTests {
    private ListMap<Object, Object> map1;
    private ListMap<Object, Object> map2;
    private ListMap<Object, Object> map3;

    @Before
    public void init() {
        // map1���������������.
        map1 = createListMap();

        String key = "";

        for (int i = 0; i < 100; i++) {
            key += "a";
            map1.put(key, new Integer(i));
        }

        // map2����key��valueΪnull�����.
        map2 = createListMap();
        map2.put(null, "111");
        map2.put("aaa", null);

        // map3Ϊ��.
        map3 = createListMap();
    }

    @Test
    public void get() {
        for (int i = 0; i < 100; i++) {
            Integer value = new Integer(i);

            assertEquals(value, map1.get(i));
        }

        try {
            map1.get(100);
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // û�е�101����
        }

        assertEquals("111", map2.get(0));
        assertEquals(null, map2.get(1)); // �ڶ����ֵΪnull

        try {
            map2.get(2);
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // û�е�3����
        }

        try {
            map3.get(0);
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // ��map
        }
    }

    @Test
    public void getKey() {
        String key = "";

        for (int i = 0; i < 100; i++) {
            key += "a";
            assertEquals(key, map1.getKey(i));
        }

        try {
            map1.getKey(100);
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // û�е�101����
        }

        assertEquals(null, map2.getKey(0)); // ��һ���keyΪnull
        assertEquals("aaa", map2.getKey(1));

        try {
            map2.getKey(2);
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // û�е�3����
        }
    }

    @Test
    public void removeEntry() {
        // ��ǰ�濪ʼɾ50��.
        String key = "";

        for (int i = 0; i < 50; i++) {
            key += "a";
            assertEquals(new DefaultMapEntry<Object, Object>(key, new Integer(i)), map1.removeEntry(0));
            assertEquals(99 - i, map1.size());

            String key2 = key;

            for (int j = i + 1; j < 100; j++) {
                key2 += "a";
                assertEquals(new Integer(j), map1.get(j - i - 1));
                assertEquals(key2, map1.getKey(j - i - 1));
            }
        }

        // �Ӻ��濪ʼɾ50��.
        key = "";

        for (int i = 0; i <= 100; i++) {
            key += "a";
        }

        for (int i = 99; i >= 50; i--) {
            key = key.substring(0, key.length() - 1);
            assertEquals(new DefaultMapEntry<Object, Object>(key, new Integer(i)), map1.removeEntry(i - 50));
            assertEquals(i - 50, map1.size());

            String key2 = key;

            for (int j = i - 51; j >= 0; j--) {
                key2 = key2.substring(0, key2.length() - 1);
                assertEquals(new Integer(j + 50), map1.get(j));
                assertEquals(key2, map1.getKey(j));
            }
        }

        // map2
        assertEquals(new DefaultMapEntry<Object, Object>(null, "111"), map2.removeEntry(0));
        assertEquals(new DefaultMapEntry<Object, Object>("aaa", null), map2.removeEntry(0));

        try {
            map2.removeEntry(0);
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // ��map
        }

        // map3
        try {
            map3.removeEntry(0);
            fail("Should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // ��map
        }
    }

    @Test
    public void clone_() {
        ListMap<Object, Object> copy;

        copy = cloneListMap(map1);
        assertNotSame(map1, copy);
        assertEquals(100, copy.size());

        String key = "";

        for (int i = 0; i < 100; i++) {
            key += "a";
            assertEquals(key, copy.getKey(i));
            assertEquals(new Integer(i), copy.get(i));
        }

        copy = cloneListMap(map2);
        assertNotSame(map2, copy);
        assertEquals(2, copy.size());
        assertNull(copy.getKey(0));
        assertEquals("111", copy.get(0));
        assertEquals("aaa", copy.getKey(1));
        assertNull(copy.get(1));
    }

    @Test
    public void serialize() throws Exception {
        ListMap<Object, Object> copy;

        copy = cloneBySerialization(map1);
        assertEquals(map1.getClass(), copy.getClass());
        assertEquals(map1.hashCode(), copy.hashCode());
        assertEquals(map1, copy);
        assertEquals(100, copy.size());

        String key = "";

        for (int i = 0; i < 100; i++) {
            key += "a";
            assertEquals(key, copy.getKey(i));
            assertEquals(new Integer(i), copy.get(i));
        }

        copy = cloneBySerialization(map2);
        assertEquals(map2.getClass(), copy.getClass());
        assertEquals(map2.hashCode(), copy.hashCode());
        assertEquals(map2, copy);
        assertEquals(2, copy.size());
        assertNull(copy.getKey(0));
        assertEquals("111", copy.get(0));
        assertEquals("aaa", copy.getKey(1));
        assertNull(copy.get(1));
    }

    protected abstract ListMap<Object, Object> createListMap();

    /**
     * ����һ��<code>ListMap</code>.
     * 
     * @param map Ҫ���Ƶ�map
     * @return ����Ʒ
     */
    protected abstract ListMap<Object, Object> cloneListMap(ListMap<Object, Object> map);
}
