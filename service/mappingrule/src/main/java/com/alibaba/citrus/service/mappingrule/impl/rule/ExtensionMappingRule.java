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
package com.alibaba.citrus.service.mappingrule.impl.rule;

import static com.alibaba.citrus.springext.util.DomUtil.*;
import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.FileUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.service.mappingrule.support.AbstractMappingRule;
import com.alibaba.citrus.service.mappingrule.support.AbstractMappingRuleDefinitionParser;
import com.alibaba.citrus.util.FileUtil.FileNameAndExtension;
import com.alibaba.citrus.util.StringUtil;

/**
 * ת���ļ�����׺��ӳ�����
 * <p>
 * ��ruleĬ�ϲ�cache�����
 * </p>
 * 
 * @author Michael Zhou
 */
public class ExtensionMappingRule extends AbstractMappingRule {
    private Map<String, String> extensionMappings;

    public void setExtensionMappings(Map<String, String> extensions) {
        this.extensionMappings = extensions;
    }

    @Override
    protected void initMappingRule() throws Exception {
        if (extensionMappings == null) {
            extensionMappings = createHashMap();
        }
    }

    @Override
    protected boolean isCacheEnabledByDefault() {
        return false;
    }

    @Override
    public String doMapping(String name) {
        FileNameAndExtension names = getFileNameAndExtension(name, true);
        String extension = names.getExtension(); // ����Ϊnull

        if (extension == null) {
            extension = EMPTY_STRING;
        } else {
            extension = extension.toLowerCase();
        }

        // ���ӳ�������ڣ����滻��׺
        if (extensionMappings.containsKey(extension)) {
            String mapToExtension = extensionMappings.get(extension);

            name = names.getFileName(); // �ܲ�Ϊnull

            // �����/��β���Ͳ��Ӻ�׺��
            if (name.length() == 0 || !StringUtil.contains(NAME_SEPARATOR, name.charAt(name.length() - 1))) {
                // �����ȡ��ӳ���׺��Ϊ�գ�����Ϻ�׺
                if (!StringUtil.isEmpty(mapToExtension)) {
                    name = name + EXTENSION_SEPARATOR + mapToExtension;
                }
            }
        } else {
            // ����׺����ӳ������У��Һ�׺Ϊ�գ������������Ƶ�ǰ��
            if (StringUtil.isEmpty(extension)) {
                name = names.getFileName();
            }
        }

        return name;
    }

    public static class DefinitionParser extends AbstractMappingRuleDefinitionParser<ExtensionMappingRule> {
        @Override
        protected void doParseMappingRule(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            Map<Object, Object> extensionMappings = createManagedMap(element, parserContext);

            for (Element subElement : subElements(element, and(sameNs(element), name("mapping")))) {
                // һ��ת����Сд��֧�ֿ��ַ�������ʾ�޺�׺
                String from = trimToEmpty(subElement.getAttribute("extension")).toLowerCase();
                String to = trimToEmpty(subElement.getAttribute("to")).toLowerCase();

                extensionMappings.put(from, to);
            }

            builder.addPropertyValue("extensionMappings", extensionMappings);
        }
    }
}
