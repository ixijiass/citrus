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

import static com.alibaba.citrus.generictype.TypeInfoUtil.*;
import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.turbine.dataresolver.impl.DataResolverUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.cglib.reflect.FastConstructor;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.MethodParameter;
import org.w3c.dom.Element;

import com.alibaba.citrus.service.dataresolver.DataResolver;
import com.alibaba.citrus.service.dataresolver.DataResolverContext;
import com.alibaba.citrus.service.dataresolver.DataResolverFactory;
import com.alibaba.citrus.service.form.Field;
import com.alibaba.citrus.service.form.Form;
import com.alibaba.citrus.service.form.FormService;
import com.alibaba.citrus.service.form.Group;
import com.alibaba.citrus.service.form.configuration.FieldConfig;
import com.alibaba.citrus.service.form.configuration.GroupConfig;
import com.alibaba.citrus.service.moduleloader.SkipModuleExecutionException;
import com.alibaba.citrus.springext.support.parser.AbstractSingleBeanDefinitionParser;
import com.alibaba.citrus.turbine.dataresolver.FormData;
import com.alibaba.citrus.turbine.dataresolver.FormField;
import com.alibaba.citrus.turbine.dataresolver.FormFields;
import com.alibaba.citrus.turbine.dataresolver.FormGroup;
import com.alibaba.citrus.turbine.dataresolver.FormGroups;

public class FormResolverFactory implements DataResolverFactory {
    private final FormService formService;

    public FormResolverFactory(FormService formService) {
        this.formService = formService;
    }

    public DataResolver getDataResolver(DataResolverContext context) {
        // ????????????????????????resolver factory????????????????????resolver????????
        // ????????????????????????????????????????????????????????????????????
        assertNotNull(formService, "no FormService defined");

        Class<?> paramType = context.getTypeInfo().getRawType();

        // Form????
        FormData formAnnotation = context.getAnnotation(FormData.class);

        if (formAnnotation != null || paramType.isAssignableFrom(Form.class)) {
            return new FormResolver(context, paramType, formAnnotation);
        }

        // Group??????annotation @FormGroup??????????????Group??????POJO
        FormGroup groupAnnotation = context.getAnnotation(FormGroup.class);

        if (groupAnnotation != null) {
            return new GroupResolver(context, paramType, groupAnnotation);
        }

        // Field??????annotation @FormField??????????????Field??????????
        FormField fieldAnnotation = context.getAnnotation(FormField.class);

        if (fieldAnnotation != null) {
            return new FieldResolver(context, paramType, fieldAnnotation);
        }

        // Groups??????annotation @FormGroups??????????????Group[]??List<Group>??????????????????
        FormGroups groupsAnnotation = context.getAnnotation(FormGroups.class);

        if (groupsAnnotation != null) {
            return new GroupsResolver(context, paramType, groupsAnnotation);
        }

        // Fields??????annotation @FormFields??????????????Field[]??List<Field>??????????????????
        FormFields fieldsAnnotation = context.getAnnotation(FormFields.class);

        if (fieldsAnnotation != null) {
            return new FieldsResolver(context, paramType, fieldsAnnotation);
        }

        return null;
    }

    private boolean isConverterQuiet(Form form) {
        return form.getFormConfig().isConverterQuiet();
    }

    /**
     * ????form????????groups??????????????????????????
     */
    private boolean isValidatedAndValid(Form form) {
        if (!form.isValid() || form.getGroups().isEmpty()) {
            return false;
        }

        for (Group group : form.getGroups()) {
            if (!group.isValidated()) {
                return false;
            }
        }

        return true;
    }

    private class FormResolver extends AbstractFormResolver {
        public FormResolver(DataResolverContext context, Class<?> paramType, FormData formAnnotation) {
            super("FormResolver", context);

            // ??????????????Form
            assertTrue(paramType.isAssignableFrom(Form.class), "Parameter type annotated with @FormData should be Form");

            this.skipIfInvalid = true;

            // @FormData????
            if (formAnnotation != null) {
                this.skipIfInvalid = formAnnotation.skipIfInvalid();
            }
        }

        public Object resolve() {
            Form form = formService.getForm();
            skipModuleExecutionIfNecessary(isValidatedAndValid(form), form);
            return form;
        }
    }

    private class GroupResolver extends AbstractFormResolver {
        private final String groupName;
        private final String groupInstanceKey;
        private final FastConstructor fc;

        public GroupResolver(DataResolverContext context, Class<?> paramType, FormGroup groupAnnotation) {
            super("GroupResolver", context);

            groupInstanceKey = trimToNull(groupAnnotation.instanceKey());
            skipIfInvalid = groupAnnotation.skipIfInvalid();

            // name()??value() - ????group????
            groupName = getGroupConfig(
                    getAnnotationNameOrValue(FormGroup.class, groupAnnotation, context, groupInstanceKey != null
                            || skipIfInvalid == false)).getName();

            // ??????pojo????????constructor??
            if (!paramType.isAssignableFrom(Group.class)) {
                fc = getFastConstructor(paramType);
            } else {
                fc = null;
            }
        }

        public Object resolve() {
            Form form = formService.getForm();
            Group group = form.getGroup(groupName, groupInstanceKey);

            boolean valid = isValidatedAndValid(form);

            if (fc == null) {
                skipModuleExecutionIfNecessary(valid, group);
                return group;
            } else {
                skipModuleExecutionIfNecessary(valid, null);

                if (valid) {
                    Object object = newInstance(fc);
                    group.setProperties(object);
                    return object;
                } else {
                    return null;
                }
            }
        }
    }

    private class FieldResolver extends AbstractFormResolver {
        private final String groupName;
        private final String groupInstanceKey;
        private final String fieldName;

        public FieldResolver(DataResolverContext context, Class<?> paramType, FormField fieldAnnotation) {
            super("FieldResolver", context);

            // ????group????
            groupName = getGroupConfig(fieldAnnotation.group()).getName();

            // ????field????
            fieldName = getFieldConfig(groupName, fieldAnnotation.name()).getName();

            groupInstanceKey = trimToNull(fieldAnnotation.groupInstanceKey());
            skipIfInvalid = fieldAnnotation.skipIfInvalid();
        }

        public Object resolve() {
            Form form = formService.getForm();
            Group group = form.getGroup(groupName, groupInstanceKey);
            Field field = group.getField(fieldName);

            boolean valid = isValidatedAndValid(form);

            if (context.getTypeInfo().getRawType().isAssignableFrom(Field.class)) {
                skipModuleExecutionIfNecessary(valid, field);
                return field;
            } else {
                skipModuleExecutionIfNecessary(valid, null);

                if (valid) {
                    try {
                        return field.getValueOfType(context.getTypeInfo().getRawType(),
                                context.getExtraObject(MethodParameter.class), null);
                    } catch (TypeMismatchException e) {
                        if (!isConverterQuiet(form)) {
                            throw e;
                        }
                    }
                }

                return null;
            }
        }
    }

    private class GroupsResolver extends AbstractFormResolver {
        private final String groupName;
        private final Class<?> componentType;
        private final FastConstructor fc;

        public GroupsResolver(DataResolverContext context, Class<?> paramType, FormGroups groupsAnnotation) {
            super("GroupResolver", context);

            skipIfInvalid = groupsAnnotation.skipIfInvalid();

            // name()??value() - ????group????
            groupName = getGroupConfig(
                    getAnnotationNameOrValue(FormGroups.class, groupsAnnotation, context, skipIfInvalid == false))
                    .getName();

            // param????????????Collection
            if (paramType.isArray()) {
                componentType = paramType.getComponentType();
            } else if (Collection.class.isAssignableFrom(paramType)) {
                componentType = resolveIterableElement(context.getTypeInfo()).getRawType();
            } else {
                componentType = null;
            }

            // component??????????Group??????????????????Object??
            assertTrue(componentType != null && !Object.class.equals(componentType), "Invalid paramType: %s",
                    context.getTypeInfo());

            if (!componentType.isAssignableFrom(Group.class)) {
                fc = getFastConstructor(componentType);
            } else {
                fc = null;
            }
        }

        public Object resolve() {
            Form form = formService.getForm();
            Collection<Group> groups = form.getGroups(groupName);

            boolean valid = isValidatedAndValid(form);

            if (fc == null) {
                Object result = null;

                try {
                    result = form.getTypeConverter().convertIfNecessary(groups, context.getTypeInfo().getRawType(),
                            context.getExtraObject(MethodParameter.class));
                } catch (TypeMismatchException e) {
                    if (!isConverterQuiet(form)) {
                        throw e;
                    }

                    return null;
                }

                skipModuleExecutionIfNecessary(valid, result);
                return result;
            } else {
                skipModuleExecutionIfNecessary(valid, null);

                if (!valid) {
                    return null;
                }

                Object[] results = new Object[groups.size()];

                int i = 0;
                for (Group group : groups) {
                    Object object = newInstance(fc);
                    group.setProperties(object);
                    results[i++] = object;
                }

                try {
                    return form.getTypeConverter().convertIfNecessary(results, context.getTypeInfo().getRawType(),
                            context.getExtraObject(MethodParameter.class));
                } catch (TypeMismatchException e) {
                    if (!isConverterQuiet(form)) {
                        throw e;
                    }

                    return null;
                }
            }
        }
    }

    private class FieldsResolver extends AbstractFormResolver {
        private final String groupName;
        private final String fieldName;
        private final Class<?> componentType;

        public FieldsResolver(DataResolverContext context, Class<?> paramType, FormFields fieldsAnnotation) {
            super("FieldsResolver", context);

            skipIfInvalid = fieldsAnnotation.skipIfInvalid();

            // ????group????
            groupName = getGroupConfig(fieldsAnnotation.group()).getName();

            // ????field????
            fieldName = getFieldConfig(groupName, fieldsAnnotation.name()).getName();

            // param????????????Collection
            if (paramType.isArray()) {
                componentType = paramType.getComponentType();
            } else if (Collection.class.isAssignableFrom(paramType)) {
                componentType = resolveIterableElement(context.getTypeInfo()).getRawType();
            } else {
                componentType = null;
            }

            // component??????????Field??????????????????Object??
            assertTrue(componentType != null && !Object.class.equals(componentType), "Invalid paramType: %s",
                    context.getTypeInfo());
        }

        public Object resolve() {
            Form form = formService.getForm();

            // ????????group instances????????field??
            Collection<Group> groups = form.getGroups(groupName);
            List<Field> fields = new ArrayList<Field>(groups.size());

            for (Group group : groups) {
                fields.add(group.getField(fieldName));
            }

            boolean valid = isValidatedAndValid(form);

            if (componentType.isAssignableFrom(Field.class)) {
                Object result = null;

                try {
                    result = form.getTypeConverter().convertIfNecessary(fields, context.getTypeInfo().getRawType(),
                            context.getExtraObject(MethodParameter.class));
                } catch (TypeMismatchException e) {
                    if (!isConverterQuiet(form)) {
                        throw e;
                    }

                    return null;
                }

                skipModuleExecutionIfNecessary(valid, result);
                return result;
            } else {
                skipModuleExecutionIfNecessary(valid, null);

                if (!valid) {
                    return null;
                }

                Object[] results = new Object[fields.size()];

                for (int i = 0; i < results.length; i++) {
                    try {
                        results[i] = fields.get(i).getValueOfType(componentType,
                                context.getExtraObject(MethodParameter.class), null);
                    } catch (TypeMismatchException e) {
                        if (!isConverterQuiet(form)) {
                            throw e;
                        }

                        results[i] = null;
                    }
                }

                try {
                    return form.getTypeConverter().convertIfNecessary(results, context.getTypeInfo().getRawType(),
                            context.getExtraObject(MethodParameter.class));
                } catch (TypeMismatchException e) {
                    if (!isConverterQuiet(form)) {
                        throw e;
                    }

                    return null;
                }
            }
        }
    }

    private abstract class AbstractFormResolver extends AbstractDataResolver {
        protected boolean skipIfInvalid;

        private AbstractFormResolver(String desc, DataResolverContext context) {
            super(desc, context);
        }

        protected final void skipModuleExecutionIfNecessary(boolean valid, Object valueForNonSkippable)
                throws SkipModuleExecutionException {
            if (skipIfInvalid && !valid) {
                throw new SkipModuleExecutionException("Form data is not valid", valueForNonSkippable);
            }
        }

        protected final GroupConfig getGroupConfig(String groupName) {
            groupName = assertNotNull(trimToNull(groupName), "group name is empty");
            return assertNotNull(formService.getFormConfig().getGroupConfig(groupName),
                    "group \"%s\" does not defined", groupName);
        }

        protected final FieldConfig getFieldConfig(String groupName, String fieldName) {
            GroupConfig groupConfig = getGroupConfig(groupName);
            fieldName = assertNotNull(trimToNull(fieldName), "field name is empty");
            return assertNotNull(groupConfig.getFieldConfig(fieldName), "field \"%s.%s\" does not defined",
                    groupConfig.getName(), fieldName);
        }
    }

    public static class DefinitionParser extends AbstractSingleBeanDefinitionParser<FormResolverFactory> {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            addConstructorArg(builder, false, FormService.class);
        }
    }
}
