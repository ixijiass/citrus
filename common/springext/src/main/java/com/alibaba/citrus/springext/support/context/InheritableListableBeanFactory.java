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
package com.alibaba.citrus.springext.support.context;

import static java.util.Collections.*;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * ����һ��hack�࣬����������⣺
 * <ul>
 * <li>��context�ɼ̳�parent
 * <code>DefaultListableBeanFactory.resolvableDependencies</code>��
 * ���磺�ڸ�context�ж����<code>&lt;request-contexts /&gt;</code>
 * ����resolvableDependencies��ע��<code>HttpServletRequest</code>��singleton
 * proxy�������û������࣬��ô����context�ж����bean��������ע����Щ����</li>
 * <li>��context���ܸ��Ǹ�context�����е�resolvableDependencies���󡣷���
 * WebApplicationContext���Զ�ע���singleton��request����
 * ��ʹ����context����ȡ�ø�context��ע���singleton proxy��</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
class InheritableListableBeanFactory extends DefaultListableBeanFactory {
    private final Map<Class<?>, Object> parentResolvableDependencies;

    InheritableListableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
        Map<Class<?>, Object> resolvableDependencies = getResolvableDependencies(this);
        Map<Class<?>, Object> parentResolvableDependencies = getResolvableDependencies(parentBeanFactory);

        if (parentResolvableDependencies != null) {
            if (resolvableDependencies != null) {
                resolvableDependencies.putAll(parentResolvableDependencies);
            }

            this.parentResolvableDependencies = unmodifiableMap(parentResolvableDependencies);
        } else {
            this.parentResolvableDependencies = null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Class<?>, Object> getResolvableDependencies(BeanFactory parentBeanFactory) {
        if (parentBeanFactory instanceof DefaultListableBeanFactory) {
            try {
                Field field = DefaultListableBeanFactory.class.getDeclaredField("resolvableDependencies");
                field.setAccessible(true);
                return (Map<Class<?>, Object>) field.get(parentBeanFactory);
            } catch (Exception e) {
                logger.warn("Failed to get value of DefaultListableBeanFactory.resolvableDependencies, "
                        + "autowiring of singleton proxy may function wrong", e);
            }
        }

        return null;
    }

    /**
     * �����ظ�ע��parent�����еĶ��󣬵����Ը��ǵ�ǰcontext�еĶ���
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void registerResolvableDependency(Class dependencyType, Object autowiredValue) {
        if (parentResolvableDependencies == null || !parentResolvableDependencies.containsKey(dependencyType)) {
            super.registerResolvableDependency(dependencyType, autowiredValue);
        }
    }
}
