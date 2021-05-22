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
package com.alibaba.citrus.service.requestcontext.rundata.impl;

import static com.alibaba.citrus.service.requestcontext.util.RequestContextUtil.*;
import static com.alibaba.citrus.util.Assert.*;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.buffered.BufferedRequestContext;
import com.alibaba.citrus.service.requestcontext.lazycommit.LazyCommitRequestContext;
import com.alibaba.citrus.service.requestcontext.locale.SetLocaleRequestContext;
import com.alibaba.citrus.service.requestcontext.parser.CookieParser;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext;
import com.alibaba.citrus.service.requestcontext.rundata.RunData;
import com.alibaba.citrus.service.requestcontext.rundata.User;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestContextWrapper;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.citrus.util.io.ByteArray;

/**
 * <code>RunData</code>��ʵ���ࡣ
 * 
 * @author Michael Zhou
 */
public class RunDataImpl extends AbstractRequestContextWrapper implements RunData {
    private final static Logger log = LoggerFactory.getLogger(RunData.class);
    private final BufferedRequestContext bufferedRequestContext;
    private final LazyCommitRequestContext lazyCommitRequestContext;
    private final SetLocaleRequestContext setLocaleRequestContext;
    private final ParserRequestContext parserRequestContext;
    private User user;

    public RunDataImpl(RequestContext wrappedContext) {
        super(wrappedContext);
        this.bufferedRequestContext = findRequestContext(wrappedContext, BufferedRequestContext.class);
        this.lazyCommitRequestContext = findRequestContext(wrappedContext, LazyCommitRequestContext.class);
        this.setLocaleRequestContext = findRequestContext(wrappedContext, SetLocaleRequestContext.class);
        this.parserRequestContext = findRequestContext(wrappedContext, ParserRequestContext.class);

        if (bufferedRequestContext == null) {
            log.debug("RunData feature BufferedRequestContext disabled");
        }

        if (lazyCommitRequestContext == null) {
            log.debug("RunData feature LazyCommitRequestContext disabled");
        }

        if (setLocaleRequestContext == null) {
            log.debug("RunData feature SetLocaleRequestContext disabled");
        }

        if (parserRequestContext == null) {
            log.debug("RunData feature ParserRequestContext disabled");
        }
    }

    protected BufferedRequestContext getBufferedRequestContext() {
        return assertNotNull(bufferedRequestContext, "Could not find BufferedRequestContext in request context chain");
    }

    protected LazyCommitRequestContext getLazyCommitRequestContext() {
        return assertNotNull(lazyCommitRequestContext,
                "Could not find LazyCommitRequestContext in request context chain");
    }

    protected SetLocaleRequestContext getSetLocaleRequestContext() {
        return assertNotNull(setLocaleRequestContext, "Could not find SetLocaleRequestContext in request context chain");
    }

    protected ParserRequestContext getParserRequestContext() {
        return assertNotNull(parserRequestContext, "Could not find ParserRequestContext in request context chain");
    }

    // ===================================================
    // HTTP request��Ϣ��
    // ===================================================

    /**
     * ȡ������query��������һ��ִ�д˷���ʱ���������request������ȡ�����еĲ�����
     * 
     * @return <code>ParameterParser</code>ʵ��
     */
    public ParameterParser getParameters() {
        return getParserRequestContext().getParameters();
    }

    /**
     * ȡ������cookie����һ��ִ�д˷���ʱ���������request������ȡ������cookies��
     * 
     * @return <code>CookieParser</code>ʵ��
     */
    public CookieParser getCookies() {
        return getParserRequestContext().getCookies();
    }

    /**
     * ȡ�õ�ǰ�����HTTP session��
     * 
     * @return HTTP session����
     */
    public HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * ȡ��webӦ�õ�������·�����൱��<code>HttpServletRequest.getContextPath</code>�����ص�ֵ��
     * 
     * @return webӦ�õ�������·��
     */
    public String getContextPath() {
        return getRequest().getContextPath();
    }

    /**
     * ȡ��servlet·�����൱��<code>HttpServletRequest.getServletPath</code>�����ص�ֵ��
     * 
     * @return servlet·��
     */
    public String getServletPath() {
        return getRequest().getServletPath();
    }

    /**
     * ȡ��path info·�����൱��<code>HttpServletRequest.getPathInfo</code>�����ص�ֵ��
     * 
     * @return path info·��
     */
    public String getPathInfo() {
        return getRequest().getPathInfo();
    }

    /**
     * ȡ�õ�ǰ��request URL������query string��
     * 
     * @return ��ǰ�����request URL
     */
    public String getRequestURL() {
        return getRequestURL(true);
    }

    /**
     * ȡ�õ�ǰ��request URL������query string��
     * 
     * @param withQueryString �Ƿ����query string
     * @return ��ǰ�����request URL
     */
    public String getRequestURL(boolean withQueryString) {
        StringBuffer buffer = getRequest().getRequestURL();

        if (withQueryString) {
            String queryString = StringUtil.trimToNull(getRequest().getQueryString());

            if (queryString != null) {
                buffer.append('?').append(queryString);
            }
        }

        return buffer.toString();
    }

    /**
     * �������������Ƿ�Ϊpost��
     */
    public boolean isPostRequest() {
        return "post".equalsIgnoreCase(getRequest().getMethod());
    }

    // ===================================================
    // ������Ϣ��
    // ===================================================

    /**
     * ȡ�����ڷ��ʵ�ǰӦ�õ��û���
     * 
     * @return �û�����
     */
    public User getUser() {
        return this.user;
    }

    /**
     * �������ڷ��ʵ�ǰӦ�õ��û���
     * 
     * @param user �û�����
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * ȡ�ú͵�ǰ����󶨵Ķ��󡣵��������ʱ�����е�attributes����������
     * 
     * @param key �����key
     * @return ��key���Ӧ�Ķ���
     */
    public Object getAttribute(String key) {
        return getRequest().getAttribute(key);
    }

    /**
     * ��ָ������󶨵���ǰ�����С����������ʱ�����е�attributes����������
     * 
     * @param key �����key
     * @param object ��key���Ӧ�Ķ���
     */
    public void setAttribute(String key, Object object) {
        if (object == null) {
            getRequest().removeAttribute(key);
        } else {
            getRequest().setAttribute(key, object);
        }
    }

    // ===================================================
    // HTTP response��Ϣ��
    // ===================================================

    /**
     * ȡ��content type��
     * 
     * @return content type������charset�Ķ���
     */
    public String getContentType() {
        return getSetLocaleRequestContext().getResponseContentType();
    }

    /**
     * ����content type�� ���content type������charset������
     * <code>getCharacterEncoding</code>�����ã������charset��ǡ�
     * 
     * @param contentType content type
     */
    public void setContentType(String contentType) {
        getResponse().setContentType(contentType);
    }

    /**
     * ����content type�� ���content type������charset������
     * <code>getCharacterEncoding</code>�����ã������charset��ǡ�
     * <p>
     * ���<code>appendCharset</code>Ϊ<code>false</code>����content
     * type�н�������charset��ǡ�
     * </p>
     * 
     * @param contentType content type
     * @param appendCharset ����ַ���
     */
    public void setContentType(String contentType, boolean appendCharset) {
        getSetLocaleRequestContext().setResponseContentType(contentType, appendCharset);
    }

    /**
     * ȡ��response������ַ�����
     */
    public String getCharacterEncoding() {
        return getResponse().getCharacterEncoding();
    }

    /**
     * ����response����ַ�����ע�⣬�˷��������ڵ�һ��<code>getWriter</code>֮ǰִ�С�
     * 
     * @param charset ����ַ��������charsetΪ<code>null</code>
     *            �����contentType��ɾ��charset���
     */
    public void setCharacterEncoding(String charset) {
        getSetLocaleRequestContext().setResponseCharacterEncoding(charset);
    }

    /**
     * ȡ���ض����URI��
     * 
     * @return �ض����URI�����û���ض����򷵻�<code>null</code>
     */
    public String getRedirectLocation() {
        return getLazyCommitRequestContext().getRedirectLocation();
    }

    /**
     * �����ض���URI��
     * 
     * @param location �ض����URI
     * @throws IOException �������ʧ��
     * @throws IllegalStateException ���response�Ѿ�committed
     */
    public void setRedirectLocation(String location) throws IOException {
        getResponse().sendRedirect(location);
    }

    /**
     * �о�ϵͳ�Ƿ��Ѿ��ض���
     * 
     * @return ���<code>setRedirectLocation</code>�����ã��򷵻�<code>true</code>
     */
    public boolean isRedirected() {
        return getLazyCommitRequestContext().isRedirected();
    }

    /**
     * ȡ��������õ�HTTP status��
     * 
     * @return HTTP statusֵ
     */
    public int getStatusCode() {
        return getLazyCommitRequestContext().getStatus();
    }

    /**
     * ����HTTP status��
     * 
     * @param status HTTP statusֵ
     */
    public void setStatusCode(int status) {
        getResponse().setStatus(status);
    }

    // ===================================================
    // Response buffer���ơ�
    // ===================================================

    /**
     * �����Ƿ�������Ϣ�������ڴ��С�
     * 
     * @return ����ǣ��򷵻�<code>true</code>
     */
    public boolean isBuffering() {
        return getBufferedRequestContext().isBuffering();
    }

    /**
     * ����bufferģʽ��������ó�<code>true</code>����ʾ��������Ϣ�������ڴ��У�����ֱ�������ԭʼresponse�С�
     * <p>
     * �˷���������<code>getOutputStream</code>��<code>getWriter</code>����֮ǰִ�У������׳�
     * <code>IllegalStateException</code>��
     * </p>
     * 
     * @param buffering �Ƿ�buffer����
     * @throws IllegalStateException <code>getOutputStream</code>��
     *             <code>getWriter</code>�����Ѿ���ִ��
     */
    public void setBuffering(boolean buffering) {
        getBufferedRequestContext().setBuffering(buffering);
    }

    /**
     * �����µ�buffer�������ϵ�buffer��
     * 
     * @throws IllegalStateException �������bufferģʽ����<code>getWriter</code>��
     *             <code>getOutputStream</code>������δ������
     */
    public void pushBuffer() {
        getBufferedRequestContext().pushBuffer();
    }

    /**
     * ���������buffer�������ջ��ֻ��һ��buffer���򵯳����ٴ���һ���µġ�
     * 
     * @return �����buffer����
     * @throws IllegalStateException �������bufferģʽ����<code>getWriter</code>
     *             �����������ã���<code>getOutputStream</code>������δ������
     */
    public ByteArray popByteBuffer() {
        return getBufferedRequestContext().popByteBuffer();
    }

    /**
     * ���������buffer�������ջ��ֻ��һ��buffer���򵯳����ٴ���һ���µġ�
     * 
     * @return �����buffer����
     * @throws IllegalStateException �������bufferģʽ����<code>getOutputStream</code>
     *             �����������ã���<code>getWriter</code>������δ������
     */
    public String popCharBuffer() {
        return getBufferedRequestContext().popCharBuffer();
    }

    /**
     * �������buffers����������ʾ������Ϣ��
     * 
     * @throws IllegalStateException ���response�Ѿ�commit
     */
    public void resetBuffer() {
        getResponse().resetBuffer();
    }

    /**
     * ��ָ�����ַ�������<code>getCaseFolding()</code>�����ã�ת����ָ����Сд��ʽ��
     * 
     * @param str Ҫת�����ַ���
     * @return ת������ַ���
     */
    public String convertCase(String str) {
        return getParserRequestContext().convertCase(str);
    }
}
