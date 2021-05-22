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
package com.alibaba.citrus.service.mail.util;

import static com.alibaba.citrus.service.mail.MailConstant.*;
import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static javax.mail.internet.MimeUtility.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;

import com.alibaba.citrus.util.io.ByteArray;
import com.alibaba.citrus.util.io.ByteArrayOutputStream;

/**
 * �й�javamail�Ĺ����ࡣ
 * 
 * @author Michael Zhou
 */
public class MailUtil {
    /**
     * ���ָ��<code>javaCharset</code>Ϊ�հף��򷵻�Ĭ��charset�����򷵻�ԭֵ��
     */
    public static String getJavaCharset(String javaCharset) {
        javaCharset = trimToNull(javaCharset);
        return defaultIfNull(javaCharset, DEFAULT_CHARSET);
    }

    /**
     * ��javamail�ʼ�����ת�����ı���ʽ�����ʽΪ��׼��<code>.eml</code>��ʽ��
     */
    public static String toString(Message message) throws MessagingException {
        try {
            return toString(message, null);
        } catch (UnsupportedEncodingException e) {
            unexpectedException(e);
            return null;
        }
    }

    /**
     * ��javamail�ʼ�����ת�����ı���ʽ�����ʽΪ��׼��<code>.eml</code>��ʽ��
     */
    public static String toString(Message message, String javaCharset) throws MessagingException,
            UnsupportedEncodingException {
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        try {
            message.writeTo(ostream);
        } catch (IOException e) {
            unexpectedException(e);
        } finally {
            ostream.close();
        }

        ByteArray bytes = ostream.toByteArray();

        javaCharset = getJavaCharset(javaCharset);

        return new String(bytes.getRawBytes(), bytes.getOffset(), bytes.getLength(), javaCharset);
    }

    /**
     * ����RFC822/MIME�������ʼ�header��ʹ֮����RFC2047��
     * <p>
     * ����󲿷��ַ�ΪASCII�ַ�������<code>"Q"</code>��ʽ���룬������<code>"B"</code>��ʽ���롣
     * </p>
     * <p>
     * ���<code>javaCharset</code>���б���<code>mimeCharset</code>��Ϊ�գ���ȡĬ��ֵ�����
     * <code>header</code>ֵΪ�գ���ȡ�հס�
     * </p>
     */
    public static String encodeHeader(String header, String javaCharset) throws UnsupportedEncodingException {
        return encodeHeader(header, javaCharset, null);
    }

    /**
     * ����RFC822/MIME�������ʼ�header��ʹ֮����RFC2047��
     * <p>
     * ���<code>javaCharset</code>���б���<code>mimeCharset</code>��Ϊ�գ���ȡĬ��ֵ�����
     * <code>header</code>ֵΪ�գ���ȡ�հס�
     * </p>
     * <p>
     * encoding���뷽ʽ��������<code>"B"</code>��<code>"Q"</code>�������ֵΪ <code>null</code>
     * �����Ҵ󲿷��ַ�ΪASCII�ַ�����Ĭ��Ϊ<code>"Q"</code>������Ĭ��Ϊ <code>"B"</code>��
     * </p>
     */
    public static String encodeHeader(String header, String javaCharset, String encoding)
            throws UnsupportedEncodingException {
        header = defaultIfNull(header, EMPTY_STRING);
        String mimeCharset = MimeUtility.mimeCharset(getJavaCharset(javaCharset));
        return MimeUtility.encodeText(header, mimeCharset, encoding);
    }

    /**
     * ����һ���Զ��Ż�ո�ָ���mail��ַ��
     * <p>
     * ֧�ָ�ʽ��<code>"My Name" &lt;name@addr.com&gt;</code>���������Ʋ���
     * <code>My Name</code>����ָ�� <code>javaCharset</code>�����롣
     * </p>
     * <p>
     * ���Կո�ָ�ʱ����֧�ּ�mail��ַ��ʽ�����������֡����磺<code>name1@addr.com name2@addr.com</code>
     * ��
     * </p>
     */
    public static InternetAddress[] parse(String addrList, String javaCharset) throws AddressException,
            UnsupportedEncodingException {
        return parse(addrList, javaCharset, false);
    }

    /**
     * ����һ���Զ��Ż�ո�ָ���mail��ַ��
     * <p>
     * ֧�ָ�ʽ��<code>"My Name" &lt;name@addr.com&gt;</code>���������Ʋ���
     * <code>My Name</code>����ָ�� <code>javaCharset</code>�����롣
     * </p>
     * <p>
     * ��<code>strict==true</code>ʱ��֧���Կո�ָ��ļ�mail��ַ��ʽ�����������֡����磺
     * <code>name1@addr.com name2@addr.com</code> ��
     * </p>
     */
    public static InternetAddress[] parse(String addrList, String javaCharset, boolean strict) throws AddressException,
            UnsupportedEncodingException {
        InternetAddress[] addrs = InternetAddress.parse(defaultIfNull(addrList, EMPTY_STRING), strict);

        if (!isEmptyArray(addrs)) {
            String mimeCharset = MimeUtility.mimeCharset(getJavaCharset(javaCharset));

            for (InternetAddress addr : addrs) {
                addr.setPersonal(trimToNull(addr.getPersonal()), mimeCharset);
            }
        }

        return addrs;
    }

    /**
     * ȡ��<code>ContentType</code>����
     */
    public static ContentType getContentType(String contentType, String javaCharset) throws ParseException {
        assertNotNull(contentType, "contentType");

        ContentType contentTypeObject = new ContentType(contentType);

        javaCharset = trimToNull(javaCharset);

        if (javaCharset != null) {
            contentTypeObject.setParameter(CONTENT_TYPE_CHARSET, mimeCharset(javaCharset));
        }

        return contentTypeObject;
    }
}
