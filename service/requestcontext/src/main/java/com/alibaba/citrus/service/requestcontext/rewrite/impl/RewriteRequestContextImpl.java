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
package com.alibaba.citrus.service.requestcontext.rewrite.impl;

import static com.alibaba.citrus.service.requestcontext.rewrite.impl.RewriteUtil.*;
import static com.alibaba.citrus.service.requestcontext.util.RequestContextUtil.*;
import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.IOException;
import java.util.regex.MatchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext;
import com.alibaba.citrus.service.requestcontext.rewrite.RewriteRequestContext;
import com.alibaba.citrus.service.requestcontext.rewrite.RewriteSubstitutionContext;
import com.alibaba.citrus.service.requestcontext.rewrite.RewriteSubstitutionHandler;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestContextWrapper;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestWrapper;
import com.alibaba.citrus.util.FileUtil;
import com.alibaba.citrus.util.ServletUtil;
import com.alibaba.citrus.util.StringEscapeUtil;
import com.alibaba.citrus.util.internal.regex.MatchResultSubstitution;

/**
 * ��дURL��������request context��������apache��mod_rewriteģ�顣
 */
public class RewriteRequestContextImpl extends AbstractRequestContextWrapper implements RewriteRequestContext {
    private final static Logger log = LoggerFactory.getLogger(RewriteRequestContext.class);
    public static final String SERVER_SCHEME_HTTP = "http";
    public static final String SERVER_SCHEME_HTTPS = "https";
    public static final int SERVER_PORT_HTTP = 80;
    public static final int SERVER_PORT_HTTPS = 443;
    private final RewriteRule[] rules;
    private ParserRequestContext parserRequestContext;
    private HttpServletRequest wrappedRequest;

    /**
     * ��װһ��<code>RequestContext</code>����
     * 
     * @param wrappedContext ����װ��<code>RequestContext</code>
     * @param rewriteConfig rewrite�������ļ���Ϣ
     */
    public RewriteRequestContextImpl(RequestContext wrappedContext, RewriteRule[] rules) {
        super(wrappedContext);

        this.rules = defaultIfEmptyArray(rules, null);

        // ȡ��parser request context���Ա��޸Ĳ���
        this.parserRequestContext = assertNotNull(findRequestContext(wrappedContext, ParserRequestContext.class),
                "Could not find ParserRequestContext in request context chain");

        // ������һ���request�����Ա�ȡ��ԭ����servletPath��pathInfo֮�����Ϣ
        this.wrappedRequest = wrappedContext.getRequest();
    }

    /**
     * ��ʼһ������
     */
    @Override
    public void prepare() {
        if (rules == null) {
            return;
        }

        // ȡ��servletPath+pathInfo������contextPath
        String originalPath = wrappedRequest.getServletPath()
                + defaultIfNull(wrappedRequest.getPathInfo(), EMPTY_STRING);
        String path = originalPath;
        boolean parameterSubstituted = false;

        if (log.isDebugEnabled()) {
            log.debug("Starting rewrite engine: path=\"{}\"", StringEscapeUtil.escapeJava(path));
        }

        // ��ʼƥ��
        int redirectCode = 0;

        for (RewriteRule rule : rules) {
            MatchResult ruleMatchResult = rule.match(path);
            MatchResult conditionMatchResult = null;
            RewriteSubstitution subs = rule.getSubstitution();

            // ���ƥ�䣬��鿴conditions
            if (ruleMatchResult != null) {
                conditionMatchResult = rule.matchConditions(ruleMatchResult, wrappedRequest);
            }

            // ���C��־��ָ���������ƥ�䣬����ȥ�ж����µĹ���
            boolean chainRule = subs.getFlags().hasC();

            if (conditionMatchResult == null) {
                if (chainRule) {
                    break;
                } else {
                    continue;
                }
            }

            // ��rule��condition��ƥ�������滻����
            MatchResultSubstitution resultSubs = getMatchResultSubstitution(ruleMatchResult, conditionMatchResult);

            // �滻path
            log.debug("Rule conditions have been satisfied, starting substitution to uri");

            path = subs.substitute(path, resultSubs);

            if (!isFullURL(path)) {
                path = FileUtil.normalizeAbsolutePath(path);
            }

            // ����parameters
            parameterSubstituted |= subs.substituteParameters(parserRequestContext.getParameters(), resultSubs);

            // post substitution����
            path = firePostSubstitutionEvent(rule, path, parserRequestContext, resultSubs);

            // �鿴�ض����־
            redirectCode = subs.getFlags().getRedirectCode();

            // ���L��־��ָ��������������
            boolean lastRule = subs.getFlags().hasL();

            if (lastRule) {
                break;
            }
        }

        // ���path���ı��ˣ����滻request���ض���
        if (!isEquals(originalPath, path)) {
            // ������ض�������ϳ��µ�URL
            if (redirectCode > 0) {
                StringBuffer uri = new StringBuffer();
                HttpServletRequest request = getRequest();

                if (!isFullURL(path)) {
                    uri.append(request.getScheme()).append("://").append(request.getServerName());

                    boolean isDefaultPort = false;

                    // http��80
                    isDefaultPort |= SERVER_SCHEME_HTTP.equals(request.getScheme())
                            && request.getServerPort() == SERVER_PORT_HTTP;

                    // https��443
                    isDefaultPort |= SERVER_SCHEME_HTTPS.equals(request.getScheme())
                            && request.getServerPort() == SERVER_PORT_HTTPS;

                    if (!isDefaultPort) {
                        uri.append(":");
                        uri.append(request.getServerPort());
                    }

                    uri.append(request.getContextPath());
                }

                uri.append(path);

                String queryString = parserRequestContext.getParameters().toQueryString();

                if (!isEmpty(queryString)) {
                    uri.append("?").append(queryString);
                }

                String uriLocation = uri.toString();

                try {
                    if (redirectCode == HttpServletResponse.SC_MOVED_TEMPORARILY) {
                        getResponse().sendRedirect(uriLocation);
                    } else {
                        getResponse().setHeader("Location", uriLocation);
                        getResponse().setStatus(redirectCode);
                    }
                } catch (IOException e) {
                    log.warn("Redirect to location \"" + uriLocation + "\" failed", e);
                }
            } else {
                RequestWrapper requestWrapper = new RequestWrapper(wrappedRequest);

                requestWrapper.setPath(path);

                setRequest(requestWrapper);
            }
        } else {
            if (!parameterSubstituted) {
                log.trace("No rewrite substitution happend!");
            }
        }
    }

    private String firePostSubstitutionEvent(RewriteRule rule, String path, ParserRequestContext parser,
                                             MatchResultSubstitution resultSubs) {
        for (Object handler : rule.handlers()) {
            RewriteSubstitutionContext context = null;

            if (handler instanceof RewriteSubstitutionHandler) {
                if (context == null) {
                    context = new RewriteSubstitutionContextImpl(path, parser, resultSubs);
                }

                if (log.isTraceEnabled()) {
                    log.trace("Processing post-substitution event for \"{}\" with handler: {}",
                            StringEscapeUtil.escapeJava(path), handler);
                }

                ((RewriteSubstitutionHandler) handler).postSubstitution(context);

                // path���Ա��ı�
                String newPath = context.getPath();

                if (newPath != null && !isEquals(path, newPath)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Rewriting \"{}\" to \"{}\"", StringEscapeUtil.escapeJava(path),
                                StringEscapeUtil.escapeJava(newPath));
                    }
                }

                path = newPath;
            }
        }

        return path;
    }

    /**
     * ʵ��<code>RewriteSubstitutionContext</code>��
     */
    private class RewriteSubstitutionContextImpl implements RewriteSubstitutionContext {
        private String path;
        private ParserRequestContext parser;
        private MatchResultSubstitution resultSubs;

        public RewriteSubstitutionContextImpl(String path, ParserRequestContext parser,
                                              MatchResultSubstitution resultSubs) {
            this.path = path;
            this.parser = parser;
            this.resultSubs = resultSubs;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public ParserRequestContext getParserRequestContext() {
            return parser;
        }

        public ParameterParser getParameters() {
            return parser.getParameters();
        }

        public MatchResultSubstitution getMatchResultSubstitution() {
            return resultSubs;
        }
    }

    /**
     * ��װrequest��
     */
    private class RequestWrapper extends AbstractRequestWrapper {
        private String path;
        private final boolean prefixMapping;
        private final String originalServletPath;

        public RequestWrapper(HttpServletRequest request) {
            super(RewriteRequestContextImpl.this, request);

            // Servlet mapping������ƥ�䷽ʽ��ǰ׺ƥ��ͺ�׺ƥ�䡣
            // ����ǰ׺ƥ�䣬���磺/turbine/aaa/bbb��servlet pathΪ/turbine��path infoΪ/aaa/bbb
            // ���ں�׺ƥ�䣬���磺/aaa/bbb.html��servlet pathΪ/aaa/bbb.html��path infoΪnull
            this.prefixMapping = ServletUtil.isPrefixServletMapping(request);
            this.originalServletPath = request.getServletPath();
        }

        public void setPath(String path) {
            this.path = trimToNull(path);
        }

        @Override
        public String getServletPath() {
            if (path == null) {
                return super.getServletPath();
            } else {
                if (prefixMapping) {
                    if (path.startsWith(originalServletPath)) {
                        return originalServletPath; // ����ԭ�е�servletPath
                    } else {
                        return "";
                    }
                } else {
                    return path;
                }
            }
        }

        @Override
        public String getPathInfo() {
            if (path == null) {
                return super.getPathInfo();
            } else {
                if (prefixMapping) {
                    if (path.startsWith(originalServletPath)) {
                        return path.substring(originalServletPath.length()); // ��ȥservletPath��ʣ�µĲ���
                    } else {
                        return path;
                    }
                } else {
                    return null;
                }
            }
        }
    }
}
