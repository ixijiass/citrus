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

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * ����<code>Map.entrySet()</code>, <code>Map.keySet()</code>,
 * <code>Map.values()</code> ���ص�collection view����.
 * 
 * @author Michael Zhou
 */
public abstract class AbstractMapViewTests extends AbstractTests {
    private Map<Object, Object> map1;
    private Map<Object, Object> map2;
    private Map<Object, Object> map3;
    private Collection<?> view1;
    private Collection<?> view2;
    private Collection<?> view3;

    @Before
    public void init() {
        // map1����һ�����.
        map1 = createMap();
        map1.put("aaa", "111");
        map1.put("bbb", "222");
        map1.put("ccc", "333");

        view1 = getView(map1);

        // map2����key��valueΪnull�����.
        map2 = createMap();
        map2.put(null, "111");
        map2.put("aaa", null);

        view2 = getView(map2);

        // map3Ϊ��.
        map3 = createMap();
        view3 = getView(map3);
    }

    private Collection<?> newCollection() {
        return createHashSet();
    }

    private Object newItem(Object key, Object value) {
        return createItem(key, value);
    }

    @Test
    public void add() {
        assertAdd(view1);
        assertAdd(view2);
        assertAdd(view3);
    }

    @SuppressWarnings("unchecked")
    private void assertAdd(Collection<?> collection) {
        try {
            ((Collection<Object>) collection).add(newItem("key", "value"));
            fail("should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void addAll() {
        assertAddAll(view1);
        assertAddAll(view2);
        assertAddAll(view3);
    }

    @SuppressWarnings("unchecked")
    private void assertAddAll(Collection<?> collection) {
        Collection<Object> items = (Collection<Object>) newCollection();

        ((Collection<Object>) collection).addAll(items); // �����collection

        try {
            items.add(newItem("key", "value"));
            ((Collection<Object>) collection).addAll(items); // ����ǿ�collection
            fail("should throw an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void clear() {
        view1.clear();
        assertEquals(0, map1.size());
        assertTrue(map1.isEmpty());
        assertTrue(view1.isEmpty());

        view2.clear();
        assertEquals(0, map2.size());
        assertTrue(map2.isEmpty());
        assertTrue(view2.isEmpty());

        view3.clear();
        assertEquals(0, map3.size());
        assertTrue(map3.isEmpty());
        assertTrue(view3.isEmpty());
    }

    @Test
    public void contains() {
        assertTrue(view1.contains(newItem("aaa", "111")));
        assertTrue(view1.contains(newItem("bbb", "222")));
        assertTrue(view1.contains(newItem("ccc", "333")));

        assertTrue(view2.contains(newItem("aaa", null)));
        assertTrue(view2.contains(newItem(null, "111")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void containsAll() {
        Collection<Object> items;

        items = (Collection<Object>) newCollection();
        items.add(newItem("aaa", "111"));
        items.add(newItem("bbb", "222"));
        items.add(newItem("ccc", "333"));
        assertTrue(view1.containsAll(items));

        items = (Collection<Object>) newCollection();
        items.add(newItem("aaa", null));
        items.add(newItem(null, "111"));
        assertTrue(view2.containsAll(items));

        items = (Collection<Object>) newCollection();
        assertTrue(view1.containsAll(items));
        assertTrue(view2.containsAll(items));
        assertTrue(view3.containsAll(items));
    }

    /**
     * ����Object.equals()����. ���������е�collection���󶼶����˴˷���.
     * �����ǰ���Ե�collection����֧�ֱȽϲ���, �� createCollectionToCompareWith()Ӧ�÷���null.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void equals_() {
        Collection<Object> items;

        // ����������÷���һ��collection����, ����ֱ�Ӻ͵�ǰ��collection��Ƚ�.
        // �������null, ��ʾ��ǰcollection��֧�ֱȽϲ���, ���������������.
        if (createCollectionToCompareWith() == null) {
            return;
        }

        items = (Collection<Object>) createCollectionToCompareWith();
        items.add(newItem("aaa", "111"));
        items.add(newItem("bbb", "222"));
        items.add(newItem("ccc", "333"));
        assertTrue(view1.equals(items));

        items = (Collection<Object>) createCollectionToCompareWith();
        items.add(newItem("aaa", null));
        items.add(newItem(null, "111"));
        assertTrue(view2.equals(items));

        items = (Collection<Object>) createCollectionToCompareWith();
        assertFalse(view1.equals(items));
        assertFalse(view2.equals(items));
        assertTrue(view3.equals(items));
    }

    /**
     * ����Object.hashCode()����. �÷������Ǻ�Object.equals()��ص�(��ȵĶ����� ��ͬ��hashCode),
     * ���,����equals_(), hashCode_ͨ���ж�
     * createCollectionToCompareWith()�Ƿ񷵻�null�������Ƿ� ���������.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void hashCode_() {
        Collection<Object> items;

        // ����������÷���һ��collection����, ����ֱ�Ӻ͵�ǰ��collection��Ƚ�.
        // �������null, ��ʾ��ǰcollection��֧�ֱȽϲ���, ���Բ����Դ���.
        if (createCollectionToCompareWith() == null) {
            return;
        }

        items = (Collection<Object>) createCollectionToCompareWith();
        items.add(newItem("aaa", "111"));
        items.add(newItem("bbb", "222"));
        items.add(newItem("ccc", "333"));
        assertEquals(items.hashCode(), view1.hashCode());

        items = (Collection<Object>) createCollectionToCompareWith();
        items.add(newItem("aaa", null));
        items.add(newItem(null, "111"));
        assertEquals(items.hashCode(), view2.hashCode());

        items = (Collection<Object>) createCollectionToCompareWith();
        assertFalse(items.hashCode() == view1.hashCode());
        assertFalse(items.hashCode() == view2.hashCode());
        assertEquals(items.hashCode(), view3.hashCode());
    }

    @Test
    public void isEmpty() {
        assertFalse(view1.isEmpty());
        assertFalse(view2.isEmpty());
        assertTrue(view3.isEmpty());
    }

    @Test
    public void iterator() {
        int count;

        count = 0;

        for (Iterator<?> i = view1.iterator(); i.hasNext(); count++) {
            Object entry = i.next();

            assertTrue(isEqual(newItem("aaa", "111"), entry) || isEqual(newItem("bbb", "222"), entry)
                    || isEqual(newItem("ccc", "333"), entry));
        }

        assertEquals(3, count);

        count = 0;

        for (Iterator<?> i = view2.iterator(); i.hasNext(); count++) {
            Object entry = i.next();

            assertTrue(isEqual(newItem("aaa", null), entry) || isEqual(newItem(null, "111"), entry));
        }

        assertEquals(2, count);

        for (Object name : view3) {
            fail("should not go here: " + name);
        }
    }

    @Test
    public void iteratorRemove() {
        int count = view1.size() - 1;

        for (Iterator<?> i = view1.iterator(); i.hasNext(); count--) {
            i.next();
            i.remove();
            assertEquals(count, view1.size());
        }
    }

    @Test
    public void remove() {
        view1.remove(newItem("aaa", "111"));
        assertFalse(map1.containsKey("aaa"));
        assertFalse(view1.contains(newItem("aaa", "111")));
        assertEquals(2, map1.size());
        assertEquals(2, view1.size());

        view2.remove(newItem("aaa", null));
        assertFalse(map2.containsKey("aaa"));
        assertFalse(view2.contains(newItem("aaa", null)));
        view2.remove(newItem(null, "111"));
        assertFalse(map2.containsKey(null));
        assertFalse(view2.contains(newItem(null, "111")));
        assertEquals(0, map2.size());
        assertEquals(0, view2.size());

        view3.remove(newItem("not exists", null));
        assertFalse(map3.containsKey("not exists"));
        assertFalse(view3.contains(newItem("not exists", null)));
        assertEquals(0, map3.size());
        assertEquals(0, view3.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void removeAll() {
        Collection<Object> items;

        items = (Collection<Object>) newCollection();
        items.add(newItem("aaa", "111"));
        items.add(newItem("bbb", "222"));
        assertTrue(view1.removeAll(items));
        assertTrue(view1.contains(newItem("ccc", "333")));
        assertEquals(1, view1.size());

        items = (Collection<Object>) newCollection();
        items.add(newItem("aaa", null));
        items.add(newItem(null, "111"));
        assertTrue(view2.removeAll(items));
        assertEquals(0, view2.size());

        items = (Collection<Object>) newCollection();
        assertFalse(view3.removeAll(items));
        assertEquals(0, view3.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void retainAll() {
        Collection<Object> items;

        items = (Collection<Object>) newCollection();
        items.add(newItem("aaa", "111"));
        items.add(newItem("bbb", "222"));
        assertTrue(view1.retainAll(items));
        assertTrue(view1.contains(newItem("aaa", "111")));
        assertTrue(view1.contains(newItem("bbb", "222")));
        assertFalse(view1.contains(newItem("ccc", "333")));
        assertEquals(2, view1.size());

        items = (Collection<Object>) newCollection();
        items.add(newItem("aaa", null));
        assertTrue(view2.retainAll(items));
        assertTrue(view2.contains(newItem("aaa", null)));
        assertFalse(view2.contains(newItem(null, "111")));
        assertEquals(1, view2.size());

        items = (Collection<Object>) newCollection();
        assertFalse(view3.retainAll(items));
        assertEquals(0, view3.size());
    }

    @Test
    public void size() {
        assertEquals(3, view1.size());
        assertEquals(2, view2.size());
        assertEquals(0, view3.size());
    }

    @Test
    public void toArray() {
        Object[] array;

        // ����������toArray().
        array = view1.toArray();
        view1.removeAll(Arrays.asList(array));
        assertEquals(0, view1.size());

        // ��������toArray(Object[]).
        array = new Object[2];
        view2.toArray(array);
        view2.removeAll(Arrays.asList(array));
        assertEquals(0, view2.size());
    }

    @Test
    public void failFast() {
        Iterator<?> i;

        // �޸�map�Ժ�, ��ͼi.next()�����쳣.
        i = view1.iterator();
        map1.put("aaa+aaa", "111+111");

        try {
            i.next();
            fail("should throw a ConcurrentModificationException");
        } catch (ConcurrentModificationException e) {
        }

        // �޸�map�Ժ�, ��ͼi.remove()�����쳣.
        i = view2.iterator();
        i.next();
        map2.put("aaa+aaa", "111+111");

        try {
            i.remove();
            fail("should throw a ConcurrentModificationException");
        } catch (ConcurrentModificationException e) {
        }
    }

    protected abstract Map<Object, Object> createMap();

    /**
     * ��<code>Map</code>��ȡ��view. ������<code>Map.entrySet()</code>,
     * <code>Map.keySet()</code> ��<code>Map.values()</code>�ȷ������صĽ��.
     * 
     * @param map �����Ե�view������<code>Map</code>
     * @return view
     */
    protected abstract Collection<?> getView(Map<Object, Object> map);

    /**
     * ����һ�����Ժ͵�ǰ���Ե�view��ȽϵĶ���. ����<code>Map.values()</code>���ص�
     * <code>Collection</code>����, û�ж���<code>equals</code> ��
     * <code>hashCode</code>����, ���Բ��ɱȽ�. �����������<code>null</code>����.
     * 
     * @return <code>Collection</code>����
     */
    protected abstract Collection<?> createCollectionToCompareWith();

    /**
     * ����һ����View�д�ŵĶ���ɱȽϵĶ���.
     * 
     * @param key key
     * @param value value
     * @return ����
     */
    protected abstract Object createItem(Object key, Object value);
}
