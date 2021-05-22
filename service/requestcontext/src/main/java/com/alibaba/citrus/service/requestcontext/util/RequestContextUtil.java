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
package com.alibaba.citrus.service.requestcontext.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.citrus.service.requestcontext.RequestContext;

/**
 * ��<code>RequestContext</code>��صĸ����ࡣ
 * 
 * @author Michael Zhou
 */
public class RequestContextUtil {
    private static final String REQUEST_CONTEXT_KEY = "_outer_webx3_request_context_";

    /**
     * ȡ�ú͵�ǰrequest�������<code>RequestContext</code>����
     * 
     * @param request Ҫ����request
     * @return <code>RequestContext</code>�������û�ҵ����򷵻�<code>null</code>
     */
    public static RequestContext getRequestContext(HttpServletRequest request) {
        return (RequestContext) request.getAttribute(REQUEST_CONTEXT_KEY);
    }

    /**
     * ��<code>RequestContext</code>�����request�������
     * 
     * @param requestContext <code>RequestContext</code>����
     */
    public static void setRequestContext(RequestContext requestContext) {
        HttpServletRequest request = requestContext.getRequest();

        request.setAttribute(REQUEST_CONTEXT_KEY, requestContext);
    }

    /**
     * ��<code>RequestContext</code>�����request���������
     */
    public static void removeRequestContext(HttpServletRequest request) {
        request.removeAttribute(REQUEST_CONTEXT_KEY);
    }

    /**
     * ��ָ����request context���伶����request context���ҵ�һ��ָ�����͵�request context��
     * 
     * @param request �Ӹ�<code>HttpServletRequest</code>��ȡ��request context
     * @param requestContextInterface Ҫ���ҵ���
     * @return <code>RequestContext</code>�������û�ҵ����򷵻�<code>null</code>
     */
    public static <R extends RequestContext> R findRequestContext(HttpServletRequest request,
                                                                  Class<R> requestContextInterface) {
        return findRequestContext(getRequestContext(request), requestContextInterface);
    }

    /**
     * ��ָ����request context���伶����request context���ҵ�һ��ָ�����͵�request context��
     * 
     * @param requestContext Ҫ������request context
     * @param requestContextInterface Ҫ���ҵ���
     * @return <code>RequestContext</code>�������û�ҵ����򷵻�<code>null</code>
     */
    public static <R extends RequestContext> R findRequestContext(RequestContext requestContext,
                                                                  Class<R> requestContextInterface) {
        do {
            if (requestContextInterface.isInstance(requestContext)) {
                break;
            }

            requestContext = requestContext.getWrappedRequestContext();
        } while (requestContext != null);

        return requestContextInterface.cast(requestContext);
    }

    /**
     * ע��spring <code>ServletRequestAttributes</code>�е������ص���������Щ��������request
     * context���ύ֮�����ε��á�
     */
    public static void registerRequestDestructionCallback(String name, Runnable callback) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        attributes.registerDestructionCallback(name, callback, RequestAttributes.SCOPE_REQUEST);
    }
}
