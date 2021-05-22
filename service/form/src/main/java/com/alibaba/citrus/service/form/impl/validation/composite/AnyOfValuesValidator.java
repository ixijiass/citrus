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
package com.alibaba.citrus.service.form.impl.validation.composite;

import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.List;

import com.alibaba.citrus.service.form.Validator;
import com.alibaba.citrus.service.form.support.AbstractCompositeValidatorDefinitionParser;
import com.alibaba.citrus.service.form.support.AbstractMultiValuesValidator;

/**
 * ��ǰfield���κ�һ��ֵͨ����֤ʱ����validator��ͨ����֤��
 * <p>
 * Message��ʹ��<code>${allMessages}</code>ȡ����validator��message��
 * ��validator��message��ʹ��<code>${valueIndex}</code>ȡ�õ�ǰ����ֵ��
 * </p>
 * 
 * @author Michael Zhou
 */
public class AnyOfValuesValidator extends AbstractMultiValuesValidator {
    @Override
    protected boolean validate(Context context, Object[] values) {
        Validator validator = getValidator();
        List<String> messages = createArrayList(getValidators().size());

        context.getMessageContext().put("allMessages", messages);

        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            Context newContext = newContext(context, validator, value);

            newContext.getMessageContext().put("valueIndex", i);

            if (validator.validate(newContext)) {
                return true;
            } else {
                messages.add(validator.getMessage(newContext));
            }
        }

        return false;
    }

    public static class DefinitionParser extends AbstractCompositeValidatorDefinitionParser<AnyOfValuesValidator> {
    }
}
