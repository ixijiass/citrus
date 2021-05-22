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
package com.alibaba.citrus.service.resource.filter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.citrus.service.resource.AbstractResourceLoadingTests;
import com.alibaba.citrus.service.resource.Resource;
import com.alibaba.citrus.service.resource.ResourceLoadingService;
import com.alibaba.citrus.util.io.StreamUtil;

public class XsltFilterTests extends AbstractResourceLoadingTests {
    @BeforeClass
    public static void initClass() throws Exception {
        initFactory("resources-root.xml");
        initSubFactory("filter/xslt-filter.xml");
    }

    @Before
    public void init() throws Exception {
        resourceLoadingService = (ResourceLoadingService) factory.getBean("resourceLoadingService");

        ResourceLoadingService parentService = (ResourceLoadingService) parentFactory.getBean("resourceLoadingService");

        assertSame(parentService, resourceLoadingService.getParent());
    }

    @Test
    public void inMemory() throws Exception {
        Resource resource = resourceLoadingService.getResource("/myfolder/test.xml");

        // ��Ϊû����saveTo����˲�����ȡ��URL��File��
        assertNull(resource.getURL());
        assertNull(resource.getFile());

        // ���ת���Ľ��, ��������Ӧ����
        String output = normalizeString(StreamUtil.readText(resource.getInputStream(), "GB2312", true));

        output = normalizeString(StreamUtil.readText(resource.getInputStream(), "GB2312", true));

        System.out.println(output);

        String expected = normalizeString(StreamUtil.readText(
                resourceLoadingService.getResourceAsStream("/myfolder/test.result"), "GB2312", true));

        assertEquals(expected, output);
    }

    @Test
    public void saveToFile() throws Exception {
        Resource resource = resourceLoadingService.getResource("/myfolder/test2.xml");

        // ��Ϊ������saveToDir����˿���ȡ��URL��File��
        assertNotNull(resource.getURL());
        assertNotNull(resource.getFile());

        // ���ת���Ľ��
        String output = normalizeString(StreamUtil.readText(resource.getInputStream(), "GB2312", true));

        output = normalizeString(StreamUtil.readText(resource.getInputStream(), "GB2312", true));

        System.out.println(output);

        String expected = normalizeString(StreamUtil.readText(
                resourceLoadingService.getResourceAsStream("/myfolder/test.result"), "GB2312", true));

        assertEquals(expected, output);
    }

    /**
     * ����ַ���, ��"\r\n"��"\r"����"\n", �����ַ����Ƚ�.
     * 
     * @param str �ַ���.
     * @return ��񻯺���ַ���.
     */
    public final String normalizeString(String str) {
        return str.replaceAll("\\r\\n?", "\n").replaceAll(">\\s*<", ">\n<");
    }
}
