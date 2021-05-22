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

import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.service.mappingrule.support.AbstractTemplateMappingRule;
import com.alibaba.citrus.service.mappingrule.support.AbstractTemplateMappingRuleDefinitionParser;
import com.alibaba.citrus.util.StringUtil;

/**
 * ����������ģ��ӳ�����
 * <ol>
 * <li>��ģ����ǰ����template prefix������еĻ�����</li>
 * <li>���ģ����δָ����׺���򲻼��Ϻ�׺��</li>
 * <li>����template service���ģ���Ƿ���ڣ���������ڣ��������һ��ģ������һֱ�ҵ���Ŀ¼��</li>
 * <li>���ȫ�Ҳ������򷵻ص�һ����ƥ���normalizedģ����������ȷƥ�䣩�����򷵻�ƥ���ģ������</li>
 * </ol>
 * <p>
 * ���磺��ģ������<code>"about/directions/driving.vm"</code>ӳ�䵽layout
 * template����˳����������ģ�壺
 * </p>
 * <ol>
 * <li><code>&quot;layout/about/directions/driving.vm&quot;</code></li>
 * <li><code>&quot;layout/about/directions/default.vm&quot;</code></li>
 * <li><code>&quot;layout/about/default.vm&quot;</code></li>
 * <li><code>&quot;layout/default.vm&quot;</code></li>
 * </ol>
 * 
 * @author Michael Zhou
 */
public class FallbackTemplateMappingRule extends AbstractTemplateMappingRule {
    public static final String DEFAULT_NAME = "default";
    public static final boolean DEFAULT_MATCH_LAST_NAME = false;

    private boolean matchLastName;

    @Override
    protected void initMappingRule() throws Exception {
        assertNotNull(getTemplateService(), "templateService");
        assertNotNull(getTemplatePrefix(), "templatePrefix");
    }

    @Override
    public String doMapping(String name) {
        FallbackTemplateIterator iter = new FallbackTemplateIterator(name, getTemplatePrefix(), matchLastName);

        // �����һ����ȷ��ƥ�䣬��һ�Ҳ������ͷ������ֵ
        String firstTemplateName = iter.getNext();

        while (iter.hasNext()) {
            String fullTemplateName = iter.next();

            if (getTemplateService().exists(fullTemplateName)) {
                return fullTemplateName;
            }
        }

        return firstTemplateName;
    }

    static class FallbackTemplateIterator extends FallbackIterator {
        private String templatePrefix;
        private String firstExt = EMPTY_STRING;

        public FallbackTemplateIterator(String name, String templatePrefix, boolean matchLastName) {
            super(name, DEFAULT_NAME, null, matchLastName);

            List<String> names = getNames();

            if (names != null && !names.isEmpty()) {
                String n = names.get(names.size() - 1);

                if (!StringUtil.isEmpty(n)) {
                    int extIndex = n.lastIndexOf(EXTENSION_SEPARATOR);

                    if (extIndex != -1) {
                        firstExt = n.substring(extIndex + 1, n.length());
                    }
                }
            }

            this.templatePrefix = trimToNull(templatePrefix);

            init();
        }

        @Override
        protected void invalidName(String name) {
            throwInvalidNameException(name);
        }

        @Override
        protected String normalizeLastName(String lastName) {
            return lastName;
        }

        @Override
        protected String generateFullName(List<String> names) {
            String fullName = StringUtil.join(names, TEMPLATE_NAME_SEPARATOR);

            if (templatePrefix != null) {
                fullName = templatePrefix + TEMPLATE_NAME_SEPARATOR + fullName;
            }

            String n = names.get(names.size() - 1);
            int extIndex = n.lastIndexOf(EXTENSION_SEPARATOR);

            if (extIndex == -1 && !StringUtil.isEmpty(firstExt)) {
                fullName = fullName + EXTENSION_SEPARATOR + firstExt;
            }

            return fullName;
        }
    }

    public static class DefinitionParser extends
            AbstractTemplateMappingRuleDefinitionParser<FallbackTemplateMappingRule> {
        @Override
        protected void doParseTemplateMappingRule(Element element, ParserContext parserContext,
                                                  BeanDefinitionBuilder builder) {
            attributesToProperties(element, builder, "matchLastName");
        }
    }
}
