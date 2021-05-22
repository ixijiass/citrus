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
 * ������ʶһ��������ʹ֮��form service��ȡ��һ��<code>Group</code>�е�ͬ��<code>Field</code>s����
 * 
 * @author Michael Zhou
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface FormFields {
    /**
     * ���ڱ�ʶfield�����ơ�
     */
    String name();

    /**
     * ���ڱ�ʶgroup�����ơ�
     */
    String group();

    /**
     * �����δ��֤ͨ����������ģ���ִ�С�Ĭ��Ϊ<code>true</code>��
     * <p>
     * ע�⣬ֻ���ض���ģ�飨action�����Ա����������������ģ�飬�˲�����Ч��
     * </p>
     */
    boolean skipIfInvalid() default true;
}
