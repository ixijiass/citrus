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
package com.alibaba.citrus.service.upload.impl;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;

import com.alibaba.citrus.service.AbstractService;
import com.alibaba.citrus.service.upload.UploadException;
import com.alibaba.citrus.service.upload.UploadParameters;
import com.alibaba.citrus.service.upload.UploadService;
import com.alibaba.citrus.service.upload.UploadSizeLimitExceededException;
import com.alibaba.citrus.service.upload.impl.cfu.DiskFileItemFactory;
import com.alibaba.citrus.service.upload.impl.cfu.ServletFileUpload;
import com.alibaba.citrus.util.HumanReadableSize;

/**
 * ���service���Դ���<code>multipart/form-data</code>��ʽ��HTTP
 * POST���󣬲�������ת����form�ֶλ����ļ���
 * 
 * @author Michael Zhou
 */
public class UploadServiceImpl extends AbstractService<UploadService> implements UploadService {
    private final UploadParameters params = new UploadParameters();
    private ServletFileUpload fileUpload;

    public File getRepository() {
        return params.getRepository();
    }

    public HumanReadableSize getSizeMax() {
        return params.getSizeMax();
    }

    public HumanReadableSize getFileSizeMax() {
        return params.getFileSizeMax();
    }

    public HumanReadableSize getSizeThreshold() {
        return params.getSizeThreshold();
    }

    public boolean isKeepFormFieldInMemory() {
        return params.isKeepFormFieldInMemory();
    }

    public void setSizeMax(HumanReadableSize sizeMax) {
        params.setSizeMax(sizeMax);
    }

    public void setFileSizeMax(HumanReadableSize fileSizeMax) {
        params.setFileSizeMax(fileSizeMax);
    }

    public void setSizeThreshold(HumanReadableSize sizeThreshold) {
        params.setSizeThreshold(sizeThreshold);
    }

    public void setKeepFormFieldInMemory(boolean keepFormFieldInMemory) {
        params.setKeepFormFieldInMemory(keepFormFieldInMemory);
    }

    public void setRepository(File repository) {
        params.setRepository(repository);
    }

    public String[] getFileNameKey() {
        return params.getFileNameKey();
    }

    public void setFileNameKey(String[] fileNameKey) {
        params.setFileNameKey(fileNameKey);
    }

    @Override
    protected void init() {
        params.applyDefaultValues();
        getLogger().info("Upload Parameters: {}", params);

        fileUpload = getFileUpload(params, false);
    }

    /**
     * �ж��Ƿ��Ƿ���<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * 
     * @param request HTTP����
     * @return ����ǣ��򷵻�<code>true</code>
     */
    public boolean isMultipartContent(HttpServletRequest request) {
        return org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);
    }

    /**
     * ��������<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * 
     * @param request HTTP����
     * @return <code>FileItem</code>���б����������˳������
     * @throws UploadException �������ʱ����
     */
    public FileItem[] parseRequest(HttpServletRequest request) {
        return parseRequest(request, null);
    }

    /**
     * ��������<a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>��׼��
     * <code>multipart/form-data</code>���͵�HTTP����
     * <p>
     * �˷���������service��Ĭ�����ã��ʺ�����action��servlet���ֹ�ִ�С�
     * </p>
     * 
     * @param request HTTP����
     * @param sizeThreshold �ļ������ڴ��е���ֵ��С�ڴ�ֵ���ļ����������ڴ��С������ֵС��0����ʹ��Ԥ���ֵ
     * @param sizeMax HTTP��������ߴ磬�����˳ߴ�����󽫱�������
     * @param repositoryPath �ݴ������ļ��ľ���·��
     * @param charset ��������HTTP header�ı����ַ���
     * @return <code>FileItem</code>���б����������˳������
     * @throws UploadException �������ʱ����
     */
    public FileItem[] parseRequest(HttpServletRequest request, UploadParameters params) {
        assertInitialized();

        ServletFileUpload fileUpload;

        if (params == null || params.equals(this.params)) {
            fileUpload = this.fileUpload;
        } else {
            fileUpload = getFileUpload(params, true);
        }

        List<?> fileItems;

        try {
            fileItems = fileUpload.parseRequest(request);
        } catch (FileUpload.SizeLimitExceededException e) {
            throw new UploadSizeLimitExceededException(e);
        } catch (FileUpload.FileSizeLimitExceededException e) {
            throw new UploadSizeLimitExceededException(e);
        } catch (FileUploadException e) {
            throw new UploadException(e);
        }

        return fileItems.toArray(new FileItem[fileItems.size()]);
    }

    /**
     * ���ݲ�������<code>FileUpload</code>����
     */
    private ServletFileUpload getFileUpload(UploadParameters params, boolean applyDefaultValues) {
        if (applyDefaultValues) {
            params.applyDefaultValues();
            getLogger().debug("Upload Parameters: {}", params);
        }

        // ��������FileItem�Ĳ���
        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setRepository(params.getRepository());
        factory.setSizeThreshold((int) params.getSizeThreshold().getValue());
        factory.setKeepFormFieldInMemory(params.isKeepFormFieldInMemory());

        // ���ڽ���multipart request�Ĳ���
        ServletFileUpload fileUpload = new ServletFileUpload(factory);

        fileUpload.setSizeMax(params.getSizeMax().getValue());
        fileUpload.setFileSizeMax(params.getFileSizeMax().getValue());
        fileUpload.setFileNameKey(params.getFileNameKey());

        return fileUpload;
    }
}
