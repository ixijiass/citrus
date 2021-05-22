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
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.service.mappingrule.MappingRuleException;
import com.alibaba.citrus.service.mappingrule.support.AbstractModuleMappingRule;
import com.alibaba.citrus.service.mappingrule.support.AbstractModuleMappingRuleDefinitionParser;
import com.alibaba.citrus.service.moduleloader.ModuleLoaderException;
import com.alibaba.citrus.util.StringUtil;

/**
 * ����������ģ��ӳ�����
 * <ol>
 * <li>��<code>"/"</code>�滻��<code>"."</code>��</li>
 * <li>��ȥ�ļ�����׺��</li>
 * <li>�����һ����������ĸ�ĳɴ�д���Է���ģ�������Ĺ���</li>
 * <li>����module loader service���ģ���Ƿ���ڣ���������ڣ��������һ��ģ������һֱ�ҵ���Ŀ¼��</li>
 * <li>���ȫ�Ҳ����������Ĭ�����ƣ���ȷ���������������ģ����ڡ�</li>
 * <li>������ڣ��򷵻�֮�����򷵻ص�һ����ƥ���normalizedģ����������ȷƥ�䣩</li>
 * </ol>
 * <p>
 * ���磺��ģ������<code>"about/directions/driving.vm"</code>ӳ�䵽screen
 * module����˳����������module��
 * </p>
 * <ol>
 * <li><code>&quot;about.directions.Driving&quot;</code></li>
 * <li><code>&quot;about.directions.Default&quot;</code></li>
 * <li><code>&quot;about.Default&quot;</code></li>
 * <li><code>&quot;Default&quot;</code></li>
 * <li><code>&quot;DefaultScreen&quot;</code>���������ļ���ָ����Ĭ��module����</li>
 * </ol>
 * <p>
 * ע�⣬���������<code>DefaultScreen</code>�����ڻ�δָ��Ĭ��ֵ���򷵻�����Ľ����
 * <code>about.directions.Driving</code>��
 * </p>
 * 
 * @author Michael Zhou
 */
public class FallbackModuleMappingRule extends AbstractModuleMappingRule {
    public static final String DEFAULT_NAME = "Default";
    public static final boolean DEFAULT_MATCH_LAST_NAME = false;

    private String defaultName;
    private boolean matchLastName;

    public void setDefaultName(String defaultName) {
        this.defaultName = trimToNull(defaultName);
    }

    public void setMatchLastName(boolean matchLastName) {
        this.matchLastName = matchLastName;
    }

    @Override
    protected void initMappingRule() {
        assertNotNull(getModuleLoaderService(), "moduleLoaderService");
        assertNotNull(getModuleType(), "moduleType");
    }

    @Override
    public String doMapping(String name) {
        FallbackIterator iter = new FallbackModuleIterator(name, defaultName, matchLastName);

        String moduleName = null;
        String firstModuleName = iter.getNext(); // �����һ����ȷ��ƥ�䣬��һ�Ҳ������ͷ������ֵ

        while (iter.hasNext()) {
            moduleName = iter.next();

            log.debug("Looking for module: " + moduleName);

            try {
                if (getModuleLoaderService().getModuleQuiet(getModuleType(), moduleName) != null) {
                    return moduleName;
                } // else ��������
            } catch (ModuleLoaderException e) {
                throw new MappingRuleException(e);
            }
        }

        return firstModuleName;
    }

    static class FallbackModuleIterator extends FallbackIterator {
        public FallbackModuleIterator(String name, String finalName, boolean matchLastName) {
            super(name, DEFAULT_NAME, finalName, matchLastName);
        }

        @Override
        protected void invalidName(String name) {
            throwInvalidNameException(name);
        }

        @Override
        protected String normalizeLastName(String lastName) {
            return normalizeClassName(lastName);
        }

        @Override
        protected String generateFullName(List<String> names) {
            return StringUtil.join(names, AbstractModuleMappingRule.MODULE_NAME_SEPARATOR);
        }
    }

    public static class DefinitionParser extends AbstractModuleMappingRuleDefinitionParser<FallbackModuleMappingRule> {
        @Override
        protected void doParseModuleMappingRule(Element element, ParserContext parserContext,
                                                BeanDefinitionBuilder builder) {
            attributesToProperties(element, builder, "defaultName", "matchLastName");
        }
    }
}
