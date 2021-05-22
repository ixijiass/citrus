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

/**
 * �������ݽ������Ĺ�����
 * 
 * @author Michael Zhou
 */
public interface DataResolverFactory {
    /**
     * ȡ��ָ��generic���͡�ָ��annotations�Ĳ�����property�����ݽ�������
     * <p>
     * ���統ǰfactory���ܽ���ָ�������ͺ�annotation���򷵻�<code>null</code>��
     * <code>DataResolverService</code>�᳢����һ��factory��ֱ���ҵ����ʵ�Ϊֹ��
     * </p>
     */
    DataResolver getDataResolver(DataResolverContext context);
}
