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
package com.alibaba.citrus.service.requestcontext;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.StringUtil.*;

/**
 * ����һ��<code>RequestContext</code>��ص���Ϣ��
 * 
 * @author Michael Zhou
 */
public interface RequestContextInfo<R extends RequestContext> {
    /**
     * ȡ�õ�ǰfactory�����ɵ�request context�ӿڡ�
     */
    Class<R> getRequestContextInterface();

    /**
     * ȡ����������proxy��Ľӿڡ�
     */
    Class<? extends R> getRequestContextProxyInterface();

    /**
     * ȡ�õ�ǰfactory���ɵ�request context���ṩ��features��
     * ��νfeatures����һЩ�ַ�����RequestContextChaining����������Щfeature��������ϵ����request
     * context����
     */
    String[] getFeatures();

    /**
     * ָ����ǰrequest context����������Щfeatures֮ǰ��֮��
     */
    FeatureOrder[] featureOrders();

    /**
     * ����request context feature��˳��
     */
    abstract class FeatureOrder {
        public final String feature;

        public FeatureOrder(String feature) {
            this.feature = assertNotNull(trimToNull(feature), "feature");
        }
    }

    /**
     * ��ʾ��ǰrequest contextӦ�������ṩָ��feature��request context֮ǰ��
     */
    class BeforeFeature extends FeatureOrder {
        public BeforeFeature(String feature) {
            super(feature);
        }

        @Override
        public String toString() {
            return "Before " + feature;
        }
    }

    /**
     * ��ʾ��ǰrequest contextӦ�������ṩָ��feature��request context֮��
     */
    class AfterFeature extends FeatureOrder {
        public AfterFeature(String feature) {
            super(feature);
        }

        @Override
        public String toString() {
            return "After " + feature;
        }
    }

    /**
     * ��ʾ��ǰrequest contextǰ��������ṩָ��feature��request context���ڡ�
     */
    class RequiresFeature extends AfterFeature {
        public RequiresFeature(String feature) {
            super(feature);
        }

        @Override
        public String toString() {
            return "Requires " + feature;
        }
    }
}
