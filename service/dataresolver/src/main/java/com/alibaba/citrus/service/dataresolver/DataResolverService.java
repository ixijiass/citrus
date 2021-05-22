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
package com.alibaba.citrus.service.dataresolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * ����ȡ��<code>DataResolver</code>�ķ���
 * <p>
 * һ��<code>DataResolver</code>����ȡ��ָ�����ͻ�ָ��annotation����������ݡ�
 * <code>DataResolver</code>���������ʵ�������ע�뵽�����Ĳ����������property�С�
 * </p>
 * 
 * @author Michael Zhou
 */
public interface DataResolverService {
    /**
     * ȡ��ָ��generic���͡�ָ��annotations�Ĳ�����property�����ݽ�������
     */
    DataResolver getDataResolver(Type type, Annotation[] annotations, Object... extraInfo)
            throws DataResolverNotFoundException;

    /**
     * ȡ��ָ�������Ĳ������͵����ݽ�������
     */
    DataResolver[] getParameterResolvers(Method method, Object... extraInfo) throws DataResolverNotFoundException;
}
