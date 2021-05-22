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

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.List;

/**
 * ���統ǰģ������׺�޷��ҵ�ģ�壬������������׺Ѱ�ҡ�
 * <p>
 * ���磺ģ��<code>test.vm</code>�����ڣ���ô�ͳ���һ��<code>test.jsp</code>��
 * <code>test.ftl</code>�ȡ�
 * </p>
 * 
 * @author Michael Zhou
 */
public class SearchExtensionsStrategy implements TemplateSearchingStrategy {
    private final String[] availableExtensions;

    public SearchExtensionsStrategy(String[] extensions) {
        this.availableExtensions = assertNotNull(extensions, "extensions");
    }

    public Object getKey(String templateName) {
        return null;
    }

    public boolean findTemplate(TemplateMatcher matcher) {
        List<String> testedExtensions = createArrayList(availableExtensions.length);
        boolean found = false;
        String ext = matcher.getExtension();

        if (ext != null) {
            testedExtensions.add(ext);
            found = matcher.findTemplate();
        }

        for (int i = 0; !found && i < availableExtensions.length; i++) {
            ext = availableExtensions[i];

            if (!testedExtensions.contains(ext)) {
                testedExtensions.add(ext);
                matcher.setExtension(ext);
                found = matcher.findTemplate();
            }
        }

        return found;
    }
}
