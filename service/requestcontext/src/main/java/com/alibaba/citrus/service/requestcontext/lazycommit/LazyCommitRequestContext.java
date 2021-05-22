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
package com.alibaba.citrus.service.requestcontext.lazycommit;

import com.alibaba.citrus.service.requestcontext.RequestContext;

/**
 * �ӳ��ύresponse����Щ�����ĵ��ûᵼ��response���ύ��������
 * <ul>
 * <li><code>sendError</code></li>
 * <li><code>sendRedirect</code></li>
 * <li><code>flushBuffer</code></li>
 * <li><code>setContentLength()</code>��
 * <code>setHeader("Content-Length", len)</code>������Щservlet
 * engine�����������ύresponse��</li>
 * </ul>
 * Responseһ���ύ���Ͳ����޸�header�ˡ������һЩӦ�ã�����cookie-based session����ʵ����һ�����⡣
 * <p>
 * ����ʹ���ӳ��ύ��֧����ЩӦ�á�
 * </p>
 * <p>
 * ע�⣬���ಢδ����<code>getWriter()</code>��<code>getOutputStream()</code>
 * �������������ύ��������Щ�������������ύ����Ҫ��<code>BufferedRequestContext</code>������
 * </p>
 * 
 * @author Michael Zhou
 */
public interface LazyCommitRequestContext extends RequestContext {
    /**
     * �жϵ�ǰ�����Ƿ��ѳ���
     * 
     * @return ��������򷵻�<code>true</code>
     */
    boolean isError();

    /**
     * ���<code>sendError()</code>�����������ã���÷�������һ��error״ֵ̬��
     * 
     * @return error״ֵ̬����ϵͳ�������򷵻�<code>0</code>
     */
    int getErrorStatus();

    /**
     * ���<code>sendError()</code>�����������ã���÷�������һ��error��Ϣ��
     * 
     * @return error��Ϣ����ϵͳ�������򷵻�<code>null</code>
     */
    String getErrorMessage();

    /**
     * �жϵ�ǰ�����Ƿ��ѱ��ض���
     * 
     * @return ����ض����򷵻�<code>true</code>
     */
    boolean isRedirected();

    /**
     * ȡ���ض����URI��
     * 
     * @return �ض����URI�����û���ض����򷵻�<code>null</code>
     */
    String getRedirectLocation();

    /**
     * ȡ��������õ�HTTP status��
     * 
     * @return HTTP statusֵ
     */
    int getStatus();
}
