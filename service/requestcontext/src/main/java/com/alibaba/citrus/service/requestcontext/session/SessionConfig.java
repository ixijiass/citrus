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

/**
 * ����sessionϵͳ�����á�
 * 
 * @author Michael Zhou
 */
public interface SessionConfig {
    Integer MAX_INACTIVE_INTERVAL_DEFAULT = 0;
    Long FORCE_EXPIRATION_PERIOD_DEFAULT = 0L;
    String MODEL_KEY_DEFAULT = "SESSION_MODEL";
    Boolean KEEP_IN_TOUCH_DEFAULT = false;

    /**
     * Session������ʱ�䣨�룩�������û�������������ʱ�ޣ�session�������ϡ�ֵ<code>0</code>��ʾ�������ڡ�
     */
    int getMaxInactiveInterval();

    /**
     * Sessionǿ���������ޣ��룩�������û����񣬴�session����֮ʱ���𣬳���������ޣ�session����ǿ�����ϡ�ֵ
     * <code>0</code>��ʾ�������ϡ�
     */
    long getForceExpirationPeriod();

    /**
     * ����session model��session�б�����ļ�ֵ��Session
     * model�����ŵ�ǰsession��״̬����Ҳ��������session�С���store-mappings�����У�����԰�session
     * model���䵽һ��session store�С�
     */
    String getModelKey();

    /**
     * �Ƿ�ÿ������touch session�������Ϊ<code>false</code>��ֻ��sessionֵ�иı�ʱtouch������session
     * model������cookie store��ʱ�����������Լ���������
     */
    boolean isKeepInTouch();

    /**
     * ȡ��session ID�����á�
     */
    IdConfig getId();

    /**
     * ȡ������stores��
     */
    StoresConfig getStores();

    /**
     * ȡ������store mappings��
     */
    StoreMappingsConfig getStoreMappings();

    /**
     * ȡ��model encoders��
     */
    SessionModelEncoder[] getSessionModelEncoders();

    /**
     * ȡ����������session��Ϊ��interceptors��
     */
    SessionInterceptor[] getSessionInterceptors();

    /**
     * ����session ID�����á�
     */
    interface IdConfig {
        Boolean COOKIE_ENABLED_DEFAULT = true;
        Boolean URL_ENCODE_ENABLED_DEFAULT = false;

        /**
         * �Ƿ��session ID������cookie�У��������ǣ���ֻ�ܱ����URL�С�
         */
        boolean isCookieEnabled();

        /**
         * �Ƿ�֧�ְ�session ID������URL�С�
         */
        boolean isUrlEncodeEnabled();

        /**
         * ȡ��session ID cookie�����á�
         */
        CookieConfig getCookie();

        /**
         * ȡ��session ID URL encode�����á�
         */
        UrlEncodeConfig getUrlEncode();

        /**
         * ȡ��session ID��������
         */
        SessionIDGenerator getGenerator();
    }

    /**
     * ����cookie�����á�
     */
    interface CookieConfig {
        String COOKIE_NAME_DEFAULT = "JSESSIONID";
        String COOKIE_DOMAIN_DEFAULT = null;
        String COOKIE_PATH_DEFAULT = "/";
        Integer COOKIE_MAX_AGE_DEFAULT = 0;
        Boolean COOKIE_HTTP_ONLY_DEFAULT = true;
        Boolean COOKIE_SECURE_DEFAULT = false;

        /**
         * ȡ��cookie���ơ�
         */
        String getName();

        /**
         * ȡ��cookie��������ֵ<code>null</code>��ʾ���ݵ�ǰ�����Զ�����domain��
         */
        String getDomain();

        /**
         * ȡ��cookie��·����
         */
        String getPath();

        /**
         * Cookie������ʱ�䣨�룩��ֵ<code>0</code>��ʾ��ʱcookie����������Ĺرն���ʧ��
         */
        int getMaxAge();

        /**
         * ��cookie������httpOnly��ǡ���IE6�����°汾�У����Ի���XSS������Σ�ա�
         */
        boolean isHttpOnly();

        /**
         * ��cookie������secure��ǡ�ֻ��https��ȫ������ܷ��ʸ�cookie��
         */
        boolean isSecure();
    }

    /**
     * ����url encode�����á�
     */
    interface UrlEncodeConfig {
        String URL_ENCODE_NAME_DEFAULT = "JSESSIONID";

        /**
         * ȡ��URL encode�����ơ�
         */
        String getName();
    }

    /**
     * ����stores�����á�
     */
    interface StoresConfig {
        /**
         * ȡ�����е�session store�����ơ�
         */
        String[] getStoreNames();

        /**
         * ȡ��ָ�����ƵĶ�������ŵ�session store��
         */
        SessionStore getStore(String storeName);
    }

    /**
     * ����store mappings�����á�
     */
    interface StoreMappingsConfig {
        String MATCHES_ALL_ATTRIBUTES = "*";

        /**
         * ȡ��ָ��session attribute���ƵĶ�������ŵ�session store��
         */
        String getStoreNameForAttribute(String attrName);

        /**
         * ����ָ��store��������Ӧ�����о�ȷƥ���attribute���ơ�
         * <p>
         * ������ڷǾ�ȷƥ���attributes���򷵻�<code>null</code>��
         * </p>
         */
        String[] getExactMatchedAttributeNames(String storeName);
    }
}
