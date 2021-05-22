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
package com.alibaba.citrus.service.requestcontext.basic.impl;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.basic.BasicRequestContext;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestContextFactory;

/**
 * ����<code>BasicRequestContext</code>�Ĺ�����
 * 
 * @author Michael Zhou
 */
public class BasicRequestContextFactoryImpl extends AbstractRequestContextFactory<BasicRequestContext> {
    private Object[] interceptors;

    public void setInterceptors(Object[] interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * ��װһ��request context��
     * 
     * @param wrappedContext ����װ��<code>RequestContext</code>����
     * @return request context
     */
    public BasicRequestContext getRequestContextWrapper(RequestContext wrappedContext) {
        return new BasicRequestContextImpl(wrappedContext, interceptors);
    }

    /**
     * �����ṩ������headers�Ĺ��ܡ�
     */
    public String[] getFeatures() {
        return new String[] { "headerInterceptors" };
    }

    /**
     * �����ṩ�˻����Եİ�ȫ���ƣ����Ӧ�ð���������ǰ�档
     */
    public FeatureOrder[] featureOrders() {
        return new FeatureOrder[] { new BeforeFeature("*") };
    }
}
