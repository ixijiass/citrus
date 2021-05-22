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

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.ClassUtil.*;

/**
 * �ӳټ��ض���
 * <p>
 * Ŀǰ�����ֿ��õļ��ط�������������������JDK�ͻ������죺
 * </p>
 * <ol>
 * <li>ͬ����(Synchronized) �� �����������ɿ���</li>
 * <li>�����̵߳�(PerThread) �� �ȵ�һ�ֿ�5��50�����ң�������Ҳ�ǿɿ��ġ�</li>
 * <li>����DCL��(Double-Checked Locking) �� �ȵ�һ�ֿ�5��70�����ң������ϲ��ɿ�������JDK5�Ժ�Ӧ��û�������ˡ�</li>
 * </ol>
 * 
 * @author Michael Zhou
 */
public abstract class LazyLoader<T, C> {
    private final Loader<T, C> loader;

    protected LazyLoader(Loader<T, C> loader) {
        this.loader = assertNotNull(loader);
    }

    public final T getInstance() {
        return getInstance(null);
    }

    public abstract T getInstance(C context);

    public abstract boolean testInstance();

    /**
     * ����loaderװ�ض���
     */
    protected final T load(C context) {
        try {
            return loader.load(context);
        } catch (RuntimeException e) {
            if (loader instanceof ExceptionHandler<?, ?>) {
                return ((ExceptionHandler<T, C>) loader).handle(e, context);
            } else {
                throw e;
            }
        }
    }

    /**
     * ������������ʵ����
     */
    public static interface Loader<T, C> {
        T load(C context);
    }

    public static interface ExceptionHandler<T, C> extends Loader<T, C> {
        T handle(RuntimeException e, C context);
    }

    /**
     * ȡ��Ĭ�ϵķ�����
     */
    public static <T, C> LazyLoader<T, C> getDefault(Loader<T, C> loader) {
        return getDoubleCheckedLockingLazyLoader(loader);
    }

    /**
     * �ñ��ص�ͬ����������������
     * <p>
     * �÷������κ�JVM�ж��ǰ�ȫ�ġ�
     * </p>
     */
    public static <T, C> LazyLoader<T, C> getSynchronizedLazyLoader(Loader<T, C> loader) {
        return new LazyLoader<T, C>(loader) {
            private boolean loaded;
            private T instance;

            @Override
            public T getInstance(C context) {
                synchronized (this) {
                    if (!loaded) {
                        instance = load(context);
                        loaded = true;
                    }
                }

                return instance;
            }

            @Override
            public synchronized boolean testInstance() {
                return loaded;
            }
        };
    }

    /**
     * ����<code>ThreadLocal</code>����ǵ�ǰ�߳��Ƿ������ͬ���������Ӷ�ʵ���ӳ�װ�ء�
     * <p>
     * �÷������κ�JVM�ж��ǰ�ȫ�ġ�
     * </p>
     */
    public static <T, C> LazyLoader<T, C> getPerThreadLazyLoader(Loader<T, C> loader) {
        return new LazyLoader<T, C>(loader) {
            private final ThreadLocal<Boolean> synced = new ThreadLocal<Boolean>();
            private boolean loaded;
            private T instance;

            @Override
            public T getInstance(C context) {
                if (synced.get() == null) {
                    synchronized (this) {
                        if (!loaded) {
                            instance = load(context);
                            loaded = true;
                        }
                    }

                    synced.set(Boolean.TRUE);
                }

                return instance;
            }

            @Override
            public boolean testInstance() {
                if (synced.get() == null) {
                    synchronized (this) {
                        if (loaded) {
                            synced.set(Boolean.TRUE);
                        }

                        return loaded;
                    }
                } else {
                    return true;
                }
            }
        };
    }

    /**
     * ����<code>volatile</code>���������ԣ���DCL�ķ�ʽ����װ�ء�
     * <p>
     * ע�⣬��ʵ�������ϱ�<code>SynchronizedLazyLoader</code>�и��õ����ܣ�������ȷ��ȡ����JVM��ʵ�֡�<br>
     * һ����Ϊ��JDK5�Ժ�֧�ֶ�<code>volatile</code>������DCL������
     * </p>
     */
    public static <T, C> LazyLoader<T, C> getDoubleCheckedLockingLazyLoader(Loader<T, C> loader) {
        return new LazyLoader<T, C>(loader) {
            private volatile boolean loaded;
            private volatile T instance;

            @Override
            public T getInstance(C context) {
                if (!loaded) {
                    synchronized (this) {
                        if (!loaded) {
                            instance = load(context);
                            loaded = true;
                        }
                    }
                }

                return instance;
            }

            @Override
            public boolean testInstance() {
                return loaded;
            }
        };
    }

    /**
     * ת�����ַ�����ʾ��
     */
    @Override
    public String toString() {
        return String.format("LazyLoader(%s%s)", getSimpleClassName(loader.getClass()), (testInstance() ? ", loaded"
                : EMPTY_STRING));
    }
}
