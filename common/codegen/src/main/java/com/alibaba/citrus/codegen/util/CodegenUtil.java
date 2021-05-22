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

import static com.alibaba.citrus.codegen.util.CodegenConstant.*;

import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.citrus.codegen.ClassBuilder;
import com.alibaba.citrus.codegen.CodeBuilder;
import com.alibaba.citrus.codegen.MethodBuilder;
import com.alibaba.citrus.util.StringUtil;

/**
 * ����������صĹ��ߡ�
 * 
 * @author Michael Zhou
 */
public class CodegenUtil {
    private static final AtomicLong uid = new AtomicLong(System.currentTimeMillis());

    /**
     * ����һ��Ψһ�ַ���������
     * <p>
     * �����ɵ�������ָ��<code>baseClass</code>��package��ͬ��
     * </p>
     */
    public static String generateClassName(Class<?> baseClass) {
        String baseName = baseClass != null ? baseClass.getName() : null;

        return generateClassName(baseName, null);
    }

    /**
     * ����һ��Ψһ�ַ���������
     * <p>
     * �����ɵ�������package���滻��ָ����<code>packageReplacement</code>��
     * </p>
     */
    public static String generateClassName(Class<?> baseClass, String packageReplacement) {
        String baseName = baseClass != null ? baseClass.getName() : null;

        return generateClassName(baseName, packageReplacement);
    }

    /**
     * ����һ��Ψһ�ַ���������
     * <p>
     * �����ɵ�������ָ��<code>baseName</code>��package��ͬ��
     * </p>
     */
    public static String generateClassName(String baseName) {
        return generateClassName(baseName, null);
    }

    /**
     * ����һ��Ψһ�ַ���������
     */
    public static String generateClassName(String baseName, String packageReplacement) {
        baseName = StringUtil.trimToEmpty(baseName);
        packageReplacement = StringUtil.trim(packageReplacement);

        // ȡ��baseName��package
        int index = baseName.lastIndexOf('.');

        if (packageReplacement == null && index > 0) {
            packageReplacement = baseName.substring(0, index);
        }

        // ȡ�ò�����package��baseName
        if (index > 0) {
            baseName = baseName.substring(index + 1);
        }

        // ����className
        String className = "$" + baseName + "_" + uid();

        if (!StringUtil.isEmpty(packageReplacement)) {
            className = packageReplacement + "." + className;
        }

        return className;
    }

    /**
     * ȡ��UID��
     */
    private static String uid() {
        return StringUtil.longToString(uid.incrementAndGet());
    }

    /**
     * ����Ĭ�Ϲ��캯����
     */
    public static void addDefaultConstructor(ClassBuilder cb) {
        MethodBuilder mb = cb.addConstructor(null, null);
        CodeBuilder code = mb.startCode();

        code.loadThis();
        code.invokeConstructor(cb.getSuperType(), new com.alibaba.citrus.asm.commons.Method(CONSTRUCTOR_NAME, "()V"));
        code.returnValue();
    }

    /**
     * ����һ�����س�����<code>toString()</code>������
     */
    public static void addToString(ClassBuilder cb, String constantToString) {
        MethodBuilder mb = cb.addMethod(String.class, "toString", null, null);
        CodeBuilder code = mb.startCode();

        code.push(constantToString);
        code.returnValue();
    }
}
