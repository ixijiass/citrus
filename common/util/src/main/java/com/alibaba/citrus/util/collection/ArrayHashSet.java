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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * һ�����ϵ�ʵ��, ʵ����<code>Set</code>�ӿ�.
 * </p>
 * <p>
 * ����������ڲ�ʹ��<code>ArrayHashMap</code>���漯���е�Ԫ��, ���������������:
 * </p>
 * <ul>
 * <li>��ͬ��<code>HashMap</code>, ������Ԫ�ص�˳����ȷ����</li>
 * <li>��<code>ArrayHashMap</code>һ��, û�н����κ�<code>synchronized</code>����</li>
 * </ul>
 * 
 * @author Michael Zhou
 * @see ArrayHashMap
 */
public class ArrayHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
    // ==========================================================================
    // ����                                                                      
    // ==========================================================================

    /** ��ʾ�ڲ�hash���ֵ. */
    private static final Object PRESENT = new Object();

    // ==========================================================================
    // ��Ա����                                                                  
    // ==========================================================================

    /** �ڲ���hash��. */
    protected transient ArrayHashMap<E, Object> map;

    // ==========================================================================
    // ���캯��                                                                   
    // ==========================================================================

    /**
     * ����һ���յļ���. ʹ��ָ����Ĭ�ϵĳ�ʼ����(16)��Ĭ�ϵĸ���ϵ��(0.75).
     */
    public ArrayHashSet() {
        map = new ArrayHashMap<E, Object>();
    }

    /**
     * ����һ���յļ���. ʹ��ָ���ĳ�ʼ��ֵ��Ĭ�ϵĸ���ϵ��(0.75).
     * 
     * @param initialCapacity ��ʼ����.
     */
    public ArrayHashSet(int initialCapacity) {
        map = new ArrayHashMap<E, Object>(initialCapacity);
    }

    /**
     * ����һ���յļ���. ʹ��ָ���ĳ�ʼ�����͸���ϵ��.
     * 
     * @param initialCapacity ��ʼ����
     * @param loadFactor ����ϵ��.
     */
    public ArrayHashSet(int initialCapacity, float loadFactor) {
        map = new ArrayHashMap<E, Object>(initialCapacity, loadFactor);
    }

    /**
     * ����һ���յļ���, ������ָ����<code>Collection</code>����������������. ʹ��Ĭ�ϵĸ���ϵ��(0.75).
     * 
     * @param collection Ҫ���Ƶ�<code>Collection</code>
     */
    public ArrayHashSet(Collection<? extends E> collection) {
        map = new ArrayHashMap<E, Object>(Math.max((int) (collection.size() / .75f) + 1, 16));
        addAll(collection);
    }

    // ==========================================================================
    // ʵ��Set�ӿڵķ���                                                          
    // ==========================================================================

    /**
     * ���ؼ�����entry�ĸ���.
     * 
     * @return �����е�entry��.
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * �ж��Ƿ�Ϊ�յļ���.
     * 
     * @return ���Ϊ��(<code>size() == 0</code>), �򷵻�<code>true</code>.
     */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * ��������а���ָ��ֵ, �򷵻�<code>true</code>.
     * 
     * @param object ����ָ��ֵ�Ƿ����.
     * @return ���ָ��ֵ����, �򷵻�<code>true</code>.
     */
    @Override
    public boolean contains(Object object) {
        return map.containsKey(object);
    }

    /**
     * ��ָ����ֵ���뵽������.
     * 
     * @param object Ҫ�����ֵ
     * @return ����������Ѿ����ڴ�ֵ, �򷵻�<code>false</code>. ���򷵻�<code>true</code>.
     */
    @Override
    public boolean add(E object) {
        return map.put(object, PRESENT) == null;
    }

    /**
     * ��ָ��ֵ�Ӽ�����ɾ��(�����ֵ���ڵĻ�).
     * 
     * @param object Ҫ��ɾ����ֵ
     * @return �����ɾ����ֵԭ��������, �򷵻�<code>false</code>, ���򷵻�<code>true</code>
     */
    @Override
    public boolean remove(Object object) {
        return map.remove(object) == PRESENT;
    }

    /**
     * ��������е����ж���.
     */
    @Override
    public void clear() {
        map.clear();
    }

    /**
     * ȡ�ü�����������ı�����.
     * 
     * @return ������������ı�����
     */
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    // ==========================================================================
    // ���Ʒ���(Clonable�ӿ�)                                                     
    // ==========================================================================

    /**
     * &quot;ǳ&quot;��������, �����еĶ�������������.
     * 
     * @return �����Ƶļ���.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            ArrayHashSet<E> newSet = (ArrayHashSet<E>) super.clone();

            newSet.map = (ArrayHashMap<E, Object>) map.clone();

            return newSet;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(); // ��֧��clone(������).
        }
    }

    // ==========================================================================
    // ���л�                                                                     
    // ==========================================================================

    /** ���л��汾��. */
    private static final long serialVersionUID = -5024744406713321676L;

    /**
     * �����������ؽ�����(Ҳ���Ƿ����л�).
     * 
     * @param is ������
     * @exception IOException �������쳣
     * @exception ClassNotFoundException ��δ�ҵ�
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();

        int capacity = is.readInt();
        float loadFactor = is.readFloat();

        map = new ArrayHashMap<E, Object>(capacity, loadFactor);

        int size = is.readInt();

        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            E e = (E) is.readObject();

            map.put(e, PRESENT);
        }
    }

    /**
     * �����ϵ�״̬���浽�������(Ҳ�������л�).
     * 
     * @param os �����
     * @exception IOException ������쳣
     */
    private void writeObject(ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();

        os.writeInt(map.getCapacity());
        os.writeFloat(map.getLoadFactor());

        os.writeInt(map.size());

        for (E e : map.keySet()) {
            os.writeObject(e);
        }
    }
}
