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

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.StringUtil.*;

/**
 * ����һ���滻���滻�ַ����еı�����ͨ��������<code>'$'</code>��ʼ�����磺 <code>$1</code>��<code>$2</code>
 * �ȣ���<code>Substitution</code>���֧�ֶԶ��ֲ�ͬǰ׺�ı��������滻��
 * 
 * @author Michael Zhou
 */
public abstract class Substitution {
    protected final String replacementPrefixes;

    /**
     * ����һ���滻����<code>'$'</code>Ϊ����ǰ׺��
     */
    public Substitution() {
        this("$");
    }

    /**
     * ����һ���滻����ָ���ַ�Ϊ����ǰ׺��
     */
    public Substitution(String replacementPrefixes) {
        this.replacementPrefixes = assertNotNull(trimToNull(replacementPrefixes), "replacementPrefixes");
    }

    /**
     * �滻�ַ����еı�����
     */
    public final String substitute(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();

        substitute(buf, input);

        return buf.toString();
    }

    /**
     * �滻�ַ����еı�����
     */
    public final void substitute(StringBuilder buf, String input) {
        int length = input.length();
        int index;

        for (int i = 0; i < length;) {
            char ch = input.charAt(i);

            if (ch == '\\') {
                i++;

                if (i < length) {
                    buf.append(input.charAt(i++));
                } else {
                    buf.append(ch);
                }
            } else if ((index = replacementPrefixes.indexOf(ch)) >= 0) {
                i++;

                int num = -1;
                int numStartIndex = i; // ����index

                while (i < length) {
                    int digit = input.charAt(i) - '0';

                    if (digit < 0 || digit > 9) {
                        break;
                    }

                    i++;

                    if (num == -1) {
                        num = digit;
                    } else {
                        num = num * 10 + digit;
                    }
                }

                String groupValue;

                if (num == -1) { // not a number
                    buf.append(ch);
                } else if ((groupValue = group(index, num)) != null) {
                    buf.append(groupValue);
                } else { // out of range
                    buf.append(ch);
                    buf.append(input, numStartIndex, i);
                }
            } else {
                buf.append(ch);
                i++;
            }
        }
    }

    /**
     * ���า�Ǵ˷��������ṩָ�����͡�ָ��group��ŵĵ�replacement�����
     */
    protected abstract String group(int index, int groupNumber);
}
