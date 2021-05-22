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

import com.alibaba.citrus.service.form.support.AbstractOptionalValidator;
import com.alibaba.citrus.service.form.support.AbstractValidatorDefinitionParser;

/**
 * �������ֵ���ַ������ȡ�
 * 
 * @author Michael Zhou
 */
public class StringLengthValidator extends AbstractOptionalValidator {
    private int minLength = 0;
    private int maxLength = -1;

    /**
     * ȡ����̳��ȡ�
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * ������̳��ȡ�
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /**
     * ȡ����󳤶ȡ�
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * ������󳤶ȡ�
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    protected boolean validate(Context context, String value) {
        int length = getLength(value);

        if (minLength >= 0 && length < minLength) {
            return false;
        }

        if (maxLength >= 0 && length > maxLength) {
            return false;
        }

        return true;
    }

    protected int getLength(String value) {
        return value.length();
    }

    public static class DefinitionParser extends AbstractValidatorDefinitionParser<StringLengthValidator> {
    }
}
