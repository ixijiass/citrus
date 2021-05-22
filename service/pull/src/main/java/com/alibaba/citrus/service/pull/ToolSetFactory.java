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
 * �������������singleton����ȷ�����ܡ�
 * </p>
 * <ul>
 * <li><code>getToolNames()</code>����������ϵͳ��ʼ��ʱ�����ã�Pre-queueing����</li>
 * <li>��<code>isSingleton() == true</code> ʱ��<code>createTool(name)</code>
 * ��������ϵͳ��ʼ��ʱ�����ã�Pre-pulling����</li>
 * <li>��<code>isSingleton() == false</code> ʱ��ÿһ�����󣬶���ÿ��<code>name</code>��
 * <code>createTool(name)</code> �������౻����һ�Ρ�</li>
 * </ul>
 * 
 * @see ToolFactory
 * @author Michael Zhou
 */
public interface ToolSetFactory {
    /**
     * Factory��������tool�ǲ���singleton��
     */
    boolean isSingleton();

    /**
     * ȡ��tools�����ơ�
     * <p>
     * ��ϵͳ��ʼ��ʱ���÷����ᱻ����һ�Σ��Ժ��ٵ��á�
     * </p>
     */
    Iterable<String> getToolNames();

    /**
     * ȡ��ָ�����Ƶ�toolʵ����
     * <p>
     * ���ڷ�singleton���ͣ��÷�����ÿ������ʱ��ÿ��<code>name</code>���౻����һ�Ρ�
     * </p>
     * <p>
     * �緵��<code>null</code>�����ʾ��tool�����á�
     * </p>
     * <p>
     * ע�⣺���ڷ�singleton���ͣ�<strong>����</strong>ÿ�η��ز�ͬ�Ķ���
     * </p>
     */
    Object createTool(String name) throws Exception;
}
