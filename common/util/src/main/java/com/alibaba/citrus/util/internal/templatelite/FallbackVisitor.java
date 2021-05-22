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
package com.alibaba.citrus.util.internal.templatelite;

/**
 * ��visit�����Ҳ���ʱ�����ô�visitor��
 * 
 * @author Michael Zhou
 */
public interface FallbackVisitor {
    /**
     * ����һ���ı���
     */
    void visitText(String text) throws Exception;

    /**
     * ����<code>${placeholder:param1, param2, ...}</code>��
     */
    void visitPlaceholder(String name, String[] params) throws Exception;

    /**
     * ������ģ�壺<code>#template ... #end</code>��
     */
    void visitTemplate(String name, Template template) throws Exception;

    /**
     * ������ģ���飺<code>#template[] ... #template[] ... #end</code>��
     */
    void visitTemplateGroup(String name, Template[] templates) throws Exception;
}
