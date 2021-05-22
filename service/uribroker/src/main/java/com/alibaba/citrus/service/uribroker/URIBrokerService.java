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
package com.alibaba.citrus.service.uribroker;

import java.io.Writer;
import java.util.List;

import com.alibaba.citrus.service.uribroker.uri.URIBroker;

/**
 * URI Broker��service �ӿڶ��塣
 * 
 * @author Michael Zhou
 * @author dux.fangl
 */
public interface URIBrokerService {
    /**
     * ȡ������URI broker����.
     */
    List<String> getNames();

    /**
     * ȡ�����б�������URI broker����.
     */
    List<String> getExposedNames();

    /**
     * ȡ��ָ�����Ƶ�URI broker.
     */
    URIBroker getURIBroker(String name);

    /**
     * �г����е�URI brokers.
     */
    String dump();

    /**
     * �г����е�URI brokers.
     */
    void dump(Writer writer);
}
