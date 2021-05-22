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

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.List;

import com.alibaba.citrus.service.form.Validator;
import com.alibaba.citrus.service.form.impl.validation.composite.AllOfValidator;

/**
 * ֻ��һ����validator�����ʽvalidator�Ļ��ࡣ
 * <p>
 * ���类�����˶��validator�����Զ�����һ��������all-of-validator��
 * </p>
 * 
 * @author Michael Zhou
 */
public abstract class AbstractSimpleCompositeValidator extends AbstractCompositeValidator {
    @Override
    protected void init() throws Exception {
        super.init();

        List<Validator> validators = getValidators();

        assertTrue(!validators.isEmpty(), "no validators");

        if (validators.size() > 1) {
            AllOfValidator all = new AllOfValidator();
            all.setValidators(validators);

            try {
                all.afterPropertiesSet();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

            validators = createArrayList((Validator) all);
            setValidators(validators);
        }
    }

    /**
     * ȡ��Ψһ��validator��
     */
    public Validator getValidator() {
        assertInitialized();
        return getValidators().get(0);
    }

    /**
     * ����validator��֤����ȡ���������Ϣ��
     */
    protected final boolean doValidate(Context context) {
        Validator validator = getValidator();
        Context newContext = newContext(context, validator);

        if (!validator.validate(newContext)) {
            context.setMessage(validator.getMessage(newContext));
            return false;
        }

        return true;
    }
}
