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

import static com.alibaba.citrus.service.form.FormConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;

import com.alibaba.citrus.service.form.Field;
import com.alibaba.citrus.service.form.Form;
import com.alibaba.citrus.service.form.Group;
import com.alibaba.citrus.service.form.InvalidGroupStateException;
import com.alibaba.citrus.service.form.MessageContext;
import com.alibaba.citrus.service.form.configuration.FieldConfig;
import com.alibaba.citrus.service.form.configuration.GroupConfig;
import com.alibaba.citrus.util.ObjectUtil;
import com.alibaba.citrus.util.StringUtil;

/**
 * �����û����ύ���е�һ���ֶΡ�
 * <p>
 * ע�⣺group�������̰߳�ȫ�ģ����ܱ����̹߳���
 * </p>
 * 
 * @author Michael Zhou
 */
public class GroupImpl implements Group {
    protected static final Logger log = LoggerFactory.getLogger(Group.class);
    private final GroupConfig groupConfig;
    private final Form form;
    private final String groupKey;
    private final String instanceKey;
    private final Map<String, Field> fields = createLinkedHashMap();
    private final Collection<Field> fieldList = Collections.unmodifiableCollection(fields.values());
    private final MessageContext messageContext;
    private boolean validated;
    private boolean valid;

    /**
     * ����һ����group��
     */
    public GroupImpl(GroupConfig groupConfig, Form form, String instanceKey) {
        this.groupConfig = groupConfig;
        this.form = form;
        this.instanceKey = instanceKey;
        this.groupKey = form.getKey() + FIELD_KEY_SEPARATOR + groupConfig.getKey() + FIELD_KEY_SEPARATOR + instanceKey;
        this.messageContext = MessageContextFactory.newInstance(this);
    }

    /**
     * ȡ��group��������Ϣ��
     */
    public GroupConfig getGroupConfig() {
        return groupConfig;
    }

    /**
     * ȡ�ð�����group��form��
     */
    public Form getForm() {
        return form;
    }

    /**
     * ȡ��group name���൱��<code>getGroupConfig().getName()</code>
     */
    public String getName() {
        return getGroupConfig().getName();
    }

    /**
     * ȡ�ô���group��key��
     * <p>
     * �ɹ̶�ǰ׺<code>"_fm"</code>������group������д���ټ���group instance key���ɡ����磺
     * <code>_fm.m._0</code>��
     * </p>
     */
    public String getKey() {
        return groupKey;
    }

    /**
     * ȡ�ñ�ʶ��ǰgroup��instance key��
     */
    public String getInstanceKey() {
        return instanceKey;
    }

    /**
     * �ж�group�Ƿ�ͨ����֤��
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * ����group���Ƿ�ͨ����֤��
     * <p>
     * ע�⣺��ֵ�������ӵ���ǰ��״̬�У�<code>this.valid &= valid</code>
     * </p>
     */
    protected void setValid(boolean valid) {
        this.valid &= valid;
        ((FormImpl) getForm()).setValid(this.valid);
    }

    /**
     * �ж���group�Ƿ���ֵ������֤������������£�<code>isValidated()</code>Ϊ<code>true</code>��
     * <ol>
     * <li>�û��ύ������ǰgroup�ֶεı�����ʱ��Ӧ��group����ʼ������֤��</li>
     * <li>�������<code>validate()</code>���������ַ�ʽ�£�group�е��ֶ�ֵ�����ɳ��������ã�Ч����ͬ�û��ύ��һ����</li>
     * </ol>
     */
    public boolean isValidated() {
        return validated;
    }

    /**
     * ��ʼ��group��
     */
    public void init() {
        init(null);
    }

    /**
     * ��ʼ��group�� ���У� <code>request</code>������<code>null</code>�����
     * <code>request</code>��Ϊ<code>null</code>����ͬʱ��֤����
     */
    public void init(HttpServletRequest request) {
        fields.clear();
        valid = true;
        validated = request != null;

        for (FieldConfig fieldConfig : getGroupConfig().getFieldConfigList()) {
            Field field = new FieldImpl(fieldConfig, this);

            fields.put(StringUtil.toLowerCase(fieldConfig.getName()), field);
            field.init(request);
        }

        // ������֤���ֶΣ���Щvalidator��Ҫ��ȡ����ֶε�ֵ����������Ϊ�˱�����Щvalidator��������������field��ֵ��
        if (request != null) {
            for (Field field : fields.values()) {
                ((FieldImpl) field).validate();
            }
        }
    }

    /**
     * ��֤����������֤����ǰ���ֶ�ֵ��
     * <p>
     * ע�⣬�˷���������<code>isValidated()</code>Ϊ<code>true</code>��
     * </p>
     */
    public void validate() {
        valid = true;
        validated = true;

        for (Field field : getFields()) {
            ((FieldImpl) field).validate();
        }
    }

    /**
     * ȡ������fields���б�
     */
    public Collection<Field> getFields() {
        return fieldList;
    }

    /**
     * ȡ��ָ�����Ƶ�field��field���ƣ���Сд�����У�
     */
    public Field getField(String fieldName) {
        return fields.get(StringUtil.toLowerCase(fieldName));
    }

    /**
     * ȡ��group����Ĵ�����Ϣ���ʽ��context��
     */
    protected MessageContext getMessageContext() {
        return messageContext;
    }

    /**
     * �������е�����ֵ���뵽fields�С�
     * <p>
     * ����<code>isValidated()</code>Ϊ<code>true</code>��group���÷�����Ч��
     * </p>
     */
    public void mapTo(Object object) {
        if (isValidated() || object == null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Mapping properties to fields: group=\"{}\", object={}", getName(),
                    ObjectUtil.identityToString(object));
        }

        BeanWrapper bean = new BeanWrapperImpl(object);
        getForm().getFormConfig().getPropertyEditorRegistrar().registerCustomEditors(bean);

        for (Field field : getFields()) {
            String propertyName = field.getFieldConfig().getPropertyName();

            if (bean.isReadableProperty(propertyName)) {
                Object propertyValue = bean.getPropertyValue(propertyName);
                Class<?> propertyType = bean.getPropertyType(propertyName);
                PropertyEditor editor = bean.findCustomEditor(propertyType, propertyName);

                if (editor == null) {
                    editor = BeanUtils.findEditorByConvention(propertyType);
                }

                if (editor == null) {
                    if (propertyType.isArray() || CollectionFactory.isApproximableCollectionType(propertyType)) {
                        field.setValues((String[]) bean.convertIfNecessary(propertyValue, String[].class));
                    } else {
                        field.setValue(bean.convertIfNecessary(propertyValue, String.class));
                    }
                } else {
                    editor.setValue(propertyValue);
                    field.setValue(editor.getAsText());
                }
            } else {
                log.debug("No readable property \"{}\" found in type {}", propertyName, object.getClass().getName());
            }
        }
    }

    /**
     * ��group�е�ֵ����ָ������
     * <p>
     * ����<code>isValidated()</code>Ϊ<code>false</code>��group���÷�����Ч��
     * </p>
     */
    public void setProperties(Object object) {
        if (!isValidated() || object == null) {
            return;
        }

        if (isValid()) {
            if (log.isDebugEnabled()) {
                log.debug("Set validated properties of group \"" + getName() + "\" to object "
                        + ObjectUtil.identityToString(object));
            }

            BeanWrapper bean = new BeanWrapperImpl(object);
            getForm().getFormConfig().getPropertyEditorRegistrar().registerCustomEditors(bean);

            for (Field field : getFields()) {
                String propertyName = field.getFieldConfig().getPropertyName();

                if (bean.isWritableProperty(propertyName)) {
                    PropertyDescriptor pd = bean.getPropertyDescriptor(propertyName);
                    MethodParameter mp = BeanUtils.getWriteMethodParameter(pd);
                    Object value = field.getValueOfType(pd.getPropertyType(), mp, null);

                    bean.setPropertyValue(propertyName, value);
                } else {
                    log.debug("No writable property \"{}\" found in type {}", propertyName, object.getClass().getName());
                }
            }
        } else {
            throw new InvalidGroupStateException("Attempted to call setProperties from an invalid input");
        }
    }

    /**
     * ת���������Ķ����ַ�����
     */
    @Override
    public String toString() {
        return "Group[name: " + getName() + "." + getInstanceKey() + ", fields: "
                + getGroupConfig().getFieldConfigList().size() + ", validated: " + isValidated() + ", valid: "
                + isValid() + "]";
    }
}
