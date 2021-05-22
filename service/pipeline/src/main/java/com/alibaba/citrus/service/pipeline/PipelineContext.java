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
package com.alibaba.citrus.service.pipeline;

/**
 * <code>PipelineContext</code>
 * ����pipeline�ṩ��valve��һ�������Ķ����������˵�ǰpipeline��ִ��״̬��������pipeline��ִ�в��衣
 * 
 * @author Michael Zhou
 */
public interface PipelineContext extends PipelineStates {
    /**
     * ִ��pipeline����һ��valve��
     * 
     * @throws IllegalStateException ����÷�������ε��á�
     */
    void invokeNext() throws IllegalStateException, PipelineException;

    /**
     * �жϲ�����pipeline��ִ�С�
     * 
     * @param levels �жϲ�����ָ��������pipeline��<code>0</code>������жϵ�ǰpipeline��ִ�С�
     */
    void breakPipeline(int levels);

    /**
     * �жϲ�����pipeline��ִ�С�
     * 
     * @param label �жϲ�����ָ��label��pipeline
     */
    void breakPipeline(String label);
}
