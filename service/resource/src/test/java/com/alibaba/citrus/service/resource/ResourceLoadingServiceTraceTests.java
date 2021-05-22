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
package com.alibaba.citrus.service.resource;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ����<code>ResourceLoadingService.trace()</code>���ܡ�
 * 
 * @author Michael Zhou
 */
public class ResourceLoadingServiceTraceTests extends AbstractResourceLoadingTests {
    @BeforeClass
    public static void initClass() throws Exception {
        initFactory("resources-root.xml");
        initSubFactory("WEB-INF/resources.xml");
    }

    @Before
    public void init() throws Exception {
        resourceLoadingService = (ResourceLoadingService) factory.getBean("resourceLoadingService");

        ResourceLoadingService parentService = (ResourceLoadingService) parentFactory.getBean("resourceLoadingService");

        assertSame(parentService, resourceLoadingService.getParent());
    }

    @Test
    public void resourceAlias_bySuperLoader() throws Exception {
        resourceLoadingService = (ResourceLoadingService) factory.getBean("resourceLoadingService_2");

        // /myfolder/testres.txt ӳ�䵽<super-loader name="/webroot">
        // ��<resource-alias name="/webroot">��Ч
        assertTrace(
                "/myfolder/testres.txt", //
                "\"/myfolder/testres.txt\" matched [resource pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService_2\"", //
                "\"/webroot/myfolder/testres.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService_2\"");
    }

    @Test
    public void getResource_notFound() throws Exception {
        assertTrace(
                "/not/found.txt", //
                "\"/not/found.txt\" matched [resource-alias pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/not/found.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"");
    }

    @Test
    public void getResource_parent_defaultMapping() throws Exception {
        // ��ǰresource loader��û�ҵ�����parent���ң�ƥ��/
        assertTrace(
                "/myfolder/testres.txt", //
                "\"/myfolder/testres.txt\" matched [resource-alias pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/myfolder/testres.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"");
    }

    @Test
    public void getResource_alias_notFound() throws Exception {
        // Alias��ƥ�䣬��û�ҵ�resource mapping
        assertTrace(
                "/my/alias1/testres.txt", //
                "\"/my/alias1/testres.txt\" matched [resource-alias pattern=\"/my/alias1\"], at \"resources.xml\", beanName=\"resourceLoadingService\"", //
                "\"/my/alias2/testres.txt\" matched [resource-alias pattern=\"/my/alias2\"], at \"resources.xml\", beanName=\"resourceLoadingService\"", //
                "\"/not/found/testres.txt\" matched [resource-alias pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/not/found/testres.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"");
    }

    @Test
    public void getResource_alias_foundInParent() throws Exception {
        // Alias��ƥ�䣬��default resource loader���ҵ���Դ
        assertTrace(
                "/my/alias3/testres.txt", //
                "\"/my/alias3/testres.txt\" matched [resource-alias pattern=\"/my/alias3\"], at \"resources.xml\", beanName=\"resourceLoadingService\"", //
                "\"/myfolder/testres.txt\" matched [resource-alias pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/myfolder/testres.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"");
    }

    @Test
    public void getResource_internal_found() throws Exception {
        // Alias��ƥ�䣬internal mapping���ҵ�
        assertTrace(
                "/my/alias4/testres.txt", //
                "\"/my/alias4/testres.txt\" matched [resource-alias pattern=\"/my/alias4\"], at \"resources.xml\", beanName=\"resourceLoadingService\"", //
                "\"/my/internal/resource/testres.txt\" matched [resource pattern=\"/my/internal/resource\"], at \"resources.xml\", beanName=\"resourceLoadingService\"");

        // super-loader��ƥ�䣬internal mapping���ҵ�
        assertTrace(
                "/my/alias5/testres.txt", //
                "\"/my/alias5/testres.txt\" matched [resource pattern=\"/my/alias5\"], at \"resources.xml\", beanName=\"resourceLoadingService\"", //
                "\"/my/internal/resource/testres.txt\" matched [resource pattern=\"/my/internal/resource\"], at \"resources.xml\", beanName=\"resourceLoadingService\"");

        // ��parent
        resourceLoadingService = resourceLoadingService.getParent();

        assertTrace(
                "/myfolder/testres.txt", //
                "\"/myfolder/testres.txt\" matched [resource-alias pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/myfolder/testres.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"");
    }

    @Test
    public void getResource_internal_notFound() throws Exception {
        // ֱ����internal mapping�ǲ��е�
        assertTrace(
                "/my/internal/resource/testres.txt", //
                "\"/my/internal/resource/testres.txt\" matched [resource-alias pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/my/internal/resource/testres.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"");

        // aliasӳ�䵽parent internal mapping�������ǲ��е�
        assertTrace(
                "/my/alias6/testres.txt", //
                "\"/my/alias6/testres.txt\" matched [resource-alias pattern=\"/my/alias6\"], at \"resources.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/myfolder/testres.txt\" matched [resource-alias pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/webroot/myfolder/testres.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"");

        // super-loaderӳ�䵽parent internal mapping�������ǲ��е�
        assertTrace(
                "/my/alias7/testres.txt", //
                "\"/my/alias7/testres.txt\" matched [resource pattern=\"/my/alias7\"], at \"resources.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/myfolder/testres.txt\" matched [resource-alias pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/webroot/myfolder/testres.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"");

        // ��parent
        resourceLoadingService = resourceLoadingService.getParent();

        assertTrace(
                "/webroot/myfolder/testres.txt", //
                "\"/webroot/myfolder/testres.txt\" matched [resource-alias pattern=\"/\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"", //
                "\"/webroot/webroot/myfolder/testres.txt\" matched [resource pattern=\"/webroot\"], at \"resources-root.xml\", beanName=\"resourceLoadingService\"");
    }

    @Test
    public void getResource_noLoaders() throws Exception {
        // ƥ�䣬��û��loaders
        assertTrace(
                "/my/resource/testres.txt", //
                "\"/my/resource/testres.txt\" matched [resource pattern=\"/my/resource\"], at \"resources.xml\", beanName=\"resourceLoadingService\"");
    }

    private void assertTrace(String resourceName, String... elements) {
        ResourceTrace trace = resourceLoadingService.trace(resourceName);

        assertEquals(elements.length, trace.length());

        int i = 0;
        for (ResourceTraceElement element : trace) {
            assertEquals(elements[i++], element.toString());
        }
    }
}
