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
package com.alibaba.citrus.service.form.support;

import static com.alibaba.citrus.util.StringUtil.*;

/**
 * �����<code>Validator</code>ʵ�֣����������ཫ����ֵΪ�յ����Ρ�
 * 
 * @author Michael Zhou
 */
public abstract class AbstractOptionalValidator extends AbstractValidator {
    /**
     * ��֤һ���ֶΡ�
     */
    public boolean validate(Context context) {
        String value = context.getValueAsType(String.class);

        // ��trimming=falseģʽ�£��հ�Ҳ����ֵ��
        if (isEmpty(value)) {
            return true;
        }

        return validate(context, value);
    }

    /**
     * ��֤һ���ֶΡ�
     */
    protected abstract boolean validate(Context context, String value);
}
