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
package com.alibaba.citrus.turbine.dataresolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ������ʶһ��������ʹ֮��request parameters��ȡ��ֵ��
 * <p>
 * �÷����£�
 * </p>
 * <ol>
 * <li>��ָ���������ƣ�<code>@Param("name")</code>��</li>
 * <li>ָ���������ƣ��Լ�����Ĭ��ֵ��<code>@Param(name="name", defaultValue="123")</code>��</li>
 * <li>ָ���������ƣ��Լ�һ��Ĭ��ֵ��
 * <code>@Param(name="name", defaultValues={"1", "2", "3"})</code>��</li>
 * </ol>
 * 
 * @author Michael Zhou
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface Param {
    /**
     * ���ڱ�ʶparam�����ơ�
     * <p>
     * �˲������ڼ򻯵���ʽ��<code>@Param("paramName")</code>��
     * </p>
     */
    String value() default "";

    /**
     * ���ڱ�ʶparam�����ơ�
     * <p>
     * �˲��������ж����������ʽ��<code>@Param(name="paramName", defaultValue="123")</code>��
     * </p>
     */
    String name() default "";

    /**
     * ָ��������Ĭ��ֵ��
     */
    String defaultValue() default "";

    /**
     * ָ��������Ĭ��ֵ���顣
     */
    String[] defaultValues() default {};
}
