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

import static com.alibaba.citrus.util.StringUtil.*;

import java.io.File;

/**
 * ���������ļ�·���ͺ�׺�Ĺ��ߡ�
 * 
 * @author Michael Zhou
 */
public class FileUtil {

    // ==========================================================================
    // ���·����
    // ==========================================================================

    /**
     * ��񻯾���·����
     * <p>
     * �÷��������ԡ�<code>/</code>����ʼ�ľ���·����ת���������£�
     * </p>
     * <ol>
     * <li>·��Ϊ�գ��򷵻�<code>""</code>��</li>
     * <li>������backslash("\\")ת����slash("/")��</li>
     * <li>ȥ���ظ���"/"��"\\"��</li>
     * <li>ȥ��"."���������".."��������˷һ��Ŀ¼��</li>
     * <li>����·��ĩβ��"/"������еĻ������˿�·������</li>
     * <li>���ھ���·�������".."��˷��·�������˸�Ŀ¼�������Ƿ�·�����׳��쳣��</li>
     * </ol>
     * 
     * @param path Ҫ��񻯵�·��
     * @return ��񻯺��·��
     * @throws IllegalPathException ���·���Ƿ�
     */
    public static String normalizeAbsolutePath(String path) throws IllegalPathException {
        return normalizePath(path, true, false, false);
    }

    /**
     * ��񻯾���·����
     * <p>
     * �÷��������ԡ�<code>/</code>����ʼ�ľ���·����ת���������£�
     * </p>
     * <ol>
     * <li>·��Ϊ�գ��򷵻�<code>""</code>��</li>
     * <li>������backslash("\\")ת����slash("/")��</li>
     * <li>ȥ���ظ���"/"��"\\"��</li>
     * <li>ȥ��"."���������".."��������˷һ��Ŀ¼��</li>
     * <li>����·��ĩβ��"/"������еĻ������˿�·����ǿ��ָ��<code>removeTrailingSlash==true</code>����</li>
     * <li>���ھ���·�������".."��˷��·�������˸�Ŀ¼�������Ƿ�·�����׳��쳣��</li>
     * </ol>
     * 
     * @param path Ҫ��񻯵�·��
     * @param removeTrailingSlash �Ƿ�ǿ���Ƴ�ĩβ��<code>"/"</code>
     * @return ��񻯺��·��
     * @throws IllegalPathException ���·���Ƿ�
     */
    public static String normalizeAbsolutePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, true, false, removeTrailingSlash);
    }

    /**
     * ������·����
     * <p>
     * �÷������ز��ԡ�<code>/</code>����ʼ�����·����ת���������£�
     * </p>
     * <ol>
     * <li>·��Ϊ�գ��򷵻�<code>""</code>��</li>
     * <li>������backslash("\\")ת����slash("/")��</li>
     * <li>ȥ���ظ���"/"��"\\"��</li>
     * <li>ȥ��"."���������".."��������˷һ��Ŀ¼��</li>
     * <li>�����·������""��</li>
     * <li>����·��ĩβ��"/"������еĻ������˿�·������</li>
     * </ol>
     * 
     * @param path Ҫ��񻯵�·��
     * @return ��񻯺��·��
     * @throws IllegalPathException ���·���Ƿ�
     */
    public static String normalizeRelativePath(String path) throws IllegalPathException {
        return normalizePath(path, false, true, false);
    }

    /**
     * ������·����
     * <p>
     * �÷������ز��ԡ�<code>/</code>����ʼ�����·����ת���������£�
     * </p>
     * <ol>
     * <li>·��Ϊ�գ��򷵻�<code>""</code>��</li>
     * <li>������backslash("\\")ת����slash("/")��</li>
     * <li>ȥ���ظ���"/"��"\\"��</li>
     * <li>ȥ��"."���������".."��������˷һ��Ŀ¼��</li>
     * <li>�����·������""��</li>
     * <li>����·��ĩβ��"/"������еĻ������˿�·����ǿ��ָ��<code>removeTrailingSlash==true</code>����</li>
     * </ol>
     * 
     * @param path Ҫ��񻯵�·��
     * @param removeTrailingSlash �Ƿ�ǿ���Ƴ�ĩβ��<code>"/"</code>
     * @return ��񻯺��·��
     * @throws IllegalPathException ���·���Ƿ�
     */
    public static String normalizeRelativePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, false, true, removeTrailingSlash);
    }

    /**
     * ���·�����������£�
     * <ol>
     * <li>·��Ϊ�գ��򷵻�<code>""</code>��</li>
     * <li>������backslash("\\")ת����slash("/")��</li>
     * <li>ȥ���ظ���"/"��"\\"��</li>
     * <li>ȥ��"."���������".."��������˷һ��Ŀ¼��</li>
     * <li>�վ���·������"/"�������·������""��</li>
     * <li>����·��ĩβ��"/"������еĻ������˿�·������</li>
     * <li>���ھ���·�������".."��˷��·�������˸�Ŀ¼�������Ƿ�·�����׳��쳣��</li>
     * </ol>
     * 
     * @param path Ҫ��񻯵�·��
     * @return ��񻯺��·��
     * @throws IllegalPathException ���·���Ƿ�
     */
    public static String normalizePath(String path) throws IllegalPathException {
        return normalizePath(path, false, false, false);
    }

    /**
     * ���·�����������£�
     * <ol>
     * <li>·��Ϊ�գ��򷵻�<code>""</code>��</li>
     * <li>������backslash("\\")ת����slash("/")��</li>
     * <li>ȥ���ظ���"/"��"\\"��</li>
     * <li>ȥ��"."���������".."��������˷һ��Ŀ¼��</li>
     * <li>�վ���·������"/"�������·������""��</li>
     * <li>����·��ĩβ��"/"������еĻ������˿�·����ǿ��ָ��<code>removeTrailingSlash==true</code>����</li>
     * <li>���ھ���·�������".."��˷��·�������˸�Ŀ¼�������Ƿ�·�����׳��쳣��</li>
     * </ol>
     * 
     * @param path Ҫ��񻯵�·��
     * @param removeTrailingSlash �Ƿ�ǿ���Ƴ�ĩβ��<code>"/"</code>
     * @return ��񻯺��·��
     * @throws IllegalPathException ���·���Ƿ�
     */
    public static String normalizePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, false, false, removeTrailingSlash);
    }

    private static String normalizePath(String path, boolean forceAbsolute, boolean forceRelative,
                                        boolean removeTrailingSlash) throws IllegalPathException {
        char[] pathChars = trimToEmpty(path).toCharArray();
        int length = pathChars.length;

        // ������·�����Լ�pathβ����"/"
        boolean startsWithSlash = false;
        boolean endsWithSlash = false;

        if (length > 0) {
            char firstChar = pathChars[0];
            char lastChar = pathChars[length - 1];

            startsWithSlash = firstChar == '/' || firstChar == '\\';
            endsWithSlash = lastChar == '/' || lastChar == '\\';
        }

        StringBuilder buf = new StringBuilder(length);
        boolean isAbsolutePath = forceAbsolute || !forceRelative && startsWithSlash;
        int index = startsWithSlash ? 0 : -1;
        int level = 0;

        if (isAbsolutePath) {
            buf.append("/");
        }

        while (index < length) {
            // ������һ����slash�ַ�����ĩβ
            index = indexOfSlash(pathChars, index + 1, false);

            if (index == length) {
                break;
            }

            // ȡ����һ��slash index����ĩβ
            int nextSlashIndex = indexOfSlash(pathChars, index, true);

            String element = new String(pathChars, index, nextSlashIndex - index);
            index = nextSlashIndex;

            // ����"."
            if (".".equals(element)) {
                continue;
            }

            // ��˷".."
            if ("..".equals(element)) {
                if (level == 0) {
                    // ����Ǿ���·����../��ͼԽ�����ϲ�Ŀ¼�����ǲ����ܵģ�
                    // �׳�·���Ƿ����쳣��
                    if (isAbsolutePath) {
                        throw new IllegalPathException(path);
                    } else {
                        buf.append("../");
                    }
                } else {
                    buf.setLength(pathChars[--level]);
                }

                continue;
            }

            // ��ӵ�path
            pathChars[level++] = (char) buf.length(); // ���Ѿ�������chars�ռ����ڼ�¼ָ��level��index
            buf.append(element).append('/');
        }

        // ��ȥ����"/"
        if (buf.length() > 0) {
            if (!endsWithSlash || removeTrailingSlash) {
                buf.setLength(buf.length() - 1);
            }
        }

        return buf.toString();
    }

    private static int indexOfSlash(char[] chars, int beginIndex, boolean slash) {
        int i = beginIndex;

        for (; i < chars.length; i++) {
            char ch = chars[i];

            if (slash) {
                if (ch == '/' || ch == '\\') {
                    break; // if a slash
                }
            } else {
                if (ch != '/' && ch != '\\') {
                    break; // if not a slash
                }
            }
        }

        return i;
    }

    // ==========================================================================
    // ȡ�û���ָ��basedir���·����
    // ==========================================================================

    /**
     * ���ָ��·���Ѿ��Ǿ���·�������񻯺�ֱ�ӷ���֮������ȡ�û���ָ��basedir�Ĺ��·����
     * 
     * @param basedir ��Ŀ¼�����<code>path</code>Ϊ���·������ʾ���ڴ�Ŀ¼
     * @param path Ҫ����·��
     * @return ��񻯵ľ���·��
     * @throws IllegalPathException ���·���Ƿ�
     */
    public static String getAbsolutePathBasedOn(String basedir, String path) throws IllegalPathException {
        // ���pathΪ����·�������񻯺󷵻�
        boolean isAbsolutePath = false;

        path = trimToEmpty(path);

        if (path.length() > 0) {
            char firstChar = path.charAt(0);
            isAbsolutePath = firstChar == '/' || firstChar == '\\';
        }

        if (!isAbsolutePath) {
            // ���pathΪ���·����������basedir�ϲ���
            if (path.length() > 0) {
                path = trimToEmpty(basedir) + "/" + path;
            } else {
                path = trimToEmpty(basedir);
            }
        }

        return normalizeAbsolutePath(path);
    }

    /**
     * ȡ�ú�ϵͳ��صľ���·����
     * 
     * @throws IllegalPathException ���basedir���Ǿ���·��
     */
    public static String getSystemDependentAbsolutePathBasedOn(String basedir, String path) {
        path = trimToEmpty(path);

        boolean endsWithSlash = path.endsWith("/") || path.endsWith("\\");

        File pathFile = new File(path);

        if (pathFile.isAbsolute()) {
            // ���path�Ѿ��Ǿ���·���ˣ���ֱ�ӷ���֮��
            path = pathFile.getAbsolutePath();
        } else {
            // ������basedirΪ����·����
            // ����ȷ��basedir����Ϊ����·����
            basedir = trimToEmpty(basedir);

            File baseFile = new File(basedir);

            if (baseFile.isAbsolute()) {
                path = new File(baseFile, path).getAbsolutePath();
            } else {
                throw new IllegalPathException("Basedir is not absolute path: " + basedir);
            }
        }

        if (endsWithSlash) {
            path = path + '/';
        }

        return normalizePath(path);
    }

    // ==========================================================================
    // ȡ�������ָ��basedir���·����
    // ==========================================================================

    /**
     * ȡ�������ָ����Ŀ¼�����·����
     * 
     * @param basedir ��Ŀ¼
     * @param path Ҫ�����·��
     * @return ���<code>path</code>��<code>basedir</code>�Ǽ��ݵģ��򷵻������
     *         <code>basedir</code>�����·�������򷵻�<code>path</code>����
     * @throws IllegalPathException ���·���Ƿ�
     */
    public static String getRelativePath(String basedir, String path) throws IllegalPathException {
        // ȡ�ù�񻯵�basedir��ȷ����Ϊ����·��
        basedir = normalizeAbsolutePath(basedir);

        // ȡ�ù�񻯵�path
        path = getAbsolutePathBasedOn(basedir, path);

        // ����pathβ����"/"
        boolean endsWithSlash = path.endsWith("/");

        // ��"/"�ָ�basedir��path
        String[] baseParts = StringUtil.split(basedir, '/');
        String[] parts = StringUtil.split(path, '/');
        StringBuilder buf = new StringBuilder();
        int i = 0;

        while (i < baseParts.length && i < parts.length && baseParts[i].equals(parts[i])) {
            i++;
        }

        if (i < baseParts.length && i < parts.length) {
            for (int j = i; j < baseParts.length; j++) {
                buf.append("..").append('/');
            }
        }

        for (; i < parts.length; i++) {
            buf.append(parts[i]);

            if (i < parts.length - 1) {
                buf.append('/');
            }
        }

        if (endsWithSlash && buf.length() > 0 && buf.charAt(buf.length() - 1) != '/') {
            buf.append('/');
        }

        return buf.toString();
    }

    // ==========================================================================
    // ȡ���ļ�����׺��
    // ==========================================================================

    /**
     * ȡ���ļ�·���ĺ�׺��
     * <ul>
     * <li>δָ���ļ��� - ����<code>null</code>��</li>
     * <li>�ļ���û�к�׺ - ����<code>null</code>��</li>
     * </ul>
     */
    public static String getExtension(String fileName) {
        return getExtension(fileName, null, false);
    }

    /**
     * ȡ���ļ�·���ĺ�׺��
     * <ul>
     * <li>δָ���ļ��� - ����<code>null</code>��</li>
     * <li>�ļ���û�к�׺ - ����<code>null</code>��</li>
     * </ul>
     */
    public static String getExtension(String fileName, boolean toLowerCase) {
        return getExtension(fileName, null, toLowerCase);
    }

    /**
     * ȡ���ļ�·���ĺ�׺��
     * <ul>
     * <li>δָ���ļ��� - ����<code>null</code>��</li>
     * <li>�ļ���û�к�׺ - ����ָ���ַ���<code>nullExt</code>��</li>
     * </ul>
     */
    public static String getExtension(String fileName, String nullExt) {
        return getExtension(fileName, nullExt, false);
    }

    /**
     * ȡ���ļ�·���ĺ�׺��
     * <ul>
     * <li>δָ���ļ��� - ����<code>null</code>��</li>
     * <li>�ļ���û�к�׺ - ����ָ���ַ���<code>nullExt</code>��</li>
     * </ul>
     */
    public static String getExtension(String fileName, String nullExt, boolean toLowerCase) {
        fileName = trimToNull(fileName);

        if (fileName == null) {
            return null;
        }

        fileName = fileName.replace('\\', '/');
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

        int index = fileName.lastIndexOf(".");
        String ext = null;

        if (index >= 0) {
            ext = trimToNull(fileName.substring(index + 1));
        }

        if (ext == null) {
            return nullExt;
        } else {
            return toLowerCase ? ext.toLowerCase() : ext;
        }
    }

    /**
     * ȡ��ָ��·�������ƺͺ�׺��
     * 
     * @param path ·��
     * @return ·���ͺ�׺
     */
    public static FileNameAndExtension getFileNameAndExtension(String path) {
        return getFileNameAndExtension(path, false);
    }

    /**
     * ȡ��ָ��·�������ƺͺ�׺��
     * 
     * @param path ·��
     * @return ·���ͺ�׺
     */
    public static FileNameAndExtension getFileNameAndExtension(String path, boolean extensionToLowerCase) {
        path = StringUtil.trimToEmpty(path);

        String fileName = path;
        String extension = null;

        if (!StringUtil.isEmpty(path)) {
            // ����ҵ���׺����index >= 0����extension != null������name��.��β��
            int index = path.lastIndexOf('.');

            if (index >= 0) {
                extension = StringUtil.trimToNull(StringUtil.substring(path, index + 1));

                if (!StringUtil.containsNone(extension, "/\\")) {
                    extension = null;
                    index = -1;
                }
            }

            if (index >= 0) {
                fileName = StringUtil.substring(path, 0, index);
            }
        }

        return new FileNameAndExtension(fileName, extension, extensionToLowerCase);
    }

    /**
     * ����ļ�����׺��
     * <ul>
     * <li>��ȥ���߿հס�</li>
     * <li>ת��Сд��</li>
     * <li>��ȥ��ͷ�ġ�<code>.</code>����</li>
     * <li>�Կհ׵ĺ�׺������<code>null</code>��</li>
     * </ul>
     */
    public static String normalizeExtension(String ext) {
        ext = trimToNull(ext);

        if (ext != null) {
            ext = ext.toLowerCase();

            if (ext.startsWith(".")) {
                ext = trimToNull(ext.substring(1));
            }
        }

        return ext;
    }

    public static class FileNameAndExtension {
        private final String fileName;
        private final String extension;

        private FileNameAndExtension(String fileName, String extension, boolean extensionToLowerCase) {
            this.fileName = fileName;
            this.extension = extensionToLowerCase ? toLowerCase(extension) : extension;
        }

        public String getFileName() {
            return fileName;
        }

        public String getExtension() {
            return extension;
        }

        @Override
        public String toString() {
            return extension == null ? fileName : fileName + "." + extension;
        }
    }
}
