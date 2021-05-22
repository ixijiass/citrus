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
package com.alibaba.citrus.springext.support.parser;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.springext.support.parser.NamedBeanDefinitionParserMixin.DefaultNameBDParser;

/**
 * ����һ��bean definition�����δָ��id����ʹ��<code>getDefaultName()</code>�����ص�Ĭ�����ơ�
 * <p>
 * ע�⣬���������ɻ���ֻ�Զ���bean��Ч����innerBean��Ȼʹ��ԭ�е��������ơ�
 * </p>
 * 
 * @author Michael Zhou
 */
public abstract class AbstractNamedBeanDefinitionParser<T> extends AbstractSingleBeanDefinitionParser<T> implements
        DefaultNameBDParser {
    private final NamedBeanDefinitionParserMixin mixin = new NamedBeanDefinitionParserMixin(this);

    /**
     * ȡ��bean��Ĭ�����ơ�
     * <p>
     * ����ע����Ĭ�������Զ��Ż�ո�ֿ����ڶ������Ƽ��������ƣ�����ע��ɱ�����
     * </p>
     */
    protected abstract String getDefaultName();

    /**
     * ��id attribute��ȡ��bean name������δָ�������<code>getDefaultName()</code>��ȡ��Ĭ������
     */
    @Override
    protected final String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return mixin.resolveId(element, definition, parserContext);
    }

    /**
     * ���統ǰbean nameΪĬ��������ͬʱע��Ĭ�ϵ�aliases��
     */
    @Override
    protected void registerBeanDefinition(BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
        mixin.registerBeanDefinition(definition, registry);
    }

    public final String internal_getDefaultName() {
        return getDefaultName();
    }

    public void super_registerBeanDefinition(BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
        super.registerBeanDefinition(definition, registry);
    }
}
