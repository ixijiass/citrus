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
package com.alibaba.citrus.service.requestcontext.locale;

import com.alibaba.citrus.service.requestcontext.RequestContext;

/**
 * ʵ����Servlet 2.4�淶�е�response�ķ�����������
 * <ul>
 * <li>response.<code>setCharacterEncoding()</code>������ʹ֮���Է������������ַ����룬������Ҫ������
 * <code>setContentType()</code>������</li>
 * <li>response.<code>getContentType()</code>������ʹ֮����ȡ�õ�ǰ�����content type��</li>
 * </ul>
 * ��������ͱ����ַ�����������
 * <ul>
 * <li><code>LocaleUtil.setContextLocale()</code></li>
 * <li><code>request.setCharacterEncoding()</code></li>
 * <li><code>response.setLocale()</code></li>
 * <li><code>response.setCharacterEncoding()</code>��</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public interface SetLocaleRequestContext extends RequestContext {
    String INPUT_CHARSET_PARAM_DEFAULT = "_input_charset";
    String OUTPUT_CHARSET_PARAM_DEFAULT = "_output_charset";

    /** ��������locale��session key�����ơ� */
    String SESSION_KEY_DEFAULT = "_lang";

    /** ��������locale��parameter key�����ơ� */
    String PARAMETER_KEY_DEFAULT = "_lang";
    String PARAMETER_SET_TO_DEFAULT_VALUE = "default";

    /** Ĭ�ϵ�locale�� */
    String LOCALE_DEFAULT = "en_US";

    /** Ĭ�ϵ�charset�� */
    String CHARSET_DEFAULT = "UTF-8";

    /**
     * ȡ��content type��
     * 
     * @return content type������charset�Ķ���
     */
    String getResponseContentType();

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
    void setResponseContentType(String contentType, boolean appendCharset);

    /**
     * ����response����ַ�����ע�⣬�˷��������ڵ�һ��<code>getWriter</code>֮ǰִ�С�
     * 
     * @param charset ����ַ��������charsetΪ<code>null</code>
     *            �����contentType��ɾ��charset���
     */
    void setResponseCharacterEncoding(String charset);
}
