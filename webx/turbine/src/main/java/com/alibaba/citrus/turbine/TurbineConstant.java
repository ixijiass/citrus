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
package com.alibaba.citrus.turbine;

/**
 * �����webx�����ĳ����� �����һЩcitrus�õ��ĳ���
 * 
 * @author Michael Zhou
 * @author dux.fangl
 */
public interface TurbineConstant {
    /* Turbine Scheme ģ���ģ�����͡� */

    /** ģ�����ͣ�action�������û��ύ���ݵ�ģ�顣 */
    String ACTION_MODULE = "action";

    /** ģ�����ͣ�screen������ҳ������塣 */
    String SCREEN_MODULE = "screen";

    /** ģ�����ͣ�screen������ҳ������塣 */
    String SCREEN_MODULE_NO_TEMPLATE = "screen.notemplate";

    /** ģ�����ͣ�control������ҳ��Ŀ�����Ƭ�Ρ� */
    String CONTROL_MODULE = "control";

    /** ģ�����ͣ�control������ҳ��Ŀ�����Ƭ�Ρ� */
    String CONTROL_MODULE_NO_TEMPLATE = "control.notemplate";

    /** ģ�����ͣ�screen������ҳ������塣 */
    String SCREEN_TEMPLATE = "screen.template";

    /** ģ�����ͣ�control������ҳ��Ŀ�����Ƭ�Ρ� */
    String CONTROL_TEMPLATE = "control.template";

    /** ģ�����ͣ�layout������ҳ��Ĳ��֡� */
    String LAYOUT_TEMPLATE = "layout.template";

    /** URL��׺ת�������� */
    String EXTENSION_INPUT = "extension.input";

    /** URL��׺ת������� */
    String EXTENSION_OUTPUT = "extension.output";

    /* Template context��س����� */

    /** ��rundata attribute��template context�д���screen�����ݵ�key�� */
    String SCREEN_PLACEHOLDER_KEY = "screen_placeholder";

    /* HTML Template��صĳ���(HtmlPageAttributeTool)�� */

    /** Default doctype root element. */
    String DEFAULT_HTML_DOCTYPE_ROOT_ELEMENT_KEY = "default.html.doctype.root.element";

    /** Default value for the doctype root element */
    String DEFAULT_HTML_DOCTYPE_ROOT_ELEMENT_DEFAULT = "HTML";

    /** Default doctype dtd. */
    String DEFAULT_HTML_DOCTYPE_IDENTIFIER_KEY = "default.html.doctype.identifier";

    /** Default Doctype dtd value */
    String DEFAULT_HTML_DOCTYPE_IDENTIFIER_DEFAULT = "-//W3C//DTD HTML 4.01 Transitional//EN";

    /** Default doctype url. */
    String DEFAULT_HTML_DOCTYPE_URI_KEY = "default.html.doctype.url";

    /** Default doctype url value. */
    String DEFAULT_HTML_DOCTYPE_URI_DEFAULT = "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd";
}
