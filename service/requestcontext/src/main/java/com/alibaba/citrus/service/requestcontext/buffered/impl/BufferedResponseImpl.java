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
package com.alibaba.citrus.service.requestcontext.buffered.impl;

import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.EmptyStackException;
import java.util.LinkedList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.support.AbstractResponseWrapper;
import com.alibaba.citrus.util.io.ByteArray;
import com.alibaba.citrus.util.io.ByteArrayInputStream;
import com.alibaba.citrus.util.io.ByteArrayOutputStream;
import com.alibaba.citrus.util.io.StreamUtil;

/**
 * ����<code>HttpServletResponse</code>��ʹ֮������ڴ��С�
 * 
 * @author Michael Zhou
 */
public class BufferedResponseImpl extends AbstractResponseWrapper {
    private static final Logger log = LoggerFactory.getLogger(BufferedResponseImpl.class);
    private boolean buffering = true;
    private Stack<ByteArrayOutputStream> bytesStack;
    private Stack<StringWriter> charsStack;
    private ServletOutputStream stream;
    private PrintWriter streamAdapter;
    private PrintWriter writer;
    private ServletOutputStream writerAdapter;

    /**
     * ����һ��<code>BufferedResponseImpl</code>��
     * 
     * @param requestContext response���ڵ�request context
     * @param response ԭʼ��response
     */
    public BufferedResponseImpl(RequestContext requestContext, HttpServletResponse response) {
        super(requestContext, response);
    }

    /**
     * ȡ���������
     * 
     * @return response�������
     * @throws IOException �������ʧ��
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (stream != null) {
            return stream;
        }

        if (writer != null) {
            // ���getWriter�����Ѿ������ã���writerת����OutputStream
            // ����������������������ڴ濪��������׼��servlet engine���������������Σ�
            // ֻ������servlet engine��Ҫ����������resin����
            if (writerAdapter != null) {
                return writerAdapter;
            } else {
                log.debug("Attampt to getOutputStream after calling getWriter.  This may cause unnecessary system cost.");
                writerAdapter = new WriterOutputStream(writer, getCharacterEncoding());
                return writerAdapter;
            }
        }

        if (buffering) {
            // ע�⣬servletStreamһ���������Ͳ��ı䣬
            // �����Ҫ�ı䣬ֻ��Ҫ�ı��������bytes�����ɡ�
            if (bytesStack == null) {
                bytesStack = new Stack<ByteArrayOutputStream>();
            }

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            bytesStack.push(bytes);
            stream = new BufferedServletOutputStream(bytes);

            log.debug("Created new byte buffer");
        } else {
            stream = super.getOutputStream();
        }

        return stream;
    }

    /**
     * ȡ������ַ�����
     * 
     * @return response������ַ���
     * @throws IOException �������ʧ��
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }

        if (stream != null) {
            // ���getOutputStream�����Ѿ������ã���streamת����PrintWriter��
            // ����������������������ڴ濪��������׼��servlet engine���������������Σ�
            // ֻ������servlet engine��Ҫ����������resin����
            if (streamAdapter != null) {
                return streamAdapter;
            } else {
                log.debug("Attampt to getWriter after calling getOutputStream.  This may cause unnecessary system cost.");
                streamAdapter = new PrintWriter(new OutputStreamWriter(stream, getCharacterEncoding()), true);
                return streamAdapter;
            }
        }

        if (buffering) {
            // ע�⣬servletWriterһ���������Ͳ��ı䣬
            // �����Ҫ�ı䣬ֻ��Ҫ�ı��������chars�����ɡ�
            if (charsStack == null) {
                charsStack = new Stack<StringWriter>();
            }

            StringWriter chars = new StringWriter();

            charsStack.push(chars);
            writer = new BufferedServletWriter(chars);

            log.debug("Created new character buffer");
        } else {
            writer = super.getWriter();
        }

        return writer;
    }

    /**
     * ����content���ȡ��õ���ֻ��<code>setBuffering(false)</code>ʱ��Ч��
     * 
     * @param length content����
     */
    @Override
    public void setContentLength(int length) {
        if (!buffering) {
            super.setContentLength(length);
        }
    }

    /**
     * ��ϴbuffer��
     * 
     * @throws IOException ���ʧ��
     */
    @Override
    public void flushBuffer() throws IOException {
        if (buffering) {
            flushBufferAdapter();

            if (writer != null) {
                writer.flush();
            } else if (stream != null) {
                stream.flush();
            }
        } else {
            super.flushBuffer();
        }
    }

    /**
     * �������buffers����������ʾ������Ϣ��
     * 
     * @throws IllegalStateException ���response�Ѿ�commit
     */
    @Override
    public void resetBuffer() {
        if (buffering) {
            flushBufferAdapter();

            if (stream != null) {
                bytesStack.clear();
                bytesStack.add(new ByteArrayOutputStream());
                ((BufferedServletOutputStream) stream).updateOutputStream(bytesStack.peek());
            }

            if (writer != null) {
                charsStack.clear();
                charsStack.add(new StringWriter());
                ((BufferedServletWriter) writer).updateWriter(charsStack.peek());
            }
        }

        super.resetBuffer();
    }

    /**
     * �����Ƿ�������Ϣ�������ڴ��С�
     * 
     * @return ����ǣ��򷵻�<code>true</code>
     */
    public boolean isBuffering() {
        return buffering;
    }

    /**
     * ����bufferģʽ��������ó�<code>true</code>����ʾ��������Ϣ�������ڴ��У�����ֱ�������ԭʼresponse�С�
     * <p>
     * �˷���������<code>getOutputStream</code>��<code>getWriter</code>����֮ǰִ�У������׳�
     * <code>IllegalStateException</code>��
     * </p>
     * 
     * @param buffering �Ƿ�buffer����
     * @throws IllegalStateException <code>getOutputStream</code>��
     *             <code>getWriter</code>�����Ѿ���ִ��
     */
    public void setBuffering(boolean buffering) {
        if (stream == null && writer == null) {
            if (this.buffering != buffering) {
                this.buffering = buffering;
                log.debug("Set buffering " + (buffering ? "on" : "off"));
            }
        } else {
            if (this.buffering != buffering) {
                throw new IllegalStateException(
                        "Unable to change the buffering mode since the getOutputStream() or getWriter() method has been called");
            }
        }
    }

    /**
     * �����µ�buffer�������ϵ�buffer��
     * 
     * @throws IllegalStateException �������bufferģʽ����<code>getWriter</code>
     *             �����������ã���<code>getOutputStream</code>������δ������
     */
    public void pushBuffer() {
        if (!buffering) {
            throw new IllegalStateException("Buffering mode is required to pushBuffer");
        }

        if (stream == null && writer == null) {
            throw new IllegalStateException("getOutputStream() or getWriter() method has not been called yet");
        }

        flushBufferAdapter();

        // ��stream��writer stack��ѹ���µ�buffer��
        if (stream != null) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            bytesStack.push(bytes);

            ((BufferedServletOutputStream) stream).updateOutputStream(bytesStack.peek());

            log.debug("Pushed new byte buffer (stack size is " + bytesStack.size() + ")");
        } else {
            StringWriter chars = new StringWriter();

            charsStack.push(chars);

            ((BufferedServletWriter) writer).updateWriter(charsStack.peek());

            log.debug("Pushed new character buffer (stack size is " + charsStack.size() + ")");
        }
    }

    /**
     * ���������buffer�������ջ��ֻ��һ��buffer���򵯳����ٴ���һ���µġ�
     * 
     * @return �����buffer���ݣ����<code>getOutputStream</code>������δ�����ã��򷵻ؿյ�byte array
     * @throws IllegalStateException �������bufferģʽ����<code>getWriter</code>������������
     */
    public ByteArray popByteBuffer() {
        if (!buffering) {
            throw new IllegalStateException("Buffering mode is required to popByteBuffer");
        }

        if (writer != null) {
            throw new IllegalStateException("Unable to popByteBuffer() since the getWriter() method has been called");
        }

        if (stream == null) {
            return new ByteArray(EMPTY_BYTE_ARRAY, 0, 0);
        } else {
            flushBufferAdapter();

            ByteArrayOutputStream block = bytesStack.pop();

            if (bytesStack.size() == 0) {
                bytesStack.push(new ByteArrayOutputStream());
            }

            ((BufferedServletOutputStream) stream).updateOutputStream(bytesStack.peek());

            log.debug("Popped the last byte buffer (stack size is " + bytesStack.size() + ")");

            return block.toByteArray();
        }
    }

    /**
     * ���������buffer�������ջ��ֻ��һ��buffer���򵯳����ٴ���һ���µġ�
     * 
     * @return �����buffer���ݣ����<code>getWriter</code>������δ�����ã��򷵻ؿյ��ַ���
     * @throws IllegalStateException �������bufferģʽ����<code>getOutputStream</code>
     *             ������������
     */
    public String popCharBuffer() {
        if (!buffering) {
            throw new IllegalStateException("Buffering mode is required to popCharBuffer");
        }

        if (stream != null) {
            throw new IllegalStateException(
                    "Unable to popCharBuffer() since the getOutputStream() method has been called");
        }

        if (writer == null) {
            return EMPTY_STRING;
        } else {
            flushBufferAdapter();

            StringWriter block = charsStack.pop();

            if (charsStack.size() == 0) {
                charsStack.push(new StringWriter());
            }

            ((BufferedServletWriter) writer).updateWriter(charsStack.peek());

            log.debug("Popped the last character buffer (stack size is " + charsStack.size() + ")");

            return block.toString();
        }
    }

    /**
     * ��buffer�е������ύ��������servlet������С�
     * <p>
     * �������û��ִ�й�<code>getOutputStream</code>��<code>getWriter</code>
     * ��������÷��������κ����顣
     * </p>
     * 
     * @throws IOException ����������ʧ��
     * @throws IllegalStateException ���������bufferģʽ����bufferջ�в�ֹһ��buffer
     */
    public void commitBuffer() throws IOException {
        if (stream == null && writer == null) {
            return;
        }

        if (!buffering) {
            throw new IllegalStateException("Buffering mode is required for commitBuffer");
        }

        // ���bytes
        if (stream != null) {
            if (bytesStack.size() > 1) {
                throw new IllegalStateException("More than 1 byte-buffers in the stack");
            }

            flushBufferAdapter();

            OutputStream ostream = super.getOutputStream();
            ByteArray bytes = popByteBuffer();

            bytes.writeTo(ostream);

            log.debug("Committed buffered bytes to the Servlet output stream");
        }

        // ���chars
        if (writer != null) {
            if (charsStack.size() > 1) {
                throw new IllegalStateException("More than 1 char-buffers in the stack");
            }

            flushBufferAdapter();

            PrintWriter writer = super.getWriter();
            String chars = popCharBuffer();

            writer.write(chars);

            log.debug("Committed buffered characters to the Servlet writer");
        }
    }

    /**
     * ��ϴbuffer adapter��ȷ��adapter�е���Ϣ��д��buffer�С�
     */
    private void flushBufferAdapter() {
        if (streamAdapter != null) {
            streamAdapter.flush();
        }

        if (writerAdapter != null) {
            try {
                writerAdapter.flush();
            } catch (IOException e) {
            }
        }
    }

    /**
     * ��<code>LinkedList</code>�̳е�stack������<code>java.util.Stack</code>
     * ��synchronized�Ĵ��ۡ�
     */
    private static class Stack<T> {
        private final LinkedList<T> list = createLinkedList();

        public T peek() {
            if (list.isEmpty()) {
                throw new EmptyStackException();
            }

            return list.getLast();
        }

        public void push(T object) {
            list.addLast(object);
        }

        public T pop() {
            if (list.isEmpty()) {
                throw new EmptyStackException();
            }

            return list.removeLast();
        }

        public int size() {
            return list.size();
        }

        public boolean add(T o) {
            return list.add(o);
        }

        public void clear() {
            list.clear();
        }
    }

    /**
     * ����һ�������ݱ������ڴ��е�<code>ServletOutputStream</code>��
     */
    private static class BufferedServletOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bytes;

        public BufferedServletOutputStream(ByteArrayOutputStream bytes) {
            this.bytes = bytes;
        }

        public void updateOutputStream(ByteArrayOutputStream bytes) {
            this.bytes = bytes;
        }

        @Override
        public void write(int b) throws IOException {
            bytes.write((byte) b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            bytes.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            bytes.flush();
        }

        @Override
        public void close() throws IOException {
            bytes.flush();
            bytes.close();
        }
    }

    /**
     * ����һ�������ݱ������ڴ��е�<code>PrintWriter</code>��
     */
    private static class BufferedServletWriter extends PrintWriter {
        public BufferedServletWriter(StringWriter chars) {
            super(chars);
        }

        public void updateWriter(StringWriter chars) {
            this.out = chars;
        }
    }

    /**
     * ��<code>Writer</code>���䵽<code>ServletOutputStream</code>��
     */
    private static class WriterOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private Writer writer;
        private String charset;

        public WriterOutputStream(Writer writer, String charset) {
            this.writer = writer;
            this.charset = defaultIfNull(charset, "ISO-8859-1");
        }

        @Override
        public void write(int b) throws IOException {
            buffer.write((byte) b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            buffer.write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            buffer.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            ByteArray bytes = buffer.toByteArray();

            if (bytes.getLength() > 0) {
                ByteArrayInputStream inputBytes = new ByteArrayInputStream(bytes.getRawBytes(), bytes.getOffset(),
                        bytes.getLength());
                InputStreamReader reader = new InputStreamReader(inputBytes, charset);

                StreamUtil.io(reader, writer, true, false);
                writer.flush();

                buffer.reset();
            }
        }

        @Override
        public void close() throws IOException {
            this.flush();
        }
    }
}
