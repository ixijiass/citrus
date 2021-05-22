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
package com.alibaba.citrus.service.velocity.impl;

import org.apache.velocity.context.AbstractContext;

import com.alibaba.citrus.service.template.TemplateContext;

/**
 * ��<code>TemplateContext</code>���䵽velocity context����������
 * 
 * @author Michael Zhou
 */
public class TemplateContextAdapter extends AbstractContext {
    private final TemplateContext context;

    /**
     * ����һ����������
     */
    public TemplateContextAdapter(TemplateContext context) {
        this.context = context;
    }

    /**
     * ȡ�ñ������<code>TemplateContext</code>����
     */
    public TemplateContext getTemplateContext() {
        return context;
    }

    /**
     * ȡ��ָ��ֵ��
     */
    @Override
    public Object internalGet(String key) {
        return context.get(key);
    }

    /**
     * ���һ��ֵ����������ڣ��򷵻�<code>null</code>��
     */
    @Override
    public Object internalPut(String key, Object value) {
        Object oldValue = context.get(key);
        context.put(key, value);
        return oldValue;
    }

    /**
     * �ж��Ƿ����ָ���ļ���
     */
    @Override
    public boolean internalContainsKey(Object key) {
        if (key instanceof String) {
            return context.containsKey((String) key);
        } else {
            return false;
        }
    }

    /**
     * ȡ������key�ļ��ϡ�
     */
    @Override
    public Object[] internalGetKeys() {
        return context.keySet().toArray();
    }

    /**
     * ɾ��һ��ֵ������ԭֵ��
     */
    @Override
    public Object internalRemove(Object key) {
        if (key instanceof String) {
            Object oldValue = context.get((String) key);
            context.remove((String) key);
            return oldValue;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "TemplateContextAdapter[" + context + "]";
    }
}
