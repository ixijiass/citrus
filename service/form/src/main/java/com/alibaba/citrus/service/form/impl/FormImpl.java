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
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;

import com.alibaba.citrus.service.form.Form;
import com.alibaba.citrus.service.form.Group;
import com.alibaba.citrus.service.form.MessageContext;
import com.alibaba.citrus.service.form.configuration.FormConfig;
import com.alibaba.citrus.service.form.configuration.GroupConfig;
import com.alibaba.citrus.util.StringUtil;

/**
 * ����һ���û��ύ��form��Ϣ��
 * <p>
 * ע�⣺form�������̰߳�ȫ�ģ����ܱ����̹߳���
 * </p>
 * 
 * @author Michael Zhou
 */
public class FormImpl implements Form {
    protected static final Logger log = LoggerFactory.getLogger(Form.class);
    private final FormConfig formConfig;
    private final String formKey;
    private final boolean forcePostOnly;
    private final Map<String, Group> groups = createLinkedHashMap();
    private final Collection<Group> groupList = Collections.unmodifiableCollection(groups.values());
    private final MessageContext messageContext;
    private boolean valid;
    private SimpleTypeConverter typeConverter;

    /**
     * ����һ����form��
     */
    public FormImpl(FormConfig formConfig, String formKey, boolean forcePostOnly) {
        this.formConfig = formConfig;
        this.formKey = formKey;
        this.messageContext = MessageContextFactory.newInstance(this);
        this.forcePostOnly = forcePostOnly;
    }

    /**
     * ȡ��form��������Ϣ��
     */
    public FormConfig getFormConfig() {
        return formConfig;
    }

    /**
     * ȡ������ת�����͵�converter��
     */
    public TypeConverter getTypeConverter() {
        if (typeConverter == null) {
            typeConverter = new SimpleTypeConverter();
            getFormConfig().getPropertyEditorRegistrar().registerCustomEditors(typeConverter);
        }

        return typeConverter;
    }

    /**
     * �Ƿ�ǿ��Ϊֻ����post����
     */
    public boolean isForcePostOnly() {
        return forcePostOnly;
    }

    /**
     * �ж�form�Ƿ�ͨ����֤��
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * ����form�ĺϷ��ԡ���ֵ�������ӵ���ǰ��״̬�У�<code>this.valid &= valid</code>
     */
    protected void setValid(boolean valid) {
        this.valid &= valid;
    }

    /**
     * ��ʼ��form����form�ָ��ɡ�δ��֤��״̬����󣬵����߿�����������ֵ���ֹ���֤����
     */
    public void init() {
        init(null);
    }

    /**
     * ��request��ʼ��form������requestΪ<code>null</code>����form���óɡ�δ��֤��״̬��������֤����
     */
    public void init(HttpServletRequest request) {
        valid = true;

        // �������group
        groups.clear();

        if (request != null) {
            Set<String> ignoredGroups = createHashSet();
            boolean logStarted = false;

            // ɨ���û�submit����������form�������ҵ����ϸ�ʽ��key��formKey.groupKey.instanceKey.fieldKey
            @SuppressWarnings("unchecked")
            Enumeration<String> e = request.getParameterNames();

            while (e.hasMoreElements()) {
                String key = e.nextElement();
                String[] keyInfo = parseParameterKey(key);

                // keyInfoΪnull��ʾ�ò������Ǵ�form service���ɵģ�����֮
                if (keyInfo != null && isEquals(keyInfo[0], formKey)) {
                    if (!logStarted) {
                        logStarted = true;
                        log.debug("Initializing user-submitted form for validating");
                    }

                    String groupKey = keyInfo[1];
                    String instanceKey = keyInfo[2];
                    String groupInstanceKey = getGroupInstanceKey(groupKey, instanceKey);

                    // �����request�г�ʼ������group instance��
                    // ��ȷ�������ظ���ʼ��ͬһ��group instance��
                    if (!groups.containsKey(groupInstanceKey) && !ignoredGroups.contains(groupInstanceKey)) {
                        GroupConfig groupConfig = getFormConfig().getGroupConfigByKey(groupKey);

                        if (groupConfig == null) {
                            log.debug("No group associated with parameter: {}", key);
                            continue;
                        } else if ((forcePostOnly || groupConfig.isPostOnly())
                                && !"post".equalsIgnoreCase(request.getMethod())) {
                            log.warn("Group {} can only read from POST request: {}", groupConfig.getName(), key);
                            ignoredGroups.add(groupInstanceKey);
                            setValid(false);
                            continue;
                        } else {
                            if (log.isDebugEnabled()) {
                                if (DEFAULT_GROUP_INSTANCE_KEY.equals(instanceKey)) {
                                    log.debug("Initializing form group: {}", groupConfig.getName());
                                } else {
                                    log.debug("Initializing form group: {}[{}]", groupConfig.getName(), instanceKey);
                                }
                            }

                            Group group = new GroupImpl(groupConfig, this, instanceKey);

                            groups.put(groupInstanceKey, group);
                            group.init(request);
                        }
                    }
                }
            }
        }
    }

    /**
     * ������URL�д�������key����������ɹ����򷵻���Ӧ��groupKey��instanceKey��fieldKey�����򷵻�
     * <code>null</code>��
     */
    private String[] parseParameterKey(String paramKey) {
        if (!paramKey.startsWith(FORM_KEY_PREFIX)) {
            return null;
        }

        String[] parts = StringUtil.split(paramKey, FIELD_KEY_SEPARATOR);

        if (parts.length < 4) {
            return null;
        }

        return parts;
    }

    /**
     * ȡ��group instance��key��������������group instance��
     */
    private String getGroupInstanceKey(String groupKey, String instanceKey) {
        return groupKey + '.' + instanceKey;
    }

    /**
     * ��֤����������֤����ǰ������group instance��
     */
    public void validate() {
        valid = true;

        for (Group group : getGroups()) {
            group.validate();
        }
    }

    /**
     * ȡ�ô���form��key��
     */
    public String getKey() {
        return formKey;
    }

    /**
     * ȡ������group���б�
     */
    public Collection<Group> getGroups() {
        return groupList;
    }

    /**
     * ȡ������ָ�����Ƶ�group���б�group���ƴ�Сд�����С�
     */
    public Collection<Group> getGroups(String groupName) {
        List<Group> resultGroups = createArrayList(groups.size());

        for (Group group : groups.values()) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                resultGroups.add(group);
            }
        }

        return resultGroups;
    }

    /**
     * ȡ��Ĭ�ϵ�group instance�������group instance�����ڣ��򴴽�֮��Group���ƴ�Сд�����С�
     */
    public Group getGroup(String groupName) {
        return getGroup(groupName, null, true);
    }

    /**
     * ȡ��group instance�������group instance�����ڣ��򴴽�֮��Group���ƴ�Сд�����С�
     */
    public Group getGroup(String groupName, String instanceKey) {
        return getGroup(groupName, instanceKey, true);
    }

    /**
     * ȡ��group instance�������group instance�����ڣ�����<code>create == true</code>
     * ���򴴽�֮��Group���ƴ�Сд�����С�
     */
    public Group getGroup(String groupName, String instanceKey, boolean create) {
        GroupConfig groupConfig = getFormConfig().getGroupConfig(groupName);

        if (groupConfig == null) {
            return null;
        }

        instanceKey = defaultIfNull(trimToNull(instanceKey), DEFAULT_GROUP_INSTANCE_KEY);

        String groupInstanceKey = getGroupInstanceKey(groupConfig.getKey(), instanceKey);
        Group group = groups.get(groupInstanceKey);

        if (group == null && create) {
            group = new GroupImpl(groupConfig, this, instanceKey);
            groups.put(groupInstanceKey, group);
            group.init();
        }

        return group;
    }

    /**
     * ȡ��form����Ĵ�����Ϣ���ʽ��context����������С���ߺ�����ϵͳ���ԡ�
     */
    protected MessageContext getMessageContext() {
        return messageContext;
    }

    /**
     * ת���������Ķ����ַ�����
     */
    @Override
    public String toString() {
        return "Form[groups: " + getFormConfig().getGroupConfigList().size() + ", group instances: "
                + getGroups().size() + ", valid: " + isValid() + "]";
    }
}
