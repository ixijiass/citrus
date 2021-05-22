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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * <p>
 * һ��Hash���ʵ��, ʵ����<code>ListMap</code>��<code>Map</code>�ӿ�.
 * </p>
 * <p>
 * ���hash���ʵ�־�����������:
 * </p>
 * <ul>
 * <li>���ڲ�������ķ�ʽ��������entry, ����˳�����</li>
 * <li>��<code>DefaultHashMap</code>һ��, û�н����κ�<code>synchronized</code>����</li>
 * </ul>
 * 
 * @author Michael Zhou
 * @see DefaultHashMap
 * @see ListMap
 */
public class ArrayHashMap<K, V> extends DefaultHashMap<K, V> implements ListMap<K, V> {
    private static final long serialVersionUID = 3258411729271927857L;

    // ==========================================================================
    // ��Ա���� 
    // ==========================================================================

    /** ��¼entry��˳�������. */
    protected transient DefaultHashMap.Entry<K, V>[] order;

    /** Key���б���ͼ. */
    private transient List<K> keyList;

    /** Value���б���ͼ. */
    private transient List<V> valueList;

    /** Entry���б���ͼ. */
    private transient List<Map.Entry<K, V>> entryList;

    // ==========================================================================
    // ���캯�� 
    // ==========================================================================

    /**
     * ����һ���յ�hash��. ʹ��ָ����Ĭ�ϵĳ�ʼ����(16)��Ĭ�ϵĸ���ϵ��(0.75).
     */
    public ArrayHashMap() {
        super();
    }

    /**
     * ����һ���յ�hash��. ʹ��ָ���ĳ�ʼ��ֵ��Ĭ�ϵĸ���ϵ��(0.75).
     * 
     * @param initialCapacity ��ʼ����.
     */
    public ArrayHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * ����һ���յ�hash��. ʹ��ָ���ĳ�ʼ�����͸���ϵ��.
     * 
     * @param initialCapacity ��ʼ����
     * @param loadFactor ����ϵ��.
     */
    public ArrayHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * ����ָ��<code>Map</code>������ͬ��<code>HashMap</code>. ʹ��Ĭ�ϵĸ���ϵ��(0.75).
     * 
     * @param map Ҫ���Ƶ�<code>Map</code>
     */
    public ArrayHashMap(Map<? extends K, ? extends V> map) {
        super(map);
    }

    // ==========================================================================
    // ʵ��Map��ListMap�ӿڵķ��� 
    // ==========================================================================

    /**
     * ���hash���а���һ������key��Ӧָ����value, �򷵻�true.
     * 
     * @param value ָ��value, ������Ĵ������.
     * @return ���hash���а���һ������key��Ӧָ����value, �򷵻�<code>true</code>.
     */
    @Override
    public boolean containsValue(Object value) {
        // ���Ǵ˷����ǳ������ܵĿ���.  ����������Ҹ���Ч.
        for (int i = 0; i < size; i++) {
            Entry entry = (Entry) order[i];

            if (eq(value, entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    /**
     * ���hash���е�����entry.
     */
    @Override
    public void clear() {
        super.clear();
        Arrays.fill(order, null);
    }

    /**
     * ����ָ��index����value. ���index������Χ, ������<code>IndexOutOfBoundsException</code>.
     * 
     * @param index Ҫ���ص�value������ֵ
     * @return ָ��index����value����
     */
    public V get(int index) {
        checkRange(index);
        return order[index].getValue();
    }

    /**
     * ����ָ��index����key. ���index������Χ, ������<code>IndexOutOfBoundsException</code>.
     * 
     * @param index Ҫ���ص�key������ֵ
     * @return ָ��index����key����
     */
    public K getKey(int index) {
        checkRange(index);
        return order[index].getKey();
    }

    /**
     * ɾ��ָ��index������. ���index������Χ, ������<code>IndexOutOfBoundsException</code>.
     * 
     * @param index Ҫɾ�����������ֵ
     * @return ��ɾ����<code>Map.Entry</code>��
     */
    public Map.Entry<K, V> removeEntry(int index) {
        checkRange(index);
        return removeEntryForKey(order[index].getKey());
    }

    /**
     * ��������key��<code>List</code>.
     * 
     * @return ����key��<code>List</code>.
     */
    public List<K> keyList() {
        return keyList != null ? keyList : (keyList = new KeyList());
    }

    /**
     * ��������value��<code>List</code>.
     * 
     * @return ����value��<code>List</code>.
     */
    public List<V> valueList() {
        return valueList != null ? valueList : (valueList = new ValueList());
    }

    /**
     * ��������entry��<code>List</code>.
     * 
     * @return ����entry��<code>List</code>.
     */
    public List<Map.Entry<K, V>> entryList() {
        return entryList != null ? entryList : (entryList = new EntryList());
    }

    // ==========================================================================
    // �ڲ��� 
    // ==========================================================================

    /**
     * <code>Map.Entry</code>��ʵ��.
     */
    protected class Entry extends DefaultHashMap.Entry<K, V> {
        /** Entry���б��е�����ֵ. */
        protected int index;

        /**
         * ����һ���µ�entry.
         * 
         * @param h key��hashֵ
         * @param k entry��key
         * @param v entry��value
         * @param n �����е���һ��entry
         */
        protected Entry(int h, K k, V v, DefaultHashMap.Entry<K, V> n) {
            super(h, k, v, n);
        }

        /**
         * ��entry����ɾ��ʱ, ���º�����entry������ֵ.
         */
        @Override
        protected void onRemove() {
            int numMoved = size - index;

            if (numMoved > 0) {
                System.arraycopy(order, index + 1, order, index, numMoved);
            }

            order[size] = null;

            for (int i = index; i < size; i++) {
                ((Entry) order[i]).index--;
            }
        }
    }

    /**
     * ������.
     */
    private abstract class ArrayHashIterator<E> implements ListIterator<E> {
        /** ������ص�entry. */
        private Entry lastReturned;

        /** ��ǰλ��. */
        private int cursor;

        /** ����iteratorʱ���޸ļ���. */
        private int expectedModCount;

        /**
         * ����һ��list iterator.
         * 
         * @param index ��ʼ��
         */
        protected ArrayHashIterator(int index) {
            if (index < 0 || index > size()) {
                throw new IndexOutOfBoundsException("Index: " + index);
            }

            cursor = index;
            expectedModCount = modCount;
        }

        /**
         * ��ָ��������뵽�б���. (��֧�ִ˲���)
         * 
         * @param o Ҫ����Ķ���
         */
        public void add(E o) {
            throw new UnsupportedOperationException();
        }

        /**
         * ��ָ�������滻���б���. (����<code>valueList</code>����, ��֧�ִ˲���)
         * 
         * @param o Ҫ�滻�Ķ���
         */
        public void set(E o) {
            throw new UnsupportedOperationException();
        }

        /**
         * ���ر��������Ƿ�����һ��entry.
         * 
         * @return ����������л�����һ��entry, ����<code>true</code>
         */
        public boolean hasNext() {
            return cursor < size;
        }

        /**
         * ���ر��������Ƿ���ǰһ��entry.
         * 
         * @return ����������л���ǰһ��entry, ����<code>true</code>
         */
        public boolean hasPrevious() {
            return cursor > 0;
        }

        /**
         * ȡ����һ��index. ��������һ��, �򷵻�<code>size</code>.
         * 
         * @return ��һ���index
         */
        public int nextIndex() {
            return cursor;
        }

        /**
         * ȡ��ǰһ��index. ����ǵ�һ��, �򷵻�<code>-1</code>.
         * 
         * @return ǰһ���index
         */
        public int previousIndex() {
            return cursor - 1;
        }

        /**
         * ɾ��һ����ǰentry. ִ��ǰ������ִ��<code>next()</code>��<code>previous()</code>����.
         */
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            checkForComodification();

            removeEntryForKey(lastReturned.getKey());

            if (lastReturned.index < cursor) {
                cursor--;
            }

            lastReturned = null;
            expectedModCount = modCount;
        }

        /**
         * ȡ����һ��entry.
         * 
         * @return ��һ��entry
         */
        protected Entry nextEntry() {
            checkForComodification();

            if (cursor >= size) {
                throw new NoSuchElementException();
            }

            lastReturned = (Entry) order[cursor++];

            return lastReturned;
        }

        /**
         * ȡ��ǰһ��entry.
         * 
         * @return ǰһ��entry
         */
        protected Entry previousEntry() {
            checkForComodification();

            if (cursor <= 0) {
                throw new NoSuchElementException();
            }

            lastReturned = (Entry) order[--cursor];

            return lastReturned;
        }

        /**
         * ���õ�ǰentry��ֵ.
         * 
         * @param o Ҫ���õ�ֵ
         */
        protected void setValue(V o) {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            checkForComodification();

            lastReturned.setValue(o);
        }

        /**
         * ����Ƿ�ͬʱ���޸�.
         */
        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * ȡ��hash���key�ı�����.
     */
    private class KeyIterator extends ArrayHashIterator<K> {
        /**
         * ����һ��list iterator.
         * 
         * @param index ��ʼ��
         */
        protected KeyIterator(int index) {
            super(index);
        }

        /**
         * ȡ����һ��key.
         * 
         * @return ��һ��key
         */
        public K next() {
            return nextEntry().getKey();
        }

        /**
         * ȡ��ǰһ��key.
         * 
         * @return ǰһ��key
         */
        public K previous() {
            return previousEntry().getKey();
        }
    }

    /**
     * ȡ��hash���value�ı�����.
     */
    private class ValueIterator extends ArrayHashIterator<V> {
        /**
         * ����һ��list iterator.
         * 
         * @param index ��ʼ��
         */
        protected ValueIterator(int index) {
            super(index);
        }

        /**
         * ��ָ�������滻���б���.
         * 
         * @param o Ҫ�滻�Ķ���(value)
         */
        @Override
        public void set(V o) {
            setValue(o);
        }

        /**
         * ȡ����һ��value.
         * 
         * @return ��һ��value
         */
        public V next() {
            return nextEntry().getValue();
        }

        /**
         * ȡ��ǰһ��value.
         * 
         * @return ǰһ��value
         */
        public V previous() {
            return previousEntry().getValue();
        }
    }

    /**
     * ȡ��hash���entry�ı�����.
     */
    private class EntryIterator extends ArrayHashIterator<Map.Entry<K, V>> {
        /**
         * ����һ��list iterator.
         * 
         * @param index ��ʼ��
         */
        protected EntryIterator(int index) {
            super(index);
        }

        /**
         * ȡ����һ��entry.
         * 
         * @return ��һ��entry
         */
        public Map.Entry<K, V> next() {
            return nextEntry();
        }

        /**
         * ȡ��ǰһ��entry.
         * 
         * @return ǰһ��entry
         */
        public Map.Entry<K, V> previous() {
            return previousEntry();
        }
    }

    /**
     * �б���ͼ.
     */
    private abstract class ArrayHashList<E> extends AbstractList<E> {
        /**
         * ����hash����entry�ĸ���.
         * 
         * @return hash���е�entry��.
         */
        @Override
        public int size() {
            return size;
        }

        /**
         * �ж��Ƿ�Ϊ�յ�hash��.
         * 
         * @return ���Ϊ��(<code>size() == 0</code>), �򷵻�<code>true</code>.
         */
        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        /**
         * �������entry.
         */
        @Override
        public void clear() {
            ArrayHashMap.this.clear();
        }

        /**
         * ȡ��ָ��entry������. ͬ<code>indexOf</code>����.
         * 
         * @param o Ҫ���ҵ�entry
         * @return ָ��entry������
         */
        @Override
        public int lastIndexOf(Object o) {
            return indexOf(o);
        }
    }

    /**
     * entry���б���ͼ.
     */
    private class EntryList extends ArrayHashList<Map.Entry<K, V>> {
        /**
         * �ж�entry�б����Ƿ����ָ������.
         * 
         * @param o Ҫ���ҵĶ���
         * @return ���entry�б����Ƿ����ָ������, �򷵻�<code>true</code>
         */
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?>)) {
                return false;
            }

            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            Entry candidate = (Entry) getEntry(entry.getKey());

            return eq(candidate, entry);
        }

        /**
         * ȡ��entry�ı�����.
         * 
         * @return entry�ı�����
         */
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return newEntryIterator();
        }

        /**
         * ɾ��ָ����entry.
         * 
         * @param o Ҫɾ����entry
         * @return ���ɾ���ɹ�, ����<code>true</code>
         */
        @Override
        public boolean remove(Object o) {
            return removeEntry(o) != null;
        }

        /**
         * ɾ��ָ��index������. ���index������Χ, ������<code>IndexOutOfBoundsException</code>.
         * 
         * @param index Ҫɾ�����������ֵ
         * @return ��ɾ����<code>Map.Entry</code>��
         */
        @Override
        public Map.Entry<K, V> remove(int index) {
            checkRange(index);
            return removeEntryForKey(order[index].getKey());
        }

        /**
         * ����ָ��index����entry. ���index������Χ, ������
         * <code>IndexOutOfBoundsException</code>.
         * 
         * @param index Ҫ���ص�entry������ֵ
         * @return ָ��index����entry����
         */
        @Override
        public Map.Entry<K, V> get(int index) {
            checkRange(index);
            return order[index];
        }

        /**
         * ȡ��ָ��entry������.
         * 
         * @param o Ҫ���ҵ�entry
         * @return ָ��entry������
         */
        @Override
        public int indexOf(Object o) {
            if (o != null && o instanceof Map.Entry<?, ?>) {
                Entry entry = (Entry) getEntry(((Map.Entry<?, ?>) o).getKey());

                if (entry != null && entry.equals(o)) {
                    return entry.index;
                }
            }

            return -1;
        }

        /**
         * ȡ��list iterator, �����õ�ǰλ��.
         * 
         * @param index ��ǰλ��
         * @return list iterator
         */
        @Override
        public ListIterator<Map.Entry<K, V>> listIterator(int index) {
            return new EntryIterator(index);
        }
    }

    /**
     * key���б���ͼ.
     */
    private class KeyList extends ArrayHashList<K> {
        /**
         * �ж�key�б����Ƿ����ָ������.
         * 
         * @param o Ҫ���ҵĶ���
         * @return ���key�б����Ƿ����ָ������, �򷵻�<code>true</code>
         */
        @Override
        public boolean contains(Object o) {
            return ArrayHashMap.this.containsKey(o);
        }

        /**
         * ȡ��key�ı�����.
         * 
         * @return key�ı�����
         */
        @Override
        public Iterator<K> iterator() {
            return newKeyIterator();
        }

        /**
         * ɾ��ָ����key.
         * 
         * @param o Ҫɾ����key
         * @return ���ɾ���ɹ�, ����<code>true</code>
         */
        @Override
        public boolean remove(Object o) {
            Entry entry = (Entry) getEntry(o);

            if (entry == null) {
                return false;
            } else {
                removeEntry(entry);
                return true;
            }
        }

        /**
         * ɾ��ָ��index������. ���index������Χ, ������<code>IndexOutOfBoundsException</code>.
         * 
         * @param index Ҫɾ�����������ֵ
         * @return ��ɾ����<code>Map.Entry</code>��
         */
        @Override
        public K remove(int index) {
            checkRange(index);
            return removeEntryForKey(order[index].getKey()).getKey();
        }

        /**
         * ����ָ��index����key. ���index������Χ, ������
         * <code>IndexOutOfBoundsException</code>.
         * 
         * @param index Ҫ���ص�key������ֵ
         * @return ָ��index����key����
         */
        @Override
        public K get(int index) {
            checkRange(index);
            return order[index].getKey();
        }

        /**
         * ȡ��ָ��key������.
         * 
         * @param o Ҫ���ҵ�key
         * @return ָ��key������
         */
        @Override
        public int indexOf(Object o) {
            Entry entry = (Entry) getEntry(o);

            if (entry != null) {
                return entry.index;
            }

            return -1;
        }

        /**
         * ȡ��list iterator, �����õ�ǰλ��.
         * 
         * @param index ��ǰλ��
         * @return list iterator
         */
        @Override
        public ListIterator<K> listIterator(int index) {
            return new KeyIterator(index);
        }
    }

    /**
     * value���б���ͼ.
     */
    private class ValueList extends ArrayHashList<V> {
        /**
         * �ж�value�б����Ƿ����ָ������.
         * 
         * @param o Ҫ���ҵĶ���
         * @return ���value�б����Ƿ����ָ������, �򷵻�<code>true</code>
         */
        @Override
        public boolean contains(Object o) {
            return ArrayHashMap.this.containsValue(o);
        }

        /**
         * ȡ��value�ı�����.
         * 
         * @return value�ı�����
         */
        @Override
        public Iterator<V> iterator() {
            return newValueIterator();
        }

        /**
         * ɾ��ָ����value.
         * 
         * @param o Ҫɾ����value
         * @return ���ɾ���ɹ�, ����<code>true</code>
         */
        @Override
        public boolean remove(Object o) {
            int index = indexOf(o);

            if (index != -1) {
                ArrayHashMap.this.removeEntry(index);
                return true;
            }

            return false;
        }

        /**
         * ɾ��ָ��index������. ���index������Χ, ������<code>IndexOutOfBoundsException</code>.
         * 
         * @param index Ҫɾ�����������ֵ
         * @return ��ɾ����<code>Map.Entry</code>��
         */
        @Override
        public V remove(int index) {
            checkRange(index);
            return removeEntryForKey(order[index].getKey()).getValue();
        }

        /**
         * ����ָ��index����value. ���index������Χ, ������
         * <code>IndexOutOfBoundsException</code>.
         * 
         * @param index Ҫ���ص�value������ֵ
         * @return ָ��index����value����
         */
        @Override
        public V get(int index) {
            checkRange(index);
            return order[index].getValue();
        }

        /**
         * ȡ��ָ��value������.
         * 
         * @param o Ҫ���ҵ�value
         * @return ָ��value������
         */
        @Override
        public int indexOf(Object o) {
            for (int i = 0; i < size; i++) {
                if (eq(o, order[i].getValue())) {
                    return i;
                }
            }

            return -1;
        }

        /**
         * ȡ��list iterator, �����õ�ǰλ��.
         * 
         * @param index ��ǰλ��
         * @return list iterator
         */
        @Override
        public ListIterator<V> listIterator(int index) {
            return new ValueIterator(index);
        }
    }

    // ==========================================================================
    // �ڲ����� 
    // ==========================================================================

    /**
     * ��ʼ��ʱhash��.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onInit() {
        order = new DefaultHashMap.Entry[threshold];
    }

    /**
     * �˷��������˸���ķ���. ���������һ��entry, ͬʱ��entry��¼��order�б���.
     * 
     * @param key hash���key
     * @param value hash���value
     */
    @Override
    protected void addEntry(K key, V value) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        Entry entry = new Entry(hash, key, value, table[i]);

        table[i] = entry;
        entry.index = size;
        order[size++] = entry;
    }

    /**
     * ���Ǹ���ķ���, ��������key�ı�����.
     * 
     * @return hash���key�ı�����
     */
    @Override
    protected Iterator<K> newKeyIterator() {
        return new KeyIterator(0);
    }

    /**
     * ���Ǹ���ķ���, ��������value�ı�����.
     * 
     * @return hash���key�ı�����
     */
    @Override
    protected Iterator<V> newValueIterator() {
        return new ValueIterator(0);
    }

    /**
     * ���Ǹ���ķ���, ��������entry�ı�����.
     * 
     * @return hash���key�ı�����
     */
    @Override
    protected Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator(0);
    }

    /**
     * ��map��������. �˷�����entry��������ֵʱ������.
     * 
     * @param newCapacity �µ�����
     */
    @Override
    protected void resize(int newCapacity) {
        super.resize(newCapacity);

        if (threshold > order.length) {
            @SuppressWarnings("unchecked")
            DefaultHashMap.Entry<K, V>[] newOrder = new DefaultHashMap.Entry[threshold];

            System.arraycopy(order, 0, newOrder, 0, order.length);

            order = newOrder;
        }
    }

    /**
     * ������<code>resize</code>ʱ����ô˷��������е���Ƶ��µ�������. ���Ǵ˷����ǳ������ܵĿ���,
     * ��Ϊ�����������hash���ԭ����ʵ�ַ�������Ч.
     * 
     * @param newTable �±�
     */
    @Override
    protected void transfer(DefaultHashMap.Entry<K, V>[] newTable) {
        int newCapacity = newTable.length;

        for (int i = 0; i < size; i++) {
            Entry entry = (Entry) order[i];
            int index = indexFor(entry.hash, newCapacity);

            entry.next = newTable[index];
            newTable[index] = entry;
        }
    }

    /**
     * ���ָ��������ֵ�Ƿ�Խ��. �����, ����������ʱ�쳣.
     * 
     * @param index Ҫ�����쳣
     */
    private void checkRange(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}
