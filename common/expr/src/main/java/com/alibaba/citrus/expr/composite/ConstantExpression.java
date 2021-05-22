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
package com.alibaba.citrus.expr.composite;

import com.alibaba.citrus.expr.ExpressionContext;
import com.alibaba.citrus.expr.support.ExpressionSupport;

/**
 * ����һ���������ʽ���ñ��ʽ��ֵ��������<code>ExpressionContext</code>��
 * 
 * @author Michael Zhou
 */
public class ConstantExpression extends ExpressionSupport {
    private Object value;

    /**
     * ����һ���������ʽ��
     */
    public ConstantExpression() {
    }

    /**
     * ����һ���������ʽ��
     * 
     * @param value ����ֵ
     */
    public ConstantExpression(Object value) {
        this.value = value;
    }

    /**
     * ȡ�ó���ֵ��
     * 
     * @return ����ֵ
     */
    public Object getValue() {
        return value;
    }

    /**
     * ���ó���ֵ��
     * 
     * @param value ����ֵ
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * ȡ�ñ��ʽ�ַ�����ʾ��
     * 
     * @return ���ʽ�ַ�����ʾ
     */
    public String getExpressionText() {
        return String.valueOf(value);
    }

    /**
     * ��ָ�����������м�����ʽ��
     * 
     * @param context <code>ExpressionContext</code>������
     * @return ���ʽ�ļ�����
     */
    public Object evaluate(ExpressionContext context) {
        return value;
    }
}
