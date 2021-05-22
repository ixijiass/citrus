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
package com.alibaba.citrus.generictype;

import static com.alibaba.citrus.util.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * ����һ�����͵���Ϣ��<code>TypeInfo</code>�Ǻ�Java {@link Type}���Ӧ�ģ�����Java Types�����á�
 * <ul>
 * <li>{@link RawTypeInfo}��{@link Class}��Ӧ�����ǲ������������͡�</li>
 * <li>{@link ParameterizedTypeInfo}��{@link ParameterizedType}��Ӧ��</li>
 * <li>{@link TypeVariableInfo}��{@link TypeVariable}��Ӧ��</li>
 * <li>{@link WildcardTypeInfo}��{@link WildcardType}��Ӧ��</li>
 * <li>{@link ArrayTypeInfo}��{@link GenericArrayType}�Լ����������{@link Class}��Ӧ��</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public interface TypeInfo {
    /** ����ȡ��<code>TypeInfo</code>�Ĺ����� */
    Factory factory = Factory.newFactory();

    /** ����{@link Object}��<code>TypeInfo</code>�� */
    RawTypeInfo OBJECT = (RawTypeInfo) factory.getType(Object.class);

    /** ��������{@link boolean}��<code>TypeInfo</code>�� */
    RawTypeInfo PRIMITIVE_BOOLEAN = (RawTypeInfo) factory.getType(boolean.class);

    /** ��������{@link byte}��<code>TypeInfo</code>�� */
    RawTypeInfo PRIMITIVE_BYTE = (RawTypeInfo) factory.getType(byte.class);

    /** ��������{@link char}��<code>TypeInfo</code>�� */
    RawTypeInfo PRIMITIVE_CHAR = (RawTypeInfo) factory.getType(char.class);

    /** ��������{@link double}��<code>TypeInfo</code>�� */
    RawTypeInfo PRIMITIVE_DOUBLE = (RawTypeInfo) factory.getType(double.class);

    /** ��������{@link float}��<code>TypeInfo</code>�� */
    RawTypeInfo PRIMITIVE_FLOAT = (RawTypeInfo) factory.getType(float.class);

    /** ��������{@link int}��<code>TypeInfo</code>�� */
    RawTypeInfo PRIMITIVE_INT = (RawTypeInfo) factory.getType(int.class);

    /** ��������{@link long}��<code>TypeInfo</code>�� */
    RawTypeInfo PRIMITIVE_LONG = (RawTypeInfo) factory.getType(long.class);

    /** ��������{@link short}��<code>TypeInfo</code>�� */
    RawTypeInfo PRIMITIVE_SHORT = (RawTypeInfo) factory.getType(short.class);

    /** ��������{@link void}��<code>TypeInfo</code>�� */
    RawTypeInfo PRIMITIVE_VOID = (RawTypeInfo) factory.getType(void.class);

    /**
     * ȡ�þ������Ͳ�����type erasure��֮���������Ϣ��
     * <p>
     * <table border="1">
     * <tr>
     * <th>TypeInfo</th>
     * <th>����ֵ</th>
     * <th>ʾ��</th>
     * </tr>
     * <tr>
     * <td>{@link RawTypeInfo}</td>
     * <td>����<code>this</code>��</td>
     * <td><code>List</code> �����أ� <code>List</code></td>
     * </tr>
     * <tr>
     * <td>{@link ParameterizedTypeInfo}</td>
     * <td>raw����</td>
     * <td><code>List&lt;Integer&gt;</code> �����أ� <code>List</code></td>
     * </tr>
     * <tr>
     * <td>{@link TypeVariableInfo}</td>
     * <td>��һ��upper bound����</td>
     * <td><code>&lt;E&gt;</code> �����أ� <code>Object</code><br>
     * <code>&lt;E extends Number & Comparable&gt;</code> �����أ�
     * <code>Number</code></td>
     * </tr>
     * <tr>
     * <td>{@link WildcardTypeInfo}</td>
     * <td>��һ��upper bound����</td>
     * <td><code>&lt;?&gt;</code> �����أ� <code>Object</code><br>
     * <code>&lt;? extends Number&gt;</code> �����أ� <code>Number</code></td>
     * </tr>
     * <tr>
     * <td>{@link ArrayTypeInfo}</td>
     * <td>rawType������</td>
     * <td><code>List&lt;Integer&gt;[][]</code> �����أ�<code>List[][]</code></td>
     * </tr>
     * </table>
     * </p>
     */
    Class<?> getRawType();

    /**
     * ȡ�����͵����ơ�
     * <p>
     * <table border="1">
     * <tr>
     * <th>TypeInfo</th>
     * <th>����ֵ</th>
     * <th>ʾ��</th>
     * </tr>
     * <tr>
     * <td>{@link RawTypeInfo}</td>
     * <td>����</td>
     * <td><code>java.util.List</code> �����أ� <code>"java.util.List"</code></td>
     * </tr>
     * <tr>
     * <td>{@link ParameterizedTypeInfo}</td>
     * <td>rawType������</td>
     * <td><code>java.util.List&lt;Integer&gt;</code> �����أ�
     * <code>"java.util.List"</code></td>
     * </tr>
     * <tr>
     * <td>{@link TypeVariableInfo}</td>
     * <td>������</td>
     * <td><code>&lt;E&gt;</code> �����أ� <code>"E"</code></td>
     * </tr>
     * <tr>
     * <td>{@link WildcardTypeInfo}</td>
     * <td>�ʺ�</td>
     * <td><code>&lt;? extends Object&gt;</code> �����أ� <code>"?"</code></td>
     * </tr>
     * <tr>
     * <td>{@link ArrayTypeInfo}</td>
     * <td>rawType������</td>
     * <td><code>java.util.List&lt;Integer&gt;[][]</code> �����أ�
     * <code>"[[Ljava.util.List;"</code></td>
     * </tr>
     * </table>
     * </p>
     */
    String getName();

    /**
     * ȡ�����͵ļ�����ơ�
     * <p>
     * <table border="1">
     * <tr>
     * <th>TypeInfo</th>
     * <th>����ֵ</th>
     * <th>ʾ��</th>
     * </tr>
     * <tr>
     * <td>{@link RawTypeInfo}</td>
     * <td>����</td>
     * <td><code>java.util.List</code> �����أ� <code>"List"</code></td>
     * </tr>
     * <tr>
     * <td>{@link ParameterizedTypeInfo}</td>
     * <td>rawType������</td>
     * <td><code>java.util.List&lt;Integer&gt;</code> �����أ� <code>"List"</code></td>
     * </tr>
     * <tr>
     * <td>{@link TypeVariableInfo}</td>
     * <td>������</td>
     * <td><code>&lt;E&gt;</code> �����أ� <code>"E"</code></td>
     * </tr>
     * <tr>
     * <td>{@link WildcardTypeInfo}</td>
     * <td>�ʺ�</td>
     * <td><code>&lt;? extends Object&gt;</code> �����أ� <code>"?"</code></td>
     * </tr>
     * <tr>
     * <td>{@link ArrayTypeInfo}</td>
     * <td>rawType������</td>
     * <td><code>java.util.List&lt;Integer&gt;[][]</code> �����أ�
     * <code>"List[][]"</code></td>
     * </tr>
     * </table>
     * </p>
     */
    String getSimpleName();

    /**
     * �жϵ�ǰ�����Ƿ�Ϊԭ�����ͣ����磺<code>int</code>��<code>boolean</code>�ȡ�
     */
    boolean isPrimitive();

    /**
     * �Ƿ�Ϊ���飿ֻ���������͵�{@link TypeInfo}���п��������飺
     * <ol>
     * <li>{@link ArrayTypeInfo} - ���磺<code>int[]</code>,
     * <code>String[][]</code>, <code>List&lt;Integer&gt;[]</code>�ȡ�</li>
     * <li>{@link WildcardTypeInfo} - ���磺<code>&lt;? extends int[]&gt;</code>,
     * <code>&lt;? extends String[][]&gt;</code>,
     * <code>&lt;? extends List&lt;Integer&gt;[]&gt;</code>�ȡ�</li>
     * </ol>
     */
    boolean isArray();

    /**
     * �жϵ�ǰ�����Ƿ�Ϊ�ӿڡ�
     */
    boolean isInterface();

    /**
     * ���統ǰ{@link TypeInfo}��primtive���ͣ���<code>int</code>�����򷵻����װ���ͣ���
     * <code>Integer</code>�������򷵻�<code>this</code>����
     */
    TypeInfo getPrimitiveWrapperType();

    /**
     * ȡ������Ԫ�ص����͡�
     * <p>
     * ���ڶ�ά���飬��������Ԫ�����͡����磺<code>int[][]</code>����<code>int</code>��
     * </p>
     * <p>
     * ����������飬�򷵻ر���<code>this</code>��
     * </p>
     */
    TypeInfo getComponentType();

    /**
     * ȡ��ֱ�ӵ�����Ԫ�ص����͡�
     * <p>
     * ���ڶ�ά���飬������һ��Ԫ�����͡����磺<code>int[][]</code>����<code>int[]</code>��
     * </p>
     * <p>
     * ����������飬�򷵻ر���<code>this</code>��
     * </p>
     */
    TypeInfo getDirectComponentType();

    /**
     * ���������ά�ȣ�����������飬�򷵻�<code>0</code>��
     */
    int getDimension();

    /**
     * ȡ�����еĽӿڣ������ǰ���ǽӿڵĻ���������ǰ�ࡣ
     * 
     * @see #getSupertypes()
     */
    List<TypeInfo> getInterfaces();

    /**
     * ȡ�����еĻ��࣬�ӵ�ǰ�������ƣ������ǰ�಻�ǽӿڵĻ���������ǰ�ࡣ
     * 
     * @see #getSupertypes()
     */
    List<TypeInfo> getSuperclasses();

    /**
     * ȡ�����еĻ���ͽӿڣ��ӵ�ǰ�������ƣ�������ǰ�ࡣ
     * <p>
     * ���ڰ˸�primitive���ͺ���������<code>void</code>������û���κθ��ࡣ
     * </p>
     * <p>
     * ���һ�����ͣ��Ȳ��ǽӿڣ�Ҳ�������飬�����´����г������͵ĸ��༰�ӿڡ� �������
     * <code>java.util.ArrayList</code>���ͣ����õ������б���˳��Ϊ�����ࡢ���ࡢ���ӿڡ�Object�ࣩ
     * </p>
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
     * <p>
     * ����һ���ӿ����ͣ������´����г�����ĸ��ӿڡ� �������<code>java.util.List</code>
     * ���ͣ����õ������б���˳��Ϊ�����ӿڡ����ӿڡ�Object�ࣩ
     * </p>
     * <ol>
     * <li>���ӿ� - <code>java.util.List</code></li>
     * <li>���ӿ� - <code>java.util.Collection</code></li>
     * <li>���ӿ� - <code>java.util.Iterable</code></li>
     * <li>Object�� - <code>java.lang.Object</code></li>
     * </ol>
     * <p>
     * ����һ�����飬�˷�������һ���б��г�����component���͵ĸ���ͽӿڵ�ά����ͬ���������͡� ���磺
     * <code>java.util.ArrayList[][]</code>
     * ��ȡ�������б���˳��Ϊ�������顢�������顢���ӿ����顢Object�����顢���ӿ����顢Object�����顢���鸸�ӿڡ�Object�ࣩ��
     * </p>
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
     * <li>���ӿ����� - <code>java.lang.Cloneable[]</code></li>
     * <li>���ӿ����� - <code>java.io.Serializable[]</code></li>
     * <li>Object������ - <code>java.lang.Object[]</code></li>
     * <li>���鸸�ӿ� - <code>java.lang.Cloneable</code></li>
     * <li>���鸸�ӿ� - <code>java.io.Serializable</code></li>
     * <li>Object�� - <code>java.lang.Object</code></li>
     * </ol>
     * <p>
     * ԭ�����͵�����Ҳ�����ơ����磺<code>int[][]</code>���õ������б�
     * </p>
     * <ol>
     * <li><code>int[][]</code></li>
     * <li><code>java.lang.Cloneable[]</code></li>
     * <li><code>java.io.Serializable[]</code></li>
     * <li><code>Object[]</code></li>
     * <li><code>java.lang.Cloneable</code></li>
     * <li><code>java.io.Serializable</code></li>
     * <li><code>java.lang.Object</code></li>
     * </ol>
     */
    List<TypeInfo> getSupertypes();

    /**
     * �����л���ͽӿ��У�����rawClassΪ<code>equivalentClass</code>���͵�{@link TypeInfo}��
     * <p>
     * ���磬
     * <code>ArrayList&lt;Integer&gt;.getSupertype(List.class) =&gt; List&lt;Integer&gt;</code>
     * </p>
     */
    TypeInfo getSupertype(Class<?> equivalentClass);

    /**
     * ��ָ���������з���ʵ�����͡�
     * <p>
     * �൱��{@link resolve(context, true)}��
     * </p>
     */
    TypeInfo resolve(GenericDeclarationInfo context);

    /**
     * ��ָ���������з���ʵ�����͡�
     * <p>
     * ����<code>context</code>Ϊ<code>List&lt;E=Integer&gt;</code>��<br>
     * ��ô����<code>List&lt;E&gt;</code>�Ľ��Ϊ��<code>Integer</code>��
     * </p>
     * <p>
     * ���<code>includeBaseType==false</code>����ô�������ͱ���ʱ��������ȡ����baseType�� ���磺
     * </p>
     * 
     * <pre>
     * class MyClass&lt;A&gt; {
     *     List&lt;A&gt; listA;
     * }
     * 
     * interface Collection&lt;E&gt; extends Iterable&lt;E&gt; {
     * }
     * 
     * interface Iterable&lt;T&gt; {
     * }
     * </pre>
     * <p>
     * ��ô��<code>Iterable&lt;T=E&gt;.resolve(List&lt;A&gt;)</code>��������
     * <code>Iterable&lt;T=A&gt;</code>��
     * </p>
     * <p>
     * ���<code>includeBaseType==true</code>����ô���������ý�����
     * <code>Iterable&lt;T=Object&gt;</code>��
     * </p>
     */
    TypeInfo resolve(GenericDeclarationInfo context, boolean includeBaseType);

    /**
     * ��������<code>TypeInfo</code>�Ĺ�����
     */
    abstract class Factory {
        /**
         * ȡ��ָ��{@link Type}��Ӧ��{@link TypeInfo}����
         */
        public abstract TypeInfo getType(Type type);

        /**
         * ȡ��ָ��������{@link Class}��Ӧ��{@link ClassTypeInfo}����
         * <p>
         * �����������������࣬���Ϊ���飬���׳�<code>IllegalArgumentException</code>��
         * </p>
         * <p>
         * ��������{@link getType(Type)}����ȫ��ͬ�ģ�ֻ��ʡȥ��һЩcast���ѡ�
         * </p>
         * <p>
         * ����{@link ClassTypeInfo}Ҳ��{@link GenericDeclarationInfo}�����࣬<br>
         * ��˵�����Ϊ{@link Class}ʱ���������Ľ����{@link
         * getGenericDeclaration(GenericDeclaration)}Ҳ��ͬ��
         * </p>
         */
        public final ClassTypeInfo getClassType(Class<?> type) {
            assertTrue(type != null && !type.isArray(), "type should not be array: %s", type.getName());
            return (ClassTypeInfo) getType(type);
        }

        /**
         * ȡ��ָ��{@link ParameterizedType}��Ӧ��{@link ClassTypeInfo}����
         * <p>
         * ��������{@link getType(Type)}����ȫ��ͬ�ģ�ֻ����ȥ��һЩcast���ѡ�
         * </p>
         */
        public final ClassTypeInfo getClassType(ParameterizedType type) {
            return (ClassTypeInfo) getType(type);
        }

        /**
         * ȡ��һ��{@link TypeInfo}����
         */
        public abstract TypeInfo[] getTypes(Type[] types);

        /**
         * ȡ��ָ��{@link GenericDeclaration}��Ӧ��{@link GenericDeclarationInfo}���� *
         * <p>
         * �����������������࣬���Ϊ���飬���׳�<code>IllegalArgumentException</code>��
         * </p>
         */
        public abstract GenericDeclarationInfo getGenericDeclaration(GenericDeclaration declaration);

        /**
         * ����һ�����������͡�
         */
        public abstract ParameterizedTypeInfo getParameterizedType(TypeInfo type, TypeInfo... args);

        /**
         * ����һ�����������͡�
         */
        public final ParameterizedTypeInfo getParameterizedType(TypeInfo type, Type... args) {
            return getParameterizedType(type, getTypes(args));
        }

        /**
         * ����һ�����������͡�
         */
        public final ParameterizedTypeInfo getParameterizedType(Class<?> type, Type... args) {
            return getParameterizedType(getType(type), getTypes(args));
        }

        /**
         * ����һ���������͡�
         */
        public abstract ArrayTypeInfo getArrayType(TypeInfo componentType, int dimension);

        /**
         * ����һ���������͡�
         */
        public final ArrayTypeInfo getArrayType(Class<?> componentType, int dimension) {
            return getArrayType(getType(componentType), dimension);
        }

        /**
         * ȡ��ָ��{@link Method}��Ӧ��{@link MethodInfo}����
         * <p>
         * ��������{@link getGenericDeclaration(GenericDeclaration)}
         * ����ȫ��ͬ�ģ�ֻ����ȥ��һЩcast���ѡ�
         * </p>
         */
        public final MethodInfo getMethod(Method method) {
            return (MethodInfo) getGenericDeclaration(method);
        }

        /**
         * ��ָ��������Ϊ�����ģ�ȡ��ָ��{@link Method}��Ӧ��{@link MethodInfo}����
         */
        public final MethodInfo getMethod(Method method, TypeInfo type) {
            MethodInfo result = getMethod(method);

            if (type != null) {
                Class<?> rawType = type.getRawType();

                assertTrue(method.getDeclaringClass().isAssignableFrom(rawType),
                        "method \"%s\" does not belong to type \"%s\"", method, type);

                if (type instanceof ClassTypeInfo) {
                    result = result.resolve((ClassTypeInfo) type, false);
                }
            }

            return result;
        }

        /**
         * ȡ��ָ��{@link Constructor}��Ӧ��{@link MethodInfo}����
         * <p>
         * ��������{@link getGenericDeclaration(GenericDeclaration)}
         * ����ȫ��ͬ�ģ�ֻ����ȥ��һЩcast���ѡ�
         * </p>
         */
        public final MethodInfo getConstructor(Constructor<?> constructor) {
            return (MethodInfo) getGenericDeclaration(constructor);
        }

        /**
         * ��ָ��������Ϊ�����ģ�ȡ��ָ��{@link Constructor}��Ӧ��{@link MethodInfo}����
         */
        public final MethodInfo getConstructor(Constructor<?> constructor, TypeInfo type) {
            MethodInfo result = getConstructor(constructor);

            if (type != null) {
                Class<?> rawType = type.getRawType();

                assertTrue(constructor.getDeclaringClass().equals(rawType),
                        "constructor \"%s\" does not belong to type \"%s\"", constructor, type);

                if (type instanceof ClassTypeInfo) {
                    result = result.resolve((ClassTypeInfo) type, false);
                }
            }

            return result;
        }

        /**
         * ȡ��ָ��{@link Field}��Ӧ��{@link FieldInfo}����
         */
        public abstract FieldInfo getField(Field field);

        /**
         * ��ָ��������Ϊ�����ģ�ȡ��ָ��{@link Field}��Ӧ��{@link FieldInfo}����
         */
        public final FieldInfo getField(Field field, TypeInfo type) {
            FieldInfo result = getField(field);

            if (type != null) {
                Class<?> rawType = type.getRawType();

                assertTrue(field.getDeclaringClass().isAssignableFrom(rawType),
                        "field \"%s\" does not belong to type \"%s\"", field, type);

                if (type instanceof ClassTypeInfo) {
                    result = result.resolve((ClassTypeInfo) type, false);
                }
            }

            return result;
        }

        /**
         * ����factory����������compileʱ������impl package��
         */
        private static Factory newFactory() {
            String factoryImplName = Factory.class.getPackage().getName() + ".impl.TypeInfoFactory";
            Factory factoryImpl = null;

            try {
                factoryImpl = (Factory) Factory.class.getClassLoader().loadClass(factoryImplName).newInstance();
            } catch (Exception e) {
                unexpectedException(e, "Failed to create TypeInfo.Factory");
            }

            return factoryImpl;
        }
    }
}
