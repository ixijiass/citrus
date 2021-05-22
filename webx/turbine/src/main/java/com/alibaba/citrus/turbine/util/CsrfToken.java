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
package com.alibaba.citrus.turbine.util;

import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ecs.html.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.service.pull.ToolFactory;
import com.alibaba.citrus.springext.support.parser.AbstractSingleBeanDefinitionParser;
import com.alibaba.citrus.util.ClassLoaderUtil;
import com.alibaba.citrus.util.StringUtil;

/**
 * ��������һ��Ψһ��ID������ֹCSRF������Cross Site Request Forgery���� ���⣬������������ֹ�ظ��ύͬһ�ű���
 * <p>
 * �������Ϊpull tool�����ڲ�����singleton request proxy�����Ը���ɱ�ע���global�������pull tool��
 * </p>
 * <p>
 * CSRF token��key�ǰ��������߼�ȡ�õģ�
 * </p>
 * <ol>
 * <li>���Thread������<code>setContextTokenKey()</code>����ȷ���ã���ʹ������</li>
 * <li>����ʹ��Ĭ��ֵ��<code>_csrf_token</code>����</li>
 * </ol>
 * 
 * @author Michael Zhou
 */
public class CsrfToken {
    public static final String DEFAULT_TOKEN_KEY = "_csrf_token";
    public static final int DEFAULT_MAX_TOKENS = 8;
    public static final String CSRF_TOKEN_SEPARATOR = "/";
    private static final AtomicInteger counter = new AtomicInteger();;
    private static final ThreadLocal<Configuration> contextTokenConfigurationHolder = new ThreadLocal<Configuration>();
    private final HttpServletRequest request;

    public CsrfToken(HttpServletRequest request) {
        this.request = assertNotNull(request, "request");
    }

    public static String getKey() {
        String key = null;
        Configuration conf = contextTokenConfigurationHolder.get();

        if (conf != null) {
            key = conf.getTokenKey();
        }

        if (key == null) {
            key = DEFAULT_TOKEN_KEY;
        }

        return key;
    }

    public static int getMaxTokens() {
        int maxTokens = -1;
        Configuration conf = contextTokenConfigurationHolder.get();

        if (conf != null) {
            maxTokens = conf.getMaxTokens();
        }

        if (maxTokens <= 0) {
            maxTokens = DEFAULT_MAX_TOKENS;
        }

        return maxTokens;
    }

    public static void setContextTokenConfiguration(String tokenKey, int maxTokens) {
        contextTokenConfigurationHolder.set(new Configuration(tokenKey, maxTokens));
    }

    public static void resetContextTokenConfiguration() {
        contextTokenConfigurationHolder.remove();
    }

    /**
     * ��������csrf token��hidden field�� �����ɵ�token�ᱣ����Ч��ֱ��session���ڡ�
     */
    public Input getHiddenField() {
        return getLongLiveHiddenField();
    }

    /**
     * ��������csrf token��hidden field��
     * 
     * @param longLiveToken ���Ϊ<code>true</code>����token�ᱣ����Ч��ֱ��session���ڡ�
     * @deprecated use getUniqueHiddenField() or getLongLiveHiddenField()
     *             instead
     */
    @Deprecated
    public Input getHiddenField(boolean longLiveToken) {
        return longLiveToken ? getLongLiveHiddenField() : getUniqueHiddenField();
    }

    public Input getUniqueHiddenField() {
        return new Input("hidden", getKey(), getUniqueToken());
    }

    public Input getLongLiveHiddenField() {
        return new Input("hidden", getKey(), getLongLiveToken());
    }

    /**
     * ����csrf token�������ɵ�tokenֻ�ܱ�ʹ��һ�Ρ�
     */
    public String getUniqueToken() {
        HttpSession session = request.getSession();
        String key = getKey();
        String tokenOfRequest = (String) request.getAttribute(key);
        int maxTokens = getMaxTokens();

        if (tokenOfRequest == null) {
            // �����µ�token��
            // �����ǰsession���Ѿ���token�ˣ�
            // ����token��û�г������������token׷�ӵ�session�У�
            // ���token������������򸲸������token��
            LinkedList<String> tokens = getTokensInSession(session, key);

            tokenOfRequest = getGenerator().generateUniqueToken();
            request.setAttribute(key, tokenOfRequest);

            tokens.addLast(tokenOfRequest);

            while (tokens.size() > maxTokens) {
                tokens.removeFirst();
            }

            setTokensInSession(session, key, tokens);
        }

        return tokenOfRequest;
    }

    /**
     * ȡ�ó�Чtoken����<code>uniqueToken</code> ��ͬ����Чtoken��������session��ͬ��
     */
    public String getLongLiveToken() {
        return getLongLiveTokenInSession(request.getSession());
    }

    public static LinkedList<String> getTokensInSession(HttpSession session, String tokenKey) {
        return createLinkedList(StringUtil.split((String) session.getAttribute(tokenKey), CSRF_TOKEN_SEPARATOR));
    }

    public static void setTokensInSession(HttpSession session, String tokenKey, List<String> tokens) {
        if (tokens.isEmpty()) {
            session.removeAttribute(tokenKey);
        } else {
            session.setAttribute(tokenKey, StringUtil.join(tokens, CSRF_TOKEN_SEPARATOR));
        }
    }

    public static String getLongLiveTokenInSession(HttpSession session) {
        return getGenerator().generateLongLiveToken(session);
    }

    @Override
    public String toString() {
        try {
            return getUniqueToken();
        } catch (IllegalStateException e) {
            return "<No thread-bound request>";
        }
    }

    /**
     * ���token�����token���ڣ��򷵻�<code>true</code>��
     */
    public static boolean check(HttpServletRequest request) {
        String key = getKey();
        String fromRequest = trimToNull(request.getParameter(key));

        return fromRequest != null;
    }

    private static class Configuration {
        private final String tokenKey;
        private final int maxTokens;

        public Configuration(String tokenKey, int maxTokens) {
            this.tokenKey = trimToNull(tokenKey);
            this.maxTokens = maxTokens;
        }

        public String getTokenKey() {
            return tokenKey;
        }

        public int getMaxTokens() {
            return maxTokens;
        }
    }

    public static class DefinitionParser extends AbstractSingleBeanDefinitionParser<Factory> {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            attributesToProperties(element, builder, "tokenKey");
        }
    }

    /**
     * pull tool factory��
     */
    public static class Factory implements ToolFactory {
        private HttpServletRequest request;

        @Autowired
        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }

        public boolean isSingleton() {
            return true;
        }

        public Object createTool() throws Exception {
            return new CsrfToken(request);
        }
    }

    private static Logger log = LoggerFactory.getLogger(CsrfToken.class);
    private static final Generator generator = new DefaultGenerator();
    private static final Generator generatorOverride = getGeneratorOverride();

    private static Generator getGeneratorOverride() {
        try {
            return Generator.class.cast(ClassLoaderUtil.newServiceInstance("csrfTokenGeneratorOverride",
                    CsrfToken.class));
        } catch (Exception e) {
            log.warn("Failure in CsrfToken.getGeneratorOverride()", e);
        }

        return null;
    }

    private static Generator getGenerator() {
        return generatorOverride != null ? generatorOverride : generator;
    }

    /**
     * ��������ģ��override����token���㷨��
     */
    public interface Generator {
        String generateUniqueToken();

        String generateLongLiveToken(HttpSession session);
    }

    private static class DefaultGenerator implements Generator {
        private final long seed = System.currentTimeMillis();

        public String generateUniqueToken() {
            return longToString(counter.getAndIncrement()) + longToString(seed + System.currentTimeMillis());
        }

        public String generateLongLiveToken(HttpSession session) {
            String sessionId = assertNotNull(session, "session").getId();
            byte[] digest = DigestUtils.md5(session.getCreationTime() + seed + sessionId);

            return StringUtil.bytesToString(digest);
        }
    }
}
