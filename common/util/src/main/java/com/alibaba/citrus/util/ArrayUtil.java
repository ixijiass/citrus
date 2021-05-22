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

import java.util.Iterator;
import java.util.Map;

/**
 * �й����鴦��Ĺ����ࡣ
 * <p>
 * ������е�ÿ�����������ԡ���ȫ���ش���<code>null</code>���������׳�<code>NullPointerException</code>��
 * </p>
 * <p>
 * ���������Ƕ�JDK <code>Arrays</code>�Ĳ��䡣
 * </p>
 * 
 * @author Michael Zhou
 */
public class ArrayUtil {
    // ==========================================================================
    // ȡ�����鳤�ȡ� 
    // ==========================================================================

    /**
     * ȡ������ĳ��ȡ�
     * <p>
     * �˷�����<code>Array.getLength()</code>Ҫ��öࡣ
     * </p>
     * 
     * @param array Ҫ��������
     * @return ���Ϊ�գ����߷����飬�򷵻�<code>0</code>��
     */
    public static int arrayLength(Object array) {
        return arrayLength(array, 0, 0);
    }

    private static int arrayLength(Object array, int defaultIfNull, int defaultIfNotArray) {
        if (array == null) {
            return defaultIfNull; // null
        } else if (array instanceof Object[]) {
            return ((Object[]) array).length;
        } else if (array instanceof long[]) {
            return ((long[]) array).length;
        } else if (array instanceof int[]) {
            return ((int[]) array).length;
        } else if (array instanceof short[]) {
            return ((short[]) array).length;
        } else if (array instanceof byte[]) {
            return ((byte[]) array).length;
        } else if (array instanceof double[]) {
            return ((double[]) array).length;
        } else if (array instanceof float[]) {
            return ((float[]) array).length;
        } else if (array instanceof boolean[]) {
            return ((boolean[]) array).length;
        } else if (array instanceof char[]) {
            return ((char[]) array).length;
        } else {
            return defaultIfNotArray; // not an array
        }
    }

    // ==========================================================================
    // �пպ�����                                                                  
    //  
    // �ж�һ�������Ƿ�Ϊnull�����0��Ԫ�ء�                                       
    // ==========================================================================

    /**
     * ��������Ƿ�Ϊ<code>null</code>�������<code>[]</code>��
     * 
     * <pre>
     * ArrayUtil.isEmptyArray(null)              = true
     * ArrayUtil.isEmptyArray(new int[0])        = true
     * ArrayUtil.isEmptyArray(new int[10])       = false
     * </pre>
     * 
     * @param array Ҫ��������
     * @return ���Ϊ��, �򷵻�<code>true</code>
     */
    public static boolean isEmptyArray(Object array) {
        return arrayLength(array, 0, -1) == 0;
    }

    // ==========================================================================
    // Ĭ��ֵ������ 
    //  
    // ������Ϊ��ʱ��ȡ��Ĭ������ֵ��
    // ע���ж�����Ϊnullʱ�����ø�ͨ�õ�ObjectUtil.defaultIfNull��
    // ==========================================================================

    /**
     * ���������<code>null</code>�������<code>[]</code>���򷵻�ָ������Ĭ��ֵ��
     * 
     * <pre>
     * ArrayUtil.defaultIfEmpty(null, defaultArray)           = defaultArray
     * ArrayUtil.defaultIfEmpty(new String[0], defaultArray)  = ���鱾��
     * ArrayUtil.defaultIfEmpty(new String[10], defaultArray) = ���鱾��
     * </pre>
     * 
     * @param array Ҫת��������
     * @param defaultArray Ĭ������
     * @return ���鱾���Ĭ������
     */
    public static <T, S extends T> T defaultIfEmptyArray(T array, S defaultValue) {
        return isEmptyArray(array) ? defaultValue : array;
    }

    // ==========================================================================
    // ������ת���ɼ����ࡣ                                                        
    // ==========================================================================

    /**
     * ������ת����<code>Iterable</code>�б�
     * <p>
     * �����������Ϊ<code>null</code>�������������顣
     * </p>
     * <p>
     * �÷������ص�<code>Iterable</code>��������������Ч�ģ����������������Ŀ����������ʹ��
     * <code>CollectionUtil.createArrayList(asIterable(componentType, array))</code>��
     * <code>CollectionUtil.createLinkedList(asIterable(componentType, array))</code>
     * ����һ���� <code>Iterable</code>ת����ָ�����͵� <code>List</code>����
     * </p>
     * 
     * @param componentType <code>Iterable</code>Ԫ�ص����ͣ�������������ͼ��ݡ��������
     *            <code>int[]</code>���飬 <code>componentType</code>����Ϊ
     *            <code>Integer.class</code>��
     * @param array Ҫת��������
     * @return ��������<code>Iterable</code>����
     */
    public static <T> Iterable<T> arrayAsIterable(final Class<T> componentType, Object array) {
        assertNotNull(componentType, "componentType");

        if (array == null) {
            return new ArrayIterable<T>(0) {
                @Override
                protected T get(int i) {
                    unreachableCode();
                    return null;
                }
            };
        } else if (array instanceof Object[]) {
            final Object[] objectArray = (Object[]) array;

            return new ArrayIterable<T>(objectArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(objectArray[i]);
                }
            };
        } else if (array instanceof int[]) {
            final int[] intArray = (int[]) array;

            return new ArrayIterable<T>(intArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(intArray[i]);
                }
            };
        } else if (array instanceof long[]) {
            final long[] longArray = (long[]) array;

            return new ArrayIterable<T>(longArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(longArray[i]);
                }
            };

        } else if (array instanceof short[]) {
            final short[] shortArray = (short[]) array;

            return new ArrayIterable<T>(shortArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(shortArray[i]);
                }
            };
        } else if (array instanceof byte[]) {
            final byte[] byteArray = (byte[]) array;

            return new ArrayIterable<T>(byteArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(byteArray[i]);
                }
            };
        } else if (array instanceof double[]) {
            final double[] doubleArray = (double[]) array;

            return new ArrayIterable<T>(doubleArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(doubleArray[i]);
                }
            };
        } else if (array instanceof float[]) {
            final float[] floatArray = (float[]) array;

            return new ArrayIterable<T>(floatArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(floatArray[i]);
                }
            };
        } else if (array instanceof boolean[]) {
            final boolean[] booleanArray = (boolean[]) array;

            return new ArrayIterable<T>(booleanArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(booleanArray[i]);
                }
            };
        } else if (array instanceof char[]) {
            final char[] charArray = (char[]) array;

            return new ArrayIterable<T>(charArray.length) {
                @Override
                protected T get(int i) {
                    return componentType.cast(charArray[i]);
                }
            };
        } else {
            throw new IllegalArgumentException(array + " is not an array");
        }
    }

    private static abstract class ArrayIterable<T> implements Iterable<T> {
        private final int length;

        public ArrayIterable(int length) {
            this.length = length;
        }

        public final Iterator<T> iterator() {
            return new Iterator<T>() {
                private int index;

                public final boolean hasNext() {
                    return index < length;
                }

                public final T next() {
                    if (index >= length) {
                        throw new ArrayIndexOutOfBoundsException(index);
                    }

                    return get(index++);
                }

                public final void remove() {
                    unsupportedOperation("remove");
                }
            };
        }

        protected abstract T get(int i);
    }

    /**
     * ������ת����<code>Map</code>�������Ԫ�ر�����Ԫ�ظ�������2�������顣
     * 
     * <pre>
     * Map colorMap = ArrayUtil.toMap(new String[][] {{
     *     {"RED", 0xFF0000},
     *     {"GREEN", 0x00FF00},
     *     {"BLUE", 0x0000FF}}, String.class, Integer.class);
     * </pre>
     * 
     * @param keyValueArray Ҫת��������
     * @param keyType key�����ͣ�����Ԫ��<code>keyValueArray[n][0]</code>�����ͱ�����֮����
     * @param valueType value�����ͣ�����Ԫ��<code>keyValueArray[n][1]</code>�����ͱ�����֮����
     * @return ��������map
     */
    public static <K, V> Map<K, V> arrayToMap(Object[][] keyValueArray, Class<K> keyType, Class<V> valueType) {
        return arrayToMap(keyValueArray, keyType, valueType, null);
    }

    /**
     * ������ת����<code>Map</code>�������Ԫ�ر�����Ԫ�ظ�������2�������顣
     * 
     * <pre>
     * Map colorMap = ArrayUtil.toMap(new String[][] {{
     *     {"RED", 0xFF0000},
     *     {"GREEN", 0x00FF00},
     *     {"BLUE", 0x0000FF}}, String.class, Integer.class, map);
     * </pre>
     * 
     * @param keyValueArray Ҫת��������
     * @param keyType key�����ͣ�����Ԫ��<code>keyValueArray[n][0]</code>�����ͱ�����֮����
     * @param valueType value�����ͣ�����Ԫ��<code>keyValueArray[n][1]</code>�����ͱ�����֮����
     * @param map Ҫ����map�����Ϊ<code>null</code>���Զ�����֮
     * @return ������������map
     */
    public static <K, V> Map<K, V> arrayToMap(Object[][] keyValueArray, Class<K> keyType, Class<V> valueType,
                                              Map<K, V> map) {
        assertNotNull(keyType, "keyType");
        assertNotNull(valueType, "valueType");

        if (keyValueArray == null) {
            return map;
        }

        if (map == null) {
            map = createLinkedHashMap((int) (keyValueArray.length * 1.5));
        }

        for (int i = 0; i < keyValueArray.length; i++) {
            Object[] keyValue = keyValueArray[i];
            Object[] entry = keyValue;

            if (entry == null || entry.length < 2) {
                throw new IllegalArgumentException("Array element " + i + " is not an array of 2 elements");
            }

            map.put(keyType.cast(entry[0]), valueType.cast(entry[1]));
        }

        return map;
    }

    // ==========================================================================
    // �Ƚ�����ĳ��ȡ�                                                            
    // ==========================================================================

    /**
     * �ж����������Ƿ������ͬ�ĳ��ȡ��������Ϊ<code>null</code>�򱻿�������Ϊ<code>0</code>��
     * 
     * @param array1 ����1
     * @param array2 ����2
     * @return ����������鳤����ͬ���򷵻�<code>true</code>
     */
    public static boolean isArraySameLength(Object[] array1, Object[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * �ж����������Ƿ������ͬ�ĳ��ȡ��������Ϊ<code>null</code>�򱻿�������Ϊ<code>0</code>��
     * 
     * @param array1 ����1
     * @param array2 ����2
     * @return ����������鳤����ͬ���򷵻�<code>true</code>
     */
    public static boolean isArraySameLength(long[] array1, long[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * �ж����������Ƿ������ͬ�ĳ��ȡ��������Ϊ<code>null</code>�򱻿�������Ϊ<code>0</code>��
     * 
     * @param array1 ����1
     * @param array2 ����2
     * @return ����������鳤����ͬ���򷵻�<code>true</code>
     */
    public static boolean isArraySameLength(int[] array1, int[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * �ж����������Ƿ������ͬ�ĳ��ȡ��������Ϊ<code>null</code>�򱻿�������Ϊ<code>0</code>��
     * 
     * @param array1 ����1
     * @param array2 ����2
     * @return ����������鳤����ͬ���򷵻�<code>true</code>
     */
    public static boolean isArraySameLength(short[] array1, short[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * �ж����������Ƿ������ͬ�ĳ��ȡ��������Ϊ<code>null</code>�򱻿�������Ϊ<code>0</code>��
     * 
     * @param array1 ����1
     * @param array2 ����2
     * @return ����������鳤����ͬ���򷵻�<code>true</code>
     */
    public static boolean isArraySameLength(byte[] array1, byte[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * �ж����������Ƿ������ͬ�ĳ��ȡ��������Ϊ<code>null</code>�򱻿�������Ϊ<code>0</code>��
     * 
     * @param array1 ����1
     * @param array2 ����2
     * @return ����������鳤����ͬ���򷵻�<code>true</code>
     */
    public static boolean isArraySameLength(double[] array1, double[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * �ж����������Ƿ������ͬ�ĳ��ȡ��������Ϊ<code>null</code>�򱻿�������Ϊ<code>0</code>��
     * 
     * @param array1 ����1
     * @param array2 ����2
     * @return ����������鳤����ͬ���򷵻�<code>true</code>
     */
    public static boolean isArraySameLength(float[] array1, float[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * �ж����������Ƿ������ͬ�ĳ��ȡ��������Ϊ<code>null</code>�򱻿�������Ϊ<code>0</code>��
     * 
     * @param array1 ����1
     * @param array2 ����2
     * @return ����������鳤����ͬ���򷵻�<code>true</code>
     */
    public static boolean isArraySameLength(boolean[] array1, boolean[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    /**
     * �ж����������Ƿ������ͬ�ĳ��ȡ��������Ϊ<code>null</code>�򱻿�������Ϊ<code>0</code>��
     * 
     * @param array1 ����1
     * @param array2 ����2
     * @return ����������鳤����ͬ���򷵻�<code>true</code>
     */
    public static boolean isArraySameLength(char[] array1, char[] array2) {
        int length1 = array1 == null ? 0 : array1.length;
        int length2 = array2 == null ? 0 : array2.length;

        return length1 == length2;
    }

    // ==========================================================================
    // ��ת�����Ԫ��˳��                                                        
    // ==========================================================================

    /**
     * ��ת�����Ԫ��˳���������Ϊ<code>null</code>����ʲôҲ������
     * 
     * @param array Ҫ��ת������
     */
    public static void arrayReverse(Object[] array) {
        if (array == null) {
            return;
        }

        Object tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * ��ת�����Ԫ��˳���������Ϊ<code>null</code>����ʲôҲ������
     * 
     * @param array Ҫ��ת������
     */
    public static void arrayReverse(long[] array) {
        if (array == null) {
            return;
        }

        long tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * ��ת�����Ԫ��˳���������Ϊ<code>null</code>����ʲôҲ������
     * 
     * @param array Ҫ��ת������
     */
    public static void arrayReverse(int[] array) {
        if (array == null) {
            return;
        }

        int tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * ��ת�����Ԫ��˳���������Ϊ<code>null</code>����ʲôҲ������
     * 
     * @param array Ҫ��ת������
     */
    public static void arrayReverse(short[] array) {
        if (array == null) {
            return;
        }

        short tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * ��ת�����Ԫ��˳���������Ϊ<code>null</code>����ʲôҲ������
     * 
     * @param array Ҫ��ת������
     */
    public static void arrayReverse(byte[] array) {
        if (array == null) {
            return;
        }

        byte tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * ��ת�����Ԫ��˳���������Ϊ<code>null</code>����ʲôҲ������
     * 
     * @param array Ҫ��ת������
     */
    public static void arrayReverse(double[] array) {
        if (array == null) {
            return;
        }

        double tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * ��ת�����Ԫ��˳���������Ϊ<code>null</code>����ʲôҲ������
     * 
     * @param array Ҫ��ת������
     */
    public static void arrayReverse(float[] array) {
        if (array == null) {
            return;
        }

        float tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * ��ת�����Ԫ��˳���������Ϊ<code>null</code>����ʲôҲ������
     * 
     * @param array Ҫ��ת������
     */
    public static void arrayReverse(boolean[] array) {
        if (array == null) {
            return;
        }

        boolean tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    /**
     * ��ת�����Ԫ��˳���������Ϊ<code>null</code>����ʲôҲ������
     * 
     * @param array Ҫ��ת������
     */
    public static void arrayReverse(char[] array) {
        if (array == null) {
            return;
        }

        char tmp;

        for (int i = 0, j = array.length - 1; j > i; i++, j--) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    // ==========================================================================
    // �������в���һ��Ԫ�ػ�һ��Ԫ�����С�                                        
    //  
    // ���ͣ�Object[]                                                              
    // ==========================================================================

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param objectToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(Object[] array, Object objectToFind) {
        return arrayIndexOf(array, objectToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(Object[] array, Object[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param objectToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(Object[] array, Object[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        Object first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // ���ҵ�һ��Ԫ��
            while (i <= max && !ObjectUtil.isEquals(array[i], first)) {
                i++;
            }

            if (i > max) {
                return -1;
            }

            // �Ѿ��ҵ���һ��Ԫ�أ�������
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (!ObjectUtil.isEquals(array[j++], arrayToFind[k++])) {
                    i++;

                    // ���²��ҵ�һ��Ԫ��
                    continue startSearchForFirst;
                }
            }

            // �ҵ���
            return i;
        }
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param objectToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(Object[] array, Object objectToFind) {
        return arrayLastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(Object[] array, Object[] arrayToFind) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param objectToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            return -1;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        if (objectToFind == null) {
            for (int i = startIndex; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = startIndex; i >= 0; i--) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(Object[] array, Object[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return -1;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        Object last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && !ObjectUtil.isEquals(array[i], last)) {
                i--;
            }

            if (i < min) {
                return -1;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (!ObjectUtil.isEquals(array[j--], arrayToFind[k--])) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param objectToFind Ҫ���ҵ�Ԫ��
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(Object[] array, Object objectToFind) {
        return arrayIndexOf(array, objectToFind) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(Object[] array, Object[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind) != -1;
    }

    // ==========================================================================
    // �������в���һ��Ԫ�ػ�һ��Ԫ�����С�                                        
    //  
    // ���ͣ�long[]                                                                
    // ==========================================================================

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param longToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(long[] array, long longToFind) {
        return arrayIndexOf(array, longToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(long[] array, long[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param longToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(long[] array, long longToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (longToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(long[] array, long[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        long first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // ���ҵ�һ��Ԫ��
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return -1;
            }

            // �Ѿ��ҵ���һ��Ԫ�أ�������
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // ���²��ҵ�һ��Ԫ��
                    continue startSearchForFirst;
                }
            }

            // �ҵ���
            return i;
        }
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param longToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(long[] array, long longToFind) {
        return arrayLastIndexOf(array, longToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(long[] array, long[] arrayToFind) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param longToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(long[] array, long longToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            return -1;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (longToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(long[] array, long[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return -1;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        long last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return -1;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param longToFind Ҫ���ҵ�Ԫ��
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(long[] array, long longToFind) {
        return arrayIndexOf(array, longToFind) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(long[] array, long[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind) != -1;
    }

    // ==========================================================================
    // �������в���һ��Ԫ�ػ�һ��Ԫ�����С�                                        
    //  
    // ���ͣ�int[]                                                                 
    // ==========================================================================

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param intToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(int[] array, int intToFind) {
        return arrayIndexOf(array, intToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(int[] array, int[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param intToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(int[] array, int intToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (intToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(int[] array, int[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // ���ҵ�һ��Ԫ��
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return -1;
            }

            // �Ѿ��ҵ���һ��Ԫ�أ�������
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // ���²��ҵ�һ��Ԫ��
                    continue startSearchForFirst;
                }
            }

            // �ҵ���
            return i;
        }
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param intToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(int[] array, int intToFind) {
        return arrayLastIndexOf(array, intToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(int[] array, int[] arrayToFind) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param intToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(int[] array, int intToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            return -1;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (intToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(int[] array, int[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return -1;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        int last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return -1;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param intToFind Ҫ���ҵ�Ԫ��
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(int[] array, int intToFind) {
        return arrayIndexOf(array, intToFind) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(int[] array, int[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind) != -1;
    }

    // ==========================================================================
    // �������в���һ��Ԫ�ػ�һ��Ԫ�����С�                                        
    //  
    // ���ͣ�short[]                                                               
    // ==========================================================================

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param shortToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(short[] array, short shortToFind) {
        return arrayIndexOf(array, shortToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(short[] array, short[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param shortToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(short[] array, short shortToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (shortToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(short[] array, short[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        short first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // ���ҵ�һ��Ԫ��
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return -1;
            }

            // �Ѿ��ҵ���һ��Ԫ�أ�������
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // ���²��ҵ�һ��Ԫ��
                    continue startSearchForFirst;
                }
            }

            // �ҵ���
            return i;
        }
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param shortToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(short[] array, short shortToFind) {
        return arrayLastIndexOf(array, shortToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(short[] array, short[] arrayToFind) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param shortToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(short[] array, short shortToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            return -1;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (shortToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(short[] array, short[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return -1;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        short last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return -1;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param shortToFind Ҫ���ҵ�Ԫ��
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(short[] array, short shortToFind) {
        return arrayIndexOf(array, shortToFind) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(short[] array, short[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind) != -1;
    }

    // ==========================================================================
    // �������в���һ��Ԫ�ػ�һ��Ԫ�����С�                                        
    //  
    // ���ͣ�byte[]                                                                
    // ==========================================================================

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param byteToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(byte[] array, byte byteToFind) {
        return arrayIndexOf(array, byteToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(byte[] array, byte[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param byteToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(byte[] array, byte byteToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (byteToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(byte[] array, byte[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        byte first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // ���ҵ�һ��Ԫ��
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return -1;
            }

            // �Ѿ��ҵ���һ��Ԫ�أ�������
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // ���²��ҵ�һ��Ԫ��
                    continue startSearchForFirst;
                }
            }

            // �ҵ���
            return i;
        }
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param byteToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(byte[] array, byte byteToFind) {
        return arrayLastIndexOf(array, byteToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(byte[] array, byte[] arrayToFind) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param byteToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(byte[] array, byte byteToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            return -1;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (byteToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(byte[] array, byte[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return -1;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        byte last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return -1;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param byteToFind Ҫ���ҵ�Ԫ��
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(byte[] array, byte byteToFind) {
        return arrayIndexOf(array, byteToFind) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(byte[] array, byte[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind) != -1;
    }

    // ==========================================================================
    // �������в���һ��Ԫ�ػ�һ��Ԫ�����С�                                        
    //  
    // ���ͣ�double[]                                                              
    // ==========================================================================

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(double[] array, double doubleToFind) {
        return arrayIndexOf(array, doubleToFind, 0, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @param tolerance ���
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(double[] array, double doubleToFind, double tolerance) {
        return arrayIndexOf(array, doubleToFind, 0, tolerance);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(double[] array, double[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind, 0, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param tolerance ���
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(double[] array, double[] arrayToFind, double tolerance) {
        return arrayIndexOf(array, arrayToFind, 0, tolerance);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(double[] array, double doubleToFind, int startIndex) {
        return arrayIndexOf(array, doubleToFind, startIndex, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @param tolerance ���
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(double[] array, double doubleToFind, int startIndex, double tolerance) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        double min = doubleToFind - tolerance;
        double max = doubleToFind + tolerance;

        for (int i = startIndex; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(double[] array, double[] arrayToFind, int startIndex) {
        return arrayIndexOf(array, arrayToFind, startIndex, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @param tolerance ���
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(double[] array, double[] arrayToFind, int startIndex, double tolerance) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        double firstMin = arrayToFind[0] - tolerance;
        double firstMax = arrayToFind[0] + tolerance;
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // ���ҵ�һ��Ԫ��
            while (i <= max && (array[i] < firstMin || array[i] > firstMax)) {
                i++;
            }

            if (i > max) {
                return -1;
            }

            // �Ѿ��ҵ���һ��Ԫ�أ�������
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (Math.abs(array[j++] - arrayToFind[k++]) > tolerance) {
                    i++;

                    // ���²��ҵ�һ��Ԫ��
                    continue startSearchForFirst;
                }
            }

            // �ҵ���
            return i;
        }
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(double[] array, double doubleToFind) {
        return arrayLastIndexOf(array, doubleToFind, Integer.MAX_VALUE, 0);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @param tolerance ���
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(double[] array, double doubleToFind, double tolerance) {
        return arrayLastIndexOf(array, doubleToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(double[] array, double[] arrayToFind) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE, 0);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param tolerance ���
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(double[] array, double[] arrayToFind, double tolerance) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(double[] array, double doubleToFind, int startIndex) {
        return arrayLastIndexOf(array, doubleToFind, startIndex, 0);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @param tolerance ���
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(double[] array, double doubleToFind, int startIndex, double tolerance) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            return -1;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        double min = doubleToFind - tolerance;
        double max = doubleToFind + tolerance;

        for (int i = startIndex; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(double[] array, double[] arrayToFind, int startIndex) {
        return arrayLastIndexOf(array, arrayToFind, startIndex, 0);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @param tolerance ���
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(double[] array, double[] arrayToFind, int startIndex, double tolerance) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return -1;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        double lastMin = arrayToFind[lastIndex] - tolerance;
        double lastMax = arrayToFind[lastIndex] + tolerance;
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && (array[i] < lastMin || array[i] > lastMax)) {
                i--;
            }

            if (i < min) {
                return -1;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (Math.abs(array[j--] - arrayToFind[k--]) > tolerance) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(double[] array, double doubleToFind) {
        return arrayIndexOf(array, doubleToFind) != -1;
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param doubleToFind Ҫ���ҵ�Ԫ��
     * @param tolerance ���
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(double[] array, double doubleToFind, double tolerance) {
        return arrayIndexOf(array, doubleToFind, tolerance) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(double[] array, double[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param tolerance ���
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(double[] array, double[] arrayToFind, double tolerance) {
        return arrayIndexOf(array, arrayToFind, tolerance) != -1;
    }

    // ==========================================================================
    // �������в���һ��Ԫ�ػ�һ��Ԫ�����С�                                        
    //  
    // ���ͣ�float[]                                                               
    // ==========================================================================

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(float[] array, float floatToFind) {
        return arrayIndexOf(array, floatToFind, 0, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @param tolerance ���
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(float[] array, float floatToFind, float tolerance) {
        return arrayIndexOf(array, floatToFind, 0, tolerance);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(float[] array, float[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind, 0, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param tolerance ���
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(float[] array, float[] arrayToFind, float tolerance) {
        return arrayIndexOf(array, arrayToFind, 0, tolerance);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(float[] array, float floatToFind, int startIndex) {
        return arrayIndexOf(array, floatToFind, startIndex, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @param tolerance ���
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(float[] array, float floatToFind, int startIndex, float tolerance) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        float min = floatToFind - tolerance;
        float max = floatToFind + tolerance;

        for (int i = startIndex; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(float[] array, float[] arrayToFind, int startIndex) {
        return arrayIndexOf(array, arrayToFind, startIndex, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @param tolerance ���
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(float[] array, float[] arrayToFind, int startIndex, float tolerance) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        float firstMin = arrayToFind[0] - tolerance;
        float firstMax = arrayToFind[0] + tolerance;
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // ���ҵ�һ��Ԫ��
            while (i <= max && (array[i] < firstMin || array[i] > firstMax)) {
                i++;
            }

            if (i > max) {
                return -1;
            }

            // �Ѿ��ҵ���һ��Ԫ�أ�������
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (Math.abs(array[j++] - arrayToFind[k++]) > tolerance) {
                    i++;

                    // ���²��ҵ�һ��Ԫ��
                    continue startSearchForFirst;
                }
            }

            // �ҵ���
            return i;
        }
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(float[] array, float floatToFind) {
        return arrayLastIndexOf(array, floatToFind, Integer.MAX_VALUE, 0);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @param tolerance ���
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(float[] array, float floatToFind, float tolerance) {
        return arrayLastIndexOf(array, floatToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(float[] array, float[] arrayToFind) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE, 0);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param tolerance ���
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(float[] array, float[] arrayToFind, float tolerance) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(float[] array, float floatToFind, int startIndex) {
        return arrayLastIndexOf(array, floatToFind, startIndex, 0);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @param tolerance ���
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(float[] array, float floatToFind, int startIndex, float tolerance) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            return -1;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        float min = floatToFind - tolerance;
        float max = floatToFind + tolerance;

        for (int i = startIndex; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(float[] array, float[] arrayToFind, int startIndex) {
        return arrayLastIndexOf(array, arrayToFind, startIndex, 0);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @param tolerance ���
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(float[] array, float[] arrayToFind, int startIndex, float tolerance) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return -1;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        float lastMin = arrayToFind[lastIndex] - tolerance;
        float lastMax = arrayToFind[lastIndex] + tolerance;
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && (array[i] < lastMin || array[i] > lastMax)) {
                i--;
            }

            if (i < min) {
                return -1;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (Math.abs(array[j--] - arrayToFind[k--]) > tolerance) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(float[] array, float floatToFind) {
        return arrayIndexOf(array, floatToFind) != -1;
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param floatToFind Ҫ���ҵ�Ԫ��
     * @param tolerance ���
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(float[] array, float floatToFind, float tolerance) {
        return arrayIndexOf(array, floatToFind, tolerance) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(float[] array, float[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param tolerance ���
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(float[] array, float[] arrayToFind, float tolerance) {
        return arrayIndexOf(array, arrayToFind, tolerance) != -1;
    }

    // ==========================================================================
    // �������в���һ��Ԫ�ػ�һ��Ԫ�����С�                                        
    //  
    // ���ͣ�boolean[]                                                             
    // ==========================================================================

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param booleanToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(boolean[] array, boolean booleanToFind) {
        return arrayIndexOf(array, booleanToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(boolean[] array, boolean[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param booleanToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(boolean[] array, boolean booleanToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (booleanToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(boolean[] array, boolean[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        boolean first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // ���ҵ�һ��Ԫ��
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return -1;
            }

            // �Ѿ��ҵ���һ��Ԫ�أ�������
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // ���²��ҵ�һ��Ԫ��
                    continue startSearchForFirst;
                }
            }

            // �ҵ���
            return i;
        }
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param booleanToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(boolean[] array, boolean booleanToFind) {
        return arrayLastIndexOf(array, booleanToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(boolean[] array, boolean[] arrayToFind) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param booleanToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(boolean[] array, boolean booleanToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            return -1;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (booleanToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(boolean[] array, boolean[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return -1;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        boolean last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return -1;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param booleanToFind Ҫ���ҵ�Ԫ��
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(boolean[] array, boolean booleanToFind) {
        return arrayIndexOf(array, booleanToFind) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(boolean[] array, boolean[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind) != -1;
    }

    // ==========================================================================
    // �������в���һ��Ԫ�ػ�һ��Ԫ�����С�                                        
    //  
    // ���ͣ�char[]                                                                
    // ==========================================================================

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param charToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(char[] array, char charToFind) {
        return arrayIndexOf(array, charToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(char[] array, char[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind, 0);
    }

    /**
     * �������в���һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param charToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(char[] array, char charToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (charToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������в���һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>����<code>0</code>���������鳤�ȵ���ʼ�����򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayIndexOf(char[] array, char[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        if (startIndex >= sourceLength) {
            return targetLength == 0 ? sourceLength : -1;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        char first = arrayToFind[0];
        int i = startIndex;
        int max = sourceLength - targetLength;

        startSearchForFirst: while (true) {
            // ���ҵ�һ��Ԫ��
            while (i <= max && array[i] != first) {
                i++;
            }

            if (i > max) {
                return -1;
            }

            // �Ѿ��ҵ���һ��Ԫ�أ�������
            int j = i + 1;
            int end = j + targetLength - 1;
            int k = 1;

            while (j < end) {
                if (array[j++] != arrayToFind[k++]) {
                    i++;

                    // ���²��ҵ�һ��Ԫ��
                    continue startSearchForFirst;
                }
            }

            // �ҵ���
            return i;
        }
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param charToFind Ҫ���ҵ�Ԫ��
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(char[] array, char charToFind) {
        return arrayLastIndexOf(array, charToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(char[] array, char[] arrayToFind) {
        return arrayLastIndexOf(array, arrayToFind, Integer.MAX_VALUE);
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�ء�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param charToFind Ҫ���ҵ�Ԫ��
     * @param startIndex ��ʼ����
     * @return ��Ԫ���������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(char[] array, char charToFind, int startIndex) {
        if (array == null) {
            return -1;
        }

        if (startIndex < 0) {
            return -1;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }

        for (int i = startIndex; i >= 0; i--) {
            if (charToFind == array[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * �������д�ĩβ��ʼ����һ��Ԫ�����С�
     * <p>
     * ���δ�ҵ�������Ϊ<code>null</code>�򷵻�<code>-1</code>��
     * </p>
     * <p>
     * ��ʼ����С��<code>0</code>�򷵻�<code>-1</code>���������鳤�ȵ���ʼ�����������ĩβ��ʼ�ҡ�
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @param startIndex ��ʼ����
     * @return ��Ԫ�������������е���ţ��������Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     */
    public static int arrayLastIndexOf(char[] array, char[] arrayToFind, int startIndex) {
        if (array == null || arrayToFind == null) {
            return -1;
        }

        int sourceLength = array.length;
        int targetLength = arrayToFind.length;

        int rightIndex = sourceLength - targetLength;

        if (startIndex < 0) {
            return -1;
        }

        if (startIndex > rightIndex) {
            startIndex = rightIndex;
        }

        if (targetLength == 0) {
            return startIndex;
        }

        int lastIndex = targetLength - 1;
        char last = arrayToFind[lastIndex];
        int min = targetLength - 1;
        int i = min + startIndex;

        startSearchForLast: while (true) {
            while (i >= min && array[i] != last) {
                i--;
            }

            if (i < min) {
                return -1;
            }

            int j = i - 1;
            int start = j - (targetLength - 1);
            int k = lastIndex - 1;

            while (j > start) {
                if (array[j--] != arrayToFind[k--]) {
                    i--;
                    continue startSearchForLast;
                }
            }

            return start + 1;
        }
    }

    /**
     * �ж�ָ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param charToFind Ҫ���ҵ�Ԫ��
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(char[] array, char charToFind) {
        return arrayIndexOf(array, charToFind) != -1;
    }

    /**
     * �ж�ָ��Ԫ�������Ƿ������ָ�������С�
     * <p>
     * �������Ϊ<code>null</code>�򷵻�<code>false</code>��
     * </p>
     * 
     * @param array Ҫɨ�������
     * @param arrayToFind Ҫ���ҵ�Ԫ������
     * @return ����ҵ��򷵻�<code>true</code>
     */
    public static boolean arrayContains(char[] array, char[] arrayToFind) {
        return arrayIndexOf(array, arrayToFind) != -1;
    }
}
