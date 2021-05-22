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
package com.alibaba.citrus.expr.jexl;

import com.alibaba.citrus.expr.Expression;
import com.alibaba.citrus.expr.ExpressionContext;
import com.alibaba.citrus.expr.ExpressionFactory;
import com.alibaba.citrus.expr.ExpressionParseException;
import com.alibaba.citrus.expr.support.ExpressionSupport;

/**
 * ����<code>JexlExpression</code>�Ĺ�����
 * 
 * @author Michael Zhou
 */
public class JexlExpressionFactory implements ExpressionFactory {
    /** �Ƿ�֧��context������������С����ָ��ı������� */
    private boolean supportContextVariables = true;

    /**
     * �Ƿ�֧��context������������С����ָ��ı�������
     * 
     * @return ���֧�֣��򷵻�<code>true</code>
     */
    public boolean isSupportContextVariables() {
        return supportContextVariables;
    }

    /**
     * ����֧��context������
     * 
     * @param supportContextVariables �Ƿ�֧��context����
     */
    public void setSupportContextVariables(boolean supportContextVariables) {
        this.supportContextVariables = supportContextVariables;
    }

    /**
     * �������ʽ��
     * 
     * @param expr ���ʽ�ַ���
     * @return ���ʽ
     */
    public Expression createExpression(final String expr) throws ExpressionParseException {
        final Expression jexlExpression;

        try {
            jexlExpression = new JexlExpression(org.apache.commons.jexl.ExpressionFactory.createExpression(expr));
        } catch (Exception e) {
            throw new ExpressionParseException(e);
        }

        if (isSupportContextVariables() && isValidContextVariableName(expr)) {
            return new ExpressionSupport() {
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
                    // ����ִ��jexl���ʽ
                    Object value = jexlExpression.evaluate(context);

                    // ���jexl���ʽ���Ϊnull�����context��ֱ��ȡֵ
                    if (value == null) {
                        value = context.get(expr);
                    }

                    return value;
                }
            };
        }

        return jexlExpression;
    }

    /**
     * �ж��Ƿ�Ϊcontext������
     * 
     * @return ����ǣ��򷵻�<code>true</code>
     */
    protected boolean isValidContextVariableName(String varName) {
        for (int i = 0; i < varName.length(); i++) {
            char ch = varName.charAt(i);

            if (Character.isWhitespace(ch) || ch == '[') {
                return false;
            }
        }

        return true;
    }
}
