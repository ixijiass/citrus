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
package com.alibaba.citrus.service.requestcontext.lazycommit.impl;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.lazycommit.LazyCommitRequestContext;
import com.alibaba.citrus.service.requestcontext.support.AbstractRequestContextFactory;

/**
 * ����<code>LazyCommitRequestContext</code>�Ĺ�����
 * 
 * @author Michael Zhou
 */
public class LazyCommitRequestContextFactoryImpl extends AbstractRequestContextFactory<LazyCommitRequestContext> {
    /**
     * ��װһ��request context��
     * 
     * @param wrappedContext ����װ��<code>RequestContext</code>����
     * @return request context
     */
    public LazyCommitRequestContext getRequestContextWrapper(RequestContext wrappedContext) {
        return new LazyCommitRequestContextImpl(wrappedContext);
    }

    /**
     * �����ṩ���ӳ��ύheaders�Ĺ��ܡ�
     */
    public String[] getFeatures() {
        return new String[] { "lazyCommitHeaders" };
    }

    /**
     * ����ʵ�����ӳ��ύheaders�Ĺ��ܡ����ǣ����粻��contentҲ�����ӳ��ύ�Ļ���
     * Ӧ�ó����������content�ᵼ��response��ǰ���ύ���Ӷ�����headers�޷��ύ��
     * ���ң�headers��������content�ύ����ˣ�lazyCommitHeaders ��������
     * lazyCommitContent֮����������lazyCommitContent���ܡ�
     */
    public FeatureOrder[] featureOrders() {
        return new FeatureOrder[] { new RequiresFeature("lazyCommitContent") };
    }
}
