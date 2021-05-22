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

import com.alibaba.citrus.util.Assert.ExceptionType;

/**
 * ����һ��request/response context��
 * <p>
 * ʹ�ô˳����࣬������ʹ�ñ�׼��servlet api��Ŀ����Ϊ�˾������ٶ����api��������
 * </p>
 */
public abstract class RequestContext {
    private final String baseURL;
    private final String resourceName;
    private PrintWriter writer;
    private OutputStream stream;

    public RequestContext(String baseURL, String resourceName) {
        if (!baseURL.endsWith("/")) {
            baseURL += "/";
        }

        if (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        }

        this.baseURL = baseURL;
        this.resourceName = resourceName;
    }

    /**
     * ȡ��URL�����������Դ���ơ�
     */
    public final String getResourceName() {
        return resourceName;
    }

    /**
     * ȡ�û�׼URL��
     */
    public final String getBaseURL() {
        return baseURL;
    }

    /**
     * ȡ��ָ����Դ��������Ӧ��URL��
     */
    public final String getResourceURL(String resourceName) {
        return normalizeURI(baseURL + resourceName);
    }

    /**
     * ȡ���Ѿ�ȡ�ù���writer��
     * 
     * @throws IllegalStateException ���<code>getWriter(contentType)</code>
     *             ��û�б����ù���
     */
    public final PrintWriter getWriter() {
        return assertNotNull(writer, ExceptionType.ILLEGAL_STATE, "call getWriter(contentType) first");
    }

    /**
     * ȡ����������ı�ҳ���<code>Writer</code>��
     */
    public final PrintWriter getWriter(String contentType) throws IOException {
        if (writer == null) {
            writer = doGetWriter(contentType);
        }

        return writer;
    }

    protected abstract PrintWriter doGetWriter(String contentType) throws IOException;

    /**
     * ȡ���Ѿ�ȡ�ù���stream��
     * 
     * @throws IllegalStateException ���<code>getOutputStream(contentType)</code>
     *             ��û�б����ù���
     */
    public final OutputStream getOutputStream() {
        return assertNotNull(stream, ExceptionType.ILLEGAL_STATE, "call getOutputStream(contentType) first");
    }

    /**
     * ȡ������������������ݵ�<code>OutputStream</code>��
     */
    public final OutputStream getOutputStream(String contentType) throws IOException {
        if (stream == null) {
            stream = doGetOutputStream(contentType);
        }

        return stream;
    }

    protected abstract OutputStream doGetOutputStream(String contentType) throws IOException;

    /**
     * ��Դδ�ҵ���
     */
    public abstract void resourceNotFound(String resourceName) throws IOException;

    /**
     * �ض���ҳ�档
     */
    public abstract void redirectTo(String location) throws IOException;

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), resourceName);
    }
}
