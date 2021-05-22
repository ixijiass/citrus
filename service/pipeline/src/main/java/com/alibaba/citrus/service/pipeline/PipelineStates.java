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
 * Pipeline�ĵ�ǰ״̬���Ǳ�<code>PipelineContext</code>��
 * <code>PipelineInvocationHandle</code>����Ľӿڡ�
 * 
 * @author Michael Zhou
 */
public interface PipelineStates {
    /**
     * ȡ�õ�ǰ����ִ�е�pipeline��Ƕ�ײ�Ρ�ע�⣬�ú����<code>1</code>��ʼ������
     */
    int level();

    /**
     * ȡ�õ�ǰ����ִ�е�valve�������š�ע�⣬�ú����<code>1</code>��ʼ������
     */
    int index();

    /**
     * ����label���������뵱ǰpipeline����Ĳ�����
     */
    int findLabel(String label);

    /**
     * ���pipeline���Ƿ��жϡ�
     */
    boolean isBroken();

    /**
     * ���pipeline���Ƿ���ִ����ɡ�
     */
    boolean isFinished();

    /**
     * ȡ�õ�ǰpipelineִ�е�״̬��
     * <p>
     * ����ȡ�����������ϲ��ң�ֱ���ҵ����ߵ��ﶥ�㡣
     * </p>
     */
    Object getAttribute(String key);

    /**
     * ���õ�ǰpipeline��״̬��
     * <p>
     * ���õ�ǰpipelineִ�е�״̬���Ḳ���ϲ�ͬ����״ֵ̬��Ȼ��ȴ����Ӱ����һ��ִ�е�״̬����ִ�з��ص���һ��ʱ�������еĸı䶼��������
     * </p>
     */
    void setAttribute(String key, Object value);
}
