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
package com.alibaba.citrus.logconfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.LoggerFactory;

/**
 * ��ʼ����־������������ض���־ϵͳ��������
 * 
 * @author Michael Zhou
 */
public abstract class LogConfigurator {
    private static final String PROVIDERS_PATTERN = "META-INF/logconfig.providers";
    private static final String LOGGING_LEVEL = "loggingLevel";
    private static final String LOGGING_CHARSET = "loggingCharset";
    private static final String LOGGING_ROOT = "loggingRoot";
    private static final String LOCAL_HOST = "localHost";
    private static final String LOCAL_ADDRESS = "localAddress";
    private String logSystem;

    /**
     * ��ָ���������ļ�������log system��
     * <p>
     * ע�⣬��ʹ���ù���ʧ�ܣ�Ҳ�����׳��κ��쳣��ֻ�Ǵ�ӡ������Ϣ����־ϵͳ��ʧ�ܲ�Ӧ��Ӱ��Ӧ��ϵͳ��
     * <p>
     */
    public final void configure(URL configFile) {
        configure(configFile, null);
    }

    /**
     * ��ָ���������ļ���properties������log system��
     * <p>
     * ע�⣬��ʹ���ù���ʧ�ܣ�Ҳ�����׳��κ��쳣��ֻ�Ǵ�ӡ������Ϣ����־ϵͳ��ʧ�ܲ�Ӧ��Ӱ��Ӧ��ϵͳ��
     * <p>
     */
    public final void configure(URL configFile, Map<String, String> props) {
        StringBuilder buf = new StringBuilder();

        buf.append("INFO: configuring \"").append(logSystem).append("\" using ").append(configFile).append("\n");

        if (props == null) {
            props = new HashMap<String, String>();
        }

        for (String key : new TreeSet<String>(props.keySet())) {
            String value = props.get(key);

            // ��log��ͷ��property������ֵ��system properties��ͬ�ģ���ӡ������
            if (key.startsWith("log") || value != null && !value.equals(System.getProperty(key))) {
                buf.append(" - with property ").append(key).append(" = ").append(value).append("\n");
            }
        }

        log(buf.toString());

        try {
            doConfigure(configFile, props);
        } catch (Exception e) {
            log("WARN: Failed to configure " + logSystem + " using " + configFile, e);
        }
    }

    /**
     * ��Ĭ�ϵ������ļ���Ĭ�ϵ�properties������log system��
     * <p>
     * �൱�ڣ�
     * <code>configure(getDefaultConfigFile(), getDefaultProperties());</code> ��
     * </p>
     */
    public final void configureDefault() {
        configureDefault(null);
    }

    /**
     * ��Ĭ�ϵ������ļ���Ĭ�ϵ�properties������log system��
     * <p>
     * �൱�ڣ�
     * <code>configure(getDefaultConfigFile(), getDefaultProperties(debug));</code>
     * ��
     * </p>
     */
    public final void configureDefault(Boolean debug) {
        URL configFile = getDefaultConfigFile();

        if (configFile == null) {
            log("ERROR: could not find default config file for \"" + logSystem + "\"");
            return;
        }

        configure(configFile, getDefaultProperties(debug));
    }

    /**
     * ȡ�õ�ǰconfigurator��Ӧ��log system�����ƣ����磺<code>logback</code>��
     */
    public final String getLogSystem() {
        return logSystem;
    }

    /**
     * ȡ��Ĭ�ϵ������ļ�URL��
     */
    public final URL getDefaultConfigFile() {
        return getClass().getClassLoader().getResource(
                getClass().getPackage().getName().replace('.', '/') + "/" + getDefaultConfigFileName());
    }

    /**
     * ȡ��Ĭ�ϵ������ļ�����
     * <p>
     * ������Ը��Ǵ˷�������ȡ���ض��������ļ����粻���ǣ�Ĭ�Ϸ���<code>logsystem.xml</code>��
     * </p>
     */
    protected String getDefaultConfigFileName() {
        return logSystem + "-default.xml";
    }

    /**
     * ȡ����������log system��Ĭ�ϵ�properties�������������ݣ�
     * <ul>
     * <li>system properties��</li>
     * <li><code>loggingCharset</code> - ���charset��ȡ����ϵͳĬ��ֵ��</li>
     * <li><code>loggingLevel</code> - ��־level��ȡ����debug������</li>
     * <li><code>loggingRoot</code> - ��־��Ŀ¼��Ĭ��Ϊ<code>$HOME/logs</code>��</li>
     * <li><code>localHost</code> - ��ǰ��������</li>
     * <li><code>localAddress</code> - ��ǰ��ַ��</li>
     * <li>����ɸ���<code>setDefaultProperties()</code>�������Ա��޸������õ�ֵ��</li>
     * </ul>
     */
    public final Map<String, String> getDefaultProperties() {
        return getDefaultProperties(null);
    }

    /**
     * ȡ����������log system��Ĭ�ϵ�properties�������������ݣ�
     * <ul>
     * <li>system properties��</li>
     * <li><code>loggingCharset</code> - ���charset��ȡ����ϵͳĬ��ֵ��</li>
     * <li><code>loggingLevel</code> - ��־level��ȡ����debug������</li>
     * <li><code>loggingRoot</code> - ��־��Ŀ¼��Ĭ��Ϊ<code>$HOME/logs</code>��</li>
     * <li><code>localHost</code> - ��ǰ��������</li>
     * <li><code>localAddress</code> - ��ǰ��ַ��</li>
     * <li>����ɸ���<code>setDefaultProperties()</code>�������Ա��޸������õ�ֵ��</li>
     * </ul>
     */
    public final Map<String, String> getDefaultProperties(Boolean debug) {
        Map<String, String> props = new HashMap<String, String>();

        // system properties
        for (Map.Entry<?, ?> entry : System.getProperties().entrySet()) {
            props.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }

        // charset
        if (!isPropertyExist(props, LOGGING_CHARSET)) {
            props.put(LOGGING_CHARSET, getDefaultCharset());
        }

        // level
        if (!isPropertyExist(props, LOGGING_LEVEL)) {
            if (debug == null) {
                debug = false;
            }

            props.put(LOGGING_LEVEL, getDefaultLevel(debug));
        }

        // default logging root
        if (!isPropertyExist(props, LOGGING_ROOT)) {
            props.put(LOGGING_ROOT, getDefaultLoggingRoot());
        }

        // host info
        String hostName;
        String hostAddress;

        try {
            InetAddress localhost = InetAddress.getLocalHost();

            hostName = localhost.getHostName();
            hostAddress = localhost.getHostAddress();
        } catch (UnknownHostException e) {
            hostName = "localhost";
            hostAddress = "127.0.0.1";
        }

        props.put(LOCAL_HOST, hostName);
        props.put(LOCAL_ADDRESS, hostAddress);

        // ������һ����������properties
        setDefaultProperties(props);

        return props;
    }

    private boolean isPropertyExist(Map<String, String> props, String name) {
        return trimToNull(props.get(name)) != null;
    }

    private String getDefaultCharset() {
        return Charset.defaultCharset().name();
    }

    private String getDefaultLevel(boolean debug) {
        return debug ? "TRACE" : "INFO";
    }

    private String getDefaultLoggingRoot() {
        return new File(System.getProperty("user.home") + "/logs").getAbsolutePath();
    }

    /**
     * ����Ĭ�ϵ�properties��
     * <p>
     * ������Ը��������������Լ���Ĭ��ֵ��
     * </p>
     */
    protected void setDefaultProperties(Map<String, String> props) {
    }

    /**
     * ���ö�Ӧ��log system��������ʵ�֡�
     */
    protected abstract void doConfigure(URL configFile, Map<String, String> props) throws Exception;

    /**
     * �رպ�����log system��������ʵ�֡�
     */
    public abstract void shutdown();

    /**
     * ȡ��ָ������־ϵͳ������������δָ����־ϵͳ�����ƣ������Ŵ�slf4j���Զ�ȡ�õ�ǰ���õ���־ϵͳ��
     */
    public static LogConfigurator getConfigurator() {
        return getConfigurator((String) null);
    }

    /**
     * ȡ��ָ������־ϵͳ������������δָ����־ϵͳ�����ƣ������Ŵ�slf4j���Զ�ȡ�õ�ǰ���õ���־ϵͳ��
     */
    public static LogConfigurator getConfigurator(String logSystem) {
        LogConfigurator[] configurators = getConfigurators(logSystem);

        if (configurators == null || configurators.length == 0) {
            return null;
        } else {
            return configurators[0];
        }
    }

    /**
     * ȡ��ָ������־ϵͳ������������δָ����־ϵͳ�����ƣ������Ŵ�slf4j���Զ�ȡ�õ�ǰ���õ���־ϵͳ��
     */
    public static LogConfigurator[] getConfigurators(String... logSystems) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Map<String, String> providers = getProviders(PROVIDERS_PATTERN, cl);

        // ���SLF4J��ѡ�����־ϵͳ
        String slf4jLogSystem = guessSlf4jLogSystem(providers);

        // �������logSystems
        if (logSystems == null || logSystems.length == 0) {
            logSystems = new String[1];
        }

        boolean containsSlf4jLogSystem = false;
        boolean containsNull = false;

        for (int i = 0; i < logSystems.length; i++) {
            String logSystem = logSystems[i];

            if (logSystem != null) {
                logSystem = trimToNull(logSystem.toLowerCase());
            }

            if (logSystem == null) {
                logSystem = slf4jLogSystem;
            }

            if (slf4jLogSystem != null && slf4jLogSystem.equals(logSystem)) {
                containsSlf4jLogSystem = true;
            }

            if (logSystem == null) {
                containsNull = true;
            }

            logSystems[i] = logSystem;
        }

        // ����logSystems�б���δ����slf4jLogSystem���򱨾���
        if (slf4jLogSystem != null && !containsSlf4jLogSystem) {
            log("WARN: SLF4J chose [" + slf4jLogSystem + "] as its logging system, not " + Arrays.toString(logSystems));
        }

        // ���ϵͳ�в�����Ĭ�ϵ�logSystems���򱨴�
        if (containsNull) {
            throw new IllegalArgumentException("No log system bound with SLF4J");
        }

        // ȡ��log configurators
        LogConfigurator[] configurators = new LogConfigurator[logSystems.length];

        for (int i = 0; i < logSystems.length; i++) {
            String logSystem = logSystems[i];
            String providerClassName = providers.get(logSystem);

            if (providerClassName == null) {
                throw new IllegalArgumentException("Could not find LogConfigurator for \"" + logSystem
                        + "\" by searching in " + PROVIDERS_PATTERN);
            }

            Class<?> providerClass;

            try {
                providerClass = cl.loadClass(providerClassName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Could not find LogConfigurator for " + logSystem, e);
            }

            if (!LogConfigurator.class.isAssignableFrom(providerClass)) {
                throw new IllegalArgumentException(logSystem + " class " + providerClassName
                        + " is not a sub-class of " + LogConfigurator.class.getName());
            }

            LogConfigurator configurator;

            try {
                configurator = (LogConfigurator) providerClass.newInstance();
            } catch (Throwable e) {
                throw new IllegalArgumentException("Could not create instance of class " + providerClassName + " for "
                        + logSystem, e);
            }

            configurator.logSystem = logSystem;

            configurators[i] = configurator;
        }

        return configurators;
    }

    private static Map<String, String> getProviders(String location, ClassLoader cl) {
        Properties props = new Properties();
        Enumeration<?> i = null;

        try {
            i = cl.getResources(location);
        } catch (IOException e) {
            log("ERROR: Failed to read " + location, e);
        }

        while (i != null && i.hasMoreElements()) {
            URL url = (URL) i.nextElement();
            InputStream is = null;

            try {
                is = url.openStream();
                props.load(is);
            } catch (Exception e) {
                log("ERROR: Failed to read " + url, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        Map<String, String> propsMap = new HashMap<String, String>();

        for (Map.Entry<?, ?> entry : props.entrySet()) {
            String key = trimToNull(entry.getKey());
            String className = trimToNull(entry.getValue());

            if (key != null && className != null) {
                propsMap.put(key.toLowerCase(), className);
            }
        }

        return propsMap;
    }

    private static String guessSlf4jLogSystem(Map<String, String> providers) {
        String s;

        try {
            s = LoggerFactory.getILoggerFactory().getClass().getName().toLowerCase();
        } catch (Throwable e) {
            s = null;
        }

        if (s != null) {
            for (String name : providers.keySet()) {
                if (s.contains(name)) {
                    return name;
                }
            }
        }

        return null;
    }

    protected static String trimToNull(Object str) {
        if (!(str instanceof String)) {
            return null;
        }

        String result = ((String) str).trim();

        if (result == null || result.length() == 0) {
            return null;
        }

        return result;
    }

    protected static void log(String msg) {
        log(msg, null);
    }

    protected static void log(String msg, Throwable e) {
        System.out.flush(); // ��ֹ����̨�������
        System.err.println(msg);

        if (e != null) {
            e.printStackTrace();
        }

        System.err.flush();
    }
}
