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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.alibaba.citrus.service.requestcontext.RequestContext;

/**
 * ��<code>com.alibaba.webx.request.contextRequestContext</code>֧�ֵ�
 * <code>ServletResponseWrapper</code>��
 * 
 * @author Michael Zhou
 */
public class AbstractResponseWrapper extends HttpServletResponseWrapper {
    private RequestContext context;

    /**
     * ����һ��response wrapper��
     * 
     * @param context request context
     * @param response response
     */
    public AbstractResponseWrapper(RequestContext context, HttpServletResponse response) {
        super(response);

        this.context = assertNotNull(context, "requestContext");
    }

    /**
     * ȡ�õ�ǰrequest������servlet context������
     * 
     * @return <code>ServletContext</code>����
     */
    public ServletContext getServletContext() {
        return getRequestContext().getServletContext();
    }

    /**
     * ȡ��֧�����ǵ�<code>RequestContext</code>��
     * 
     * @return <code>RequestContext</code>ʵ��
     */
    public RequestContext getRequestContext() {
        return context;
    }

    /**
     * ȡ���ַ�����ʾ��
     * 
     * @return �ַ�����ʾ
     */
    @Override
    public String toString() {
        return "Http response within request context: " + getRequestContext().toString();
    }
}
