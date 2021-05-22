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
package com.alibaba.citrus.service.mappingrule;

import static junit.framework.Assert.*;

import java.util.Locale;

import org.junit.Test;

import com.alibaba.citrus.util.i18n.LocaleUtil;

public class FallbackTemplateTests extends AbstractMappingRuleTests {
    @Override
    protected String getMappedName_forCacheTest() {
        return mappingRules.getMappedName("fallback.template", "aaa/bbb/myOtherModule.vm");
    }

    @Override
    protected boolean isProductionModeSensible() {
        return true;
    }

    @Test
    public void testFallbackTemplateMappingRule() {
        String result;

        // Exact match
        result = mappingRules.getMappedName("fallback.template", "aaa/bbb/myOtherModule.vm");
        assertEquals("myprefix/aaa/bbb/myOtherModule.vm", result);
        assertSame(result, mappingRules.getMappedName("fallback.template", "aaa/bbb/myOtherModule.vm")); // ����cache���ڣ����Եڶ���Ӧ��������

        // Exact match���պ�׺
        result = mappingRules.getMappedName("fallback.template", "aaa/bbb/myOtherModule");
        assertEquals("myprefix/aaa/bbb/myOtherModule", result);//������������չ��������Ҳ���
        assertSame(result, mappingRules.getMappedName("fallback.template", "aaa/bbb/myOtherModule")); // ����cache���ڣ����Եڶ���Ӧ��������

        // Fallback level 1 �����׺
        result = mappingRules.getMappedName("fallback.template", "aaa/bbb/nonexistModule.vm");
        assertEquals("myprefix/aaa/bbb/default.vm", result);//�����ڷ���default��������չ����vm������Ҳ��vm
        assertSame(result, mappingRules.getMappedName("fallback.template", "aaa/bbb/nonexistModule.vm")); // ����cache���ڣ����Եڶ���Ӧ��������

        // Fallback level 1���պ�׺
        result = mappingRules.getMappedName("fallback.template", "aaa/bbb/nonexistModule");
        assertEquals("myprefix/aaa/bbb/default", result);//�����ڷ���default��������չ���ǿգ�����Ҳ�ǿ�
        assertSame(result, mappingRules.getMappedName("fallback.template", "aaa/bbb/nonexistModule")); // ����cache���ڣ����Եڶ���Ӧ��������

        // Fallback level 2 �����׺
        result = mappingRules.getMappedName("fallback.template", "aaa/nonexistPackage/nonexistModule.vm");
        assertEquals("myprefix/aaa/default.vm", result);//�����ڷ���default��������չ����vm������Ҳ��vm
        assertSame(result, mappingRules.getMappedName("fallback.template", "aaa/nonexistPackage/nonexistModule.vm")); // ����cache���ڣ����Եڶ���Ӧ��������

        // Fallback level 2���պ�׺
        result = mappingRules.getMappedName("fallback.template", "aaa/nonexistPackage/nonexistModule");
        assertEquals("myprefix/aaa/default", result);//�����ڷ���default��������չ���ǿգ�����Ҳ�ǿ�
        assertSame(result, mappingRules.getMappedName("fallback.template", "aaa/nonexistPackage/nonexistModule")); // ����cache���ڣ����Եڶ���Ӧ��������

        // not found �����׺
        result = mappingRules.getMappedName("fallback.template", "nonexistPackage1/nonexistPackage2/nonexistModule.vm");
        assertEquals("myprefix/nonexistPackage1/nonexistPackage2/nonexistModule.vm", result);//ȫ�������ڷ�������ģ�������չ����vm������Ҳ��vm
        assertSame(result,
                mappingRules.getMappedName("fallback.template", "nonexistPackage1/nonexistPackage2/nonexistModule.vm")); // ����cache���ڣ����Եڶ���Ӧ��������
    }

    @Test
    public void testFallbackTemplateMappingRule_HybridExtensions_SearchExts() {
        String result;

        result = mappingRules.getMappedName("fallback.template.searchExts", "aaa/bbb/myOtherModule.jsp");
        assertEquals("myprefix/aaa/bbb/myOtherModule.jsp", result);
        assertSame(result, mappingRules.getMappedName("fallback.template.searchExts", "aaa/bbb/myOtherModule.jsp")); // ����cache���ڣ����Եڶ���Ӧ��������

        // Fallback level 1
        result = mappingRules.getMappedName("fallback.template.searchExts", "aaa/bbb/nonexistModule.jsp");
        assertEquals("myprefix/aaa/bbb/default.jsp", result);
        assertSame(result, mappingRules.getMappedName("fallback.template.searchExts", "aaa/bbb/nonexistModule.jsp")); // ����cache���ڣ����Եڶ���Ӧ��������

        // Fallback level 2
        result = mappingRules.getMappedName("fallback.template.searchExts", "aaa/nonexistPackage/nonexistModule.jsp");
        assertEquals("myprefix/aaa/default.jsp", result);
        assertSame(result,
                mappingRules.getMappedName("fallback.template.searchExts", "aaa/nonexistPackage/nonexistModule.jsp")); // ����cache���ڣ����Եڶ���Ӧ��������

        result = mappingRules.getMappedName("fallback.template.searchExts", "ccc/nonexistPackage/nonexistModule.jsp");
        assertEquals("myprefix/ccc/default.jsp", result);
        assertSame(result,
                mappingRules.getMappedName("fallback.template.searchExts", "ccc/nonexistPackage/nonexistModule.jsp")); // ����cache���ڣ����Եڶ���Ӧ��������

        // not found
        result = mappingRules.getMappedName("fallback.template.searchExts",
                "nonexistPackage1/nonexistPackage2/nonexistModule.jsp");
        assertEquals("myprefix/nonexistPackage1/nonexistPackage2/nonexistModule.jsp", result);
        assertSame(result, mappingRules.getMappedName("fallback.template.searchExts",
                "nonexistPackage1/nonexistPackage2/nonexistModule.jsp")); // ����cache���ڣ����Եڶ���Ӧ��������
    }

    @Test
    public void testFallbackTemplateMappingRule_HybridExtensions_SearchExts_Locale() {
        String result;

        LocaleUtil.setContext(Locale.TAIWAN);

        result = mappingRules.getMappedName("fallback.template.searchExts.local", "aaa/bbb/myOtherModule.jsp");
        assertEquals("myprefix.locale/aaa/bbb/myOtherModule.jsp", result);//ʵ����myOtherModule.vm
        assertSame(result,
                mappingRules.getMappedName("fallback.template.searchExts.local", "aaa/bbb/myOtherModule.jsp")); // ����cache���ڣ����Եڶ���Ӧ��������

        LocaleUtil.setContext(Locale.CHINA);
        assertSame(result,
                mappingRules.getMappedName("fallback.template.searchExts.local", "aaa/bbb/myOtherModule.jsp")); // ��Ȼlocale��ͬ����zh��zh_TW����һ�������ģ�����Ӧ�ô�cache��ȡֵ

        LocaleUtil.setContext(Locale.TAIWAN);
        // Fallback level 1
        result = mappingRules.getMappedName("fallback.template.searchExts.local", "aaa/bbb/nonexistModule.jsp");
        assertEquals("myprefix.locale/aaa/bbb/default.jsp", result);//ʵ����default.jsp
        assertSame(result,
                mappingRules.getMappedName("fallback.template.searchExts.local", "aaa/bbb/nonexistModule.jsp")); // ����cache���ڣ����Եڶ���Ӧ��������

        // Fallback level 2
        result = mappingRules.getMappedName("fallback.template.searchExts.local",
                "aaa/nonexistPackage/nonexistModule.jsp");
        assertEquals("myprefix.locale/aaa/default.jsp", result);//ʵ����default.jsp
        assertSame(result, mappingRules.getMappedName("fallback.template.searchExts.local",
                "aaa/nonexistPackage/nonexistModule.jsp")); // ����cache���ڣ����Եڶ���Ӧ��������

        result = mappingRules.getMappedName("fallback.template.searchExts.local",
                "ccc/nonexistPackage/nonexistModule.do");
        assertEquals("myprefix.locale/ccc/default.do", result);//ʵ����default_zh.vm
        assertSame(result, mappingRules.getMappedName("fallback.template.searchExts.local",
                "ccc/nonexistPackage/nonexistModule.do")); // ����cache���ڣ����Եڶ���Ӧ��������

        // not found
        result = mappingRules.getMappedName("fallback.template.searchExts.local",
                "nonexistPackage1/nonexistPackage2/nonexistModule.jsp");
        assertEquals("myprefix.locale/nonexistPackage1/nonexistPackage2/nonexistModule.jsp", result);
        assertSame(result, mappingRules.getMappedName("fallback.template.searchExts.local",
                "nonexistPackage1/nonexistPackage2/nonexistModule.jsp")); // ����cache���ڣ����Եڶ���Ӧ��������

        LocaleUtil.resetContext();
    }
}
