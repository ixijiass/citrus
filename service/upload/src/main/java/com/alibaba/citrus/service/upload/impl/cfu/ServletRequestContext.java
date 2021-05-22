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
package com.alibaba.citrus.service.upload.impl.cfu;

import javax.servlet.http.HttpServletRequest;

/**
 * �̳���commons-fileupload-1.2.1��ͬ���࣬�Ľ����������ݣ�
 * <ul>
 * <li>����<code>request.getCharacterEncoding()</code>����<code>null</code> ����ô����Ĭ��ֵ
 * <code>ISO-8859-1</code>���÷���������������header�����а���field name��file name�ȡ�ԭʼ����
 * <code>request.getCharacterEncoding()</code>����<code>null</code>
 * ʱ����ʹ�ò���ϵͳĬ�ϱ��룬���������ز�ȷ���Ľ���������޸ĺ󣬾ͺ�servlet�淶��һ�¡�</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public class ServletRequestContext extends org.apache.commons.fileupload.servlet.ServletRequestContext {
    public static final String DEFAULT_CHARSET = "ISO-8859-1";

    public ServletRequestContext(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getCharacterEncoding() {
        String charset = super.getCharacterEncoding();

        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        return charset;
    }
}
