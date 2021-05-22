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
package com.alibaba.citrus.springext.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * һ��ͨ�û��࣬����ʵ��contribution�ࡣ�������������ԣ�
 * <ul>
 * <li>����ɸ���<code>resolveBeanInterface()</code>��������ȡ�ýӿڡ�</li>
 * <li>ʵ��Ĭ�ϵ�<code>toString()</code>������</li>
 * <li>ʵ����spring�ĳ�ʼ�������ٵ������ڷ�����</li>
 * <li>��ȡ����spring��ע���bean name��</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public abstract class BeanSupport implements InitializingBean, DisposableBean, BeanNameAware {
    private Class<?> beanInterface;
    private String beanName;
    private boolean initialized;

    /**
     * ȡ��bean�ӿڡ�
     */
    public final Class<?> getBeanInterface() {
        if (beanInterface == null) {
            beanInterface = resolveBeanInterface();
        }

        return beanInterface;
    }

    protected Class<?> resolveBeanInterface() {
        return getClass();
    }

    /**
     * �Ƿ��Ѿ���ʼ����
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * �������Ѿ�����ʼ�����������׳�<code>IllegalStateException</code>�쳣��
     */
    public void assertInitialized() {
        if (!initialized) {
            throw new IllegalStateException(String.format("Bean instance of %s has not been initialized yet.",
                    getBeanInterface().getName()));
        }
    }

    /**
     * ��ʼ��ǰִ�С�
     */
    protected void preInit() throws Exception {
    }

    /**
     * ��ʼ��bean��
     */
    protected void init() throws Exception {
    }

    /**
     * ��ʼ����ִ�С�
     */
    protected void postInit() throws Exception {
    }

    public final void afterPropertiesSet() throws Exception {
        preInit();
        init();
        initialized = true;
        postInit();
    }

    /**
     * ����bean��
     */
    protected void dispose() {
    }

    /**
     * ����ǰִ�С�
     */
    protected void preDispose() {
    }

    /**
     * ���ٺ�ִ�С�
     */
    protected void postDispose() {
    }

    public final void destroy() {
        preDispose();
        dispose();
        initialized = false;
        postDispose();
    }

    /**
     * ȡ��spring�����е�bean���ƣ������ڵ��ԡ�
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * ����spring�����е�bean���ƣ������ڵ��ԡ�
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * ת�����ַ�����
     */
    @Override
    public String toString() {
        return getBeanDescription();
    }

    /**
     * ȡ��bean��������<code>beanName + ":" + beanInterfaceName</code>��
     */
    protected String getBeanDescription() {
        return getBeanDescription(true);
    }

    /**
     * ȡ��bean��������<code>beanName + ":" + beanInterfaceName</code>��
     */
    protected String getBeanDescription(boolean simpleName) {
        String interfaceDesc = simpleName ? getBeanInterface().getSimpleName() : getBeanInterface().getName();
        return beanName == null || beanName.contains("(inner bean)") ? interfaceDesc : beanName + ":" + interfaceDesc;
    }
}
