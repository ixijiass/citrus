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
package com.alibaba.citrus.service.requestcontext.parser;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.TypeConverter;
import org.springframework.core.MethodParameter;

/**
 * ����һ��������������ȡ��HTTP�����еĲ�����cookies��
 * <p>
 * ע�⣺������cookie�����ƿ��ܱ�ת����ȫ����д��ȫ��Сд�� ���Ǹ��������ļ��еĲ�����<code>url.case.folding</code>
 * ��ָ���ġ�
 * </p>
 * 
 * @author Michael Zhou
 */
public interface ValueParser {
    /**
     * ȡ������ת������
     */
    TypeConverter getTypeConverter();

    // =============================================================
    //  ��ѯ�����ķ���
    // =============================================================

    /**
     * ȡ��ֵ��������
     * 
     * @return ֵ������
     */
    int size();

    /**
     * �ж��Ƿ���ֵ��
     * 
     * @return �����ֵ���򷵻�<code>true</code>
     */
    boolean isEmpty();

    /**
     * ����Ƿ����ָ�����ƵĲ�����
     * 
     * @param key Ҫ���ҵĲ�����
     * @return ������ڣ��򷵻�<code>true</code>
     */
    boolean containsKey(String key);

    /*
     * ȡ�����в������ļ��ϡ�
     * @return ���в������ļ���
     */
    Set<String> keySet();

    /*
     * ȡ�����в����������顣
     * @return ���в�����������
     */
    String[] getKeys();

    // =============================================================
    //  ȡ�ò�����ֵ
    // =============================================================

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>false</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    boolean getBoolean(String key);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    byte getByte(String key);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    byte getByte(String key, byte defaultValue);

    /**
     * ȡ��ָ���������ֽڡ�����ֽ��Ǹ���<code>getCharacterEncoding()</code>�����ص��ַ������б���ġ�
     * 
     * @param key ������
     * @return ����ֵ���ֽ�����
     * @throws UnsupportedEncodingException ���ָ���˴���ı����ַ���
     */
    byte[] getBytes(String key) throws UnsupportedEncodingException;

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>'\0'</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    char getChar(String key);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    char getChar(String key, char defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    double getDouble(String key);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    double getDouble(String key, double defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    float getFloat(String key);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    float getFloat(String key, float defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    int getInt(String key);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    int getInt(String key, int defaultValue);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ������
     */
    int[] getInts(String key);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    int[] getInts(String key, int[] defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    long getLong(String key);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    long getLong(String key, long defaultValue);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ������
     */
    long[] getLongs(String key);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    long[] getLongs(String key, long[] defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    short getShort(String key);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    short getShort(String key, short defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    String getString(String key);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    String getString(String key, String defaultValue);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ������
     */
    String[] getStrings(String key);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    String[] getStrings(String key, String[] defaultValue);

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>null</code>�� �˷�����<code>getString</code>
     * һ��������ģ���б�����ʹ�á�
     * 
     * @param key ������
     * @return ����ֵ
     */
    Object get(String key);

    /**
     * ȡ��ָ��������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    Object getObject(String key);

    /**
     * ȡ��ָ��������ֵ��������������ڣ��򷵻�Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    Object getObject(String key, Object defaultValue);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ������
     */
    Object[] getObjects(String key);

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    Object[] getObjects(String key, Object[] defaultValue);

    /**
     * ȡ�����ڡ��ַ�����ʹ��ָ����<code>DateFormat</code>����������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @param format <code>DateFormat</code>����
     * @return <code>java.util.Date</code>����
     */
    Date getDate(String key, DateFormat format);

    /**
     * ȡ�����ڡ��ַ�����ʹ��ָ����<code>DateFormat</code>����������������ڣ��򷵻�Ĭ��ֵ��
     * 
     * @param key ������
     * @param format <code>DateFormat</code>����
     * @param defaultValue Ĭ��ֵ
     * @return <code>java.util.Date</code>����
     */
    Date getDate(String key, DateFormat format, Date defaultValue);

    /**
     * ȡ��ָ�����͵Ķ���
     */
    <T> T getObjectOfType(String key, Class<T> type);

    /**
     * ȡ��ָ�����͵Ķ���
     */
    <T> T getObjectOfType(String key, Class<T> type, MethodParameter methodParameter, Object[] defaultValues);

    /**
     * �����ݱ��浽object properties�С�
     */
    void setProperties(Object object);

    // =============================================================
    //  ��Ӻ��޸Ĳ����ķ���
    // =============================================================

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void add(String key, boolean value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void add(String key, byte value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void add(String key, char value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void add(String key, double value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void add(String key, float value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void add(String key, int value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void add(String key, long value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void add(String key, short value);

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void add(String key, Object value);

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void setString(String key, String value);

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param key ������
     * @param values ����ֵ������
     */
    void setStrings(String key, String[] values);

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void setObject(String key, Object value);

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    void setObjects(String key, Object[] value);

    // =============================================================
    //  ��������ķ��� 
    // =============================================================

    /**
     * ɾ��ָ�����ƵĲ�����
     * 
     * @return ԭ�Ⱥ�ָ�����ƶ�Ӧ�Ĳ���ֵ��������<code>String[]</code>��<code>null</code>
     */
    Object remove(String key);

    /**
     * �������ֵ��
     */
    void clear();
}
