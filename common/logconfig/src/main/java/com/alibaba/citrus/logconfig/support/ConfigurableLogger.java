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
package com.alibaba.citrus.logconfig.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ��װ��<code>Logger</code>���ṩ�˶���Ĺ��ܡ�
 * <ul>
 * <li>֧��Ĭ�ϵ�log��������ͨ��<code>setLogName()</code>�������ı䡣ͨ����ͨ�������ļ������ø�ֵ�ġ�</li>
 * <li>�ṩ<code>log(level, ...)</code>�������������ָ��level����־��</li>
 * </ul>
 * 
 * @author Michael Zhou
 */
public abstract class ConfigurableLogger {
    private Logger log;

    /**
     * ����û������<code>logName</code>����ô�Ͱ���־��������Ĭ�ϵ�logger�С�
     */
    protected abstract Logger getDefaultLogger();

    public final Logger getLogger() {
        if (log == null) {
            log = getDefaultLogger();
        }

        return log;
    }

    public final void setLogName(String logName) {
        if (logName != null) {
            logName = logName.trim();

            if (logName.length() == 0) {
                logName = null;
            }
        }

        if (logName != null) {
            log = LoggerFactory.getLogger(logName);
        }
    }

    public final boolean isLevelEnabled(Level level) {
        Logger log = getLogger();

        if (level != null) {
            switch (level) {
                case trace:
                    return log.isTraceEnabled();

                case debug:
                    return log.isDebugEnabled();

                case info:
                    return log.isInfoEnabled();

                case warn:
                    return log.isWarnEnabled();

                case error:
                    return log.isErrorEnabled();
            }
        }

        unknownLogLevel(level);
        return false;
    }

    public final void log(Level level, String message) {
        log(level, message, null);
    }

    public final void log(Level level, String message, Throwable e) {
        Logger log = getLogger();

        if (level != null) {
            switch (level) {
                case trace:
                    log.trace(message, e);
                    return;

                case debug:
                    log.debug(message, e);
                    return;

                case info:
                    log.info(message, e);
                    return;

                case warn:
                    log.warn(message, e);
                    return;

                case error:
                    log.error(message, e);
                    return;
            }
        }

        unknownLogLevel(level);
    }

    private void unknownLogLevel(Level level) {
        throw new IllegalArgumentException("Unknown log level: " + level);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getLogger().getName() + "]";
    }

    /**
     * ��־�ļ���
     */
    public static enum Level {
        trace,
        debug,
        info,
        warn,
        error
    }
}
