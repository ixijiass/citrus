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
package com.alibaba.citrus.service.mail.session;

import static com.alibaba.citrus.service.mail.MailConstant.*;
import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Transport;

import com.alibaba.citrus.service.mail.MailException;
import com.alibaba.citrus.service.mail.MailNotFoundException;
import com.alibaba.citrus.service.mail.MailService;
import com.alibaba.citrus.service.mail.MailStoreNotFoundException;
import com.alibaba.citrus.service.mail.builder.MailBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ��������e-mail���ࡣ
 * <p>
 * ���౻��Ƴɡ���״̬�ġ���Ҳ����˵���ܱ�����̹߳���
 * </p>
 * 
 * @author Michael Zhou
 */
public class MailTransport extends MailSession {
    private String transportProtocol;
    private String popBeforeSmtpId;
    private MailTransportHandler handler;
    private Transport transport;

    /**
     * ����һ��mail transport��
     */
    public MailTransport() {
    }

    /**
     * ����һ��mail transport��
     */
    public MailTransport(MailTransport transport, Properties overrideProps) {
        super(transport, overrideProps);
        this.transportProtocol = transport.transportProtocol;
        this.popBeforeSmtpId = transport.popBeforeSmtpId;
    }

    /**
     * ȡ��mail transport��Э�顣
     */
    public String getProtocol() {
        return defaultIfNull(transportProtocol, DEFAULT_MAIL_TRANSPORT_PROTOCOL);
    }

    /**
     * ����mail transport��Э�顣
     */
    public void setProtocol(String protocol) {
        this.transportProtocol = trimToNull(protocol);
    }

    /**
     * ȡ��pop before smtp��store ID��
     */
    public String getPopBeforeSmtp() {
        return popBeforeSmtpId;
    }

    /**
     * ����pop before smtp��store ID��
     */
    public void setPopBeforeSmtp(String popBeforeSmtpId) {
        this.popBeforeSmtpId = trimToNull(popBeforeSmtpId);
    }

    /**
     * ȡ��session properties��
     */
    @Override
    protected Properties getSessionProperties() {
        setProperty(SMTP_AUTH, String.valueOf(useAuth()), "false");
        return super.getSessionProperties();
    }

    /**
     * ȡ�÷���e-mail�Ĵ������
     */
    public MailTransportHandler getHandler() {
        return handler;
    }

    /**
     * ���÷���e-mail�Ĵ������
     */
    public void setHandler(MailTransportHandler newHandler) {
        if (this.handler != null && this.transport != null) {
            this.transport.removeTransportListener(this.handler);
        }

        if (newHandler != null) {
            this.handler = newHandler;
        }

        if (this.handler != null && this.transport != null) {
            this.transport.addTransportListener(this.handler);
        }
    }

    /**
     * �ж��Ƿ��Ѿ������ϡ�
     */
    @Override
    public boolean isConnected() {
        return transport != null && transport.isConnected();
    }

    /**
     * ����mail��������
     */
    @Override
    public void connect() throws MailException {
        if (!isConnected()) {
            try {
                transport = getSession().getTransport(getProtocol());
                setHandler(null);
                connectPopBeforeSmtp();
                transport.connect(getHost(), getPort(), getUser(), getPassword());

                if (getHandler() != null) {
                    getHandler().prepareConnection(transport);
                }
            } catch (NoSuchProviderException e) {
                transport = null;
                throw new MailException("Could not find a provider of " + getProtocol() + " protocol", e);
            } catch (MessagingException me) {
                transport = null;
                throw new MailException("Could not connect to the transport", me);
            }
        }
    }

    /**
     * �ر�mail�����������ӡ�
     */
    @Override
    public void close() throws MailException {
        if (transport != null) {
            try {
                transport.close();
            } catch (MessagingException e) {
                // ignore
            } finally {
                transport = null;
            }
        }
    }

    /**
     * ����һ��email��
     */
    public void send(String mailId) throws MailException {
        send(mailId, null);
    }

    /**
     * ����һ��email��
     */
    public void send(String mailId, MailTransportHandler handler) throws MailException {
        MailService service = getMailService();

        if (service == null) {
            throw new MailNotFoundException("Could not find mail \"" + mailId + "\": mail service is not set");
        }

        MailBuilder builder = service.getMailBuilder(mailId);

        send(builder, handler);
    }

    /**
     * ����һ��email��
     */
    public void send(MailBuilder builder) throws MailException {
        send(builder, null);
    }

    /**
     * ����һ��email��
     */
    public void send(MailBuilder builder, MailTransportHandler handler) throws MailException {
        setHandler(handler);

        if (getHandler() != null) {
            getHandler().prepareMessage(builder);
        }

        send(builder.getMessage(getSession()), getHandler());
    }

    /**
     * ����һ��email��
     */
    public void send(Message message) throws MailException {
        send(message, null);
    }

    /**
     * ����һ��email��
     */
    public void send(Message message, MailTransportHandler handler) throws MailException {
        boolean autoClose = false;

        setHandler(handler);

        if (!isConnected()) {
            autoClose = true;
            connect();
        }

        try {
            message.setSentDate(new Date());

            if (getHandler() != null) {
                getHandler().processMessage(message);
            }

            message.saveChanges();

            Address[] recipients = message.getAllRecipients();

            if (isEmptyArray(recipients)) {
                throw new MailException("No recipient was specified in mail");
            }

            transport.sendMessage(message, recipients);
        } catch (MessagingException me) {
            throw new MailException("Could not send message", me);
        } finally {
            if (autoClose) {
                close();
            }
        }
    }

    /**
     * ��һЩ������Ҫ������smtp֮ǰ����pop3��������ȥ��֤��
     */
    private void connectPopBeforeSmtp() throws MailException {
        if (popBeforeSmtpId != null) {
            MailService service = getMailService();

            if (service == null) {
                throw new MailStoreNotFoundException("Could not find mail store \"" + popBeforeSmtpId
                        + "\": mail service is not set");
            }

            MailStore popBeforeSmtpStore = assertNotNull(service.getMailStore(popBeforeSmtpId),
                    "popBeforeSmtpStore: %s", popBeforeSmtpId);

            try {
                popBeforeSmtpStore.connect();
            } finally {
                popBeforeSmtpStore.close();
            }
        }
    }

    @Override
    protected void toString(MapBuilder mb) {
        mb.append("protocol", getProtocol());
        mb.append("popBeforeSmtp", getPopBeforeSmtp());
    }
}
