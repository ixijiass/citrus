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

import java.util.Properties;

import com.alibaba.citrus.service.mail.builder.MailBuilder;
import com.alibaba.citrus.service.mail.session.MailStore;
import com.alibaba.citrus.service.mail.session.MailTransport;

/**
 * ���ɺͷ���e-mail�ķ���
 * 
 * @author Michael Zhou
 */
public interface MailService extends MailSettings {
    /**
     * ȡ��ָ�����Ƶ�mail builder��
     */
    MailBuilder getMailBuilder(String id) throws MailNotFoundException;

    /**
     * ȡ��Ĭ�ϵ�mail store��
     */
    MailStore getMailStore() throws MailStoreNotFoundException;

    /**
     * ȡ��Ĭ�ϵ�mail store��
     */
    MailStore getMailStore(Properties overrideProps) throws MailStoreNotFoundException;

    /**
     * ȡ��ָ�����Ƶ�mail store��
     */
    MailStore getMailStore(String id) throws MailStoreNotFoundException;

    /**
     * ȡ��ָ�����Ƶ�mail store��
     */
    MailStore getMailStore(String id, Properties overrideProps) throws MailStoreNotFoundException;

    /**
     * ȡ��Ĭ�ϵ�mail transport��
     */
    MailTransport getMailTransport() throws MailTransportNotFoundException;

    /**
     * ȡ��Ĭ�ϵ�mail transport��
     */
    MailTransport getMailTransport(Properties overrideProps) throws MailTransportNotFoundException;

    /**
     * ȡ��ָ�����Ƶ�mail transport��
     */
    MailTransport getMailTransport(String id) throws MailTransportNotFoundException;

    /**
     * ȡ��ָ�����Ƶ�mail transport��
     */
    MailTransport getMailTransport(String id, Properties overrideProps) throws MailTransportNotFoundException;
}
