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

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * �����쳣�Ĺ����ࡣ
 * 
 * @author Michael Zhou
 */
public class ExceptionUtil {
    /**
     * ����쳣�Ƿ���ָ�����͵��쳣����
     */
    public static boolean causedBy(Throwable t, Class<? extends Throwable> causeType) {
        assertNotNull(causeType, "causeType");

        Set<Throwable> causes = createHashSet();

        for (; t != null && !causeType.isInstance(t) && !causes.contains(t); t = t.getCause()) {
            causes.add(t);
        }

        return t != null && causeType.isInstance(t);
    }

    /**
     * ȡ�ð�����ǰ�쳣���ڵ����е�causes�쳣�������ֵ�˳�����С�
     */
    public static List<Throwable> getCauses(Throwable t) {
        return getCauses(t, false);
    }

    /**
     * ȡ�ð�����ǰ�쳣���ڵ����е�causes�쳣�������ֵ�˳�����С�
     */
    public static List<Throwable> getCauses(Throwable t, boolean reversed) {
        LinkedList<Throwable> causes = createLinkedList();

        for (; t != null && !causes.contains(t); t = t.getCause()) {
            if (reversed) {
                causes.addFirst(t);
            } else {
                causes.addLast(t);
            }
        }

        return causes;
    }

    /**
     * ���쳣ת����<code>RuntimeException</code>��
     */
    public static RuntimeException toRuntimeException(Exception e) {
        return toRuntimeException(e, null);
    }

    /**
     * ���쳣ת����<code>RuntimeException</code>��
     */
    public static RuntimeException toRuntimeException(Exception e,
                                                      Class<? extends RuntimeException> runtimeExceptionClass) {
        if (e == null) {
            return null;
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            if (runtimeExceptionClass == null) {
                return new RuntimeException(e);
            } else {
                RuntimeException runtimeException;

                try {
                    runtimeException = runtimeExceptionClass.newInstance();
                } catch (Exception ee) {
                    return new RuntimeException(e);
                }

                runtimeException.initCause(e);
                return runtimeException;
            }
        }
    }

    /**
     * ȡ���쳣��stacktrace�ַ�����
     * 
     * @param throwable �쳣
     * @return stacktrace�ַ���
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);

        throwable.printStackTrace(out);
        out.flush();

        return buffer.toString();
    }
}
