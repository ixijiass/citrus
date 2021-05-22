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
package com.alibaba.citrus.webx.util;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ExceptionUtil.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * �����ȡrequest�еĴ�����Ϣ�Ĺ����ࡣ
 * 
 * @author Michael Zhou
 */
public class ErrorHandlerHelper {
    // Servlet�淶��ָ�������ڱ�ʾerror��Ϣ��key��
    // SRV.9.9.1 Request Attributes
    public final static String KEY_STATUS_CODE = "javax.servlet.error.status_code";
    public final static String KEY_MESSAGE = "javax.servlet.error.message";
    public final static String KEY_EXCEPTION = "javax.servlet.error.exception";
    public final static String KEY_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    public final static String KEY_REQUEST_URI = "javax.servlet.error.request_uri";
    public final static String KEY_SERVLET_NAME = "javax.servlet.error.servlet_name";

    // ��request�б���errorHandlerHelper��key��
    public final static String KEY_ERROR_HANDLER_HELPER = "_webx_errorHandlerHelper_";

    /** HTTP��׼statusCode����Ӧ��message��Ϣ�� */
    public final static Map<Integer, String> STATUS_CODE_MESSAGES = getCodeMessages();

    // ��Ӧ��request
    private final HttpServletRequest request;

    // ������Ϣ
    private int statusCode;
    private String message;
    private Throwable exception;
    private Class<?> exceptionType;
    private String requestURI;
    private String servletName;

    private ErrorHandlerHelper(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * ��request��ȡ��helper����������ڣ��򴴽�һ����
     */
    public static ErrorHandlerHelper getInstance(HttpServletRequest request) {
        ErrorHandlerHelper helper = (ErrorHandlerHelper) assertNotNull(request, "request").getAttribute(
                KEY_ERROR_HANDLER_HELPER);

        if (helper == null) {
            helper = new ErrorHandlerHelper(request);
            request.setAttribute(KEY_ERROR_HANDLER_HELPER, helper);
        }

        return helper;
    }

    /**
     * ��ʼ��helper��
     */
    public void init(String servletName, Throwable exception, ExceptionCodeMapping mapping) {
        setRequestURI(request.getRequestURI());
        setServletName(servletName);
        setException(assertNotNull(exception, "exception"));

        int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        if (mapping != null) {
            for (Throwable e : getCauses(exception, true)) {
                int mappedCode = mapping.getExceptionCode(e);

                if (mappedCode > 0) {
                    statusCode = mappedCode;
                    break;
                }
            }
        }

        setStatusCode(statusCode);
    }

    /**
     * ��helper�е��쳣��Ϣ���óɱ�׼��request attributes��
     */
    public void setServletErrorAttributes() {
        setAttribute(KEY_STATUS_CODE, getStatusCode());
        setAttribute(KEY_MESSAGE, getMessage());
        setAttribute(KEY_EXCEPTION, getException());
        setAttribute(KEY_EXCEPTION_TYPE, getExceptionType());
        setAttribute(KEY_REQUEST_URI, getRequestURI());
        setAttribute(KEY_SERVLET_NAME, getServletName());
    }

    /**
     * ��request�����û�ɾ��ֵ��
     */
    private void setAttribute(String key, Object value) {
        if (value == null) {
            request.removeAttribute(key);
        } else {
            request.setAttribute(key, value);
        }
    }

    /**
     * ȡ�ô�����룬���û�У�Ĭ��Ϊ500��
     */
    public int getStatusCode() {
        if (statusCode <= 0) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        } else {
            return statusCode;
        }
    }

    /**
     * ȡ���쳣����Ϣ�����û�У��򷵻�<code>null</code>��
     */
    public String getMessage() {
        if (message == null) {
            return STATUS_CODE_MESSAGES.get(getStatusCode());
        } else {
            return message;
        }
    }

    /**
     * ���ô�����롣
     */
    public void setStatusCode(int sc) {
        setStatusCode(sc, null);
    }

    /**
     * ���ô�����롣
     */
    public void setStatusCode(int sc, String message) {
        Integer code = sc <= 0 ? null : sc;

        if (message == null) {
            message = STATUS_CODE_MESSAGES.get(code);
        }

        this.statusCode = code;
        this.message = message;
    }

    /**
     * ȡ���쳣�����û�У��򷵻�<code>null</code>��
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * ȡ���쳣���ͣ����û�У��򷵻�<code>null</code>��
     */
    public Class<?> getExceptionType() {
        return exceptionType;
    }

    /**
     * �����쳣��
     */
    public void setException(Throwable exception) {
        this.exception = exception;
        this.exceptionType = exception == null ? null : exception.getClass();
    }

    /**
     * ȡ�÷��������request URI�����û�У��򷵻�<code>null</code>��
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * ����requestURI��
     */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * ȡ�÷��������servlet���֣����û�У��򷵻�<code>null</code>��
     */
    public String getServletName() {
        return servletName;
    }

    /**
     * ����servletName��
     */
    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    /**
     * ��<code>HttpServletResponse</code>��ȡ��statusCode��������
     */
    private static Map<Integer, String> getCodeMessages() {
        Map<Integer, String> messages = createTreeMap();
        int constantMask = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;

        for (Field field : HttpServletResponse.class.getFields()) {
            Class<?> type = field.getType();
            String name = field.getName();
            int modifiers = field.getModifiers() & constantMask;

            if (modifiers == constantMask && name.startsWith("SC_") && int.class.equals(type)) {
                try {
                    Integer sc = (Integer) field.get(null);
                    String message = name.substring("SC_".length());

                    messages.put(sc, message);
                } catch (Exception e) {
                    unexpectedException(e);
                }
            }
        }

        return messages;
    }

    @Override
    public String toString() {
        return getStatusCode() + " " + getMessage();
    }

    /**
     * ͨ��exceptionȡ��status code�Ľӿڡ�
     */
    public static interface ExceptionCodeMapping {
        /**
         * ȡ��status code�������ȷ�����򷵻�<code>0</code>��<code>-1</code>��
         */
        int getExceptionCode(Throwable exception);
    }
}
