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
package com.alibaba.citrus.util.internal;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.ArrayList;

/**
 * ֧�ַּ�������string builder��
 * 
 * @author Michael Zhou
 */
public class IndentableStringBuilder extends NormalizableStringBuilder<IndentableStringBuilder> {
    private final IndentStack indents = new IndentStack();
    private final int defaultIndent;
    private int indentLevel;
    private int quoteLevel;
    private boolean lazyAppendNewLine; // �Ƴ�������У��Ƴٵ���һ���ַ������ǰ
    private boolean lazyStartHangingIndent; // �Ƴ������������Ƴٵ���һ�����к����һ��start()����Ч��Ϊ��������
    private int hangingIndent;

    public IndentableStringBuilder() {
        this(-1);
    }

    public IndentableStringBuilder(int indent) {
        this.defaultIndent = indent <= 0 ? 2 : indent;
    }

    @Override
    public void clear() {
        super.clear();

        indents.clear();
        indentLevel = 0;
        quoteLevel = 0;
        lazyAppendNewLine = false;
        lazyStartHangingIndent = false;
        hangingIndent = 0;
    }

    /**
     * �˴��յ����ַ��У����� CR/LF/CRLF ���ѱ���񻯳�ͳһ��LF�ˡ�
     */
    @Override
    protected void visit(char c) {
        boolean newLine = endsWithNewLine();

        if (c == LF && lazyStartHangingIndent) {
            appendInternalNewLine();
            doStartHanglingIndentIfRequired();
            return;
        }

        // ��end quote��׷�ӻ���
        if (!newLine && lazyAppendNewLine) {
            appendInternalNewLine();
            newLine = true;
        }

        // ���begin quotes
        for (; quoteLevel < indentLevel; quoteLevel++) {
            String beginQuote = indents.getBeginQuote(quoteLevel);

            if (isEmpty(beginQuote)) {
                if (!newLine && indents.independent(quoteLevel)) {
                    appendInternalNewLine();
                    newLine = true;
                }
            } else {
                if (newLine) {
                    appendIndent(quoteLevel);
                } else {
                    if (!endsWith(" ")) {
                        appendInternal(" "); // begin quoteǰ��һ��
                    }
                }

                appendInternal(beginQuote);
                appendInternalNewLine();

                newLine = true;
            }
        }

        lazyAppendNewLine = false;

        // ����ַ�
        if (c == LF) {
            appendInternalNewLine();
        } else {
            if (newLine) {
                appendIndent(indentLevel);
            }

            appendInternal(c);
        }
    }

    /**
     * ����һ��������
     */
    public IndentableStringBuilder start() {
        return start(null, null, -1);
    }

    /**
     * ����һ��������
     */
    public IndentableStringBuilder start(int indent) {
        return start(null, null, indent);
    }

    /**
     * ����һ��������ʹ��ָ����ǰ��������
     */
    public IndentableStringBuilder start(String beginQuote, String endQuote) {
        return start(beginQuote, endQuote, -1);
    }

    /**
     * ����һ��������ʹ��ָ����ǰ��������
     */
    public IndentableStringBuilder start(String beginQuote, String endQuote, int indent) {
        doStartHanglingIndentIfRequired();
        indents.pushIndent(beginQuote, endQuote, indent);
        indentLevel++;
        return this;
    }

    /**
     * ����һ�����л�start()��ʼ����������
     */
    public IndentableStringBuilder startHangingIndent() {
        return startHangingIndent(0);
    }

    /**
     * ����һ�����л�start()��ʼ����������
     */
    public IndentableStringBuilder startHangingIndent(int indentOffset) {
        doStartHanglingIndentIfRequired();

        lazyStartHangingIndent = true;

        if (!lazyAppendNewLine && lineLength() - currentIndent() > 0 && quoteLevel >= indentLevel) {
            hangingIndent = defaultIndent(lineLength() - currentIndent() + indentOffset);
        } else {
            hangingIndent = defaultIndent(indentOffset);
        }

        return this;
    }

    /**
     * ȷ����������������еĻ����Ѿ�������
     */
    private void doStartHanglingIndentIfRequired() {
        if (lazyStartHangingIndent) {
            lazyStartHangingIndent = false;
            start(EMPTY_STRING, EMPTY_STRING, hangingIndent);
        }
    }

    /**
     * ����һ��������ע�⣬������֮ǰ�������ٵ���һ��end()����ȷ�����Ļ��п��Ա������
     */
    public IndentableStringBuilder end() {
        flush();

        // ����δ��������������
        if (lazyStartHangingIndent) {
            if (!endsWithNewLine()) {
                lazyAppendNewLine = true;
            }

            lazyStartHangingIndent = false;
            return this;
        }

        // ���ڸտ�ʼ�ͽ����ģ������end quote
        if (indentLevel > quoteLevel) {
            indentLevel--;
        } else {
            assertTrue(indentLevel == quoteLevel, "indentLevel != quoteLevel");

            if (indentLevel > 0) {
                indentLevel--;
                quoteLevel--;

                String endQuote = indents.getEndQuote(indentLevel);

                if (!isEmpty(endQuote)) {
                    // ȷ��end quote֮ǰ����
                    if (!endsWithNewLine()) {
                        appendInternalNewLine();
                    }

                    // ���end quote
                    appendIndent(indentLevel);
                    appendInternal(endQuote);
                }

                lazyAppendNewLine = true;
            }
        }

        indents.popIndent();

        return this;
    }

    /**
     * ȡ�õ�ǰ������������
     */
    public int currentIndent() {
        return indents.getCurrentIndent();
    }

    /**
     * ���indentδָ������ȡ��Ĭ��indent��
     */
    private int defaultIndent(int indent) {
        return indent <= 0 ? defaultIndent : indent;
    }

    private void appendIndent(int indentLevel) {
        int indent = indents.getIndent(indentLevel - 1);

        for (int j = 0; j < indent; j++) {
            appendInternal(' ');
        }
    }

    /**
     * ���������Ϣ��ջ��
     */
    private class IndentStack extends ArrayList<Object> {
        private static final long serialVersionUID = -876139304840511103L;
        private static final int entrySize = 4;

        public String getBeginQuote(int indentLevel) {
            if (indentLevel < 0 || indentLevel >= depth()) {
                return EMPTY_STRING;
            }

            return (String) super.get(indentLevel * entrySize);
        }

        public String getEndQuote(int indentLevel) {
            if (indentLevel < 0 || indentLevel >= depth()) {
                return EMPTY_STRING;
            }

            return (String) super.get(indentLevel * entrySize + 1);
        }

        public int getIndent(int indentLevel) {
            if (indentLevel < 0 || indentLevel >= depth()) {
                return 0;
            }

            return (Integer) super.get(indentLevel * entrySize + 2);
        }

        /**
         * �����ǰlevel�����ں�һ��level���򷵻�false��
         */
        public boolean independent(int indentLevel) {
            if (indentLevel < 0 || indentLevel >= depth() - 1) {
                return true;
            }

            int i1 = (Integer) super.get(indentLevel * entrySize + 3);
            int i2 = (Integer) super.get((indentLevel + 1) * entrySize + 3);

            return i1 != i2;
        }

        public int getCurrentIndent() {
            int depth = depth();

            if (depth > 0) {
                return getIndent(depth - 1);
            } else {
                return 0;
            }
        }

        public int depth() {
            return super.size() / entrySize;
        }

        public void pushIndent(String beginQuote, String endQuote, int indent) {
            super.add(defaultIfNull(beginQuote, "{"));
            super.add(defaultIfNull(endQuote, "}"));
            super.add(defaultIndent(indent) + getCurrentIndent());
            super.add(length());
        }

        public void popIndent() {
            int length = super.size();

            if (length > 0) {
                for (int i = 0; i < entrySize; i++) {
                    super.remove(--length);
                }
            }
        }
    }
}

/**
 * ��CR/LF/CRLFͳһ��LF��string builder��
 * 
 * @author Michael Zhou
 */
abstract class NormalizableStringBuilder<B extends NormalizableStringBuilder<B>> implements Appendable {
    protected final static char CR = '\r';
    protected final static char LF = '\n';
    private final static char NONE = '\0';
    private final StringBuilder out = new StringBuilder();
    private final String newLine;
    private int newLineStartIndex = 0;
    private char readAheadBuffer = '\0';

    public NormalizableStringBuilder() {
        this(null);
    }

    public NormalizableStringBuilder(String newLine) {
        this.newLine = defaultIfNull(newLine, String.valueOf(LF));
    }

    /**
     * ����������ݡ�
     */
    public void clear() {
        out.setLength(0);
        newLineStartIndex = 0;
        readAheadBuffer = '\0';
    }

    /**
     * ȡ��buffer�����ݵĳ��ȡ�
     */
    public final int length() {
        return out.length();
    }

    /**
     * ȡ�õ�ǰ�еĳ��ȡ�
     */
    public final int lineLength() {
        return out.length() - newLineStartIndex;
    }

    /**
     * <code>Appendable</code>�ӿڷ�����
     */
    public final B append(CharSequence csq) {
        return append(csq, 0, csq.length());
    }

    /**
     * <code>Appendable</code>�ӿڷ�����
     */
    public final B append(CharSequence csq, int start, int end) {
        for (int i = start; i < end; i++) {
            append(csq.charAt(i));
        }

        return thisObject();
    }

    /**
     * <code>Appendable</code>�ӿڷ�����
     */
    public final B append(char c) {
        // �� CR|LF|CRLF ת����ͳһ�� LF
        switch (readAheadBuffer) {
            case NONE:
                switch (c) {
                    case CR: // \r
                        readAheadBuffer = CR;
                        break;

                    case LF: // \n
                        readAheadBuffer = NONE;
                        visit(LF);
                        break;

                    default:
                        readAheadBuffer = NONE;
                        visit(c);
                        break;
                }

                break;

            case CR:
                switch (c) {
                    case CR: // \r\r
                        readAheadBuffer = CR;
                        visit(LF);
                        break;

                    case LF: // \r\n
                        readAheadBuffer = NONE;
                        visit(LF);
                        break;

                    default:
                        readAheadBuffer = NONE;
                        visit(LF);
                        visit(c);
                        break;
                }

                break;

            default:
                unreachableCode();
                break;
        }

        return thisObject();
    }

    /**
     * ���า�Ǵ˷������Ա���������ַ������У����� CR/LF/CRLF ���ѱ���񻯳�ͳһ��LF�ˡ�
     */
    protected abstract void visit(char c);

    /**
     * ����ͨ���˷������ڲ�buffer��������ݡ�
     */
    protected final void appendInternal(String s) {
        out.append(s);
    }

    /**
     * ����ͨ���˷������ڲ�buffer��������ݡ�
     */
    protected final void appendInternal(char c) {
        out.append(c);
    }

    /**
     * ����ͨ���˷������ڲ�buffer����ӻ��С�
     * <p>
     * �������ͨ���˷��������У�����<code>newLineStartIndex</code>�᲻��ȷ��
     * </p>
     */
    protected final void appendInternalNewLine() {
        out.append(newLine);
        newLineStartIndex = out.length();
    }

    /**
     * �ж�buf�Ƿ���ָ���ַ�����β��
     */
    public final boolean endsWith(String testStr) {
        if (testStr == null) {
            return false;
        }

        int testStrLength = testStr.length();
        int bufferLength = out.length();

        if (bufferLength < testStrLength) {
            return false;
        }

        int baseIndex = bufferLength - testStrLength;

        for (int i = 0; i < testStrLength; i++) {
            if (out.charAt(baseIndex + i) != testStr.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * �ж�out�Ƿ��Ի��н�β�������ǿ�buffer��
     */
    public final boolean endsWithNewLine() {
        return out.length() == 0 || endsWith(newLine);
    }

    private B thisObject() {
        @SuppressWarnings("unchecked")
        B buf = (B) this;
        return buf;
    }

    /**
     * ȷ�����һ�����б������
     */
    public final void flush() {
        if (readAheadBuffer == CR) {
            readAheadBuffer = NONE;
            visit(LF);
        }
    }

    @Override
    public final String toString() {
        return out.toString();
    }
}
