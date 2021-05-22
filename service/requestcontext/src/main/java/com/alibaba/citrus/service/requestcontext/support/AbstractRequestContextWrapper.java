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
package com.alibaba.citrus.service.requestcontext.support;

import static com.alibaba.citrus.util.Assert.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.RequestContextException;
import com.alibaba.citrus.util.internal.ToStringBuilder;

/**
 * <code>RequestContext</code>��װ����Ĭ��ʵ�֡�
 * <p>
 * ���<code>toString()</code>���������г����м�����<code>RequestContext</code>����
 * </p>
 * 
 * @author Michael Zhou
 */
public abstract class AbstractRequestContextWrapper implements RequestContext {
    private final RequestContext wrappedContext;
    private final ServletContext servletContext;
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     * ��װһ��<code>RequestContext</code>����
     * 
     * @param wrappedContext ����װ��<code>RequestContext</code>
     */
    public AbstractRequestContextWrapper(RequestContext wrappedContext) {
        this.wrappedContext = assertNotNull(wrappedContext, "wrappedContext");
        this.servletContext = wrappedContext.getServletContext();
        this.request = wrappedContext.getRequest();
        this.response = wrappedContext.getResponse();
    }

    /**
     * ȡ�ñ���װ��context��
     * 
     * @return ����װ��<code>RequestContext</code>����
     */
    public RequestContext getWrappedRequestContext() {
        return wrappedContext;
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
     * ����request����
     * 
     * @param request <code>HttpServletRequest</code>����
     */
    protected void setRequest(HttpServletRequest request) {
        this.request = request;
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
     * ����response����
     * 
     * @param response <code>HttpServletResponse</code>����
     */
    protected void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * ��ʼһ������
     */
    public void prepare() {
    }

    /**
     * ����һ������
     * 
     * @throws RequestContextException ���ʧ��
     */
    public void commit() throws RequestContextException {
    }

    /**
     * ��ʾ��ǰ��<code>RequestContext</code>�Լ����м�����<code>RequestContext</code>��
     * 
     * @return �ַ�����ʾ
     */
    @Override
    public String toString() {
        return new ToStringBuilder().append(thisToString()).start().append(getWrappedRequestContext()).end().toString();
    }

    /**
     * ��ʾ��ǰ<code>RequestContext</code>���������Ϣ��
     */
    protected String thisToString() {
        return super.toString();
    }
}
