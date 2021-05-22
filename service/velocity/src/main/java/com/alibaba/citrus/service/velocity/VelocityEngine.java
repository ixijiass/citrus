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
package com.alibaba.citrus.service.velocity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.velocity.context.Context;

import com.alibaba.citrus.service.template.TemplateEngine;
import com.alibaba.citrus.service.template.TemplateException;

/**
 * Velocityģ�����档
 * <p>
 * Velocityģ������ȿ��Բ��뵽<code>TemplateService</code>�У�Ҳ���Ե���ʹ�á����ṩ��һ��velocityԭ���ķ���
 * <code>mergeTemplate()</code>�Լ���Ϊ<code>TemplateService</code>����ķ�����
 * </p>
 * 
 * @author Michael Zhou
 */
public interface VelocityEngine extends TemplateEngine {
    /**
     * ��Ⱦģ�壬�����ַ�������ʽȡ����Ⱦ�Ľ����
     */
    String mergeTemplate(String template, Context context, String inputEncoding) throws TemplateException, IOException;

    /**
     * ��Ⱦģ�壬������Ⱦ�Ľ���͵��ֽ�������С�
     */
    void mergeTemplate(String template, Context context, OutputStream ostream, String inputEncoding,
                       String outputEncoding) throws TemplateException, IOException;

    /**
     * ��Ⱦģ�壬������Ⱦ�Ľ���͵��ַ�������С�
     */
    void mergeTemplate(String template, Context context, Writer writer, String inputEncoding) throws TemplateException,
            IOException;
}
