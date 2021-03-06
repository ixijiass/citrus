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
 * 测试<code>IndexedPropertiesAnalyzer</code>。
 * 
 * @author Michael Zhou
 */
public class IndexedPropertiesAnalyzerTests extends AbstractPropertiesAnalyzerTests {
    /**
     * 测试不同的访问控制。
     */
    @Test
    public void accessible() {
        @SuppressWarnings("unused")
        class MyClass {
            private String getPrivateString(int index) {
                return null;
            }

            private void setPrivateString(int index, String s) {
            }

            String getPackageString(int index) {
                return null;
            }

            void setPackageString(int index, String s) {
            }

            protected String getProtectedString(int index) {
                return null;
            }

            protected void setProtectedString(int index, String s) {
            }

            public String getPublicString(int index) {
                return null;
            }

            public void setPublicString(int index, String s) {
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
                "getPublicString(I)Ljava/lang/String;", "setPublicString(ILjava/lang/String;)V");
    }

    /**
     * 测试不同的方法形态。
     */
    @Test
    public void signatures() {
        @SuppressWarnings("unused")
        class MyClass {
            // get但没有返回值
            public void getNoReturn(int index) {
            }

            // 不支持is
            public String isNotBoolean(int index) {
                return null;
            }

            // get带参数
            public String getWithParams(int index, String s) {
                return null;
            }

            // set带有返回值
            public String setWithReturn(int index, int i) {
                return null;
            }

            // set带有双参数
            public void setWith2Params(int index, int i, long j) {
            }

            // 正常的set
            public void setNormal(int index, String s) {
            }

            // 正常的get
            public String getNormal(int index) {
                return null;
            }

            // 不支持is
            public boolean isNormal(int index) {
                return false;
            }

            // 大写的property
            public URL getURL(int index) {
                return null;
            }

            // 小写的property
            public URL getUrl(int index) {
                return null;
            }

            // 正常的set，但和Object.getClass类型不同
            public void setClass(int index, String s) {
            }

            // 不支持is
            public boolean isBoolean(int index) {
                return false;
            }

            // 正常的boolean get
            public boolean getBoolean(int index) {
                return false;
            }

            // generic property
            public <T> List<T> getList(int index) {
                return null;
            }

            // generic property
            public <T> void setList(int index, List<T> list) {
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
                "setWithReturn(II)Ljava/lang/String;");

        assertNull(props.get("with2Params"));

        pis = props.get("normal");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "normal", String.class, false, "getNormal(I)Ljava/lang/String;",
                "setNormal(ILjava/lang/String;)V");

        pis = props.get("URL");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "URL", URL.class, false, "getURL(I)Ljava/net/URL;", null);

        pis = props.get("url");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "url", URL.class, false, "getUrl(I)Ljava/net/URL;", null);

        pis = props.get("class");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "class", String.class, false, null,
                "setClass(ILjava/lang/String;)V");

        pis = props.get("boolean");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "boolean", boolean.class, false, "getBoolean(I)Z", null);

        pis = props.get("list");
        assertEquals(1, pis.size());
        assertPropertyInfo(pis.get(0), MyClass.class, "list", List.class, true, "getList(I)Ljava/util/List;",
                "setList(ILjava/util/List;)V");
    }

    @Override
    protected ClassAnalyzer[] getAnalyzers() {
        return new ClassAnalyzer[] { new IndexedPropertiesAnalyzer() };
    }
}
