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
package com.alibaba.citrus.service.pull;

/**
 * ����һ��pull tools�Ĺ�����
 * <p>
 * �������������singleton����ȷ�����ܡ����Ǻ�<code>ToolSetFactory</code>��ͬ���ǣ�
 * <code>RuntimeToolSetFactory.getToolNames()</code>
 * ����������ϵͳ��ʼ��ʱ�����õģ�������ÿ�����������౻����һ�Ρ�
 * </p>
 * <p>
 * �����͵Ķ�������ܲ���<code>ToolSetFactory</code>���뾡��ʹ�ú��ߡ�
 * </p>
 * 
 * @see ToolFactory
 * @see ToolSetFactory
 * @author Michael Zhou
 */
public interface RuntimeToolSetFactory {
    /**
     * ȡ��toolsetʵ����
     * <p>
     * �÷�����ÿ������ʱ�����౻����һ�Ρ�
     * </p>
     * <p>
     * �緵��<code>null</code>�����ʾ��tool�����á�
     * </p>
     * <p>
     * ע�⣺ÿ�ε���<strong>����</strong>���ز�ͬ�Ķ���
     * </p>
     */
    Object createToolSet() throws Exception;

    /**
     * ȡ��tools�����ơ�
     * <p>
     * ��ÿ�������У��÷������ᱻ��������һ�Ρ�
     * </p>
     */
    Iterable<String> getToolNames(Object toolSet);

    /**
     * ȡ��ָ�����Ƶ�toolʵ����
     * <p>
     * ���ڷ�singleton���ͣ��÷�����ÿ������ʱ��ÿ��<code>name</code>���౻����һ�Ρ�
     * </p>
     * <p>
     * �緵��<code>null</code>�����ʾ��tool�����á�
     * </p>
     */
    Object createTool(Object toolSet, String name) throws Exception;
}
