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
package com.alibaba.citrus.util.io;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.citrus.util.StringUtil;
import com.alibaba.citrus.util.io.OutputEngine.OutputStreamFactory;

public class OutputEngineTests {
    private String charData;
    private byte[] data;

    @Before
    public void init() throws Exception {
        charData = StringUtil.repeat("�л����", 1024 / 16 * 100);
        data = charData.getBytes("UTF-8");
    }

    @Test
    public void compressInputStream() throws Exception {

        // GZIPInputStream�Ƕ�ѹ�������н�ѹ����read() ԭʼ���� <- decompress <- compressed data stream
        // GZIPOutputStream�Ƕ����������ѹ����write() ԭʼ���� -> compress -> compressed data stream
        // ����JDK�в���������һ������read() compressed data <- compress <- ԭʼ������
        // ����OutputEngine�Ϳ���ʵ������������

        // ԭʼ����������
        InputStream rawDataStream = new ByteArrayInputStream(data);

        // OutputEngine����ȡ�������������GZIPOutputStream��ʵ��ѹ����
        OutputEngine isoe = new InputStreamOutputEngine(rawDataStream, new OutputStreamFactory() {
            public OutputStream getOutputStream(OutputStream out) throws IOException {
                return new GZIPOutputStream(out);
            }
        });

        // ��OutputEngine��ֱ��ȡ��ѹ��������
        OutputEngineInputStream compressedDataStream = new OutputEngineInputStream(isoe);

        byte[] compressedData = StreamUtil.readBytes(compressedDataStream, true).toByteArray();

        assertTrue(compressedData.length < data.length);

        // ��ѹ�����лָ�
        InputStream zis = new GZIPInputStream(new ByteArrayInputStream(compressedData));

        byte[] decompressedData = StreamUtil.readBytes(zis, true).toByteArray();

        assertArrayEquals(data, decompressedData);
    }

    @Test
    public void compressInputStream_fromReader() throws Exception {

        // ������������������read() compressed data <- compress <- ԭʼchar������

        // ԭʼ����������
        Reader rawDataStream = new StringReader(charData);

        // OutputEngine����ȡ�������������GZIPOutputStream��ʵ��ѹ����
        OutputEngine isoe = new ReaderOutputEngine(rawDataStream, new OutputStreamFactory() {
            public OutputStream getOutputStream(OutputStream out) throws IOException {
                return new GZIPOutputStream(out);
            }
        }, "UTF-8");

        // ��OutputEngine��ֱ��ȡ��ѹ��������
        OutputEngineInputStream compressedDataStream = new OutputEngineInputStream(isoe);

        byte[] compressedData = StreamUtil.readBytes(compressedDataStream, true).toByteArray();

        assertTrue(compressedData.length < charData.length());

        // ��ѹ�����лָ�
        Reader zis = new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(compressedData)), "UTF-8");

        String decompressedData = StreamUtil.readText(zis, true);

        assertEquals(charData, decompressedData);
    }
}
