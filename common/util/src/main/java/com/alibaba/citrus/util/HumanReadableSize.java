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
package com.alibaba.citrus.util;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ���׶��ķ�ʽ����������ʾ�ֽ�����
 * 
 * @author Michael Zhou
 */
public class HumanReadableSize {
    private final static Long ONE = 1L;
    private final static Long ONE_KILO = 1024L;
    private final static Long ONE_MEGA = 1024 * 1024L;
    private final static Long ONE_GIGA = 1024 * 1024 * 1024L;
    private final static Long ONE_TERA = 1024 * 1024 * 1024 * 1024L;
    private final static Map<String, Long> UNIT_NAMES = createHashMap();
    private final static Pattern REGEXP = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(K|M|G|T)?", Pattern.CASE_INSENSITIVE);

    static {
        UNIT_NAMES.put("B", ONE);
        UNIT_NAMES.put("K", ONE_KILO);
        UNIT_NAMES.put("M", ONE_MEGA);
        UNIT_NAMES.put("G", ONE_GIGA);
        UNIT_NAMES.put("T", ONE_TERA);
    }

    /**
     * ���׶����ֽ���ת������ʵ�ֽ�����
     * <ul>
     * <li><code>1</code> ת���� <code>1</code>��</li>
     * <li><code>1K</code> ת���� <code>1024</code>��</li>
     * <li><code>1M</code> ת���� <code>1048576</code>��</li>
     * <li><code>1G</code> ת���� <code>1073741824</code>��</li>
     * <li><code>1T</code> ת���� <code>1099511627776</code>��</li>
     * <li>֧��С���㣬���磺<code>1.5K</code>��</li>
     * </ul>
     */
    public static long parse(String humanReadbleSize) {
        return parse(humanReadbleSize, (String[]) null);
    }

    private static long parse(String humanReadbleSize, String... nas) {
        humanReadbleSize = assertNotNull(trimToNull(humanReadbleSize), "human readble size");

        if (nas != null) {
            for (String na : nas) {
                if (na != null && na.equalsIgnoreCase(humanReadbleSize)) {
                    return -1;
                }
            }
        }

        Matcher matcher = REGEXP.matcher(humanReadbleSize);

        assertTrue(matcher.matches(), "wrong format: %s", humanReadbleSize);

        double size = Double.parseDouble(matcher.group(1));
        String unit = trimToNull(matcher.group(3));

        if (unit != null) {
            size *= UNIT_NAMES.get(unit.toUpperCase());
        }

        return (long) size;
    }

    /**
     * ���ֽ���ȡ������ת�����׶��ĸ�Ҫ�ֽ�����
     * <ul>
     * <li><code>1</code>ת����<code>1</code>��</li>
     * <li><code>1024</code>ת����<code>1K</code>��</li>
     * <li><code>1048576</code>ת����<code>1M</code>��</li>
     * <li><code>1073741824</code>ת����<code>1G</code>��</li>
     * <li><code>1099511627776</code>ת����<code>1T</code>��</li>
     * <li>С��<code>0</code>��һ��ת����<code>n/a</code>��</li>
     * </ul>
     */
    public static String toHumanReadble(long size) {
        if (size < 0) {
            return "n/a";
        }

        DecimalFormat format = new DecimalFormat("#.##");

        if (size / ONE_TERA > 0) {
            return format.format((double) size / ONE_TERA) + "T";
        }

        if (size / ONE_GIGA > 0) {
            return format.format((double) size / ONE_GIGA) + "G";
        }

        if (size / ONE_MEGA > 0) {
            return format.format((double) size / ONE_MEGA) + "M";
        }

        if (size / ONE_KILO > 0) {
            return format.format((double) size / ONE_KILO) + "K";
        }

        return String.valueOf(size);
    }

    private final long value;
    private final String humanReadable;

    public HumanReadableSize(String humanReadable) {
        this.value = parse(humanReadable, "-1", "n/a");
        this.humanReadable = toHumanReadble(this.value);
    }

    public HumanReadableSize(long value) {
        this.value = value < 0 ? -1 : value;
        this.humanReadable = toHumanReadble(this.value);
    }

    public long getValue() {
        return value;
    }

    public String getHumanReadable() {
        return humanReadable;
    }

    @Override
    public int hashCode() {
        return 31 + (int) (value ^ value >>> 32);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof HumanReadableSize) {
            return value == ((HumanReadableSize) obj).value;
        }

        return false;
    }

    @Override
    public String toString() {
        return humanReadable;
    }
}
