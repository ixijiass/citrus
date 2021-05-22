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
package com.alibaba.citrus.service.form.impl;

import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import com.alibaba.citrus.expr.ExpressionContext;
import com.alibaba.citrus.service.form.Field;
import com.alibaba.citrus.service.form.Form;
import com.alibaba.citrus.service.form.Group;
import com.alibaba.citrus.service.form.MessageContext;
import com.alibaba.citrus.service.form.Validator;
import com.alibaba.citrus.util.Utils;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

public class MessageContextFactory {
    /**
     * ����form����Ĵ�����Ϣcontext�������������ݣ�
     * <ol>
     * <li>����ϵͳ���ԣ�<code>System.getProperties()</code>��</li>
     * <li>����С�����磺<code>stringUtil</code>��<code>stringEscapeUtil</code>�ȡ�</li>
     * </ol>
     */
    public static MessageContext newInstance(final Form form) {
        MessageContext formContext = new MessageContext() {
            private static final long serialVersionUID = 3833185835016140853L;

            @Override
            protected Object internalGet(String key) {
                return null;
            }

            @Override
            public ExpressionContext getParentContext() {
                return null;
            }

            @Override
            protected void buildToString(ToStringBuilder sb) {
                sb.append("FormMessageContext");
            }

            @Override
            protected void buildToString(MapBuilder mb) {
                mb.append("form", form);
            }
        };

        Map<String, Object> utils = Utils.getUtils();

        formContext.putAll(System.getProperties());
        formContext.putAll(utils);

        return formContext;
    }

    /**
     * ����group����Ĵ�����Ϣcontext�������������ݣ�
     * <ol>
     * <li>Form�����context���������ݡ�</li>
     * <li><code>form</code>ָ��ǰ������</li>
     * <li>Group�е�����field��</li>
     * </ol>
     */
    public static MessageContext newInstance(final Group group) {
        MessageContext groupContext = new MessageContext() {
            private static final long serialVersionUID = 3258407326913149238L;

            @Override
            protected Object internalGet(String key) {
                Object value = null;

                // ����fields
                value = group.getField(key);

                if (value == null) {
                    if ("form".equals(key)) {
                        value = group.getForm();
                    }
                }

                return value;
            }

            @Override
            public ExpressionContext getParentContext() {
                return ((FormImpl) group.getForm()).getMessageContext();
            }

            @Override
            protected void buildToString(ToStringBuilder sb) {
                sb.append("GroupMessageContext");
            }

            @Override
            protected void buildToString(MapBuilder mb) {
                mb.append("group", group);
            }
        };

        return groupContext;
    }

    /**
     * ����field����Ĵ�����Ϣcontext�������������ݣ�
     * <ol>
     * <li>Group�����context���������ݡ�</li>
     * <li><code>group</code>ָ��ǰ�����</li>
     * <li>Field��������ԣ��磺<code>displayName</code>��<code>value</code>��
     * <code>values</code>��<code>defaultValue</code>��<code>defaultValues</code>��
     * </li>
     * </ol>
     */
    public static MessageContext newInstance(final Field field) {
        MessageContext fieldContext = new MessageContext() {
            private static final long serialVersionUID = 3258130258607026229L;
            private BeanWrapper fieldWrapper;

            @Override
            protected Object internalGet(String key) {
                if (fieldWrapper == null) {
                    fieldWrapper = new BeanWrapperImpl(field);
                    field.getGroup().getForm().getFormConfig().getPropertyEditorRegistrar()
                            .registerCustomEditors(fieldWrapper);
                }

                // ��fieldʵ���в���property
                try {
                    return fieldWrapper.getPropertyValue(key);
                } catch (BeansException e) {
                    return null;
                }
            }

            @Override
            public ExpressionContext getParentContext() {
                return ((GroupImpl) field.getGroup()).getMessageContext();
            }

            @Override
            protected void buildToString(ToStringBuilder sb) {
                sb.append("FieldMessageContext");
            }

            @Override
            protected void buildToString(MapBuilder mb) {
                mb.append("field", field);
            }
        };

        return fieldContext;
    }

    /**
     * ����validator����Ĵ�����Ϣcontext�������������ݣ�
     * <ol>
     * <li>Field�����context���������ݡ�</li>
     * <li>Validator������������ԡ�</li>
     * </ol>
     */
    public static MessageContext newInstance(final Field field, final Validator validator) {
        MessageContext validatorContext = new MessageContext() {
            private static final long serialVersionUID = 3616450081390475317L;
            private BeanWrapper validatorWrapper;

            @Override
            protected Object internalGet(String key) {
                if (validatorWrapper == null) {
                    validatorWrapper = new BeanWrapperImpl(validator);
                    field.getGroup().getForm().getFormConfig().getPropertyEditorRegistrar()
                            .registerCustomEditors(validatorWrapper);
                }

                // ��validator object�в���property
                try {
                    return validatorWrapper.getPropertyValue(key);
                } catch (BeansException e) {
                    return null;
                }
            }

            @Override
            public ExpressionContext getParentContext() {
                return ((FieldImpl) field).getMessageContext();
            }

            @Override
            protected void buildToString(ToStringBuilder sb) {
                sb.append("ValidatorMessageContext");
            }

            @Override
            protected void buildToString(MapBuilder mb) {
                mb.append("validator", validator);
            }
        };

        return validatorContext;
    }
}
