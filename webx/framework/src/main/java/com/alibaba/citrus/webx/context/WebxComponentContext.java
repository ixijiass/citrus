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
package com.alibaba.citrus.webx.context;

import static com.alibaba.citrus.util.Assert.*;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.alibaba.citrus.webx.WebxComponent;

/**
 * 和<code>WebxComponent</code>对应的context类。
 * 
 * @author Michael Zhou
 */
public class WebxComponentContext extends WebxApplicationContext {
    private final WebxComponent component;

    public WebxComponentContext(WebxComponent component) {
        this.component = assertNotNull(component, "component");
        setParent(component.getWebxComponents().getParentApplicationContext());
    }

    public WebxComponent getWebxComponent() {
        return component;
    }

    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.postProcessBeanFactory(beanFactory);
        beanFactory.registerResolvableDependency(WebxComponent.class, component);
    }
}
