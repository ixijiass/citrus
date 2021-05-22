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
package com.alibaba.citrus.service.requestcontext.buffered.impl;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.buffered.BufferedRequestContext;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestContextFactory;

/**
 * ����<code>BufferedRequestContext</code>�Ĺ�����
 * 
 * @author Michael Zhou
 */
public class BufferedRequestContextFactoryImpl extends AbstractRequestContextFactory<BufferedRequestContext> {
    /**
     * ��װһ��request context��
     * 
     * @param wrappedContext ����װ��<code>RequestContext</code>����
     * @return request context
     */
    public BufferedRequestContext getRequestContextWrapper(RequestContext wrappedContext) {
        return new BufferedRequestContextImpl(wrappedContext);
    }

    /**
     * �����ṩ���ӳ��ύcontent�Ĺ��ܡ�
     */
    public String[] getFeatures() {
        return new String[] { "lazyCommitContent" };
    }

    /**
     * ���಻��������features��
     */
    public FeatureOrder[] featureOrders() {
        return null;
    }
}
