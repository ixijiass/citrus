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

import java.util.List;

import com.alibaba.citrus.expr.Expression;
import com.alibaba.citrus.expr.ExpressionContext;
import com.alibaba.citrus.expr.support.ExpressionSupport;

/**
 * ����һ����ϵı��ʽ��
 * 
 * @author Michael Zhou
 */
public class CompositeExpression extends ExpressionSupport {
    private String expr;
    private Expression[] expressions;

    /**
     * ����һ����ϵı��ʽ��
     * 
     * @param expr ���ʽ�ַ���
     * @param expressions ���ʽ�б�
     */
    public CompositeExpression(String expr, List<Expression> expressions) {
        this.expr = expr;
        this.expressions = expressions.toArray(new Expression[expressions.size()]);
    }

    /**
     * ȡ�ñ��ʽ�ַ�����ʾ��
     * 
     * @return ���ʽ�ַ�����ʾ
     */
    public String getExpressionText() {
        return expr;
    }

    /**
     * ��ָ�����������м�����ʽ��
     * 
     * @param context <code>ExpressionContext</code>������
     * @return ���ʽ�ļ�����
     */
    public Object evaluate(ExpressionContext context) {
        StringBuffer buffer = new StringBuffer();

        for (Expression expression : expressions) {
            Object value = expression.evaluate(context);

            if (value != null) {
                buffer.append(value);
            }
        }

        return buffer.toString();
    }
}
