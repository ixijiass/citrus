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

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.alibaba.citrus.service.pull.PullService;
import com.alibaba.citrus.service.uribroker.AbstractURIBrokerServiceTests;
import com.alibaba.citrus.service.uribroker.uri.URIBroker;

public class URIBrokerToolTests extends AbstractURIBrokerServiceTests {
    private PullService pull;

    @Before
    public final void init() {
        ApplicationContext factory = createContext("services.xml", createContext("services-root.xml", null));
        pull = (PullService) factory.getBean("pullService");
        assertNotNull(pull);
    }

    @Test
    public void pull() throws Exception {
        Map<String, Object> tools = assertRequest();

        Object u1 = tools.get("link2");
        Object u2 = tools.get("linkCharset");
        Object u3 = tools.get("uris");

        // -----------------------------------
        // �µ�request
        prepareRequest();

        tools = assertRequest();

        Object u12 = tools.get("link2");
        Object u22 = tools.get("linkCharset");
        Object u32 = tools.get("uris");

        // ȷ����������ȡ�ò�ͬ�Ķ���
        assertNotSame(u1, u12);
        assertNotSame(u2, u22);
        assertNotSame(u3, u32);
    }

    private Map<String, Object> assertRequest() {
        Map<String, Object> tools = pull.getTools();
        assertEquals(3, tools.size());

        // ��pull service��ֱ��ȡ��
        URIBroker u1 = (URIBroker) tools.get("link2");
        URIBroker u2 = (URIBroker) tools.get("linkCharset");

        // ͨ��uris toolȡ��
        URIBrokerTool.Helper tool = (URIBrokerTool.Helper) tools.get("uris");
        URIBroker u1_2 = tool.get("link2");
        URIBroker u2_2 = tool.get("linkCharset");

        // �鿴��Ⱦ����������ԣ�request�Ѿ���ע��
        assertEquals("http://myuser2:mypass2@myservername2:1234/aaa/a1/bbb/ccc/ddd"
                + "?aaa=1111&bbb=2222&ccc=3333#myreference2", u1.toString());

        assertEquals("http://localhost/", u2.toString());

        assertEquals(u1_2.toString(), u1.toString());
        assertEquals(u2_2.toString(), u2.toString());

        // ͨ��uri.get()����ȡ��broker����cache��
        assertSame(u1_2, tool.get("link2"));
        assertSame(u2_2, tool.get("linkCharset"));

        // ��ȡ�÷�exposed brokers
        assertEquals("http://localhost/", tool.get("link").toString());

        return tools;
    }
}
