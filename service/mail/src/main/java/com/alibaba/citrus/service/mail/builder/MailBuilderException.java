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

import com.alibaba.citrus.service.mail.MailException;

/**
 * 代表一个mail builder创建邮件失败的异常。
 * 
 * @author Michael Zhou
 */
public class MailBuilderException extends MailException {
    private static final long serialVersionUID = 6335985167965343989L;

    public MailBuilderException() {
        super();
    }

    public MailBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailBuilderException(String message) {
        super(message);
    }

    public MailBuilderException(Throwable cause) {
        super(cause);
    }
}
