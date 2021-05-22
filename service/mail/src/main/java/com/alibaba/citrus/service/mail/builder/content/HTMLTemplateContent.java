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
package com.alibaba.citrus.service.mail.builder.content;

import static com.alibaba.citrus.service.mail.MailConstant.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.alibaba.citrus.service.mail.builder.MailBuilderException;
import com.alibaba.citrus.service.mail.support.ResourceDataSource;
import com.alibaba.citrus.service.mail.util.MailUtil;
import com.alibaba.citrus.service.template.TemplateContext;
import com.alibaba.citrus.util.FileUtil;
import com.alibaba.citrus.util.SystemUtil;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ��ģ�����ɵ�HTML�����ݡ�
 * 
 * @author Michael Zhou
 */
public class HTMLTemplateContent extends TemplateContent implements ResourceLoaderAware {
    private ResourceLoader resourceLoader;
    private Map<String, String> inlineResourceMap = createHashMap();
    private Map<String, InlineResource> inlineResources = createHashMap();

    /**
     * ����һ��<code>HTMLTemplateContent</code>��
     */
    public HTMLTemplateContent() {
    }

    /**
     * ����һ��<code>HTMLTemplateContent</code>��
     */
    public HTMLTemplateContent(String templateName) {
        setTemplate(templateName);
    }

    /**
     * ����һ��<code>HTMLTemplateContent</code>��
     */
    public HTMLTemplateContent(String templateName, String contentType) {
        setTemplate(templateName);
        setContentType(contentType);
    }

    /**
     * ȡ������װ����Դ��<code>ResourceLoader</code>��
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    /**
     * ��������װ����Դ��<code>ResourceLoader</code>��
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * ����һ��������Դ��tool��
     */
    public void setInlineResources(Map<String, String> resourceMap) {
        if (resourceMap != null) {
            inlineResourceMap.clear();

            for (Map.Entry<String, String> entry : resourceMap.entrySet()) {
                addInlineResource(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * ���һ����ģ���д���������Դ��tool�����У�<code>id</code>Ϊ��ģ�������ø�tool��key��
     * <code>prefix</code>Ϊ��tool�ڲ�����Դʱ���Զ�����ָ��ǰ׺��
     */
    public void addInlineResource(String id, String prefix) {
        id = assertNotNull(trimToNull(id), "The ID of inline resource was not specified");
        prefix = assertNotNull(trimToNull(prefix), "The prefix of inline resource was not specified");

        assertTrue(!inlineResourceMap.containsKey(id), "Duplicated ID \"%s\" of inline resource", id);

        inlineResourceMap.put(id, prefix);
    }

    /**
     * ��Ⱦ�ʼ����ݡ�
     */
    public void render(Part mailPart) throws MessagingException {
        // ����������Դ��helper����, �Ա�ģ�����resource������.
        inlineResources.clear();

        // ��Ⱦģ��.
        String text = renderTemplate();

        // ����message part.
        // �������һ�����ϵ���Ƕ��Դ, ��ʹ��multipart/related����, ����ʹ��text/html����.
        try {
            if (inlineResources.isEmpty()) {
                renderHTMLContent(mailPart, text);
            } else {
                MimeMultipart multipartRelated = new MimeMultipart(CONTENT_TYPE_MULTIPART_SUBTYPE_RELATED);
                MimeBodyPart bodyPart = new MimeBodyPart();

                renderHTMLContent(bodyPart, text);
                multipartRelated.addBodyPart(bodyPart);

                // ȡ��������Ƕ����Դ
                Set<String> fileNames = createHashSet();

                for (InlineResource inlineResource : inlineResources.values()) {
                    renderInlineResource(multipartRelated, inlineResource, fileNames);
                }

                mailPart.setContent(multipartRelated);
            }
        } finally {
            inlineResources.clear();
        }
    }

    /**
     * ��ȾHTML���ݡ�
     */
    private void renderHTMLContent(Part mailPart, String text) throws MessagingException {
        String contentType = getContentType();
        ContentType contentTypeObject = MailUtil.getContentType(contentType, getMailBuilder().getCharacterEncoding());

        mailPart.setContent(text, contentTypeObject.toString());
        mailPart.setHeader(CONTENT_TRANSFER_ENCODING, DEFAULT_TRANSFER_ENCODING);
    }

    private void renderInlineResource(Multipart multipart, InlineResource inlineResource, Set<String> fileNames)
            throws MessagingException {
        assertNotNull(resourceLoader, "no resourceLoader");

        String resourceName = inlineResource.getResourceName();
        Resource resource = resourceLoader.getResource(resourceName);

        if (!resource.exists()) {
            throw new MailBuilderException("Could not find resource \"" + resourceName + "\"");
        }

        DataSource ds;

        try {
            ds = new URLDataSource(resource.getURL());
        } catch (IOException e) {
            ds = new ResourceDataSource(resource);
        }

        MimeBodyPart bodyPart = new MimeBodyPart();

        bodyPart.setDataHandler(new DataHandler(ds));
        bodyPart.setHeader(CONTENT_ID, "<" + inlineResource.getContentId() + ">");
        bodyPart.setFileName(inlineResource.getUniqueFilename(fileNames));
        bodyPart.setDisposition("inline");

        multipart.addBodyPart(bodyPart);
    }

    /**
     * ��װtemplateContext�е����ݡ�
     */
    @Override
    protected void populateTemplateContext(TemplateContext templateContext) {
        for (Map.Entry<String, String> entry : inlineResourceMap.entrySet()) {
            String key = entry.getKey();
            String prefix = entry.getValue();

            templateContext.put(key, new InlineResourceHelper(prefix));
        }
    }

    /**
     * ��ȸ���һ��content��
     */
    @Override
    protected void copyTo(AbstractContent copy) {
        super.copyTo(copy);

        HTMLTemplateContent copyContent = (HTMLTemplateContent) copy;

        copyContent.resourceLoader = resourceLoader;
        copyContent.inlineResourceMap.clear();
        copyContent.inlineResourceMap.putAll(inlineResourceMap);
        copyContent.inlineResources.clear();
    }

    @Override
    protected HTMLTemplateContent newInstance() {
        return new HTMLTemplateContent();
    }

    @Override
    protected String getDefaultContentType() {
        return CONTENT_TYPE_TEXT_HTML;
    }

    @Override
    protected void toString(MapBuilder mb) {
        super.toString(mb);
        mb.append("inlineResources", inlineResourceMap);
    }

    /**
     * ��¼��ģ����ʹ�õ�������������Դ����Ϣ��
     */
    private static class InlineResource {
        private static MessageFormat formatter = new MessageFormat("{0,time,yyyyMMdd.HHmmss}.{1}@{2}");
        private static int count = 0;
        private static String hostname = SystemUtil.getHostInfo().getName();
        private String resourceName;
        private String contentId;
        private String filename;

        public InlineResource(String resourceName) {
            this.resourceName = resourceName;

            synchronized (getClass()) {
                count = (count + 1) % (2 << 20);
                this.contentId = formatter.format(new Object[] { new Date(), String.valueOf(count), hostname });
            }

            this.filename = getFileName(resourceName);
        }

        private static String getFileName(String name) {
            if (name.endsWith("/")) {
                name = name.substring(0, name.length() - 1);
            }

            return name.substring(name.lastIndexOf("/") + 1);
        }

        public String getResourceName() {
            return resourceName;
        }

        public String getContentId() {
            return contentId;
        }

        /**
         * ȡ��Ψһ���ļ�����
         */
        public String getUniqueFilename(Set<String> fileNames) {
            String name = filename;
            int dotIndex = filename.lastIndexOf(".");

            for (int i = 1; fileNames.contains(name); i++) {
                if (dotIndex >= 0) {
                    name = filename.substring(0, dotIndex) + i + filename.substring(dotIndex);
                } else {
                    name = filename + i;
                }
            }

            fileNames.add(name);

            return name;
        }
    }

    /**
     * ��ģ����Ƕ�����õ���Դ�ĸ����ࡣ
     */
    public class InlineResourceHelper {
        private String prefix;

        public InlineResourceHelper(String prefix) {
            this.prefix = FileUtil.normalizeAbsolutePath(prefix + "/");
        }

        public String getURI(String path) {
            String resourceName = FileUtil.normalizeAbsolutePath(prefix + path);
            InlineResource inlineResource = inlineResources.get(resourceName);

            if (inlineResource == null) {
                inlineResource = new InlineResource(resourceName);
                inlineResources.put(resourceName, inlineResource);
            }

            return "cid:" + inlineResource.getContentId();
        }
    }
}
