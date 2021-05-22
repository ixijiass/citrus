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
package com.alibaba.citrus.util.internal.templatelite;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.Assert.ExceptionType.*;

import java.io.IOException;

/**
 * һ����template�����������<code>Appendable</code>��visitor��
 * 
 * @author Michael Zhou
 */
public abstract class TextWriter<A extends Appendable> {
    private A out;

    public TextWriter() {
    }

    public TextWriter(A out) {
        setOut(out);
    }

    /**
     * ���ʴ��ı���
     */
    public final void visitText(String text) throws IOException {
        out().append(text);
    }

    /**
     * ȡ�����������<code>Appendable</code>ʵ����
     */
    public final A out() {
        return assertNotNull(out, ILLEGAL_STATE, "setOut() not called yet");
    }

    /**
     * �������������<code>Appendable</code>ʵ����
     */
    public final void setOut(A out) {
        this.out = out;
    }
}
