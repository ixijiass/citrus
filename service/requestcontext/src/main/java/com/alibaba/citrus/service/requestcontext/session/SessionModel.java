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
package com.alibaba.citrus.service.requestcontext.session;

public interface SessionModel {
    /**
     * ȡ��session ID��
     * 
     * @return session ID
     */
    String getSessionID();

    /**
     * ȡ��session�Ĵ���ʱ�䡣
     * 
     * @return ����ʱ��¾
     */
    long getCreationTime();

    /**
     * ȡ���������ʱ�䡣
     * 
     * @return �������ʱ��¾
     */
    long getLastAccessedTime();

    /**
     * ȡ��session����󲻻���ޣ�������ʱ�䣬session�ͻ�ʧЧ��
     * 
     * @return ������޵�����
     */
    int getMaxInactiveInterval();

    /**
     * �ж�session��û�й��ڡ�
     * 
     * @return ��������ˣ��򷵻�<code>true</code>
     */
    boolean isExpired();

    /**
     * ��������session modelʵ���Ĺ�����
     */
    interface Factory {
        SessionModel newInstance(String sessionID, long creationTime, long lastAccessedTime, int maxInactiveInterval);
    }
}
