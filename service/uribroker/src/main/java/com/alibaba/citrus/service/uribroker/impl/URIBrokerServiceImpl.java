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
package com.alibaba.citrus.service.uribroker.impl;

import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static java.util.Collections.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.citrus.service.AbstractService;
import com.alibaba.citrus.service.uribroker.URIBrokerService;
import com.alibaba.citrus.service.uribroker.uri.URIBroker;

/**
 * URI Broker�����ʵ�֡�
 * 
 * @author Michael Zhou
 * @author dux.fangl
 */
public class URIBrokerServiceImpl extends AbstractService<URIBrokerService> implements URIBrokerService {
    private final HttpServletRequest request;
    private Boolean requestAware;
    private String defaultCharset;
    private URIBrokerInfo[] brokerInfos; // ��ʱbrokers��Ϣ�������ڳ�ʼ��
    private Map<String, URIBroker> brokers;
    private List<String> exposedNames;
    private List<String> names;

    /**
     * ��������ȡ��request proxy��
     */
    public URIBrokerServiceImpl(HttpServletRequest request) {
        this.request = assertProxy(request); // request����Ϊ��
    }

    /**
     * �Ƿ�ʹ��request�Ĳ�����
     */
    public boolean isRequestAware() {
        return requestAware == null ? true : requestAware;
    }

    /**
     * �����Ƿ�ʹ��request�Ĳ�����
     */
    public void setRequestAware(boolean requestAware) {
        this.requestAware = requestAware;
    }

    /**
     * ȡ��Ĭ�ϵ�charset��
     * <p>
     * ������ر�ָ��charset��uri broker��ȡ��ֵ��Ϊcharset��
     * </p>
     */
    public String getDefaultCharset() {
        return defaultCharset;
    }

    /**
     * ����Ĭ�ϵ�charset��
     */
    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    /**
     * ����һ��uri broker���ϡ�
     */
    public void setBrokers(URIBrokerInfo[] brokerInfos) {
        this.brokerInfos = brokerInfos;
    }

    /**
     * ȡ������URI broker���ơ�
     */
    public List<String> getNames() {
        return unmodifiableList(names);
    }

    /**
     * ȡ�����б�������URI broker���ơ�
     */
    public List<String> getExposedNames() {
        return unmodifiableList(exposedNames);
    }

    /**
     * ȡ��ָ�����Ƶ�URI broker��
     */
    public URIBroker getURIBroker(String name) {
        URIBroker broker = brokers.get(name);
        return broker == null ? null : broker.fork();
    }

    /**
     * ȡ��ָ�����Ƶ�URI broker������fork��
     */
    URIBroker getURIBrokerInternal(String name) {
        return brokers.get(name);
    }

    /**
     * ��ʼ��ʱ���е�brokers��
     */
    @Override
    protected void init() {
        assertNotNull(brokerInfos, "brokers");

        brokers = createLinkedHashMap();
        names = createLinkedList();
        exposedNames = createLinkedList();

        // ����name��broker��ӳ��
        Map<String, URIBrokerInfo> brokerInfoMap = createLinkedHashMap();

        for (URIBrokerInfo brokerInfo : brokerInfos) {
            URIBroker broker = assertNotNull(brokerInfo == null ? null : brokerInfo.broker, "broker");

            brokerInfo.name = assertNotNull(trimToNull(brokerInfo.name), "broker ID");
            brokerInfo.parentName = trimToNull(brokerInfo.parentName);

            assertTrue(!brokerInfoMap.containsKey(brokerInfo.name), "duplicated broker ID: %s", brokerInfo.name);

            brokerInfoMap.put(brokerInfo.name, brokerInfo);
            brokers.put(brokerInfo.name, broker);
            names.add(brokerInfo.name);

            if (brokerInfo.exposed) {
                exposedNames.add(brokerInfo.name);
            }

            // ����������requestAware�����򱣳�broker�е�Ĭ��ֵ
            if (requestAware != null) {
                broker.setRequestAware(requestAware);
            }

            // ����������defaultCharset�����򱣳�Ĭ�ϵ�broker charset��
            if (defaultCharset != null && broker.getCharset() == null) {
                broker.setCharset(defaultCharset);
            }

            broker.setRequest(request);
        }

        brokerInfos = null;

        // ����parent brokers��ȷ��parent broker��������Ҳ�Ǹ����ͬ�࣬ͬʱȷ��û�еݹ�����
        for (Map.Entry<String, URIBrokerInfo> entry : brokerInfoMap.entrySet()) {
            String name = entry.getKey();
            URIBrokerInfo brokerInfo = entry.getValue();
            String parentName = brokerInfo.parentName;

            // ���̳�����ȷ��û�еݹ�
            checkCyclic(brokerInfoMap, name, parentName);

            if (parentName != null) {
                URIBroker parentBroker = assertNotNull(brokers.get(parentName),
                        "parent \"%s\" not found for broker \"%s\"", parentName, brokerInfo.name);
                URIBroker thisBroker = brokerInfo.broker;

                thisBroker.setParent(parentBroker);
            }
        }

        // �ݹ鸴��parent�е���Ϣ
        for (URIBroker broker : brokers.values()) {
            broker.init();
        }
    }

    private String checkCyclic(Map<String, URIBrokerInfo> brokerInfoMap, String name, String parentName) {
        Set<String> inheritanceChain = createLinkedHashSet(name);

        for (; parentName != null; parentName = brokerInfoMap.containsKey(parentName) ? brokerInfoMap.get(parentName).parentName
                : null) {
            if (inheritanceChain.contains(parentName)) {
                StringBuilder buf = new StringBuilder();

                buf.append("Cyclic detected: ");

                for (String item : inheritanceChain) {
                    buf.append(item).append("->");
                }

                buf.append(parentName);

                throw new IllegalArgumentException(buf.toString());
            }

            inheritanceChain.add(parentName);
        }
        return parentName;
    }

    /**
     * �г����е�URI brokers��
     */
    public String dump() {
        StringWriter buf = new StringWriter();
        dump(buf);
        return buf.toString();
    }

    /**
     * �г����е�URI brokers��
     */
    public void dump(Writer writer) {
        PrintWriter out = null;

        if (writer instanceof PrintWriter) {
            out = (PrintWriter) writer;
        } else {
            out = new PrintWriter(writer);
        }

        // ȡ�����key�ĳ���
        int classWidth = 0;
        int keyWidth = 0;

        for (Map.Entry<String, URIBroker> entry : brokers.entrySet()) {
            String name = entry.getKey();
            URIBroker broker = entry.getValue();
            String className = broker.getClass().getSimpleName();

            if (className.length() > classWidth) {
                classWidth = className.length();
            }

            if (name.length() > keyWidth) {
                keyWidth = name.length();
            }
        }

        for (Map.Entry<String, URIBroker> entry : brokers.entrySet()) {
            String name = entry.getKey();
            URIBroker broker = entry.getValue();

            broker = broker.fork();

            StringBuilder format = new StringBuilder();

            if (exposedNames.contains(name)) {
                format.append("* ");
            } else {
                format.append("  ");
            }

            format.append("%-").append(classWidth + 2).append("s %-").append(keyWidth).append("s= %s%n");

            out.printf(format.toString(), "(" + broker.getClass().getSimpleName() + ")", name, broker);
        }

        out.flush();
    }

    /**
     * ���uri broker��������Ϣ��
     */
    public static class URIBrokerInfo {
        public String name;
        public String parentName;
        public boolean exposed;
        public URIBroker broker;

        public URIBrokerInfo(String name, String parentName, boolean exposed, URIBroker broker) {
            this.name = trimToNull(name);
            this.parentName = trimToNull(parentName);
            this.exposed = exposed;
            this.broker = broker;
        }
    }
}
