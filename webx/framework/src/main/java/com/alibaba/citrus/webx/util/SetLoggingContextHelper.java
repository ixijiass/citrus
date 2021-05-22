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
package com.alibaba.citrus.webx.util;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static java.util.Collections.*;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

/**
 * ���û����logging MDC�Ĺ����ࡣ
 * <p>
 * �ù�����ɱ�����valve��filter�С�������ʼ��ʱ�򣬵���<code>setLoggingContext()</code>������ʱ����
 * <code>clearLoggingContext()</code>�� ����<code>clearLoggingContext()</code>
 * ֮ǰ����ε���<code>setLoggingContext()</code>���������Ӷ���Ŀ�����
 * </p>
 * <p>
 * ����<code>setLoggingContext()</code>֮��SLF4j
 * MDC�лᴴ������ֵ����Щֵ����logback��log4j�����ļ���ֱ�����á�
 * </p>
 * <table border="1" cellpadding="5">
 * <tr>
 * <td colspan="2"><strong>������Ϣ</strong></td>
 * </tr>
 * <tr>
 * <td>%X{method}</td>
 * <td>�������ͣ�GET��POST</td>
 * </tr>
 * <tr>
 * <td>%X{requestURL}</td>
 * <td>������URL</td>
 * </tr>
 * <tr>
 * <td>%X{requestURLWithQueryString}</td>
 * <td>������URL����querydata</td>
 * </tr>
 * <tr>
 * <td>%X{requestURI}</td>
 * <td>������host��Ϣ��URL</td>
 * </tr>
 * <tr>
 * <td>%X{requestURIWithQueryString}</td>
 * <td>������host��Ϣ��URL����querydata</td>
 * </tr>
 * <tr>
 * <td>%X{queryString}</td>
 * <td>Querydata</td>
 * </tr>
 * <tr>
 * <td>%X{cookies}</td>
 * <td>����cookie�����ƣ��Զ��ŷָ�</td>
 * </tr>
 * <tr>
 * <td>%X{cookie.*}</td>
 * <td>ָ��cookie��ֵ�����磺cookie.JSESSIONID</td>
 * </tr>
 * <tr>
 * <td colspan="2"><strong>�ͻ�����Ϣ</strong></td>
 * </tr>
 * <tr>
 * <td>%X{remoteAddr}</td>
 * <td>�û�IP��ַ</td>
 * </tr>
 * <tr>
 * <td>%X{remoteHost}</td>
 * <td>�û�������Ҳ������IP��ַ��</td>
 * </tr>
 * <tr>
 * <td>%X{userAgent}</td>
 * <td>�û������</td>
 * </tr>
 * <tr>
 * <td>%X{referrer}</td>
 * <td>��һ������</td>
 * </tr>
 * </table>
 * 
 * @author Michael Zhou
 */
public class SetLoggingContextHelper {
    public static final String MDC_METHOD = "method";
    public static final String MDC_REQUEST_URL = "requestURL";
    public static final String MDC_REQUEST_URL_WITH_QUERY_STRING = "requestURLWithQueryString";
    public static final String MDC_REQUEST_URI = "requestURI";
    public static final String MDC_REQUEST_URI_WITH_QUERY_STRING = "requestURIWithQueryString";
    public static final String MDC_QUERY_STRING = "queryString";
    public static final String MDC_REMOTE_ADDR = "remoteAddr";
    public static final String MDC_REMOTE_HOST = "remoteHost";
    public static final String MDC_USER_AGENT = "userAgent";
    public static final String MDC_REFERRER = "referrer";
    public static final String MDC_COOKIES = "cookies";
    public static final String MDC_COOKIE_PREFIX = "cookie.";
    private static final String FLAG_MDC_HAS_ALREADY_SET = "_flag_mdc_has_already_set";
    private final HttpServletRequest request;

    public SetLoggingContextHelper(HttpServletRequest request) {
        this.request = assertNotNull(request, "request");
    }

    /**
     * ����MDC��
     */
    public void setLoggingContext() {
        if (testAndSet()) {
            Map<String, String> mdc = getMDCCopy();

            populateMDC(mdc);
            setMDC(mdc);
        }
    }

    /**
     * ���MDC��
     * <p>
     * ֻ�е�ǰ�����Լ����õ�MDC���ܱ������
     * </p>
     */
    public void clearLoggingContext() {
        if (this == request.getAttribute(FLAG_MDC_HAS_ALREADY_SET)) {
            request.removeAttribute(FLAG_MDC_HAS_ALREADY_SET);
            clearMDC();
        }
    }

    protected void populateMDC(Map<String, String> mdc) {
        // GET or POST
        putMDC(mdc, MDC_METHOD, request.getMethod());

        // request URL��������URL
        StringBuffer requestURL = request.getRequestURL();
        String queryString = trimToNull(request.getQueryString());

        putMDC(mdc, MDC_REQUEST_URL, getRequestURL(requestURL, null));
        putMDC(mdc, MDC_REQUEST_URL_WITH_QUERY_STRING, getRequestURL(requestURL, queryString));

        // request URI��������host��Ϣ��URL
        String requestURI = request.getRequestURI();
        String requestURIWithQueryString = queryString == null ? requestURI : requestURI + "?" + queryString;

        putMDC(mdc, MDC_REQUEST_URI, requestURI);
        putMDC(mdc, MDC_REQUEST_URI_WITH_QUERY_STRING, requestURIWithQueryString);
        putMDC(mdc, MDC_QUERY_STRING, queryString);

        // client info
        putMDC(mdc, MDC_REMOTE_HOST, request.getRemoteHost());
        putMDC(mdc, MDC_REMOTE_ADDR, request.getRemoteAddr());

        // user agent
        putMDC(mdc, MDC_USER_AGENT, request.getHeader("User-Agent"));

        // referrer
        putMDC(mdc, MDC_REFERRER, request.getHeader("Referer"));

        // cookies
        Cookie[] cookies = request.getCookies();
        List<String> names = emptyList();

        if (cookies != null) {
            names = createArrayList(cookies.length);

            for (Cookie cookie : cookies) {
                names.add(cookie.getName());
                putMDC(mdc, MDC_COOKIE_PREFIX + cookie.getName(), cookie.getValue());
            }

            sort(names);
        }

        putMDC(mdc, MDC_COOKIES, names.toString());
    }

    private boolean testAndSet() {
        if (request.getAttribute(FLAG_MDC_HAS_ALREADY_SET) == null) {
            request.setAttribute(FLAG_MDC_HAS_ALREADY_SET, this);
            return true;
        }

        return false;
    }

    /**
     * ȡ�õ�ǰ��request URL������query string��
     * 
     * @param withQueryString �Ƿ����query string
     * @return ��ǰ�����request URL
     */
    private String getRequestURL(StringBuffer requestURL, String queryString) {
        int length = requestURL.length();

        try {
            if (queryString != null) {
                requestURL.append('?').append(queryString);
            }

            return requestURL.toString();
        } finally {
            requestURL.setLength(length);
        }
    }

    /**
     * ����mdc�����valueΪ�գ������롣
     */
    private void putMDC(Map<String, String> mdc, String key, String value) {
        if (value != null) {
            mdc.put(key, value);
        }
    }

    /**
     * ȡ�õ�ǰMDC map�ĸ�����
     */
    @SuppressWarnings("unchecked")
    protected Map<String, String> getMDCCopy() {
        Map<String, String> mdc = MDC.getCopyOfContextMap();

        if (mdc == null) {
            mdc = createHashMap();
        }

        return mdc;
    }

    /**
     * ��map�е�ֵ���õ�MDC�С�
     */
    protected void setMDC(Map<String, String> mdc) {
        MDC.setContextMap(mdc);
    }

    /**
     * ����MDC��
     */
    protected void clearMDC() {
        MDC.clear();
    }
}
