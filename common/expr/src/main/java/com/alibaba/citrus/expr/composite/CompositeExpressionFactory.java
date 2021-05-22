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

import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.List;

import com.alibaba.citrus.expr.Expression;
import com.alibaba.citrus.expr.ExpressionFactory;
import com.alibaba.citrus.expr.ExpressionParseException;
import com.alibaba.citrus.expr.jexl.JexlExpressionFactory;

/**
 * ����<code>CompositeExpression</code>�Ĺ�����
 * 
 * @author Michael Zhou
 */
public class CompositeExpressionFactory implements ExpressionFactory {
    private ExpressionFactory factory;

    /**
     * ����һ����ϱ��ʽ�Ĺ�����Ĭ��ʹ��<code>JexlExpressionFactory</code>�������ӱ��ʽ��
     */
    public CompositeExpressionFactory() {
        this.factory = new JexlExpressionFactory();
    }

    /**
     * ����һ����ϱ��ʽ�Ĺ�����
     * 
     * @param factory ������ϱ��ʽ�е��ӱ��ʽ�Ĺ���
     */
    public CompositeExpressionFactory(ExpressionFactory factory) {
        this.factory = factory;
    }

    /**
     * �������ʽ��
     * <ul>
     * <li>������ʽ�в�������<code>${...}</code>�����򴴽�<code>ConstantExpression</code>��</li>
     * <li>������ʽ�ԡ�<code>${</code>����ʼ�����ԡ�<code>}</code>����β�������ָ����
     * <code>ExpressionFactory</code>����������ϱ��ʽ��</li>
     * <li>������ʽ������<code>${...}</code>�������ڴ�֮�⻹�б���ַ����򴴽�
     * <code>CompositeExpression</code>��</li>
     * </ul>
     * 
     * @param expr ���ʽ�ַ���
     * @return ���ʽ
     */
    public Expression createExpression(String expr) throws ExpressionParseException {
        int length = expr.length();
        int startIndex = expr.indexOf("${");

        // ������ʽ������${}���򴴽�constant expression��
        if (startIndex < 0) {
            return new ConstantExpression(expr);
        }

        int endIndex = expr.indexOf("}", startIndex + 2);

        if (endIndex < 0) {
            throw new ExpressionParseException("Missing '}' character at the end of expression: " + expr);
        }

        // ������ʽ��${��ͷ����}��β����ֱ�ӵ���factory���������ʽ��
        if (startIndex == 0 && endIndex == length - 1) {
            return factory.createExpression(expr.substring(2, endIndex));
        }

        // �������ϵı��ʽ��
        List<Expression> expressions = createLinkedList();
        char ch = 0;
        int i = 0;

        StringBuffer chars = new StringBuffer();
        StringBuffer exprBuff = new StringBuffer();

        MAIN: while (i < length) {
            ch = expr.charAt(i);

            switch (ch) {
                case '$': {
                    if (i + 1 < length) {
                        ++i;
                        ch = expr.charAt(i);

                        switch (ch) {
                            case '$': {
                                chars.append(ch);
                                break;
                            }

                            case '{': {
                                if (chars.length() > 0) {
                                    expressions.add(new ConstantExpression(chars.toString()));
                                    chars.delete(0, chars.length());
                                }

                                if (i + 1 < length) {
                                    ++i;

                                    while (i < length) {
                                        ch = expr.charAt(i);

                                        {
                                            switch (ch) {
                                                case '"': {
                                                    exprBuff.append(ch);
                                                    ++i;

                                                    DOUBLE_QUOTE: while (i < length) {
                                                        ch = expr.charAt(i);

                                                        switch (ch) {
                                                            case '\\': {
                                                                ++i;
                                                                exprBuff.append(ch);
                                                                break;
                                                            }

                                                            case '"': {
                                                                ++i;
                                                                exprBuff.append(ch);
                                                                break DOUBLE_QUOTE;
                                                            }

                                                            default: {
                                                                ++i;
                                                                exprBuff.append(ch);
                                                            }
                                                        }
                                                    }

                                                    break;
                                                }

                                                case '\'': {
                                                    exprBuff.append(ch);
                                                    ++i;

                                                    SINGLE_QUOTE: while (i < length) {
                                                        ch = expr.charAt(i);

                                                        switch (ch) {
                                                            case '\\': {
                                                                ++i;
                                                                exprBuff.append(ch);
                                                                break;
                                                            }

                                                            case '\'': {
                                                                ++i;
                                                                exprBuff.append(ch);
                                                                break SINGLE_QUOTE;
                                                            }

                                                            default: {
                                                                ++i;
                                                                exprBuff.append(ch);
                                                            }
                                                        }
                                                    }

                                                    break;
                                                }

                                                case '}': {
                                                    expressions.add(factory.createExpression(exprBuff.toString()));

                                                    exprBuff.delete(0, exprBuff.length());
                                                    ++i;
                                                    continue MAIN;
                                                }

                                                default: {
                                                    exprBuff.append(ch);
                                                    ++i;
                                                }
                                            }
                                        }
                                    }
                                }

                                break;
                            }

                            default:
                                chars.append(ch);
                        }
                    } else {
                        chars.append(ch);
                    }

                    break;
                }

                default:
                    chars.append(ch);
            }

            ++i;
        }

        if (chars.length() > 0) {
            expressions.add(new ConstantExpression(chars.toString()));
        }

        return new CompositeExpression(expr, expressions);
    }
}
