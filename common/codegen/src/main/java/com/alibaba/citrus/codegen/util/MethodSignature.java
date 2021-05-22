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
package com.alibaba.citrus.codegen.util;

import static com.alibaba.citrus.asm.Type.*;
import static com.alibaba.citrus.codegen.util.CodegenConstant.*;
import static com.alibaba.citrus.codegen.util.TypeUtil.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.alibaba.citrus.asm.Type;

/**
 * ����һ�����캯���򷽷���ǩ�������������ͬ�ķ�����
 * <p>
 * �ȽϷ���ǩ��ʱ��ֻ�з��������ͣ����캯������ͨ���������������Ͳ��������˱Ƚϡ�
 * </p>
 * 
 * @author Michael Zhou
 */
public class MethodSignature extends com.alibaba.citrus.asm.commons.Method {
    public MethodSignature(String name, Class<?> returnType, Class<?>... parameterTypes) {
        super(name, getType(returnType), getTypes(parameterTypes));
    }

    public MethodSignature(Method method) {
        super(method.getName(), Type.getMethodDescriptor(method));
    }

    public MethodSignature(Constructor<?> constructor) {
        super(CONSTRUCTOR_NAME, Type.getConstructorDescriptor(constructor));
    }
}
