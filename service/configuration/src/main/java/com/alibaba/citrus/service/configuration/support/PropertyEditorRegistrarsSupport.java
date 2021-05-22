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
package com.alibaba.citrus.service.configuration.support;

import static com.alibaba.citrus.springext.util.DomUtil.*;
import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static java.util.Collections.*;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.springext.util.DomUtil.ElementSelector;
import com.alibaba.citrus.util.internal.ToStringBuilder;

/**
 * ����һ��property editor registrar�ļ��ϣ�Ȼ��������Ҳ��һ��
 * <code>PropertyEditorRegistrar</code>��
 * 
 * @author Michael Zhou
 */
public class PropertyEditorRegistrarsSupport implements PropertyEditorRegistrar, Iterable<PropertyEditorRegistrar> {
    private List<PropertyEditorRegistrar> propertyEditorRegistrars = emptyList();

    /**
     * ȡ��һ��<code>PropertyEditor</code>ע������
     * <p>
     * <code>PropertyEditor</code>�����ַ���ֵת����bean property�����ͣ���֮��
     * </p>
     */
    public PropertyEditorRegistrar[] getPropertyEditorRegistrars() {
        return propertyEditorRegistrars.toArray(new PropertyEditorRegistrar[propertyEditorRegistrars.size()]);
    }

    /**
     * ����һ��<code>PropertyEditor</code>ע������
     * <p>
     * <code>PropertyEditor</code>�����ַ���ֵת����bean property�����ͣ���֮��
     * </p>
     */
    public void setPropertyEditorRegistrars(PropertyEditorRegistrar[] propertyEditorRegistrars) {
        this.propertyEditorRegistrars = createArrayList(propertyEditorRegistrars);
    }

    /**
     * ��registry��ע���Զ����<code>PropertyEditor</code>��
     */
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        for (PropertyEditorRegistrar registrar : getPropertyEditorRegistrars()) {
            registrar.registerCustomEditors(registry);
        }
    }

    /**
     * �鿴�м���registrars��
     */
    public int size() {
        return propertyEditorRegistrars.size();
    }

    /**
     * ����registrars��
     */
    public Iterator<PropertyEditorRegistrar> iterator() {
        return propertyEditorRegistrars.iterator();
    }

    /**
     * ����element�µ�����registrars��
     */
    public static Object parseRegistrars(Element parentElement, ParserContext parserContext,
                                         BeanDefinitionBuilder containingBeanDefBuilder) {
        ElementSelector registrarSelector = and(sameNs(parentElement), name("property-editor-registrar"));
        List<Object> registrars = createManagedList(parentElement, parserContext);

        for (Element subElement : subElements(parentElement, registrarSelector)) {
            registrars.add(parseBean(subElement, parserContext, containingBeanDefBuilder));
        }

        return registrars;
    }

    @Override
    public String toString() {
        return new ToStringBuilder().append(getClass().getSimpleName()).append(propertyEditorRegistrars).toString();
    }
}
