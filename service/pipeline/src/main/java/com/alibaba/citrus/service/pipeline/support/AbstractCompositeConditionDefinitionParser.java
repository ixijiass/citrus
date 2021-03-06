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
package com.alibaba.citrus.service.pipeline.support;

import static com.alibaba.citrus.springext.util.DomUtil.*;
import static com.alibaba.citrus.springext.util.SpringExtUtil.*;

import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.springext.ConfigurationPoint;
import com.alibaba.citrus.springext.Contribution;
import com.alibaba.citrus.springext.ContributionAware;

/**
 * <code>AbstractCompositeCondition</code>???????Ļ??ࡣ
 * 
 * @author Michael Zhou
 */
public class AbstractCompositeConditionDefinitionParser<C extends AbstractCompositeCondition> extends
        AbstractConditionDefinitionParser<C> implements ContributionAware {
    private ConfigurationPoint conditionConfigurationPoint;

    public void setContribution(Contribution contrib) {
        this.conditionConfigurationPoint = getSiblingConfigurationPoint("services/pipeline/conditions", contrib);
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);

        List<Object> conditions = createManagedList(element, parserContext);

        for (Element subElement : subElements(element)) {
            Object condition = parseConfigurationPointBean(subElement, conditionConfigurationPoint, parserContext,
                    builder);

            if (condition != null) {
                conditions.add(condition);
            }
        }

        builder.addPropertyValue("conditions", conditions);
    }
}
