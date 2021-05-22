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

import static com.alibaba.citrus.generictype.TypeInfo.*;
import static com.alibaba.citrus.util.ClassUtil.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alibaba.citrus.test.runner.Prototyped;
import com.alibaba.citrus.test.runner.Prototyped.Prototypes;
import com.alibaba.citrus.test.runner.Prototyped.TestData;
import com.alibaba.citrus.test.runner.Prototyped.TestName;

/**
 * ����{@link RawTypeInfo}��
 * 
 * @author Michael Zhou
 */
@RunWith(Prototyped.class)
public class RawTypeTests extends BaseTypeTests implements Cloneable {
    private transient RawTypeInfo typeInfo;
    private Class<?> clazz; // rawClass
    private boolean generic; // �ǲ���generic��
    private String name; // ����
    private String simpleName; // ���
    private Class<?> wrapper; // ��װ��
    private boolean isInterface; // �Ƿ�Ϊ�ӿ�
    private String params; // ������toString
    private String toString; // toString���
    private String[] supertypes; // ���ࡢ�ӿ�
    private String[] args; // actual args

    @Before
    public void init() {
        this.typeInfo = (RawTypeInfo) factory.getType(clazz);
    }

    @TestName
    public String testName() {
        return getSimpleClassName(clazz);
    }

    @Prototypes
    public static TestData<RawTypeTests> data() {
        TestData<RawTypeTests> data = TestData.getInstance(RawTypeTests.class);
        RawTypeTests prototype;

        // =========================
        // ��generic�ࡢ�ӿ�
        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = String.class;
        prototype.generic = false;
        prototype.name = "java.lang.String";
        prototype.simpleName = "String";
        prototype.wrapper = null;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "String";
        prototype.supertypes = new String[] { "String", "CharSequence", "Comparable<T=String>", "Serializable",
                "Object" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = Short.class;
        prototype.generic = false;
        prototype.name = "java.lang.Short";
        prototype.simpleName = "Short";
        prototype.wrapper = null;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "Short";
        prototype.supertypes = new String[] { "Short", "Number", "Comparable<T=Short>", "Serializable", "Object" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = Externalizable.class;
        prototype.generic = false;
        prototype.name = "java.io.Externalizable";
        prototype.simpleName = "Externalizable";
        prototype.wrapper = null;
        prototype.isInterface = true;
        prototype.params = "[]";
        prototype.toString = "Externalizable";
        prototype.supertypes = new String[] { "Externalizable", "Serializable", "Object" };
        prototype.args = new String[] {};

        // =========================
        // generic�ࡢ�ӿ�
        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = List.class;
        prototype.generic = true;
        prototype.name = "java.util.List";
        prototype.simpleName = "List";
        prototype.wrapper = null;
        prototype.isInterface = true;
        prototype.params = "[E]";
        prototype.toString = "List<E>";
        prototype.supertypes = new String[] { "List<E>", "Collection<E=E>", "Iterable<T=E>", "Object" };
        prototype.args = new String[] { "Object" };

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = Map.class;
        prototype.generic = true;
        prototype.name = "java.util.Map";
        prototype.simpleName = "Map";
        prototype.wrapper = null;
        prototype.isInterface = true;
        prototype.params = "[K, V]";
        prototype.toString = "Map<K, V>";
        prototype.supertypes = new String[] { "Map<K, V>", "Object" };
        prototype.args = new String[] { "Object", "Object" };

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = ArrayList.class;
        prototype.generic = true;
        prototype.name = "java.util.ArrayList";
        prototype.simpleName = "ArrayList";
        prototype.wrapper = null;
        prototype.isInterface = false;
        prototype.params = "[E]";
        prototype.toString = "ArrayList<E>";
        prototype.supertypes = new String[] { "ArrayList<E>", "AbstractList<E=E>", "AbstractCollection<E=E>",
                "Cloneable", "Collection<E=E>", "Iterable<T=E>", "List<E=E>", "RandomAccess", "Serializable", "Object" };
        prototype.args = new String[] { "Object" };

        // -----------------
        class LocalTest<A extends Number, B extends A> {
        }
        prototype = data.newPrototype();
        prototype.clazz = LocalTest.class;
        prototype.generic = true;
        prototype.name = RawTypeTests.class.getName() + "$1LocalTest";
        prototype.simpleName = "RawTypeTests$1LocalTest";
        prototype.wrapper = null;
        prototype.isInterface = false;
        prototype.params = "[A, B]";
        prototype.toString = "RawTypeTests$1LocalTest<A, B>";
        prototype.supertypes = new String[] { "RawTypeTests$1LocalTest<A, B>", "Object" };
        prototype.args = new String[] { "Number", "Number" };

        // =========================
        // primitive��
        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = boolean.class;
        prototype.generic = false;
        prototype.name = "boolean";
        prototype.simpleName = "boolean";
        prototype.wrapper = Boolean.class;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "boolean";
        prototype.supertypes = new String[] { "boolean" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = byte.class;
        prototype.generic = false;
        prototype.name = "byte";
        prototype.simpleName = "byte";
        prototype.wrapper = Byte.class;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "byte";
        prototype.supertypes = new String[] { "byte" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = char.class;
        prototype.generic = false;
        prototype.name = "char";
        prototype.simpleName = "char";
        prototype.wrapper = Character.class;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "char";
        prototype.supertypes = new String[] { "char" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = double.class;
        prototype.generic = false;
        prototype.name = "double";
        prototype.simpleName = "double";
        prototype.wrapper = Double.class;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "double";
        prototype.supertypes = new String[] { "double" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = float.class;
        prototype.generic = false;
        prototype.name = "float";
        prototype.simpleName = "float";
        prototype.wrapper = Float.class;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "float";
        prototype.supertypes = new String[] { "float" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = int.class;
        prototype.generic = false;
        prototype.name = "int";
        prototype.simpleName = "int";
        prototype.wrapper = Integer.class;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "int";
        prototype.supertypes = new String[] { "int" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = long.class;
        prototype.generic = false;
        prototype.name = "long";
        prototype.simpleName = "long";
        prototype.wrapper = Long.class;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "long";
        prototype.supertypes = new String[] { "long" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = short.class;
        prototype.generic = false;
        prototype.name = "short";
        prototype.simpleName = "short";
        prototype.wrapper = Short.class;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "short";
        prototype.supertypes = new String[] { "short" };
        prototype.args = new String[] {};

        // =========================
        // void��Void
        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = void.class;
        prototype.generic = false;
        prototype.name = "void";
        prototype.simpleName = "void";
        prototype.wrapper = Void.class;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "void";
        prototype.supertypes = new String[] { "void" };
        prototype.args = new String[] {};

        // -----------------
        prototype = data.newPrototype();
        prototype.clazz = Void.class;
        prototype.generic = false;
        prototype.name = "java.lang.Void";
        prototype.simpleName = "Void";
        prototype.wrapper = null;
        prototype.isInterface = false;
        prototype.params = "[]";
        prototype.toString = "Void";
        prototype.supertypes = new String[] { "Void", "Object" };
        prototype.args = new String[] {};

        return data;
    }

    @Test
    public void ����() {
        // 1. ���ԣ��߳�1����rawClassInfoʱ��
        // ��һ���߳�2��ͼ����ParameterizedType����������ͬһ��rawClassInfo��
        // ��ʱ��classCache��ȡ�õ�rawClassInfo����δ��ʼ����ɣ�����getParamters()Ϊnull

        // 2. class cache�е�class�������Ժ�weak ref��֮���ͷţ�����ͬһ��class��α���ʼ����
    }

    /**
     * from {@link GenericDeclarationInfo}��
     */
    @Test
    public void isGeneric() {
        assertEquals(generic, typeInfo.isGeneric());
    }

    /**
     * from {@link GenericDeclarationInfo}��
     */
    @Test
    public void getTypeParameters() {
        assertEquals(params, typeInfo.getTypeParameters().toString());
    }

    @Test
    public void getName() {
        assertEquals(name, typeInfo.getName());
    }

    @Test
    public void getSimpleName() {
        assertEquals(simpleName, typeInfo.getSimpleName());
    }

    @Test
    public void getRawType() {
        assertEquals(clazz, typeInfo.getRawType());
    }

    @Test
    public void isArray() {
        assertFalse(typeInfo.isArray());
    }

    @Test
    public void isInterface() {
        assertEquals(isInterface, typeInfo.isInterface());
    }

    @Test
    public void primitiveWrapper() {
        if (wrapper == null) {
            assertFalse(typeInfo.isPrimitive());
            assertSame(typeInfo, typeInfo.getPrimitiveWrapperType());
        } else {
            assertTrue(typeInfo.isPrimitive());
            assertNotSame(typeInfo, typeInfo.getPrimitiveWrapperType());
            assertEquals(factory.getType(wrapper), typeInfo.getPrimitiveWrapperType());
        }
    }

    @Test
    public void getDimension() {
        assertEquals(0, typeInfo.getDimension());
    }

    @Test
    public void getComponentType() {
        assertSame(typeInfo, typeInfo.getComponentType());
    }

    @Test
    public void getDirectComponentType() {
        assertSame(typeInfo, typeInfo.getDirectComponentType());
    }

    @Test
    public void getSupertypes() {
        assertSupertypes(typeInfo, supertypes);
    }

    @Test
    public void resolve() {
        assertSame(typeInfo, typeInfo.resolve(null));
        assertSame(typeInfo, typeInfo.resolve(null, true));
        assertSame(typeInfo, typeInfo.resolve(null, false));
    }

    @Test
    public void getActualTypeParameters() {
        List<TypeVariableInfo> vars = typeInfo.getTypeParameters();
        List<TypeInfo> actualArgs = typeInfo.getActualTypeArguments();

        assertEquals(vars.size(), actualArgs.size());
        assertEquals(vars.size(), args.length);

        for (int i = 0; i < vars.size(); i++) {
            assertEquals(args[i], actualArgs.get(i).toString());
            assertEquals(actualArgs.get(i), typeInfo.getActualTypeArgument(vars.get(i).getName()));
        }
    }

    @Test
    public void toString_() {
        assertEquals(toString, typeInfo.toString());
    }

    @Test
    public void equalsHashCode() {
        TypeInfo newType = factory.getType(typeInfo.getRawType());

        assertSame(newType, typeInfo);

        newType = factory.getType(Exception.class);

        assertThat(typeInfo, not(equalTo(newType)));
        assertNotSame(newType, typeInfo);
        assertThat(typeInfo.hashCode(), not(equalTo(newType.hashCode())));
    }
}
