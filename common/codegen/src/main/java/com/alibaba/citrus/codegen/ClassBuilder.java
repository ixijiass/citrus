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

import java.lang.reflect.Method;

import com.alibaba.citrus.asm.ClassVisitor;
import com.alibaba.citrus.asm.ClassWriter;
import com.alibaba.citrus.asm.Type;

/**
 * ��������һ�����ӿڵĹ��ߡ�
 * 
 * @author Michael Zhou
 */
public abstract class ClassBuilder {
    private final ClassWriter cw;
    private final ClassVisitor cv; // decorated cw
    private final boolean isInterface;
    private final String className;
    private final Type classType;
    private final Type superType;
    private final Type[] interfaceTypes;
    private Member lastMember;

    /**
     * ����һ��<code>ClassBuilder</code>��
     * 
     * @param cw <code>ClassWriter</code>����
     * @param access �����ԣ������<code>-1</code>����ȡĬ��ֵ<code>public</code>��
     * @param isInterface �Ƿ�Ϊ�ӿڡ�
     * @param className Ҫ���ɵ�����
     * @param superclass ����
     * @param interfaces �ӿ�
     * @param classVersion �����ư汾�������<code>-1</code>����ȡĬ��ֵ��
     * @param source Դ�ļ����������<code>null</code>����ȡĬ��ֵ��
     */
    public ClassBuilder(ClassWriter cw, int access, boolean isInterface, String className, Class<?> superclass,
                        Class<?>[] interfaces, int classVersion, String source) {
        // class writer/visitor
        this.cw = cw;
        this.cv = decorate(cw);

        // access
        if (access < 0) {
            access = ACC_PUBLIC; // Ĭ��ֵ
        }

        access |= ACC_SUPER; // for backward-compatibility

        if (isInterface) {
            access |= ACC_ABSTRACT | ACC_INTERFACE;
        }

        // isInterface
        this.isInterface = isInterface;

        // className
        this.className = assertNotNull(className, "className");
        this.classType = getTypeFromClassName(className);

        // superclass
        if (superclass == null) {
            this.superType = OBJECT_TYPE;
        } else {
            this.superType = getTypeFromClass(superclass);
        }

        // interfaces
        this.interfaceTypes = getTypes(interfaces);

        // classVersion
        if (classVersion < 0) {
            classVersion = DEFAULT_CLASS_VERSION;
        }

        // source
        if (source == null) {
            source = DEFAULT_SOURCE;
        }

        cv.visit(classVersion, access, classType.getInternalName(), null, superType.getInternalName(),
                getInternalNames(interfaceTypes));

        if (source != null) {
            cv.visitSource(source, null);
        }
    }

    /**
     * ȡ��<code>ClassVisitor</code>��
     */
    public ClassVisitor getClassVisitor() {
        return cv;
    }

    /**
     * ȡ�õ�ǰ��������
     */
    public String getClassName() {
        return className;
    }

    /**
     * �ж��Ƿ�Ϊ�ӿڡ�
     */
    public boolean isInterface() {
        return isInterface;
    }

    /**
     * ȡ�õ�ǰ��������Ϣ��
     */
    public Type getType() {
        return classType;
    }

    /**
     * ȡ�ø����������Ϣ��
     */
    public Type getSuperType() {
        return superType;
    }

    /**
     * ����һ��public����field��
     */
    public FieldBuilder addConstantField(Class<?> fieldType, String fieldName, Object value) {
        return addField(ACC_PUBLIC | ACC_CONSTANT, fieldType, fieldName, value);
    }

    /**
     * ����һ������field��
     */
    public FieldBuilder addConstantField(int access, Class<?> fieldType, String fieldName, Object value) {
        return addField(access | ACC_CONSTANT, fieldType, fieldName, value);
    }

    /**
     * ����һ��private field��
     */
    public FieldBuilder addField(Class<?> fieldType, String fieldName, Object value) {
        return addField(-1, fieldType, fieldName, value);
    }

    /**
     * ����һ��field��
     */
    public FieldBuilder addField(int access, Class<?> fieldType, String fieldName, Object value) {
        FieldBuilder fb = setMember(new FieldMember(this, access, fieldType, fieldName, value));

        return fb;
    }

    /**
     * ����public���캯����
     */
    public MethodBuilder addConstructor(Class<?>[] parameterTypes, Class<?>[] exceptionTypes) {
        return addMethod(-1, null, CONSTRUCTOR_NAME, parameterTypes, exceptionTypes);
    }

    /**
     * �������캯����
     */
    public MethodBuilder addConstructor(int access, Class<?>[] parameterTypes, Class<?>[] exceptionTypes) {
        return addMethod(access, null, CONSTRUCTOR_NAME, parameterTypes, exceptionTypes);
    }

    /**
     * ������̬���캯����
     */
    public MethodBuilder addStaticConstructor() {
        return addMethod(-1, null, STATIC_CONSTRUCTOR_NAME, null, null);
    }

    /**
     * ����public������
     */
    public MethodBuilder addMethod(Method method) {
        return addMethod(-1, method.getReturnType(), method.getName(), method.getParameterTypes(),
                method.getExceptionTypes());
    }

    /**
     * ����public������
     */
    public MethodBuilder addMethod(Class<?> returnType, String methodName, Class<?>[] parameterTypes,
                                   Class<?>[] exceptionTypes) {
        return addMethod(-1, returnType, methodName, parameterTypes, exceptionTypes);
    }

    /**
     * ����������
     */
    public MethodBuilder addMethod(int access, Class<?> returnType, String methodName, Class<?>[] parameterTypes,
                                   Class<?>[] exceptionTypes) {
        MethodBuilder mb = setMember(new MethodMember(this, access, returnType, methodName, parameterTypes,
                exceptionTypes));

        return mb;
    }

    /**
     * ����class��
     */
    public final Class<?> toClass() {
        setMember(null);
        cv.visitEnd();
        byte[] bytes = cw.toByteArray();

        return defineClass(getClassName(), bytes);
    }

    /**
     * ������һ�������װclass writer��
     */
    protected ClassVisitor decorate(ClassVisitor cv) {
        return cv;
    }

    /**
     * ���岢װ���ࡣ
     */
    protected abstract Class<?> defineClass(String className, byte[] bytes);

    /**
     * ����<code>end</code>������class��Ա��
     */
    private interface Member {
        void end();
    }

    /**
     * ����ǰһ����Ա����ʼ�µĳ�Ա��
     */
    private <M extends Member> M setMember(M member) {
        if (lastMember != null) {
            lastMember.end();
        }

        lastMember = member;

        return member;
    }

    /**
     * ���Զ�������<code>FieldBuilder</code>��
     */
    private class FieldMember extends FieldBuilder implements Member {
        protected FieldMember(ClassBuilder cb, int access, Class<?> fieldType, String fieldName, Object value) {
            super(cb, access, fieldType, fieldName, value);
        }

        public void end() {
            endField();
        }
    }

    /**
     * ���Զ�������<code>MethodBuilder</code>��
     */
    private class MethodMember extends MethodBuilder implements Member {
        protected MethodMember(ClassBuilder cb, int access, Class<?> returnType, String methodName,
                               Class<?>[] parameterTypes, Class<?>[] exceptionTypes) {
            super(cb, access, returnType, methodName, parameterTypes, exceptionTypes);
        }

        public void end() {
            endMethod();
        }
    }
}
