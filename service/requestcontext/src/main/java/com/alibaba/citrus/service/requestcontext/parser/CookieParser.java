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
package com.alibaba.citrus.service.requestcontext.parser;

/**
 * <code>CookieParser</code>���������������HTTP�����е�cookies�Ľӿڡ�
 * <p>
 * ע�⣺<code>CookieParser</code>��Զʹ��<code>ISO-8859-1</code>����������cookie�����ƺ�ֵ��
 * </p>
 * 
 * @author Michael Zhou
 */
public interface CookieParser extends ValueParser {
    int AGE_SESSION = -1;
    int AGE_DELETE = 0;

    /**
     * ����session cookie��
     */
    void setCookie(String name, String value);

    /**
     * Set a persisten cookie on the client that will expire after a maximum age
     * (given in seconds).
     */
    void setCookie(String name, String value, int seconds_age);

    /**
     * Remove a previously set cookie from the client machine.
     */
    void removeCookie(String name);
}
