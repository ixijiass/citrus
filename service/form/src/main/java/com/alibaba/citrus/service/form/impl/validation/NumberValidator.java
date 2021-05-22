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
package com.alibaba.citrus.service.form.impl.validation;

import static com.alibaba.citrus.service.form.support.CompareOperator.*;
import static com.alibaba.citrus.util.StringUtil.*;

import com.alibaba.citrus.service.form.support.AbstractNumberValidator;
import com.alibaba.citrus.service.form.support.AbstractValidatorDefinitionParser;
import com.alibaba.citrus.service.form.support.CompareOperator;
import com.alibaba.citrus.service.form.support.NumberSupport;

/**
 * ��֤�û����������ȷ�����ָ�ʽ���������ַ����޶��ķ�Χ��
 * 
 * @author Michael Zhou
 */
public class NumberValidator extends AbstractNumberValidator {
    private NumberSupport[] operands = new NumberSupport[CompareOperator.values().length];

    /**
     * ȡ���޶�ֵ�����ڡ�
     */
    public String getEqualTo() {
        return getOperandString(equalTo);
    }

    /**
     * �����޶�ֵ�����ڡ�
     */
    public void setEqualTo(String value) {
        setOperand(equalTo, value);
    }

    /**
     * ȡ���޶�ֵ�������ڡ�
     */
    public String getNotEqualTo() {
        return getOperandString(notEqualTo);
    }

    /**
     * �����޶�ֵ�������ڡ�
     */
    public void setNotEqualTo(String value) {
        setOperand(notEqualTo, value);
    }

    /**
     * ȡ���޶�ֵ��С�ڡ�
     */
    public String getLessThan() {
        return getOperandString(lessThan);
    }

    /**
     * �����޶�ֵ��С�ڡ�
     */
    public void setLessThan(String value) {
        setOperand(lessThan, value);
    }

    /**
     * ȡ���޶�ֵ�����ڡ�
     */
    public String getGreaterThan() {
        return getOperandString(greaterThan);
    }

    /**
     * �����޶�ֵ�����ڡ�
     */
    public void setGreaterThan(String value) {
        setOperand(greaterThan, value);
    }

    /**
     * ȡ���޶�ֵ��С�ڵ��ڡ�
     */
    public String getLessThanOrEqualTo() {
        return getOperandString(lessThanOrEqualTo);
    }

    /**
     * �����޶�ֵ��С�ڵ��ڡ�
     */
    public void setLessThanOrEqualTo(String value) {
        setOperand(lessThanOrEqualTo, value);
    }

    /**
     * ȡ���޶�ֵ�����ڵ��ڡ�
     */
    public String getGreaterThanOrEqualTo() {
        return getOperandString(greaterThanOrEqualTo);
    }

    /**
     * �����޶�ֵ�����ڵ��ڡ�
     */
    public void setGreaterThanOrEqualTo(String value) {
        setOperand(greaterThanOrEqualTo, value);
    }

    private String getOperandString(CompareOperator op) {
        NumberSupport n = getOperand(op);
        return n == null ? null : n.getStringValue();
    }

    public final NumberSupport getOperand(CompareOperator op) {
        return operands[op.ordinal()];
    }

    protected final void setOperand(CompareOperator op, String value) {
        operands[op.ordinal()] = new NumberSupport(null, trimToNull(value));
    }

    @Override
    protected void init() throws Exception {
        super.init();

        // parse operands, throws NumberFormatException
        for (NumberSupport operand : operands) {
            if (operand != null) {
                operand.setNumberType(getNumberType());
                operand.getValue();
            }
        }
    }

    /**
     * ��֤һ���ֶΡ�
     */
    @Override
    protected boolean validate(Context context, String value) {
        NumberSupport numberValue = new NumberSupport(getNumberType(), value);
        boolean valid = true;

        try {
            numberValue.getValue();
        } catch (NumberFormatException e) {
            valid = false;
        }

        for (int i = 0; i < operands.length; i++) {
            if (operands[i] != null) {
                valid &= CompareOperator.values()[i].accept(numberValue.compareTo(operands[i]));
            }
        }

        return valid;
    }

    public static class DefinitionParser extends AbstractValidatorDefinitionParser<NumberValidator> {
    }
}
