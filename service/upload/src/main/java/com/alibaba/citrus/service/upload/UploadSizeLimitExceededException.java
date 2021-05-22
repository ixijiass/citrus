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

/**
 * ����һ��uploadʱ���ļ����糬��������Ƶ��쳣��
 * 
 * @author Michael Zhou
 */
public class UploadSizeLimitExceededException extends UploadException {
    private static final long serialVersionUID = 3835158363137716276L;

    /**
     * ����һ���쳣��
     */
    public UploadSizeLimitExceededException() {
        super();
    }

    /**
     * ����һ���쳣��
     * 
     * @param message �쳣��Ϣ
     */
    public UploadSizeLimitExceededException(String message) {
        super(message);
    }

    /**
     * ����һ���쳣��
     * 
     * @param message �쳣��Ϣ
     * @param cause �쳣ԭ��
     */
    public UploadSizeLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * ����һ���쳣��
     * 
     * @param cause �쳣ԭ��
     */
    public UploadSizeLimitExceededException(Throwable cause) {
        super(cause);
    }
}
