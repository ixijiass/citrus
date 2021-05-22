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

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * �����־û��洢session attribute�Ļ��ơ�
 * 
 * @author Michael Zhou
 */
public interface SessionStore {
    /**
     * ��ʼ��SessionStore��
     */
    void init(String storeName, SessionConfig sessionConfig) throws Exception;

    /**
     * ȡ��ָ��session������attribute���ơ�
     * 
     * @param sessionID Ҫװ�ص�session ID
     * @param storeContext ����ȡ��request��Ϣ�������store��ǰ��״̬
     * @return attributes���б�
     */
    Iterable<String> getAttributeNames(String sessionID, StoreContext storeContext);

    /**
     * װ��ָ��session��ĳ��attribute��
     * 
     * @param attrName Ҫװ�ص�attribute����
     * @param sessionID Ҫ��ȡ��session ID
     * @param storeContext ����ȡ��request��Ϣ�������store��ǰ��״̬
     * @return attribute��ֵ��������ڵĻ���
     */
    Object loadAttribute(String attrName, String sessionID, StoreContext storeContext);

    /**
     * ����ָ��session ID���������ݡ�
     * 
     * @param sessionID Ҫ������session ID
     * @param storeContext ����ȡ��request��Ϣ�������store��ǰ��״̬
     */
    void invaldiate(String sessionID, StoreContext storeContext);

    /**
     * ����ָ��session��attributes��
     * 
     * @param attrs Ҫ�����attrs�����ֵΪ<code>null</code>��ʾɾ��
     * @param sessionID Ҫ�����sessionID
     * @param storeContext ����ȡ��request��Ϣ�������store��ǰ��״̬
     */
    void commit(Map<String, Object> attrs, String sessionID, StoreContext storeContext);

    /**
     * ����storeȡ�õ�ǰrequest����Ϣ������������Լ��ĵ�ǰ״̬��
     */
    interface StoreContext {
        /**
         * ȡ��store��request scope״̬���ݡ�
         * 
         * @return ״ֵ̬
         */
        Object getState();

        /**
         * ����store��request scope״̬���ݡ�
         * 
         * @param stateObject ״ֵ̬
         */
        void setState(Object stateObject);

        /**
         * ȡ��ָ�����Ƶ�store��״̬���ݡ�
         * 
         * @param storeName store����
         * @return ״ֵ̬
         */
        StoreContext getStoreContext(String storeName);

        /**
         * ȡ�õ�ǰ��request context��
         * 
         * @return <code>SessionRequestContext</code>����
         */
        SessionRequestContext getSessionRequestContext();

        /**
         * ȡ�õ�ǰ��session����
         * 
         * @return <code>HttpSession</code>����
         */
        HttpSession getHttpSession();
    }
}
