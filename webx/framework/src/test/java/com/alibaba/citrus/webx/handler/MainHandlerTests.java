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
package com.alibaba.citrus.webx.handler;

import static com.alibaba.citrus.test.TestUtil.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.citrus.webx.AbstractWebxTests;
import com.alibaba.citrus.webx.util.WebxUtil;

public class MainHandlerTests extends AbstractWebxTests {
    @Before
    public void init() throws Exception {
        prepareWebClient(null);
    }

    @After
    public void destroy() throws Exception {
        System.clearProperty("productionModeFromSystemProperties");
    }

    @Test
    public void internalRequest_productionMode() throws Exception {
        System.setProperty("productionModeFromSystemProperties", "true");
        prepareWebClient(null);

        // homepage - ���ᵼ��main internal page
        assertHomepage("");
        assertHomepage("/");
        assertHomepage("?home");
        assertHomepage("/?a=1&home=&b=2");

        // �����ڵ�internalҳ��
        assertNotAvailable("/internal/notexist");

        // main - not available in production mode
        assertNotAvailable("/internal");
        assertNotAvailable("/internal/");

        // schema - not available in production mode
        assertNotAvailable("/internal/schema");
        assertNotAvailable("/internal/schema/");
    }

    private void assertNotAvailable(String url) throws Exception {
        invokeServlet(url);

        assertEquals(404, clientResponseCode);

        // http unit sendError��ʵ�֣���ʵ������������web.xml�е�error-page
        assertThat(clientResponseContent, containsAll("<html><head><title></title></head><body></body></html>"));
    }

    /**
     * �ڿ���ģʽ�£�����/��/internal��������ʾmain internal page��
     */
    @Test
    public void internalRequest_main() throws Exception {
        assertMainInternalPage("");
        assertMainInternalPage("/");
        assertMainInternalPage("/internal");
        assertMainInternalPage("/internal/");
    }

    /**
     * �ڿ���ģʽ�£�����/?home������ʾԭ����homepage��
     */
    @Test
    public void internalRequest_homepage() throws Exception {
        // ����Ϊhome�����ļ�����̬������������ʽƥ���
        assertHomepage("?home");
        assertHomepage("/?home=");
        assertHomepage("/?a=1&home=2&b=3");
        assertHomepage("/?a=1&home&b=3");

        // ֻ��/?home�Ż���ʾhomepage
        assertMainInternalPage("/internal?home");
        assertMainInternalPage("/internal/?home");
    }

    private void assertHomepage(String url) throws Exception {
        invokeServlet(url);

        assertEquals(200, clientResponseCode);
        assertEquals("Homepage", clientResponseContent);
    }

    private void assertMainInternalPage(String url) throws Exception {
        invokeServlet(url);

        assertEquals(200, clientResponseCode);

        // ��ֵ��ͬ��ResourceServlet�����ص�homepageҳ
        assertThat(clientResponseContent, not(equalTo("Homepage")));

        // ����webx�汾��
        assertThat(clientResponseContent, containsString(WebxUtil.getWebxVersion()));

        // ����home
        assertThat(clientResponseContent, containsString("images/home1.gif\" alt=\"Home\" /> Home</a>"));

        // ����application home
        assertThat(clientResponseContent,
                containsString("images/home2.gif\" alt=\"Application Home\" /> Application Home</a>"));
    }
}
