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
package com.alibaba.citrus.service.resource.loader;

import static com.alibaba.citrus.service.resource.ResourceLoadingOption.*;
import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.FileUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.resource.Resource;
import com.alibaba.citrus.service.resource.ResourceLister;
import com.alibaba.citrus.service.resource.ResourceListerContext;
import com.alibaba.citrus.service.resource.ResourceLoaderContext;
import com.alibaba.citrus.service.resource.ResourceLoadingOption;
import com.alibaba.citrus.service.resource.ResourceLoadingService;
import com.alibaba.citrus.service.resource.ResourceMatchResult;
import com.alibaba.citrus.service.resource.support.FileResource;
import com.alibaba.citrus.util.internal.ToStringBuilder;

/**
 * ����װ���ļ�ϵͳ�е���Դ��
 * 
 * @author Michael Zhou
 */
public class FileResourceLoader implements ResourceLister {
    private final static Logger log = LoggerFactory.getLogger(FileResourceLoader.class);
    private String basedir;
    private String configFileBasedir;
    private SearchPath[] paths;

    /**
     * ȡ��basedir��
     * <p>
     * ����û��ָ�����򷵻ص�ǰ�����ļ����ڵ�Ŀ¼����file-loader�������ڵ�Ŀ¼����
     * </p>
     */
    public String getBasedir() {
        return basedir;
    }

    /**
     * ����basedir��
     */
    public void setBasedir(String basedir) {
        this.basedir = trimToNull(basedir);
    }

    /**
     * ȡ���������õ�ǰfile-loader�������ļ����ڵ�Ŀ¼��
     * <p>
     * ���Ŀ¼��������Ĭ�ϵ����·����Ŀ¼��
     * </p>
     * <p>
     * ���������ļ�����ֱ�Ӵ��ļ�ϵͳ��ȡ�ã��򷵻�<code>null</code>��
     * </p>
     */
    public String getConfigFileBasedir() {
        return configFileBasedir;
    }

    /**
     * ����file-loader���ڵ������ļ���URL��
     */
    public void setConfigFileURL(URL configFileURL) {
        if (configFileURL != null) {
            File configFile = null;

            try {
                configFile = new File(configFileURL.toURI());
            } catch (Exception e) {
                // not a file: URL
            }

            if (configFile != null) {
                this.configFileBasedir = configFile.getParentFile().getAbsolutePath();
            }
        }
    }

    public SearchPath[] getPaths() {
        return paths;
    }

    public void setPaths(SearchPath[] paths) {
        this.paths = paths;
    }

    /**
     * ��ʼ��loader�����趨loader���ڵ�<code>ResourceLoadingService</code>��ʵ����
     */
    public void init(ResourceLoadingService resourceLoadingService) {
        // ����basedir��
        // 1. ���û��ָ��basedir���򽫵�ǰ�����ļ�����Ŀ¼����basedir
        // 2. ���ָ�������·����basedir��������ڵ�ǰ�����ļ�����Ŀ¼
        // 3. ���ָ���˾���·����basedir�����Դ���Ϊbasedir
        // ��󣬹��basedir��
        if (basedir == null) {
            basedir = configFileBasedir;
        } else {
            if (configFileBasedir != null) {
                basedir = getSystemDependentAbsolutePathBasedOn(configFileBasedir, basedir);
            }
        }

        basedir = trimToNull(normalizePath(basedir));

        // ���δָ��path�������Ĭ�ϵ�path��/
        if (isEmptyArray(paths)) {
            paths = new SearchPath[] { new SearchPath("/", true) };
        }

        // ����relative path��basedir
        for (SearchPath searchPath : paths) {
            searchPath.init(basedir);
        }
    }

    /**
     * �����ļ���Դ��
     */
    public Resource getResource(ResourceLoaderContext context, Set<ResourceLoadingOption> options) {
        File file = find(context, options);

        if (file != null) {
            return new FileResource(file);
        } else {
            return null;
        }
    }

    /**
     * ����Ŀ¼�б�
     */
    public String[] list(ResourceListerContext context, Set<ResourceLoadingOption> options) {
        File file = find(context, options);
        File[] files = file == null ? null : file.listFiles();

        if (files != null) {
            String[] names = new String[files.length];

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    names[i] = files[i].getName() + "/";
                } else {
                    names[i] = files[i].getName();
                }
            }

            return names;
        } else {
            return null;
        }
    }

    /**
     * �����ļ���
     */
    private File find(ResourceMatchResult context, Set<ResourceLoadingOption> options) {
        File file = null;

        log.trace("Searching for file {} in {} search-paths", context.getResourceName(), paths.length);

        for (SearchPath searchPath : paths) {
            File resourceFile = searchPath.getPath(context);

            if (log.isTraceEnabled()) {
                StringBuilder buf = new StringBuilder();

                buf.append("Search in ").append(searchPath).append("\n");
                buf.append("  Testing file: ").append(resourceFile.getAbsolutePath());

                if (resourceFile.exists()) {
                    buf.append(", file exists");
                } else {
                    buf.append(", file does not exist");
                }

                log.trace(buf.toString());
            }

            if (resourceFile.exists()) {
                file = resourceFile;
                break;
            } else {
                // ����ļ������ڣ���ָ����for_create�������򷵻ص�һ�������ڵ��ļ�����
                if (options != null && options.contains(FOR_CREATE)) {
                    if (file == null) {
                        file = resourceFile;
                    }
                }
            }
        }

        return file;
    }

    @Override
    public String toString() {
        return new ToStringBuilder().append(getClass().getSimpleName()).append(paths).toString();
    }

    /**
     * ����һ������·����
     * <p>
     * ����ָ����basedir�����ʾpathΪ�����basedir��·���������ʾpathΪ����·����
     * </p>
     */
    public static class SearchPath {
        private final String path;
        private final boolean relative;
        private String basedir;

        public SearchPath(String path, boolean relative) {
            this.path = assertNotNull(trimToNull(normalizePath(path)), "path");
            this.relative = relative;
        }

        public void init(String basedir) {
            if (relative) {
                this.basedir = assertNotNull(basedir, "Could not get basedir for search path: %s.  "
                        + "Please set basedir explictly at file-loader or use absolute path instead", this);
            }
        }

        /**
         * ȡ��ƥ���·����
         */
        public File getPath(ResourceMatchResult context) {
            String realPath = context.substitute(path);

            if (basedir != null) {
                realPath = new File(basedir, realPath).getAbsolutePath();
            }

            return new File(normalizePath(realPath));
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();

            if (relative) {
                buf.append("relpath=").append(path);
                buf.append(", basedir=").append(basedir);
            } else {
                buf.append("abspath=").append(path);
            }

            return buf.toString();
        }
    }
}
