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
package com.alibaba.citrus.service.uribroker.support;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.pull.ToolFactory;
import com.alibaba.citrus.service.pull.ToolSetFactory;
import com.alibaba.citrus.service.uribroker.URIBrokerService;
import com.alibaba.citrus.service.uribroker.uri.URIBroker;
import com.alibaba.citrus.springext.support.parser.AbstractSingleBeanDefinitionParser;

/**
 * ȡ������<code>URIBroker</code>��pull tool��
 * 
 * @author Michael Zhou
 */
public class URIBrokerTool implements ToolFactory, ToolSetFactory, InitializingBean {
    private URIBrokerService brokers;

    @Autowired
    public void setBrokers(URIBrokerService brokers) {
        this.brokers = brokers;
    }

    /**
     * ��ʼ��pull tool��
     */
    public void afterPropertiesSet() throws Exception {
        assertNotNull(brokers, "no URIBrokerService");
    }

    /**
     * ÿ�����󶼻ᴴ���µ�ʵ����
     */
    public boolean isSingleton() {
        return false;
    }

    /**
     * ȡ������exposed URI broker�����ơ�
     */
    public Iterable<String> getToolNames() {
        return brokers.getExposedNames();
    }

    /**
     * ȡ��һ�����󣬿��Դ���ȡ�����е�brokers��
     */
    public Object createTool() throws Exception {
        return new Helper();
    }

    /**
     * ȡ��ָ�����Ƶ�broker��
     */
    public Object createTool(String name) throws Exception {
        return brokers.getURIBroker(name);
    }

    /**
     * ����һ�������࣬ÿ�����󶼻ᴴ��һ�Ρ�
     */
    public class Helper {
        private Map<String, URIBroker> cache = createHashMap();

        /**
         * ����ģ��ʹ�õķ�����ȡ��ָ�����Ƶ�broker��
         */
        public URIBroker get(String name) {
            URIBroker broker = cache.get(name);

            if (broker == null) {
                broker = brokers.getURIBroker(name);

                if (broker == null) {
                    return null;
                }

                cache.put(name, broker);
            }

            return broker;
        }
    }

    public static class DefinitionParser extends AbstractSingleBeanDefinitionParser<URIBrokerTool> {
    }
}
