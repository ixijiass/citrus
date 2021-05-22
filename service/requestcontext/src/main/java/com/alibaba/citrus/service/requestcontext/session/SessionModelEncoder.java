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
package com.alibaba.citrus.service.requestcontext.session;

/**
 * ��session modelת��Ϊstore�пɴ洢�Ķ��󣬻��߽�store��ȡ�õĶ���ת����session model��
 * 
 * @author Michael Zhou
 */
public interface SessionModelEncoder {
    /**
     * ��session modelת����store�пɴ洢�Ķ���
     */
    Object encode(SessionModel model);

    /**
     * ��store��ȡ�õ�����ת����session model��
     * <p>
     * �������<code>null</code>�������ݸ�ʽ��֧�֡���ܽ��᳢���������<code>SessionModelEncoder</code>
     * �����롣
     * </p>
     */
    SessionModel decode(Object data, SessionModel.Factory factory);
}
