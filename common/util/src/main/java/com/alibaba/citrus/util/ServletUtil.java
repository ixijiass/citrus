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
package com.alibaba.citrus.util;

import static com.alibaba.citrus.util.FileUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

/**
 * �й�servlet��С���ߡ�
 * 
 * @author Michael Zhou
 */
public class ServletUtil {
    /**
     * �ж�servlet�Ƿ�Ϊǰ׺ӳ�䡣
     * <p>
     * Servlet mapping������ƥ�䷽ʽ��ǰ׺ƥ��ͺ�׺ƥ�䡣
     * </p>
     * <ul>
     * <li>����ǰ׺ƥ�䣬���磺/turbine/aaa/bbb��servlet pathΪ/turbine��path infoΪ/aaa/bbb</li>
     * <li>���ں�׺ƥ�䣬���磺/aaa/bbb.html��servlet pathΪ/aaa/bbb.html��path infoΪnull</li>
     * </ul>
     */
    public static boolean isPrefixServletMapping(HttpServletRequest request) {
        String pathInfo = trimToNull(request.getPathInfo());

        if (pathInfo != null) {
            return true;
        } else {
            // ���������ǰ׺ӳ��/turbine��requestURI=/turbine
            // ��ʱ��pathInfoҲ��null������ʵ��ǰ׺ƥ�䡣
            // �����������ͨ���鿴servletPath�Ƿ��к�׺�����ֵ�ʶ��
            String servletPath = trimToEmpty(request.getServletPath());
            int index = servletPath.lastIndexOf("/");

            if (servletPath.indexOf(".", index + 1) >= 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * ȡ��request���������Դ·����
     * <p>
     * ��Դ·��Ϊ<code>getServletPath() + getPathInfo()</code>��
     * </p>
     * <p>
     * ע�⣬<code>ResourcePath</code>��<code>"/"</code>��ʼ����������ݣ��򷵻ؿ��ַ���
     * <code>""</code>��
     * </p>
     */
    public static String getResourcePath(HttpServletRequest request) {
        String pathInfo = normalizeAbsolutePath(request.getPathInfo(), false);
        String servletPath = normalizeAbsolutePath(request.getServletPath(), pathInfo.length() != 0);

        return servletPath + pathInfo;
    }

    /**
     * ȡ��request����Ļ�׼URL��
     * <p>
     * ��׼URL��ͬ��<code>SERVER/contextPath</code>��
     * </p>
     * <p>
     * ��׼URL����<strong>��</strong>��<code>"/"</code>��β��
     * </p>
     * <p>
     * ���µ�ʽ���ǳ�����<code>fullURL = baseURL + resourcePath</code>��
     * </p>
     */
    public static String getBaseURL(HttpServletRequest request) {
        String fullURL = request.getRequestURL().toString();
        String fullPath;

        try {
            fullPath = new URL(fullURL).getPath();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + fullURL, e);
        }

        // ����URL
        StringBuilder buf = new StringBuilder(fullURL);
        buf.setLength(fullURL.length() - fullPath.length());

        // ����contextPath
        buf.append(normalizeAbsolutePath(request.getContextPath(), true));

        return buf.toString();
    }

    /**
     * ȡ��request���������Դ·����
     * <ul>
     * <li>����ǰ׺ƥ���servlet����ͬ��<code>getPathInfo()</code>������ӳ��
     * <code>/turbine/*</code>��<code>/turbine/xx/yy</code>��resource pathΪ
     * <code>/xx/yy</code>��</li>
     * <li>���ں�׺ƥ���servlet����ͬ�� <code>getServletPath()</code>������ӳ��
     * <code>*.do</code>��<code>/xx/yy.do</code>��resource pathΪ
     * <code>/xx/yy.do</code>��</li>
     * </ul>
     * <p>
     * ע�⣬<code>ResourcePath</code>��<code>"/"</code>��ʼ����������ݣ��򷵻ؿ��ַ���
     * <code>""</code>��
     * </p>
     * <p>
     * ������������servlet-mapping��Ӧ��URL��
     * </p>
     */
    public static String getServletResourcePath(HttpServletRequest request) {
        String resourcePath;

        if (isPrefixServletMapping(request)) {
            resourcePath = request.getPathInfo();
        } else {
            resourcePath = request.getServletPath();
        }

        resourcePath = normalizeAbsolutePath(resourcePath, false);

        return resourcePath;
    }

    /**
     * ȡ��request����Ļ�׼URL��
     * <ul>
     * <li>����ǰ׺ƥ���servlet����ͬ��<code>SERVER/contextPath/servletPath</code>������ӳ��
     * <code>/turbine/*</code>��<code>http://localhost/myapp/turbine/xx/yy</code>
     * ��baseURLΪ <code>http://localhost/myapp/turbine</code>��</li>
     * <li>���ں�׺ƥ���servlet����ͬ��<code>SERVER/contextPath</code>������ӳ��
     * <code>*.do</code>��<code>http://localhost/myapp/xx/yy.do</code>��baseURLΪ
     * <code>http://localhost/myapp</code>��</li>
     * </ul>
     * <p>
     * ��׼URL����<strong>��</strong>��<code>"/"</code>��β��
     * </p>
     * <p>
     * ���µ�ʽ���ǳ�����<code>fullURL = servletBaseURL + servletResourcePath</code>��
     * </p>
     * <p>
     * ������������servlet-mapping��Ӧ��URL��
     * </p>
     */
    public static String getServletBaseURL(HttpServletRequest request) {
        String fullURL = request.getRequestURL().toString();
        String fullPath;

        try {
            fullPath = new URL(fullURL).getPath();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + fullURL, e);
        }

        // ����URL
        StringBuilder buf = new StringBuilder(fullURL);
        buf.setLength(fullURL.length() - fullPath.length());

        // ����contextPath
        buf.append(normalizeAbsolutePath(request.getContextPath(), true));

        // ����ǰ׺ƥ�䣬����servletPath
        if (isPrefixServletMapping(request)) {
            buf.append(normalizeAbsolutePath(request.getServletPath(), true));
        }

        return buf.toString();
    }

    /**
     * ���URI��
     */
    public static String normalizeURI(String uri) {
        return URI.create(trimToEmpty(uri)).normalize().toString();
    }
}
