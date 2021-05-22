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
package com.alibaba.citrus.service.requestcontext.parser.impl;

import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.StringUtil.*;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.requestcontext.parser.AbstractValueParser;
import com.alibaba.citrus.service.requestcontext.parser.CookieParser;
import com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext;

/**
 * <code>CookieParser</code>���������������HTTP�����е�cookies�Ľӿڡ�
 * <p>
 * ע�⣺<code>CookieParser</code>��Զʹ��<code>ISO-8859-1</code>����������cookie�����ƺ�ֵ��
 * </p>
 * 
 * @author Michael Zhou
 */
public class CookieParserImpl extends AbstractValueParser implements CookieParser {
    private final static Logger log = LoggerFactory.getLogger(CookieParser.class);

    /**
     * ��request�д����µ�cookies��
     */
    public CookieParserImpl(ParserRequestContext requestContext) {
        super(requestContext);

        Cookie[] cookies = requestContext.getRequest().getCookies();

        if (cookies != null) {
            if (log.isDebugEnabled()) {
                log.debug("Number of Cookies " + cookies.length);
            }

            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();

                if (log.isDebugEnabled()) {
                    log.debug("Adding " + name + " = " + value);
                }

                add(name, value);
            }
        }
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    /**
     * Set a cookie that will be stored on the client for the duration of the
     * session.
     */
    public void setCookie(String name, String value) {
        setCookie(name, value, AGE_SESSION);
    }

    /**
     * Set a persisten cookie on the client that will expire after a maximum age
     * (given in seconds).
     */
    public void setCookie(String name, String value, int seconds_age) {
        Cookie cookie = new Cookie(name, value);

        // ����cookie����ʱ�䡢domain��path��
        cookie.setMaxAge(seconds_age);
        cookie.setDomain(getCookieDomain());
        cookie.setPath(getCookiePath());

        requestContext.getResponse().addCookie(cookie);
    }

    /**
     * ȡ��cookie��domain��
     * 
     * @return cookie��domain
     */
    protected String getCookieDomain() {
        String domain = defaultIfEmpty(requestContext.getRequest().getServerName(), EMPTY_STRING);
        String[] parts = split(domain, ".");
        int length = parts.length;

        if (length < 2) {
            return domain;
        } else {
            // ֻȡ��������֣��������ձ������
            return "." + parts[length - 2] + "." + parts[length - 1];
        }
    }

    /**
     * ȡ��cookie��path��
     * 
     * @return cookie��path
     */
    protected String getCookiePath() {
        return defaultIfEmpty(requestContext.getRequest().getContextPath(), "/");
    }

    /**
     * Remove a previously set cookie from the client machine.
     */
    public void removeCookie(String name) {
        setCookie(name, " ", AGE_DELETE);
    }
}
