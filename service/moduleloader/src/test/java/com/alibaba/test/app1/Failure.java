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
package com.alibaba.test.app1;

import static com.alibaba.citrus.util.Assert.*;

import org.springframework.beans.factory.InitializingBean;

import com.alibaba.citrus.service.moduleloader.Module;

public class Failure implements InitializingBean, Module {
    public void afterPropertiesSet() throws Exception {
        assertTrue(false);
    }

    public void execute() throws Exception {
    }
}
