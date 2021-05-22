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

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.UnsupportedEncodingException;

import com.alibaba.citrus.service.form.support.AbstractValidatorDefinitionParser;
import com.alibaba.citrus.util.i18n.LocaleUtil;

/**
 * �������ֵ���ַ������ȣ�������ת����ָ���ַ�����bytes��
 * 
 * @author Michael Zhou
 */
public class StringByteLengthValidator extends StringLengthValidator {
    private String charset;

    /**
     * ����ָ��charset��bytes����
     */
    public String getCharset() {
        if (charset == null) {
            return LocaleUtil.getContext().getCharset().name();
        } else {
            return charset;
        }
    }

    /**
     * ����ָ��charset��bytes����
     */
    public void setCharset(String charset) {
        this.charset = trimToNull(charset);
    }

    @Override
    protected void init() throws Exception {
        super.init();
        assertTrue(LocaleUtil.isCharsetSupported(getCharset()), "Invalid charset: " + getCharset());
    }

    @Override
    protected int getLength(String value) {
        try {
            return value.getBytes(getCharset()).length;
        } catch (UnsupportedEncodingException e) {
            return -1;
        }
    }

    public static class DefinitionParser extends AbstractValidatorDefinitionParser<StringByteLengthValidator> {
    }
}
