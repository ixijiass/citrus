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
package com.alibaba.citrus.service.requestcontext.locale.impl;

import static com.alibaba.citrus.util.StringUtil.*;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.locale.SetLocaleRequestContext;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestContextWrapper;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestWrapper;
import com.alibaba.citrus.service.requestcontext.support.AbstractResponseWrapper;
import com.alibaba.citrus.util.i18n.LocaleInfo;
import com.alibaba.citrus.util.i18n.LocaleUtil;

/**
 * <code>SetLocaleRequestContext</code>��ʵ�֡�
 * 
 * @author Michael Zhou
 */
public class SetLocaleRequestContextImpl extends AbstractRequestContextWrapper implements SetLocaleRequestContext {
    private final static Logger log = LoggerFactory.getLogger(SetLocaleRequestContext.class);
    private Pattern inputCharsetPattern;
    private Pattern outputCharsetPattern;
    private Locale defaultLocale;
    private String defaultCharset;
    private String sessionKey;
    private String paramKey;
    private Locale locale;

    /**
     * ��װһ��<code>RequestContext</code>����
     * 
     * @param wrappedContext ����װ��<code>RequestContext</code>
     */
    public SetLocaleRequestContextImpl(RequestContext wrappedContext) {
        super(wrappedContext);
        setRequest(new RequestWrapper(wrappedContext.getRequest()));
        setResponse(new ResponseWrapper(wrappedContext.getResponse()));
    }

    public void setInputCharsetPattern(Pattern inputCharsetPattern) {
        this.inputCharsetPattern = inputCharsetPattern;
    }

    public void setOutputCharsetPattern(Pattern outputCharsetPattern) {
        this.outputCharsetPattern = outputCharsetPattern;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    /**
     * ȡ��content type��
     * 
     * @return content type������charset�Ķ���
     */
    public String getResponseContentType() {
        return ((ResponseWrapper) getResponse()).getContentType();
    }

    /**
     * ����content type�� ���content type������charset������
     * <code>getCharacterEncoding</code>�����ã������charset��ǡ�
     * <p>
     * ���<code>appendCharset</code>Ϊ<code>false</code>����content
     * type�н�������charset��ǡ�
     * </p>
     * 
     * @param contentType content type
     * @param appendCharset ����ַ���
     */
    public void setResponseContentType(String contentType, boolean appendCharset) {
        ((ResponseWrapper) getResponse()).setContentType(contentType, appendCharset);
    }

    /**
     * ����response����ַ�����ע�⣬�˷��������ڵ�һ��<code>getWriter</code>֮ǰִ�С�
     * 
     * @param charset ����ַ��������charsetΪ<code>null</code>
     *            �����contentType��ɾ��charset���
     */
    public void setResponseCharacterEncoding(String charset) {
        ((ResponseWrapper) getResponse()).setCharacterEncoding(charset);
    }

    /**
     * ����locale��
     */
    @Override
    public void prepare() {
        // ���ȴ�session��ȡ��input charset�������õ�request�У��Ա��һ������request parameters��
        LocaleInfo locale = getLocaleFromSession();

        try {
            // ��ͼ��queryString��ȡ��inputCharset
            String queryString = getRequest().getQueryString();
            String inputCharset = locale.getCharset().name();

            if (queryString != null) {
                Matcher matcher = inputCharsetPattern.matcher(queryString);

                if (matcher.find()) {
                    String charset = matcher.group(1);

                    if (LocaleUtil.isCharsetSupported(charset)) {
                        inputCharset = charset;
                    } else {
                        log.warn("Specified input charset is not supported: " + charset);
                    }
                }
            }

            getRequest().setCharacterEncoding(inputCharset);

            log.debug("Set INPUT charset to " + inputCharset);
        } catch (UnsupportedEncodingException e) {
            try {
                getRequest().setCharacterEncoding(CHARSET_DEFAULT);

                log.warn("Unknown charset " + locale.getCharset() + ".  Set INPUT charset to " + CHARSET_DEFAULT);
            } catch (UnsupportedEncodingException ee) {
                log.error("Failed to set INPUT charset to " + locale.getCharset());
            }
        }

        // ��parameter��ȡlocale��Ϣ��������ڣ������õ�cookie�С�
        if (PARAMETER_SET_TO_DEFAULT_VALUE.equalsIgnoreCase(getRequest().getParameter(paramKey))) {
            HttpSession session = getRequest().getSession(false); // ���session�����ڣ�Ҳ���ô���

            if (session != null) {
                session.removeAttribute(sessionKey);
            }

            locale = new LocaleInfo(defaultLocale, defaultCharset);

            log.debug("Reset OUTPUT locale:charset to " + locale);
        } else {
            // ��ͼ��queryString��ȡ��outputCharset
            String queryString = getRequest().getQueryString();
            String outputCharset = null;

            if (queryString != null) {
                Matcher matcher = outputCharsetPattern.matcher(queryString);

                if (matcher.find()) {
                    String charset = matcher.group(1);

                    if (LocaleUtil.isCharsetSupported(charset)) {
                        outputCharset = charset;
                    } else {
                        log.warn("Specified output charset is not supported: " + charset);
                    }
                }
            }

            // ���parameter��ָ����locale����ȡ�ò�����֮
            LocaleInfo paramLocale = getLocaleFromParameter();

            if (paramLocale != null) {
                getRequest().getSession().setAttribute(sessionKey, paramLocale.toString());

                // ��parameter�е�locale��Ϣ����cookie����Ϣ��
                locale = paramLocale;
            }

            if (outputCharset != null) {
                locale = new LocaleInfo(locale.getLocale(), outputCharset);
            }
        }

        // �������������locale��Ϣ��
        getResponse().setLocale(locale.getLocale());
        setResponseCharacterEncoding(locale.getCharset().name());
        log.debug("Set OUTPUT locale:charset to " + locale);

        // ����thread context�е�locale��Ϣ��
        LocaleUtil.setContext(locale.getLocale(), locale.getCharset().name());
        log.debug("Set THREAD CONTEXT locale:charset to " + locale);

        this.locale = locale.getLocale();
    }

    /**
     * �ӵ�ǰ�����session��ȡ���û���locale���á����sessionδ���ã���ȡĬ��ֵ��
     * 
     * @return ��ǰsession�е�locale����
     */
    private LocaleInfo getLocaleFromSession() {
        HttpSession session = getRequest().getSession(false); // ���session�����ڣ�Ҳ���ô�����
        String localeName = session == null ? null : (String) getRequest().getSession().getAttribute(sessionKey);
        LocaleInfo locale = null;

        if (isEmpty(localeName)) {
            locale = new LocaleInfo(defaultLocale, defaultCharset);
        } else {
            locale = LocaleInfo.parse(localeName);

            if (!LocaleUtil.isLocaleSupported(locale.getLocale())
                    || !LocaleUtil.isCharsetSupported(locale.getCharset().name())) {
                log.warn("Invalid locale " + locale + " from session");

                locale = new LocaleInfo(defaultLocale, defaultCharset);
            }
        }

        return locale;
    }

    /**
     * �ӵ�ǰ����Ĳ�����ȡ���û���locale���á��������δ���ã��򷵻�<code>null</code>��
     * 
     * @return ��ǰrequest parameters�е�locale����
     */
    private LocaleInfo getLocaleFromParameter() {
        String localeName = getRequest().getParameter(paramKey);
        LocaleInfo locale = null;

        if (!isEmpty(localeName)) {
            locale = LocaleInfo.parse(localeName);

            if (!LocaleUtil.isLocaleSupported(locale.getLocale())
                    || !LocaleUtil.isCharsetSupported(locale.getCharset().name())) {
                log.warn("Invalid locale " + locale + " from request parameters");

                locale = new LocaleInfo(defaultLocale, defaultCharset);
            }
        }

        return locale;
    }

    /**
     * ��װrequest��
     */
    private class RequestWrapper extends AbstractRequestWrapper {
        public RequestWrapper(HttpServletRequest request) {
            super(SetLocaleRequestContextImpl.this, request);
        }

        @Override
        public Locale getLocale() {
            return locale == null ? super.getLocale() : locale;
        }
    }

    /**
     * ��װresponse��
     */
    private class ResponseWrapper extends AbstractResponseWrapper {
        private String contentType;
        private String charset;

        public ResponseWrapper(HttpServletResponse response) {
            super(SetLocaleRequestContextImpl.this, response);
        }

        /**
         * ȡ��content type��
         * 
         * @return content type������charset�Ķ���
         */
        @Override
        public String getContentType() {
            return contentType;
        }

        /**
         * ����content type�� ���content type������charset������
         * <code>getCharacterEncoding</code>�����ã������charset��ǡ�
         * 
         * @param contentType content type
         */
        @Override
        public void setContentType(String contentType) {
            setContentType(contentType, true);
        }

        /**
         * ����content type�� ���content type������charset������
         * <code>getCharacterEncoding</code>�����ã������charset��ǡ�
         * <p>
         * ���<code>appendCharset</code>Ϊ<code>false</code>����content
         * type�н�������charset��ǡ�
         * </p>
         * 
         * @param contentType content type
         * @param appendCharset ����ַ���
         */
        public void setContentType(String contentType, boolean appendCharset) {
            // ȡ��ָ��contentType�е�"; charset="���֡�
            String charset = trimToNull(substringAfterLast(contentType, "charset="));

            // ���δָ��charset�����this.charset��ȡ��������setCharacterEncoding���������õġ�
            if (charset == null) {
                charset = this.charset;
            }

            // ��ȥcontentType�е�charset���֡�
            this.contentType = trimToNull(substringBefore(contentType, ";"));

            // ����setCharacterEncoding��������charset��
            setCharacterEncoding(appendCharset ? charset : null);
        }

        /**
         * ȡ��response������ַ�����
         */
        @Override
        public String getCharacterEncoding() {
            return super.getCharacterEncoding();
        }

        /**
         * ����response����ַ�����ע�⣬�˷��������ڵ�һ��<code>getWriter</code>֮ǰִ�С�
         * 
         * @param charset ����ַ��������charsetΪ<code>null</code>
         *            �����contentType��ɾ��charset���
         */
        @Override
        public void setCharacterEncoding(String charset) {
            this.charset = charset;

            if (contentType != null) {
                contentType = trimToNull(substringBefore(contentType, ";"));

                if (charset != null) {
                    contentType += "; charset=" + charset;
                }

                log.debug("Set content type to " + contentType);

                super.setContentType(contentType);
            } else {
                // ����û������contentType��ȷ��charset��Ȼ�����á�
                // ������Servlet API 2.4�����°档
                try {
                    super.setCharacterEncoding(charset);
                } catch (NoSuchMethodError e) {
                }
            }
        }
    }
}
