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
package com.alibaba.citrus.webx;

import org.springframework.web.context.WebApplicationContext;

import com.alibaba.citrus.webx.config.WebxConfiguration;

/**
 * ����һ��webx component����Ϣ��
 * 
 * @author Michael Zhou
 */
public interface WebxComponents extends Iterable<WebxComponent> {
    /**
     * ȡ������components���ơ�
     */
    String[] getComponentNames();

    /**
     * ȡ��ָ�����Ƶ�component��
     */
    WebxComponent getComponent(String componentName);

    /**
     * ȡ��Ĭ�ϵ�component�����δ���ã��򷵻�<code>null</code>��
     */
    WebxComponent getDefaultComponent();

    /**
     * ����ƥ���component��
     */
    WebxComponent findMatchedComponent(String path);

    /**
     * ȡ���������������controller��
     */
    WebxRootController getWebxRootController();

    /**
     * ȡ��webx configuration���á�
     */
    WebxConfiguration getParentWebxConfiguration();

    /**
     * ȡ������component�ĸ�application context���������û�У��򷵻�<code>null</code>��
     */
    WebApplicationContext getParentApplicationContext();
}
