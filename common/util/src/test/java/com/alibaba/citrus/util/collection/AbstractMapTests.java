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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;

/**
 * <code>MapTest</code>����<code>java.util.Map</code>�Ļ�����Ϊ��
 * 
 * @author Michael Zhou
 */
public abstract class AbstractMapTests extends AbstractTests {
    private Map<Object, Object> map1;
    private Map<Object, Object> map2;
    private Map<Object, Object> map3;

    @Before
    public void init() {
        // map1����һ�����.
        map1 = createMap();
        map1.put("aaa", "111");
        map1.put("bbb", "222");
        map1.put("ccc", "333");

        // map2����key��valueΪnull�����.
        map2 = createMap();
        map2.put(null, "111");
        map2.put("aaa", null);

        // map3Ϊ��.
        map3 = createMap();
    }

    private Map<Object, Object> newMap() {
        return createHashMap();
    }

    @Test
    public void clear() {
        map1.clear();
        assertEquals(0, map1.size());
        assertTrue(map1.isEmpty());

        map2.clear();
        assertEquals(0, map2.size());
        assertTrue(map2.isEmpty());

        map3.clear();
        assertEquals(0, map3.size());
        assertTrue(map3.isEmpty());
    }

    @Test
    public void containsKey() {
        assertTrue(map1.containsKey("aaa"));
        assertTrue(map1.containsKey("bbb"));
        assertTrue(map1.containsKey("ccc"));

        assertTrue(map2.containsKey(null));
        assertTrue(map2.containsKey("aaa"));
    }

    @Test
    public void containsValue() {
        assertTrue(map1.containsValue("111"));
        assertTrue(map1.containsValue("222"));
        assertTrue(map1.containsValue("333"));

        assertTrue(map2.containsValue(null));
        assertTrue(map2.containsValue("111"));
    }

    @Test
    public void equals_() {
        Map<Object, Object> newMap;

        newMap = newMap();
        newMap.put("aaa", "111");
        newMap.put("bbb", "222");
        newMap.put("ccc", "333");
        assertEquals(newMap, map1);

        newMap = newMap();
        newMap.put(null, "111");
        newMap.put("aaa", null);
        assertEquals(newMap, map2);

        newMap = newMap();
        assertEquals(newMap, map3);

        assertFalse(map1.equals(map2));
        assertFalse(map1.equals(map3));
        assertFalse(map2.equals(map3));
    }

    @Test
    public void get() {
        assertEquals("111", map1.get("aaa"));
        assertEquals("222", map1.get("bbb"));
        assertEquals("333", map1.get("ccc"));

        assertEquals("111", map2.get(null));
        assertEquals(null, map2.get("aaa"));
    }

    @Test
    public void hashCode_() {
        Map<Object, Object> newMap;

        newMap = newMap();
        newMap.put("aaa", "111");
        newMap.put("bbb", "222");
        newMap.put("ccc", "333");
        assertEquals(newMap.hashCode(), map1.hashCode());

        newMap = newMap();
        newMap.put(null, "111");
        newMap.put("aaa", null);
        assertEquals(newMap.hashCode(), map2.hashCode());

        newMap = newMap();
        assertEquals(newMap.hashCode(), map3.hashCode());

        assertFalse(map1.hashCode() == map2.hashCode());
        assertFalse(map1.hashCode() == map3.hashCode());
        assertFalse(map2.hashCode() == map3.hashCode());
    }

    @Test
    public void isEmpty() {
        assertFalse(map1.isEmpty());
        assertFalse(map2.isEmpty());
        assertTrue(map3.isEmpty());
    }

    @Test
    public void put() {
        assertEquals("111", map1.put("aaa", "111+111")); // �滻aaa

        assertEquals(null, map1.put("ddd", "222+222")); // ����ddd

        assertEquals("111+111", map1.get("aaa"));
        assertEquals("222+222", map1.get("ddd"));
        assertEquals(4, map1.size());

        assertEquals("111", map2.put(null, "111+111")); // �滻null

        assertEquals(null, map2.put("aaa", "222+222")); // �滻aaa

        assertEquals(null, map2.put("ccc", "333+333")); // ����ccc

        assertEquals("111+111", map2.get(null));
        assertEquals("222+222", map2.get("aaa"));
        assertEquals("333+333", map2.get("ccc"));
        assertEquals(3, map2.size());

        assertEquals(null, map3.put("aaa", "111+111")); // ����aaa

        assertEquals("111+111", map3.get("aaa"));
        assertEquals(1, map3.size());
    }

    @Test
    public void putAll() {
        Map<Object, Object> newMap;

        // �����map.
        newMap = newMap();
        map1.putAll(newMap);
        map2.putAll(newMap);
        map3.putAll(newMap);
        assertEquals(3, map1.size());
        assertEquals(2, map2.size());
        assertEquals(0, map3.size());
        assertEquals("111", map1.get("aaa"));
        assertEquals("222", map1.get("bbb"));
        assertEquals("333", map1.get("ccc"));
        assertEquals(null, map2.get("aaa"));
        assertEquals("111", map2.get(null));

        // ����ǿյ�map.
        newMap = newMap();
        newMap.put("aaa", "111+111");
        newMap.put("ddd", "444+444");
        newMap.put(null, "111+111");
        newMap.put("eee", null);
        map1.putAll(newMap);
        map2.putAll(newMap);
        map3.putAll(newMap);
        assertEquals(6, map1.size());
        assertEquals(4, map2.size());
        assertEquals(4, map3.size());
        assertEquals("111+111", map1.get("aaa")); // ���滻
        assertEquals("222", map1.get("bbb")); // ����
        assertEquals("333", map1.get("ccc")); // ����
        assertEquals("444+444", map1.get("ddd")); // ����ddd
        assertEquals(null, map1.get("eee")); // ����eee
        assertEquals("111+111", map1.get(null)); // ����null
        assertEquals("111+111", map2.get("aaa")); // ���滻
        assertEquals("111+111", map2.get(null)); // ���滻
        assertEquals("444+444", map2.get("ddd")); // ����ddd
        assertEquals(null, map2.get("eee")); // ����eee
        assertEquals("111+111", map3.get("aaa")); // ����aaa
        assertEquals("111+111", map3.get(null)); // ����null
        assertEquals("444+444", map3.get("ddd")); // ����ddd
        assertEquals(null, map3.get("eee")); // ����eee
    }

    @Test
    public void remove() {
        map1.remove("aaa");
        assertFalse(map1.containsKey("aaa"));
        assertEquals(2, map1.size());

        map2.remove("aaa");
        assertFalse(map2.containsKey("aaa"));
        map2.remove(null);
        assertFalse(map2.containsKey(null));
        assertEquals(0, map2.size());

        map3.remove("not exists");
        assertFalse(map3.containsKey("not exists"));
        assertEquals(0, map3.size());
    }

    @Test
    public void size() {
        assertEquals(3, map1.size());
        assertEquals(2, map2.size());
        assertEquals(0, map3.size());
    }

    @Test
    public void clone_() {
        Map<Object, Object> copy;

        try {
            copy = cloneMap(map1);
            assertNotSame(map1, copy); // ��ͬ
            assertEquals(map1, copy); // �����
            assertEquals(map1.hashCode(), copy.hashCode());

            assertEquals(3, copy.size());
            assertEquals("111", copy.get("aaa"));
            assertEquals("222", copy.get("bbb"));
            assertEquals("333", copy.get("ccc"));

            copy = cloneMap(map2);
            assertNotSame(map2, copy); // ��ͬ
            assertEquals(map2, copy); // �����
            assertEquals(map2.hashCode(), copy.hashCode());

            assertEquals(2, copy.size());
            assertEquals("111", copy.get(null));
            assertEquals(null, copy.get("aaa"));
            assertTrue(copy.containsKey("aaa"));

            copy = cloneMap(map3);
            assertNotSame(map3, copy); // ��ͬ
            assertEquals(map3, copy); // �����
            assertEquals(map3.hashCode(), copy.hashCode());

            assertEquals(0, copy.size());
        } catch (UnsupportedOperationException e) {
            // �����Ե�Map��֧��clone
        }
    }

    @Test
    public void toString_() {
        List<String> list;

        assertNotNull(list = parseToString(map1));
        assertEquals("aaa=111", list.get(0));
        assertEquals("bbb=222", list.get(1));
        assertEquals("ccc=333", list.get(2));

        assertNotNull(list = parseToString(map2));
        assertEquals("aaa=null", list.get(0));
        assertEquals("null=111", list.get(1));

        assertNotNull(list = parseToString(map3));
        assertEquals(0, list.size());
    }

    /**
     * ��map.toString()�Ľ��������������.
     * 
     * @param map Ҫ�����map
     * @return ���������Ľ��
     */
    private List<String> parseToString(Map<Object, Object> map) {
        List<String> list = createArrayList();
        String str = map.toString();

        try {
            str = str.substring(1, str.length() - 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(str, ", ");

        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }

        Collections.sort(list);

        return list;
    }

    @Test
    public void serialize() throws Exception {
        Map<Object, Object> copy;

        try {
            copy = (Map<Object, Object>) cloneBySerialization(map1);
            assertNotSame(map1, copy); // ��ͬ
            assertEquals(map1, copy); // �����
            assertEquals(map1.hashCode(), copy.hashCode());

            assertEquals(3, copy.size());
            assertEquals("111", copy.get("aaa"));
            assertEquals("222", copy.get("bbb"));
            assertEquals("333", copy.get("ccc"));

            copy = (Map<Object, Object>) cloneBySerialization(map2);
            assertNotSame(map2, copy); // ��ͬ
            assertEquals(map2, copy); // �����
            assertEquals(map2.hashCode(), copy.hashCode());

            assertEquals(2, copy.size());
            assertEquals("111", copy.get(null));
            assertEquals(null, copy.get("aaa"));
            assertTrue(copy.containsKey("aaa"));

            copy = (Map<Object, Object>) cloneBySerialization(map3);
            assertNotSame(map3, copy); // ��ͬ
            assertEquals(map3, copy); // �����
            assertEquals(map3.hashCode(), copy.hashCode());

            assertEquals(0, copy.size());
        } catch (UnsupportedOperationException e) {
            // �����Ե�Map��֧��serialize
        }
    }

    /**
     * ���Ե�hash���е�����������ֵʱ�ı���.
     */
    @Test
    public void resize() {
        int capacity = 0;
        int threshold = 0;

        // ȡ�ó�ʼ��������ֵ, �������exception, �򲻲��Դ���.
        try {
            capacity = getCapacity(map3);
            threshold = getThreshold(map3);
        } catch (UnsupportedOperationException e) {
            return;
        }

        // Ԥ����������.
        int max = threshold * 4 + 1;

        /** �����㹻����hash����, ȷ������������ֵ. */
        String key = "";

        for (int i = 0; i < max; i++) {
            key += "a";
            map3.put(key, new Integer(i));

            if (map3.size() > threshold) { // ����!
                threshold *= 2;
                capacity *= 2;
            }

            assertEquals(capacity, getCapacity(map3));
            assertEquals(threshold, getThreshold(map3));
        }

        /** ����������, �����������ȷ��. */
        List<String> list;

        assertNotNull(list = parseToString(map3));
        assertEquals(max, list.size());
        key = "";

        for (int i = 0; i < max; i++) {
            key += "a";
            assertEquals(key + "=" + i, list.get(i));
        }
    }

    protected abstract Map<Object, Object> createMap();

    protected abstract Map<Object, Object> cloneMap(Map<Object, Object> map);

    protected abstract int getThreshold(Map<Object, Object> map);

    protected abstract int getCapacity(Map<Object, Object> map);
}
