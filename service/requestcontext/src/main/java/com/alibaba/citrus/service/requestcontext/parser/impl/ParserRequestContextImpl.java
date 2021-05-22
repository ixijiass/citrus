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
package com.alibaba.citrus.service.requestcontext.parser.impl;

import static com.alibaba.citrus.util.Assert.*;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.PropertyEditorRegistrar;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.parser.CookieParser;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParserFilter;
import com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestContextWrapper;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestWrapper;
import com.alibaba.citrus.service.upload.UploadService;
import com.alibaba.citrus.util.StringUtil;

/**
 * �Զ�����request parameters��cookie parameters����͸���ش���upload�����request contextʵ�֡�
 */
public class ParserRequestContextImpl extends AbstractRequestContextWrapper implements ParserRequestContext {
    private PropertyEditorRegistrar propertyEditorRegistrar;
    private boolean converterQuiet;
    private String caseFolding;
    private boolean autoUpload;
    private boolean unescapeParameters;
    private boolean useServletEngineParser;
    private boolean useBodyEncodingForURI;
    private String uriEncoding;
    private boolean trimming;
    private UploadService upload;
    private ParameterParser parameters;
    private ParameterParserFilter[] filters;
    private String htmlFieldSuffix;
    private CookieParser cookies;

    /**
     * ��װһ��<code>RequestContext</code>����
     * 
     * @param wrappedContext ����װ��<code>RequestContext</code>
     */
    public ParserRequestContextImpl(RequestContext wrappedContext) {
        super(wrappedContext);
        setRequest(new RequestWrapper(wrappedContext.getRequest()));
    }

    /**
     * ȡ������ת���������͵�propertyEditorע������
     */
    public PropertyEditorRegistrar getPropertyEditorRegistrar() {
        return propertyEditorRegistrar;
    }

    /**
     * ��������ת���������͵�propertyEditorע������
     */
    public void setPropertyEditorRegistrar(PropertyEditorRegistrar propertyEditorRegistrar) {
        this.propertyEditorRegistrar = propertyEditorRegistrar;
    }

    /**
     * ����ת������ʱ���Ƿ񲻱������Ƿ���Ĭ��ֵ��
     */
    public boolean isConverterQuiet() {
        return converterQuiet;
    }

    /**
     * ��������ת������ʱ���Ƿ񲻱������Ƿ���Ĭ��ֵ��
     */
    public void setConverterQuiet(boolean converterQuiet) {
        this.converterQuiet = converterQuiet;
    }

    /**
     * �Ƿ��Զ�ִ��Upload��
     */
    public boolean isAutoUpload() {
        return autoUpload;
    }

    /**
     * �Ƿ��Զ�ִ��Upload��
     */
    public void setAutoUpload(boolean autoUpload) {
        this.autoUpload = autoUpload;
    }

    /**
     * ����ָ���ķ��ת��parameters��cookies�����ƣ�Ĭ��Ϊ��Сд���»��ߡ���
     */
    public String getCaseFolding() {
        return caseFolding;
    }

    /**
     * ����ָ���ķ��ת��parameters��cookies�����ƣ�Ĭ��Ϊ��Сд���»��ߡ���
     */
    public void setCaseFolding(String folding) {
        this.caseFolding = folding;
    }

    /**
     * �Ƿ�Բ�������HTML entities���룬Ĭ��Ϊ<code>false</code>��
     */
    public boolean isUnescapeParameters() {
        return unescapeParameters;
    }

    /**
     * �Ƿ�Բ�������HTML entities���룬Ĭ��Ϊ<code>false</code>��
     */
    public void setUnescapeParameters(boolean unescapeParameters) {
        this.unescapeParameters = unescapeParameters;
    }

    /**
     * �Ƿ�ʹ��servlet�����parser��Ĭ��Ϊ<code>false</code>��
     */
    public void setUseServletEngineParser(boolean useServletEngineParser) {
        this.useServletEngineParser = useServletEngineParser;
    }

    /**
     * �Ƿ�ʹ��servlet�����parser��Ĭ��Ϊ<code>false</code>��
     */
    public boolean isUseServletEngineParser() {
        return useServletEngineParser;
    }

    /**
     * �Ƿ���request.setCharacterEncoding��ָ���ı���������query��Ĭ��Ϊ<code>true</code>��
     */
    public boolean isUseBodyEncodingForURI() {
        return useBodyEncodingForURI;
    }

    /**
     * �Ƿ���request.setCharacterEncoding��ָ���ı���������query��Ĭ��Ϊ<code>true</code>��
     */
    public void setUseBodyEncodingForURI(boolean useBodyEncodingForURI) {
        this.useBodyEncodingForURI = useBodyEncodingForURI;
    }

    /**
     * ��<code>useServletEngineParser==false</code>����
     * <code>useBodyEncodingForURI=false</code>ʱ���øñ���������GET����Ĳ�����
     */
    public String getURIEncoding() {
        return uriEncoding;
    }

    /**
     * ��<code>useServletEngineParser==false</code>����
     * <code>useBodyEncodingForURI=false</code>ʱ���øñ���������GET����Ĳ�����
     */
    public void setURIEncoding(String uriEncoding) {
        this.uriEncoding = uriEncoding;
    }

    /**
     * �Ƿ�������������trimming��Ĭ��Ϊ<code>true</code>��
     */
    public boolean isTrimming() {
        return trimming;
    }

    /**
     * �Ƿ�������������trimming��Ĭ��Ϊ<code>true</code>��
     */
    public void setTrimming(boolean trimming) {
        this.trimming = trimming;
    }

    /**
     * ����upload service��
     * 
     * @param upload <code>UploadService</code>����
     */
    public void setUploadService(UploadService upload) {
        this.upload = upload;
    }

    /**
     * �������ڹ��˲�����filters��
     */
    public void setParameterParserFilters(ParameterParserFilter[] filters) {
        this.filters = filters;
    }

    /**
     * ȡ�ô���HTML�ֶεĺ�׺��
     */
    public String getHtmlFieldSuffix() {
        return htmlFieldSuffix;
    }

    /**
     * ���ô���HTML�ֶεĺ�׺��
     */
    public void setHtmlFieldSuffix(String htmlFieldSuffix) {
        this.htmlFieldSuffix = htmlFieldSuffix;
    }

    /**
     * ȡ������query��������һ��ִ�д˷���ʱ���������request������ȡ�����еĲ�����
     * 
     * @return <code>ParameterParser</code>ʵ��
     */
    public ParameterParser getParameters() {
        if (parameters == null) {
            parameters = new ParameterParserImpl(this, upload, trimming, filters, htmlFieldSuffix);
        }

        return parameters;
    }

    /**
     * ȡ������cookie����һ��ִ�д˷���ʱ���������request������ȡ������cookies��
     * 
     * @return <code>CookieParser</code>ʵ��
     */
    public CookieParser getCookies() {
        if (cookies == null) {
            cookies = new CookieParserImpl(this);
        }

        return cookies;
    }

    /**
     * ��ָ�����ַ�������<code>getCaseFolding()</code>�����ã�ת����ָ����Сд��ʽ��
     * 
     * @param str Ҫת�����ַ���
     * @return ת������ַ���
     */
    public String convertCase(String str) {
        String folding = getCaseFolding();

        str = StringUtil.trimToEmpty(str);

        if (URL_CASE_FOLDING_LOWER.equals(folding)) {
            str = StringUtil.toLowerCase(str);
        } else if (URL_CASE_FOLDING_LOWER_WITH_UNDERSCORES.equals(folding)) {
            str = StringUtil.toLowerCaseWithUnderscores(str);
        } else if (URL_CASE_FOLDING_UPPER.equals(folding)) {
            str = StringUtil.toUpperCase(str);
        } else if (URL_CASE_FOLDING_UPPER_WITH_UNDERSCORES.equals(folding)) {
            str = StringUtil.toUpperCaseWithUnderscores(str);
        }

        return str;
    }

    /**
     * ��װrequest��
     */
    private class RequestWrapper extends AbstractRequestWrapper {
        private final ParameterMap parameterMap = new ParameterMap();

        public RequestWrapper(HttpServletRequest request) {
            super(ParserRequestContextImpl.this, request);
        }

        @Override
        public String getParameter(String key) {
            return getParameters().getString(key);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return parameterMap;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return new IteratorEnumeration<String>(getParameters().keySet().iterator());
        }

        @Override
        public String[] getParameterValues(String key) {
            return getParameters().getStrings(key);
        }
    }

    private class IteratorEnumeration<E> implements Enumeration<E> {
        private Iterator<E> iterator;

        public IteratorEnumeration(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        public E nextElement() {
            return iterator.next();
        }
    }

    /**
     * һ����ParameterParserΪ������map��
     */
    private class ParameterMap extends AbstractMap<String, String[]> {
        private final ParameterEntrySet entrySet = new ParameterEntrySet();

        @Override
        public boolean containsKey(Object key) {
            try {
                return getParameters().containsKey((String) key);
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override
        public String[] get(Object key) {
            try {
                return getParameters().getStrings((String) key);
            } catch (ClassCastException e) {
                return null;
            }
        }

        @Override
        public Set<java.util.Map.Entry<String, String[]>> entrySet() {
            return entrySet;
        }
    }

    private class ParameterEntrySet extends AbstractSet<Map.Entry<String, String[]>> {
        @Override
        public Iterator<Map.Entry<String, String[]>> iterator() {
            final Iterator<String> i = getParameters().keySet().iterator();

            return new Iterator<Map.Entry<String, String[]>>() {
                public boolean hasNext() {
                    return i.hasNext();
                }

                public Entry<String, String[]> next() {
                    return new ParameterEntry(i.next());
                }

                public void remove() {
                    unsupportedOperation();
                }
            };
        }

        @Override
        public int size() {
            return getParameters().size();
        }
    }

    private class ParameterEntry implements Map.Entry<String, String[]> {
        private final String key;

        private ParameterEntry(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public String[] getValue() {
            return getParameters().getStrings(key);
        }

        public String[] setValue(String[] value) {
            unsupportedOperation();
            return null;
        }
    }
}
