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
 * Plugin��һ�ֻ��ƣ�������velocity engine��һЩ�������չ��
 * <p>
 * Plugin��һ��Ҫʵ�����<code>VelocityPlugin</code>�ӿڡ�Plugin������ʵ��Velocity��
 * <code>EventHandler</code>�ӿڣ��Ա㴦��velocity�¼���
 * </p>
 * 
 * @author Michael Zhou
 */
public interface VelocityPlugin {
    /**
     * ��ʼ��plugin��
     * <p>
     * Plugin�����������޸�velocity engine��properties��
     * </p>
     */
    void init(VelocityConfiguration configuration) throws Exception;

    /**
     * Plugin�����ṩ�����macros��
     */
    Resource[] getMacros() throws IOException;
}
