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
package com.alibaba.citrus.turbine;

import com.alibaba.citrus.service.uribroker.uri.URIBroker;

/**
 * ������������ڲ����ⲿ�ض���Ľӿڡ�
 * 
 * @author Michael Zhou
 */
public interface Navigator {
    /**
     * �����ڲ��ض���ָ��һ��target���ơ�
     */
    Parameters forwardTo(String target);

    /**
     * �����ⲿ�ض���ָ��һ��uri broker�����ơ�
     */
    RedirectParameters redirectTo(String uriName);

    /**
     * �����ⲿ�ض���ָ��һ��������URL location��
     */
    void redirectToLocation(String location);

    /**
     * �ض���Ĳ�����
     */
    interface Parameters {
        Parameters withParameter(String name, String... values);
    }

    /**
     * �ⲿ�ض���Ĳ�����
     */
    interface RedirectParameters extends Parameters {
        RedirectParameters withTarget(String target);

        URIBroker uri();
    }
}
