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

import static com.alibaba.citrus.service.mail.MailConstant.*;

import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

/**
 * ����Ӧ���ʼ����ݡ�
 * <p>
 * ͨ����������ʽͬʱ����һ���ʼ��Ĵ��ı���HTML�汾���ʼ��ͻ��˻��Զ�ѡ����ʾ��һ���汾��
 * ���һ���ʼ��ͻ��˲�֧��HTML���û����������ı����ʼ�����֧��HTML��ƽ̨�ϣ��û���������Ư����HTML�ʼ���
 * </p>
 * 
 * @author Michael Zhou
 */
public class AlternativeMultipartContent extends MultipartContent {
    /**
     * ȡ��<code>Multipart</code>��ʵ�֡�
     */
    @Override
    protected Multipart getMultipart() {
        return new MimeMultipart(CONTENT_TYPE_MULTIPART_SUBTYPE_ALTERNATIVE);
    }

    @Override
    protected AlternativeMultipartContent newInstance() {
        return new AlternativeMultipartContent();
    }
}
