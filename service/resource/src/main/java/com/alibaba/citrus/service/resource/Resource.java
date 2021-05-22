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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * ����һ����Դ��
 * <p>
 * һ����Դ���Ա���ʾ��<code>URL</code>��<code>File</code>����<code>InputStream</code>��
 * ��Ҫע����ǣ������������͵���Դ������ͬʱȡ������������ʽ��
 * </p>
 * 
 * @author Michael Zhou
 */
public interface Resource {
    /**
     * ȡ����Դ��<code>URL</code>�������Դ���ܱ���ʾ��<code>URL</code>���򷵻�<code>null</code>��
     */
    URL getURL();

    /**
     * ȡ����Դ��<code>File</code>�������Դ���ܱ���ʾ��<code>File</code>���򷵻�<code>null</code>��
     */
    File getFile();

    /**
     * ȡ����Դ��<code>InputStream</code>�������Դ���ܱ���ʾ��<code>InputStream</code>���򷵻�
     * <code>null</code>��
     */
    InputStream getInputStream() throws IOException;

    /**
     * �ж���Դ�Ƿ���ڡ�
     * 
     * @return ������ڣ��򷵻�<code>true</code>
     */
    boolean exists();

    /**
     * ȡ����Դ����޸�ʱ�䣨ms���������֧�֣��򷵻�<code>0</code>��
     */
    long lastModified();

    /**
     * ��ԴӦ��ʵ�ָ÷�����
     */
    int hashCode();

    /**
     * ��ԴӦ��ʵ�ָ÷�����
     */
    boolean equals(Object other);
}
