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
package com.alibaba.citrus.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * ��<code>ResourceBundle</code>����Ϣ�ַ����йصĹ����ࡣ
 * 
 * @author Michael Zhou
 */
public class MessageUtil {
    /**
     * ��<code>ResourceBundle</code>��ȡ���ַ�������ʹ��<code>MessageFormat</code>��ʽ���ַ���.
     * 
     * @param bundle resource bundle
     * @param key Ҫ���ҵļ�
     * @param params ������
     * @return key��Ӧ���ַ��������keyΪ<code>null</code>��resource bundleΪ
     *         <code>null</code>����resource keyδ�ҵ����򷵻�<code>key</code>
     */
    public static String getMessage(ResourceBundle bundle, String key, Object... params) {
        if (bundle == null || key == null) {
            return key;
        }

        try {
            return formatMessage(bundle.getString(key), params);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * ʹ��<code>MessageFormat</code>��ʽ���ַ���.
     * 
     * @param message Ҫ��ʽ�����ַ���
     * @param params ������
     * @return ��ʽ�����ַ��������messageΪ<code>null</code>���򷵻�<code>null</code>
     */
    public static String formatMessage(String message, Object... params) {
        if (message == null || params == null || params.length == 0) {
            return message;
        }

        return MessageFormat.format(message, params);
    }
}
