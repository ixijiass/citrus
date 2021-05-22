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
package com.alibaba.citrus.service.requestcontext.parser;

import org.apache.commons.fileupload.FileItem;

import com.alibaba.citrus.service.upload.UploadException;
import com.alibaba.citrus.service.upload.UploadParameters;

/**
 * <code>ParameterParser</code>����������HTTP������GET��POST�Ĳ����Ľӿڡ�
 * 
 * @author Michael Zhou
 */
public interface ParameterParser extends ValueParser {
    /**
     * ȡ��ָ�����Ƶ�<code>FileItem</code>������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return <code>FileItem</code>����
     */
    FileItem getFileItem(String key);

    /**
     * ȡ��ָ�����Ƶ�<code>FileItem</code>������������ڣ��򷵻�<code>null</code>��
     * 
     * @param key ������
     * @return <code>FileItem</code>���������
     */
    FileItem[] getFileItems(String key);

    /**
     * ���<code>FileItem</code>��
     * 
     * @param name ������
     * @param value ����ֵ
     */
    void add(String name, FileItem value);

    /**
     * ��������<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * <p>
     * Ҫִ�д˷������뽫<code>UploadService.automatic</code>���ò������ó�<code>false</code>��
     * �˷���������service��Ĭ�����ã��ʺ�����action��servlet���ֹ�ִ�С�
     * </p>
     * 
     * @throws UploadException �������ʱ����
     */
    void parseUpload() throws UploadException;

    /**
     * ��������<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * <p>
     * Ҫִ�д˷������뽫<code>UploadService.automatic</code>���ò������ó�<code>false</code>��
     * �˷���������service��Ĭ�����ã��ʺ�����action��servlet���ֹ�ִ�С�
     * </p>
     * 
     * @param sizeThreshold �ļ������ڴ��е���ֵ��С�ڴ�ֵ���ļ����������ڴ��С������ֵС��0����ʹ��Ԥ���ֵ
     * @param sizeMax HTTP��������ߴ磬�����˳ߴ�����󽫱�������
     * @param repositoryPath �ݴ������ļ��ľ���·��
     * @throws UploadException �������ʱ����
     */
    void parseUpload(UploadParameters params) throws UploadException;

    /**
     * ��parameters������װ��query string��
     * 
     * @return query string�����û�в������򷵻�<code>null</code>
     */
    String toQueryString();
}
