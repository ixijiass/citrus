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

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.parser.CookieParser;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.util.io.ByteArray;

/**
 * <code>RunData</code>�ṩ�˶Գ���request contextһվʽ�ķ��ʷ�����
 * <p>
 * �ⲻ��һ���ر���request context����Ҳ����ֱ��ȡ���²��request
 * context���ﵽͬ���Ĺ��ܡ���rundataΪӦ���ṩ��һ����ݷ�ʽ��
 * </p>
 * 
 * @author Michael Zhou
 */
public interface RunData extends RequestContext {
    // ===================================================
    // HTTP request��Ϣ��
    // ===================================================

    /**
     * ȡ������query������
     * 
     * @return <code>ParameterParser</code>ʵ��
     */
    ParameterParser getParameters();

    /**
     * ȡ������cookie��
     * 
     * @return <code>CookieParser</code>ʵ��
     */
    CookieParser getCookies();

    /**
     * ȡ�õ�ǰ���ڴ����HTTP����
     * 
     * @return HTTP�������
     */
    HttpServletRequest getRequest();

    /**
     * ȡ�õ�ǰ���ڴ����HTTP��Ӧ��
     * 
     * @return HTTP��Ӧ����
     */
    HttpServletResponse getResponse();

    /**
     * ȡ�õ�ǰ�����HTTP session��
     * 
     * @return HTTP session����
     */
    HttpSession getSession();

    /**
     * ȡ�ô�����ǰservlet��container����������Ϣ��
     * 
     * @return ������ǰservlet��container����������Ϣ
     */
    ServletContext getServletContext();

    /**
     * ȡ��webӦ�õ�������·�����൱��<code>HttpServletRequest.getContextPath</code>�����ص�ֵ��
     * 
     * @return webӦ�õ�������·��
     */
    String getContextPath();

    /**
     * ȡ��servlet·�����൱��<code>HttpServletRequest.getServletPath</code>�����ص�ֵ��
     * 
     * @return servlet·��
     */
    String getServletPath();

    /**
     * ȡ��path info·�����൱��<code>HttpServletRequest.getPathInfo</code>�����ص�ֵ��
     * 
     * @return path info·��
     */
    String getPathInfo();

    /**
     * ȡ�õ�ǰ��request URL������query string��
     * 
     * @return ��ǰ�����request URL
     */
    String getRequestURL();

    /**
     * ȡ�õ�ǰ��request URL������query string��
     * 
     * @param withQueryString �Ƿ����query string
     * @return ��ǰ�����request URL
     */
    String getRequestURL(boolean withQueryString);

    /**
     * �������������Ƿ�Ϊpost��
     */
    boolean isPostRequest();

    // ===================================================
    // ������Ϣ��
    // ===================================================

    /**
     * ȡ�����ڷ��ʵ�ǰӦ�õ��û���
     * 
     * @return �û�����
     */
    User getUser();

    /**
     * �������ڷ��ʵ�ǰӦ�õ��û���
     * 
     * @param user �û�����
     */
    void setUser(User user);

    /**
     * ȡ�ú͵�ǰ����󶨵Ķ��󡣵��������ʱ�����е�attributes����������
     * 
     * @param key �����key
     * @return ��key���Ӧ�Ķ���
     */
    Object getAttribute(String key);

    /**
     * ��ָ������󶨵���ǰ�����С����������ʱ�����е�attributes����������
     * 
     * @param key �����key
     * @param object ��key���Ӧ�Ķ���
     */
    void setAttribute(String key, Object object);

    // ===================================================
    // HTTP response��Ϣ��
    // ===================================================

    /**
     * ȡ��content type��
     * 
     * @return content type������charset�Ķ���
     */
    String getContentType();

    /**
     * ����content type�� ���content type������charset������
     * <code>getCharacterEncoding</code>�����ã������charset��ǡ�
     * 
     * @param contentType content type
     */
    void setContentType(String contentType);

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
    void setContentType(String contentType, boolean appendCharset);

    /**
     * ȡ��response������ַ�����
     */
    String getCharacterEncoding();

    /**
     * ����response����ַ�����ע�⣬�˷��������ڵ�һ��<code>getWriter</code>֮ǰִ�С�
     * 
     * @param charset ����ַ��������charsetΪ<code>null</code>
     *            �����contentType��ɾ��charset���
     */
    void setCharacterEncoding(String charset);

    /**
     * ȡ���ض����URI��
     * 
     * @return �ض����URI�����û���ض����򷵻�<code>null</code>
     */
    String getRedirectLocation();

    /**
     * �����ض���URI��
     * 
     * @param location �ض����URI
     * @throws IOException �������ʧ��
     * @throws IllegalStateException ���response�Ѿ�committed
     */
    void setRedirectLocation(String location) throws IOException;

    /**
     * �о�ϵͳ�Ƿ��Ѿ��ض���
     * 
     * @return ���<code>setRedirectLocation</code>�����ã��򷵻�<code>true</code>
     */
    boolean isRedirected();

    /**
     * ȡ��������õ�HTTP status��
     * 
     * @return HTTP statusֵ
     */
    int getStatusCode();

    /**
     * ����HTTP status��
     * 
     * @param status HTTP statusֵ
     */
    void setStatusCode(int status);

    // ===================================================
    // Response buffer���ơ�
    // ===================================================

    /**
     * �����Ƿ�������Ϣ�������ڴ��С�
     * 
     * @return ����ǣ��򷵻�<code>true</code>
     */
    boolean isBuffering();

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
    void setBuffering(boolean buffering);

    /**
     * �����µ�buffer�������ϵ�buffer��
     * 
     * @throws IllegalStateException �������bufferģʽ����<code>getWriter</code>��
     *             <code>getOutputStream</code>������δ������
     */
    void pushBuffer();

    /**
     * ���������buffer�������ջ��ֻ��һ��buffer���򵯳����ٴ���һ���µġ�
     * 
     * @return �����buffer����
     * @throws IllegalStateException �������bufferģʽ����<code>getWriter</code>
     *             �����������ã���<code>getOutputStream</code>������δ������
     */
    ByteArray popByteBuffer();

    /**
     * ���������buffer�������ջ��ֻ��һ��buffer���򵯳����ٴ���һ���µġ�
     * 
     * @return �����buffer����
     * @throws IllegalStateException �������bufferģʽ����<code>getOutputStream</code>
     *             �����������ã���<code>getWriter</code>������δ������
     */
    String popCharBuffer();

    /**
     * �������buffers����������ʾ������Ϣ��
     * 
     * @throws IllegalStateException ���response�Ѿ�commit
     */
    void resetBuffer();

    /**
     * ��ָ�����ַ�������<code>getCaseFolding()</code>�����ã�ת����ָ����Сд��ʽ��
     * 
     * @param str Ҫת�����ַ���
     * @return ת������ַ���
     */
    String convertCase(String str);
}
