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

import static org.junit.Assert.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.citrus.generictype.introspect.PropertyInfo;

/**
 * ����<code>SimplePropertiesAnalyzer</code>��
 * 
 * @author Michael Zhou
 */
public class SimplePropertiesAnalyzerTests extends AbstractPropertiesAnalyzerTests {
    /**
     * ���Բ�ͬ�ķ��ʿ��ơ�
     */
    @Test
    public void accessible() {
        @SuppressWarnings("unused")
        class MyClass {
            private String getPrivateString() {
                return null;
            }

            private void setPrivateString(String s) {
            }

            String getPackageString() {
                return null;
            }

            void setPackageString(String s) {
            }

            protected String getProtectedString() {
                return null;
            }

            protected void setProtectedString(String s) {
            }

            public String getPublicString() {
                return null;
            }

            public void setPublicString(String s) {
            }
        }

        TypeIntrospectionInfo ci = getClassInfo(MyClass.class);
        Map<String, List<PropertyInfo>> props = ci.getProperties();

        assertNull(props.get("privateString"));
        assertNull(props.get("packageString"));
        assertNull(props.get("protectedString"));

        List<PropertyInfo> pubStrs = props.get("publicString");

        assertEquals(1, pubStrs.size());
        assertPropertyInfo(pubStrs.get(0), MyClass.class, "publicString", String.class, false,
                "getPublicString()Ljava/lang/String;", "setPublicString(Ljava/lang/String;)V");
    }

    /**
     * ���Բ�ͬ�ķ�����̬��
     */
    @Test
    public void signatures() {
        @SuppressWarnings("unused")
        class MyClass {
            // get��û�з���ֵ
            public void getNoReturn() {
            }

            // is������ֵ����boolean
            public String isNotBoolean() {
                return null;
            }

            // get������
            public String getWithParams(String s) {
                return null;
            }

            // set���з���ֵ
            public String setWithReturn(int i) {
                return null;
            }

            // set����˫����
            public void setWith2Params(int i, long j) {
            }

            // ������set
            public void setNormal(String s) {
            }

            // ������get
            public String getNormal() {
                return null;
            }

            // ������boolean get������getNormal���Ͳ�ͬ
            public boolean isNormal() {
                return false;
            }

            // ��д��property
            public URL getURL() {
                return null;
            }

            // Сд��property
            public URL getUrl() {
                return null;
            }

            // ������set������Object.getClass���Ͳ�ͬ
            public void setClass(String s) {
            }

            // ������boolean get
            public boolean isBoolean() {
                return false;
            }

            // ������boolean get������isBoolean��ͻ��������
            public boolean getBoolean() {
                return false;
            }

            // generic property
            public <T> List<T> getList() {
                return null;
            }

            // generic property
            public <T> void setList(List<T> list) {
            }
        }

        TypeIntrospectionInfo ci = getClassInfo(MyClass.class);
        Map<String, List<PropertyInfo>> props = ci.getProperties();
        List<PropertyInfo> pis;

        assertNull(props.get("noReturn"));
        assertNull(props.get("notBoolean"));
        assertNull(props.get("withParams"));

        pis = props.get("withReturn");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "withReturn", int.class, false, null,
                "setWithReturn(I)Ljava/lang/String;");

        assertNull(props.get("with2Params"));

        pis = props.get("normal");
        assertEquals(2, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "normal", String.class, false, "getNormal()Ljava/lang/String;",
                "setNormal(Ljava/lang/String;)V");
        assertPropertyInfo(pis.get(1), MyClass.class, "normal", boolean.class, false, "isNormal()Z", null);

        pis = props.get("URL");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "URL", URL.class, false, "getURL()Ljava/net/URL;", null);

        pis = props.get("url");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "url", URL.class, false, "getUrl()Ljava/net/URL;", null);

        pis = props.get("class");
        assertEquals(2, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "class", String.class, false, null,
                "setClass(Ljava/lang/String;)V");
        assertPropertyInfo(pis.get(1), Object.class, "class", Class.class, true, "getClass()Ljava/lang/Class;", null);

        pis = props.get("boolean");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "boolean", boolean.class, false, new String[] { "isBoolean()Z",
                "getBoolean()Z" }, null);

        pis = props.get("list");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "list", List.class, true, "getList()Ljava/util/List;",
                "setList(Ljava/util/List;)V");
    }

    @Override
    protected ClassAnalyzer[] getAnalyzers() {
        return new ClassAnalyzer[] { new SimplePropertiesAnalyzer() };
    }
}
