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
package com.alibaba.citrus.service.uribroker.uri;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.citrus.util.ServletUtil;

/**
 * Servlet����URI��
 * <p>
 * һ��Servlet����URI�������¼������֣�
 * </p>
 * 
 * <pre>
 * URI         = SERVER_INFO + PATH + "?" + QUERY_DATA + "#" + REFERENCE
 * SERVER_INFO = scheme://loginUser:loginPassword@serverName:serverPort
 * PATH        = /contextPath/servletPath/PATH_INFO
 * QUERY_DATA  = queryKey1=value1&queryKey2=value2
 * REFERENCE   = reference
 * </pre>
 * <p>
 * ���磺
 * </p>
 * 
 * <pre>
 * http://user:pass@myserver.com:8080/mycontext/myservlet/view?id=1#top
 * </pre>
 * <p>
 * ע�⣬<code>ServletURIBroker</code>û���ṩ�޸�pathInfo�ķ��������Ҫ��ӡ�ɾ�����޸�path����ֱ��ʹ������
 * <code>GenericServletURIBroker</code>��
 * </p>
 * 
 * @author Michael Zhou
 * @author dux.fangl
 */
public abstract class ServletURIBroker extends WebAppURIBroker {
    protected static final int SERVLET_PATH_INDEX = CONTEXT_PATH_INDEX + 1;
    protected static final int PATH_INFO_INDEX = SERVLET_PATH_INDEX + 1;
    private boolean hasServletPath;

    /**
     * ��request�е�����ʱ��Ϣ��䵽uri broker�С�
     */
    @Override
    protected void populateWithRequest(HttpServletRequest request) {
        boolean savedHasContextPath = hasContextPath; // ��ֵ���ܱ�populateWithRequest()�ı�
        super.populateWithRequest(request);

        // ����������contextPath��servletPath�Ż������塣
        if (!savedHasContextPath && !hasServletPath) {
            // ֻ��ǰ׺ƥ��ʱ��������servletPath������ǰ׺ƥ�䣺/myservlet/*��
            // ���ں�׺ƥ�䣬����*.htm������servletPathû�����塣
            if (ServletUtil.isPrefixServletMapping(request)) {
                setServletPath(request.getServletPath());
            }
        }
    }

    /**
     * ȡ��servlet path��
     */
    public String getServletPath() {
        if (hasServletPath) {
            return getPathSegmentAsString(SERVLET_PATH_INDEX);
        } else {
            return null;
        }
    }

    /**
     * ����servlet path��
     */
    public ServletURIBroker setServletPath(String servletPath) {
        setPathSegment(SERVLET_PATH_INDEX, servletPath);
        hasServletPath = true;
        return this;
    }

    /**
     * ȡ��script��, ����contextPath��servletName.
     */
    public String getScriptName() {
        if (hasContextPath) {
            return getContextPath() + getServletPath();
        } else {
            return getServletPath();
        }
    }

    /**
     * ȡ��path info��
     */
    public String getPathInfo() {
        return getAllPathSegmentsAsString(PATH_INFO_INDEX);
    }

    /**
     * ȡ��һ��path info��
     */
    public List<String> getPathInfoElements() {
        return getAllPathSegments(PATH_INFO_INDEX);
    }

    @Override
    protected void initDefaults(URIBroker parent) {
        super.initDefaults(parent);

        if (parent instanceof ServletURIBroker) {
            ServletURIBroker parentServlet = (ServletURIBroker) parent;

            if (!hasServletPath) {
                hasServletPath = parentServlet.hasServletPath;
                setPathSegment(SERVLET_PATH_INDEX, parentServlet.getPathSegment(SERVLET_PATH_INDEX));
            }
        }
    }

    @Override
    protected void copyFrom(URIBroker parent) {
        super.copyFrom(parent);

        if (parent instanceof ServletURIBroker) {
            ServletURIBroker parentServlet = (ServletURIBroker) parent;

            hasServletPath = parentServlet.hasServletPath;
            setPathSegment(SERVLET_PATH_INDEX, parentServlet.getPathSegment(SERVLET_PATH_INDEX));
        }
    }
}
