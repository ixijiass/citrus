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
package com.alibaba.citrus.service.mail.impl;

import static com.alibaba.citrus.service.mail.MailConstant.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.alibaba.citrus.service.AbstractService;
import com.alibaba.citrus.service.mail.MailNotFoundException;
import com.alibaba.citrus.service.mail.MailService;
import com.alibaba.citrus.service.mail.MailStoreNotFoundException;
import com.alibaba.citrus.service.mail.MailTransportNotFoundException;
import com.alibaba.citrus.service.mail.builder.MailBuilder;
import com.alibaba.citrus.service.mail.session.MailStore;
import com.alibaba.citrus.service.mail.session.MailTransport;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ���ɺͷ���e-mail�ķ���
 * 
 * @author Michael Zhou
 */
public class MailServiceImpl extends AbstractService<MailService> implements MailService, BeanFactoryAware {
    private final List<Object> importedServices = createLinkedList();
    private final Map<String, MailBuilder> mails = createHashMap();
    private final Map<String, MailStore> mailStores = createHashMap();
    private final Map<String, MailTransport> mailTransports = createHashMap();
    private BeanFactory factory;

    public void setBeanFactory(BeanFactory factory) {
        this.factory = assertNotNull(factory, "beanFactory");
    }

    public void setImportedServices(Object[] services) {
        if (services != null) {
            importedServices.clear();

            for (Object service : services) {
                if (service != null) {
                    importedServices.add(service);
                }
            }
        }
    }

    public void setMails(Map<String, MailBuilder> mails) {
        if (mails != null) {
            this.mails.clear();
            this.mails.putAll(mails);

            // ����mail id��mail service
            for (Map.Entry<String, MailBuilder> entry : mails.entrySet()) {
                String mailId = assertNotNull(trimToNull(entry.getKey()), "mail id");
                MailBuilder builder = assertNotNull(entry.getValue(), "mail builder");

                builder.setId(mailId);
                builder.setMailService(this);
            }
        }
    }

    public void setMailStores(Map<String, MailStore> mailStores) {
        if (mailStores != null) {
            this.mailStores.clear();
            this.mailStores.putAll(mailStores);

            // ���stores��ֻ��һ��store�������ó�default��
            if (mailStores.size() == 1) {
                this.mailStores.put(DEFAULT_MAIL_SESSION_ID, mailStores.values().iterator().next());
            } else {
                // �����һ��mail store�����Ϊdefault��������Ϊdefault store��
                // ���������default stores���򱨴�
                for (MailStore store : mailStores.values()) {
                    if (store.isDefault()) {
                        assertTrue(!this.mailStores.containsKey(DEFAULT_MAIL_SESSION_ID), "more than 1 default stores");
                        this.mailStores.put(DEFAULT_MAIL_SESSION_ID, store);
                    }
                }
            }

            // ����mail service��
            for (MailStore store : this.mailStores.values()) {
                store.setMailService(this);
            }
        }
    }

    public void setMailTransports(Map<String, MailTransport> mailTransports) {
        if (mailTransports != null) {
            this.mailTransports.clear();
            this.mailTransports.putAll(mailTransports);

            // ���transports��ֻ��һ��transport�������ó�default��
            if (mailTransports.size() == 1) {
                this.mailTransports.put(DEFAULT_MAIL_SESSION_ID, mailTransports.values().iterator().next());
            } else {
                // �����һ��mail transport�����Ϊdefault��������Ϊdefault transport��
                // ���������default transports���򱨴�
                for (MailTransport transport : mailTransports.values()) {
                    if (transport.isDefault()) {
                        assertTrue(!this.mailTransports.containsKey(DEFAULT_MAIL_SESSION_ID),
                                "more than 1 default transports");
                        this.mailTransports.put(DEFAULT_MAIL_SESSION_ID, transport);
                    }
                }
            }

            // ����mail service��
            for (MailTransport transport : this.mailTransports.values()) {
                transport.setMailService(this);
            }
        }
    }

    /**
     * ȡ��ָ�����͵ķ��� ���δָ�����������ʹ��Ĭ��ID��������ȡ�÷���
     */
    public <T> T getService(Class<T> serviceType, String defaultServiceId) {
        assertNotNull(serviceType, "serviceType");

        for (Object service : importedServices) {
            if (serviceType.isInstance(service)) {
                return serviceType.cast(service);
            }
        }

        if (factory != null && defaultServiceId != null) {
            return serviceType.cast(factory.getBean(defaultServiceId, serviceType));
        }

        return null;
    }

    /**
     * ȡ��ָ�����Ƶ�mail builder��
     */
    public MailBuilder getMailBuilder(String id) throws MailNotFoundException {
        id = assertNotNull(trimToNull(id), "no mail id");

        MailBuilder builder = mails.get(id);

        if (builder == null) {
            throw new MailNotFoundException("Could not find mail builder: " + id);
        }

        // ����mail builder�ĸ���
        return builder.clone();
    }

    /**
     * ȡ��Ĭ�ϵ�mail store��
     */
    public MailStore getMailStore() throws MailStoreNotFoundException {
        return getMailStore(DEFAULT_MAIL_SESSION_ID, null);
    }

    /**
     * ȡ��Ĭ�ϵ�mail store��
     */
    public MailStore getMailStore(Properties overrideProps) throws MailStoreNotFoundException {
        return getMailStore(DEFAULT_MAIL_SESSION_ID, overrideProps);
    }

    /**
     * ȡ��ָ�����Ƶ�mail store��
     */
    public MailStore getMailStore(String id) throws MailStoreNotFoundException {
        return getMailStore(id, null);
    }

    /**
     * ȡ��ָ�����Ƶ�mail store��
     */
    public MailStore getMailStore(String id, Properties overrideProps) throws MailStoreNotFoundException {
        id = assertNotNull(trimToNull(id), "no mailStore id");

        MailStore store = mailStores.get(id);

        if (store == null) {
            throw new MailStoreNotFoundException("Could not find mail store: " + id);
        }

        // ���ظ���
        return new MailStore(store, overrideProps);
    }

    /**
     * ȡ��Ĭ�ϵ�mail transport��
     */
    public MailTransport getMailTransport() throws MailTransportNotFoundException {
        return getMailTransport(DEFAULT_MAIL_SESSION_ID, null);
    }

    /**
     * ȡ��Ĭ�ϵ�mail transport��
     */
    public MailTransport getMailTransport(Properties overrideProps) throws MailTransportNotFoundException {
        return getMailTransport(DEFAULT_MAIL_SESSION_ID, overrideProps);
    }

    /**
     * ȡ��ָ�����Ƶ�mail transport��
     */
    public MailTransport getMailTransport(String id) throws MailTransportNotFoundException {
        return getMailTransport(id, null);
    }

    /**
     * ȡ��ָ�����Ƶ�mail transport��
     */
    public MailTransport getMailTransport(String id, Properties overrideProps) throws MailTransportNotFoundException {
        id = assertNotNull(trimToNull(id), "no mailTransport id");

        MailTransport transport = mailTransports.get(id);

        if (transport == null) {
            throw new MailTransportNotFoundException("Could not find mail transport: " + id);
        }

        // ���ظ���
        return new MailTransport(transport, overrideProps);
    }

    @Override
    public String toString() {
        MapBuilder mb = new MapBuilder();

        mb.append("mails", mails);
        mb.append("stores", mailStores);
        mb.append("transports", mailTransports);

        return new ToStringBuilder().append(getBeanDescription()).append(mb).toString();
    }
}
