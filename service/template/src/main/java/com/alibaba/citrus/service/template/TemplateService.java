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
package com.alibaba.citrus.service.template;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * ��������ṩ��һ����Ⱦģ���ͨ�ýӿڣ�ͨ���������һ���ض���ģ��ϵͳ������Velocity��JSP��ֻ ��Ҫʵ��
 * <code>TemplateEngine</code>�ӿڣ�����template service�Ǽ��Լ����Ϳ��Ա�ͳһ�ķ��������á�
 * 
 * @author Michael Zhou
 */
public interface TemplateService {
    /**
     * ȡ�����б��Ǽǵ�ģ������׺��
     */
    String[] getSupportedExtensions();

    /**
     * ȡ��ָ��ģ������׺��Ӧ��ģ�����档
     */
    TemplateEngine getTemplateEngine(String extension);

    /**
     * �˷���������Ӧ��ģ�����棬���ж�ģ���Ƿ���ڡ�
     */
    boolean exists(String templateName);

    /**
     * ��Ⱦģ�壬�����ַ�������ʽȡ����Ⱦ�Ľ����
     */
    String getText(String templateName, TemplateContext context) throws TemplateException, IOException;

    /**
     * ��Ⱦģ�壬������Ⱦ�Ľ���͵��ֽ�������С�
     */
    void writeTo(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException,
            IOException;

    /**
     * ��Ⱦģ�壬������Ⱦ�Ľ���͵��ַ�������С�
     */
    void writeTo(String templateName, TemplateContext context, Writer writer) throws TemplateException, IOException;
}
