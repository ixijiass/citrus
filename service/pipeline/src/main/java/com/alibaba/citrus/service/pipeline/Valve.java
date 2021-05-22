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
 * ����pipeline�е�һ�������š���
 * <p>
 * ��ͬ��ʵ�������ˮ���еķ��ţ������Կ��ƺ͸ı�Һ�������<code>Valve</code> Ҳ���Կ���pipeline�к���valves��ִ�С�
 * <code>Valve</code>���Ծ����Ƿ����ִ�к�����valves�������ж�����pipeline��ִ�С�
 * </p>
 * 
 * @author Michael Zhou
 */
public interface Valve {
    /**
     * ִ��valve��
     * <ul>
     * <li>Valveͨ������<code>PipelineContext.invokeNext()</code>��������������valves��</li>
     * <li>Valve���Ե���<code>PipelineContext.breakPipeline()</code>
     * ������������ǰ������n���pipeline��</li>
     * <li>Valve���Ե���<code>Pipeline.invokeSub(pipelineContext)</code>������һ�������̡�</li>
     * <li>Valve���Ե���<code>PipelineHandle.reinvoke()</code>���ظ�ִ�������̡�</li>
     * <li>����valveֱ�ӷ��ض�������<code>PipelineContext.invokeNext()</code>
     * ������������������ǰpipeline��ִ�С�</li>
     * </ul>
     */
    void invoke(PipelineContext pipelineContext) throws Exception;
}
