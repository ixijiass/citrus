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
package com.alibaba.citrus.service.requestcontext.parser;

import org.springframework.beans.PropertyEditorRegistrar;

import com.alibaba.citrus.service.requestcontext.RequestContext;

/**
 * �Զ�����request parameters��cookie parameters����͸���ش���upload�����request contextʵ�֡�
 * 
 * @author Michael Zhou
 */
public interface ParserRequestContext extends RequestContext {
    /** �����ļ����Կ�ѡ�����parameters��cookies�����ƽ��д�Сдת���� */
    String URL_CASE_FOLDING_NONE = "none";

    /** �����ļ����Կ�ѡ���parameters��cookies������ת����Сд�� */
    String URL_CASE_FOLDING_LOWER = "lower";

    /** �����ļ����Կ�ѡ���parameters��cookies������ת����Сд���»��ߡ� */
    String URL_CASE_FOLDING_LOWER_WITH_UNDERSCORES = "lower_with_underscores";

    /** �����ļ����Կ�ѡ���parameters��cookies������ת���ɴ�д�� */
    String URL_CASE_FOLDING_UPPER = "upper";

    /** �����ļ����Կ�ѡ���parameters��cookies������ת���ɴ�д���»��ߡ� */
    String URL_CASE_FOLDING_UPPER_WITH_UNDERSCORES = "upper_with_underscores";

    /** Ĭ�ϵı����ַ����� */
    String DEFAULT_CHARSET_ENCODING = "ISO-8859-1";

    /** ��parameters�б�ʾuploadʧ�ܣ����󱻺��ԡ� */
    String UPLOAD_FAILED = "upload_failed";

    /** ��parameters�б�ʾupload�ļ��ߴ糬������ֵ�����󱻺��ԡ� */
    String UPLOAD_SIZE_LIMIT_EXCEEDED = "upload_size_limit_exceeded";

    /**
     * ȡ������ת���������͵�propertyEditorע������
     */
    PropertyEditorRegistrar getPropertyEditorRegistrar();

    /**
     * ����ת������ʱ���Ƿ񲻱������Ƿ���Ĭ��ֵ��
     */
    boolean isConverterQuiet();

    /**
     * �Ƿ��Զ�ִ��Upload��
     */
    boolean isAutoUpload();

    /**
     * ȡ�ô���HTML�ֶεĺ�׺��
     */
    String getHtmlFieldSuffix();

    /**
     * ����ָ���ķ��ת��parameters��cookies�����ƣ�Ĭ��Ϊ��Сд���»��ߡ���
     */
    String getCaseFolding();

    /**
     * �Ƿ�Բ�������HTML entities���룬Ĭ��Ϊ<code>true</code>��
     */
    boolean isUnescapeParameters();

    /**
     * �Ƿ�ʹ��servlet�����parser��Ĭ��Ϊ<code>false</code>��
     */
    boolean isUseServletEngineParser();

    /**
     * �Ƿ���request.setCharacterEncoding��ָ���ı���������query��Ĭ��Ϊ<code>true</code>��
     * <p>
     * ֻ�е�<code>useServletEngineParser==false</code>ʱ����ѡ�����Ч��
     * </p>
     */
    boolean isUseBodyEncodingForURI();

    /**
     * ��<code>useServletEngineParser==false</code>����
     * <code>useBodyEncodingForURI=false</code>ʱ���øñ���������GET����Ĳ�����
     */
    String getURIEncoding();

    /**
     * �Ƿ�������������trimming��Ĭ��Ϊ<code>true</code>��
     */
    boolean isTrimming();

    /**
     * ȡ������query��������һ��ִ�д˷���ʱ���������request������ȡ�����еĲ�����
     * 
     * @return <code>ParameterParser</code>ʵ��
     */
    ParameterParser getParameters();

    /**
     * ȡ������cookie����һ��ִ�д˷���ʱ���������request������ȡ������cookies��
     * 
     * @return <code>CookieParser</code>ʵ��
     */
    CookieParser getCookies();

    /**
     * ��ָ�����ַ�������<code>getCaseFolding()</code>�����ã�ת����ָ����Сд��ʽ��
     * 
     * @param str Ҫת�����ַ���
     * @return ת������ַ���
     */
    String convertCase(String str);
}
