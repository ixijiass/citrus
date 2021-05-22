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
package com.alibaba.citrus.test;

import static org.junit.Assert.*;

import java.io.File;

/**
 * ������ʼ�����Ի����ľ�̬�����ࡣ
 * 
 * @author Michael Zhou
 */
public class TestEnvStatic {
    private static final TestEnv env = new TestEnv().init();

    public static final File basedir = env.getBasedir();
    public static final File srcdir = env.getSrcdir();
    public static final File destdir = env.getDestdir();
    public static final File javaHome = TestUtil.getJavaHome();

    /**
     * ���絥Ԫ������û����ʽ�������κγ�������ô���������������ȷ�����Ի�������ʼ����
     */
    public static void init() {
        assertNotNull(env);
    }
}
