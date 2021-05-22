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
package com.alibaba.citrus.service.requestcontext.impl;

import static com.alibaba.citrus.util.Assert.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ʵ����<code>RequestContext</code>�ӿڣ�����request��response��servletContext����Ϣ��
 * 
 * @author Michael Zhou
 */
public class SimpleRequestContext implements RequestContext {
    private final ServletContext servletContext;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    /**
     * ����һ���µ�<code>RequestContext</code>����
     * 
     * @param servletContext ��ǰ�������ڵ�<code>ServletContext</code>
     * @param request <code>HttpServletRequest</code>����
     * @param response <code>HttpServletResponse</code>����
     */
    public SimpleRequestContext(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) {
        this.servletContext = assertNotNull(servletContext, "servletContext");
        this.request = assertNotNull(request, "request");
        this.response = assertNotNull(response, "response");
    }

    /**
     * ȡ�ñ���װ��context��
     * 
     * @return ����װ��<code>RequestContext</code>����
     */
    public RequestContext getWrappedRequestContext() {
        return null;
    }

    /**
     * ȡ��servletContext����
     * 
     * @return <code>ServletContext</code>����
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * ȡ��request����
     * 
     * @return <code>HttpServletRequest</code>����
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * ȡ��response����
     * 
     * @return <code>HttpServletResponse</code>����
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * ��ʼһ������
     */
    public void prepare() {
    }

    /**
     * ����һ������
     */
    public void commit() {
    }

    /**
     * ��ʾ��ǰ<code>RequestContext</code>�����ݡ�
     * 
     * @return �ַ�����ʾ
     */
    @Override
    public String toString() {
        MapBuilder mb = new MapBuilder();

        mb.append("request", getRequest());
        mb.append("response", getResponse());
        mb.append("webapp", getServletContext());

        return new ToStringBuilder().append(getClass().getSimpleName()).append(mb).toString();
    }
}
