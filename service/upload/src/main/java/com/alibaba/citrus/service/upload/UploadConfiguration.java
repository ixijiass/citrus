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
package com.alibaba.citrus.service.upload;

import java.io.File;

import com.alibaba.citrus.util.HumanReadableSize;

/**
 * ����<code>UploadService</code>�Ĳ�����
 * 
 * @author Michael Zhou
 */
public interface UploadConfiguration {
    /** Ĭ��ֵ��HTTP��������ߴ磬�����˳ߴ�����󽫱������� */
    long SIZE_MAX_DEFAULT = -1;

    /** Ĭ��ֵ�������ļ���������ߴ磬�����˳ߴ�����󽫱������� */
    long FILE_SIZE_MAX_DEFAULT = -1;

    /** Ĭ��ֵ�����ļ������ڴ��е���ֵ��С�ڴ�ֵ���ļ����������ڴ��С� */
    int SIZE_THRESHOLD_DEFAULT = 10240;

    /**
     * ȡ���ݴ��ļ���Ŀ¼��
     */
    File getRepository();

    /**
     * ȡ��HTTP��������ߴ磬�����˳ߴ�����󽫱���������λ���ֽڣ�ֵ<code>-1</code>��ʾû�����ơ�
     */
    HumanReadableSize getSizeMax();

    /**
     * ȡ�õ����ļ���������ߴ磬�����˳ߴ���ļ�������������λ���ֽڣ�ֵ<code>-1</code>��ʾû�����ơ�
     */
    HumanReadableSize getFileSizeMax();

    /**
     * ȡ�ý��ļ������ڴ��е���ֵ��С�ڴ�ֵ���ļ����������ڴ��С���λ���ֽڡ�
     */
    HumanReadableSize getSizeThreshold();

    /**
     * �Ƿ���ͨ��form field�������ڴ����<code>sizeThreshold</code>ֵΪ<code>0</code>
     * ��ʱ�򣬸�ֵ�Զ�Ϊ<code>true</code>��
     */
    boolean isKeepFormFieldInMemory();

    /**
     * ��׼���ϴ��ļ������У��������������ݣ�
     * <code>Content-Disposition: attachment; filename=xxx.txt</code>
     * ��Ȼ����Щ���淶��Ӧ�ã���ȡ<code>fname=xxx.txt</code>���˱���Ϊ��������������衣
     */
    String[] getFileNameKey();
}
