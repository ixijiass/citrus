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
package com.alibaba.citrus.service.velocity.impl;

import static com.alibaba.citrus.util.StringUtil.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;

/**
 * Velocity <code>ResourceLoader</code>�ĳ���ʵ�֡�
 * 
 * @author Michael Zhou
 */
public abstract class AbstractResourceLoader extends org.apache.velocity.runtime.resource.loader.ResourceLoader {
    /**
     * ȡ����������
     */
    @Override
    public final InputStream getResourceStream(String templateName) throws ResourceNotFoundException {
        Resource resource = getResource(templateName);
        Exception exception = null;

        if (resource != null && resource.exists()) {
            try {
                return resource.getInputStream();
            } catch (IOException e) {
                exception = e;
            }
        }

        throw new ResourceNotFoundException(getLogID() + " Error: could not find template: " + templateName, exception);
    }

    /**
     * �ж���Դ�Ƿ񱻸ı䡣
     */
    @Override
    public final boolean isSourceModified(org.apache.velocity.runtime.resource.Resource templateResource) {
        Resource resource = getResource(templateResource.getName());

        // 1. ������Դû�ҵ��������Ǳ�ɾ���ˣ���ô��Ϊmodified==true��ģ�彫��������װ��ʱ����
        if (resource == null || !resource.exists()) {
            return true;
        }

        long lastModified;

        try {
            lastModified = resource.lastModified();
        } catch (IOException e) {
            lastModified = 0;
        }

        // 2. ������Դ�ҵ��ˣ����ǲ�֧��lastModified���ܣ�����Ϊmodified==false��ģ�岻������װ�ء�
        if (lastModified <= 0L) {
            return false;
        }

        // 3. ��Դ�ҵ�����֧��lastModified���ܣ���Ƚ�lastModified��
        return lastModified != templateResource.getLastModified();
    }

    /**
     * ȡ��������޸ĵ�ʱ�䡣
     */
    @Override
    public final long getLastModified(org.apache.velocity.runtime.resource.Resource templateResource) {
        Resource resource = getResource(templateResource.getName());

        if (resource != null && resource.exists()) {
            try {
                return resource.lastModified();
            } catch (IOException e) {
            }
        }

        return 0;
    }

    /**
     * ���ģ������
     */
    protected final String normalizeTemplateName(String templateName) {
        if (isEmpty(templateName)) {
            throw new ResourceNotFoundException("Need to specify a template name!");
        }

        if (templateName.startsWith("/")) {
            templateName = templateName.substring(1);
        }

        return templateName;
    }

    /**
     * ȡ����Դ��
     */
    protected abstract Resource getResource(String templateName);

    /**
     * �Ż��ж��߼�����������ȡ��resource stream��
     */
    @Override
    public boolean resourceExists(String resourceName) {
        Resource resource = getResource(resourceName);
        return resource != null && resource.exists();
    }

    /**
     * ȡ��������־��¼��ID��
     */
    protected abstract String getLogID();

    protected abstract String getDesc();

    @Override
    public String toString() {
        return getLogID() + "[" + getDesc() + "]";
    }
}
