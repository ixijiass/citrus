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
package com.alibaba.citrus.service.requestcontext.rewrite.impl;

import static com.alibaba.citrus.util.ArrayUtil.*;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.citrus.service.requestcontext.rewrite.RewriteSubstitutionHandler;
import com.alibaba.citrus.util.StringEscapeUtil;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;
import com.alibaba.citrus.util.internal.regex.MatchResultSubstitution;

/**
 * ����һ��rewrite����
 * <p>
 * ÿ��rewrite����ƥ��󣬾ͻ�������ƥ�����е�conditions������еĻ���������conditionsҲ�����㣬
 * ��ôsubstitution�ͻᱻִ�С�
 * </p>
 * 
 * @author Michael Zhou
 */
public class RewriteRule implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(RewriteRule.class);
    private String patternString;
    private Pattern pattern;
    private boolean negative;
    private RewriteCondition[] conditions;
    private RewriteSubstitution substitution;
    private Object[] handlers;

    public String getPattern() {
        return patternString;
    }

    public void setPattern(String patternString) throws PatternSyntaxException {
        this.patternString = StringUtil.trimToNull(patternString);
    }

    public void setConditions(RewriteCondition[] conditions) {
        this.conditions = conditions;
    }

    public void setSubstitution(RewriteSubstitution substitution) {
        this.substitution = substitution;
    }

    public void setHandlers(Object[] handlers) {
        this.handlers = handlers;
    }

    public Object[] handlers() {
        return handlers;
    }

    public void afterPropertiesSet() throws Exception {
        // pattern
        if (patternString == null || "!".equals(patternString)) {
            throw new PatternSyntaxException("empty pattern", patternString, -1);
        }

        String realPattern;

        if (patternString.startsWith("!")) {
            negative = true;
            realPattern = patternString.substring(1);
        } else {
            realPattern = patternString;
        }

        pattern = Pattern.compile(realPattern);

        // conditions
        if (conditions == null) {
            conditions = new RewriteCondition[0];
        }

        // substitution
        if (substitution == null) {
            substitution = new RewriteSubstitution();
            substitution.afterPropertiesSet();
        }

        // handlers
        if (handlers == null) {
            handlers = new RewriteSubstitutionHandler[0];
        }
    }

    /**
     * ��ͼƥ��rule��
     * <p>
     * ���ƥ�䣬�򷵻�ƥ���������򷵻�<code>null</code>��ʾ��ƥ�䡣
     * </p>
     */
    public MatchResult match(String path) {
        Matcher matcher = pattern.matcher(path);
        boolean matched = matcher.find();

        if (!negative && matched) {
            if (log.isDebugEnabled()) {
                log.debug("Testing \"{}\" with rule pattern: \"{}\", MATCHED", StringEscapeUtil.escapeJava(path),
                        StringEscapeUtil.escapeJava(patternString));
            }

            return matcher.toMatchResult();
        }

        if (negative && !matched) {
            if (log.isDebugEnabled()) {
                log.debug("Testing \"{}\" with rule pattern: \"{}\", MATCHED", StringEscapeUtil.escapeJava(path),
                        StringEscapeUtil.escapeJava(patternString));
            }

            return MatchResultSubstitution.EMPTY_MATCH_RESULT;
        }

        if (log.isTraceEnabled()) {
            log.trace("Testing \"{}\" with rule pattern: \"{}\", MISMATCHED", StringEscapeUtil.escapeJava(path),
                    StringEscapeUtil.escapeJava(patternString));
        }

        return null;
    }

    public MatchResult matchConditions(MatchResult ruleMatchResult, HttpServletRequest request) {
        MatchResult conditionMatchResult = MatchResultSubstitution.EMPTY_MATCH_RESULT;

        if (!isEmptyArray(conditions)) {
            int i = 0;
            for (RewriteCondition condition : conditions) {
                MatchResult result = condition.match(ruleMatchResult, conditionMatchResult, request);

                // �ж�ornext��ǣ��������һ��condition����ornext��ǣ������ó���������
                boolean ornext = i < conditions.length - 1 && condition.getFlags().hasOR();

                if (result == null) {
                    if (!ornext) {
                        conditionMatchResult = null;
                        break;
                    }

                    // ����conditionMatchResult���Ϊ�����һ��ƥ��
                } else {
                    conditionMatchResult = result;

                    if (ornext) {
                        break;
                    }
                }

                i++;
            }
        }

        return conditionMatchResult;
    }

    public RewriteSubstitution getSubstitution() {
        return substitution;
    }

    @Override
    public String toString() {
        MapBuilder mb = new MapBuilder();

        mb.append("pattern", patternString);

        if (!isEmptyArray(conditions)) {
            mb.append("conditions", conditions);
        }

        mb.append("substitution", substitution);

        return new ToStringBuilder().append("RewriteRule").append(mb).toString();
    }
}
