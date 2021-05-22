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
package com.alibaba.citrus.expr.support;

import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.Map;

import com.alibaba.citrus.expr.ExpressionContext;

/**
 * ʹ��<code>Map</code>ʵ�ֵ�<code>ExpressionContext</code>��
 * 
 * @author Michael Zhou
 */
public class MappedExpressionContext implements ExpressionContext {
    private final Map<String, Object> context = createHashMap();

    /**
     * ȡ��ָ��ֵ��
     * 
     * @param key ��
     * @return ����Ӧ��ֵ
     */
    public Object get(String key) {
        return context.get(key);
    }

    /**
     * ���һ��ֵ��
     * 
     * @param key ��
     * @param value ��Ӧ��ֵ
     */
    public void put(String key, Object value) {
        if (value == null) {
            context.remove(key);
        } else {
            context.put(key, value);
        }
    }
}
