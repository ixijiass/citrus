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
package com.alibaba.citrus.service.freemarker.impl.log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.alibaba.citrus.service.freemarker.FreeMarkerEngine;
import com.alibaba.citrus.util.ExceptionUtil;
import com.alibaba.citrus.util.io.ByteArray;
import com.alibaba.citrus.util.io.StreamUtil;

import freemarker.log.Logger;

public class LoggerHacker {
    private final static org.slf4j.Logger slf4jLog = org.slf4j.LoggerFactory.getLogger(FreeMarkerEngine.class);
    private final static int SLF4J_INDEX = 2;

    /**
     * XXX Hack! FreeMarker����־ϵͳ�Ƿ�յģ�ֻ֧�����޵�logger���ͣ���֧��slf4j��
     * ���hack�޸���LIBINIT�ڲ�������ֵ��ʹ����ʼ��SLF4J��ȡ����Avalon����
     * ���罫��������Ʊ��޸ģ���δ����п��ܳ�����ʱ��ά��FreeMarkerĬ�ϵ����á�
     */
    public static final void hackLogger(String prefix) {
        try {
            // ��PublicLoggerFactory��Slf4jLoggerFactory����LoggerFactory���ڵ�loader�С�
            definePublicLoggerFactory(Logger.class.getClassLoader(), "freemarker.log.PublicLoggerFactory");
            definePublicLoggerFactory(Logger.class.getClassLoader(), "freemarker.log.Slf4jLoggerFactory");

            // ʹLoggerѡ��slf4jΪ��־ϵͳ
            Field field = Logger.class.getDeclaredField("LIBINIT");
            field.setAccessible(true);

            String[] LIBINIT = (String[]) field.get(null);

            LIBINIT[SLF4J_INDEX * 2] = "org.slf4j";
            LIBINIT[SLF4J_INDEX * 2 + 1] = "Slf4j";

            Logger.selectLoggerLibrary(SLF4J_INDEX);
            Logger.setCategoryPrefix(prefix);
        } catch (Throwable e) {
            // ��illegal access����ʱ����־ϵͳ�����getStackTrace(e)����֮��
            slf4jLog.warn("Could not hack FreeMarker Logging System: {}", ExceptionUtil.getStackTrace(e));

            try {
                Logger.selectLoggerLibrary(0);
                Logger.setCategoryPrefix("");
            } catch (Throwable ee) {
            }
        }
    }

    private static void definePublicLoggerFactory(ClassLoader loader, String className) {
        try {
            Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class,
                    int.class, int.class);

            defineClassMethod.setAccessible(true);

            ByteArray ba = StreamUtil.readBytes(
                    LoggerHacker.class.getClassLoader().getResource(className.replace('.', '/') + ".class")
                            .openStream(), true);

            try {
                defineClassMethod.invoke(loader, className, ba.getRawBytes(), ba.getOffset(), ba.getLength());
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } catch (Throwable e) {
            slf4jLog.debug("Failed to define {}: {}", className, e.getMessage());
        }
    }
}
