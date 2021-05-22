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
package com.alibaba.citrus.turbine.dataresolver.impl;

import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.StringUtil.*;
import net.sf.cglib.reflect.FastConstructor;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.MethodParameter;
import org.w3c.dom.Element;

import com.alibaba.citrus.service.dataresolver.DataResolver;
import com.alibaba.citrus.service.dataresolver.DataResolverContext;
import com.alibaba.citrus.service.dataresolver.DataResolverFactory;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext;
import com.alibaba.citrus.springext.support.parser.AbstractSingleBeanDefinitionParser;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.citrus.turbine.dataresolver.Params;

/**
 * ȡ���û��ύ�Ĳ�����
 * 
 * @author Michael Zhou
 */
public class ParameterResolverFactory implements DataResolverFactory {
    private final ParserRequestContext parserRequestContext;

    public ParameterResolverFactory(ParserRequestContext parserRequestContext) {
        this.parserRequestContext = assertProxy(parserRequestContext);
    }

    public DataResolver getDataResolver(DataResolverContext context) {
        // ������Ҫ�Ķ���δ����ʱ��resolver factory�Կ��Դ���������ȡ��resolverʱ����
        // ����ʹ��ͬһ�����ÿ��������л�������������Ҫע���ض�����ʱ���ű���
        assertNotNull(parserRequestContext, "no ParserRequestContext defined");

        // ��������
        Param paramAnnotation = context.getAnnotation(Param.class);

        if (paramAnnotation != null) {
            String[] defaultValues = getDefaultValues(paramAnnotation, context);
            String paramName = DataResolverUtil.getAnnotationNameOrValue(Param.class, paramAnnotation, context,
                    !isEmptyArray(defaultValues));

            return new ParameterResolver(context, defaultValues, paramName);
        }

        // ����ע��
        Params paramsAnnotation = context.getAnnotation(Params.class);

        if (paramsAnnotation != null) {
            Class<?> beanType = context.getTypeInfo().getRawType();
            FastConstructor fc = DataResolverUtil.getFastConstructor(beanType);
            return new ParametersResolver(context, fc);
        }

        return null;
    }

    private String[] getDefaultValues(Param param, DataResolverContext context) {
        String defaultValue = trimToNull(param.defaultValue());

        if (defaultValue == null) {
            return param.defaultValues();
        } else {
            // ����defaultValue��defaultValuesͬʱ���֡�
            assertTrue(isEmptyArray(param.defaultValues()),
                    "use @Param(... defaultValue=\"...\") or @Param(... defaultValues={...}): %s", context);

            return new String[] { defaultValue };
        }
    }

    /**
     * ������������������resolver��
     */
    private class ParameterResolver extends AbstractDataResolver {
        private final String[] defaultValues;
        private final String paramName;

        private ParameterResolver(DataResolverContext context, String[] defaultValues, String paramName) {
            super("ParameterResolver", context);
            this.defaultValues = defaultValues;
            this.paramName = paramName;
        }

        public Object resolve() {
            ParameterParser params = parserRequestContext.getParameters();
            Class<?> paramType = context.getTypeInfo().getRawType();
            MethodParameter methodParameter = context.getExtraObject(MethodParameter.class);

            return params.getObjectOfType(paramName, paramType, methodParameter, defaultValues);
        }
    }

    /**
     * �������������ע��bean�е�resolver��
     */
    private class ParametersResolver extends AbstractDataResolver {
        private final FastConstructor fc;

        private ParametersResolver(DataResolverContext context, FastConstructor fc) {
            super("ParametersResolver", context);
            this.fc = assertNotNull(fc, "fc");
        }

        public Object resolve() {
            ParameterParser params = parserRequestContext.getParameters();
            Object object = DataResolverUtil.newInstance(fc);

            params.setProperties(object);

            return object;
        }
    }

    public static class DefinitionParser extends AbstractSingleBeanDefinitionParser<ParameterResolverFactory> {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            addConstructorArg(builder, false, ParserRequestContext.class);
        }
    }
}
