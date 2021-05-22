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

import java.io.Serializable;

/**
 * ����������
 * 
 * @author Michael Zhou
 */
public final class BasicConstant {
    // =============================================================
    //  ���鳣��
    // =============================================================

    // primitive arrays

    /** �յ�<code>byte</code>���顣 */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];

    /** �յ�<code>short</code>���顣 */
    public static final short[] EMPTY_SHORT_ARRAY = new short[0];
    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];

    /** �յ�<code>int</code>���顣 */
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];

    /** �յ�<code>long</code>���顣 */
    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];

    /** �յ�<code>float</code>���顣 */
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];

    /** �յ�<code>double</code>���顣 */
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];

    /** �յ�<code>char</code>���顣 */
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];

    /** �յ�<code>boolean</code>���顣 */
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];

    // object arrays

    /** �յ�<code>Object</code>���顣 */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /** �յ�<code>Class</code>���顣 */
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

    /** �յ�<code>String</code>���顣 */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    // =============================================================
    //  ������
    // =============================================================

    // 0-valued primitive wrappers
    public static final Byte BYTE_ZERO = new Byte((byte) 0);
    public static final Short SHORT_ZERO = new Short((short) 0);
    public static final Integer INT_ZERO = new Integer(0);
    public static final Long LONG_ZERO = new Long(0L);
    public static final Float FLOAT_ZERO = new Float(0);
    public static final Double DOUBLE_ZERO = new Double(0);
    public static final Character CHAR_NULL = new Character('\0');
    public static final Boolean BOOL_FALSE = Boolean.FALSE;

    /** ����nullֵ��ռλ���� */
    public static final Object NULL_PLACEHOLDER = new NullPlaceholder();

    private final static class NullPlaceholder implements Serializable {
        private static final long serialVersionUID = 7092611880189329093L;

        @Override
        public String toString() {
            return "null";
        }

        private Object readResolve() {
            return NULL_PLACEHOLDER;
        }
    }

    /** ���ַ����� */
    public static final String EMPTY_STRING = "";
}