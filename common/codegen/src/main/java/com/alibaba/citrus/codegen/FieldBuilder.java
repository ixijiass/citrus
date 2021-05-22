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

import com.alibaba.citrus.asm.AnnotationVisitor;
import com.alibaba.citrus.asm.Attribute;
import com.alibaba.citrus.asm.FieldVisitor;
import com.alibaba.citrus.asm.Type;

/**
 * ��������һ��field�Ĺ��ߡ�
 * 
 * @author Michael Zhou
 */
public abstract class FieldBuilder {
    private final ClassBuilder cb;
    private final FieldVisitor fv;
    private final boolean isConstant;
    private final Type fieldType;
    private final String fieldName;

    /**
     * ����һ��<code>FieldBuilder</code>��
     * 
     * @param cb field���ڵ�<code>ClassBuilder</code>����
     * @param access �����ԣ������<code>-1</code>����ȡĬ��ֵ��
     * @param fieldType field����
     * @param fieldName field����
     * @param value �ֶ�ֵ
     */
    protected FieldBuilder(ClassBuilder cb, int access, Class<?> fieldType, String fieldName, Object value) {
        // class builder
        this.cb = cb;

        // access
        if (access < 0) { // Ĭ��ֵ
            if (cb.isInterface()) {
                access = ACC_PUBLIC | ACC_CONSTANT; // ��interface���ԣ�����Ϊ�� public final static
            } else {
                access = ACC_PRIVATE; // ����class���ԣ�Ĭ��Ϊ�� private
            }
        }

        // isConstant
        this.isConstant = testBits(access, ACC_CONSTANT);

        // fieldType
        this.fieldType = getTypeFromClass(assertNotNull(fieldType, "fieldClass"));

        // fieldName
        this.fieldName = assertNotNull(fieldName, "fieldName");

        // visit field
        this.fv = decorate(cb.getClassVisitor().visitField(access, fieldName, this.fieldType.getDescriptor(), null,
                value));
    }

    /**
     * ȡ��field���ڵ�<code>ClassBuilder</code>��
     */
    public ClassBuilder getClassBuilder() {
        return cb;
    }

    /**
     * ȡ��<code>FieldVisitor</code>��
     */
    public FieldVisitor getFieldVisitor() {
        return fv;
    }

    /**
     * �Ƿ�Ϊ������
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * ȡ��field���͡�
     */
    public Type getFieldType() {
        return fieldType;
    }

    /**
     * ȡ��field���ơ�
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * ʵ�ֽӿ�<code>FieldVisitor</code>��
     */
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return fv.visitAnnotation(desc, visible);
    }

    /**
     * ʵ�ֽӿ�<code>FieldVisitor</code>��
     */
    public void visitAttribute(Attribute attr) {
        fv.visitAttribute(attr);
    }

    /**
     * ����field��
     */
    protected void endField() {
        fv.visitEnd();
    }

    /**
     * ������һ�������װfield visitor��
     */
    protected FieldVisitor decorate(FieldVisitor fv) {
        return fv;
    }
}
