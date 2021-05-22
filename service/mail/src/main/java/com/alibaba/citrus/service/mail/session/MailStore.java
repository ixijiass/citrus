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
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;

import com.alibaba.citrus.service.mail.MailException;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ����e-mail���ࡣ
 * <p>
 * ���౻��Ƴɡ���״̬�ġ���Ҳ����˵���ܱ�����̹߳���
 * </p>
 * 
 * @author Michael Zhou
 */
public class MailStore extends MailSession {
    private String storeProtocol;
    private String storeFolder;
    private MailStoreHandler handler;
    private Store store;

    /**
     * ����һ��mail store��
     */
    public MailStore() {
    }

    /**
     * ����һ��mail store��
     */
    public MailStore(MailStore store, Properties overrideProps) {
        super(store, overrideProps);
        this.storeProtocol = store.storeProtocol;
        this.storeFolder = store.storeFolder;
    }

    /**
     * ȡ��mail store��Э�顣
     */
    public String getProtocol() {
        return defaultIfNull(storeProtocol, DEFAULT_MAIL_STORE_PROTOCOL);
    }

    /**
     * ����mail store��Э�顣
     */
    public void setProtocol(String protocol) {
        this.storeProtocol = trimToNull(protocol);
    }

    /**
     * ȡ��mail store���ļ��С�
     */
    public String getFolder() {
        return defaultIfNull(storeFolder, DEFAULT_MAIL_STORE_FOLDER);
    }

    /**
     * ����mail store���ļ��С�
     */
    public void setFolder(String folder) {
        this.storeFolder = trimToNull(folder);
    }

    /**
     * ȡ�ý���e-mail�Ĵ������
     */
    public MailStoreHandler getHandler() {
        return handler;
    }

    /**
     * ���ý���e-mail�Ĵ������
     */
    public void setHandler(MailStoreHandler newHandler) {
        if (newHandler != null) {
            this.handler = newHandler;
        }
    }

    /**
     * �ж��Ƿ��Ѿ������ϡ�
     */
    @Override
    public boolean isConnected() {
        return store != null && store.isConnected();
    }

    /**
     * ����mail��������
     */
    @Override
    public void connect() throws MailException {
        if (!isConnected()) {
            try {
                store = getSession().getStore(getProtocol());
                store.connect(getHost(), getPort(), getUser(), getPassword());

                if (getHandler() != null) {
                    getHandler().prepareConnection(store);
                }
            } catch (NoSuchProviderException e) {
                store = null;
                throw new MailException("Could not find a provider of " + getProtocol() + " protocol", e);
            } catch (MessagingException me) {
                store = null;
                throw new MailException("Could not connect to the store", me);
            }
        }
    }

    /**
     * �ر�mail�����������ӡ�
     */
    @Override
    public void close() {
        if (store != null) {
            try {
                store.close();
            } catch (MessagingException e) {
            } finally {
                store = null;
            }
        }
    }

    /**
     * �����ʼ���
     */
    public void receive() throws MailException {
        receive(null);
    }

    /**
     * �����ʼ���
     */
    public void receive(MailStoreHandler handler) throws MailException {
        Folder inbox = null;
        boolean autoClose = false;

        setHandler(handler);

        if (!isConnected()) {
            autoClose = true;
            connect();
        }

        try {
            inbox = store.getFolder(getFolder());
            inbox.open(Folder.READ_WRITE);

            int messageCount = inbox.getMessageCount();

            if (getHandler() != null) {
                int max = getHandler().getMessageCount(messageCount);

                if (max >= 0 && max <= messageCount) {
                    messageCount = max;
                }
            }

            for (int i = 1; i <= messageCount; i++) {
                Message message = inbox.getMessage(i);
                boolean deleteMessage = false;

                if (getHandler() != null) {
                    deleteMessage = getHandler().processMessage(message);
                }

                message.setFlag(Flags.Flag.DELETED, deleteMessage);
            }
        } catch (MessagingException me) {
            throw new MailException("Could not receive messages", me);
        } finally {
            try {
                if (inbox != null && inbox.isOpen()) {
                    inbox.close(true);
                }
            } catch (MessagingException e) {
            }

            if (autoClose) {
                close();
            }
        }
    }

    @Override
    protected void toString(MapBuilder mb) {
        mb.append("protocol", getProtocol());
        mb.append("folder", getFolder());
    }
}
