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
package com.alibaba.citrus.service.resource.loader;

import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.FileUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.Set;

import com.alibaba.citrus.service.resource.Resource;
import com.alibaba.citrus.service.resource.ResourceLister;
import com.alibaba.citrus.service.resource.ResourceListerContext;
import com.alibaba.citrus.service.resource.ResourceLoaderContext;
import com.alibaba.citrus.service.resource.ResourceLoadingOption;
import com.alibaba.citrus.service.resource.ResourceLoadingService;
import com.alibaba.citrus.service.resource.ResourceMatchResult;

/**
 * һ�������resource loader��������Ĭ�ϵķ���������Դ��
 * 
 * @author Michael Zhou
 */
public class SuperResourceLoader implements ResourceLister {
    private String newResourceName;

    public void setName(String resourceName) {
        this.newResourceName = trimToNull(resourceName);
    }

    /**
     * ��ʼ��loader�����趨loader���ڵ�<code>ResourceLoadingService</code>��ʵ����
     */
    public void init(ResourceLoadingService resourceLoadingService) {
    }

    /**
     * ����ָ�����Ƶ���Դ��
     */
    public Resource getResource(ResourceLoaderContext context, Set<ResourceLoadingOption> options) {
        return context.getResource(getNewResourceName(context), options);
    }

    /**
     * ����Ŀ¼�б�
     */
    public String[] list(ResourceListerContext context, Set<ResourceLoadingOption> options) {
        return context.list(getNewResourceName(context), options);
    }

    private String getNewResourceName(ResourceMatchResult context) {
        String resourceName = EMPTY_STRING;

        if (newResourceName != null) {
            resourceName = newResourceName;
        }

        resourceName = normalizeAbsolutePath(context.substitute(resourceName));

        return resourceName;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(getClass().getSimpleName()).append("[");

        if (newResourceName != null) {
            buf.append(newResourceName);
        }

        buf.append("]");

        return buf.toString();
    }
}
