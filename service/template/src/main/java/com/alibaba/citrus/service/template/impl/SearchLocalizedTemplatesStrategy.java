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
package com.alibaba.citrus.service.template.impl;

import static com.alibaba.citrus.util.i18n.LocaleUtil.*;

import java.util.List;
import java.util.Locale;

import com.alibaba.citrus.util.i18n.LocaleUtil;

/**
 * ���ݵ�ǰlocale��������ƥ��ı��ػ�ģ�塣
 * <p>
 * ���磺����ģ��<code>test.vm</code>�����統ǰlocaleΪ<code>zh_CN</code>�������γ��Բ��ң�
 * </p>
 * <ul>
 * <li>test_zh_CN.vm</li>
 * <li>test_zh.vm</li>
 * <li>test.vm</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public class SearchLocalizedTemplatesStrategy implements TemplateSearchingStrategy {
    public Object getKey(String templateName) {
        return getCurrentLocale();
    }

    public boolean findTemplate(TemplateMatcher matcher) {
        List<String> localizedTemplateNames = calculateBundleNames(matcher.getTemplateNameWithoutExtension(),
                getCurrentLocale(), true);

        for (String name : localizedTemplateNames) {
            matcher.setTemplateNameWithoutExtension(name);

            if (matcher.findTemplate()) {
                return true;
            }
        }

        return false;
    }

    private Locale getCurrentLocale() {
        return LocaleUtil.getContext().getLocale();
    }
}
