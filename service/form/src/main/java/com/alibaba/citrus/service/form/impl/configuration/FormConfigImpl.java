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
package com.alibaba.citrus.service.form.impl.configuration;

import static com.alibaba.citrus.service.form.FormConstant.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.Assert.ExceptionType.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static java.util.Collections.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.PropertyEditorRegistrar;

import com.alibaba.citrus.service.configuration.support.PropertyEditorRegistrarsSupport;
import com.alibaba.citrus.service.form.FormService;
import com.alibaba.citrus.service.form.configuration.FormConfig;
import com.alibaba.citrus.service.form.configuration.GroupConfig;
import com.alibaba.citrus.service.form.configuration.GroupConfig.Import;

/**
 * ʵ��<code>FormConfig</code>��
 * 
 * @author Michael Zhou
 */
public class FormConfigImpl extends AbstractConfig<FormConfig> implements FormConfig {
    private FormService formService;
    private Map<String, GroupConfigImpl> groups; // group name to groupConfig
    private Map<String, GroupConfigImpl> groupsByKey; // group key to groupConfig
    private List<GroupConfig> groupList; // unmodifiable group list
    private PropertyEditorRegistrarsSupport propertyEditorRegistrars = new PropertyEditorRegistrarsSupport();
    private Boolean converterQuiet;
    private Boolean postOnlyByDefault;
    private String messageCodePrefix;

    /**
     * ȡ�ô�����form��service��
     */
    public FormService getFormService() {
        return formService;
    }

    /**
     * ���ô�����form��service��
     */
    public void setFormService(FormService formService) {
        this.formService = assertNotNull(formService, "formService");
    }

    /**
     * ����ת������ʱ���Ƿ񲻱������Ƿ���Ĭ��ֵ��
     */
    public boolean isConverterQuiet() {
        return converterQuiet == null ? true : converterQuiet.booleanValue();
    }

    /**
     * ��������ת������ʱ���Ƿ񲻱������Ƿ���Ĭ��ֵ��
     */
    public void setConverterQuiet(boolean converterQuiet) {
        this.converterQuiet = converterQuiet;
    }

    /**
     * Group�Ƿ�Ĭ�ϱ����post������ȡ�����ݡ�
     */
    public boolean isPostOnlyByDefault() {
        return postOnlyByDefault == null ? true : postOnlyByDefault.booleanValue();
    }

    /**
     * ����group�Ƿ�Ĭ�ϱ����post������ȡ�����ݡ�
     */
    public void setPostOnlyByDefault(boolean postOnlyByDefault) {
        this.postOnlyByDefault = postOnlyByDefault;
    }

    /**
     * ȡ��message code��ǰ׺��
     * <p>
     * Validator���Դ�spring <code>MessageSource</code>
     * ��ȡ��message���ݡ���������message��codeΪ��
     * <code>messageCodePrefix.groupName.fieldName.validatorId</code>��
     * </p>
     * <p>
     * Ĭ�ϵ�ǰ׺Ϊ��<code>form.</code>��
     * </p>
     */
    public String getMessageCodePrefix() {
        return messageCodePrefix == null ? FORM_MESSAGE_CODE_PREFIX : messageCodePrefix;
    }

    /**
     * ����message code��ǰ׺��
     */
    public void setMessageCodePrefix(String messageCodePrefix) {
        this.messageCodePrefix = normalizeMessageCodePrefix(messageCodePrefix);
    }

    private String normalizeMessageCodePrefix(String messageCodePrefix) {
        messageCodePrefix = trimToNull(messageCodePrefix);

        if (messageCodePrefix != null && !messageCodePrefix.endsWith(".")) {
            messageCodePrefix += ".";
        }

        return messageCodePrefix;
    }

    /**
     * ȡ������group config���б�
     */
    public List<GroupConfig> getGroupConfigList() {
        if (groupList == null) {
            return emptyList();
        } else {
            return groupList;
        }
    }

    /**
     * ȡ��ָ�����Ƶ�group config�����ƴ�Сд�����С� ���δ�ҵ����򷵻�<code>null</code>��
     */
    public GroupConfig getGroupConfig(String groupName) {
        if (groups == null) {
            return null;
        } else {
            return groups.get(caseInsensitiveName(groupName));
        }
    }

    /**
     * ȡ�ú�ָ��key���Ӧ��group config�����δ�ҵ����򷵻�<code>null</code>
     */
    public GroupConfig getGroupConfigByKey(String groupKey) {
        return assertNotNull(groupsByKey, ILLEGAL_STATE, "groupsByKey not inited").get(groupKey);
    }

    /**
     * ����group configs��
     */
    public void setGroupConfigImplList(List<GroupConfigImpl> groupConfigList) {
        if (groupConfigList != null) {
            groups = createLinkedHashMap();

            for (GroupConfigImpl groupConfig : groupConfigList) {
                String groupName = caseInsensitiveName(groupConfig.getName()); // ��Сд�����У�
                assertTrue(!groups.containsKey(groupName), "Duplicated group name: %s", groupConfig.getName());
                groups.put(groupName, groupConfig);
            }
        }
    }

    /**
     * ȡ��<code>PropertyEditor</code>ע������
     * <p>
     * <code>PropertyEditor</code>�����ַ���ֵת����bean property�����ͣ���֮��
     * </p>
     */
    public PropertyEditorRegistrar getPropertyEditorRegistrar() {
        return propertyEditorRegistrars;
    }

    /**
     * ����һ��<code>PropertyEditor</code>ע������
     * <p>
     * <code>PropertyEditor</code>�����ַ���ֵת����bean property�����ͣ���֮��
     * </p>
     */
    public void setPropertyEditorRegistrars(PropertyEditorRegistrar[] registrars) {
        propertyEditorRegistrars.setPropertyEditorRegistrars(registrars);
    }

    /**
     * ��ʼ��form config��
     */
    @Override
    protected void init() throws Exception {
        // ������ʼ������groups
        assertNotNull(groups, "no groups");

        groupsByKey = createHashMap();
        groupList = createArrayList(groups.size());

        for (Map.Entry<String, GroupConfigImpl> entry : groups.entrySet()) {
            String caseInsensitiveName = entry.getKey();
            GroupConfigImpl groupConfig = entry.getValue();

            // ���ò��ظ���key
            for (int i = 1; i <= caseInsensitiveName.length(); i++) {
                String key = caseInsensitiveName.substring(0, i);

                if (!groupsByKey.containsKey(key)) {
                    groupConfig.setKey(key);
                    groupsByKey.put(key, groupConfig);
                    break;
                }
            }

            // ����group.form
            groupConfig.setFormConfig(this);

            // ����groupList
            groupList.add(groupConfig);
        }

        groupList = unmodifiableList(groupList);

        // ����group֮��ļ̳й�ϵ����parent group��imports�е�����չ������group�С�
        // ÿ��group.init2()�������á�
        Set<GroupConfigImpl> processedGroups = createHashSet();
        GroupStack processingGroups = new GroupStack();

        for (GroupConfig groupConfig : getGroupConfigList()) {
            processGroup((GroupConfigImpl) groupConfig, processedGroups, processingGroups);
        }
    }

    /**
     * ����group֮��ļ̳й�ϵ����parent group��imports�е�����չ������group�С�
     */
    private void processGroup(GroupConfigImpl groupConfig, Set<GroupConfigImpl> processedGroups,
                              GroupStack processingGroups) throws Exception {
        if (!processedGroups.contains(groupConfig)) {
            if (groupConfig.getParentGroup() != null || !groupConfig.getImports().isEmpty()) {
                // ��ֹѭ���̳л�import
                if (processingGroups.contains(groupConfig)) {
                    StringBuilder buf = new StringBuilder();

                    for (GroupConfigImpl group : processingGroups) {
                        if (buf.length() == 0) {
                            buf.append("Cycle detected: ");
                        } else {
                            buf.append(" -> ");
                        }

                        buf.append(group.getName());
                    }

                    buf.append(" -> ").append(groupConfig.getName());

                    throw new IllegalArgumentException(buf.toString());
                }

                processingGroups.push(groupConfig);

                // ����parentGroup
                if (groupConfig.getParentGroup() != null) {
                    copyFields(groupConfig, groupConfig.getParentGroup(), null, true, processedGroups, processingGroups);
                }

                // ����imports
                for (Import impot : groupConfig.getImports()) {
                    copyFields(groupConfig, impot.getGroupName(), impot.getFieldName(), false, processedGroups,
                            processingGroups);
                }

                processingGroups.pop();
            }

            processedGroups.add(groupConfig); // ��ֹ�ظ�����
            groupConfig.init2(); // ��ʼ��group
        }
    }

    private void copyFields(GroupConfigImpl targetGroup, String srcGroupName, String srcFieldName, boolean isExtends,
                            Set<GroupConfigImpl> processedGroups, GroupStack processingGroups) throws Exception {
        GroupConfigImpl srcGroup = (GroupConfigImpl) assertNotNull(getGroupConfig(srcGroupName),
                "Parent or imported group name \"%s\" not found", srcGroupName);

        // �ݹ鴦��parentGroup��imported Groups��
        processGroup(srcGroup, processedGroups, processingGroups);

        // ��parentGroup��imported Groups�е��������ݸ��Ƶ���ǰgroup�С�
        if (isExtends) {
            targetGroup.extendsFrom(srcGroup);
        } else {
            targetGroup.importsFrom(srcGroup, srcFieldName);
        }
    }

    /**
     * ת���������Ķ����ַ�����
     */
    @Override
    public String toString() {
        return "FormConfig[groups: " + getGroupConfigList().size() + "]";
    }

    /**
     * ������ֹgroup�ݹ�̳С�
     */
    private static class GroupStack implements Iterable<GroupConfigImpl> {
        private final LinkedList<GroupConfigImpl> groups = createLinkedList();

        public void push(GroupConfigImpl group) {
            groups.addLast(group);
        }

        public GroupConfigImpl pop() {
            return groups.removeLast();
        }

        public boolean contains(GroupConfigImpl group) {
            return groups.contains(group);
        }

        public Iterator<GroupConfigImpl> iterator() {
            return groups.iterator();
        }

        @Override
        public String toString() {
            return groups.toString();
        }
    }
}
