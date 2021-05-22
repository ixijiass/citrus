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
package com.alibaba.citrus.service.requestcontext.lazycommit.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.lazycommit.LazyCommitFailedException;
import com.alibaba.citrus.service.requestcontext.lazycommit.LazyCommitRequestContext;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestContextWrapper;
import com.alibaba.citrus.service.requestcontext.support.AbstractResponseWrapper;
import com.alibaba.citrus.util.StringUtil;

/**
 * �ӳ��ύresponse��ʵ�֡�
 * 
 * @author Michael Zhou
 */
public class LazyCommitRequestContextImpl extends AbstractRequestContextWrapper implements LazyCommitRequestContext {
    private final static Logger log = LoggerFactory.getLogger(LazyCommitRequestContext.class);
    private SendError sendError;
    private String sendRedirect;
    private boolean setLocation;
    private boolean bufferFlushed;
    private int status;

    /**
     * ��װһ��<code>RequestContext</code>����
     * 
     * @param wrappedContext ����װ��<code>RequestContext</code>
     */
    public LazyCommitRequestContextImpl(RequestContext wrappedContext) {
        super(wrappedContext);

        setResponse(new ResponseWrapper(wrappedContext.getResponse()));
    }

    /**
     * �жϵ�ǰ�����Ƿ��ѳ���
     * 
     * @return ��������򷵻�<code>true</code>
     */
    public boolean isError() {
        return sendError != null;
    }

    /**
     * ���<code>sendError()</code>�����������ã���÷�������һ��error״ֵ̬��
     * 
     * @return error״ֵ̬����ϵͳ�������򷵻�<code>0</code>
     */
    public int getErrorStatus() {
        if (sendError != null) {
            return sendError.status;
        }

        return 0;
    }

    /**
     * ���<code>sendError()</code>�����������ã���÷�������һ��error��Ϣ��
     * 
     * @return error��Ϣ����ϵͳ�������򷵻�<code>null</code>
     */
    public String getErrorMessage() {
        if (sendError != null) {
            return sendError.message;
        }

        return null;
    }

    /**
     * �жϵ�ǰ�����Ƿ��ѱ��ض���
     * 
     * @return ����ض����򷵻�<code>true</code>
     */
    public boolean isRedirected() {
        return setLocation || !StringUtil.isEmpty(sendRedirect);
    }

    /**
     * ȡ���ض����URI��
     * 
     * @return �ض����URI�����û���ض����򷵻�<code>null</code>
     */
    public String getRedirectLocation() {
        return sendRedirect;
    }

    /**
     * ȡ��������õ�HTTP status��
     * 
     * @return HTTP statusֵ
     */
    public int getStatus() {
        return status;
    }

    /**
     * ����һ������
     * 
     * @throws LazyCommitFailedException ���ʧ��
     */
    @Override
    public void commit() throws LazyCommitFailedException {
        try {
            ((ResponseWrapper) getResponse()).commit();
        } catch (IOException e) {
            throw new LazyCommitFailedException(e);
        }
    }

    /**
     * ��װresponse��
     */
    private class ResponseWrapper extends AbstractResponseWrapper {
        public ResponseWrapper(HttpServletResponse response) {
            super(LazyCommitRequestContextImpl.this, response);
        }

        @Override
        public void sendError(int status) throws IOException {
            sendError(status, null);
        }

        @Override
        public void sendError(int status, String message) throws IOException {
            if (sendError == null && sendRedirect == null) {
                sendError = new SendError(status, message);
            }
        }

        /**
         * �����ض���URI��
         * 
         * @param location �ض����URI
         * @throws IOException �������ʧ��
         * @throws IllegalStateException ���response�Ѿ�committed
         */
        @Override
        public void sendRedirect(String location) throws IOException {
            if (sendError == null && sendRedirect == null) {
                sendRedirect = location;
            }
        }

        @Override
        public void setHeader(String key, String value) {
            if ("location".equalsIgnoreCase(key)) {
                setLocation = true;
            }

            super.setHeader(key, value);
        }

        @Override
        public void flushBuffer() throws IOException {
            bufferFlushed = true;
        }

        /**
         * ����HTTP status��
         * 
         * @param sc HTTP statusֵ
         */
        @Override
        public void setStatus(int sc) {
            status = sc;
        }

        private void commit() throws IOException {
            if (status > 0) {
                log.debug("Set HTTP status to " + status);
                super.setStatus(status);
            }

            if (sendError != null) {
                if (sendError.message == null) {
                    log.debug("Set error page: " + sendError.status);

                    super.sendError(sendError.status);
                } else {
                    log.debug("Set error page: " + sendError.status + " " + sendError.message);

                    super.sendError(sendError.status, sendError.message);
                }
            } else if (sendRedirect != null) {
                log.debug("Set redirect location to " + sendRedirect);

                // ��location���������ת��һ�£���������ȷ��������US_ASCII�ַ���URL��ȷ���
                String charset = getCharacterEncoding();

                if (charset != null) {
                    sendRedirect = new String(sendRedirect.getBytes(charset), "8859_1");
                }

                super.sendRedirect(sendRedirect);
            }

            if (bufferFlushed) {
                super.flushBuffer();
            }
        }
    }

    /**
     * ����sendError����Ϣ��
     */
    private class SendError {
        public final int status;
        public final String message;

        public SendError(int status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
