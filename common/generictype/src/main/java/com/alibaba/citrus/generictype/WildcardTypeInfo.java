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
package com.alibaba.citrus.generictype;

import java.lang.reflect.WildcardType;

/**
 * ��{@link WildcardType}��Ӧ������һ��ͨ������ͱ�������Ϣ��
 * 
 * @author Michael Zhou
 */
public interface WildcardTypeInfo extends BoundedTypeInfo {
    /**
     * �ж�wildcard�Ƿ�Ϊ��unknown wildcard����
     * <p>
     * Wildcard�����֣�
     * </p>
     * <ol>
     * <li>һ���ǡ�unknown wildcard���ģ����磺<code>Collection&lt;?&gt;</code>
     * ����˼Ϊ��Collection of Unknown������֮����<code>?</code>������ƥ���������͡�</li>
     * <li>��һ���ǡ�bounded wildcard�������磺
     * <code>Collection&lt;? extends Number&gt;</code>��</li>
     * </ol>
     * <p>
     * ����unknown wildcard������JDK�ṩ��API����Ȼ������upper boundΪ<code>Object</code>
     * ��Ȼ������ʵ�ϵ�upper boundȡ���ڶ�Ӧ��<code>TypeVariable</code>��upper bound��
     */
    boolean isUnknown();
}
