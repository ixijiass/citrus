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
package com.alibaba.citrus.service.velocity;

import java.io.IOException;

import org.springframework.core.io.Resource;

/**
 * Plugin是一种机制，用来对velocity engine做一些额外的扩展。
 * <p>
 * Plugin不一定要实现这个<code>VelocityPlugin</code>接口。Plugin还可以实现Velocity的
 * <code>EventHandler</code>接口，以便处理velocity事件。
 * </p>
 * 
 * @author Michael Zhou
 */
public interface VelocityPlugin {
    /**
     * 初始化plugin。
     * <p>
     * Plugin可以在这里修改velocity engine的properties。
     * </p>
     */
    void init(VelocityConfiguration configuration) throws Exception;

    /**
     * Plugin可以提供额外的macros。
     */
    Resource[] getMacros() throws IOException;
}
