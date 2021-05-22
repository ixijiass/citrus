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
 * ����<code>MappedPropertiesAnalyzer</code>��
 * 
 * @author Michael Zhou
 */
public class MappedPropertiesAnalyzerTests extends AbstractPropertiesAnalyzerTests {
    /**
     * ���Բ�ͬ�ķ��ʿ��ơ�
     */
    @Test
    public void accessible() {
        @SuppressWarnings("unused")
        class MyClass {
            private String getPrivateString(String key) {
                return null;
            }

            private void setPrivateString(String key, String s) {
            }

            String getPackageString(String key) {
                return null;
            }

            void setPackageString(String key, String s) {
            }

            protected String getProtectedString(String key) {
                return null;
            }

            protected void setProtectedString(String key, String s) {
            }

            public String getPublicString(String key) {
                return null;
            }

            public void setPublicString(String key, String s) {
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
                "getPublicString(Ljava/lang/String;)Ljava/lang/String;",
                "setPublicString(Ljava/lang/String;Ljava/lang/String;)V");
    }

    /**
     * ���Բ�ͬ�ķ�����̬��
     */
    @Test
    public void signatures() {
        @SuppressWarnings("unused")
        class MyClass {
            // get��û�з���ֵ
            public void getNoReturn(String key) {
            }

            // ��֧��is
            public String isNotBoolean(String key) {
                return null;
            }

            // get������
            public String getWithParams(String key, String s) {
                return null;
            }

            // set���з���ֵ
            public String setWithReturn(String key, int i) {
                return null;
            }

            // set����˫����
            public void setWith2Params(String key, int i, long j) {
            }

            // ������set
            public void setNormal(String key, String s) {
            }

            // ������get
            public String getNormal(String key) {
                return null;
            }

            // ��֧��is
            public boolean isNormal(String key) {
                return false;
            }

            // ��д��property
            public URL getURL(String key) {
                return null;
            }

            // Сд��property
            public URL getUrl(String key) {
                return null;
            }

            // ������set������Object.getClass���Ͳ�ͬ
            public void setClass(String key, String s) {
            }

            // ��֧��is
            public boolean isBoolean(String key) {
                return false;
            }

            // ������boolean get
            public boolean getBoolean(String key) {
                return false;
            }

            // generic property
            public <T> List<T> getList(String key) {
                return null;
            }

            // generic property
            public <T> void setList(String key, List<T> list) {
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
                "setWithReturn(Ljava/lang/String;I)Ljava/lang/String;");

        assertNull(props.get("with2Params"));

        pis = props.get("normal");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "normal", String.class, false,
                "getNormal(Ljava/lang/String;)Ljava/lang/String;", "setNormal(Ljava/lang/String;Ljava/lang/String;)V");

        pis = props.get("URL");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "URL", URL.class, false,
                "getURL(Ljava/lang/String;)Ljava/net/URL;", null);

        pis = props.get("url");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "url", URL.class, false,
                "getUrl(Ljava/lang/String;)Ljava/net/URL;", null);

        pis = props.get("class");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "class", String.class, false, null,
                "setClass(Ljava/lang/String;Ljava/lang/String;)V");

        pis = props.get("boolean");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "boolean", boolean.class, false,
                "getBoolean(Ljava/lang/String;)Z", null);

        pis = props.get("list");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "list", List.class, true,
                "getList(Ljava/lang/String;)Ljava/util/List;", "setList(Ljava/lang/String;Ljava/util/List;)V");
    }

    @Override
    protected ClassAnalyzer[] getAnalyzers() {
        return new ClassAnalyzer[] { new MappedPropertiesAnalyzer() };
    }
}
