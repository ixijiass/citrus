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
package com.alibaba.citrus.service.resource.support;

import static com.alibaba.citrus.util.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.alibaba.citrus.service.resource.Resource;
import com.alibaba.citrus.util.internal.ToStringBuilder;

/**
 * ����һ��<code>byte[]</code>����Դ��
 * 
 * @author Michael Zhou
 */
public class ByteArrayResource implements Resource {
    private final byte[] bytes;
    private final int offset;
    private final int length;

    /**
     * ����һ��<code>ByteArrayResource</code>��
     */
    public ByteArrayResource(byte[] bytes) {
        this(assertNotNull(bytes, "bytes"), 0, bytes.length);
    }

    /**
     * ����һ��<code>ByteArrayResource</code>��
     */
    public ByteArrayResource(byte[] bytes, int offset, int length) {
        this.bytes = assertNotNull(bytes, "bytes");

        if (offset < 0 || offset + length > bytes.length || length < 0) {
            throw new IndexOutOfBoundsException();
        }

        this.offset = offset;
        this.length = length;
    }

    /**
     * ȡ����Դ��<code>URL</code>��
     */
    public URL getURL() {
        return null;
    }

    /**
     * ȡ����Դ��<code>File</code>��
     */
    public File getFile() {
        return null;
    }

    /**
     * ȡ����Դ��<code>InputStream</code>��
     */
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes, offset, length);
    }

    /**
     * �ж���Դ�Ƿ���ڡ�
     */
    public boolean exists() {
        return bytes != null;
    }

    /**
     * ȡ����Դ����޸�ʱ�䡣
     * 
     * @return ��Դ������޸�ʱ�䣬�����֧�֣��򷵻�<code>0</code>
     */
    public long lastModified() {
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + hashCode(bytes, offset, length);
        return result;
    }

    private static int hashCode(byte bytes[], int offset, int length) {
        int result = 1;

        for (int i = offset, end = offset + length; i < end; i++) {
            result = 31 * result + bytes[i];
        }

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

        ByteArrayResource other = (ByteArrayResource) obj;

        if (length != other.length) {
            return false;
        }

        for (int i = offset, j = other.offset, end = offset + length; i < end; i++, j++) {
            if (bytes[i] != other.bytes[j]) {
                return false;
            }
        }

        return true;
    }

    /**
     * ��resourceת�����ַ�����ʾ��
     */
    @Override
    public String toString() {
        ToStringBuilder buf = new ToStringBuilder();

        buf.append(getClass().getSimpleName());
        buf.appendArray(bytes, offset, 128); // ����ӡ128���ֽ�

        return buf.toString();
    }
}
