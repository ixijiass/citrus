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
package com.alibaba.citrus.service.moduleloader;

import java.util.Set;

/**
 * ����װ��module�ķ���
 * <p>
 * һ��module��web����У��ɱ���������չ��controllerģ�塣
 * </p>
 * 
 * @author Michael Zhou
 */
public interface ModuleLoaderService {
    /**
     * ȡ�õ�ǰfactory��֧�ֵ�����module���͡�
     */
    Set<String> getModuleTypes();

    /**
     * ȡ��ָ��module���͵�����module���ơ�
     */
    Set<String> getModuleNames(String moduleType);

    /**
     * ȡ��ָ�����ƺ����͵�moduleʵ����
     */
    Module getModule(String moduleType, String moduleName) throws ModuleLoaderException, ModuleNotFoundException;

    /**
     * ȡ��ָ�����ƺ����͵�moduleʵ����
     * <p>
     * ��<code>getModule()</code>��ͬ���ǣ��÷��������׳�<code>ModuleNotFoundException</code>
     * �����ģ�鲻���ڣ��򷵻�<code>null</code>��
     * </p>
     */
    Module getModuleQuiet(String moduleType, String moduleName) throws ModuleLoaderException;
}
