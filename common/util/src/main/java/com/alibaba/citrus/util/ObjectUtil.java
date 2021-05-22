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
import static com.alibaba.citrus.util.BasicConstant.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * �й�<code>Object</code>����Ĺ����ࡣ
 * <p>
 * ������е�ÿ�����������ԡ���ȫ���ش���<code>null</code>���������׳�<code>NullPointerException</code>��
 * </p>
 * 
 * @author Michael Zhou
 */
public class ObjectUtil {
    // ==========================================================================
    // �пպ����� 
    // ==========================================================================

    /**
     * �Ƿ�Ϊ<code>null</code>�����ַ�����������顣
     */
    public static boolean isEmptyObject(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof String) {
            return StringUtil.isEmpty((String) object);
        } else if (object.getClass().isArray()) {
            return ArrayUtil.isEmptyArray(object);
        } else {
            return false;
        }
    }

    // ==========================================================================
    // Ĭ��ֵ������ 
    //
    // ������Ϊnullʱ��������ת����ָ����Ĭ�϶��� 
    // ==========================================================================

    /**
     * �������Ϊ<code>null</code>���򷵻�ָ��Ĭ�϶��󣬷��򷵻ض�����
     * 
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     * 
     * @param object Ҫ���ԵĶ���
     * @param defaultValue Ĭ��ֵ
     * @return �������Ĭ�϶���
     */
    public static <T, S extends T> T defaultIfNull(T object, S defaultValue) {
        return object == null ? defaultValue : object;
    }

    // ==========================================================================
    // �ȽϺ����� 
    //
    // ���·��������Ƚ����������ֵ�������Ƿ���ͬ��
    // ==========================================================================

    /**
     * �Ƚ����������Ƿ���ȫ��ȡ�
     * <p>
     * �˷���������ȷ�رȽ϶�ά���顣
     * 
     * <pre>
     * ObjectUtil.equals(null, null)                  = true
     * ObjectUtil.equals(null, "")                    = false
     * ObjectUtil.equals("", null)                    = false
     * ObjectUtil.equals("", "")                      = true
     * ObjectUtil.equals(Boolean.TRUE, null)          = false
     * ObjectUtil.equals(Boolean.TRUE, "true")        = false
     * ObjectUtil.equals(Boolean.TRUE, Boolean.TRUE)  = true
     * ObjectUtil.equals(Boolean.TRUE, Boolean.FALSE) = false
     * </pre>
     * 
     * </p>
     * 
     * @param object1 ����1
     * @param object2 ����2
     * @return ������, �򷵻�<code>true</code>
     */
    public static boolean isEquals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }

        if (object1 == null || object2 == null) {
            return false;
        }

        if (!object1.getClass().equals(object2.getClass())) {
            return false;
        }

        if (object1 instanceof Object[]) {
            return Arrays.deepEquals((Object[]) object1, (Object[]) object2);
        } else if (object1 instanceof int[]) {
            return Arrays.equals((int[]) object1, (int[]) object2);
        } else if (object1 instanceof long[]) {
            return Arrays.equals((long[]) object1, (long[]) object2);
        } else if (object1 instanceof short[]) {
            return Arrays.equals((short[]) object1, (short[]) object2);
        } else if (object1 instanceof byte[]) {
            return Arrays.equals((byte[]) object1, (byte[]) object2);
        } else if (object1 instanceof double[]) {
            return Arrays.equals((double[]) object1, (double[]) object2);
        } else if (object1 instanceof float[]) {
            return Arrays.equals((float[]) object1, (float[]) object2);
        } else if (object1 instanceof char[]) {
            return Arrays.equals((char[]) object1, (char[]) object2);
        } else if (object1 instanceof boolean[]) {
            return Arrays.equals((boolean[]) object1, (boolean[]) object2);
        } else {
            return object1.equals(object2);
        }
    }

    /**
     * ������������Ƿ�������ͬ���͡�<code>null</code>���������������͡�
     * 
     * @param object1 ����1
     * @param object2 ����2
     * @return ���������������ͬ�����ͣ��򷵻�<code>true</code>
     */
    public static boolean isSameType(Object object1, Object object2) {
        if (object1 == null || object2 == null) {
            return true;
        }

        return object1.getClass().equals(object2.getClass());
    }

    // ==========================================================================
    // Hash code������ 
    //
    // ���·�������ȡ�ö����hash code�� 
    // ==========================================================================

    /**
     * ȡ�ö����hashֵ, �������Ϊ<code>null</code>, �򷵻�<code>0</code>��
     * <p>
     * �˷���������ȷ�ش����ά���顣
     * </p>
     * 
     * @param object ����
     * @return hashֵ
     */
    public static int hashCode(Object object) {
        if (object == null) {
            return 0;
        } else if (object instanceof Object[]) {
            return Arrays.deepHashCode((Object[]) object);
        } else if (object instanceof int[]) {
            return Arrays.hashCode((int[]) object);
        } else if (object instanceof long[]) {
            return Arrays.hashCode((long[]) object);
        } else if (object instanceof short[]) {
            return Arrays.hashCode((short[]) object);
        } else if (object instanceof byte[]) {
            return Arrays.hashCode((byte[]) object);
        } else if (object instanceof double[]) {
            return Arrays.hashCode((double[]) object);
        } else if (object instanceof float[]) {
            return Arrays.hashCode((float[]) object);
        } else if (object instanceof char[]) {
            return Arrays.hashCode((char[]) object);
        } else if (object instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) object);
        } else {
            return object.hashCode();
        }
    }

    // ==========================================================================
    // ȡ�ö����identity�� 
    // ==========================================================================

    /**
     * ȡ�ö����ԭʼ��hashֵ, �������Ϊ<code>null</code>, �򷵻�<code>0</code>��
     * <p>
     * �÷���ʹ��<code>System.identityHashCode</code>��ȡ��hashֵ����ֵ���ܶ������
     * <code>hashCode</code>������Ӱ�졣
     * </p>
     * 
     * @param object ����
     * @return hashֵ
     */
    public static int identityHashCode(Object object) {
        return object == null ? 0 : System.identityHashCode(object);
    }

    /**
     * ȡ�ö��������identity����ͬ����û�и���<code>toString()</code>����ʱ��
     * <code>Object.toString()</code>��ԭʼ�����
     * 
     * <pre>
     * ObjectUtil.identityToString(null)          = null
     * ObjectUtil.identityToString("")            = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE)  = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0])    = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0]) = "java.lang.Object[]@7fa"
     * </pre>
     * 
     * @param object ����
     * @return �����identity�����������<code>null</code>���򷵻�<code>null</code>
     */
    public static String identityToString(Object object) {
        if (object == null) {
            return null;
        }

        return appendIdentityToString(new StringBuilder(), object).toString();
    }

    /**
     * ȡ�ö��������identity����ͬ����û�и���<code>toString()</code>����ʱ��
     * <code>Object.toString()</code>��ԭʼ�����
     * 
     * <pre>
     * ObjectUtil.identityToString(null, "NULL")            = "NULL"
     * ObjectUtil.identityToString("", "NULL")              = "java.lang.String@1e23"
     * ObjectUtil.identityToString(Boolean.TRUE, "NULL")    = "java.lang.Boolean@7fa"
     * ObjectUtil.identityToString(new int[0], "NULL")      = "int[]@7fa"
     * ObjectUtil.identityToString(new Object[0], "NULL")   = "java.lang.Object[]@7fa"
     * </pre>
     * 
     * @param object ����
     * @param nullStr �������Ϊ<code>null</code>���򷵻ظ��ַ���
     * @return �����identity�����������<code>null</code>���򷵻�ָ���ַ���
     */
    public static String identityToString(Object object, String nullStr) {
        if (object == null) {
            return nullStr;
        }

        return appendIdentityToString(new StringBuilder(), object).toString();
    }

    /**
     * �����������identity������ͬ����û�и���<code>toString()</code>����ʱ��
     * <code>Object.toString()</code>��ԭʼ�������׷�ӵ�<code>Appendable</code>�С�
     * 
     * <pre>
     * ObjectUtil.appendIdentityToString(buf, null)          = null
     * ObjectUtil.appendIdentityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa")
     * ObjectUtil.appendIdentityToString(buf, new int[0])    = buf.append("int[]@7fa")
     * ObjectUtil.appendIdentityToString(buf, new Object[0]) = buf.append("java.lang.Object[]@7fa")
     * </pre>
     * 
     * @param buffer <code>Appendable</code>����
     * @param object ����
     * @return <code>Appendable</code>�����������Ϊ<code>null</code>�������
     *         <code>"null"</code>
     */
    public static <A extends Appendable> A appendIdentityToString(A buffer, Object object) {
        assertNotNull(buffer, "appendable");

        try {
            if (object == null) {
                buffer.append("null");
            } else {
                buffer.append(ClassUtil.getFriendlyClassNameForObject(object));
                buffer.append('@').append(Integer.toHexString(identityHashCode(object)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return buffer;
    }

    // ==========================================================================
    // toString������ 
    // ==========================================================================

    /**
     * ȡ�ö����<code>toString()</code>��ֵ���������Ϊ<code>null</code>���򷵻ؿ��ַ���
     * <code>""</code>��
     * 
     * <pre>
     * ObjectUtil.toString(null)         = ""
     * ObjectUtil.toString("")           = ""
     * ObjectUtil.toString("bat")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE) = "true"
     * ObjectUtil.toString([1, 2, 3])    = "[1, 2, 3]"
     * </pre>
     * 
     * @param object ����
     * @return �����<code>toString()</code>�ķ���ֵ������ַ���<code>""</code>
     */
    public static String toString(Object object) {
        return toString(object, EMPTY_STRING);
    }

    /**
     * ȡ�ö����<code>toString()</code>��ֵ���������Ϊ<code>null</code>���򷵻�ָ���ַ�����
     * 
     * <pre>
     * ObjectUtil.toString(null, null)           = null
     * ObjectUtil.toString(null, "null")         = "null"
     * ObjectUtil.toString("", "null")           = ""
     * ObjectUtil.toString("bat", "null")        = "bat"
     * ObjectUtil.toString(Boolean.TRUE, "null") = "true"
     * ObjectUtil.toString([1, 2, 3], "null")    = "[1, 2, 3]"
     * </pre>
     * 
     * @param object ����
     * @param nullStr �������Ϊ<code>null</code>���򷵻ظ��ַ���
     * @return �����<code>toString()</code>�ķ���ֵ����ָ���ַ���
     */
    public static String toString(Object object, String nullStr) {
        if (object == null) {
            return nullStr;
        } else if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        } else if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        } else if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        } else if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        } else if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        } else if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        } else if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        } else if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        } else if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        } else {
            return object.toString();
        }
    }
}
