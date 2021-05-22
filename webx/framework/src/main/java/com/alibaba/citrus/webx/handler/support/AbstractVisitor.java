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
package com.alibaba.citrus.webx.handler.support;

import static com.alibaba.citrus.util.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;

import com.alibaba.citrus.util.internal.templatelite.TextWriter;
import com.alibaba.citrus.util.internal.webpagelite.PageComponent;
import com.alibaba.citrus.webx.handler.RequestHandlerContext;

/**
 * Ϊinternalҳ���component�ṩ�������ܵ�visitor��
 * 
 * @author Michael Zhou
 */
public abstract class AbstractVisitor extends TextWriter<PrintWriter> {
    private final PageComponent component;
    protected final RequestHandlerContext context;

    /**
     * ���ڴ���page�е�visitor��
     * <p>
     * �ڵ��ô˹��캯��ǰ��<code>context.getWriter(contentType)</code>�����Ѿ�ȡ�á�
     * </p>
     */
    public AbstractVisitor(RequestHandlerContext context) {
        this(context, (PageComponent) null);
    }

    /**
     * ���ڴ���component�е�visitor��
     * <p>
     * �ڵ��ô˹��캯��ǰ��<code>context.getWriter(contentType)</code>�����Ѿ�ȡ�á�
     * </p>
     */
    public AbstractVisitor(RequestHandlerContext context, PageComponent component) {
        super(context.getWriter());
        this.context = context;
        this.component = component;
    }

    /**
     * ���ڴ�������page�е�visitor��
     * <p>
     * ����page�ᴴ��writer��������������HTMLҳ�档
     * </p>
     */
    public AbstractVisitor(RequestHandlerContext context, String contentTypeAndCharset) throws IOException {
        super(context.getWriter(contentTypeAndCharset));
        this.context = context;
        this.component = null;
    }

    /**
     * ȡ������ڵ�ǰҳ���URL��
     */
    public void visitUrl(String relativeUrl) {
        out().append(context.getResourceURL(relativeUrl));
    }

    /**
     * ȡ������ڵ�ǰcomponent��URL��
     */
    public void visitComponentUrl(String relativeUrl) {
        assertNotNull(component, "no component");
        out().append(component.getComponentURL(context, relativeUrl));
    }
}
