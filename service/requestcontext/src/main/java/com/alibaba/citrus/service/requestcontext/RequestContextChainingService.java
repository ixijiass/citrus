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
package com.alibaba.citrus.service.requestcontext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ��һ����<code>RequestContext</code>������������service��
 * <p>
 * ͨ��������ʵ�ֶ��ذ�װ��HTTP request��response��
 * </p>
 * 
 * @author Michael Zhou
 */
public interface RequestContextChainingService {
    /**
     * ȡ�����е�request context����Ϣ��
     */
    RequestContextInfo<?>[] getRequestContextInfos();

    /**
     * ȡ��<code>RequestContext</code>����
     * 
     * @param servletContext <code>ServletContext</code>����
     * @param request <code>HttpServletRequest</code>����
     * @param response <code>HttpServletResponse</code>����
     * @return request context
     */
    RequestContext getRequestContext(ServletContext servletContext, HttpServletRequest request,
                                     HttpServletResponse response);

    /**
     * ���⵽�ڵص���<code>requestContext.commit()</code>������
     * 
     * @param requestContext Ҫ��ʼ����request context
     * @throws RequestContextException ���ʧ��
     */
    void commitRequestContext(RequestContext requestContext);
}
