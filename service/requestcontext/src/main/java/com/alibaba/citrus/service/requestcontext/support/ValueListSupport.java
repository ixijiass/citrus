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
package com.alibaba.citrus.service.requestcontext.support;

import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.ClassUtil.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;

import com.alibaba.citrus.service.requestcontext.util.ValueList;
import com.alibaba.citrus.util.ArrayUtil;
import com.alibaba.citrus.util.ClassUtil;
import com.alibaba.citrus.util.ObjectUtil;
import com.alibaba.citrus.util.internal.ToStringBuilder;

/**
 * ����һ��ֵ���б�
 * 
 * @author Michael Zhou
 */
public class ValueListSupport implements ValueList {
    private final TypeConverter converter;
    private final List<Object> values = createLinkedList();
    private final boolean quiet;

    public ValueListSupport(TypeConverter converter, boolean quiet) {
        this.converter = assertNotNull(converter, "converter");
        this.quiet = quiet;
    }

    // =============================================================
    //  ȡ�ò�����ֵ 
    // =============================================================

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>false</code>��
     * 
     * @return ����ֵ
     */
    public boolean getBooleanValue() {
        return getBooleanValue(null);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public boolean getBooleanValue(Boolean defaultValue) {
        return getValueOfType(Boolean.class, true, (MethodParameter) null, new Object[] { defaultValue });
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    public byte getByteValue() {
        return getByteValue(null);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public byte getByteValue(Byte defaultValue) {
        return getValueOfType(Byte.class, true, (MethodParameter) null, new Object[] { defaultValue });
    }

    /**
     * ȡ��ָ���������ֽڡ�
     * 
     * @param charset ����ת���ַ��ı���
     * @return ����ֵ���ֽ�����
     * @throws UnsupportedEncodingException ���ָ���˴���ı����ַ���
     */
    public byte[] getBytes(String charset) throws UnsupportedEncodingException {
        String value = getStringValue();
        return value == null ? EMPTY_BYTE_ARRAY : value.getBytes(charset);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>'\0'</code>��
     * 
     * @return ����ֵ
     */
    public char getCharacterValue() {
        return getCharacterValue(null);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public char getCharacterValue(Character defaultValue) {
        return getValueOfType(Character.class, true, (MethodParameter) null, new Object[] { defaultValue });
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    public double getDoubleValue() {
        return getDoubleValue(null);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public double getDoubleValue(Double defaultValue) {
        return getValueOfType(Double.class, true, (MethodParameter) null, new Object[] { defaultValue });
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    public float getFloatValue() {
        return getFloatValue(null);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public float getFloatValue(Float defaultValue) {
        return getValueOfType(Float.class, true, (MethodParameter) null, new Object[] { defaultValue });
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    public int getIntegerValue() {
        return getIntegerValue(null);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public int getIntegerValue(Integer defaultValue) {
        return getValueOfType(Integer.class, true, (MethodParameter) null, new Object[] { defaultValue });
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ������
     */
    public int[] getIntegerValues() {
        return getIntegerValues(EMPTY_INT_ARRAY);
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    public int[] getIntegerValues(int[] defaultValue) {
        return getValueOfType(int[].class, null, toIntegerArray(defaultValue));
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    public long getLongValue() {
        return getLongValue(null);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public long getLongValue(Long defaultValue) {
        return getValueOfType(Long.class, true, (MethodParameter) null, new Object[] { defaultValue });
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ������
     */
    public long[] getLongValues() {
        return getLongValues(EMPTY_LONG_ARRAY);
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    public long[] getLongValues(long[] defaultValue) {
        return getValueOfType(long[].class, null, toLongArray(defaultValue));
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @return ����ֵ
     */
    public short getShortValue() {
        return getShortValue(null);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public short getShortValue(Short defaultValue) {
        return getValueOfType(Short.class, true, (MethodParameter) null, new Object[] { defaultValue });
    }

    /**
     * ȡ�����ڡ��ַ�����ʹ��ָ����<code>DateFormat</code>����������������ڣ��򷵻�<code>null</code>��
     * 
     * @param format <code>DateFormat</code>����
     * @return <code>java.util.Date</code>����
     */
    public Date getDateValue(DateFormat format) {
        return getDateValue(format, null);
    }

    /**
     * ȡ�����ڡ��ַ�����ʹ��ָ����<code>DateFormat</code>����������������ڣ��򷵻�Ĭ��ֵ��
     * 
     * @param format <code>DateFormat</code>����
     * @param defaultValue Ĭ��ֵ
     * @return <code>java.util.Date</code>����
     */
    public Date getDateValue(DateFormat format, Date defaultValue) {
        String value = getStringValue();
        Date date = defaultValue;

        if (value != null) {
            try {
                format.setLenient(false);
                date = format.parse(value);
            } catch (ParseException e) {
                date = defaultValue;
            }
        }

        return date;
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ
     */
    public String getStringValue() {
        return getStringValue(null);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public String getStringValue(String defaultValue) {
        String value = getValueOfType(String.class, null, new Object[] { defaultValue });

        if (value == null || "null".equals(value) || value.length() == 0) {
            value = defaultValue;
        }

        return value;
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ������
     */
    public String[] getStringValues() {
        return getStringValues(EMPTY_STRING_ARRAY);
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    public String[] getStringValues(String[] defaultValue) {
        String[] values = getValueOfType(String[].class, null, defaultValue);

        if (values == null) {
            values = defaultValue;
        } else {
            for (int i = 0; i < values.length; i++) {
                if (values[i] == null || "null".equals(values[i]) || values[i].length() == 0) {
                    values[i] = "";
                }
            }
        }

        return values;
    }

    /**
     * ȡ��<code>FileItem</code>������������ڣ��򷵻�<code>null</code>��
     * 
     * @return <code>FileItem</code>����
     */
    public FileItem getFileItem() {
        Object value = getValue();

        return value instanceof FileItem ? (FileItem) value : null;
    }

    /**
     * ȡ��<code>FileItem</code>������������ڣ��򷵻�<code>null</code>��
     * 
     * @return <code>FileItem</code>���������
     */
    public FileItem[] getFileItems() {
        try {
            return values.toArray(new FileItem[values.size()]);
        } catch (ArrayStoreException e) {
            return new FileItem[0];
        }
    }

    /**
     * ȡ��ָ��������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ
     */
    public Object getValue() {
        return getValue(null);
    }

    /**
     * ȡ��ָ��������ֵ��������������ڣ��򷵻�Ĭ��ֵ��
     * 
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public Object getValue(Object defaultValue) {
        Object value = null;

        if (values.size() > 0) {
            value = values.get(0);
        }

        return ObjectUtil.defaultIfNull(value, defaultValue);
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @return ����ֵ������
     */
    public Object[] getValues() {
        return getValues(EMPTY_OBJECT_ARRAY);
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param defaultValues Ĭ��ֵ
     * @return ����ֵ������
     */
    public Object[] getValues(Object[] defaultValues) {
        Object[] values = this.values.toArray();
        return isEmptyArray(values) ? defaultValues : values;
    }

    /**
     * ȡ��ָ�����͵�ֵ��
     */
    public <T> T getValueOfType(Class<T> type, MethodParameter methodParameter, Object[] defaultValues) {
        return getValueOfType(type, false, methodParameter, defaultValues);
    }

    /**
     * ȡ��ָ�����͵�ֵ��
     */
    public <T> T getValueOfType(Class<T> type, boolean isPrimitive, MethodParameter methodParameter,
                                Object[] defaultValues) {
        // ����Ĭ��ֵ����Ϊ�գ�ת��Ϊ�����顣
        if (defaultValues == null || defaultValues.length == 1 && defaultValues[0] == null) {
            defaultValues = EMPTY_OBJECT_ARRAY;
        }

        // ����primitive���ͣ�ת��ΪϵͳĬ��ֵ��
        if (type.isPrimitive()) {
            isPrimitive = true;
            type = ClassUtil.getWrapperTypeIfPrimitive(type);
        }

        if (isPrimitive && isEmptyArray(defaultValues)) {
            Object defaultValue = getPrimitiveDefaultValue(type);

            if (defaultValue != null) {
                defaultValues = new Object[] { defaultValue };
            }
        }

        // ȡ��ֵ��������������ڣ���ȡĬ��ֵ��
        Object[] values = getValues(defaultValues);

        // ���������[""]ҲȡĬ��ֵ��
        if (values.length == 1 && isEmptyObject(values[0])) {
            values = defaultValues;
        }

        try {
            return convert(type, methodParameter, values, defaultValues.length > 0 ? defaultValues[0] : null);
        } catch (TypeMismatchException e) {
            if (quiet) {
                return convert(type, methodParameter, defaultValues, null);
            } else {
                throw e;
            }
        }
    }

    private <T> T convert(Class<T> type, MethodParameter methodParameter, Object[] values, Object defaultValue) {
        if (values == null) {
            values = EMPTY_STRING_ARRAY;
        }

        Object convertedValue = null;
        boolean requiresArray = type.isArray() || CollectionFactory.isApproximableCollectionType(type);

        if (values.length == 0) {
            if (!type.equals(String.class)) {
                try {
                    convertedValue = converter.convertIfNecessary(values, type, methodParameter);
                } catch (TypeMismatchException e) {
                    // ignored for empty value, just returns null
                }
            }
        } else {
            if (requiresArray) {
                convertedValue = converter.convertIfNecessary(values, type, methodParameter);
            } else {
                Object singleValue = values[0];

                if (isEmptyObject(singleValue)) {
                    singleValue = defaultValue;
                }

                convertedValue = converter.convertIfNecessary(singleValue, type, methodParameter);
            }
        }

        return type.cast(convertedValue);
    }

    private Integer[] toIntegerArray(int[] values) {
        if (isEmptyArray(values)) {
            return EMPTY_INTEGER_OBJECT_ARRAY;
        }

        Integer[] integerValues = new Integer[values.length];

        for (int i = 0; i < values.length; i++) {
            integerValues[i] = values[i];
        }

        return integerValues;
    }

    private Long[] toLongArray(long[] values) {
        if (isEmptyArray(values)) {
            return EMPTY_LONG_OBJECT_ARRAY;
        }

        Long[] longValues = new Long[values.length];

        for (int i = 0; i < values.length; i++) {
            longValues[i] = values[i];
        }

        return longValues;
    }

    // =============================================================
    //  ��Ӻ��޸Ĳ����ķ���
    // =============================================================

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    public void addValue(boolean value) {
        addValue(Boolean.toString(value));
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    public void addValue(byte value) {
        addValue(Byte.toString(value));
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    public void addValue(char value) {
        addValue(Character.toString(value));
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    public void addValue(double value) {
        addValue(Double.toString(value));
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    public void addValue(float value) {
        addValue(Float.toString(value));
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    public void addValue(int value) {
        addValue(Integer.toString(value));
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    public void addValue(long value) {
        addValue(Long.toString(value));
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    public void addValue(short value) {
        addValue(Short.toString(value));
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param value ����ֵ
     */
    public void addValue(Object value) {
        values.add(value);
    }

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param value ����ֵ
     */
    public void setValue(Object value) {
        clear();
        addValue(value);
    }

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param values ����ֵ
     */
    public void setValues(Object[] values) {
        clear();

        if (!ArrayUtil.isEmptyArray(values)) {
            for (Object value : values) {
                addValue(value);
            }
        }
    }

    // =============================================================
    //  ��������
    // =============================================================

    /**
     * ȡ��ֵ�ĸ�����
     * 
     * @return ֵ�ĸ���
     */
    public int size() {
        return values.size();
    }

    /**
     * �������ֵ��
     */
    protected void clear() {
        values.clear();
    }

    /**
     * ȡ���ַ�����ʾ��
     * 
     * @return �ַ�����ʾ
     */
    @Override
    public String toString() {
        ToStringBuilder buf = new ToStringBuilder();

        if (values.size() == 1) {
            buf.append(values.get(0));
        } else {
            buf.appendCollection(values);
        }

        return buf.toString();
    }
}
