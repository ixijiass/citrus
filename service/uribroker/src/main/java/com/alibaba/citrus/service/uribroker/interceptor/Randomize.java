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
package com.alibaba.citrus.service.uribroker.interceptor;

import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.Random;
import java.util.regex.Pattern;

import com.alibaba.citrus.service.uribroker.uri.URIBroker;

/**
 * ��URL���������Ԫ�أ��Է�ֹ���������ҳ�档
 * 
 * @author Michael Zhou
 * @author dux.fangl
 */
public class Randomize implements URIBrokerPathInterceptor {
    private final static String DEFAULT_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
    private final static String DEFAULT_KEY = "r";
    private final static long DEFAULT_RANGE = 100000;
    private final Random random = new Random();
    private boolean initialized;
    private String path;
    private Pattern pathPattern;
    private char[] chars;
    private int radix;
    private double factor;
    private String key;

    /**
     * �����������query ID��
     */
    public void setKey(String key) {
        this.key = defaultIfNull(trimToNull(key), DEFAULT_KEY);
    }

    /**
     * ����������ɵ��ַ���Χ��
     */
    public void setChars(String chars) {
        this.chars = defaultIfNull(trimToNull(chars), DEFAULT_CHARS).toCharArray();
        this.radix = this.chars.length;
    }

    /**
     * �����������Χ��
     */
    public void setRange(long range) {
        if (range == 0L) {
            range = DEFAULT_RANGE;
        }

        this.factor = Math.abs(range / (double) Long.MAX_VALUE);
    }

    /**
     * ����ƥ���·��������ʽ��ֻ��ƥ��ָ��path�Ľ���Ż������������
     */
    public void setPath(String path) {
        this.path = trimToNull(path);
    }

    void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        if (chars == null) {
            setChars(null);
        }

        if (factor == 0.0D) {
            setRange(0L);
        }

        if (key == null) {
            setKey(null);
        }

        if (path != null && pathPattern == null) {
            pathPattern = Pattern.compile(path);
        }
    }

    public void perform(URIBroker broker) {
    }

    public String perform(URIBroker broker, String path) {
        init();

        if (pathPattern == null ? true : pathPattern.matcher(path).matches()) {
            broker.setQueryData(key, longToString(random()));
        }

        return path;
    }

    /**
     * �������������
     */
    protected final long random() {
        return (long) (Math.abs(random.nextLong()) * factor);
    }

    /**
     * ����������������ַ���
     */
    protected final String longToString(long longValue) {
        if (longValue == 0) {
            return String.valueOf(chars[0]);
        }

        if (longValue < 0) {
            longValue = -longValue;
        }

        StringBuilder strValue = new StringBuilder();

        while (longValue != 0) {
            int digit = (int) (longValue % radix);
            longValue = longValue / radix;

            strValue.append(chars[digit]);
        }

        return strValue.reverse().toString();
    }
}
