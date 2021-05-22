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

import static com.alibaba.citrus.util.Assert.*;

import com.alibaba.citrus.service.mail.MailService;
import com.alibaba.citrus.service.mail.builder.MailBuilder;
import com.alibaba.citrus.service.mail.builder.MailContent;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * һ��<code>MailContent</code>�Ļ��ࡣ
 * 
 * @author Michael Zhou
 */
public abstract class AbstractContent implements MailContent {
    private String id;
    private MailBuilder builder;
    private MailContent parentContent;

    /**
     * ȡ��content��ΨһID����ID������mail builder��������content����Ψһ�ġ�
     */
    public String getId() {
        return id;
    }

    /**
     * ����content��ΨһID����ID������mail builder��������content����Ψһ�ġ�
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * ȡ�ô�content������mail builder�����粻���ڣ�����<code>IllegalArgumentException</code>
     * �쳣��
     */
    public MailBuilder getMailBuilder() {
        return getMailBuilder(true);
    }

    /**
     * ȡ�ô�content������mail builder��
     */
    protected final MailBuilder getMailBuilder(boolean required) {
        if (builder != null) {
            return builder;
        }

        if (parentContent != null) {
            return parentContent.getMailBuilder();
        }

        if (required) {
            throw new IllegalArgumentException("no mailBuilder");
        }

        return null;
    }

    /**
     * ����mail builder��
     */
    public void setMailBuilder(MailBuilder builder) {
        this.builder = builder;
    }

    /**
     * ȡ�ð��ݴ����ݵĸ����ݡ�
     */
    public MailContent getParentContent() {
        return parentContent;
    }

    /**
     * ���ð��ݴ����ݵĸ����ݡ�
     */
    public void setParentContent(MailContent parentContent) {
        this.parentContent = parentContent;
    }

    /**
     * ��ȸ���һ��content��
     */
    @Override
    public final AbstractContent clone() {
        String className = getClass().getSimpleName();

        // new instance
        AbstractContent copy = assertNotNull(newInstance(), "%s.newInstance() returned null", className);
        assertTrue(copy.getClass().equals(getClass()), "%s.newInstance() returned an object of wrong class", className);

        // copy to new instance
        copyTo(copy);
        copy.id = id;
        return copy;
    }

    /**
     * ����һ��ͬ���͵�content��
     */
    protected abstract AbstractContent newInstance();

    /**
     * ��ȸ���һ��content��
     */
    protected void copyTo(AbstractContent copy) {
    }

    /**
     * ����ȡ��ָ�����͵�service�ĸ�������������<code>defaultInstance</code>�ǿգ���ֱ�ӷ���֮���������
     * <code>getMailBuilder().getMailService().getService()</code>��
     */
    protected final <T> T getService(Class<T> serviceType, String defaultId, T defaultInstance) {
        if (defaultInstance != null) {
            return defaultInstance;
        }

        MailBuilder builder = getMailBuilder(false);

        if (builder != null) {
            MailService mailService = builder.getMailService();

            if (mailService != null) {
                return mailService.getService(serviceType, defaultId);
            }
        }

        return null;
    }

    @Override
    public final String toString() {
        ToStringBuilder buf = new ToStringBuilder();

        buf.append(getClass().getSimpleName());
        toString(buf);

        return buf.toString();
    }

    protected void toString(ToStringBuilder buf) {
        MapBuilder mb = new MapBuilder();
        toString(mb);
        buf.append(mb);
    }

    protected void toString(MapBuilder mb) {
    }
}
