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
package com.alibaba.citrus.generictype.introspect;

import com.alibaba.citrus.util.internal.StringUtil;

/**
 * �������<code>PropertyPath</code>�Ĵ���
 * 
 * @author Michael Zhou
 */
public class InvalidPropertyPathException extends PropertyException {
    private static final long serialVersionUID = -1069105738628240802L;

    public InvalidPropertyPathException(String propertyPath, int index) {
        super(String.format("Invalid property path: \"%s\" at index=%d, near \"%s\"", StringUtil
                .escapeJava(propertyPath), index, StringUtil.escapeJava(propertyPath.substring(0, index))));
    }
}
