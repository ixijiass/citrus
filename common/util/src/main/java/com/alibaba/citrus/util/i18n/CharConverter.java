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
 * 代表一个字符转换器。例如：将简体中文转为繁体中文。
 * 
 * @author Michael Zhou
 */
public abstract class CharConverter {
    /** 简体中文到繁体中文的转换器名称。 */
    public static final String SIMPLIFIED_TO_TRADITIONAL_CHINESE = "SimplifiedToTraditionalChinese";

    /** 繁体中文到简体中文的转换器名称。 */
    public static final String TRADITIONAL_TO_SIMPLIFIED_CHINESE = "TraditionalToSimplifiedChinese";

    // 私有变量
    private static final Map<String, CharConverter> converters = createConcurrentHashMap();

    /**
     * 取得一个指定名称的转换器。
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
     * 转换一个字符。
     */
    public abstract char convert(char ch);

    /**
     * 转换一个字符串。
     */
    public String convert(CharSequence chars) {
        return convert(chars, 0, chars.length());
    }

    /**
     * 转换一个字符串。
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
     * 转换一个字符数组，将结果写入原数组。
     */
    public void convert(char[] chars) {
        convert(chars, 0, chars.length);
    }

    /**
     * 转换一个字符数组，将结果写入原数组。
     * <p>
     * 该方法假设转换前后的字符数是相等的，这种假设有利于写提高转换的性能。
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
