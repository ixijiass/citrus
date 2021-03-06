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
 * 用来创建modules的工厂。
 * 
 * @author Michael Zhou
 */
public interface ModuleFactory {
    /**
     * 取得当前factory所支持的所有module类型。
     */
    Set<String> getModuleTypes();

    /**
     * 取得指定module类型的所有module名称。
     */
    Set<String> getModuleNames(String moduleType);

    /**
     * 取得指定名称和类型的module实例。
     * <p>
     * 如果未找到，则返回<code>null</code>。
     * </p>
     */
    Object getModule(String moduleType, String moduleName) throws ModuleLoaderException;
}
