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
package com.alibaba.citrus.springext.support.context;

import static org.springframework.context.annotation.AnnotationConfigUtils.*;

import java.io.IOException;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.alibaba.citrus.springext.ResourceLoadingExtendable;
import com.alibaba.citrus.springext.ResourceLoadingExtender;
import com.alibaba.citrus.springext.support.resolver.XmlBeanDefinitionReaderProcessor;

/**
 * ��XML�����ļ���װ���<code>ApplicationContext</code>�Ļ��࣬������
 * {@link org.springframework.context.support.AbstractXmlApplicationContext}
 * ���������������ԣ�
 * <ul>
 * <li>֧��<code>ConfigurationPoint</code>���ơ�</li>
 * <li>��չ��resource loading���ơ�����<code>ResourceLoadingExtender</code>
 * �����ã���ʹ������װ����Դ������ʹ��Ĭ�ϵ�װ������</li>
 * <li>Ĭ�ϴ�annotation config���൱��<code>&lt;context:annotation-config/&gt;</code>��
 * </li>
 * <li>����<code>parentResolvableDependenciesAccessible==true</code>����֧�ִ�parent
 * context��ȡ��Ԥ������<code>resolvableDependencies</code>�еĶ���Ĭ��Ϊ<code>true</code>��</li>
 * <li>Ĭ��<code>allowBeanDefinitionOverriding==false</code>��</li>
 * </ul>
 * <p>
 * �������з�WEBӦ�õ�application context�Ӹû������������ڼ򵥵����Σ��絥Ԫ���ԣ�ֱ�Ӵ�����
 * {@link XmlApplicationContext}����ʵ����
 * </p>
 * 
 * @author Michael Zhou
 */
public abstract class AbstractXmlApplicationContext extends
        org.springframework.context.support.AbstractXmlApplicationContext implements ResourceLoadingExtendable {
    private ResourceLoadingExtender resourceLoadingExtender;
    private boolean parentResolvableDependenciesAccessible = true;

    public AbstractXmlApplicationContext() {
        super();
        setAllowBeanDefinitionOverriding(false);
    }

    public AbstractXmlApplicationContext(ApplicationContext parent) {
        super(parent);
        setAllowBeanDefinitionOverriding(false);
    }

    /**
     * �Ƿ�ɷ��ʵ�parent context�е�resolvableDependencies�� Ĭ���ǿɷ��ʡ�
     */
    public boolean isParentResolvableDependenciesAccessible() {
        return parentResolvableDependenciesAccessible;
    }

    public void setParentResolvableDependenciesAccessible(boolean parentResolvableDependenciesAccessible) {
        this.parentResolvableDependenciesAccessible = parentResolvableDependenciesAccessible;
    }

    public void setResourceLoadingExtender(ResourceLoadingExtender resourceLoadingExtender) {
        if (this.resourceLoadingExtender != null) {
            getApplicationListeners().remove(this.resourceLoadingExtender);
        }

        this.resourceLoadingExtender = resourceLoadingExtender;

        if (resourceLoadingExtender instanceof ApplicationListener) {
            addApplicationListener((ApplicationListener) resourceLoadingExtender);
        }
    }

    @Override
    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
        new XmlBeanDefinitionReaderProcessor(beanDefinitionReader).addConfigurationPointsSupport();
    }

    /**
     * ��annotationע�롣
     */
    @Override
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        super.customizeBeanFactory(beanFactory);
        registerAnnotationConfigProcessors(beanFactory, null);
    }

    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        if (isParentResolvableDependenciesAccessible()) {
            return new InheritableListableBeanFactory(getInternalParentBeanFactory());
        } else {
            return super.createBeanFactory();
        }
    }

    /**
     * ��չ<code>ResourceLoader</code>���ƣ�ʵ���Զ������Դװ�ء�
     */
    @Override
    protected Resource getResourceByPath(String path) {
        Resource resource = null;

        if (resourceLoadingExtender != null) {
            resource = resourceLoadingExtender.getResourceByPath(path);
        }

        if (resource == null) {
            resource = super.getResourceByPath(path);
        }

        return resource;
    }

    /**
     * ��չ<code>ResourcePatternResolver</code>���ƣ�ʵ���Զ������Դװ�ء�
     */
    @Override
    protected ResourcePatternResolver getResourcePatternResolver() {
        final ResourcePatternResolver defaultResolver = super.getResourcePatternResolver();

        return new ResourcePatternResolver() {
            public Resource[] getResources(String locationPattern) throws IOException {
                ResourcePatternResolver resolver = null;

                if (resourceLoadingExtender != null) {
                    resolver = resourceLoadingExtender.getResourcePatternResolver();
                }

                if (resolver == null) {
                    resolver = defaultResolver;
                }

                return resolver.getResources(locationPattern);
            }

            public ClassLoader getClassLoader() {
                return defaultResolver.getClassLoader();
            }

            public Resource getResource(String location) {
                return defaultResolver.getResource(location);
            }
        };
    }
}
