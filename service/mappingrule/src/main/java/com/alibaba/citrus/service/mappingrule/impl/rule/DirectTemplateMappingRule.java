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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.service.mappingrule.support.AbstractTemplateMappingRule;
import com.alibaba.citrus.service.mappingrule.support.AbstractTemplateMappingRuleDefinitionParser;
import com.alibaba.citrus.util.ArrayUtil;
import com.alibaba.citrus.util.StringUtil;

/**
 * ���ģ��ӳ�����,����ѯtemplateService��ֻƴװ·����
 * <ol>
 * <li>��ģ����ǰ����template prefix������еĻ�����</li>
 * <li>���ģ����δָ����׺����Ҳ�����Ϻ�׺��</li>
 * <li>��ruleĬ�ϲ�cache�����</li>
 * </ol>
 * <p>
 * ���磺��ģ������<code>"about/directions/driving.vm"</code>ӳ�䵽layout(template prefix)��
 * <code>"layout/about/directions/driving.vm"</code>��
 * </p>
 * 
 * @author Michael Zhou
 */
public class DirectTemplateMappingRule extends AbstractTemplateMappingRule {
    @Override
    protected boolean isCacheEnabledByDefault() {
        return false;
    }

    @Override
    public String doMapping(String name) {
        String[] parts = StringUtil.split(name, NAME_SEPARATOR);

        if (ArrayUtil.isEmptyArray(parts)) {
            return throwInvalidNameException(name);
        }

        boolean withPrefix = !StringUtil.isEmpty(getTemplatePrefix());
        String firstTemplateName = StringUtil.join(parts, TEMPLATE_NAME_SEPARATOR);

        if (withPrefix) {
            firstTemplateName = getTemplatePrefix() + TEMPLATE_NAME_SEPARATOR + firstTemplateName;
        }

        return firstTemplateName;
    }

    public static class DefinitionParser extends AbstractTemplateMappingRuleDefinitionParser<DirectTemplateMappingRule> {
        @Override
        protected void doParseTemplateMappingRule(Element element, ParserContext parserContext,
                                                  BeanDefinitionBuilder builder) {
        }
    }
}
