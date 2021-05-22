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
package com.alibaba.citrus.generictype.introspect.impl;

import static java.util.Collections.*;

import java.util.List;
import java.util.Map;

import com.alibaba.citrus.generictype.TypeInfo;
import com.alibaba.citrus.generictype.introspect.PropertyInfo;

/**
 * �����{@link PropertiesFinder}ʵ�֣����������顢<code>Collection</code>��<code>Map</code>
 * �����Ϳ���һ��������property��
 * 
 * @author Michael Zhou
 */
public abstract class SinglePropertyFinder extends AbstractTypeVisitor implements PropertiesFinder {
    public final Map<String, List<PropertyInfo>> getProperties() {
        PropertyInfo prop = createPropertyInfo(getType());

        if (prop != null) {
            return singletonMap(prop.getName(), singletonList(prop));
        } else {
            return emptyMap();
        }
    }

    /**
     * �������ʹ���һ��{@link PropertyInfo}ʵ����������Ͳ�֧�֣��򷵻�<code>null</code>��
     */
    protected abstract PropertyInfo createPropertyInfo(TypeInfo type);
}
