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
package com.alibaba.citrus.service.mail.session;

import static com.alibaba.citrus.service.mail.MailConstant.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.Map;
import java.util.Properties;

import javax.mail.Session;

import com.alibaba.citrus.service.mail.MailException;
import com.alibaba.citrus.service.mail.MailService;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ������װ��<code>java.mail.Session</code>���Ը��Ѻõķ�ʽ��֧��mail transport��store��
 * <p>
 * ע�⣬<code>java.mail.Session</code>���ڶ�ε��úͶ���߳��й���ģ���<code>MailSession</code>
 * ����Ƴ�ÿ�ε��ö������µġ�
 * </p>
 * 
 * @author Michael Zhou
 */
public abstract class MailSession {
    private final Properties props = new Properties();
    private MailService mailService;
    private Session session;
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean debug;
    private boolean defaultSession;

    /**
     * ����һ��mail session��
     */
    public MailSession() {
    }

    /**
     * ����һ��mail session��
     */
    public MailSession(MailSession session, Properties overrideProps) {
        this.mailService = session.mailService;
        this.session = session.getSession(); // ע�⣬�˷�����synchronized��
        this.host = session.host;
        this.port = session.port;
        this.user = session.user;
        this.password = session.password;
        this.debug = session.debug;
        this.defaultSession = false; // default value not copied

        this.props.putAll(session.props);

        if (overrideProps != null) {
            for (Object element : overrideProps.keySet()) {
                String key = (String) element;
                String value = overrideProps.getProperty(key);

                // ע�⣬ִ�д˷���ʱ�����key/value��ԭֵ��ͬ��session������ա�
                setProperty(key, value, null);
            }
        }
    }

    /**
     * ȡ�ô�����session��mail service��
     */
    public MailService getMailService() {
        return mailService;
    }

    /**
     * ����mail service��
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * ȡ��mail server�ķ���������IP��ַ��
     */
    public String getHost() {
        return host;
    }

    /**
     * ����mail server�ķ���������IP��ַ��
     */
    public void setHost(String host) {
        this.host = trimToNull(host);
    }

    /**
     * ȡ��mail server�ķ������˿ڡ�
     */
    public int getPort() {
        return port > 0 ? port : -1;
    }

    /**
     * ����mail server�ķ������˿ڡ�
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * �ж��Ƿ���Ҫ��֤��
     */
    public boolean useAuth() {
        return user != null;
    }

    /**
     * ȡ��mail server����֤�û���
     */
    public String getUser() {
        return user;
    }

    /**
     * ����mail server����֤�û���
     */
    public void setUser(String user) {
        this.user = trimToNull(user);
    }

    /**
     * ȡ��mail server����֤���롣
     */
    public String getPassword() {
        return password;
    }

    /**
     * ����mail server����֤���롣
     */
    public void setPassword(String password) {
        this.password = trimToNull(password);
    }

    /**
     * �Ƿ���debugģʽ���ڴ�ģʽ�£�javamail���ӡ���������Ϣ��
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * ����debugģʽ���ڴ�ģʽ�£�javamail���ӡ���������Ϣ��
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * �Ƿ�ΪĬ�ϵ�transport��store��
     * <p>
     * ��һ��<code>MailService</code>��ֻ����һ��Ĭ�ϵ�transport��һ��Ĭ�ϵ�store��
     * </p>
     */
    public boolean isDefault() {
        return defaultSession;
    }

    /**
     * ����ΪĬ�ϵ�transport��store��
     * <p>
     * ��һ��<code>MailService</code>��ֻ����һ��Ĭ�ϵ�transport��һ��Ĭ�ϵ�store��
     * </p>
     */
    public void setDefault(boolean defaultSession) {
        this.defaultSession = defaultSession;
    }

    /**
     * �����������ԡ�
     */
    public void setProperties(Map<String, String> props) {
        if (props != null) {
            this.props.clear();

            for (Map.Entry<String, String> entry : props.entrySet()) {
                String key = assertNotNull(trimToNull(entry.getKey()), "propertyName");
                String value = trimToNull(entry.getValue());

                setProperty(key, value);
            }
        }
    }

    /**
     * ����session�����ԣ����ֵ���ı��ˣ������session��
     */
    public void setProperty(String key, String value) {
        setProperty(key, value, null);
    }

    /**
     * ����session�����ԣ����ֵ���ı��ˣ������session��
     */
    protected final void setProperty(String key, String value, String defaultValue) {
        String currentValue = props.getProperty(key, defaultValue);

        if (!isEquals(currentValue, value)) {
            props.setProperty(key, value);
            session = null;
        }
    }

    /**
     * ȡ��session properties��
     */
    protected Properties getSessionProperties() {
        setProperty(MAIL_DEBUG, String.valueOf(isDebug()), "false");
        return props;
    }

    /**
     * ȡ��javamail session��
     * <p>
     * �˷������̰߳�ȫ�ģ��������ڸ���session��ʱ��
     * </p>
     */
    protected synchronized Session getSession() {
        // ע�⣬��ִ�д˷���ʱ��session�п��ܱ���ա�
        Properties props = getSessionProperties();

        if (session == null) {
            session = Session.getInstance(props);
        }

        return session;
    }

    /**
     * �ж��Ƿ��Ѿ������ϡ�
     */
    protected abstract boolean isConnected();

    /**
     * ����mail��������
     */
    protected abstract void connect() throws MailException;

    /**
     * �ر�mail�����������ӡ�
     */
    protected abstract void close() throws MailException;

    @Override
    public final String toString() {
        MapBuilder mb = new MapBuilder().setSortKeys(true).setPrintCount(true);

        mb.append("host", getHost());
        mb.append("port", getPort());
        mb.append("user", getUser());
        mb.append("password", getPassword());
        mb.append("debug", isDebug());
        mb.append("default", isDefault());
        mb.append("otherProperties", props);

        toString(mb);

        return new ToStringBuilder().append(getClass().getSimpleName()).append(mb).toString();
    }

    protected abstract void toString(MapBuilder mb);
}
