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
package com.alibaba.citrus.asm.attrs;

import java.util.Map;

import com.alibaba.citrus.asm.Attribute;
import com.alibaba.citrus.asm.ByteVector;
import com.alibaba.citrus.asm.ClassReader;
import com.alibaba.citrus.asm.ClassWriter;
import com.alibaba.citrus.asm.Label;
import com.alibaba.citrus.asm.util.ASMifiable;
import com.alibaba.citrus.asm.util.Traceable;

/**
 * A non standard attribute used for testing purposes.
 * 
 * @author Eric Bruneton
 */
public class Comment extends Attribute implements ASMifiable, Traceable {

    public Comment() {
        super("Comment");
    }

    @Override
    public boolean isUnknown() {
        return false;
    }

    @Override
    protected Attribute read(final ClassReader cr, final int off, final int len, final char[] buf, final int codeOff,
                             final Label[] labels) {

        return new Comment();
    }

    @Override
    protected ByteVector write(final ClassWriter cw, final byte[] code, final int len, final int maxStack,
                               final int maxLocals) {
        return new ByteVector();
    }

    public void asmify(final StringBuilder buf, final String varName, final Map labelNames) {
        buf.append("Attribute ").append(varName).append(" = new com.alibaba.citrus.asm.attrs.Comment();");
    }

    public void trace(final StringBuilder buf, final Map labelNames) {
    }
}
