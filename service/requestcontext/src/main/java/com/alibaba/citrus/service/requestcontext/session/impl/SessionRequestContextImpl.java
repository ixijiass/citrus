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
package com.alibaba.citrus.service.requestcontext.session.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.session.SessionConfig;
import com.alibaba.citrus.service.requestcontext.session.SessionConfig.CookieConfig;
import com.alibaba.citrus.service.requestcontext.session.SessionRequestContext;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestContextWrapper;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestWrapper;
import com.alibaba.citrus.service.requestcontext.support.AbstractResponseWrapper;
import com.alibaba.citrus.service.requestcontext.util.CookieSupport;
import com.alibaba.citrus.util.StringUtil;

/**
 * ֧��session��<code>HttpRequestContext</code>ʵ�֡�
 */
public class SessionRequestContextImpl extends AbstractRequestContextWrapper implements SessionRequestContext {
    private final static Logger log = LoggerFactory.getLogger(SessionRequestContext.class);
    private SessionConfig sessionConfig;
    private boolean requestedSessionIDParsed;
    private String requestedSessionID;
    private boolean requestedSessionIDFromCookie;
    private boolean requestedSessionIDFromURL;
    private SessionImpl session;
    private boolean sessionReturned;

    /**
     * ���캯����
     */
    public SessionRequestContextImpl(RequestContext wrappedContext, SessionConfig sessionConfig) {
        super(wrappedContext);
        this.sessionConfig = sessionConfig;
        setRequest(new SessionRequestWrapper(wrappedContext.getRequest()));
        setResponse(new SessionResponseWrapper(wrappedContext.getResponse()));
    }

    /**
     * ȡ��<code>SessionConfig</code>ʵ����
     * 
     * @return <code>SessionConfig</code>ʵ��
     */
    public SessionConfig getSessionConfig() {
        return sessionConfig;
    }

    /**
     * �ж�session�Ƿ��Ѿ����ϡ�
     * 
     * @return �������ϣ��򷵻�<code>true</code>
     */
    public boolean isSessionInvalidated() {
        return session == null ? false : session.isInvalidated();
    }

    /**
     * ���session������<code>invalidate()</code>��������֧�ֺ����������������׳�
     * <code>IllegalStateException</code>��
     */
    public void clear() {
        if (session != null) {
            session.clear();
        }
    }

    /**
     * ȡ�õ�ǰ��session ID��
     * 
     * @return session ID
     */
    public String getRequestedSessionID() {
        ensureRequestedSessionID();
        return requestedSessionID;
    }

    /**
     * ��ǰ��session ID�Ǵ�cookie��ȡ�õ���
     * 
     * @return ����ǣ��򷵻�<code>true</code>
     */
    public boolean isRequestedSessionIDFromCookie() {
        ensureRequestedSessionID();
        return requestedSessionIDFromCookie;
    }

    /**
     * ��ǰ��session ID�Ǵ�URL��ȡ�õ���
     * 
     * @return ����ǣ��򷵻�<code>true</code>
     */
    public boolean isRequestedSessionIDFromURL() {
        ensureRequestedSessionID();
        return requestedSessionIDFromURL;
    }

    /**
     * �жϵ�ǰ��session ID�Ƿ���Ȼ�Ϸ���
     * 
     * @return ����ǣ��򷵻�<code>true</code>
     */
    public boolean isRequestedSessionIDValid() {
        HttpSession session = getSession(false);

        return session != null && session.getId().equals(requestedSessionID);
    }

    /**
     * ȷ��session ID�Ѿ���request�б����������ˡ�
     */
    private void ensureRequestedSessionID() {
        if (!requestedSessionIDParsed) {
            if (sessionConfig.getId().isCookieEnabled()) {
                requestedSessionID = decodeSessionIDFromCookie();
                requestedSessionIDFromCookie = requestedSessionID != null;
            }

            if (requestedSessionID == null && sessionConfig.getId().isUrlEncodeEnabled()) {
                requestedSessionID = decodeSessionIDFromURL();
                requestedSessionIDFromURL = requestedSessionID != null;
            }
        }
    }

    /**
     * ��session ID���뵽Cookie��ȥ��
     */
    public void encodeSessionIDIntoCookie() {
        writeSessionIDCookie(session.getId());
    }

    /**
     * ��session ID��Cookie��ɾ����
     */
    public void clearSessionIDFromCookie() {
        writeSessionIDCookie("");
    }

    /**
     * дcookie��
     */
    private void writeSessionIDCookie(String cookieValue) {
        CookieConfig cookieConfig = sessionConfig.getId().getCookie();
        CookieSupport cookie = new CookieSupport(cookieConfig.getName(), cookieValue);
        String cookieDomain = cookieConfig.getDomain();

        if (!StringUtil.isEmpty(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }

        String cookiePath = cookieConfig.getPath();

        if (!StringUtil.isEmpty(cookiePath)) {
            cookie.setPath(cookiePath);
        }

        int cookieMaxAge = cookieConfig.getMaxAge();

        if (cookieMaxAge > 0) {
            cookie.setMaxAge(cookieMaxAge);
        }

        cookie.setHttpOnly(cookieConfig.isHttpOnly());
        cookie.setSecure(cookieConfig.isSecure());

        log.debug("Set-cookie: {}", cookie);

        cookie.addCookie(getResponse());
    }

    /**
     * ��cookie��ȡ��session ID��
     * 
     * @return ������ڣ��򷵻�session ID�����򷵻�<code>null</code>
     */
    public String decodeSessionIDFromCookie() {
        Cookie[] cookies = getRequest().getCookies();

        if (cookies != null) {
            String sessionCookieName = sessionConfig.getId().getCookie().getName();

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(sessionCookieName)) {
                    String sessionID = StringUtil.trimToNull(cookie.getValue());

                    if (sessionID != null) {
                        return sessionID;
                    }
                }
            }
        }

        return null;
    }

    /**
     * ��session ID���뵽URL��ȥ��
     * 
     * @return ����session ID��URL
     */
    public String encodeSessionIDIntoURL(String url) {
        HttpSession session = getRequest().getSession(false);

        if (session != null && (session.isNew() || isRequestedSessionIDFromURL() && !isRequestedSessionIDFromCookie())) {
            String sessionID = session.getId();
            String keyName = getSessionConfig().getId().getUrlEncode().getName();
            int keyNameLength = keyName.length();
            int urlLength = url.length();
            int urlQueryIndex = url.indexOf('?');

            if (urlQueryIndex >= 0) {
                urlLength = urlQueryIndex;
            }

            boolean found = false;

            for (int keyBeginIndex = url.indexOf(';'); keyBeginIndex >= 0 && keyBeginIndex < urlLength; keyBeginIndex = url
                    .indexOf(';', keyBeginIndex + 1)) {
                keyBeginIndex++;

                if (urlLength - keyBeginIndex <= keyNameLength
                        || !url.regionMatches(keyBeginIndex, keyName, 0, keyNameLength)
                        || url.charAt(keyBeginIndex + keyNameLength) != '=') {
                    continue;
                }

                int valueBeginIndex = keyBeginIndex + keyNameLength + 1;
                int valueEndIndex = url.indexOf(';', valueBeginIndex);

                if (valueEndIndex < 0) {
                    valueEndIndex = urlLength;
                }

                if (!url.regionMatches(valueBeginIndex, sessionID, 0, sessionID.length())) {
                    url = url.substring(0, valueBeginIndex) + sessionID + url.substring(valueEndIndex);
                }

                found = true;
                break;
            }

            if (!found) {
                url = url.substring(0, urlLength) + ';' + keyName + '=' + sessionID + url.substring(urlLength);
            }
        }

        return url;
    }

    /**
     * ��URL��ȡ��session ID��
     * 
     * @return ������ڣ��򷵻�session ID�����򷵻�<code>null</code>
     */
    public String decodeSessionIDFromURL() {
        String uri = getRequest().getRequestURI();
        String keyName = sessionConfig.getId().getUrlEncode().getName();
        int uriLength = uri.length();
        int keyNameLength = keyName.length();

        for (int keyBeginIndex = uri.indexOf(';'); keyBeginIndex >= 0; keyBeginIndex = uri.indexOf(';',
                keyBeginIndex + 1)) {
            keyBeginIndex++;

            if (uriLength - keyBeginIndex <= keyNameLength
                    || !uri.regionMatches(keyBeginIndex, keyName, 0, keyNameLength)
                    || uri.charAt(keyBeginIndex + keyNameLength) != '=') {
                continue;
            }

            int valueBeginIndex = keyBeginIndex + keyNameLength + 1;
            int valueEndIndex = uri.indexOf(';', valueBeginIndex);

            if (valueEndIndex < 0) {
                valueEndIndex = uriLength;
            }

            return uri.substring(valueBeginIndex, valueEndIndex);
        }

        return null;
    }

    /**
     * ȡ�õ�ǰ��session����������ڣ���<code>create</code>Ϊ<code>true</code>���򴴽�һ���µġ�
     * 
     * @param create ��Ҫʱ�Ƿ񴴽��µ�session
     * @return ��ǰ��session���µ�session����������ڣ���<code>create</code>Ϊ
     *         <code>false</code> ���򷵻�<code>null</code>
     */
    public HttpSession getSession(boolean create) {
        // ���getSession�����Ѿ���ִ�й��ˣ���ôֱ�ӷ���
        if (session != null && sessionReturned) {
            return session;
        }

        // ����session�������п��ܴ���ȴ������
        if (session == null) {
            // ��request��ȡ��session ID
            ensureRequestedSessionID();

            String sessionID = requestedSessionID;
            boolean isNew = false;

            // ���sessionIDΪ�գ��򴴽�һ���µ�ID
            if (sessionID == null) {
                if (!create) {
                    return null; // ����create=false��ֱ�ӷ���null
                }

                sessionID = sessionConfig.getId().getGenerator().generateSessionID();
                isNew = true;
            }

            // �����������ȴ���һ��session������˵�������session�п��ܲ����ڻ��ǹ��ڵ�
            session = new SessionImpl(sessionID, this, isNew, create);
        }

        // SessionΪnew���п�����sessionIDΪ�գ�����sessionID��Ӧ��session�����ڣ�����session�ѹ���
        // ���ͬʱcreateΪfalse������null�Ϳ����ˡ�
        if (session.isNew() && !create) {
            return null;
        }

        // ���ԭ��sessionID�Ѵ�����request�У�����session�Ƿ����½�������ø�sessionID��
        // ��ˣ�����������£��Ͳ���Ҫ������cookie�ˡ�
        if (sessionConfig.getId().isCookieEnabled() && !session.getId().equals(requestedSessionID)) {
            if (getResponse().isCommitted()) {
                throw new IllegalStateException(
                        "Failed to create a new session because the responseWrapper was already committed");
            }

            encodeSessionIDIntoCookie();
        }

        sessionReturned = true;
        return session;
    }

    /**
     * ��ʼһ������
     */
    @Override
    public void prepare() {
    }

    /**
     * ����һ������
     */
    @Override
    public void commit() {
        if (!sessionReturned) {
            return;
        }

        if (session.isInvalidated()) {
            // ���cookie�е�session ID
            clearSessionIDFromCookie();
        }

        session.commit();
    }

    /**
     * ֧��session��<code>HttpServletRequestWrapper</code>��
     */
    private class SessionRequestWrapper extends AbstractRequestWrapper {
        /**
         * ���캯����
         * 
         * @param request ����װ��<code>HttpServletRequest</code>ʵ��
         */
        public SessionRequestWrapper(HttpServletRequest request) {
            super(SessionRequestContextImpl.this, request);
        }

        /**
         * ȡ�õ�ǰrequest�е�session ID��
         * 
         * @return session ID
         */
        @Override
        public String getRequestedSessionId() {
            return SessionRequestContextImpl.this.getRequestedSessionID();
        }

        /**
         * ��ǰ��session ID�Ǵ�cookie��ȡ�õ���
         * 
         * @return ����ǣ��򷵻�<code>true</code>
         */
        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return SessionRequestContextImpl.this.isRequestedSessionIDFromCookie();
        }

        /**
         * ��ǰ��session ID�Ǵ�URL��ȡ�õ���
         * 
         * @return ����ǣ��򷵻�<code>true</code>
         */
        @Override
        public boolean isRequestedSessionIdFromURL() {
            return SessionRequestContextImpl.this.isRequestedSessionIDFromURL();
        }

        /**
         * �жϵ�ǰ��session ID�Ƿ���Ȼ�Ϸ���
         * 
         * @return ����ǣ��򷵻�<code>true</code>
         */
        @Override
        public boolean isRequestedSessionIdValid() {
            return SessionRequestContextImpl.this.isRequestedSessionIDValid();
        }

        /**
         * ȡ�õ�ǰ��session����������ڣ��򴴽�һ���µġ�
         * 
         * @return ��ǰ��session���µ�session
         */
        @Override
        public HttpSession getSession() {
            return getSession(true);
        }

        /**
         * ȡ�õ�ǰ��session����������ڣ���<code>create</code>Ϊ<code>true</code>���򴴽�һ���µġ�
         * 
         * @param create ��Ҫʱ�Ƿ񴴽��µ�session
         * @return ��ǰ��session���µ�session����������ڣ���<code>create</code>Ϊ
         *         <code>false</code>���򷵻�<code>null</code>
         */
        @Override
        public HttpSession getSession(boolean create) {
            return SessionRequestContextImpl.this.getSession(create);
        }

        /**
         * @deprecated use isRequestedSessionIdFromURL instead
         */
        @Override
        @Deprecated
        public boolean isRequestedSessionIdFromUrl() {
            return isRequestedSessionIdFromURL();
        }
    }

    /**
     * ֧��session��<code>HttpServletResponseWrapper</code>��
     */
    private class SessionResponseWrapper extends AbstractResponseWrapper {
        /**
         * ���캯����
         * 
         * @param response ����װ��<code>HttpServletResponse</code>ʵ��
         */
        public SessionResponseWrapper(HttpServletResponse response) {
            super(SessionRequestContextImpl.this, response);
        }

        /**
         * ��session ID���뵽URL�С�
         * 
         * @param url Ҫ�����URL
         * @return ����session ID��URL
         */
        @Override
        public String encodeURL(String url) {
            if (sessionConfig.getId().isUrlEncodeEnabled()) {
                url = SessionRequestContextImpl.this.encodeSessionIDIntoURL(url);
            }

            return url;
        }

        /**
         * ��session ID���뵽URL�С�
         * 
         * @param url Ҫ�����URL
         * @return ����session ID��URL
         */
        @Override
        public String encodeRedirectURL(String url) {
            if (sessionConfig.getId().isUrlEncodeEnabled()) {
                url = SessionRequestContextImpl.this.encodeSessionIDIntoURL(url);
            }

            return url;
        }

        /**
         * @deprecated use encodeURL instead
         */
        @Override
        @Deprecated
        public String encodeUrl(String url) {
            return encodeURL(url);
        }

        /**
         * @deprecated use encodeRedirectURL instead
         */
        @Override
        @Deprecated
        public String encodeRedirectUrl(String url) {
            return encodeRedirectURL(url);
        }
    }
}
