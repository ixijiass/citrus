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
package com.alibaba.citrus.springext;

import java.util.Collection;

import org.springframework.beans.factory.xml.NamespaceHandler;

public interface ConfigurationPoint {
    /**
     * ȡ�õ�ǰconfiguration point���ڵ�����configuration pointsע���
     */
    ConfigurationPoints getConfigurationPoints();

    /**
     * ȡ����XML�����ļ��У���������ǰconfiguration point�����ֿռ䡣
     */
    String getNamespaceUri();

    /**
     * ȡ��spring <code>NamespaceHandler</code>����
     */
    NamespaceHandler getNamespaceHandler();

    /**
     * ȡ��configuration point�����ơ�
     */
    String getName();

    /**
     * ȡ��Ĭ�ϵ�element���ơ�
     */
    String getDefaultElementName();

    /**
     * ȡ�ý����nsǰ׺����
     */
    String getPreferredNsPrefix();

    /**
     * ȡ��ָ�����ƺ����͵�contribution��
     */
    Contribution getContribution(String name, ContributionType type);

    /**
     * ȡ�����е�contributions��
     */
    Collection<Contribution> getContributions();

    /**
     * ȡ��schemas��
     */
    VersionableSchemas getSchemas();

    /**
     * ȡ��������
     */
    String getDescription();
}
