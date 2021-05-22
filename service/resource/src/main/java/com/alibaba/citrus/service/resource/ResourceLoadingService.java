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
package com.alibaba.citrus.service.resource;

import static java.util.Collections.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

/**
 * װ����Դ��service������ȡ��ָ�����Ƶ���Դ��URL���ļ���File������������InputStream����
 * <p>
 * ��Ҫע����ǣ������������͵���Դ������ͬʱȡ��URL��File��InputStream�ġ�
 * </p>
 * 
 * @author Michael Zhou
 */
public interface ResourceLoadingService {
    /**
     * ����ѡ�<code>FOR_CREATE</code>��
     */
    Set<ResourceLoadingOption> FOR_CREATE = unmodifiableSet(EnumSet.of(ResourceLoadingOption.FOR_CREATE));

    /**
     * ȡ��parentװ�ط���
     */
    ResourceLoadingService getParent();

    /**
     * ����ָ�����Ƶ���Դ��
     */
    URL getResourceAsURL(String resourceName) throws ResourceNotFoundException;

    /**
     * ����ָ�����Ƶ���Դ��
     */
    File getResourceAsFile(String resourceName) throws ResourceNotFoundException;

    /**
     * ����ָ�����Ƶ���Դ��
     */
    File getResourceAsFile(String resourceName, Set<ResourceLoadingOption> options) throws ResourceNotFoundException;

    /**
     * ����ָ�����Ƶ���Դ��
     */
    InputStream getResourceAsStream(String resourceName) throws ResourceNotFoundException, IOException;

    /**
     * ����ָ�����Ƶ���Դ��
     */
    Resource getResource(String resourceName) throws ResourceNotFoundException;

    /**
     * ����ָ�����Ƶ���Դ��
     */
    Resource getResource(String resourceName, Set<ResourceLoadingOption> options) throws ResourceNotFoundException;

    /**
     * �ж�ָ�����Ƶ���Դ�Ƿ���ڡ�������ڣ��򷵻�<code>true</code>��
     */
    boolean exists(String resourceName);

    /**
     * ���ٲ���ȡ������Դ��·����
     * <p>
     * ��ʹ��Դ�޷��ҵ���<code>trace</code>����Ҳ�᷵���������Թ�������·�����������׳��쳣��
     * </p>
     * <p>
     * �÷�����Ҫ���ڵ��ԺͲ��Է���
     * </p>
     */
    ResourceTrace trace(String resourceName);

    /**
     * ���ٲ���ȡ������Դ��·����
     * <p>
     * ��ʹ��Դ�޷��ҵ���<code>trace</code>����Ҳ�᷵���������Թ�������·�����������׳��쳣��
     * </p>
     * <p>
     * �÷�����Ҫ���ڵ��ԺͲ��Է���
     * </p>
     */
    ResourceTrace trace(String resourceName, Set<ResourceLoadingOption> options);

    /**
     * ���г�ָ����Դ����Ŀ¼���ļ�����Ŀ¼����<code>/</code>��β�����Ŀ¼�����ڣ��򷵻�<code>null</code>��
     */
    String[] list(String resourceName) throws ResourceNotFoundException;

    /**
     * ���г�ָ����Դ����Ŀ¼���ļ�����Ŀ¼����<code>/</code>��β�����Ŀ¼�����ڣ��򷵻�<code>null</code>��
     */
    String[] list(String resourceName, Set<ResourceLoadingOption> options) throws ResourceNotFoundException;

    /**
     * ���г�ָ����Դ����Ŀ¼���ļ���Դ�����Ŀ¼�����ڣ��򷵻�<code>null</code>��
     */
    Resource[] listResources(String resourceName) throws ResourceNotFoundException;

    /**
     * ���г�ָ����Դ����Ŀ¼���ļ���Դ�����Ŀ¼�����ڣ��򷵻�<code>null</code>��
     */
    Resource[] listResources(String resourceName, Set<ResourceLoadingOption> options) throws ResourceNotFoundException;

    /**
     * ȡ�����е�patterns���ơ�
     */
    String[] getPatterns(boolean includeParent);
}
