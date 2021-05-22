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
package com.alibaba.citrus.service.resource;

/**
 * ����һ��<code>&lt;resource&gt;</code>��<code>&lt;resource-alias&gt;</code>��
 * <code>&lt;resource-filters&gt;</code>��ƥ������
 * 
 * @author Michael Zhou
 */
public interface ResourceMatchResult {
    /**
     * ȡ���Ѿ�ƥ������ơ�
     */
    String getResourceName();

    /**
     * ��ƥ�䲿���滻��ָ���ַ�����
     */
    String substitute(String substitution);
}
