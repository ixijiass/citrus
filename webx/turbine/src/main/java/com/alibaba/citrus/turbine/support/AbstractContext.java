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
package com.alibaba.citrus.turbine.support;

import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.Set;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * �����<code>Context</code>ʵ�֣��ṩ�˿�Ƕ�׵�context���ơ�
 * 
 * @author Michael Zhou
 */
public abstract class AbstractContext implements Context {
    /** ��context�����ָ��key�ڵ�ǰcontext�����ڣ�����ڸ�context�в��ҡ� */
    private final Context parentContext;

    /**
     * ����һ��context��
     */
    public AbstractContext() {
        this(null);
    }

    /**
     * ����һ��context��ָ��parent context��
     */
    public AbstractContext(Context parentContext) {
        this.parentContext = parentContext;
    }

    /**
     * ȡ�ø�context���粻�����򷵻�<code>null</code>��
     */
    public Context getParentContext() {
        return parentContext;
    }

    /**
     * ���һ��ֵ��
     */
    public final void put(String key, Object value) {
        if (value == null) {
            remove(key);
        } else {
            internalPut(key, value);
        }
    }

    /**
     * ȡ��ָ��ֵ��
     */
    public final Object get(String key) {
        Object value = internalGet(key);

        if (value == null && parentContext != null) {
            return parentContext.get(key);
        }

        return decodeValue(value);
    }

    /**
     * ɾ��һ��ֵ��
     */
    public final void remove(String key) {
        if (parentContext != null && parentContext.containsKey(key)) {
            internalPut(key, NULL_PLACEHOLDER);
        } else {
            internalRemove(key);
        }
    }

    /**
     * �ж��Ƿ����ָ���ļ���
     */
    public final boolean containsKey(String key) {
        boolean containsKey = internalContainsKey(key);

        if (!containsKey && parentContext != null) {
            return parentContext.containsKey(key);
        }

        return containsKey;
    }

    /**
     * ȡ������key�ļ��ϡ�
     */
    public final Set<String> keySet() {
        Set<String> internalKeySet = internalKeySet();
        Set<String> parentKeySet = parentContext == null ? null : parentContext.keySet();

        if (parentKeySet == null || parentKeySet.isEmpty()) {
            return internalKeySet;
        }

        Set<String> newSet = createHashSet();

        newSet.addAll(parentKeySet);
        newSet.addAll(internalKeySet);

        return newSet;
    }

    /**
     * ȡ������key�ļ��ϡ�
     */
    protected abstract Set<String> internalKeySet();

    /**
     * ȡ��ָ��ֵ��
     */
    protected abstract Object internalGet(String key);

    /**
     * ɾ��һ��ֵ��
     */
    protected abstract void internalRemove(String key);

    /**
     * �ж��Ƿ����ָ���ļ���
     */
    protected abstract boolean internalContainsKey(String key);

    /**
     * ���һ��ֵ��
     */
    protected abstract void internalPut(String key, Object value);

    /**
     * ����context��ֵ�����Ϊ<code>NULL_PLACEHOLDER</code>���򷵻�<code>null</code>��
     */
    private Object decodeValue(Object value) {
        return value == NULL_PLACEHOLDER ? null : value;
    }

    @Override
    public String toString() {
        MapBuilder mb;

        if (parentContext == null) {
            mb = getMapBuilder();
        } else {
            mb = new MapBuilder();

            mb.append("parentContext", parentContext);
            mb.append("thisContext", getMapBuilder());
        }

        return new ToStringBuilder().append(getClass().getSimpleName()).append(mb).toString();
    }

    private MapBuilder getMapBuilder() {
        MapBuilder mb = new MapBuilder().setSortKeys(true);

        for (String key : internalKeySet()) {
            mb.append(key, get(key));
        }

        return mb;
    }
}
