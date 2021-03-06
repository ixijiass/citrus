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
/***
 * ASM tests
 * Copyright (c) 2002-2005 France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.alibaba.citrus.asm.commons;

import junit.framework.TestSuite;

import com.alibaba.citrus.asm.AbstractTest;
import com.alibaba.citrus.asm.ClassAdapter;
import com.alibaba.citrus.asm.ClassReader;
import com.alibaba.citrus.asm.ClassVisitor;
import com.alibaba.citrus.asm.ClassWriter;
import com.alibaba.citrus.asm.MethodVisitor;
import com.alibaba.citrus.asm.Opcodes;

/**
 * AnalyzerAdapter tests.
 * 
 * @author Eric Bruneton
 */
public class AnalyzerAdapterTest extends AbstractTest {

    public static TestSuite suite() throws Exception {
        return new AnalyzerAdapterTest().getSuite();
    }

    @Override
    public void test() throws Exception {
        ClassReader cr = new ClassReader(openStream());
        if (cr.readInt(4) != Opcodes.V1_6) {
            try {
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                cr.accept(cw, 0);
                cr = new ClassReader(cw.toByteArray());
            } catch (Exception e) {
                return;
            }
        }
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ClassAdapter(cw) {

            private String owner;

            @Override
            public void visit(final int version, final int access, final String name, final String signature,
                              final String superName, final String[] interfaces) {
                owner = name;
                cv.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(final int access, final String name, final String desc,
                                             final String signature, final String[] exceptions) {
                MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                return new AnalyzerAdapter(owner, access, name, desc, mv);
            }
        };
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
    }
}
