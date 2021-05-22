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
package com.alibaba.citrus.service.requestcontext.util;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.fileupload.FileItem;
import org.springframework.core.MethodParameter;

/**
 * ����һ��ֵ���б�
 * 
 * @author Michael Zhou
 */
public interface ValueList {
    // =============================================================
    //  ȡ�ò�����ֵ 
    // =============================================================

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>false</code>��
     * 
     * @return ����ֵ
     */
    boolean getBooleanValue();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    boolean getBooleanValue(Boolean defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    byte getByteValue();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    byte getByteValue(Byte defaultValue);

    /**
     * ȡ��ָ���������ֽڡ�
     * 
     * @param charset ����ת���ַ��ı���
     * @return ����ֵ���ֽ�����
     * @throws UnsupportedEncodingException ���ָ���˴���ı����ַ���
     */
    byte[] getBytes(String charset) throws UnsupportedEncodingException;

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>'\0'</code>��
     * 
     * @return ����ֵ
     */
    char getCharacterValue();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    char getCharacterValue(Character defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    double getDoubleValue();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    double getDoubleValue(Double defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    float getFloatValue();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    float getFloatValue(Float defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    int getIntegerValue();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    int getIntegerValue(Integer defaultValue);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ������
     */
    int[] getIntegerValues();

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    int[] getIntegerValues(int[] defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    long getLongValue();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    long getLongValue(Long defaultValue);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ������
     */
    long[] getLongValues();

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    long[] getLongValues(long[] defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    short getShortValue();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    short getShortValue(Short defaultValue);

    /**
     * ȡ�����ڡ��ַ�����ʹ��ָ����<code>DateFormat</code>����������������ڣ��򷵻�<code>null</code>��
     * 
     * @param format <code>DateFormat</code>����
     * @return <code>java.util.Date</code>����
     */
    Date getDateValue(DateFormat format);

    /**
     * ȡ�����ڡ��ַ�����ʹ��ָ����<code>DateFormat</code>����������������ڣ��򷵻�Ĭ��ֵ��
     * 
     * @param format <code>DateFormat</code>����
     * @param defaultValue Ĭ��ֵ
     * @return <code>java.util.Date</code>����
     */
    Date getDateValue(DateFormat format, Date defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ
     */
    String getStringValue();

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    String getStringValue(String defaultValue);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ������
     */
    String[] getStringValues();

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    String[] getStringValues(String[] defaultValue);

    /**
     * ȡ��<code>FileItem</code>������������ڣ��򷵻�<code>null</code>��
     * 
     * @return <code>FileItem</code>����
     */
    FileItem getFileItem();

    /**
     * ȡ��<code>FileItem</code>������������ڣ��򷵻�<code>null</code>��
     * 
     * @return <code>FileItem</code>���������
     */
    FileItem[] getFileItems();

    /**
     * ȡ��ָ��������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ
     */
    Object getValue();

    /**
     * ȡ��ָ��������ֵ��������������ڣ��򷵻�Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    Object getValue(Object defaultValue);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ������
     */
    Object[] getValues();

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValues Ĭ��ֵ
     * @return ����ֵ������
     */
    Object[] getValues(Object[] defaultValues);

    /**
     * ȡ��ָ�����͵�ֵ��
     */
    <T> T getValueOfType(Class<T> type, MethodParameter methodParameter, Object[] defaultValues);

    /**
     * ȡ��ָ�����͵�ֵ��
     */
    <T> T getValueOfType(Class<T> type, boolean isPrimitive, MethodParameter methodParameter, Object[] defaultValues);

    // =============================================================
    //  ��Ӻ��޸Ĳ����ķ���
    // =============================================================

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    void addValue(boolean value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    void addValue(byte value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    void addValue(char value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    void addValue(double value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    void addValue(float value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    void addValue(int value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    void addValue(long value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    void addValue(short value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    void addValue(Object value);

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param value ����ֵ
     */
    void setValue(Object value);

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param values ����ֵ
     */
    void setValues(Object[] values);

    // =============================================================
    //  ��������
    // =============================================================

    /**
     * ȡ��ֵ�ĸ�����
     * 
     * @return ֵ�ĸ���
     */
    int size();
}
