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
package com.alibaba.citrus.springext.util;

import static com.alibaba.citrus.springext.util.DomUtil.*;
import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static org.springframework.util.ReflectionUtils.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.alibaba.citrus.springext.ConfigurationPoint;
import com.alibaba.citrus.springext.Contribution;
import com.alibaba.citrus.springext.util.DomUtil.ElementSelector;
import com.alibaba.citrus.util.Assert;

public class SpringExtUtil {
    private static final Logger log = LoggerFactory.getLogger(SpringExtUtil.class);

    public static <T> T getBeanOfType(BeanFactory beanFactory, Class<T> type) {
        if (beanFactory == null || !(beanFactory instanceof ListableBeanFactory)) {
            return null;
        }

        ListableBeanFactory listable = (ListableBeanFactory) beanFactory;

        @SuppressWarnings("unchecked")
        Map<String, T> beans = listable.getBeansOfType(type);

        if (beans == null || beans.isEmpty()) {
            return null;
        }

        return beans.values().iterator().next();
    }

    public static <T> T autowireAndInitialize(T existingBean, ApplicationContext context, int autowireMode,
                                              String beanName) {
        context.getAutowireCapableBeanFactory().autowireBeanProperties(existingBean, autowireMode, false);
        context.getAutowireCapableBeanFactory().initializeBean(existingBean, beanName);

        return existingBean;
    }

    public static ConfigurationPoint getSiblingConfigurationPoint(String configurationPointName, Contribution contrib) {
        assertNotNull(contrib, "contribution");
        return getSiblingConfigurationPoint(configurationPointName, contrib.getConfigurationPoint());
    }

    public static ConfigurationPoint getSiblingConfigurationPoint(String configurationPointName, ConfigurationPoint cp) {
        assertNotNull(configurationPointName, "configurationPointName");
        assertNotNull(cp, "configurationPoint");

        ConfigurationPoint siblingCp = cp.getConfigurationPoints().getConfigurationPointByName(configurationPointName);

        assertNotNull(siblingCp, "could not find configuration point of name: %s", configurationPointName);

        return siblingCp;
    }

    /**
     * ����<code>ManagedList</code>������type safety���档
     */
    @SuppressWarnings("unchecked")
    public static List<Object> createManagedList(Element element, ParserContext parserContext) {
        ManagedList list = new ManagedList();

        list.setSource(parserContext.getReaderContext().extractSource(element));

        return list;
    }

    /**
     * ����<code>ManagedMap</code>������type safety���档
     */
    @SuppressWarnings("unchecked")
    public static Map<Object, Object> createManagedMap(Element element, ParserContext parserContext) {
        ManagedMap map = new ManagedMap();

        map.setSource(parserContext.getReaderContext().extractSource(element));

        return map;
    }

    /**
     * ����<code>ManagedSet</code>������type safety���档
     */
    @SuppressWarnings("unchecked")
    public static Set<Object> createManagedSet(Element element, ParserContext parserContext) {
        ManagedSet set = new ManagedSet();

        set.setSource(parserContext.getReaderContext().extractSource(element));

        return set;
    }

    /**
     * ����element��ֵ����properties��
     */
    public static void subElementsToProperties(Element element, BeanDefinitionBuilder builder) {
        subElementsToProperties(element, null, builder, null);
    }

    /**
     * ����element��ֵ����properties��
     */
    public static void subElementsToProperties(Element element, BeanDefinitionBuilder builder, ElementSelector selector) {
        subElementsToProperties(element, null, builder, selector);
    }

    /**
     * ����element��ֵ����properties��
     */
    public static void subElementsToProperties(Element element, String propertyPrefix, BeanDefinitionBuilder builder,
                                               ElementSelector selector) {
        for (Element subElement : subElements(element, selector)) {
            elementToProperty(subElement, propertyPrefix, builder);
        }
    }

    /**
     * ��һ��element��ֵ����property��
     */
    public static void elementToProperty(Element element, BeanDefinitionBuilder builder) {
        elementToProperty(element, null, builder);
    }

    /**
     * ��һ��element��ֵ����property��
     */
    public static void elementToProperty(Element element, String propertyPrefix, BeanDefinitionBuilder builder) {
        String propName = element.getLocalName();

        if (!isEmpty(propertyPrefix)) {
            propName = propertyPrefix + propName;
        }

        builder.addPropertyValue(propName, trimToNull(element.getTextContent()));
    }

    /**
     * ��attribute��ֵ����properties��
     */
    public static void attributesToProperties(Element element, BeanDefinitionBuilder builder, String... attrNames) {
        attributesToProperties(element, null, builder, attrNames);
    }

    /**
     * ��attribute��ֵ����properties��
     */
    public static void attributesToProperties(Element element, String propertyPrefix, BeanDefinitionBuilder builder,
                                              String... attrNames) {
        NamedNodeMap attrs = element.getAttributes();
        Set<String> attrNameSet = isEmptyArray(attrNames) ? null : createHashSet(attrNames);

        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr) attrs.item(i);

            if (attrNameSet == null || attrNameSet.contains(attr.getNodeName())) {
                attributeToProperty(attr, propertyPrefix, builder);
            }
        }
    }

    /**
     * ��һ��attribute��ֵ����properties��
     */
    public static void attributeToProperty(Attr attr, String propertyPrefix, BeanDefinitionBuilder builder) {
        String propName = attr.getNodeName();

        if (!isEmpty(propertyPrefix)) {
            propName = propertyPrefix + propName;
        }

        builder.addPropertyValue(propName, trimToNull(attr.getNodeValue()));
    }

    /**
     * ����beans:bean��attributes��
     */
    public static void parseBeanDefinitionAttributes(Element element, ParserContext parserContext,
                                                     BeanDefinitionBuilder builder) {
        parserContext.getDelegate().parseBeanDefinitionAttributes(element, null, null, builder.getRawBeanDefinition());
    }

    /**
     * ��element����ָ��configuration point��bean�����element�����ڸ�configuration
     * point�����ֿռ䣬�򷵻�<code>null</code>��
     */
    public static BeanDefinitionHolder parseConfigurationPointBean(Element element, ConfigurationPoint cp,
                                                                   ParserContext parserContext,
                                                                   BeanDefinitionBuilder containingBeanBuilder) {
        assertNotNull(cp, "configurationPoint");

        BeanDefinitionParserDelegate delegate = parserContext.getDelegate();
        boolean isInnerBean = containingBeanBuilder != null;
        BeanDefinition containingBean = isInnerBean ? containingBeanBuilder.getRawBeanDefinition() : null;
        ElementSelector customSelector = ns(cp.getNamespaceUri());

        // ����custom element��
        if (customSelector.accept(element)) {
            AbstractBeanDefinition bean = (AbstractBeanDefinition) delegate.parseCustomElement(element, containingBean);
            String beanName = trimToNull(element.getAttribute("id"));

            if (beanName == null) {
                beanName = BeanDefinitionReaderUtils.generateBeanName(bean, parserContext.getRegistry(), isInnerBean);
            }

            return new BeanDefinitionHolder(bean, beanName);
        }

        return null;
    }

    /**
     * ����bean element��
     */
    public static Object parseBean(Element element, ParserContext parserContext,
                                   BeanDefinitionBuilder containingBeanBuilder) {
        return parseBean(element, parserContext,
                containingBeanBuilder == null ? null : containingBeanBuilder.getRawBeanDefinition());
    }

    /**
     * ����bean element��
     */
    public static Object parseBean(Element element, ParserContext parserContext, BeanDefinition containingBean) {
        BeanDefinitionParserDelegate delegate = parserContext.getDelegate();
        String refName = trimToNull(element.getAttribute("ref"));

        // �����ref
        if (refName != null) {
            NamedBeanReference ref = new NamedBeanReference(refName, element.getAttribute("id"));
            ref.setSource(parserContext.extractSource(element));

            return ref;
        }

        // �����bean
        else {
            BeanDefinitionHolder beanHolder = delegate.parseBeanDefinitionElement(element, containingBean);

            if (beanHolder != null) {
                beanHolder = delegate.decorateBeanDefinitionIfRequired(element, beanHolder, containingBean);
            }

            return beanHolder;
        }
    }

    /**
     * ȡ�ö����beanName���粻֧�֣��򷵻�<code>null</code>��
     */
    public static String getBeanName(Object bean) {
        if (bean instanceof BeanDefinitionHolder) {
            return ((BeanDefinitionHolder) bean).getBeanName();
        } else if (bean instanceof NamedBeanReference) {
            return ((NamedBeanReference) bean).getId();
        } else if (bean instanceof BeanReference) {
            return ((BeanReference) bean).getBeanName();
        } else {
            return null;
        }
    }

    /**
     * ����baseName�������ظ���beanName��
     * <p>
     * �б��� {@link BeanDefinitionReaderUtils.generateBeanName()}
     * ����������ʹ��ָ����baseName��������ʹ��������ΪbaseName��
     * </p>
     */
    public static String generateBeanName(String baseName, BeanDefinitionRegistry registry) {
        return generateBeanName(baseName, registry, null, false);
    }

    /**
     * ����baseName�������ظ���beanName��
     * <p>
     * �б��� {@link BeanDefinitionReaderUtils.generateBeanName()}
     * ����������ʹ��ָ����baseName��������ʹ��������ΪbaseName��
     * </p>
     * <p>
     * �����innerBean������Ҫ�ṩbean definition������id��
     * </p>
     */
    public static String generateBeanName(String baseName, BeanDefinitionRegistry registry, BeanDefinition definition,
                                          boolean isInnerBean) {
        baseName = assertNotNull(trimToNull(baseName), "baseName");
        String name;

        if (isInnerBean) {
            name = baseName + BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR
                    + ObjectUtils.getIdentityHexString(definition);
        } else {
            name = baseName;

            for (int i = 0; registry.containsBeanDefinition(name); i++) {
                name = baseName + BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR + i;
            }
        }

        return name;
    }

    /**
     * ���һ��constructor������
     */
    public static void addConstructorArg(BeanDefinitionBuilder builder, boolean required, Class<?> argType) {
        builder.addConstructorArgValue(createConstructorArg(builder.getRawBeanDefinition().getBeanClass(), required,
                argType));
    }

    /**
     * ���һ��constructor������
     * <p>
     * ����δָ��argTypes����ֻ����һ��constructor��
     * </p>
     */
    public static void addConstructorArg(BeanDefinitionBuilder builder, boolean required, int argIndex,
                                         Class<?>... argTypes) {
        builder.addConstructorArgValue(createConstructorArg(builder.getRawBeanDefinition().getBeanClass(), required,
                argIndex, argTypes));
    }

    /**
     * �������ṩһ��bean definition���ɱ�<code>BeanDefinitionParser</code>
     * ���ã�����ע���ѡ��constructor arg����֧��resolvableDependency�����Լ�optionalѡ�
     */
    public static BeanDefinition createConstructorArg(Class<?> beanType, boolean required, Class<?> argType) {
        return createConstructorArg(beanType, required, 0, argType);
    }

    /**
     * Spring������ע�뷽����
     * <ul>
     * <li>ͨ����ȷ��bean definition������ע�����ȱ�����޷�ע��
     * <code>ConfigurableListableBeanFactory.registerResolvableDependency()</code>
     * ��ע��Ķ�����<code>HttpServletRequest</code>��</li>
     * <li>ͨ��autowire
     * byConstructor��ע����󣬿���ע�����resolvableDependency���ڵĶ���ȱ�����޷���ע�������ɡ�optional��
     * ���������󲻴��ڣ��򱨴�</li>
     * <li>ͨ��autowire
     * byName/byType��ע������൱��optionalע�룬ȱ���ǲ���ע�����resolvableDependency���ڵĶ���
     * ���һ��Զ�������property����ע�룬�޷���ȷ���ƣ�������ɲ�ȷ�������</li>
     * <li>ͨ��<code>@Autowired</code>
     * annotation��ע����󣬿���ע�����resolvableDependency���ڵĶ���
     * ��Ҳ��������optionalѡ�ȱ�����������ԣ������޸�bean class�Ĵ��롣Springext��ͼ��ע��Ķ������޶���
     * <code>BeanDefinitionParser</code>�ķ�Χ�У����ʹ��annotation������õķ�����</li>
     * </ul>
     * <p>
     * �������ṩһ��bean definition���ɱ�<code>BeanDefinitionParser</code>
     * ���ã�����ע�뵽��constructor arg����֧��resolvableDependency�����Լ�optionalѡ�
     * </p>
     */
    public static BeanDefinition createConstructorArg(Class<?> beanType, boolean required, int argIndex,
                                                      Class<?>... argTypes) {
        assertNotNull(beanType, "beanType");
        argTypes = defaultIfNull(argTypes, EMPTY_CLASS_ARRAY);

        // ����ȡ��constructor
        Constructor<?> constructor;

        if (isEmptyArray(argTypes)) {
            Constructor<?>[] constructors = beanType.getConstructors();

            assertTrue(constructors.length == 1, "%d constructors found, please specify argTypes", constructors.length);
            constructor = constructors[0];
            argTypes = constructor.getParameterTypes();
        } else {
            try {
                constructor = beanType.getConstructor(argTypes);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Could not find constructor", e);
            }
        }

        assertTrue(argIndex >= 0 && argIndex < argTypes.length, "argIndex is out of bound: %d", argIndex);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConstructorArg.class);

        builder.addConstructorArgValue(constructor);
        builder.addConstructorArgValue(argTypes[argIndex]);
        builder.addConstructorArgValue(argIndex);
        builder.addConstructorArgValue(required);

        return builder.getBeanDefinition();
    }

    /**
     * ���һ��bean���ã�֧��optionalѡ�
     */
    public static void addPropertyRef(BeanDefinitionBuilder builder, String propertyName, String beanName,
                                      Class<?> beanType, boolean required) {
        if (required) {
            builder.addPropertyReference(propertyName, beanName);
        } else {
            builder.addPropertyValue(propertyName, createOptionalPropertyRef(beanName, beanType));
        }
    }

    /**
     * �������ṩһ��bean definition���ɱ�<code>BeanDefinitionParser</code>
     * ���ã�����ע�뵽��property arg����֧��resolvableDependency���󣬵�֧��optionalע�롣
     */
    public static BeanDefinition createOptionalPropertyRef(String beanName, Class<?> beanType) {
        beanName = assertNotNull(trimToNull(beanName), "beanName");
        assertNotNull(beanType, "beanType");

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(OptionalPropertyRef.class);

        builder.addConstructorArgValue(beanName);
        builder.addConstructorArgValue(beanType);

        return builder.getBeanDefinition();
    }

    private final static DefaultInterceptor defaultInterceptor = new DefaultInterceptor();
    private final static ProxiedFilter proxiedFilter = new ProxiedFilter();

    /**
     * ����ָ��interface��proxy����proxy�ķ���������ʱ��proxy�����factory��ȡ��ʵ�ʶ���
     * Ȼ�󽫵���delegate��ʵ�ʶ�����ִ�С�
     */
    public static <T> T createProxy(final Class<T> intfs, final ObjectFactory factory) {
        return createProxy(intfs, null, factory);
    }

    /**
     * ����ָ��interface��proxy����proxy�ķ���������ʱ��proxy�����factory��ȡ��ʵ�ʶ���
     * Ȼ�󽫵���delegate��ʵ�ʶ�����ִ�С�
     */
    public static <T> T createProxy(Class<T> intfs, ClassLoader classLoader, final ObjectFactory factory) {
        assertNotNull(intfs, "no interface");
        assertNotNull(factory, "no ObjectFactory");

        try {
            intfs.getMethod("getObject");
            throw new IllegalArgumentException("Method name conflict: interface " + intfs.getName() + ".getObject()");
        } catch (NoSuchMethodException e) {
        }

        Enhancer generator = new Enhancer();

        generator.setClassLoader(classLoader);
        generator.setSuperclass(AbstractProxy.class);
        generator.setInterfaces(new Class<?>[] { intfs });

        generator.setCallbacks(new Callback[] { defaultInterceptor, new ProxiedInterceptor(factory) });
        generator.setCallbackFilter(proxiedFilter);
        generator.setNamingPolicy(new ProxiedNamingPolicy(intfs));

        return intfs.cast(generator.create(new Class<?>[] { Class.class, ObjectFactory.class }, new Object[] { intfs,
                factory }));
    }

    /**
     * ȷ�����õ��Ķ���Ϊ�������
     * <p>
     * <strong>ע��</strong>���˷������Խ���<code>null</code>ֵ�������ȷ���ǿգ�����
     * {@link Assert.assertNotNull}����ʹ�á�
     * </p>
     */
    public static <T> T assertProxy(T object) {
        if (object != null) {
            assertTrue(object instanceof ObjectFactory,
                    "expects a proxy delegating to a real object, but got an object of type %s", object.getClass()
                            .getName());
        }

        return object;
    }

    /**
     * ȡ��proxy��ָ�����ʵ����
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxyTarget(T object) {
        if (object instanceof ObjectFactory) {
            try {
                return (T) ((ObjectFactory) object).getObject();
            } catch (Exception e) {
                log.warn("Could not get proxied object from ObjectFactory: {} {}", e.getClass().getSimpleName(),
                        e.getMessage());
                return null;
            }
        } else {
            return object;
        }
    }

    public static class ConstructorArg implements FactoryBean, BeanFactoryAware {
        private final Constructor<?> constructor;
        private final Class<?> argType;
        private final int argIndex;
        private final boolean required;
        private ConfigurableListableBeanFactory context;

        public ConstructorArg(Constructor<?> constructor, Class<?> argType, int argIndex, boolean required) {
            this.constructor = constructor;
            this.argType = argType;
            this.argIndex = argIndex;
            this.required = required;
        }

        public void setBeanFactory(BeanFactory context) throws BeansException {
            BeanFactory factory = null;

            if (context instanceof ApplicationContext) {
                factory = ((ApplicationContext) context).getAutowireCapableBeanFactory();
            } else {
                factory = context;
            }

            if (factory instanceof ConfigurableListableBeanFactory) {
                this.context = (ConfigurableListableBeanFactory) factory;
            }
        }

        public Class<?> getObjectType() {
            return argType;
        }

        public boolean isSingleton() {
            return false;
        }

        public Object getObject() {
            if (required && context == null) {
                throw new IllegalArgumentException("could not get object of " + argType.getName()
                        + ": no Application Context");
            }

            Object object = null;

            if (context != null) {
                DependencyDescriptor dd = new DependencyDescriptor(new MethodParameter(constructor, argIndex), required);
                object = context.resolveDependency(dd, null);
            }

            return object;
        }
    }

    public static class OptionalPropertyRef implements FactoryBean, BeanFactoryAware {
        private final String beanName;
        private final Class<?> beanType;
        private BeanFactory context;

        public OptionalPropertyRef(String beanName, Class<?> beanType) {
            this.beanName = beanName;
            this.beanType = beanType;
        }

        public void setBeanFactory(BeanFactory context) throws BeansException {
            this.context = context;
        }

        public Class<?> getObjectType() {
            return beanType;
        }

        public boolean isSingleton() {
            return false;
        }

        public Object getObject() {
            Object object = null;

            if (context != null) {
                try {
                    object = context.getBean(beanName);
                } catch (NoSuchBeanDefinitionException e) {
                    // ignore
                }
            }

            return object;
        }
    }

    /**
     * Я��id��bean ref��
     */
    private static class NamedBeanReference extends RuntimeBeanReference {
        private final String id;

        public NamedBeanReference(String beanName, String id) {
            this(beanName, id, false);
        }

        public NamedBeanReference(String beanName, String id, boolean toParent) {
            super(beanName, toParent);
            this.id = trimToNull(id);
        }

        public String getId() {
            return id == null ? getBeanName() : id;
        }

        @Override
        public String toString() {
            return '<' + (id == null ? "" : id + "=") + getBeanName() + '>';
        }
    }

    /**
     * ������createProxy()������Proxy�Ļ��ࡣ
     */
    public static class AbstractProxy implements ObjectFactory {
        private final Class<?> intfs;
        private final ObjectFactory factory;

        public AbstractProxy(Class<?> intfs, ObjectFactory factory) {
            this.intfs = assertNotNull(intfs);
            this.factory = assertNotNull(factory, "objectFactory");
        }

        public Object getObject() throws BeansException {
            return factory.getObject();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;

            result = prime * result + (factory == null ? 0 : factory.hashCode());

            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (getClass() != obj.getClass()) {
                return false;
            }

            AbstractProxy other = (AbstractProxy) obj;

            if (factory == null) {
                if (other.factory != null) {
                    return false;
                }
            } else if (!factory.equals(other.factory)) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            try {
                return factory.getObject().toString();
            } catch (Exception e) {
                return String.format("%s[%s: %s]", intfs.getSimpleName(), e.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    /**
     * ������createProxy()������������ת����proxy������
     */
    private static final class DefaultInterceptor implements MethodInterceptor {
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return proxy.invokeSuper(obj, args);
        }
    }

    /**
     * ������createProxy()������������ί�и�ObjectFactory�����صĶ���
     */
    private static final class ProxiedInterceptor implements MethodInterceptor {
        private final ObjectFactory factory;

        private ProxiedInterceptor(ObjectFactory factory) {
            this.factory = factory;
        }

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return proxy.invoke(factory.getObject(), args);
        }
    }

    /**
     * ������createProxy()����������equals��hashCode��toString��ObjectFactory.getObject������
     * ִ��defaultInterceptor������ִ��proxiedInterceptor��
     */
    private static final class ProxiedFilter implements CallbackFilter {
        public int accept(Method method) {
            if (isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method)
                    || isObjectFactoryMethod(method)) {
                return 0; // invoke super
            } else {
                return 1; // invoke proxied object
            }
        }

        private boolean isObjectFactoryMethod(Method method) {
            return method != null && method.getName().equals("getObject") && method.getParameterTypes().length == 0;
        }
    }

    /**
     * ������createProxy()������������interface name��Ϊǰ׺��proxy class name��
     */
    private static final class ProxiedNamingPolicy extends DefaultNamingPolicy {
        private final Class<?> intfs;

        private ProxiedNamingPolicy(Class<?> intfs) {
            this.intfs = intfs;
        }

        @Override
        public String getClassName(String prefix, String source, Object key, Predicate names) {
            if (AbstractProxy.class.getName().equals(prefix)) {
                prefix = intfs.getName();
            }

            return super.getClassName(prefix, source, key, names);
        }
    }
}
