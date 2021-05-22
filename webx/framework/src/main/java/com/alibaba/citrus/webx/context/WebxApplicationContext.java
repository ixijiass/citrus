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
package com.alibaba.citrus.webx.context;

import static com.alibaba.citrus.webx.WebxConstant.*;

import com.alibaba.citrus.service.resource.support.context.ResourceLoadingXmlWebApplicationContext;

/**
 * ����webx��ܵ�application context��
 * <ul>
 * <li>��չ��Spring��
 * {@link org.springframework.web.context.support.XmlWebApplicationContext}
 * �������SpringExt��֧�֣�����configuration point�Լ�resource loading��չ��</li>
 * <li>�޸���Ĭ�ϵ������ļ�����<code>/WEB-INF/webx-*.xml</code>��</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public class WebxApplicationContext extends ResourceLoadingXmlWebApplicationContext {
    /**
     * ȡ��Ĭ�ϵ�Spring�����ļ�����
     * <ul>
     * <li>Root contextĬ�������ļ�Ϊ<code>/WEB-INF/webx.xml</code>��</li>
     * <li>����component����Ϊ<code>"test"</code> ����Ĭ�ϵ������ļ���
     * <code>/WEB-INF/webx-test.xml</code>��</li>
     * </ul>
     */
    @Override
    protected String[] getDefaultConfigLocations() {
        if (getNamespace() != null) {
            return new String[] { WEBX_COMPONENT_CONFIGURATION_LOCATION_PATTERN.replace("*", getNamespace()) };
        } else {
            return new String[] { WEBX_CONFIGURATION_LOCATION };
        }
    }
}
