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
package com.alibaba.citrus.service.resource.loader;

import static com.alibaba.citrus.service.resource.ResourceLoadingService.*;
import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.citrus.service.resource.AbstractResourceLoadingTests;
import com.alibaba.citrus.service.resource.ResourceLoadingService;
import com.alibaba.citrus.service.resource.ResourceNotFoundException;
import com.alibaba.citrus.util.io.StreamUtil;

public class WebappLoaderIntegratingTests extends AbstractResourceLoadingTests {
    @BeforeClass
    public static void initClass() throws Exception {
        initFactory("loader/webapp-loader.xml");
    }

    @Before
    public void init() throws Exception {
        resourceLoadingService = (ResourceLoadingService) factory.getBean("resourceLoadingService");
    }

    @Test
    public void getResource() throws Exception {
        assertResourceServiceList("/webroot", "", true, true, "WEB-INF/", "appcontext/", "beans.xml", "filter/",
                "loader/", "logback.xml", "myfolder/", "resources-root.xml", "test.txt");
        assertResourceServiceList("/webroot/test.txt", "test.txt", true, false);
        assertResourceServiceList("/webroot/WEB-INF/", "WEB-INF", true, true, "aaa/", "resources.xml", "web.xml");
        assertResourceServiceList("/webroot/WEB-INF/web.xml", "WEB-INF/web.xml", true, false);

        assertResourceServiceList("/webroot/notexist.txt", "notexist.txt", false, false);

        // webapp-loader��֧��for_createѡ��
        try {
            resourceLoadingService.getResource("/webroot/not/found", FOR_CREATE);
            fail();
        } catch (ResourceNotFoundException e) {
        }
    }

    @Test
    public void factoryBean_init_recursively() {
        // ��������µ����⣺
        // ---------------
        // ԭ�ȣ�webapp-loaderͨ��inject by Type����servletContextע��constructor��
        // ��ᴥ������FactoryBean�Ĵ������Ա�ȡ��factoryBean.getObjectType()��
        // ����getObjectType()�����ڳ�ʼ�����ܷ��ؽ��������webx2 serviceFactoryBean������ôspring�������ʼ��factoryBean��
        // ����factoryBean�ĳ�ʼ���ִ�����resource loading���ƣ����ڴ�ʱresource loading��û��ʼ���꣬��˲��ܹ����������Ҳ�����Դ�ļ���
        // ��ֻ���ҵ�SpringĬ�ϵ���Դ����ServletResource����
        // ---------------
        // ���ڣ�����ͨ��ServletContextAware�ӿ�ע��servletContext�������㲻�ᴥ��factoryBean�ĳ�ʼ����
        // �Ӷ�ȷ��factoryBean�ڳ�ʼ��ǰ��Resource loading�����Ѿ���ʼ�������ã�����ȡ����Դ��
        assertEquals("test", factory.getBean("myTest"));
    }

    public static class FactoryBeanUsingResource implements FactoryBean, InitializingBean {
        private Class<?> type;
        private URL location;
        private String text;

        public void setLocation(URL location) {
            this.location = location;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }

        public void afterPropertiesSet() throws Exception {
            this.text = StreamUtil.readText(location.openStream(), "UTF-8", true);
        }

        public Object getObject() throws Exception {
            return text;
        }

        public Class<?> getObjectType() {
            return type;
        }

        public boolean isSingleton() {
            return true;
        }
    }
}
