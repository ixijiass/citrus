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
package com.alibaba.citrus.util.internal.regex;

import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ��<code>MatchResult</code>���滻�ַ����еı�����ͨ��������<code>'$'</code>��ʼ�����磺
 * <code>$1</code>��<code>$2</code>�ȣ���<code>MatchResultSubstitution</code>��֧�ֶԶ��
 * <code>MatchResult</code>���������滻���ֱ��Ӧ��ͬ��ǰ׺��
 * 
 * @author Michael Zhou
 */
public class MatchResultSubstitution extends Substitution {
    /**
     * ����һ���ɹ��������ݵ�ƥ������
     */
    public static final MatchResult EMPTY_MATCH_RESULT = createEmptyMatchResult();

    private final MatchResult[] results;

    private static MatchResult createEmptyMatchResult() {
        Matcher matcher = Pattern.compile("^$").matcher("");

        assertTrue(matcher.find());

        return matcher.toMatchResult();
    }

    /**
     * ����һ���滻���滻����<code>$num</code>������ı�����
     */
    public MatchResultSubstitution() {
        this("$", EMPTY_MATCH_RESULT);
    }

    /**
     * ����һ���滻���滻����<code>$num</code>������ı�����
     */
    public MatchResultSubstitution(MatchResult result) {
        this("$", result);
    }

    /**
     * ������ƥ�䡣
     */
    public void setMatchResult(MatchResult result) {
        if (results.length != 1) {
            new IllegalArgumentException("expected " + this.results.length + " MatchResults");
        }

        results[0] = result;
    }

    /**
     * ������ƥ�䡣
     */
    public void setMatchResults(MatchResult... results) {
        assertTrue(!isEmptyArray(results), "results");

        if (this.results.length != results.length) {
            throw new IllegalArgumentException("expected " + this.results.length + " MatchResults");
        }

        for (int i = 0; i < results.length; i++) {
            this.results[i] = results[i];
        }
    }

    /**
     * ����һ���滻��������ָ��ǰ׺������ı����滻����Ӧ<code>MatchResult.group(num)</code>��ֵ��
     */
    public MatchResultSubstitution(String replacementPrefixes, MatchResult... results) {
        super(replacementPrefixes);
        this.results = new MatchResult[this.replacementPrefixes.length()];

        setMatchResults(results);
    }

    /**
     * ȡ��ƥ�䡣
     */
    public MatchResult getMatch() {
        return getMatch(0);
    }

    /**
     * ȡ��ƥ�䡣
     */
    public MatchResult getMatch(int index) {
        return results[index];
    }

    @Override
    protected String group(int index, int groupNumber) {
        MatchResult result = getMatch(index);

        if (groupNumber <= result.groupCount()) {
            return result.group(groupNumber);
        }

        return null;
    }

    @Override
    public String toString() {
        MapBuilder mb = new MapBuilder();

        for (int i = 0; i < replacementPrefixes.length(); i++) {
            mb.append(replacementPrefixes.charAt(i) + "n", results[i]);
        }

        return new ToStringBuilder().append(getClass().getSimpleName()).append(mb).toString();
    }
}
