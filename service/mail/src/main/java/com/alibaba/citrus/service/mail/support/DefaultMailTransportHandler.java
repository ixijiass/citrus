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
package com.alibaba.citrus.service.mail.support;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;

import com.alibaba.citrus.service.mail.MailException;
import com.alibaba.citrus.service.mail.builder.MailBuilder;
import com.alibaba.citrus.service.mail.session.MailTransportHandler;

/**
 * Ĭ�ϵķ���e-mail�Ĵ�������
 * <p>
 * ��Ҫע����ǣ�<code>TransportListener</code>���첽�ġ����������ʱ�ӣ�
 * <code>TransportListener</code>�еķ�����һ���ᱻ�������á�
 * </p>
 * 
 * @author Michael Zhou
 */
public abstract class DefaultMailTransportHandler implements MailTransportHandler {
    /**
     * Ԥ�������ӡ�
     */
    public void prepareConnection(Transport transport) throws MailException, MessagingException {
    }

    /**
     * Ԥ�����ʼ���
     */
    public void prepareMessage(MailBuilder builder) throws MailException {
    }

    /**
     * �����ʼ���
     */
    public void processMessage(Message message) throws MailException, MessagingException {
    }

    /**
     * ����ʼ������ͳɹ���
     */
    public void messageDelivered(TransportEvent transportEvent) {
    }

    /**
     * ����ʼ�δ���ͳɹ���
     */
    public void messageNotDelivered(TransportEvent transportEvent) {
    }

    /**
     * ����ʼ����ַ��ͳɹ���
     */
    public void messagePartiallyDelivered(TransportEvent transportEvent) {
    }
}
