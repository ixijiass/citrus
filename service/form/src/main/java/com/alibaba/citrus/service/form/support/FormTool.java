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

import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.ObjectUtil.*;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.ecs.xhtml.input;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.form.Field;
import com.alibaba.citrus.service.form.Form;
import com.alibaba.citrus.service.form.FormService;
import com.alibaba.citrus.service.form.Group;
import com.alibaba.citrus.service.pull.ToolFactory;
import com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext;
import com.alibaba.citrus.service.requestcontext.util.RequestContextUtil;
import com.alibaba.citrus.springext.support.parser.AbstractSingleBeanDefinitionParser;
import com.alibaba.citrus.util.StringEscapeUtil;

/**
 * ����ģ��ʹ�õ�pull tool��
 * 
 * @author Michael Zhou
 */
public class FormTool implements ToolFactory {
    private FormService formService;
    private HttpServletRequest request;

    @Autowired
    public void setFormService(FormService formService) {
        this.formService = formService;
    }

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = assertProxy(request);
    }

    /**
     * ÿ���󷵻���ͬ��form tool��
     */
    public boolean isSingleton() {
        return true;
    }

    public Object createTool() throws Exception {
        return this;
    }

    /**
     * ȡ��ָ�����Ƶ�group helper��
     */
    public GroupHelper get(String groupName) {
        return new GroupHelper(groupName);
    }

    /**
     * ȡ������groupʵ����
     */
    public Iterator<GroupInstanceHelper> getGroups() {
        return new GroupInstanceHelperIterator(getForm().getGroups().iterator());
    }

    /**
     * ȡ��ָ�����Ƶ�groupʵ����
     */
    public Iterator<GroupInstanceHelper> getGroups(String groupName) {
        return new GroupInstanceHelperIterator(getForm().getGroups(groupName).iterator());
    }

    /**
     * �ж�����form�Ƿ�ͨ����֤��
     */
    public boolean isValid() {
        return getForm().isValid();
    }

    private Form getForm() {
        return formService.getForm();
    }

    @Override
    public String toString() {
        return formService == null ? "FormTool[no FormService]" : formService.toString();
    }

    /**
     * ����ģ��ʹ�õĸ����ࡣ
     */
    public class GroupHelper {
        private final String groupName;

        /**
         * ����group helper��
         */
        public GroupHelper(String groupName) {
            this.groupName = groupName;
        }

        /**
         * ȡ�õ�ǰgroup��Ĭ��instance��
         */
        public GroupInstanceHelper getDefaultInstance() {
            Group group = getForm().getGroup(groupName);
            return group == null ? null : new GroupInstanceHelper(group);
        }

        /**
         * ȡ�õ�ǰgroup��ָ��instance��
         */
        public GroupInstanceHelper getInstance(String instanceName) {
            Group group = getForm().getGroup(groupName, instanceName);
            return group == null ? null : new GroupInstanceHelper(group);
        }

        /**
         * ȡ�õ�ǰgroup��ָ��instance��
         */
        public GroupInstanceHelper getInstance(String instanceName, boolean create) {
            Group group = getForm().getGroup(groupName, instanceName, create);
            return group == null ? null : new GroupInstanceHelper(group);
        }

        @Override
        public String toString() {
            return "Group[" + groupName + "]";
        }
    }

    /**
     * ����ģ��ʹ�õĸ����ࡣ
     */
    public class GroupInstanceHelper {
        private final Group group;

        /**
         * ����group instance helper��
         */
        public GroupInstanceHelper(Group group) {
            this.group = group;
        }

        /**
         * ȡ��ָ�����Ƶ�field helper��
         */
        public FieldHelper get(String fieldName) {
            Field field = group.getField(fieldName);
            return field == null ? null : new FieldHelper(field);
        }

        /**
         * ȡ�����е�fields��
         */
        public Iterator<FieldHelper> getFields() {
            return new FieldHelperIterator(group.getFields().iterator());
        }

        /**
         * �ж�����group instance�Ƿ�ͨ����֤��
         */
        public boolean isValid() {
            return group.isValid();
        }

        /**
         * �ж����group��û�б���֤����
         */
        public boolean isValidated() {
            return group.isValidated();
        }

        /**
         * �������е�ֵ���뵱ǰgroup instance�С�
         */
        public void mapTo(Object object) {
            group.mapTo(object);
        }

        @Override
        public String toString() {
            return group.toString();
        }
    }

    /**
     * ȡ��group instance helper�ı�������
     */
    private class GroupInstanceHelperIterator extends FilterIterator<GroupInstanceHelper, Group> {
        private GroupInstanceHelperIterator(Iterator<Group> i) {
            super(i);
        }

        public GroupInstanceHelper next() {
            return new GroupInstanceHelper(i.next());
        }
    }

    /**
     * ����ģ��ʹ�õĸ����ࡣ
     */
    public class FieldHelper {
        private final Field field;
        private String htmlFieldSuffix;

        /**
         * ����field helper��
         */
        public FieldHelper(Field field) {
            this.field = field;
        }

        /**
         * ȡ��field��������ʾ�����ơ�
         */
        public String getDisplayName() {
            return field.getFieldConfig().getDisplayName();
        }

        /**
         * ȡ����form��Ψһ��ʾ��field��key��
         */
        public String getKey() {
            return field.getKey();
        }

        /**
         * ȡ����form��Ψһ��ʾ��field��key��
         */
        public String getHtmlKey() {
            if (htmlFieldSuffix == null) {
                htmlFieldSuffix = RequestContextUtil.findRequestContext(request, ParserRequestContext.class)
                        .getHtmlFieldSuffix();
            }

            return field.getKey() + htmlFieldSuffix;
        }

        /**
         * ȡ����form��Ψһ��ʾ��field��key�����û��ύ�ı���δ������field����Ϣʱ��ȡ���key��ֵ��Ϊ��field��ֵ��
         */
        public String getAbsentKey() {
            return field.getAbsentKey();
        }

        /**
         * ȡ����form�к͵�ǰfield�󶨵ĸ�����key��
         */
        public String getAttachmentKey() {
            return field.getAttachmentKey();
        }

        /**
         * ȡ��field��ֵ��
         */
        public String getValue() {
            return field.getStringValue();
        }

        /**
         * ȡ��field��ֵ��������<code>escapeHtml</code>���롣
         */
        public String getEscapedValue() {
            return StringEscapeUtil.escapeHtml(field.getStringValue());
        }

        /**
         * ȡ��field��ֵ��
         */
        public String[] getValues() {
            return field.getStringValues();
        }

        /**
         * ȡ��field��ֵ��������<code>escapeHtml</code>���롣
         */
        public String[] getEscapedValues() {
            String[] values = field.getStringValues();
            String[] escapedValues = new String[values.length];

            for (int i = 0; i < values.length; i++) {
                escapedValues[i] = StringEscapeUtil.escapeHtml(values[i]);
            }

            return escapedValues;
        }

        /**
         * ȡ��absent�ֶε�HTML hidden field��
         */
        public input getAbsentHiddenField(String value) {
            return new input("hidden", field.getAbsentKey(), defaultIfNull(value, EMPTY_STRING));
        }

        /**
         * ȡ�ø�����
         */
        public Object getAttachment() {
            return field.getAttachment();
        }

        /**
         * ȡ�ñ����ĸ�����
         */
        public String getAttachmentEncoded() {
            return defaultIfNull(field.getAttachmentEncoded(), EMPTY_STRING);
        }

        /**
         * �Ƿ����������
         */
        public boolean hasAttachment() {
            return field.hasAttachment();
        }

        /**
         * ���ø�����
         */
        public void setAttachment(Object attachment) {
            field.setAttachment(attachment);
        }

        /**
         * ȡ�ô�������HTML hidden field��
         */
        public input getAttachmentHiddenField() {
            return new input("hidden", field.getAttachmentKey(), getAttachmentEncoded());
        }

        /**
         * ���������
         */
        public void clearAttachment() {
            field.clearAttachment();
        }

        /**
         * �ж����field�Ƿ��ǺϷ��ġ�
         */
        public boolean isValid() {
            return field.isValid();
        }

        /**
         * ȡ�����field���Ӧ�ĳ�����Ϣ��
         */
        public String getMessage() {
            return field.getMessage();
        }

        @Override
        public String toString() {
            return field.toString();
        }
    }

    /**
     * ȡ��field helper�ı�������
     */
    private class FieldHelperIterator extends FilterIterator<FieldHelper, Field> {
        private FieldHelperIterator(Iterator<Field> i) {
            super(i);
        }

        public FieldHelper next() {
            return new FieldHelper(i.next());
        }
    }

    private static abstract class FilterIterator<E, F> implements Iterator<E> {
        protected final Iterator<F> i;

        public FilterIterator(Iterator<F> i) {
            this.i = assertNotNull(i);
        }

        public boolean hasNext() {
            return i.hasNext();
        }

        public void remove() {
            i.remove();
        }
    }

    public static class DefinitionParser extends AbstractSingleBeanDefinitionParser<FormTool> {
    }
}
