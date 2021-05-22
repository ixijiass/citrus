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
package com.alibaba.citrus.service.mail;

/**
 * Mail��س�����
 * 
 * @author Michael Zhou
 */
public final class MailConstant {
    /** ��û��ָ��text���͵�content��charsetʱ, ʹ��Ĭ��ֵ�� */
    public final static String DEFAULT_CHARSET = "UTF-8";

    /** ������뷽ʽ��header���� */
    public final static String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

    /** ����������header���� */
    public final static String CONTENT_DESCRIPTION = "Content-Description";

    /** Content ID��header���� */
    public final static String CONTENT_ID = "Content-ID";

    /** Ĭ�ϵĴ�����롣 */
    public final static String DEFAULT_TRANSFER_ENCODING = "8bit";

    /** ������ContentType�е�charset���ơ� */
    public final static String CONTENT_TYPE_CHARSET = "charset";

    /** ���ı���content type�� */
    public final static String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    /** ��HTML��content type�� */
    public final static String CONTENT_TYPE_TEXT_HTML = "text/html";

    /** ����message��content type�� */
    public final static String CONTENT_TYPE_MESSAGE = "message/rfc822";

    /** ��Ƕ��Դ��HTML��content type�����͡� */
    public final static String CONTENT_TYPE_MULTIPART_SUBTYPE_RELATED = "related";

    /** ѡ����multipart��content type�����͡� */
    public final static String CONTENT_TYPE_MULTIPART_SUBTYPE_ALTERNATIVE = "alternative";

    /** �����multipart��content type�����͡� */
    public final static String CONTENT_TYPE_MULTIPART_SUBTYPE_MIXED = "mixed";

    /** Ĭ�ϵ�mail-storeЭ�顣 */
    public final static String DEFAULT_MAIL_STORE_PROTOCOL = "pop3";

    /** Ĭ�ϵ�mail-transportЭ�顣 */
    public final static String DEFAULT_MAIL_TRANSPORT_PROTOCOL = "smtp";

    /** �趨��Ҫ��֤��smtp���������ơ� */
    public final static String SMTP_AUTH = "mail.smtp.auth";

    /** Ĭ�ϵ�mail-store��folder���� */
    public final static String DEFAULT_MAIL_STORE_FOLDER = "INBOX";

    /** Ĭ�ϵ�store��transport��ID�� */
    public final static String DEFAULT_MAIL_SESSION_ID = "_DEFAULT_";

    /** �Ƿ��debug��Ϣ�� */
    public final static String MAIL_DEBUG = "mail.debug";
}
