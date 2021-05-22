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
package com.alibaba.citrus.util.internal;

import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.ClassUtil.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.alibaba.citrus.util.StringUtil;

/**
 * ���ɳ����е���־��������Ϣ�Ĺ����ࡣ
 * 
 * @author Michael Zhou
 */
public class MessageFormatter<T> {
    private final Map<Locale, Reference<ResourceBundle>> bundles;

    /**
     * ����һ��<code>MessageFormatter</code>ʵ����
     */
    public MessageFormatter() {
        this.bundles = createConcurrentHashMap();
    }

    /**
     * ȡ��<code>ResourceBundle</code>�����ƣ�Ĭ�Ϻ�������ͬ��
     */
    protected String getBundleName() {
        return getClass().getName();
    }

    /**
     * ȡ��ָ��locale��<code>ResourceBundle</code>��
     */
    private ResourceBundle getBundle(Locale locale) {
        if (locale == null) {
            locale = getDefaultLocale();
        }

        Reference<ResourceBundle> bundleRef = bundles.get(locale);
        ResourceBundle bundle = null;

        if (bundleRef != null) {
            bundle = bundleRef.get();
        }

        if (bundle == null) {
            bundle = ResourceBundle.getBundle(getBundleName(), locale);
            bundles.put(locale, new WeakReference<ResourceBundle>(bundle));
        }

        return bundle;
    }

    /**
     * ȡ��ָ����enum������ע�ġ�ϵͳĬ��locale����Ϣ��������ʽ��֮��
     */
    public String format(T key, Object... args) {
        return format(key, null, args);
    }

    /**
     * ȡ��ָ����enum������ע�ġ�ָ��locale����Ϣ��������ʽ��֮��
     */
    public String format(T key, Locale locale, Object... args) {
        ResourceBundle bundle = getBundle(locale);
        String strKey = String.valueOf(key);
        String message;

        try {
            message = format(bundle.getString(strKey), args);
        } catch (MissingResourceException e) {
            StringBuilder buffer = new StringBuilder();

            buffer.append(strKey).append(": ");

            if (args != null && args.length > 0) {
                for (Object arg : args) {
                    buffer.append(arg).append(", ");
                }
            }

            buffer.setLength(buffer.length() - 2);

            message = buffer.toString();
        }

        return message;
    }

    private String format(String format, Object[] args) {
        format = StringUtil.trimToEmpty(format);

        if (args.length == 0) {
            return format;
        } else {
            return java.text.MessageFormat.format(format, preprocess(args));
        }
    }

    /**
     * Ԥ���������ʹ֮���õر���ʾ��
     */
    protected final Object preprocess(Object o) {
        if (o == null) {
            return EMPTY_STRING;
        } else if (o instanceof Object[]) {
            return preprocess((Object[]) o);
        } else if (o instanceof Throwable) {
            return preprocess((Throwable) o);
        } else if (o instanceof Class<?>) {
            return preprocess((Class<?>) o);
        } else if (o instanceof Double) {
            double d = (Double) o;

            if (Double.isNaN(d) || Double.isInfinite(d)) {
                return o.toString();
            } else {
                return o;
            }
        } else if (o instanceof Float) {
            float f = (Float) o;

            if (Float.isNaN(f) || Float.isInfinite(f)) {
                return o.toString();
            } else {
                return o;
            }
        } else if (o instanceof Constructor<?>) {
            return preprocess((Constructor<?>) o);
        } else if (o instanceof Method) {
            return preprocess((Method) o);
        } else {
            return preprocessObject(o);
        }
    }

    /**
     * ������Ԥ���ӿڣ��Ա㴦��δ֪���͡�
     */
    protected Object preprocessObject(Object o) {
        return o;
    }

    private Object[] preprocess(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = preprocess(args[i]);
        }

        return args;
    }

    private Object preprocess(Throwable t) {
        String message = t.getMessage();

        if (message != null) {
            return getSimpleClassName(t.getClass()) + " - " + message;
        } else {
            return getSimpleClassName(t.getClass());
        }
    }

    private Object preprocess(Class<?> c) {
        return getSimpleClassName(c);
    }

    private Object preprocess(Constructor<?> method) {
        try {
            return preprocessMethodOrConstructor(method.getModifiers(), null, method.getDeclaringClass(), null,
                    method.getParameterTypes(), method.getExceptionTypes());
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    private Object preprocess(Method method) {
        try {
            return preprocessMethodOrConstructor(method.getModifiers(), method.getReturnType(),
                    method.getDeclaringClass(), method.getName(), method.getParameterTypes(),
                    method.getExceptionTypes());
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    private Object preprocessMethodOrConstructor(int mod, Class<?> returnType, Class<?> declaringClass,
                                                 String methodName, Class<?>[] paramTypes, Class<?>[] exceptionTypes) {
        StringBuilder sb = new StringBuilder();

        // return type
        if (returnType != null) {
            sb.append(getSimpleClassName(returnType)).append(" ");
        }

        // declaring class
        sb.append(getSimpleClassName(declaringClass));

        // method name
        if (methodName != null) {
            sb.append(".").append(methodName);
        }

        sb.append("(");

        // params
        for (int i = 0; i < paramTypes.length; i++) {
            sb.append(getSimpleClassName(paramTypes[i]));

            if (i < paramTypes.length - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");

        // exceptions
        if (exceptionTypes.length > 0) {
            sb.append(" throws ");

            for (int i = 0; i < exceptionTypes.length; i++) {
                sb.append(getSimpleClassName(exceptionTypes[i]));

                if (i < exceptionTypes.length - 1) {
                    sb.append(",");
                }
            }
        }

        return sb.toString();
    }

    /**
     * ȡ��Ĭ�ϵ�locale��
     */
    private static Locale getDefaultLocale() {
        Locale locale = null;

        if (defaultLocale != null) {
            locale = defaultLocale.get();
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }

    private static ThreadLocal<Locale> defaultLocale;

    /**
     * ��������Ǹ���Ԫ����Ԥ���ģ�����ĳ��򲻿��á�
     */
    static void setDefaultLocale(Locale locale) {
        if (defaultLocale == null) {
            defaultLocale = new ThreadLocal<Locale>();
        }

        defaultLocale.set(locale);
    }
}
