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

import static com.alibaba.citrus.codegen.util.CodegenConstant.*;
import static com.alibaba.citrus.codegen.util.CodegenUtil.*;
import static com.alibaba.citrus.codegen.util.TypeUtil.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.alibaba.citrus.asm.ClassReader;
import com.alibaba.citrus.asm.ClassWriter;
import com.alibaba.citrus.asm.util.TraceClassVisitor;

/**
 * ��̬���ɺͱ������ӿڡ�
 * 
 * @author Michael Zhou
 */
public class ClassPool {
    private final BytecodeLoader classLoader;
    private boolean debugging;
    private File debuggingLocation;
    private int classWriterFlags;
    private String packageName;

    /**
     * ����һ��<code>ClassPool</code>��ʹ��װ��<code>ClassPool</code>���
     * <code>ClassLoader</code>��
     */
    public ClassPool() {
        this(null);
    }

    /**
     * ����һ��<code>ClassPool</code>��ʹ��ָ����<code>ClassLoader</code>��
     */
    public ClassPool(ClassLoader parentClassLoader) {
        if (parentClassLoader == null) {
            parentClassLoader = getClass().getClassLoader();
        }

        this.classLoader = new BytecodeLoader(parentClassLoader);
        this.classWriterFlags = DEFAULT_CLASS_WRITER_FLAGS;
        this.packageName = DEFAULT_PACKAGE_NAME;
    }

    /**
     * ȡ��<code>ClassLoader</code>��
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * �Ƿ񱣴�classԴ�ļ���Ŀ���ļ���
     */
    public boolean isDebugging() {
        return debugging;
    }

    /**
     * ���ò������Ƿ񱣴�classԴ�ļ���Ŀ���ļ���
     */
    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    /**
     * Class��Դ����Ͷ������ļ������������Ŀ¼��
     */
    public File getDebuggingLocation() {
        return debuggingLocation;
    }

    /**
     * ����class��Դ����Ͷ������ļ������Ŀ¼��
     */
    public void setDebuggingLocation(File debuggingLocation) {
        this.debuggingLocation = debuggingLocation;
    }

    /**
     * ȡ��<code>ClassWriter</code>�ı�־λ��
     */
    public int getClassWriterFlags() {
        return classWriterFlags;
    }

    /**
     * ����<code>ClassWriter</code>�ı�־λ��
     */
    public void setClassWriterFlags(int classWriterFlags) {
        this.classWriterFlags = classWriterFlags;
    }

    /**
     * ȡ�����ɴ����package���ơ�
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * �������ɴ����package���ơ�
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * ����ָ����<code>basename</code>����һ��Ψһ�����������������������͵�<code>ClassBuilder</code>��
     */
    public ClassBuilder createClass(String basename, Class<?> superclass, Class<?>[] interfaces) {
        return createClassOrInterface(-1, false, basename, superclass, interfaces, -1, null);
    }

    /**
     * ����ָ����<code>basename</code>����һ��Ψһ�����������������������͵�<code>ClassBuilder</code>��
     */
    public ClassBuilder createClass(int access, String basename, Class<?> superclass, Class<?>[] interfaces) {
        return createClassOrInterface(access, false, basename, superclass, interfaces, -1, null);
    }

    /**
     * ����ָ����<code>basename</code>����һ��Ψһ�����������������������͵�<code>ClassBuilder</code>��
     */
    public ClassBuilder createClass(int access, String basename, Class<?> superclass, Class<?>[] interfaces,
                                    int classVersion, String source) {
        return createClassOrInterface(access, false, basename, superclass, interfaces, classVersion, source);
    }

    /**
     * ����ָ����<code>basename</code>����һ��Ψһ�ӿ����������������������͵�<code>ClassBuilder</code>��
     */
    public ClassBuilder createInterface(String basename, Class<?>[] interfaces) {
        return createClassOrInterface(-1, true, basename, null, interfaces, -1, null);
    }

    /**
     * ����ָ����<code>basename</code>����һ��Ψһ�ӿ����������������������͵�<code>ClassBuilder</code>��
     */
    public ClassBuilder createInterface(int access, String basename, Class<?>[] interfaces) {
        return createClassOrInterface(access, true, basename, null, interfaces, -1, null);
    }

    /**
     * ����ָ����<code>basename</code>����һ��Ψһ�ӿ����������������������͵�<code>ClassBuilder</code>��
     */
    public ClassBuilder createInterface(int access, String basename, Class<?>[] interfaces, int classVersion,
                                        String source) {
        return createClassOrInterface(access, true, basename, null, interfaces, classVersion, source);
    }

    /**
     * ����ָ����<code>basename</code>����һ��Ψһ�����������������������͵�<code>ClassBuilder</code>��
     */
    private ClassBuilder createClassOrInterface(int access, boolean isInterface, String basename, Class<?> superclass,
                                                Class<?>[] interfaces, int classVersion, String source) {
        String className = generateClassName(basename, getPackageName());
        ClassWriter cw = new DebuggingClassWriter();

        return new PooledClassBuilder(cw, access, isInterface, className, superclass, interfaces, classVersion, source);
    }

    /**
     * ����װ��bytecode��<code>ClassLoader</code>��
     */
    private static class BytecodeLoader extends ClassLoader {
        public BytecodeLoader(ClassLoader parentClassLoader) {
            super(parentClassLoader);
        }

        public Class<?> defineClass(String className, byte[] bytes) {
            return defineClass(className, bytes, 0, bytes.length, getClass().getProtectionDomain());
        }
    }

    /**
     * ��̬�������ӿڡ�
     */
    private class PooledClassBuilder extends ClassBuilder {
        public PooledClassBuilder(ClassWriter cw, int access, boolean isInterface, String className,
                                  Class<?> superclass, Class<?>[] interfaces, int classVersion, String source) {
            super(cw, access, isInterface, className, superclass, interfaces, classVersion, source);
        }

        @Override
        public Class<?> defineClass(String className, byte[] bytes) {
            return classLoader.defineClass(className, bytes);
        }
    }

    /**
     * ֧��debugging���ഴ������
     */
    private class DebuggingClassWriter extends ClassWriter {
        private String className;

        public DebuggingClassWriter() {
            super(classWriterFlags);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (debugging) {
                this.className = getTypeFromInternalName(name).getClassName();
            }

            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public byte[] toByteArray() {
            byte[] bytes = super.toByteArray();

            if (className != null) {
                if (debuggingLocation == null) {
                    debuggingLocation = new File(System.getProperty("java.io.tmpdir"));
                }

                File baseFile = new File(debuggingLocation, className.replace('.', File.separatorChar));
                File classDir = baseFile.getParentFile();

                classDir.mkdirs();

                if (classDir.exists() && classDir.isDirectory()) {
                    try {
                        // ���class�ļ�
                        File classFile = new File(classDir, baseFile.getName() + ".class");
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(classFile));

                        try {
                            out.write(bytes);
                        } finally {
                            out.close();
                        }

                        // ���asm�ļ�
                        File asmFile = new File(classDir, baseFile.getName() + ".asm");
                        out = new BufferedOutputStream(new FileOutputStream(asmFile));

                        try {
                            ClassReader cr = new ClassReader(bytes);
                            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
                            TraceClassVisitor tcv = new TraceClassVisitor(null, pw);

                            cr.accept(tcv, 0);
                            pw.flush();
                        } finally {
                            out.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }

            return bytes;
        }
    }
}
