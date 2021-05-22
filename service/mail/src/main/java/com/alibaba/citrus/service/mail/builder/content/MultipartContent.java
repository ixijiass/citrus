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
package com.alibaba.citrus.service.mail.builder.content;

import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

import com.alibaba.citrus.service.mail.builder.MailContent;
import com.alibaba.citrus.util.internal.ToStringBuilder;

/**
 * �ɶಿ�ֹ��ɵ��ʼ����ݡ�
 * 
 * @author Michael Zhou
 */
public abstract class MultipartContent extends AbstractContent implements
        com.alibaba.citrus.service.mail.builder.Multipart {
    private final List<MailContent> contents = createLinkedList();

    /**
     * �������contents��
     */
    public void setContents(MailContent[] contents) {
        if (contents != null) {
            this.contents.clear();

            for (MailContent content : contents) {
                addContent(content);
            }
        }
    }

    /**
     * ���һ�����ݲ��֡�
     */
    public void addContent(MailContent content) {
        if (content != null) {
            content.setParentContent(this);
            contents.add(content);
        }
    }

    /**
     * ȡ�����е���contents��
     */
    public MailContent[] getContents() {
        return contents.toArray(new MailContent[contents.size()]);
    }

    /**
     * ��Ⱦ�ʼ����ݡ�
     */
    public void render(Part mailPart) throws MessagingException {
        Multipart multipart = getMultipart();

        for (MailContent content : contents) {
            MimeBodyPart bodyPart = new MimeBodyPart();
            content.render(bodyPart);
            multipart.addBodyPart(bodyPart);
        }

        mailPart.setContent(multipart);
    }

    /**
     * ȡ��<code>Multipart</code>��ʵ�֡�
     */
    protected abstract Multipart getMultipart();

    /**
     * ��ȸ���һ��content��
     */
    @Override
    protected void copyTo(AbstractContent copy) {
        for (MailContent content : contents) {
            ((MultipartContent) copy).addContent(content.clone());
        }
    }

    @Override
    public void toString(ToStringBuilder buf) {
        buf.append(contents);
    }
}
