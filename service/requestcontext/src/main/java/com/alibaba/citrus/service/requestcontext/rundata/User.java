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
package com.alibaba.citrus.service.requestcontext.rundata;

/**
 * ����һ������WEBӦ�ó�����û���
 * 
 * @author Michael Zhou
 */
public interface User {
    /**
     * ȡ����������ͬ�û���ID��
     * 
     * @return �û�ID����ͬ�û���ID������ͬ
     */
    String getId();

    /**
     * �ж��û��Ƿ��Ѿ���¼��
     * 
     * @return ����û��Ѿ���¼���򷵻�<code>true</code>
     */
    boolean hasLoggedIn();

    /**
     * ȡ�ú��û��󶨵Ķ��󡣵��û�������ʱ�����籣����HTTP session�У������е�attributesҲ�������档
     * ���û����󱻻ָ�ʱ�����е�attributesҲ�����ָ���
     * 
     * @param key �����key
     * @return ��key���Ӧ�Ķ���
     */
    Object getAttribute(String key);

    /**
     * ��ָ������󶨵��û������С����û�������ʱ�����籣����HTTP session�У������е�attributesҲ�������档
     * ���û����󱻻ָ�ʱ�����е�attributesҲ�����ָ���
     * 
     * @param key �����key
     * @param object ��key���Ӧ�Ķ���
     */
    void setAttribute(String key, Object object);
}
