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

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * Hash���һ��ʵ��, ʵ����<code>Map</code>�ӿ�.
 * </p>
 * <p>
 * ���hash���ʵ����ȫ����JDK��<code>HashMap</code>, ���������¸ı�, �Ա�����������, ��ʵ�����⹦��:
 * </p>
 * <ul>
 * <li>���������Ա���ó�protected��friendly</li>
 * <li>������һЩ�������¼�</li>
 * </ul>
 * <p>
 * ��JDK��<code>HashMap</code>һ��, ���ʵ�־�����������:
 * </p>
 * <ul>
 * <li>֧��ֵΪ<code>null</code>��key��value</li>
 * <li>û�н����κ�<code>synchronized</code>����, ��������̰߳�ȫ��. ������ͨ�����²���ʵ���̰߳�ȫ:</li>
 * </ul>
 * 
 * <pre style="margin-left:48.0">
 *  Map m = Collections.synchronizedMap(new DefaultHashMap(...));
 * </pre>
 * <ul>
 * <li>����֤hash���е�entry��˳��</li>
 * <li>�Լ����ⶨ�����ܴ�ȡhash���е�ÿ��entry</li>
 * <li>��hash����ȡ�õ��κ�<code>Iterator</code>����<i>fail-fast</i>����: ��hash��Ľṹ���ı�ʱ, ����
 * <code>Iterator.remove</code>��<code>Iterator.add</code>����ʱ, ������
 * <code>ConcurrentModificationException</code>. ����ȷ��������ֲ�ȷ�������.</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public class DefaultHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {
    // ==========================================================================
    // ���� 
    // ==========================================================================

    /** Ĭ�ϵĳ�ʼ���� - <code>2����������</code>. */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /** ������� - <code>2����������</code>. */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /** Ĭ�ϵĸ���ϵ�� */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    // ==========================================================================
    // ��Ա���� 
    // ==========================================================================

    /** Hash��, ���ȿɱ� - �����ȱ�����<code>2����������</code>. */
    protected transient Entry<K, V>[] table;

    /** Hash���е�entry��. */
    protected transient int size;

    /**
     * ��ֵ. ��hash���е�entry��������ʱ, �Զ�����(<code>resize</code>). ��ֵ����
     * <code>capacity&times;loadFactor</code>.
     * 
     * @serial �Զ����л��ֶ�
     */
    protected int threshold;

    /**
     * ����ϵ��.
     * 
     * @serial �Զ����л��ֶ�
     */
    protected final float loadFactor;

    /**
     * ��hash����&quot;�ṹ�ı�&quot;�ļ���. ��ν&quot;�ṹ�ı�&quot;,
     * ��ָhash����entry����Ŀ�����ı���ڲ��ṹ�ı�(��<code>resize</code>).
     * ����ֶ���Ϊ��ʵ��<i>fail-fast</i>.
     */
    protected transient volatile int modCount;

    /** key�ļ�����ͼ. */
    private transient Set<K> keySet = null;

    /** entry�ļ�����ͼ. */
    private transient Set<Map.Entry<K, V>> entrySet = null;

    /** value�ļ�����ͼ. */
    private transient Collection<V> values = null;

    // ==========================================================================
    // ���캯�� 
    // ==========================================================================

    /**
     * ����һ���յ�hash��. ʹ��ָ����Ĭ�ϵĳ�ʼ����(16)��Ĭ�ϵĸ���ϵ��(0.75).
     */
    public DefaultHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * ����һ���յ�hash��. ʹ��ָ���ĳ�ʼ��ֵ��Ĭ�ϵĸ���ϵ��(0.75).
     * 
     * @param initialCapacity ��ʼ����.
     */
    public DefaultHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * ����һ���յ�hash��. ʹ��ָ���ĳ�ʼ�����͸���ϵ��.
     * 
     * @param initialCapacity ��ʼ����
     * @param loadFactor ����ϵ��.
     */
    @SuppressWarnings("unchecked")
    public DefaultHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }

        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }

        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        // ȷ����ʼ����Ϊ2����������.
        int capacity = 1;

        while (capacity < initialCapacity) {
            capacity <<= 1;
        }

        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.table = new Entry[capacity];

        onInit();
    }

    /**
     * ����ָ��<code>Map</code>������ͬ��<code>HashMap</code>. ʹ��Ĭ�ϵĸ���ϵ��(0.75).
     * 
     * @param map Ҫ���Ƶ�<code>Map</code>
     */
    public DefaultHashMap(Map<? extends K, ? extends V> map) {
        this(Math.max((int) (map.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        putAllForCreate(map);
    }

    // ==========================================================================
    // ʵ��Map�ӿڵķ��� 
    // ==========================================================================

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
     * ����ָ��key��Ӧ��value. ���hash����û��value��Ӧkey, �򷵻�<code>null</code>. ���Ƿ���
     * <code>null</code>�������Ǵ���û��value��Ӧָ����key, Ҳ�п�����ָ valueֵ����Ϊ<code>null</code>
     * . ����ͨ������<code>containsKey</code>���������� �����.
     * 
     * @param key ָ��key����Ӧ��value��������.
     * @return ָ��key��Ӧ��value, ���û��value��Ӧ��key, �򷵻�<code>null</code>.
     */
    @Override
    public V get(Object key) {
        Entry<K, V> entry = getEntry(key);

        return entry == null ? null : entry.getValue();
    }

    /**
     * ���hash���а���ָ��key��entry, �򷵻�<code>true</code>.
     * 
     * @param key ����ָ����key�Ƿ����.
     * @return ���key��Ӧ��entry����, �򷵻�<code>true</code>.
     */
    @Override
    public boolean containsKey(Object key) {
        Entry<K, V> entry = getEntry(key);

        return entry != null;
    }

    /**
     * ��ָ����value��key����. ����Ѿ���value�ʹ�key�����, ��ȡ��֮, �� ���ر�ȡ����value.
     * 
     * @param key Ҫ������key
     * @param value Ҫ��key������value
     * @return ����Ѿ����ںʹ�key�������value, �򷵻ش�value. ���򷵻�<code>null</code>. ����
     *         <code>null</code>Ҳ��������Ϊ��ȡ�������valueֵΪ<code>null</code>.
     */
    @Override
    public V put(K key, V value) {
        Entry<K, V> entry = getEntry(key);

        if (entry != null) {
            V oldValue = entry.getValue();

            entry.setValue(value);
            entry.onAccess();

            return oldValue;
        } else {
            modCount++;

            // ������е���������������ֵ, ����������.
            if (size >= threshold) {
                resize(table.length * 2);
            }

            addEntry(key, value);

            return null;
        }
    }

    /**
     * ��<code>Map</code>�е���������뵽��ǰ��<code>Map</code>��. �������ͬ��key, ���滻֮.
     * 
     * @param map Ҫ�����<code>Map</code>
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        // һ��������, �Ա������Լ����entry.
        int n = map.size();

        if (n == 0) {
            return;
        }

        if (n >= threshold) {
            n = (int) (n / loadFactor + 1);

            if (n > MAXIMUM_CAPACITY) {
                n = MAXIMUM_CAPACITY;
            }

            int capacity = table.length;

            while (capacity < n) {
                capacity <<= 1;
            }

            resize(capacity);
        }

        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * ��ָ��key��entry��hash����ɾ��(�����entry���ڵĻ�).
     * 
     * @param key Ҫ��ɾ����entry��key
     * @return ��ɾ����entry��value. ���entry������, �򷵻�<code>null</code>. ���Ƿ���
     *         <code>null</code>�������Ǵ���û��value��Ӧָ����key, Ҳ�п�����ָ valueֵ����Ϊ
     *         <code>null</code>.
     */
    @Override
    public V remove(Object key) {
        Entry<K, V> entry = removeEntryForKey(key);

        return entry == null ? null : entry.getValue();
    }

    /**
     * ���hash���е�����entry.
     */
    @Override
    public void clear() {
        modCount++;
        Arrays.fill(table, null);
        size = 0;
    }

    /**
     * �ж�hash�����Ƿ���һ������entry����ָ����value.
     * 
     * @param value Ҫ���Ե�value
     * @return �����һ������entry����ָ����value, �򷵻�<code>true</code>
     */
    @Override
    public boolean containsValue(Object value) {
        Entry<K, V>[] tab = table;

        for (Entry<K, V> element : tab) {
            for (Entry<K, V> entry = element; entry != null; entry = entry.next) {
                if (eq(value, entry.getValue())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * ȡ��key�ļ�����ͼ. �����������hash��Ϊ������, �κ�hash��ĸı�, ���ᷴ�䵽�������, ��֮��Ȼ. �ü���֧��ɾ������,
     * ɾ�������е�key��ɾ����hash���е���Ӧentry. ����ͨ�����·���ɾ��һ��entry:
     * <code>Iterator.remove</code>, <code>Set.remove</code>,
     * <code>removeAll</code>, <code>retainAll</code>, ��<code>clear</code>.
     * �����ϲ�֧��<code>add</code>��<code>addAll</code>����.
     * 
     * @return key�ļ�����ͼ
     */
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;

        return ks != null ? ks : (keySet = new KeySet());
    }

    /**
     * ȡ��value�ļ�����ͼ. �����������hash��Ϊ������, �κ�hash��ĸı�, ���ᷴ�䵽�������, ��֮��Ȼ. �ü���֧��ɾ������,
     * ɾ�������е�key��ɾ����hash���е���Ӧentry. ����ͨ�����·���ɾ��һ��entry:
     * <code>Iterator.remove</code>, <code>Collection.remove</code>,
     * <code>removeAll</code>, <code>retainAll</code>, ��<code>clear</code>.
     * �����ϲ�֧��<code>add</code>��<code>addAll</code>����.
     * 
     * @return value�ļ�����ͼ
     */
    @Override
    public Collection<V> values() {
        Collection<V> vs = values;

        return vs != null ? vs : (values = new Values());
    }

    /**
     * ȡ��entry�ļ�����ͼ. �����������hash��Ϊ������, �κ�hash��ĸı�, ���ᷴ�䵽�������, ��֮��Ȼ. �ü���֧��ɾ������,
     * ɾ�������е�key��ɾ����hash���е���Ӧentry. ����ͨ�����·���ɾ��һ��entry:
     * <code>Iterator.remove</code>, <code>Set.remove</code>,
     * <code>removeAll</code>, <code>retainAll</code>, ��<code>clear</code>.
     * �����ϲ�֧��<code>add</code>��<code>addAll</code>����.
     * 
     * @return entry�ļ�����ͼ
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = entrySet;

        return es != null ? es : (entrySet = new EntrySet());
    }

    // ==========================================================================
    // �ڲ��� 
    // ==========================================================================

    /**
     * <code>Map.Entry</code>��ʵ��.
     */
    protected static class Entry<K, V> extends DefaultMapEntry<K, V> {
        /** key��hashֵ. */
        protected final int hash;

        /** ��ͬhashֵ��entry��������ķ�ʽ��ŵ�, �������ָ�������е���һ��entry. */
        protected Entry<K, V> next;

        /**
         * ����һ���µ�entry.
         * 
         * @param h key��hashֵ
         * @param k entry��key
         * @param v entry��value
         * @param n �����е���һ��entry
         */
        protected Entry(int h, K k, V v, Entry<K, V> n) {
            super(k, v);
            next = n;
            hash = h;
        }

        /**
         * ��<code>put(key, value)</code>����������ʱ, ���entry�Ѿ����ڽ�������ʱ, �˷���������.
         */
        protected void onAccess() {
        }

        /**
         * ��entry����ɾ��ʱ, �˷���������.
         */
        protected void onRemove() {
        }
    }

    /**
     * ������.
     */
    private abstract class HashIterator<E> implements Iterator<E> {
        /** ��ǰentry. */
        private Entry<K, V> current;

        /** ��һ��Ҫ���ص�entry. */
        private Entry<K, V> next;

        /** ����iteratorʱ���޸ļ���. */
        private int expectedModCount;

        /** ��ǰλ������. */
        private int index;

        /**
         * ����һ��������.
         */
        protected HashIterator() {
            expectedModCount = modCount;

            Entry<K, V>[] t = table;
            int i = t.length;
            Entry<K, V> n = null;

            if (size != 0) { // advance to first entry

                while (i > 0 && (n = t[--i]) == null) {
                    ;
                }
            }

            next = n;
            index = i;
        }

        /**
         * ���ر��������Ƿ�����һ��entry.
         * 
         * @return ����������л�����һ��entry, ����<code>true</code>
         */
        public boolean hasNext() {
            return next != null;
        }

        /**
         * ɾ��һ����ǰentry. ִ��ǰ������ִ��<code>next()</code>����.
         */
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }

            checkForComodification();

            Object k = current.getKey();

            current = null;
            DefaultHashMap.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

        /**
         * ȡ����һ��entry.
         * 
         * @return ��һ��entry
         */
        protected Entry<K, V> nextEntry() {
            checkForComodification();

            Entry<K, V> entry = next;

            if (entry == null) {
                throw new NoSuchElementException();
            }

            Entry<K, V> n = entry.next;
            Entry<K, V>[] t = table;
            int i = index;

            while (n == null && i > 0) {
                n = t[--i];
            }

            index = i;
            next = n;

            return current = entry;
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
    private class KeyIterator extends HashIterator<K> {
        /**
         * ȡ����һ��key.
         * 
         * @return ��һ��key
         */
        public K next() {
            return nextEntry().getKey();
        }
    }

    /**
     * ȡ��hash���value�ı�����.
     */
    private class ValueIterator extends HashIterator<V> {
        /**
         * ȡ����һ��value.
         * 
         * @return ��һ��value
         */
        public V next() {
            return nextEntry().getValue();
        }
    }

    /**
     * ȡ��hash���entry�ı�����.
     */
    private class EntryIterator extends HashIterator<Map.Entry<K, V>> {
        /**
         * ȡ����һ��entry.
         * 
         * @return ��һ��entry
         */
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    /**
     * key�ļ�����ͼ.
     */
    private class KeySet extends AbstractSet<K> {
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
         * ȡ�ü��ϵĴ�С, ����hash����entry������.
         * 
         * @return hash����entry������
         */
        @Override
        public int size() {
            return size;
        }

        /**
         * �ж�key���Ƿ����ָ������.
         * 
         * @param o Ҫ���ҵĶ���
         * @return ���key�а���ָ���Ķ���, �򷵻�<code>true</code>
         */
        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        /**
         * ��hash����ɾ��keyΪָ�������entry.
         * 
         * @param o ָ����key
         * @return ���ɾ���ɹ�, �򷵻�<code>true</code>
         */
        @Override
        public boolean remove(Object o) {
            return DefaultHashMap.this.removeEntryForKey(o) != null;
        }

        /**
         * �������entry.
         */
        @Override
        public void clear() {
            DefaultHashMap.this.clear();
        }
    }

    /**
     * value�ļ�����ͼ.
     */
    private class Values extends AbstractCollection<V> {
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
         * ȡ�ü��ϵĴ�С, ����hash����entry������.
         * 
         * @return hash����entry������
         */
        @Override
        public int size() {
            return size;
        }

        /**
         * �ж�value���Ƿ����ָ������.
         * 
         * @param o Ҫ���ҵĶ���
         * @return ���value�а���ָ���Ķ���, �򷵻�<code>true</code>
         */
        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        /**
         * �������entry.
         */
        @Override
        public void clear() {
            DefaultHashMap.this.clear();
        }
    }

    /**
     * entry�ļ�����ͼ.
     */
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
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
         * �ж�entry�������Ƿ����ָ������.
         * 
         * @param o Ҫ���ҵĶ���
         * @return ���entry���Ƿ����ָ������, �򷵻�<code>true</code>
         */
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }

            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            Entry<K, V> candidate = getEntry(entry.getKey());

            return eq(candidate, entry);
        }

        /**
         * ��hash����ɾ��ָ��entry.
         * 
         * @param o Ҫɾ����entry
         * @return ���ɾ���ɹ�, �򷵻�<code>true</code>
         */
        @Override
        public boolean remove(Object o) {
            return removeEntry(o) != null;
        }

        /**
         * ȡ�ü��ϵĴ�С, ����hash����entry������.
         * 
         * @return hash����entry������
         */
        @Override
        public int size() {
            return size;
        }

        /**
         * �������entry.
         */
        @Override
        public void clear() {
            DefaultHashMap.this.clear();
        }
    }

    // ==========================================================================
    // ���л� 
    // ==========================================================================

    /** ���л��汾��. */
    private static final long serialVersionUID = 362498820763181265L;

    /**
     * �����������ؽ�hash��(Ҳ���Ƿ����л�).
     * 
     * @param is ������
     * @exception IOException �������쳣
     * @exception ClassNotFoundException ��δ�ҵ�
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream is) throws IOException, ClassNotFoundException {
        // ����threshold, loadfactor, ���������صĶ���.
        is.defaultReadObject();

        // ȡ��hash�������.
        int numBuckets = is.readInt();

        table = new Entry[numBuckets];

        // ������һ�������ʼ��.
        onInit();

        // ����hash����entry�ĸ���.
        int size = is.readInt();

        // �������е�entry.
        for (int i = 0; i < size; i++) {
            K key = (K) is.readObject();
            V value = (V) is.readObject();

            putForCreate(key, value);
        }
    }

    /**
     * ��hash���״̬���浽�������(Ҳ����&quot;���л�&quot;).
     * 
     * @param os �����
     * @exception IOException ������쳣
     */
    private void writeObject(java.io.ObjectOutputStream os) throws IOException {
        // ���threshold, loadfactor, ���������صĶ���.
        os.defaultWriteObject();

        // ���hash�������.
        os.writeInt(table.length);

        // ���hash��Ĵ�С.
        os.writeInt(size);

        // �������entry.
        for (Map.Entry<K, V> entry : entrySet()) {
            os.writeObject(entry.getKey());
            os.writeObject(entry.getValue());
        }
    }

    // ==========================================================================
    // ���Ʒ���(Clonable�ӿ�) 
    // ==========================================================================

    /**
     * &quot;ǳ&quot;����hash��, key��value������������.
     * 
     * @return �����Ƶ�hash��.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        DefaultHashMap<K, V> result = null;

        try {
            result = (DefaultHashMap<K, V>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(); // ��֧��clone(������).
        }

        result.table = new Entry[table.length];
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.onInit();
        result.putAllForCreate(this);

        return result;
    }

    // ==========================================================================
    // �ڲ����� 
    // ==========================================================================

    /**
     * ������һ�������ʼ���Լ�. �÷��������й��캯���Լ�&quot;α���캯��&quot;(<code>clone</code>,
     * <code>readObject</code>)����. ����ʱ, hash���ѱ���ʼ��, ��������δ�����뵽����.
     */
    protected void onInit() {
    }

    /**
     * ����ָ��key��Ӧ��entry. ���������, �򷵻�null.
     * 
     * @param key ����ָ��key��Ӧ��entry
     * @return ָ��key��Ӧ��entry
     */
    protected Entry<K, V> getEntry(Object key) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);

        for (Entry<K, V> entry = table[i]; entry != null; entry = entry.next) {
            if (entry.hash == hash && eq(key, entry.getKey())) {
                return entry;
            }
        }

        return null;
    }

    /**
     * ����һ��entry��hash����, �������hash�����<code>resize()</code>����. ������Ը��Ǵ˷���, �Ըı�
     * <code>put</code>, <code>new HashMap(Map)</code>, <code>clone</code>, ��
     * <code>readObject</code>��������Ϊ.
     * 
     * @param key hash���key
     * @param value hash���value
     */
    protected void addEntry(K key, V value) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);

        table[i] = new Entry<K, V>(hash, key, value, table[i]);
        size++;
    }

    /**
     * �˷��������캯����&quot;α���캯��&quot;(clone, readObject)����, ����ͬput����,
     * ���������resize��ı�modCount����.
     * 
     * @param key Ҫ������key
     * @param value Ҫ��key������value
     */
    private void putForCreate(K key, V value) {
        Entry<K, V> entry = getEntry(key);

        if (entry != null) {
            entry.setValue(value);
        } else {
            addEntry(key, value);
        }
    }

    /**
     * һ��put���entry.
     * 
     * @param map ָ��map������entry��������hash����
     */
    private void putAllForCreate(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            putForCreate(entry.getKey(), entry.getValue());
        }
    }

    /**
     * ɾ��ָ��key��Ӧ��entry, �����ر�ɾ����entry.
     * 
     * @param key Ҫɾ����entry��key
     * @return ��ɾ����entry, ���entry������, �򷵻�<code>null</code>
     */
    protected Entry<K, V> removeEntryForKey(Object key) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        Entry<K, V> prev = table[i];
        Entry<K, V> entry = prev;

        while (entry != null) {
            Entry<K, V> next = entry.next;

            if (entry.hash == hash && eq(key, entry.getKey())) {
                modCount++;
                size--;

                if (prev == entry) {
                    table[i] = next;
                } else {
                    prev.next = next;
                }

                entry.onRemove();

                return entry;
            }

            prev = entry;
            entry = next;
        }

        return entry;
    }

    /**
     * ɾ��ָ����entry. �����������<code>EntrySet.remove</code>.
     * 
     * @param o Ҫɾ����entry
     * @return ��ɾ����entry
     */
    protected Entry<K, V> removeEntry(Object o) {
        if (!(o instanceof Map.Entry<?, ?>)) {
            return null;
        }

        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
        Object key = entry.getKey();
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        Entry<K, V> prev = table[i];
        Entry<K, V> e = prev;

        while (e != null) {
            Entry<K, V> next = e.next;

            if (e.hash == hash && e.equals(entry)) {
                modCount++;
                size--;

                if (prev == e) {
                    table[i] = next;
                } else {
                    prev.next = next;
                }

                e.onRemove();

                return e;
            }

            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * ���า�Ǵ˷���, ��������key�ı�����.
     * 
     * @return hash���key�ı�����
     */
    protected Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }

    /**
     * ���า�Ǵ˷���, ��������value�ı�����.
     * 
     * @return hash���key�ı�����
     */
    protected Iterator<V> newValueIterator() {
        return new ValueIterator();
    }

    /**
     * ���า�Ǵ˷���, ��������entry�ı�����.
     * 
     * @return hash���key�ı�����
     */
    protected Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }

    /**
     * ���ض����hashֵ.
     * 
     * @param obj ȡ��ָ�������hashֵ
     * @return ָ�������hashֵ
     */
    protected static int hash(Object obj) {
        int h = obj == null ? 0 : obj.hashCode();

        return h - (h << 7); // Ҳ����, -127 * h
    }

    /**
     * �Ƚ���������.
     * 
     * @param x ��һ������
     * @param y �ڶ�������
     * @return �����ͬ, �򷵻�<code>true</code>
     */
    protected static boolean eq(Object x, Object y) {
        return x == null ? y == null : x == y || x.equals(y);
    }

    /**
     * ��������ֵ, ����ָ����hashֵ������ĳ���.
     * 
     * @param hash hashֵ
     * @param length ����ĳ���, ��Ȼ��2����������
     * @return hashֵ�������е����
     */
    protected static int indexFor(int hash, int length) {
        return hash & length - 1;
    }

    /**
     * ��map��������. �˷�����entry��������ֵʱ������.
     * 
     * @param newCapacity �µ�����(����Ϊ2����������).
     */
    protected void resize(int newCapacity) {
        Entry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;

        if (size < threshold || oldCapacity > newCapacity) {
            return;
        }

        @SuppressWarnings("unchecked")
        Entry<K, V>[] newTable = new Entry[newCapacity];

        transfer(newTable);
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * ������entry�ӵ�ǰ�����Ƶ��±���(����).
     * 
     * @param newTable �±�
     */
    protected void transfer(Entry<K, V>[] newTable) {
        Entry<K, V>[] src = table;
        int newCapacity = newTable.length;

        for (int j = 0; j < src.length; j++) {
            Entry<K, V> entry = src[j];

            if (entry != null) {
                src[j] = null;

                do {
                    Entry<K, V> next = entry.next;
                    int i = indexFor(entry.hash, newCapacity);

                    entry.next = newTable[i];
                    newTable[i] = entry;
                    entry = next;
                } while (entry != null);
            }
        }
    }

    /**
     * ȡ��hash�������.
     * 
     * @return hash�������
     */
    protected int getCapacity() {
        return table.length;
    }

    /**
     * ȡ��hash��ĸ���ϵ��.
     * 
     * @return hash��ĸ���ϵ��
     */
    protected float getLoadFactor() {
        return loadFactor;
    }

    /**
     * ȡ��hash�����ֵ.
     * 
     * @return hash�����ֵ
     */
    protected int getThreshold() {
        return threshold;
    }
}
