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
package com.alibaba.citrus.service.jsp.impl;

import static com.alibaba.citrus.util.Assert.*;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.alibaba.citrus.service.template.TemplateContext;

/**
 * ��<code>TemplateContext</code>���䵽HTTP request����������
 * 
 * @author Michael Zhou
 */
public class TemplateContextAdapter extends HttpServletRequestWrapper {
    private final TemplateContext context;

    public TemplateContextAdapter(HttpServletRequest request, TemplateContext context) {
        super(assertNotNull(request, "request"));
        this.context = assertNotNull(context, "templateContext");
    }

    /**
     * ȡ�ñ������<code>TemplateContext</code>����
     */
    public TemplateContext getTemplateContext() {
        return context;
    }

    /**
     * ȡ��request��������������Ե�keys��
     * <p>
     * ����ȡ��context�е�����keys��Ȼ��ȡ��<code>request.getAttributeNames</code>
     * �����ص�keys��keys�����ظ���
     * </p>
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        @SuppressWarnings("unchecked")
        Enumeration<String> attrNames = getRequest().getAttributeNames();
        return new AttributeNamesEnumeration(context.keySet(), attrNames);
    }

    /**
     * ȡ��request����������ԡ�
     * <p>
     * ���context�д���ָ�����ƵĶ����򷵻�֮�����򷵻�<code>request.getAttribute</code>��ֵ��
     * </p>
     * <p>
     * ���ֵ�����ڣ��򷵻�<code>null</code>��
     * </p>
     */
    @Override
    public Object getAttribute(String name) {
        Object value = context.get(name);

        if (value == null) {
            value = getRequest().getAttribute(name);
        }

        return value;
    }

    /**
     * ����request����������ԡ�
     * <p>
     * ��ֵ�������õ�<code>request.setAttribute</code>
     * �У���context�е�ͬ��ֵ����ɾ�����Ա����Ĵ�����Է��������õ�ֵ��
     * </p>
     */
    @Override
    public void setAttribute(String name, Object value) {
        context.remove(name);
        getRequest().setAttribute(name, value);
    }

    /**
     * ɾ��request����������ԡ�ͬʱ��<code>request.removeAttribute</code>
     * ��context��ɾ��ָ�����Ƶ����ԡ�
     */
    @Override
    public void removeAttribute(String name) {
        context.remove(name);
        getRequest().removeAttribute(name);
    }

    @Override
    public String toString() {
        return "TemplateContextAdapter[" + context + "]";
    }

    /**
     * ��һ�����Ϻ�һ��<code>Enumeration</code>��ϵ�<code>Enumeration</code>��
     */
    private static class AttributeNamesEnumeration implements Enumeration<String> {
        private final Set<String> set;
        private final Iterator<String> iterator;
        private final Enumeration<String> enumeration; // ����Ϊnull
        private String next = null;

        public AttributeNamesEnumeration(Set<String> set, Enumeration<String> enumeration) {
            this.set = set;
            this.iterator = set.iterator();
            this.enumeration = enumeration; // ����Ϊnull
        }

        public boolean hasMoreElements() {
            if (next == null) {
                if (iterator.hasNext()) {
                    next = iterator.next();
                } else if (enumeration != null) {
                    while (next == null && enumeration.hasMoreElements()) {
                        next = enumeration.nextElement();

                        if (set.contains(next)) {
                            next = null;
                        }
                    }
                }
            }

            return next != null;
        }

        public String nextElement() {
            if (hasMoreElements()) {
                String result = next;

                next = null;
                return result;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
