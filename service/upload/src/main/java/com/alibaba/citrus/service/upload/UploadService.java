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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

/**
 * ��������<code>multipart/form-data</code>��ʽ��HTTP POST���󣬲�������ת����form�ֶλ��ļ���
 * 
 * @author Michael Zhou
 */
public interface UploadService extends UploadConfiguration {
    /**
     * �ж��Ƿ��Ƿ���<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * 
     * @param request HTTP����
     * @return ����ǣ��򷵻�<code>true</code>
     */
    boolean isMultipartContent(HttpServletRequest request);

    /**
     * ��������<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * 
     * @param request HTTP����
     * @return <code>FileItem</code>���б����������˳������
     * @throws UploadException �������ʱ����
     */
    FileItem[] parseRequest(HttpServletRequest request);

    /**
     * ��������<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * <p>
     * �˷���������service��Ĭ�����ã��ʺ�����action��servlet���ֹ�ִ�С�
     * </p>
     * 
     * @param request HTTP����
     * @param params upload����
     * @return <code>FileItem</code>���б����������˳������
     * @throws UploadException �������ʱ����
     */
    FileItem[] parseRequest(HttpServletRequest request, UploadParameters params);
}
