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
package com.alibaba.citrus.codegen;

import static com.alibaba.citrus.asm.Opcodes.*;
import static com.alibaba.citrus.codegen.util.CodegenConstant.*;
import static com.alibaba.citrus.codegen.util.TypeUtil.*;
import static com.alibaba.citrus.util.Assert.*;

import com.alibaba.citrus.asm.MethodVisitor;
import com.alibaba.citrus.asm.Type;

/**
 * ��������һ��method�Ĺ��ߡ�
 * 
 * @author Michael Zhou
 */
public abstract class MethodBuilder {
    private final ClassBuilder cb;
    private final MethodVisitor mv;
    private final int access;
    private final String methodName;
    private final Type returnType;
    private final Type[] parameterTypes;
    private final Type[] exceptionTypes;
    private final String methodDesc;
    private CodeBuilder codeBuilder;

    /**
     * ����<code>MethodBuilder</code>��
     * 
     * @param cb method���ڵ�<code>ClassBuilder</code>����
     * @param access �����ԣ������<code>-1</code>����ȡĬ��ֵ��
     * @param returnType �������ͣ�Ϊ<code>null</code>�����޷���ֵ��
     * @param methodName method����
     * @param parameterTypes ��������
     * @param exceptionTypes �쳣����
     */
    protected MethodBuilder(ClassBuilder cb, int access, Class<?> returnType, String methodName,
                            Class<?>[] parameterTypes, Class<?>[] exceptionTypes) {
        // class builder
        this.cb = cb;

        // methodName
        this.methodName = assertNotNull(methodName, "methodName");

        // access
        if (access < 0) {
            if (isStaticConstructor()) {
                access = ACC_STATIC;
            } else {
                access = ACC_PUBLIC;
            }
        }

        this.access = access;

        // method desc
        this.returnType = getTypeFromClass(returnType == null ? void.class : returnType);
        this.parameterTypes = getTypes(parameterTypes);
        this.methodDesc = Type.getMethodDescriptor(this.returnType, this.parameterTypes);

        // exceptions
        this.exceptionTypes = getTypes(exceptionTypes);

        // visit method
        this.mv = decorate(cb.getClassVisitor().visitMethod(access, this.methodName, this.methodDesc, null,
                getInternalNames(this.exceptionTypes)));
    }

    /**
     * ȡ��method���ڵ�<code>ClassBuilder</code>��
     */
    public ClassBuilder getClassBuilder() {
        return cb;
    }

    /**
     * ȡ��<code>MethodVisitor</code>��
     */
    public MethodVisitor getMethodVisitor() {
        return mv;
    }

    /**
     * ȡ�÷������͡�
     */
    public Type getReturnType() {
        return returnType;
    }

    /**
     * ȡ�÷�������
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * �Ƿ�Ϊ���캯����
     */
    public boolean isConstructor() {
        return CONSTRUCTOR_NAME.equals(methodName);
    }

    /**
     * �Ƿ�Ϊ��̬���캯����
     */
    public boolean isStaticConstructor() {
        return STATIC_CONSTRUCTOR_NAME.equals(methodName);
    }

    /**
     * ȡ�ò������͡�
     */
    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * ȡ���쳣���͡�
     */
    public Type[] getExceptionTypes() {
        return exceptionTypes;
    }

    /**
     * ��ʼ���롣
     */
    public CodeBuilder startCode() {
        if (codeBuilder == null) {
            codeBuilder = new CodeBuilder(this, mv, access, methodName, methodDesc);
        }

        return codeBuilder;
    }

    /**
     * ����������
     */
    protected void endMethod() {
        if (codeBuilder == null) {
            mv.visitEnd(); // ����interface�ͳ��󷽷�
        } else {
            codeBuilder.visitMaxs(1, 1); // auto calculate stacks and locals
            codeBuilder.visitEnd();
        }
    }

    /**
     * ������һ�������װmethod visitor��
     */
    protected MethodVisitor decorate(MethodVisitor mv) {
        return mv;
    }
}
