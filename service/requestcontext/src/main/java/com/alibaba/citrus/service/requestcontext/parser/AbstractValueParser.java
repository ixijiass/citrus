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

import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.core.MethodParameter;

import com.alibaba.citrus.service.requestcontext.support.ValueListSupport;
import com.alibaba.citrus.service.requestcontext.util.ValueList;
import com.alibaba.citrus.service.upload.support.StringFileItemEditor;
import com.alibaba.citrus.util.ObjectUtil;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ����һ���������Ļ��࣬����ȡ��HTTP�����еĲ�����cookies��
 * <p>
 * ע�⣺������cookie�����ƿ��ܱ�ת����ȫ����д��ȫ��Сд�� ���Ǹ��������ļ��еĲ�����<code>caseFolding</code> ��ָ���ġ�
 * </p>
 */
public abstract class AbstractValueParser implements ValueParser {
    protected final SimpleTypeConverter converter;
    protected final Map<String, Object> parameters = createLinkedHashMap();
    protected final Map<String, String> parameterKeys = createLinkedHashMap();
    protected final ParserRequestContext requestContext;

    public AbstractValueParser(ParserRequestContext requestContext) {
        this.requestContext = requestContext;
        this.converter = new SimpleTypeConverter();
        this.converter.registerCustomEditor(String.class, new StringFileItemEditor());

        if (requestContext.getPropertyEditorRegistrar() != null) {
            requestContext.getPropertyEditorRegistrar().registerCustomEditors(converter);
        }
    }

    protected abstract Logger getLogger();

    public TypeConverter getTypeConverter() {
        return converter;
    }

    // =============================================================
    //  ��ѯ�����ķ��� 
    // =============================================================

    /**
     * ȡ��ֵ��������
     * 
     * @return ֵ������
     */
    public int size() {
        return parameters.size();
    }

    /**
     * �ж��Ƿ���ֵ��
     * 
     * @return �����ֵ���򷵻�<code>true</code>
     */
    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    /**
     * ����Ƿ����ָ�����ƵĲ�����
     * 
     * @param key Ҫ���ҵĲ�����
     * @return ������ڣ��򷵻�<code>true</code>
     */
    public boolean containsKey(String key) {
        return parameters.containsKey(convert(key));
    }

    /*
     * ȡ�����в������ļ��ϡ�
     * @return ���в������ļ���
     */
    public Set<String> keySet() {
        return createLinkedHashSet(parameterKeys.values());
    }

    /*
     * ȡ�����в����������顣
     * @return ���в�����������
     */
    public String[] getKeys() {
        return parameterKeys.values().toArray(new String[parameterKeys.size()]);
    }

    // =============================================================
    //  ȡ�ò�����ֵ
    // =============================================================

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>false</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public boolean getBoolean(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? false : container.getBooleanValue();
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getBooleanValue(defaultValue);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public byte getByte(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? 0 : container.getByteValue();
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public byte getByte(String key, byte defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getByteValue(defaultValue);
    }

    /**
     * ȡ��ָ���������ֽڡ�����ֽ��Ǹ���<code>getCharacterEncoding()</code>�����ص��ַ������б���ġ�
     * 
     * @param key ������
     * @return ����ֵ���ֽ����飬������������ڣ��򷵻�<code>null</code>
     * @throws UnsupportedEncodingException ���ָ���˴���ı����ַ���
     */
    public byte[] getBytes(String key) throws UnsupportedEncodingException {
        ValueList container = getValueList(key, false);
        return container == null ? EMPTY_BYTE_ARRAY : container.getBytes(getCharacterEncoding());
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>'\0'</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public char getChar(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? '\0' : container.getCharacterValue();
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public char getChar(String key, char defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getCharacterValue(defaultValue);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public double getDouble(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? 0 : container.getDoubleValue();
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public double getDouble(String key, double defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getDoubleValue(defaultValue);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public float getFloat(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? 0 : container.getFloatValue();
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public float getFloat(String key, float defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getFloatValue(defaultValue);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public int getInt(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? 0 : container.getIntegerValue();
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public int getInt(String key, int defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getIntegerValue(defaultValue);
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ������
     */
    public int[] getInts(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? EMPTY_INT_ARRAY : container.getIntegerValues();
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    public int[] getInts(String key, int[] defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getIntegerValues(defaultValue);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public long getLong(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? 0 : container.getLongValue();
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public long getLong(String key, long defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getLongValue(defaultValue);
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ������
     */
    public long[] getLongs(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? EMPTY_LONG_ARRAY : container.getLongValues();
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    public long[] getLongs(String key, long[] defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getLongValues(defaultValue);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>0</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public short getShort(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? 0 : container.getShortValue();
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public short getShort(String key, short defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getShortValue(defaultValue);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public String getString(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? null : container.getStringValue();
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public String getString(String key, String defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getStringValue(defaultValue);
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ������
     */
    public String[] getStrings(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? EMPTY_STRING_ARRAY : container.getStringValues();
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    public String[] getStrings(String key, String[] defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getStringValues(defaultValue);
    }

    /**
     * ȡ�ò���ֵ�����ָ�����ƵĲ��������ڣ��򷵻�<code>null</code>�� �˷�����<code>getObject</code>
     * һ��������ģ���б�����ʹ�á�
     * 
     * @param key ������
     * @return ����ֵ
     */
    public Object get(String key) {
        return getObject(key);
    }

    /**
     * ȡ�����ڡ��ַ�����ʹ��ָ����<code>DateFormat</code>����������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @param format <code>DateFormat</code>����
     * @return <code>java.util.Date</code>����
     */
    public Date getDate(String key, DateFormat format) {
        ValueList container = getValueList(key, false);
        return container == null ? null : container.getDateValue(format);
    }

    /**
     * ȡ�����ڡ��ַ�����ʹ��ָ����<code>DateFormat</code>����������������ڣ��򷵻�Ĭ��ֵ��
     * 
     * @param key ������
     * @param format <code>DateFormat</code>����
     * @param defaultValue Ĭ��ֵ
     * @return <code>java.util.Date</code>����
     */
    public Date getDate(String key, DateFormat format, Date defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getDateValue(format, defaultValue);
    }

    /**
     * ȡ��ָ��������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ
     */
    public Object getObject(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? null : container.getValue();
    }

    /**
     * ȡ��ָ��������ֵ��������������ڣ��򷵻�Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ
     */
    public Object getObject(String key, Object defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getValue(defaultValue);
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return ����ֵ������
     */
    public Object[] getObjects(String key) {
        ValueList container = getValueList(key, false);
        return container == null ? EMPTY_OBJECT_ARRAY : container.getValues();
    }

    /**
     * ȡ��ָ������������ֵ��������������ڣ��򷵻�ָ��Ĭ��ֵ��
     * 
     * @param key ������
     * @param defaultValue Ĭ��ֵ
     * @return ����ֵ������
     */
    public Object[] getObjects(String key, Object[] defaultValue) {
        ValueList container = getValueList(key, false);
        return container == null ? defaultValue : container.getValues(defaultValue);
    }

    /**
     * ȡ��ָ�����͵Ķ���
     */
    public <T> T getObjectOfType(String key, Class<T> type) {
        return getObjectOfType(key, type, null, null);
    }

    /**
     * ȡ��ָ�����͵Ķ���
     */
    public <T> T getObjectOfType(String key, Class<T> type, MethodParameter methodParameter, Object[] defaultValues) {
        return getObjectOfType(key, type, false, methodParameter, defaultValues);
    }

    /**
     * ȡ��ָ�����͵Ķ���
     */
    <T> T getObjectOfType(String key, Class<T> type, boolean isPrimitive, MethodParameter methodParameter,
                          Object[] defaultValues) {
        ValueList container = getValueList(key, false);

        if (container == null) {
            container = new ValueListSupport(getTypeConverter(), requestContext.isConverterQuiet());

            if (!isEmptyArray(defaultValues)) {
                for (Object value : defaultValues) {
                    container.addValue(value);
                }
            }

            defaultValues = null;
        }

        return container.getValueOfType(type, isPrimitive, methodParameter, defaultValues);
    }

    public void setProperties(Object object) {
        if (object == null) {
            return;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Set HTTP request parameters to object " + ObjectUtil.identityToString(object));
        }

        BeanWrapper bean = new BeanWrapperImpl(object);
        requestContext.getPropertyEditorRegistrar().registerCustomEditors(bean);

        for (String key : keySet()) {
            String propertyName = StringUtil.toCamelCase(key);

            if (bean.isWritableProperty(propertyName)) {
                PropertyDescriptor pd = bean.getPropertyDescriptor(propertyName);
                MethodParameter mp = BeanUtils.getWriteMethodParameter(pd);
                Object value = getObjectOfType(key, pd.getPropertyType(), mp, null);

                bean.setPropertyValue(propertyName, value);
            } else {
                getLogger().debug("No writable property \"{}\" found in type {}", propertyName,
                        object.getClass().getName());
            }
        }
    }

    // =============================================================
    //  ��Ӻ��޸Ĳ����ķ���
    // =============================================================

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void add(String key, boolean value) {
        getValueList(key, true).addValue(value);
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void add(String key, byte value) {
        getValueList(key, true).addValue(value);
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void add(String key, char value) {
        getValueList(key, true).addValue(value);
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void add(String key, double value) {
        getValueList(key, true).addValue(value);
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void add(String key, float value) {
        getValueList(key, true).addValue(value);
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void add(String key, int value) {
        getValueList(key, true).addValue(value);
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void add(String key, long value) {
        getValueList(key, true).addValue(value);
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void add(String key, short value) {
        getValueList(key, true).addValue(value);
    }

    /**
     * ��Ӳ�����/����ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void add(String key, Object value) {
        getValueList(key, true).addValue(value);
    }

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void setString(String key, String value) {
        setObject(key, value);
    }

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param key ������
     * @param values ����ֵ������
     */
    public void setStrings(String key, String[] values) {
        setObjects(key, values);
    }

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param key ������
     * @param value ����ֵ
     */
    public void setObject(String key, Object value) {
        getValueList(key, true).setValue(value);
    }

    /**
     * ���ò���ֵ����<code>add</code>������ͬ���˷���������ԭ�е�ֵ��
     * 
     * @param key ������
     * @param values ����ֵ
     */
    public void setObjects(String key, Object[] values) {
        getValueList(key, true).setValues(values);
    }

    // =============================================================
    //  ��������ķ���
    // =============================================================

    /**
     * ɾ��ָ�����ƵĲ�����
     * 
     * @return ԭ�Ⱥ�ָ�����ƶ�Ӧ�Ĳ���ֵ��������<code>String[]</code>��<code>null</code>
     */
    public Object remove(String key) {
        key = convert(key);
        parameterKeys.remove(key);
        return parameters.remove(key);
    }

    /**
     * �������ֵ��
     */
    public void clear() {
        parameterKeys.clear();
        parameters.clear();
    }

    // =============================================================
    //  ��������
    // =============================================================

    /**
     * ���Ƚ�����������<code>trim()</code>��Ȼ���ٽ��д�Сдת����ת���Ǹ��������ļ��е�
     * <code>url.case.folding</code>���趨�ġ�
     * 
     * @param key Ҫת���Ĳ�����
     * @return ��<code>trim()</code>�ʹ�Сдת����Ĳ������������<code>null</code>����ת���ɿ��ַ���
     */
    protected String convert(String key) {
        if (requestContext == null) {
            return key;
        }

        return requestContext.convertCase(key);
    }

    /**
     * ȡ��ָ��������ֵ���б�
     * 
     * @param key ������
     * @param create ������������ڣ��Ƿ񴴽�֮
     * @return ����ֵ���б�������������ڣ���<code>create==false</code>���򷵻�<code>null</code>
     */
    protected ValueList getValueList(String key, boolean create) {
        String originalKey = key;

        key = convert(key);

        ValueList container = (ValueList) parameters.get(key);

        if (create) {
            if (container == null) {
                container = new ValueListSupport(getTypeConverter(), requestContext.isConverterQuiet());
                parameterKeys.put(key, originalKey);
                parameters.put(key, container);
            }

            return container;
        } else {
            if (container == null || container.size() == 0) {
                return null;
            } else {
                return container;
            }
        }
    }

    /**
     * ȡ�����ڽ��������ı����ַ�������ͬ��ʵ��ȡ�ñ����ַ����ķ���Ҳ��ͬ�����磬����<code>ParameterParser</code>��
     * �˱����ַ�������<code>request.getCharacterEncoding()</code>�����ġ�
     * <p>
     * Ĭ�����Ƿ���<code>ISO-8859-1</code>��
     * </p>
     * 
     * @return �����ַ���
     */
    protected String getCharacterEncoding() {
        return ParserRequestContext.DEFAULT_CHARSET_ENCODING;
    }

    /**
     * ת�����ַ�����
     * 
     * @return �ַ�������
     */
    @Override
    public String toString() {
        MapBuilder mb = new MapBuilder().setSortKeys(true);

        for (String key : parameterKeys.values()) {
            mb.append(key, getValueList(key, false));
        }

        return mb.toString();
    }
}
