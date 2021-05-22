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
package com.alibaba.citrus.logconfig.log4j;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.helpers.LogLog;
import org.w3c.dom.Element;

/**
 * ��XML�ļ�����log4j�Ĺ����ࡣ��Log4jĬ�ϵ�<code>DOMConfigurator</code>
 * ��ȣ�����������ṩ�����properties�����������ļ��б����á�
 * 
 * @author Michael Zhou
 */
public class DOMConfigurator extends org.apache.log4j.xml.DOMConfigurator {
    private static final Field propsField;

    static {
        Field field = null;

        try {
            field = org.apache.log4j.xml.DOMConfigurator.class.getDeclaredField("props");
        } catch (Throwable e) {
        }

        propsField = field;
    }

    /**
     * �����¶���
     */
    public DOMConfigurator() {
        this(null);
    }

    /**
     * �����¶���
     * 
     * @param props ���������ļ��б����õ�����
     */
    public DOMConfigurator(Properties props) {
        setProperties(props);
    }

    /**
     * ʹ��XML�ļ�����log4j��
     * 
     * @param filename �����ļ���
     */
    public static void configure(String filename) {
        new DOMConfigurator().doConfigure(filename, LogManager.getLoggerRepository());
    }

    /**
     * ʹ��XML�ļ�����log4j��
     * 
     * @param filename �����ļ���
     * @param props ���������ļ��б����õ�����
     */
    public static void configure(String filename, Properties props) {
        new DOMConfigurator(props).doConfigure(filename, LogManager.getLoggerRepository());
    }

    /**
     * ʹ��XML�ļ�����log4j��
     * 
     * @param url �����ļ���URL
     */
    public static void configure(URL url) {
        new DOMConfigurator().doConfigure(url, LogManager.getLoggerRepository());
    }

    /**
     * ʹ��XML�ļ�����log4j��
     * 
     * @param url �����ļ���URL
     * @param props ���������ļ��б����õ�����
     */
    public static void configure(URL url, Properties props) {
        new DOMConfigurator(props).doConfigure(url, LogManager.getLoggerRepository());
    }

    /**
     * ʹ��XML�ļ�����log4j��
     * 
     * @param element �����ļ�����DOM element
     */
    public static void configure(Element element) {
        new DOMConfigurator().doConfigure(element, LogManager.getLoggerRepository());
    }

    /**
     * ʹ��XML�ļ�����log4j��
     * 
     * @param element �����ļ�����DOM element
     * @param props ���������ļ��б����õ�����
     */
    public static void configure(Element element, Properties props) {
        new DOMConfigurator(props).doConfigure(element, LogManager.getLoggerRepository());
    }

    /**
     * ʹ��XML�ļ�����log4j��ͬʱ����ļ��ĸ��ġ�
     * 
     * @param filename �����ļ���
     */
    public static void configureAndWatch(String filename) {
        configureAndWatch(filename, null, FileWatchdog.DEFAULT_DELAY);
    }

    /**
     * ʹ��XML�ļ�����log4j��ͬʱ����ļ��ĸ��ġ�
     * 
     * @param filename �����ļ���
     * @param props ���������ļ��б����õ�����
     */
    public static void configureAndWatch(String filename, Properties props) {
        configureAndWatch(filename, props, FileWatchdog.DEFAULT_DELAY);
    }

    /**
     * ʹ��XML�ļ�����log4j��ͬʱ����ļ��ĸ��ġ��˷���������һ������̣߳����̵߳ڸ�ָ��ʱ��ͻ����ļ��Ƿ񱻴�����ı䣬����ǣ�
     * ����ļ��ж�ȡlog4j���á�
     * 
     * @param filename �����ļ���
     * @param interval ����̼߳������ms��
     */
    public static void configureAndWatch(String filename, long interval) {
        XMLWatchdog xdog = new XMLWatchdog(filename, null);

        xdog.setDelay(interval);
        xdog.start();
    }

    /**
     * ʹ��XML�ļ�����log4j��ͬʱ����ļ��ĸ��ġ��˷���������һ������̣߳����̵߳ڸ�ָ��ʱ��ͻ����ļ��Ƿ񱻴�����ı䣬����ǣ�
     * ����ļ��ж�ȡlog4j���á�
     * 
     * @param filename �����ļ���
     * @param props ���������ļ��б����õ�����
     * @param interval ����̼߳������ms��
     */
    public static void configureAndWatch(String filename, Properties props, long interval) {
        XMLWatchdog xdog = new XMLWatchdog(filename, props);

        xdog.setDelay(interval);
        xdog.start();
    }

    /**
     * �������ԣ���Щ���Կ����������ļ��б����á�
     * 
     * @param props ����
     */
    public void setProperties(Properties props) {
        try {
            propsField.setAccessible(true);
            propsField.set(this, props);
        } catch (Throwable e) {
            LogLog.warn("Could not set field: org.apache.log4j.xml.DOMConfigurator.props");
        }
    }

    /**
     * ����̡߳�
     */
    private static class XMLWatchdog extends FileWatchdog {
        private Properties props;

        public XMLWatchdog(String filename, Properties props) {
            super(filename);
            this.props = props;
        }

        @Override
        public void doOnChange() {
            new DOMConfigurator(props).doConfigure(filename, LogManager.getLoggerRepository());
        }
    }
}
