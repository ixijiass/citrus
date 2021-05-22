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
package com.alibaba.citrus.service.mail.builder;

import static com.alibaba.citrus.service.mail.MailConstant.*;
import static com.alibaba.citrus.service.mail.builder.MailAddressType.*;
import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.alibaba.citrus.service.mail.MailService;
import com.alibaba.citrus.service.mail.util.MailUtil;
import com.alibaba.citrus.util.Assert.ExceptionType;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ����һ��javamail����Ĺ����ࡣ
 * <p>
 * <code>MailBuilder</code>��������״̬�ģ����ܱ�����߳�ͬʱʹ�á�
 * </p>
 * 
 * @author Michael Zhou
 */
public class MailBuilder implements Cloneable {
    private MailService mailService;
    private final Set<InternetAddress>[] addresses;
    private final Map<String, Object> attributes;
    private String id;
    private String charset;
    private String subject;
    private Date sentDate;
    private MailContent content;
    private transient Session session;

    @SuppressWarnings("unchecked")
    public MailBuilder() {
        this.addresses = (Set<InternetAddress>[]) new Set<?>[MailAddressType.values().length];
        this.attributes = createHashMap();
    }

    /**
     * ��ȸ���һ��mail builder��
     */
    @Override
    public MailBuilder clone() {
        MailBuilder copy = new MailBuilder();

        copy.mailService = mailService;
        copy.id = id;
        copy.charset = charset;

        for (int i = 0; i < addresses.length; i++) {
            Set<InternetAddress> addrSet = addresses[i];

            if (addrSet != null && !addrSet.isEmpty()) {
                copy.addresses[i] = createLinkedHashSet(addrSet);
            }
        }

        copy.attributes.putAll(attributes);
        copy.subject = subject;
        copy.sentDate = sentDate;

        if (content != null) {
            copy.setContent(content.clone());
        }

        return copy;
    }

    /**
     * ȡ�ô�mail builder������service��
     */
    public MailService getMailService() {
        return mailService;
    }

    /**
     * ���ô�mail builder������service��
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * ȡ��mail builder��ID��
     */
    public String getId() {
        return id;
    }

    /**
     * ����mail builder��ID��
     */
    public void setId(String id) {
        this.id = trimToNull(id);
    }

    /**
     * ȡ�õ�ǰmail builder��session��
     * <p>
     * ֻ����build�׶η��ɵõ���ֵ��
     * </p>
     */
    public Session getSession() {
        return assertNotNull(session, ExceptionType.ILLEGAL_STATE, "Not in build time");
    }

    /**
     * ȡ���ʼ������⡣
     */
    public String getSubject() {
        return subject;
    }

    /**
     * �����ʼ������⡣
     */
    public void setSubject(String subject) {
        this.subject = trimToNull(subject);
    }

    /**
     * ȡ�������ʼ�ʱʹ�õı����ַ��������δָ�����򷵻�Ĭ���ַ���<code>UTF-8</code>��
     */
    public String getCharacterEncoding() {
        return getDefaultCharsetIfNull(charset);
    }

    /**
     * ���������ʼ�ʱʹ�õı����ַ�����
     */
    public void setCharacterEncoding(String javaCharset) {
        javaCharset = trimToNull(javaCharset);

        String oldCharset = getCharacterEncoding();
        String newCharset = getDefaultCharsetIfNull(javaCharset);

        if (!oldCharset.equals(newCharset)) {
            this.charset = javaCharset;
            updateAddressCharset(newCharset);
        }
    }

    private void updateAddressCharset(String newCharset) {
        String mimeCharset = MimeUtility.mimeCharset(newCharset);

        for (Set<InternetAddress> addrSet : addresses) {
            if (addrSet != null) {
                for (InternetAddress addr : addrSet) {
                    try {
                        addr.setPersonal(addr.getPersonal(), mimeCharset);
                    } catch (UnsupportedEncodingException e) {
                        invalidCharset(newCharset, e);
                    }
                }
            }
        }
    }

    private static String getDefaultCharsetIfNull(String charset) {
        return defaultIfNull(charset, DEFAULT_CHARSET);
    }

    /**
     * ȡ��ָ�����͵����е�ַ�����δ���ø����͵ĵ�ַ���򷵻ؿ����顣
     */
    public InternetAddress[] getAddresses(MailAddressType addrType) {
        Set<InternetAddress> addrSet = getAddressSet(addrType, false);

        if (addrSet == null) {
            return new InternetAddress[0];
        } else {
            return addrSet.toArray(new InternetAddress[addrSet.size()]);
        }
    }

    /**
     * ����ʼ���ַ��
     */
    public void addAddress(MailAddressType addrType, String addrList) throws InvalidAddressException {
        if (isEmpty(addrList)) {
            return;
        }

        InternetAddress[] addrs;
        String javaCharset = getCharacterEncoding();

        try {
            addrs = MailUtil.parse(addrList, javaCharset);
        } catch (AddressException e) {
            throw new InvalidAddressException("Invalid mail address: " + addrList, e);
        } catch (UnsupportedEncodingException e) {
            invalidCharset(javaCharset, e);
            return;
        }

        Set<InternetAddress> addrSet = getAddressSet(addrType, true);

        for (InternetAddress addr : addrs) {
            addrSet.add(addr);
        }
    }

    /**
     * �����ʼ���ַ��
     * <p>
     * ��<code>addAddress</code>������ͬ���÷������ԭ�еĵ�ַ��
     * </p>
     */
    public void setAddress(MailAddressType addrType, String addr) throws InvalidAddressException {
        getAddressSet(addrType, true).clear();
        addAddress(addrType, addr);
    }

    /**
     * ȡ���ʼ����ݡ�
     */
    public MailContent getContent() {
        return content;
    }

    /**
     * ȡ��ָ��ID��content��
     * <p>
     * ���contentδָ��ID�����޷��ҵ���
     * </p>
     * <p>
     * �����ID��Ӧ��contentʵ��δ�ҵ����򷵻�<code>null</code>��
     * </p>
     */
    public MailContent getContent(String id) {
        return findContent(trimToNull(id), content);
    }

    /**
     * �����ʼ����ݡ�
     */
    public void setContent(MailContent content) {
        MailContent oldContent = this.content;

        this.content = content;
        this.content.setMailBuilder(this);

        if (oldContent != null) {
            oldContent.setMailBuilder(null);
        }
    }

    /**
     * ȡ�÷������ڣ����δ���ã���ȡ�õ�ǰʱ�䡣
     */
    public Date getSentDate() {
        if (sentDate == null) {
            sentDate = new Date();
        }

        return sentDate;
    }

    /**
     * ���÷������ڡ�
     */
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * ȡ�ð󶨵Ķ���
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * ȡ��attributes��key���ϡ�
     */
    public Set<String> getAttributeKeys() {
        return attributes.keySet();
    }

    /**
     * ��ָ���Ķ���
     */
    public void setAttribute(String key, Object object) {
        if (object == null) {
            attributes.remove(key);
        } else {
            attributes.put(key, object);
        }
    }

    /**
     * �����󶨶���
     */
    public void setAttributes(Map<String, Object> attrs) {
        attributes.putAll(attrs);
    }

    /**
     * ת����javamail�ʼ�����
     */
    public MimeMessage getMessage(Session session) throws MailBuilderException {
        this.session = assertNotNull(session, "session");

        MimeMessage message = new MimeMessage(session);

        try {
            if (content != null) {
                content.render(message);
            } else {
                message.setContent(EMPTY_STRING, "text/plain");
            }
        } catch (MessagingException e) {
            throw new MailBuilderException("Failed to render content", e);
        }

        try {
            // from addresses
            message.addFrom(getAddresses(FROM));

            // recipients addresses
            message.setRecipients(Message.RecipientType.TO, getAddresses(TO));
            message.setRecipients(Message.RecipientType.CC, getAddresses(CC));
            message.setRecipients(Message.RecipientType.BCC, getAddresses(BCC));

            // reply to addresses
            message.setReplyTo(getAddresses(REPLY_TO));

            // subject
            message.setSubject(MailUtil.encodeHeader(getSubject(), getCharacterEncoding()));

            // sent date
            message.setSentDate(getSentDate());
        } catch (MessagingException e) {
            throw new MailBuilderException("Failed to create javamail message", e);
        } catch (UnsupportedEncodingException e) {
            invalidCharset(getCharacterEncoding(), e);
            return null;
        }

        return message;
    }

    /**
     * ��javamail�ʼ�����ת�����ı���ʽ�����ʽΪ��׼��<code>.eml</code>��ʽ��
     */
    public String getMessageAsString(Session session) throws MailBuilderException {
        Message message = getMessage(session);

        try {
            return MailUtil.toString(message, getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            invalidCharset(getCharacterEncoding(), e);
            return null;
        } catch (MessagingException e) {
            throw new MailBuilderException(e);
        }
    }

    /**
     * ��javamail�ʼ����������ָ�����С�
     */
    public void writeTo(OutputStream ostream, Session session) throws MailBuilderException, IOException {
        Message message = getMessage(session);

        try {
            message.writeTo(ostream);
        } catch (MessagingException e) {
            throw new MailBuilderException(e);
        }
    }

    /**
     * ȡ��ָ�����͵��ʼ���ַ���ϡ�
     * <p>
     * ��������͵�ַ�����ڣ�����<code>null</code>������<code>create==true</code>������Զ��������ϡ�
     * </p>
     */
    private Set<InternetAddress> getAddressSet(MailAddressType addrType, boolean create) {
        assertNotNull(addrType, "addressType");

        int index = addrType.ordinal();
        assertTrue(index < addresses.length, "internal state inconsistent");

        Set<InternetAddress> addrSet = addresses[index];

        if (addrSet == null && create) {
            addrSet = createLinkedHashSet();
            addresses[index] = addrSet;
        }

        return addrSet;
    }

    /**
     * �ݹ����ָ��ID��content��
     */
    private MailContent findContent(String id, MailContent content) {
        MailContent result = null;

        if (id != null && content != null) {
            String contentId = content.getId();

            if (id.equals(contentId)) {
                result = content;
            } else if (content instanceof Multipart) {
                for (MailContent subcontent : ((Multipart) content).getContents()) {
                    result = findContent(id, subcontent);

                    if (result != null) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    private void invalidCharset(String charset, UnsupportedEncodingException e) {
        StringBuilder message = new StringBuilder();
        String id = getId();

        message.append("Invalid charset \"").append(charset).append("\"");

        if (!isEmpty(id)) {
            message.append(" specified at mail (id=\"").append(id).append("\")");
        }

        throw new MailBuilderException(message.toString(), e);
    }

    @Override
    public String toString() {
        MapBuilder mb = new MapBuilder();

        if (getId() != null) {
            mb.append("id", getId());
        }

        mb.append("subject", getSubject());
        mb.append("charset", getCharacterEncoding());
        mb.append("sentDate", sentDate); // don't use getSentDate()

        for (MailAddressType addrType : MailAddressType.values()) {
            InternetAddress[] addrs = getAddresses(addrType);

            if (isEmptyArray(addrs)) {
                mb.append(addrType.name(), EMPTY_STRING);
            } else if (addrs.length == 1) {
                mb.append(addrType.name(), addrs[0]);
            } else {
                mb.append(addrType.name(), addrs);
            }
        }

        mb.append("attributes", attributes);
        mb.append("content", content);

        return new ToStringBuilder().append(getClass().getSimpleName()).append(mb).toString();
    }
}
