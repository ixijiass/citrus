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
package com.alibaba.citrus.springext.support;

import static com.alibaba.citrus.generictype.TypeInfoUtil.*;

/**
 * ͨ��generic������ȡ��beanInterface��bean���ࡣ
 * <p>
 * ��Ҫע����ǣ�����generic������һ�������ܿ���������Ӧ�ñ��⽫�˻������ڷ�singleton�Ķ���
 * </p>
 */
public class GenericBeanSupport<T> extends BeanSupport {
    @Override
    protected final Class<?> resolveBeanInterface() {
        return resolveParameter(getClass(), GenericBeanSupport.class, 0).getRawType();
    }
}
