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

import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * �й� <code>Class</code> ����Ĺ����ࡣ
 * <p>
 * ������е�ÿ�����������ԡ���ȫ���ش��� <code>null</code> ���������׳�
 * <code>NullPointerException</code>��
 * </p>
 * 
 * @author Michael Zhou
 * @version $Id: ClassUtil.java 509 2004-02-16 05:42:07Z baobao $
 */
public class ClassUtil {
    // ==========================================================================
    // ȡ���Ѻ�������package���ķ�����                                                  
    // ==========================================================================

    /**
     * ȡ�ö�������������Ѻ�������
     * <p>
     * ����<code>object.getClass().getName()</code>������ͬ���ǣ��÷����ø��Ѻõķ�ʽ��ʾ�������͡� ���磺
     * </p>
     * 
     * <pre>
     *  int[].class.getName() = "[I"
     *  ClassUtil.getFriendlyClassName(int[].class) = "int[]"
     * 
     *  Integer[][].class.getName() = "[[Ljava.lang.Integer;"
     *  ClassUtil.getFriendlyClassName(Integer[][].class) = "java.lang.Integer[][]"
     * </pre>
     * <p>
     * ���ڷ���������ͣ��÷�����Ч�� <code>Class.getName()</code> ������
     * </p>
     * <p>
     * ע�⣬�÷��������ص���������ֻ��������ʾ���˿����������� <code>Class.forName</code> ������
     * </p>
     * 
     * @param object Ҫ��ʾ�����Ķ���
     * @return ������ʾ���Ѻ��������������Ϊ�գ��򷵻�<code>null</code>
     */
    public static String getFriendlyClassNameForObject(Object object) {
        if (object == null) {
            return null;
        }

        String javaClassName = object.getClass().getName();

        return toFriendlyClassName(javaClassName, true, javaClassName);
    }

    /**
     * ȡ���Ѻõ�������
     * <p>
     * ����<code>clazz.getName()</code>������ͬ���ǣ��÷����ø��Ѻõķ�ʽ��ʾ�������͡� ���磺
     * </p>
     * 
     * <pre>
     *  int[].class.getName() = "[I"
     *  ClassUtil.getFriendlyClassName(int[].class) = "int[]"
     * 
     *  Integer[][].class.getName() = "[[Ljava.lang.Integer;"
     *  ClassUtil.getFriendlyClassName(Integer[][].class) = "java.lang.Integer[][]"
     * </pre>
     * <p>
     * ���ڷ���������ͣ��÷�����Ч�� <code>Class.getName()</code> ������
     * </p>
     * <p>
     * ע�⣬�÷��������ص���������ֻ��������ʾ���˿����������� <code>Class.forName</code> ������
     * </p>
     * 
     * @param object Ҫ��ʾ�����Ķ���
     * @return ������ʾ���Ѻ���������������Ϊ�գ��򷵻�<code>null</code>
     */
    public static String getFriendlyClassName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        String javaClassName = clazz.getName();

        return toFriendlyClassName(javaClassName, true, javaClassName);
    }

    /**
     * ȡ���Ѻõ�������
     * <p>
     * <code>className</code> �����Ǵ� <code>clazz.getName()</code>
     * �����صĺϷ��������÷����ø��Ѻõķ�ʽ��ʾ�������͡� ���磺
     * </p>
     * 
     * <pre>
     *  int[].class.getName() = "[I"
     *  ClassUtil.getFriendlyClassName(int[].class) = "int[]"
     * 
     *  Integer[][].class.getName() = "[[Ljava.lang.Integer;"
     *  ClassUtil.getFriendlyClassName(Integer[][].class) = "java.lang.Integer[][]"
     * </pre>
     * <p>
     * ���ڷ���������ͣ��÷�����Ч�� <code>Class.getName()</code> ������
     * </p>
     * <p>
     * ע�⣬�÷��������ص���������ֻ��������ʾ���˿����������� <code>Class.forName</code> ������
     * </p>
     * 
     * @param javaClassName Ҫת��������
     * @return ������ʾ���Ѻ����������ԭ����Ϊ�գ��򷵻� <code>null</code> �����ԭ�����ǷǷ��ģ��򷵻�ԭ����
     */
    public static String getFriendlyClassName(String javaClassName) {
        return toFriendlyClassName(javaClassName, true, javaClassName);
    }

    /**
     * ��Java����ת�����Ѻ�������
     * 
     * @param javaClassName Java����
     * @param processInnerClass �Ƿ�������ָ��� <code>'$'</code> ת���� <code>'.'</code>
     * @return �Ѻõ���������������Ƿ���գ��򷵻�<code>null</code>��
     */
    private static String toFriendlyClassName(String javaClassName, boolean processInnerClass, String defaultIfInvalid) {
        String name = StringUtil.trimToNull(javaClassName);

        if (name == null) {
            return defaultIfInvalid;
        }

        if (processInnerClass) {
            name = name.replace('$', '.');
        }

        int length = name.length();
        int dimension = 0;

        // ȡ�������ά��������������飬ά��Ϊ0
        for (int i = 0; i < length; i++, dimension++) {
            if (name.charAt(i) != '[') {
                break;
            }
        }

        // ����������飬��ֱ�ӷ���
        if (dimension == 0) {
            return name;
        }

        // ȷ�������Ϸ�
        if (length <= dimension) {
            return defaultIfInvalid; // �Ƿ�����
        }

        // ��������
        StringBuilder componentTypeName = new StringBuilder();

        switch (name.charAt(dimension)) {
            case 'Z':
                componentTypeName.append("boolean");
                break;

            case 'B':
                componentTypeName.append("byte");
                break;

            case 'C':
                componentTypeName.append("char");
                break;

            case 'D':
                componentTypeName.append("double");
                break;

            case 'F':
                componentTypeName.append("float");
                break;

            case 'I':
                componentTypeName.append("int");
                break;

            case 'J':
                componentTypeName.append("long");
                break;

            case 'S':
                componentTypeName.append("short");
                break;

            case 'L':
                if (name.charAt(length - 1) != ';' || length <= dimension + 2) {
                    return defaultIfInvalid; // �Ƿ�����
                }

                componentTypeName.append(name.substring(dimension + 1, length - 1));
                break;

            default:
                return defaultIfInvalid; // �Ƿ�����
        }

        for (int i = 0; i < dimension; i++) {
            componentTypeName.append("[]");
        }

        return componentTypeName.toString();
    }

    /**
     * ȡ��ָ��������������ļ�������������package����
     * <p>
     * �˷���������ȷ��ʾ���������������ơ� ���磺
     * 
     * <pre>
     *  ClassUtil.getSimpleClassNameForObject(Boolean.TRUE) = "Boolean"
     *  ClassUtil.getSimpleClassNameForObject(new Boolean[10]) = "Boolean[]"
     *  ClassUtil.getSimpleClassNameForObject(new int[1][2]) = "int[][]"
     * </pre>
     * <p>
     * ��������<code>Class.getSimpleName()</code>���������ڣ��������ᱣ��inner�����������ơ�
     * </p>
     * 
     * @param object Ҫ�鿴�Ķ���
     * @return ���������������Ϊ <code>null</code> ���򷵻� <code>null</code>
     */
    public static String getSimpleClassNameForObject(Object object) {
        if (object == null) {
            return null;
        }

        return getSimpleClassName(object.getClass().getName());
    }

    /**
     * ȡ�ü�������������package����
     * <p>
     * �˷���������ȷ��ʾ���������������ơ� ���磺
     * 
     * <pre>
     *  ClassUtil.getSimpleClassName(Boolean.class) = "Boolean"
     *  ClassUtil.getSimpleClassName(Boolean[].class) = "Boolean[]"
     *  ClassUtil.getSimpleClassName(int[][].class) = "int[][]"
     *  ClassUtil.getSimpleClassName(Map.Entry.class) = "Map.Entry"
     * </pre>
     * <p>
     * ��������<code>Class.getSimpleName()</code>���������ڣ��������ᱣ��inner�����������ơ�
     * </p>
     * 
     * @param clazz Ҫ�鿴����
     * @return �������������Ϊ <code>null</code> ���򷵻� <code>null</code>
     */
    public static String getSimpleClassName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getSimpleClassName(clazz.getName());
    }

    /**
     * ȡ��������������package����
     * <p>
     * �˷���������ȷ��ʾ���������������ơ� ���磺
     * 
     * <pre>
     *  ClassUtil.getSimpleClassName(Boolean.class.getName()) = "Boolean"
     *  ClassUtil.getSimpleClassName(Boolean[].class.getName()) = "Boolean[]"
     *  ClassUtil.getSimpleClassName(int[][].class.getName()) = "int[][]"
     *  ClassUtil.getSimpleClassName(Map.Entry.class.getName()) = "Map.Entry"
     * </pre>
     * <p>
     * ��������<code>Class.getSimpleName()</code>���������ڣ��������ᱣ��inner�����������ơ�
     * </p>
     * 
     * @param javaClassName Ҫ�鿴������
     * @return ���������������Ϊ�գ��򷵻� <code>null</code>
     */
    public static String getSimpleClassName(String javaClassName) {
        String friendlyClassName = toFriendlyClassName(javaClassName, false, null);

        if (friendlyClassName == null) {
            return javaClassName;
        }

        char[] chars = friendlyClassName.toCharArray();
        int beginIndex = 0;

        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] == '.') {
                beginIndex = i + 1;
                break;
            } else if (chars[i] == '$') {
                chars[i] = '.';
            }
        }

        return new String(chars, beginIndex, chars.length - beginIndex);
    }

    /**
     * ȡ�ü���method������
     */
    public static String getSimpleMethodSignature(Method method) {
        return getSimpleMethodSignature(method, false, false, false, false);
    }

    /**
     * ȡ�ü���method������
     */
    public static String getSimpleMethodSignature(Method method, boolean withModifiers, boolean withReturnType,
                                                  boolean withClassName, boolean withExceptionType) {
        if (method == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();

        if (withModifiers) {
            buf.append(Modifier.toString(method.getModifiers())).append(' ');
        }

        if (withReturnType) {
            buf.append(getSimpleClassName(method.getReturnType())).append(' ');
        }

        if (withClassName) {
            buf.append(getSimpleClassName(method.getDeclaringClass())).append('.');
        }

        buf.append(method.getName()).append('(');

        Class<?>[] paramTypes = method.getParameterTypes();

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            buf.append(getSimpleClassName(paramType));

            if (i < paramTypes.length - 1) {
                buf.append(", ");
            }
        }

        buf.append(')');

        if (withExceptionType) {
            Class<?>[] exceptionTypes = method.getExceptionTypes();

            if (!isEmptyArray(exceptionTypes)) {
                buf.append(" throws ");

                for (int i = 0; i < exceptionTypes.length; i++) {
                    Class<?> exceptionType = exceptionTypes[i];

                    buf.append(getSimpleClassName(exceptionType));

                    if (i < exceptionTypes.length - 1) {
                        buf.append(", ");
                    }
                }
            }
        }

        return buf.toString();
    }

    /**
     * ȡ��ָ���������������package����
     * <p>
     * �������飬�˷������ص�������Ԫ�����͵�package����
     * </p>
     * 
     * @param object Ҫ�鿴�Ķ���
     * @return package�����������Ϊ <code>null</code> ���򷵻�<code>""</code>
     */
    public static String getPackageNameForObject(Object object) {
        if (object == null) {
            return EMPTY_STRING;
        }

        return getPackageName(object.getClass().getName());
    }

    /**
     * ȡ��ָ�����package����
     * <p>
     * �������飬�˷������ص�������Ԫ�����͵�package����
     * </p>
     * 
     * @param clazz Ҫ�鿴����
     * @return package���������Ϊ <code>null</code> ���򷵻�<code>""</code>
     */
    public static String getPackageName(Class<?> clazz) {
        if (clazz == null) {
            return EMPTY_STRING;
        }

        return getPackageName(clazz.getName());
    }

    /**
     * ȡ��ָ��������package����
     * <p>
     * �������飬�˷������ص�������Ԫ�����͵�package����
     * </p>
     * 
     * @param javaClassName Ҫ�鿴������
     * @return package�����������Ϊ�գ��򷵻� <code>null</code>
     */
    public static String getPackageName(String javaClassName) {
        String friendlyClassName = toFriendlyClassName(javaClassName, false, null);

        if (friendlyClassName == null) {
            return EMPTY_STRING;
        }

        int i = friendlyClassName.lastIndexOf('.');

        if (i == -1) {
            return EMPTY_STRING;
        }

        return friendlyClassName.substring(0, i);
    }

    // ==========================================================================
    // ȡ��������package����resource���ķ�����                                      
    //  
    // ��������package����ͬ���ǣ�resource�������ļ��������淶�����磺              
    // java/lang/String.class                                                      
    // com/alibaba/commons/lang                                                    
    // etc.                                                                        
    // ==========================================================================

    /**
     * ȡ�ö��������������Դ����
     * <p>
     * ���磺
     * </p>
     * 
     * <pre>
     * ClassUtil.getResourceNameForObjectClass(&quot;This is a string&quot;) = &quot;java/lang/String.class&quot;
     * </pre>
     * 
     * @param object Ҫ��ʾ�����Ķ���
     * @return ָ���������������Դ�����������Ϊ�գ��򷵻�<code>null</code>
     */
    public static String getResourceNameForObjectClass(Object object) {
        if (object == null) {
            return null;
        }

        return object.getClass().getName().replace('.', '/') + ".class";
    }

    /**
     * ȡ��ָ�������Դ����
     * <p>
     * ���磺
     * </p>
     * 
     * <pre>
     * ClassUtil.getResourceNameForClass(String.class) = &quot;java/lang/String.class&quot;
     * </pre>
     * 
     * @param clazz Ҫ��ʾ��������
     * @return ָ�������Դ�������ָ����Ϊ�գ��򷵻�<code>null</code>
     */
    public static String getResourceNameForClass(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return clazz.getName().replace('.', '/') + ".class";
    }

    /**
     * ȡ��ָ�������Դ����
     * <p>
     * ���磺
     * </p>
     * 
     * <pre>
     * ClassUtil.getResourceNameForClass(&quot;java.lang.String&quot;) = &quot;java/lang/String.class&quot;
     * </pre>
     * 
     * @param className Ҫ��ʾ������
     * @return ָ��������Ӧ����Դ�������ָ������Ϊ�գ��򷵻�<code>null</code>
     */
    public static String getResourceNameForClass(String className) {
        if (className == null) {
            return null;
        }

        return className.replace('.', '/') + ".class";
    }

    /**
     * ȡ��ָ���������������package������Դ����
     * <p>
     * �������飬�˷������ص�������Ԫ�����͵�package����
     * </p>
     * 
     * @param object Ҫ�鿴�Ķ���
     * @return package�����������Ϊ <code>null</code> ���򷵻� <code>null</code>
     */
    public static String getResourceNameForObjectPackage(Object object) {
        if (object == null) {
            return null;
        }

        return getPackageNameForObject(object).replace('.', '/');
    }

    /**
     * ȡ��ָ�����package������Դ����
     * <p>
     * �������飬�˷������ص�������Ԫ�����͵�package����
     * </p>
     * 
     * @param clazz Ҫ�鿴����
     * @return package���������Ϊ <code>null</code> ���򷵻� <code>null</code>
     */
    public static String getResourceNameForPackage(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return getPackageName(clazz).replace('.', '/');
    }

    /**
     * ȡ��ָ��������package������Դ����
     * <p>
     * �������飬�˷������ص�������Ԫ�����͵�package����
     * </p>
     * 
     * @param className Ҫ�鿴������
     * @return package�����������Ϊ�գ��򷵻� <code>null</code>
     */
    public static String getResourceNameForPackage(String className) {
        if (className == null) {
            return null;
        }

        return getPackageName(className).replace('.', '/');
    }

    // ==========================================================================
    // ȡ�������ࡣ                                   
    // ==========================================================================

    /**
     * ȡ��ָ��һά������.
     * 
     * @param componentType ����Ļ�����
     * @return �����࣬�������Ļ���Ϊ <code>null</code> ���򷵻� <code>null</code>
     */
    public static Class<?> getArrayClass(Class<?> componentType) {
        return getArrayClass(componentType, 1);
    }

    /**
     * ȡ��ָ��ά���� <code>Array</code>��.
     * 
     * @param componentType ����Ļ���
     * @param dimension ά�������С�� <code>0</code> ���� <code>0</code>
     * @return ���ά��Ϊ0, �򷵻ػ��౾��, ���򷵻������࣬�������Ļ���Ϊ <code>null</code> ���򷵻�
     *         <code>null</code>
     */
    public static Class<?> getArrayClass(Class<?> componentClass, int dimension) {
        if (componentClass == null) {
            return null;
        }

        switch (dimension) {
            case 1:
                return Array.newInstance(componentClass, 0).getClass();

            case 0:
                return componentClass;

            default:
                assertTrue(dimension > 0, "wrong dimension: %d", dimension);

                return Array.newInstance(componentClass, new int[dimension]).getClass();
        }
    }

    // ==========================================================================
    // ȡ��ԭ�����ͻ�����wrapper�ࡣ                                   
    // ==========================================================================

    /**
     * ȡ��primitive�ࡣ
     * <p>
     * ���磺
     * 
     * <pre>
     * ClassUtil.getPrimitiveType(&quot;int&quot;) = int.class;
     * ClassUtil.getPrimitiveType(&quot;long&quot;) = long.class;
     * </pre>
     * 
     * </p>
     */
    public static Class<?> getPrimitiveType(String name) {
        PrimitiveInfo<?> info = PRIMITIVES.get(name);

        if (info != null) {
            return info.type;
        }

        return null;
    }

    /**
     * ȡ��primitive�ࡣ
     * <p>
     * ���磺
     * 
     * <pre>
     * ClassUtil.getPrimitiveType(Integer.class) = int.class;
     * ClassUtil.getPrimitiveType(Long.class) = long.class;
     * </pre>
     * 
     * </p>
     */
    public static Class<?> getPrimitiveType(Class<?> type) {
        return getPrimitiveType(type.getName());
    }

    /**
     * ȡ��primitive���͵�wrapper���������primitive����ԭ�����ء�
     * <p>
     * ���磺
     * 
     * <pre>
     * ClassUtil.getPrimitiveWrapperType(int.class) = Integer.class;
     * ClassUtil.getPrimitiveWrapperType(int[].class) = int[].class;
     * ClassUtil.getPrimitiveWrapperType(int[][].class) = int[][].class;
     * ClassUtil.getPrimitiveWrapperType(String[][].class) = String[][].class;
     * </pre>
     * 
     * </p>
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getWrapperTypeIfPrimitive(Class<T> type) {
        if (type.isPrimitive()) {
            return ((PrimitiveInfo<T>) PRIMITIVES.get(type.getName())).wrapperType;
        }

        return type;
    }

    /**
     * ȡ��primitive���͵�Ĭ��ֵ���������primitive���򷵻�<code>null</code>��
     * <p>
     * ���磺
     * 
     * <pre>
     * ClassUtil.getPrimitiveDefaultValue(int.class) = 0;
     * ClassUtil.getPrimitiveDefaultValue(boolean.class) = false;
     * ClassUtil.getPrimitiveDefaultValue(char.class) = '\0';
     * </pre>
     * 
     * </p>
     */
    @SuppressWarnings("unchecked")
    public static <T> T getPrimitiveDefaultValue(Class<T> type) {
        PrimitiveInfo<T> info = (PrimitiveInfo<T>) PRIMITIVES.get(type.getName());

        if (info != null) {
            return info.defaultValue;
        }

        return null;
    }

    private static final Map<String, PrimitiveInfo<?>> PRIMITIVES = createHashMap();

    static {
        addPrimitive(boolean.class, "Z", Boolean.class, "booleanValue", false);
        addPrimitive(short.class, "S", Short.class, "shortValue", (short) 0);
        addPrimitive(int.class, "I", Integer.class, "intValue", 0);
        addPrimitive(long.class, "J", Long.class, "longValue", 0L);
        addPrimitive(float.class, "F", Float.class, "floatValue", 0F);
        addPrimitive(double.class, "D", Double.class, "doubleValue", 0D);
        addPrimitive(char.class, "C", Character.class, "charValue", '\0');
        addPrimitive(byte.class, "B", Byte.class, "byteValue", (byte) 0);
        addPrimitive(void.class, "V", Void.class, null, null);
    }

    private static <T> void addPrimitive(Class<T> type, String typeCode, Class<T> wrapperType, String unwrapMethod,
                                         T defaultValue) {
        PrimitiveInfo<T> info = new PrimitiveInfo<T>(type, typeCode, wrapperType, unwrapMethod, defaultValue);

        PRIMITIVES.put(type.getName(), info);
        PRIMITIVES.put(wrapperType.getName(), info);
    }

    /**
     * ����һ��primitive���͵���Ϣ��
     */
    @SuppressWarnings("unused")
    private static class PrimitiveInfo<T> {
        final Class<T> type;
        final String typeCode;
        final Class<T> wrapperType;
        final String unwrapMethod;
        final T defaultValue;

        public PrimitiveInfo(Class<T> type, String typeCode, Class<T> wrapperType, String unwrapMethod, T defaultValue) {
            this.type = type;
            this.typeCode = typeCode;
            this.wrapperType = wrapperType;
            this.unwrapMethod = unwrapMethod;
            this.defaultValue = defaultValue;
        }
    }

    // ==========================================================================
    // ����ƥ�䡣                                   
    // ==========================================================================

    /**
     * ���һ��ָ������ <code>fromClasses</code> �Ķ����Ƿ���Ը�ֵ����һ������ <code>classes</code>��
     * <p>
     * �˷�����������ȷ��ָ�����͵Ĳ��� <code>object1, object2, ...</code> �Ƿ������������ȷ����������Ϊ
     * <code>class1, class2,
     * ...</code> �ķ�����
     * </p>
     * <p>
     * ���� <code>fromClasses</code> ��ÿ��Ԫ�� <code>fromClass</code> ��
     * <code>classes</code> ��ÿ��Ԫ�� <code>clazz</code>�� �������¹���
     * <ol>
     * <li>���Ŀ���� <code>clazz</code> Ϊ <code>null</code> �����Ƿ��� <code>false</code>
     * ��</li>
     * <li>����������� <code>fromClass</code> Ϊ <code>null</code> ������Ŀ������
     * <code>clazz</code> Ϊ��ԭ�����ͣ��򷵻� <code>true</code>�� ��Ϊ <code>null</code>
     * ���Ա������κ��������͡�</li>
     * <li>���� <code>Class.isAssignableFrom</code> ������ȷ��Ŀ���� <code>clazz</code>
     * �Ƿ�Ͳ����� <code>fromClass</code> ��ͬ�����丸�ࡢ�ӿڣ�����ǣ��򷵻� <code>true</code>��</li>
     * <li>���Ŀ������ <code>clazz</code> Ϊԭ�����ͣ���ô���� <a
     * href="http://java.sun.com/docs/books/jls/">The Java Language
     * Specification</a> ��sections 5.1.1, 5.1.2, 5.1.4�����Widening Primitive
     * Conversion���򣬲������� <code>fromClass</code> �������κ�����չ�ɸ�Ŀ�����͵�ԭ�����ͼ����װ�ࡣ ���磬
     * <code>clazz</code> Ϊ <code>long</code> ����ô�������Ϳ����� <code>byte</code>��
     * <code>short</code>��<code>int</code>��<code>long</code>��<code>char</code>
     * �����װ�� <code>java.lang.Byte</code>��<code>java.lang.Short</code>��
     * <code>java.lang.Integer</code>�� <code>java.lang.Long</code> ��
     * <code>java.lang.Character</code> �������������������򷵻� <code>true</code>��</li>
     * <li>���������������������򷵻� <code>false</code>��</li>
     * </ol>
     * </p>
     * 
     * @param classes Ŀ�������б������ <code>null</code> ���Ƿ��� <code>false</code>
     * @param fromClasses ���������б� <code>null</code> ��ʾ�ɸ�ֵ�������ԭ������
     * @return ������Ա���ֵ���򷵻� <code>true</code>
     */
    public static boolean isAssignable(Class<?>[] classes, Class<?>[] fromClasses) {
        if (!isArraySameLength(fromClasses, classes)) {
            return false;
        }

        if (fromClasses == null) {
            fromClasses = EMPTY_CLASS_ARRAY;
        }

        if (classes == null) {
            classes = EMPTY_CLASS_ARRAY;
        }

        for (int i = 0; i < fromClasses.length; i++) {
            if (isAssignable(classes[i], fromClasses[i]) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * ���ָ������ <code>fromClass</code> �Ķ����Ƿ���Ը�ֵ����һ������ <code>clazz</code>��
     * <p>
     * �˷�����������ȷ��ָ�����͵Ĳ��� <code>object1, object2, ...</code> �Ƿ������������ȷ����������
     * <code>class1, class2,
     * ...</code> �ķ�����
     * </p>
     * <p>
     * �������¹���
     * <ol>
     * <li>���Ŀ���� <code>clazz</code> Ϊ <code>null</code> �����Ƿ��� <code>false</code>
     * ��</li>
     * <li>����������� <code>fromClass</code> Ϊ <code>null</code> ������Ŀ������
     * <code>clazz</code> Ϊ��ԭ�����ͣ��򷵻� <code>true</code>�� ��Ϊ <code>null</code>
     * ���Ա������κ��������͡�</li>
     * <li>���� <code>Class.isAssignableFrom</code> ������ȷ��Ŀ���� <code>clazz</code>
     * �Ƿ�Ͳ����� <code>fromClass</code> ��ͬ�����丸�ࡢ�ӿڣ�����ǣ��򷵻� <code>true</code>��</li>
     * <li>���Ŀ������ <code>clazz</code> Ϊԭ�����ͣ���ô���� <a
     * href="http://java.sun.com/docs/books/jls/">The Java Language
     * Specification</a> ��sections 5.1.1, 5.1.2, 5.1.4�����Widening Primitive
     * Conversion���򣬲������� <code>fromClass</code> �������κ�����չ�ɸ�Ŀ�����͵�ԭ�����ͼ����װ�ࡣ ���磬
     * <code>clazz</code> Ϊ <code>long</code> ����ô�������Ϳ����� <code>byte</code>��
     * <code>short</code>��<code>int</code>��<code>long</code>��<code>char</code>
     * �����װ�� <code>java.lang.Byte</code>��<code>java.lang.Short</code>��
     * <code>java.lang.Integer</code>�� <code>java.lang.Long</code> ��
     * <code>java.lang.Character</code> �������������������򷵻� <code>true</code>��</li>
     * <li>���������������������򷵻� <code>false</code>��</li>
     * </ol>
     * </p>
     * 
     * @param clazz Ŀ�����ͣ������ <code>null</code> ���Ƿ��� <code>false</code>
     * @param fromClass �������ͣ� <code>null</code> ��ʾ�ɸ�ֵ�������ԭ������
     * @return ������Ա���ֵ���򷵻� <code>null</code>
     */
    public static boolean isAssignable(Class<?> clazz, Class<?> fromClass) {
        if (clazz == null) {
            return false;
        }

        // ���fromClass��null��ֻҪclazz����ԭ��������int����һ�����Ը�ֵ
        if (fromClass == null) {
            return !clazz.isPrimitive();
        }

        // �������ͬ���и��ӹ�ϵ����Ȼ���Ը�ֵ
        if (clazz.isAssignableFrom(fromClass)) {
            return true;
        }

        // ����ԭ�����ͣ�����JLS�Ĺ��������չ
        // Ŀ��classΪԭ������ʱ��fromClass����Ϊԭ�����ͺ�ԭ�����͵İ�װ���͡�
        if (clazz.isPrimitive()) {
            return assignmentTable.get(clazz).contains(fromClass);
        }

        return false;
    }

    private final static Map<Class<?>, Set<Class<?>>> assignmentTable = createHashMap();

    static {
        // boolean���Խ��ܣ�boolean
        assignmentTable.put(boolean.class, assignableSet(boolean.class));

        // byte���Խ��ܣ�byte
        assignmentTable.put(byte.class, assignableSet(byte.class));

        // char���Խ��ܣ�char
        assignmentTable.put(char.class, assignableSet(char.class));

        // short���Խ��ܣ�short, byte
        assignmentTable.put(short.class, assignableSet(short.class, byte.class));

        // int���Խ��ܣ�int��byte��short��char
        assignmentTable.put(int.class, assignableSet(int.class, byte.class, short.class, char.class));

        // long���Խ��ܣ�long��int��byte��short��char
        assignmentTable.put(long.class, assignableSet(long.class, int.class, byte.class, short.class, char.class));

        // float���Խ��ܣ�float, long, int, byte, short, char
        assignmentTable.put(float.class,
                assignableSet(float.class, long.class, int.class, byte.class, short.class, char.class));

        // double���Խ��ܣ�double, float, long, int, byte, short, char
        assignmentTable.put(double.class,
                assignableSet(double.class, float.class, long.class, int.class, byte.class, short.class, char.class));

        assertTrue(assignmentTable.size() == 8);
    }

    private static Set<Class<?>> assignableSet(Class<?>... types) {
        Set<Class<?>> assignableSet = createHashSet();

        for (Class<?> type : types) {
            assignableSet.add(getPrimitiveType(type));
            assignableSet.add(getWrapperTypeIfPrimitive(type));
        }

        return assignableSet;
    }

    // ==========================================================================
    // ��λclass��λ�á�                                   
    // ==========================================================================

    /**
     * ��class loader�в���class��λ�á�
     */
    public static String locateClass(Class<?> clazz) {
        return locateClass(clazz.getName(), clazz.getClassLoader());
    }

    /**
     * ��class loader�в���class��λ�á�
     */
    public static String locateClass(String className) {
        return locateClass(className, null);
    }

    /**
     * ��class loader�в���class��λ�á�
     */
    public static String locateClass(String className, ClassLoader loader) {
        className = assertNotNull(trimToNull(className), "className");

        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }

        String classFile = className.replace('.', '/') + ".class";
        URL locationURL = loader.getResource(classFile);
        String location = null;

        if (locationURL != null) {
            location = locationURL.toExternalForm();

            if (location.endsWith(classFile)) {
                location = location.substring(0, location.length() - classFile.length());
            }

            location = location.replaceAll("^(jar|zip):|!/$", EMPTY_STRING);
        }

        return location;
    }
}
