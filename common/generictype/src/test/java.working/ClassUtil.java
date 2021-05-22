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

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.alibaba.citrus.util.internal.ArrayUtil;
import com.alibaba.citrus.util.internal.StringUtil;

/**
 * �й����͵Ĺ����ࡣ
 * 
 * @author Michael Zhou
 */
public class ClassUtil {
    private static final Map<String, PrimitiveInfo<?>> PRIMITIVES = createHashMap();

    static {
        PRIMITIVES.put("boolean", new PrimitiveInfo<Boolean>(boolean.class, "Z", Boolean.class, "booleanValue"));
        PRIMITIVES.put("short", new PrimitiveInfo<Short>(short.class, "S", Short.class, "shortValue"));
        PRIMITIVES.put("int", new PrimitiveInfo<Integer>(int.class, "I", Integer.class, "intValue"));
        PRIMITIVES.put("long", new PrimitiveInfo<Long>(long.class, "J", Long.class, "longValue"));
        PRIMITIVES.put("float", new PrimitiveInfo<Float>(float.class, "F", Float.class, "floatValue"));
        PRIMITIVES.put("double", new PrimitiveInfo<Double>(double.class, "D", Double.class, "doubleValue"));
        PRIMITIVES.put("char", new PrimitiveInfo<Character>(char.class, "C", Character.class, "charValue"));
        PRIMITIVES.put("byte", new PrimitiveInfo<Byte>(byte.class, "B", Byte.class, "byteValue"));
        PRIMITIVES.put("void", new PrimitiveInfo<Void>(void.class, "V", Void.class, null));
    }

    /**
     * ����һ��primitive���͵���Ϣ��
     */
    private static class PrimitiveInfo<T> {
        final Class<T> type;
        final String typeCode;
        final Class<T> wrapperType;
        final String unwrapMethod;

        public PrimitiveInfo(Class<T> type, String typeCode, Class<T> wrapperType, String unwrapMethod) {
            this.type = type;
            this.typeCode = typeCode;
            this.wrapperType = wrapperType;
            this.unwrapMethod = unwrapMethod;
        }
    }

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
    public static <T> Class<T> getPrimitiveWrapperType(Class<T> type) {
        if (type.isPrimitive()) {
            return ((PrimitiveInfo<T>) PRIMITIVES.get(type.getName())).wrapperType;
        }

        return type;
    }

    /**
     * ����Array����Ϣ��
     */
    public static final class ArrayInfo {
        public final Class<?> componentType;
        public final int dimension;

        private ArrayInfo(Class<?> componentType, int dimension) {
            this.componentType = componentType;
            this.dimension = dimension;
        }
    }

    /**
     * ȡ�������ά�ȣ��������򷵻�<code>0</code>��
     */
    public static ArrayInfo getArrayInfo(Class<?> arrayType) {
        assertNotNull(arrayType, "arrayType");

        int dimension = 0;

        while (arrayType.isArray()) {
            arrayType = arrayType.getComponentType();
            dimension++;
        }

        return new ArrayInfo(arrayType, dimension);
    }

    /**
     * ȡ��ָ��component���ͺ�ά�ȵ������ࡣ
     */
    public static Class<?> getArrayType(Class<?> componentType, int dimension) {
        assertTrue(dimension >= 0, "dimension");

        if (dimension == 0) {
            return componentType;
        }

        return Array.newInstance(componentType, new int[dimension]).getClass();
    }

    /**
     * ȡ��JVM�ڲ���������
     * <p>
     * ���磺
     * 
     * <pre>
     *  ClassUtil.getJVMClassName(&quot;int[]&quot;) = &quot;[I&quot;
     *  ClassUtil.getJVMClassName(&quot;java.lang.Integer[][]&quot;) = &quot;[[Ljava.lang.Integer;&quot;
     * </pre>
     * 
     * </p>
     * <p>
     * �÷��������ص����������� <code>Class.forName</code> ������
     * </p>
     */
    public static String getJVMClassName(String name) {
        return getJVMClassName(name, 0);
    }

    /**
     * ȡ��JVM�ڲ�������������
     * <p>
     * ���磺
     * 
     * <pre>
     *  ClassUtil.getJVMClassName(&quot;int&quot;, 1) = &quot;[I&quot;  // int[]
     *  ClassUtil.getJVMClassName(&quot;java.lang.Integer&quot;, 2) = &quot;[[Ljava.lang.Integer;&quot; // Integer[][]
     * 
     *  ClassUtil.getJVMClassName(&quot;int[]&quot;, 1) = &quot;[[I&quot;  // int[][]
     *  ClassUtil.getJVMClassName(&quot;java.lang.Integer[]&quot;, 1) = &quot;[[Ljava.lang.Integer;&quot; // Integer[][]
     * </pre>
     * 
     * </p>
     * <p>
     * �÷��������ص����������� <code>Class.forName</code> ������
     * </p>
     */
    public static String getJVMClassName(String name, int dimension) {
        assertTrue(dimension >= 0, "dimension");

        if (StringUtil.isEmpty(name)) {
            return name;
        }

        if (!name.endsWith("[]") && dimension == 0) {
            return name;
        }

        StringBuilder buffer = new StringBuilder();

        while (name.endsWith("[]")) {
            buffer.append("[");
            name = name.substring(0, name.length() - 2);
        }

        for (int i = 0; i < dimension; i++) {
            buffer.append("[");
        }

        PrimitiveInfo<?> pi = PRIMITIVES.get(name);

        if (pi != null) {
            buffer.append(pi.typeCode);
        } else {
            buffer.append("L");
            buffer.append(name);
            buffer.append(";");
        }

        return buffer.toString();
    }

    /**
     * ȡ��������������
     * <p>
     * �������飬�������ԡ�<code>[]</code>����β�����֡�
     * </p>
     * <p>
     * ��������<code>Class.getCanonicalName()</code>���������ڣ��������ᱣ��inner���
     * <code>$</code>���š�
     * </p>
     */
    public static String getJavaClassName(Class<?> clazz) {
        if (clazz.isArray()) {
            return getJavaClassName(clazz.getComponentType()) + "[]";
        }

        return clazz.getName();
    }

    /**
     * ȡ��������������
     * <p>
     * �������飬�������ԡ�<code>[]</code>����β�����֡�
     * </p>
     * <p>
     * ��������<code>Class.getSimpleName()</code>���������ڣ��������ᱣ��inner���<code>$</code>
     * ���š�
     * </p>
     */
    public static String getSimpleJavaClassName(Class<?> clazz) {
        String className = getJavaClassName(clazz);

        return className.substring(className.lastIndexOf(".") + 1);
    }

    /**
     * �жϷ����ǲ���<code>String toString()</code>������
     */
    public static boolean isToString(Method method) {
        return isToString(new MethodSignature(method));
    }

    /**
     * �жϷ����ǲ���<code>String toString()</code>������
     */
    public static boolean isToString(MethodSignature method) {
        if (!"toString".equals(method.getName())) {
            return false;
        }

        if (method.getParameterTypes().length > 0) {
            return false;
        }

        return String.class == method.getReturnType();
    }

    /**
     * ȡ��ָ��������и���ͽӿڡ�
     * <p>
     * ����һ��<code>Class</code>��������������ǽӿڣ�Ҳ�������飬�����´����г�����ĸ��༰�ӿڡ�
     * </p>
     * <p>
     * ����<code>ClassUtil.getSupertypes(java.util.ArrayList.class)</code>���������б�
     * ��˳��Ϊ�����ࡢ���ࡢ���ӿڡ�Object�ࣩ
     * <ol>
     * <li>���� - <code>java.util.ArrayList</code></li>
     * <li>���� - <code>java.util.AbstractList</code></li>
     * <li>���� - <code>java.util.AbstractCollection</code></li>
     * <li>���ӿ� - <code>java.util.List</code></li>
     * <li>���ӿ� - <code>java.util.Collection</code></li>
     * <li>���ӿ� - <code>java.util.RandomAccess</code></li>
     * <li>���ӿ� - <code>java.lang.Cloneable</code></li>
     * <li>���ӿ� - <code>java.io.Serializable</code></li>
     * <li>���ӿ� - <code>java.io.Iterable</code></li>
     * <li>Object�� - <code>java.lang.Object</code></li>
     * </ol>
     * </p>
     * <p>
     * ����һ��<code>Class</code>����������ǽӿڣ������´����г�����ĸ��ӿڡ�
     * </p>
     * <p>
     * ����<code>ClassUtil.getSupertypes(java.util.List.class)</code>�����������б�
     * ��˳��Ϊ�����ӿڡ����ӿڡ�Object�ࣩ
     * </p>
     * <ol>
     * <li>���ӿ� - <code>java.util.List</code></li>
     * <li>���ӿ� - <code>java.util.Collection</code></li>
     * <li>���ӿ� - <code>java.util.Iterable</code></li>
     * <li>Object�� - <code>java.lang.Object</code></li>
     * </ol>
     * <p>
     * ����һ�����飬�˷�������һ���б��г�����component���͵ĸ���ͽӿڵ�ά����ͬ���������͡� ���磺
     * <code>ClassUtil.getSupertypes(java.util.ArrayList[][].class)</code>
     * ���������б���˳��Ϊ�������顢�������顢���ӿ����顢Object�����顢���鸸�ࡢ���鸸�ӿڡ�Object�ࣩ
     * <ol>
     * <li>������ - <code>java.util.ArrayList[][]</code></li>
     * <li>�������� - <code>java.util.AbstractList[][]</code></li>
     * <li>�������� - <code>java.util.AbstractCollection[][]</code></li>
     * <li>���ӿ����� - <code>java.util.List[][]</code></li>
     * <li>���ӿ����� - <code>java.util.Collection[][]</code></li>
     * <li>���ӿ����� - <code>java.util.RandomAccess[][]</code></li>
     * <li>���ӿ����� - <code>java.lang.Cloneable[][]</code></li>
     * <li>���ӿ����� - <code>java.io.Serializable[][]</code></li>
     * <li>���ӿ����� - <code>java.io.Iterable[][]</code></li>
     * <li>Object������ - <code>java.lang.Object[][]</code></li>
     * <li>���鸸�� - <code>java.lang.Object[]</code></li>
     * <li>���鸸�ӿ� - <code>java.lang.Cloneable</code></li>
     * <li>���鸸�ӿ� - <code>java.io.Serializable</code></li>
     * <li>Object�� - <code>java.lang.Object</code></li>
     * </ol>
     * ��������<code>void</code>��<code>Void</code>û���κθ��ࡣ
     * <ol>
     * <li><code>java.lang.Void</code></li>
     * </ol>
     * ���ԭ�����ͽ��ᱻת���ɰ�װ�ࡣ ���磺<code>ClassUtil.getSupertypes(int.class)</code>
     * ���������б�
     * <ol>
     * <li><code>java.lang.Integer</code></li>
     * <li><code>java.lang.Number</code></li>
     * <li><code>java.lang.Comparable</code></li>
     * <li><code>java.io.Serializable</code></li>
     * <li><code>java.lang.Object</code></li>
     * </ol>
     * ����ԭ�����͵����鲢���ᱻת�ɰ�װ�ࡣ���磺<code>ClassUtil.getSupertypes(int[][].class)</code>
     * ���������б�
     * <ol>
     * <li><code>int[][]</code></li>
     * <li><code>Object[]</code></li>
     * <li><code>java.lang.Comparable</code></li>
     * <li><code>java.io.Serializable</code></li>
     * <li><code>java.lang.Object</code></li>
     * </ol>
     * </p>
     */
    public static Iterable<Class<?>> getSupertypes(Class<?> clazz) {
        return new Supertypes(clazz);
    }

    /**
     * �������и���ͽӿڡ�
     */
    private static class Supertypes implements Iterable<Class<?>> {
        private Class<?> clazz;

        public Supertypes(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Iterator<Class<?>> iterator() {
            return new SupertypeIterator(clazz);
        }
    }

    /**
     * �������и���ͽӿڵı�������
     */
    private static class SupertypeIterator implements Iterator<Class<?>> {
        private static enum State {
            CLASSES,
            INTERFACES,
            ARRAYS,
            END
        }

        private final Set<Class<?>> processedInterfaces = createHashSet();
        private final LinkedList<Class<?>> interfaceQueue = createLinkedList();

        private Class<?> clazz;
        private int dimension;
        private Iterator<Class<?>> componentTypes;

        private State state;

        public SupertypeIterator(Class<?> clazz) {
            this(clazz, true);
        }

        public SupertypeIterator(Class<?> clazz, boolean convertPrimitive) {
            assertNotNull(clazz, "clazz");

            clazz = convertPrimitive ? getPrimitiveWrapperType(clazz) : clazz;

            queueInterfaces(clazz);

            // �Ƿ�Ϊ���飿
            ArrayInfo ai = getArrayInfo(clazz);

            this.clazz = clazz = ai.componentType;
            this.dimension = ai.dimension;

            // ���ó�ʼ״̬
            if (dimension > 0) {
                componentTypes = new SupertypeIterator(clazz, false);
                state = State.ARRAYS;
            } else if (clazz.isInterface() || clazz == Object.class) {
                state = State.INTERFACES;
            } else {
                state = State.CLASSES;
            }
        }

        public boolean hasNext() {
            return state != State.END;
        }

        public Class<?> next() {
            Class<?> result;

            switch (state) {
                case ARRAYS:
                    result = getArrayType(componentTypes.next(), dimension);

                    if (!componentTypes.hasNext()) {
                        if (--dimension > 0) {
                            state = State.CLASSES;
                            clazz = Object.class;
                        } else {
                            state = State.INTERFACES;
                        }
                    }

                    break;

                case CLASSES:
                    if (dimension > 0) {
                        result = getArrayType(clazz, dimension);

                        if (--dimension == 0) {
                            state = State.INTERFACES;
                        }
                    } else {
                        result = clazz;

                        if (clazz == Void.class) {
                            clazz = null;
                        } else {
                            clazz = clazz.getSuperclass();
                        }

                        if (clazz == null) {
                            if (interfaceQueue.isEmpty()) {
                                state = State.END;
                            }
                        } else {
                            queueInterfaces(clazz);

                            if (clazz == Object.class) {
                                state = State.INTERFACES;
                            }
                        }
                    }

                    break;

                case INTERFACES:
                    if (interfaceQueue.isEmpty()) {
                        state = State.END;
                        result = Object.class;
                    } else {
                        result = interfaceQueue.removeFirst();
                        queueInterfaces(result);
                    }

                    break;

                default:
                    throw new NoSuchElementException();
            }

            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void queueInterfaces(Class<?> clazz) {
            if (clazz.isInterface() && !processedInterfaces.contains(clazz)) {
                interfaceQueue.addLast(clazz);
                processedInterfaces.add(clazz);
            }

            for (Class<?> interfaceClass : clazz.getInterfaces()) {
                if (!processedInterfaces.contains(interfaceClass)) {
                    interfaceQueue.addLast(interfaceClass);
                    processedInterfaces.add(interfaceClass);
                }
            }
        }

    }

    /**
     * ȡ��������Ӧ����Դ����
     */
    public static String getResourceNameOfClass(Class<?> clazz) {
        String className = clazz == null ? null : clazz.getName();

        return getResourceNameOfClass(className);
    }

    /**
     * ȡ��������Ӧ����Դ����
     */
    public static String getResourceNameOfClass(String className) {
        if (className == null) {
            return null;
        }

        return className.trim().replace('.', '/');
    }

    /**
     * ȡ��ָ�����ӿڵ�����<code>public</code>����ǩ����
     * <p>
     * �÷������ص�ǩ���Ǳ�����ġ�
     * </p>
     */
    public static Map<MethodSignature, Class<?>> getMethodSignatures(Class<?>... classes)
            throws IncompatibleMethodSignatureException {
        class Tuple implements Comparable<Tuple> {
            public final MethodSignature signature;
            public final Class<?> declaringClass;

            public Tuple(MethodSignature signature, Class<?> declaringClass) {
                this.signature = signature;
                this.declaringClass = declaringClass;
            }

            public int compareTo(Tuple other) {
                return signature.compareTo(other.signature);
            }
        }

        Map<MethodSignature, Tuple> set = createHashMap();

        for (Class<?> clazz : classes) {
            for (Method method : clazz.getMethods()) {
                MethodSignature signature = new MethodSignature(method);
                Tuple existing = set.get(signature);

                if (existing == null) {
                    set.put(signature, new Tuple(signature, method.getDeclaringClass()));
                } else {
                    if (existing.signature.isOverridingSignatureOf(signature)) {
                        set.put(signature, new Tuple(signature, method.getDeclaringClass()));
                    } else if (!signature.isOverridingSignatureOf(existing.signature)) {
                        throw new IncompatibleMethodSignatureException(incompatibleMethodSignaturesDetected(
                                existing.signature, signature));
                    }
                }
            }
        }

        List<Tuple> tuples = createArrayList(set.size());

        tuples.addAll(set.values());

        Collections.sort(tuples);

        Map<MethodSignature, Class<?>> signatures = createLinkedHashMap();

        for (Tuple tuple : tuples) {
            signatures.put(tuple.signature, tuple.declaringClass);
        }

        return signatures;
    }

    /**
     * ���ù��캯����������
     */
    public static <T> T newInstance(Class<T> clazz) {
        return newInstance(clazz, null, null);
    }

    /**
     * ���ù��캯����������
     */
    public static <T> T newInstance(Class<T> clazz, Class<?>[] paramTypes, Object[] paramValues) {
        try {
            if (ArrayUtil.isEmpty(paramTypes)) {
                return clazz.newInstance();
            }

            Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);

            return constructor.newInstance(paramValues);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                unexpectedException(e.getCause());
                return null;
            }
        } catch (Exception e) {
            unexpectedException(e);
            return null;
        }
    }
}
