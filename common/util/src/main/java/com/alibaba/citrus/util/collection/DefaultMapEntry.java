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

import static com.alibaba.citrus.util.ObjectUtil.*;

import java.util.Map;

/**
 * <p>
 * <code>Map.Entry</code>��Ĭ��ʵ��. ������������:
 * </p>
 * <ul>
 * <li>֧��ֵΪ<code>null</code>��key</li>
 * <li>���Ժ�����<code>Map.Entry</code>��ʵ�ֽ���<code>equals</code>�Ƚ�</li>
 * <li>�������<code>Map.Entry</code>��ͬ(<code>e1.equals(e2) == true</code>), �����ǵ�
 * <code>hashCode()</code>Ҳ���</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public class DefaultMapEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    /**
     * ����һ��<code>Map.Entry</code>.
     * 
     * @param key <code>Map.Entry</code>��key
     * @param value <code>Map.Entry</code>��value
     */
    public DefaultMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * ȡ��key.
     * 
     * @return <code>Map.Entry</code>��key
     */
    public K getKey() {
        return key;
    }

    /**
     * ȡ��value.
     * 
     * @return <code>Map.Entry</code>��value
     */
    public V getValue() {
        return value;
    }

    /**
     * ����value��ֵ.
     * 
     * @param value �µ�valueֵ
     * @return �ϵ�valueֵ
     */
    public V setValue(V value) {
        V oldValue = this.value;

        this.value = value;

        return oldValue;
    }

    /**
     * �ж����������Ƿ���ͬ.
     * 
     * @param o Ҫ�ȽϵĶ���
     * @return �����ͬ, �򷵻�<code>true</code>
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (!(o instanceof Map.Entry)) {
            return false;
        }

        Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;

        return isEquals(key, e.getKey()) && isEquals(value, e.getValue());
    }

    /**
     * ȡ��<code>Map.Entry</code>��hashֵ. �������<code>Map.Entry</code>��ͬ,
     * �����ǵ�hashֵҲ��ͬ.
     * 
     * @return hashֵ
     */
    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    /**
     * ��<code>Map.Entry</code>ת�����ַ���.
     * 
     * @return �ַ�����ʽ��<code>Map.Entry</code>
     */
    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }
}
