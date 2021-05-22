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
package com.alibaba.citrus.util.i18n;

import static com.alibaba.citrus.util.CollectionUtil.*;

import java.util.Map;

import com.alibaba.citrus.util.ClassInstantiationException;
import com.alibaba.citrus.util.ClassLoaderUtil;

/**
 * ����һ���ַ�ת���������磺����������תΪ�������ġ�
 * 
 * @author Michael Zhou
 */
public abstract class CharConverter {
    /** �������ĵ��������ĵ�ת�������ơ� */
    public static final String SIMPLIFIED_TO_TRADITIONAL_CHINESE = "SimplifiedToTraditionalChinese";

    /** �������ĵ��������ĵ�ת�������ơ� */
    public static final String TRADITIONAL_TO_SIMPLIFIED_CHINESE = "TraditionalToSimplifiedChinese";

    // ˽�б���
    private static final Map<String, CharConverter> converters = createConcurrentHashMap();

    /**
     * ȡ��һ��ָ�����Ƶ�ת������
     */
    public static final CharConverter getInstance(String name) {
        CharConverter converter = converters.get(name);

        if (converter == null) {
            CharConverterProvider provider;

            try {
                provider = (CharConverterProvider) ClassLoaderUtil.newServiceInstance("char.converter." + name);
            } catch (ClassInstantiationException e) {
                throw new IllegalArgumentException("Failed to load char converter provider: " + name + ": "
                        + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Failed to load char converter provider: " + name + ": "
                        + e.getMessage());
            }

            converter = provider.createCharConverter();
            converters.put(name, converter);
        }

        return converter;
    }

    /**
     * ת��һ���ַ���
     */
    public abstract char convert(char ch);

    /**
     * ת��һ���ַ�����
     */
    public String convert(CharSequence chars) {
        return convert(chars, 0, chars.length());
    }

    /**
     * ת��һ���ַ�����
     */
    public String convert(CharSequence chars, int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }

        if (count < 0) {
            throw new StringIndexOutOfBoundsException(count);
        }

        int end = offset + count;

        if (end > chars.length()) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }

        StringBuffer buffer = new StringBuffer();

        for (int i = offset; i < end; i++) {
            buffer.append(convert(chars.charAt(i)));
        }

        return buffer.toString();
    }

    /**
     * ת��һ���ַ����飬�����д��ԭ���顣
     */
    public void convert(char[] chars) {
        convert(chars, 0, chars.length);
    }

    /**
     * ת��һ���ַ����飬�����д��ԭ���顣
     * <p>
     * �÷�������ת��ǰ����ַ�������ȵģ����ּ���������д���ת�������ܡ�
     * </p>
     */
    public void convert(char[] chars, int offset, int count) {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }

        if (count < 0) {
            throw new ArrayIndexOutOfBoundsException(count);
        }

        int end = offset + count;

        if (end > chars.length) {
            throw new ArrayIndexOutOfBoundsException(offset + count);
        }

        for (int i = offset; i < end; i++) {
            chars[i] = convert(chars[i]);
        }
    }
}
