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

import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.citrus.util.io.ByteArrayOutputStream;

/**
 * ����ΨһID��
 * <p>
 * ΨһID������Ԫ�ع��ɣ�<code>machineId-jvmId-timestamp-counter</code>��
 * </p>
 * <p>
 * Ĭ������£�UUID�����ֺʹ�д��ĸ���ɡ�����ڹ��캯��ʱ��ָ��<code>noCase=false</code>
 * ����ô�����ɵ�ID������Сд��ĸ������ID�ĳ��Ȼ�϶̡�
 * </p>
 * 
 * @author Michael Zhou
 */
public class UUID {
    private boolean noCase;
    private String instanceId;
    private AtomicInteger counter;

    public UUID() {
        this(true);
    }

    public UUID(boolean noCase) {
        // 1. Machine ID - ����IP/MAC����
        byte[] machineId = getLocalHostAddress();

        // 2. JVM ID - ��������ʱ������ + �����
        byte[] jvmId = getRandomizedTime();

        this.instanceId = StringUtil.bytesToString(machineId, noCase) + "-" + StringUtil.bytesToString(jvmId, noCase);

        // counter
        this.counter = new AtomicInteger();

        this.noCase = noCase;
    }

    /**
     * ȡ��local host�ĵ�ַ������п��ܣ�ȡ������MAC��ַ��
     */
    private static byte[] getLocalHostAddress() {
        Method getHardwareAddress;

        try {
            getHardwareAddress = NetworkInterface.class.getMethod("getHardwareAddress");
        } catch (Exception e) {
            getHardwareAddress = null;
        }

        byte[] addr;

        try {
            InetAddress localHost = InetAddress.getLocalHost();

            if (getHardwareAddress != null) {
                addr = (byte[]) getHardwareAddress.invoke(NetworkInterface.getByInetAddress(localHost)); // maybe null
            } else {
                addr = localHost.getAddress();
            }
        } catch (Exception e) {
            addr = null;
        }

        if (addr == null) {
            addr = new byte[] { 127, 0, 0, 1 };
        }

        return addr;
    }

    /**
     * ȡ�õ�ǰʱ�䣬�����������
     */
    private byte[] getRandomizedTime() {
        long jvmId = System.currentTimeMillis();
        long random = new SecureRandom().nextLong();

        // ȡ������ID��bytes����ת�����ַ���
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeLong(jvmId);
            dos.writeLong(random);
        } catch (Exception e) {
        }

        return baos.toByteArray().toByteArray();
    }

    public String nextID() {
        // MACHINE_ID + JVM_ID + ��ǰʱ�� + counter
        return instanceId + "-" + StringUtil.longToString(System.currentTimeMillis(), noCase) + "-"
                + StringUtil.longToString(counter.getAndIncrement(), noCase);
    }
}
