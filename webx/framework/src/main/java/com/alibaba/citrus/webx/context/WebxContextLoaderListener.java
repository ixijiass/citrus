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
package com.alibaba.citrus.webx.context;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

/**
 * ��������root context��listener��
 * <p>
 * ��Spring {@link ContextLoaderListener}���ƣ�listener����ȡ
 * <code>/WEB-INF/web.xml</code>��context param <code>contextClass</code>
 * ��ָ������������Ϊroot <code>ApplicationContext</code>��ʵ���ࡣ����δ��ȷָ������ʹ��Ĭ��ֵ
 * {@link WebxApplicationContext}��
 * </p>
 * <p>
 * Ĭ��ֵ����ͨ������<code>getDefaultContextClass()</code>���ı䡣
 * </p>
 * 
 * @author Michael Zhou
 */
public class WebxContextLoaderListener extends ContextLoaderListener {
    @Override
    protected final ContextLoader createContextLoader() {
        return new WebxComponentsLoader() {

            @Override
            protected Class<?> getDefaultContextClass() {
                Class<?> defaultContextClass = WebxContextLoaderListener.this.getDefaultContextClass();

                if (defaultContextClass == null) {
                    defaultContextClass = super.getDefaultContextClass();
                }

                return defaultContextClass;
            }
        };
    }

    protected Class<?> getDefaultContextClass() {
        return null;
    }
}
