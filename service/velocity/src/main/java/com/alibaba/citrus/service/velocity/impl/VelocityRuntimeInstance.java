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
package com.alibaba.citrus.service.velocity.impl;

import java.io.Reader;

import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import com.alibaba.citrus.service.velocity.impl.parser.ASTStringLiteralEnhanced;
import com.alibaba.citrus.service.velocity.impl.parser.SimpleNodeUtil;

/**
 * ��չ��velocity��<code>RuntimeInstance</code>�࣬ʵ��һ�����ܣ�ʹ
 * <code>ReferenceInsertionEventHandler</code>���Ը�֪�����ص������Ƿ�λ��
 * <code>StringLiteral</code>�С� ���磺
 * <p>
 * <code>EscapeSupport</code>���Ը������õ�λ�ã��������Ƿ�Ҫ�Խ������escapeת�塣�����velocity��佫���ᱻת�壺
 * </p>
 * 
 * <pre>
 * #set ($value = "hello, $name")
 * </pre>
 * <p>
 * ͨ������<code>InterpolationUtil.isInInterpolation(context)</code>����֪����ϸ�ڡ�
 * </p>
 * <p>
 * ͨ��velocity configuration��
 * <code>runtime.interpolate.string.literals.hack</code>���Կ��ش����ԣ�Ĭ��ֵΪ
 * <code>true</code>��
 * </p>
 * 
 * @author Michael Zhou
 */
public class VelocityRuntimeInstance extends RuntimeInstance {
    private static final String INTERPOLATION_HACK_KEY = "runtime.interpolate.string.literals.hack";
    private static final Boolean INTERPOLATION_HACK_DEFAULT = true;
    private boolean interpolationHack;

    @Override
    public synchronized void init() throws Exception {
        super.init();
        interpolationHack = getConfiguration().getBoolean(INTERPOLATION_HACK_KEY, INTERPOLATION_HACK_DEFAULT);
    }

    @Override
    public SimpleNode parse(Reader reader, String templateName, boolean dumpNamespace) throws ParseException {
        SimpleNode node = super.parse(reader, templateName, dumpNamespace);

        if (interpolationHack) {
            node = traversNode(node);
        }

        return node;
    }

    private SimpleNode traversNode(SimpleNode node) {
        int length = node.jjtGetNumChildren();

        for (int i = 0; i < length; i++) {
            Node child = node.jjtGetChild(i);

            if (child instanceof ASTStringLiteral) {
                replaceStringLiteral(node, (ASTStringLiteral) child, i);
            }

            if (child instanceof SimpleNode) {
                traversNode((SimpleNode) child);
            }
        }

        return node;
    }

    private void replaceStringLiteral(SimpleNode parent, ASTStringLiteral strLit, int index) {
        if (!(strLit instanceof ASTStringLiteralEnhanced)) {
            SimpleNodeUtil.jjtSetChild(parent, new ASTStringLiteralEnhanced(strLit), index);
        }
    }
}
