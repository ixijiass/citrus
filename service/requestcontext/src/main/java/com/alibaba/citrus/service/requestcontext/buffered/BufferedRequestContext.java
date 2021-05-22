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
package com.alibaba.citrus.service.requestcontext.buffered;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.util.io.ByteArray;

/**
 * ��response.<code>getWriter()</code>��response.<code>getOutputStream()</code>
 * �����ص���������л��������
 * 
 * @author Michael Zhou
 */
public interface BufferedRequestContext extends RequestContext {
    /**
     * �����Ƿ�������Ϣ�������ڴ��С�
     * 
     * @return ����ǣ��򷵻�<code>true</code>
     */
    boolean isBuffering();

    /**
     * ����bufferģʽ��������ó�<code>true</code>����ʾ��������Ϣ�������ڴ��У�����ֱ�������ԭʼresponse�С�
     * <p>
     * �˷���������<code>getOutputStream</code>��<code>getWriter</code>����֮ǰִ�У������׳�
     * <code>IllegalStateException</code>��
     * </p>
     * 
     * @param buffering �Ƿ�buffer����
     * @throws IllegalStateException <code>getOutputStream</code>��
     *             <code>getWriter</code>�����Ѿ���ִ��
     */
    void setBuffering(boolean buffering);

    /**
     * �����µ�buffer�������ϵ�buffer��
     * 
     * @throws IllegalStateException �������bufferģʽ����<code>getWriter</code>��
     *             <code>getOutputStream</code>������δ������
     */
    void pushBuffer();

    /**
     * ���������buffer�������ջ��ֻ��һ��buffer���򵯳����ٴ���һ���µġ�
     * 
     * @return �����buffer����
     * @throws IllegalStateException �������bufferģʽ����<code>getWriter</code>
     *             �����������ã��� <code>getOutputStream</code>������δ������
     */
    ByteArray popByteBuffer();

    /**
     * ���������buffer�������ջ��ֻ��һ��buffer���򵯳����ٴ���һ���µġ�
     * 
     * @return �����buffer����
     * @throws IllegalStateException �������bufferģʽ����<code>getOutputStream</code>
     *             �����������ã���<code>getWriter</code>������δ������
     */
    String popCharBuffer();
}
