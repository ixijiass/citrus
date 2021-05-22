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
package com.alibaba.citrus.service.form.support;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.StringUtil.*;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;

import com.alibaba.citrus.expr.Expression;
import com.alibaba.citrus.expr.ExpressionContext;
import com.alibaba.citrus.expr.ExpressionFactory;
import com.alibaba.citrus.expr.ExpressionParseException;
import com.alibaba.citrus.expr.composite.CompositeExpressionFactory;
import com.alibaba.citrus.service.form.Validator;
import com.alibaba.citrus.service.form.configuration.FieldConfig;
import com.alibaba.citrus.service.form.configuration.FormConfig;
import com.alibaba.citrus.service.form.configuration.GroupConfig;
import com.alibaba.citrus.springext.support.BeanSupport;
import com.alibaba.citrus.util.ObjectUtil;
import com.alibaba.citrus.util.StringEscapeUtil;
import com.alibaba.citrus.util.i18n.LocaleUtil;

/**
 * �����<code>Validator</code>ʵ�֡�
 * 
 * @author Michael Zhou
 */
public abstract class AbstractValidator extends BeanSupport implements Validator, MessageSourceAware {
    protected static final ExpressionFactory EXPRESSION_FACTORY = new CompositeExpressionFactory();
    private String id;
    private String messageCode;
    private Message message;
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * �Ƿ���Ҫ���messageֵ��
     * <p>
     * ����ǣ���messageδָ��ʱ������
     * </p>
     */
    protected boolean requiresMessage() {
        return true;
    }

    /**
     * ��GroupConfig����ʼ������Ժ󱻵��ã���ʱ��ȡ��ͬ����������fields��
     */
    public void init(FieldConfig fieldConfig) throws Exception {
        if (requiresMessage()) {
            boolean hasMessage = false;

            // 1. ��messageSource�в���
            if (id != null && messageSource != null) {
                GroupConfig groupConfig = fieldConfig.getGroupConfig();
                FormConfig formConfig = groupConfig.getFormConfig();

                // form.groupName.fieldName.validatorId
                messageCode = formConfig.getMessageCodePrefix() + groupConfig.getName() + "." + fieldConfig.getName()
                        + "." + id;

                hasMessage = getMessageFromMessageSource() != null;
            }

            // 2. ���messageSource���Ҳ�������validator��������message
            if (!hasMessage) {
                assertNotNull(message, "no message");
                message.compile();
            }
        }
    }

    private String getMessageFromMessageSource() {
        try {
            return messageSource.getMessage(messageCode, null, LocaleUtil.getContext().getLocale());
        } catch (NoSuchMessageException e) {
            return null;
        }
    }

    /**
     * ȡ��validator��ID��ͨ����ID�����ҵ�ָ����validator��
     */
    public String getId() {
        return id == null ? getBeanName() : id;
    }

    /**
     * ����validator��ID��ͨ����ID�����ҵ�ָ����validator��
     */
    public void setId(String id) {
        this.id = trimToNull(id);
    }

    /**
     * ȡ�ó�����Ϣ��
     */
    public final String getMessage(Context context) {
        // ���ȣ�����message�Ѿ������ã���ֱ�ӷ��ء�
        // ����all-of-validator�ͻ��������message��
        String result = trimToNull(context.getMessage());

        if (result == null) {
            // Ȼ�����Ų���message source������id���ڣ�
            Message message = this.message;

            if (messageCode != null && messageSource != null) {
                String messageFromMessageSource = getMessageFromMessageSource();

                if (messageFromMessageSource != null) {
                    message = new Message(messageFromMessageSource);
                    message.compile();
                }
            }

            // ��Ⱦmessage
            if (message != null) {
                result = message.getMessageString(context.getMessageContext());
            }
        }

        return result;
    }

    /**
     * ���ó�����Ϣ��
     */
    public void setMessage(String message) {
        this.message = new Message(message);
    }

    /**
     * ���ɸ�����
     */
    @Override
    public Validator clone() {
        try {
            return (Validator) super.clone();
        } catch (CloneNotSupportedException e) {
            return null; // �����ܷ�����
        }
    }

    /**
     * ����һ��message���ʽ��
     */
    protected static class Message implements Cloneable {
        private String message;
        private Expression messageExpression;

        public Message(String message) {
            this.message = trimToNull(message);
        }

        /**
         * ������ʽ��
         */
        public void compile() {
            assertNotNull(message, "message");

            try {
                messageExpression = EXPRESSION_FACTORY.createExpression(message);
            } catch (ExpressionParseException e) {
                throw new IllegalArgumentException("Invalid message for validator " + getClass().getSimpleName()
                        + ": \"" + StringEscapeUtil.escapeJava(message) + "\"");
            }
        }

        /**
         * ȡ�ó�����Ϣ��
         */
        public String getMessageString(ExpressionContext context) {
            return ObjectUtil.toString(messageExpression.evaluate(context), "");
        }

        @Override
        public String toString() {
            return "ValidatorMessage[" + message + "]";
        }
    }
}
