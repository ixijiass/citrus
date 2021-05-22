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
package com.alibaba.citrus.util.internal.webpagelite;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.ServletUtil.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ����servlet api��request contextʵ�֡�
 * 
 * @author Michael Zhou
 */
public class ServletRequestContext extends RequestContext {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;

    public ServletRequestContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        super(getServletBaseURL(request), getServletResourcePath(request));

        this.request = assertNotNull(request, "request is null");
        this.response = assertNotNull(response, "response is null");
        this.servletContext = assertNotNull(servletContext, "servletContext is null");
    }

    public ServletRequestContext(HttpServletRequest request, HttpServletResponse response,
                                 ServletContext servletContext, String baseURL, String resourceName) {
        super(baseURL, resourceName);

        this.request = assertNotNull(request, "request is null");
        this.response = assertNotNull(response, "response is null");
        this.servletContext = assertNotNull(servletContext, "servletContext is null");
    }

    public final HttpServletRequest getRequest() {
        return request;
    }

    public final HttpServletResponse getResponse() {
        return response;
    }

    public final ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    protected OutputStream doGetOutputStream(String contentType) throws IOException {
        response.setContentType(contentType);
        return response.getOutputStream();
    }

    @Override
    protected PrintWriter doGetWriter(String contentType) throws IOException {
        response.setContentType(contentType);
        return response.getWriter();
    }

    @Override
    public final void redirectTo(String location) throws IOException {
        response.sendRedirect(location);
    }

    /**
     * ���������Դ�Ҳ���ʱ�����������á���������޸Ĵ���Ϊ��
     */
    @Override
    public void resourceNotFound(String resourceName) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found: " + resourceName);
    }

    /**
     * ȡ��servlet request context��
     */
    public static ServletRequestContext getServletRequestContext(RequestContext request) {
        if (request instanceof ServletRequestContext) {
            return (ServletRequestContext) request;
        } else {
            return null;
        }
    }

    /**
     * ����http headers������cache��
     */
    public static void disableCache(RequestContext request) {
        if (getServletRequestContext(request) != null) {
            HttpServletResponse response = getServletRequestContext(request).getResponse();

            // Set to expire far in the past.
            response.setHeader("Expires", "Sat, 6 May 1911 12:00:00 GMT");

            // Set standard HTTP/1.1 no-cache headers.
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

            // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");

            // Set standard HTTP/1.0 no-cache header.
            response.setHeader("Pragma", "no-cache");
        }
    }
}
