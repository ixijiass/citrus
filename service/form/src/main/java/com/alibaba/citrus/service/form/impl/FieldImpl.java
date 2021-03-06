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
package com.alibaba.citrus.service.form.impl;

import static com.alibaba.citrus.service.form.FormConstant.*;
import static com.alibaba.citrus.service.requestcontext.util.RequestContextUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.form.CustomErrorNotFoundException;
import com.alibaba.citrus.service.form.Field;
import com.alibaba.citrus.service.form.Group;
import com.alibaba.citrus.service.form.MessageContext;
import com.alibaba.citrus.service.form.Validator;
import com.alibaba.citrus.service.form.Validator.Context;
import com.alibaba.citrus.service.form.configuration.FieldConfig;
import com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext;
import com.alibaba.citrus.service.requestcontext.support.ValueListSupport;
import com.alibaba.citrus.util.ArrayUtil;
import com.alibaba.citrus.util.ObjectUtil;
import com.alibaba.citrus.util.StringEscapeUtil;
import com.alibaba.citrus.util.io.ByteArrayInputStream;
import com.alibaba.citrus.util.io.ByteArrayOutputStream;

/**
 * ??????????????????????????field??
 * <p>
 * ??????field??????????????????????????????????????
 * </p>
 * 
 * @author Michael Zhou
 */
public class FieldImpl extends ValueListSupport implements Field {
    private final static Logger log = LoggerFactory.getLogger(Field.class);
    private final FieldConfig fieldConfig;
    private final Group group;
    private final String fieldKey;
    private final MessageContext messageContext;
    private boolean valid;
    private String message;
    private Attachment attachment;

    /**
     * ??????????field??
     */
    public FieldImpl(FieldConfig fieldConfig, Group group) {
        super( //
                assertNotNull(group, "group").getForm().getTypeConverter(), // converter
                fieldConfig.getGroupConfig().getFormConfig().isConverterQuiet() // converter quiet
        );
        this.fieldConfig = assertNotNull(fieldConfig, "fieldConfig");
        this.group = group;
        this.fieldKey = group.getKey() + FIELD_KEY_SEPARATOR + fieldConfig.getKey();
        this.messageContext = MessageContextFactory.newInstance(this);
    }

    /**
     * ????field????????????
     */
    public FieldConfig getFieldConfig() {
        return fieldConfig;
    }

    /**
     * ??????????field??group??
     */
    public Group getGroup() {
        return group;
    }

    /**
     * ????field??????????????
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * ??????form????????????field??key??
     * <p>
     * ??????????<code>"_fm"</code>??????group??????????????group instance
     * fieldKey????????field????????????????????<code>_fm.m._0.n</code>??
     * </p>
     */
    public String getKey() {
        return fieldKey;
    }

    /**
     * ??????form????????????field??key????????????????????????????field????????????????key??????????field??????
     * <p>
     * ??????checkbox??????HTML??????????????
     * </p>
     * <p>
     * Key??????????<code>_fm.groupKey.instanceKey.fieldKey.absent</code>??
     * </p>
     */
    public String getAbsentKey() {
        return getKey() + FORM_FIELD_ABSENT_KEY;
    }

    /**
     * ??????form????????field????????????key??
     * <p>
     * Key??????????<code>_fm.groupKey.instanceKey.fieldKey.attach</code>??
     * </p>
     */
    public String getAttachmentKey() {
        return getKey() + FORM_FIELD_ATTACHMENT_KEY;
    }

    /**
     * ??????????????
     */
    public String getMessage() {
        return message;
    }

    /**
     * ????????????????????<code>isValid()</code>??<code>false</code>??
     * <p>
     * ????<code>isValid()</code>??????<code>false</code>??????????????????????????????????????????
     * </p>
     * <p>
     * id??????????????ID????????????form????????????
     * </p>
     */
    public void setMessage(String id) {
        setMessage(id, null);
    }

    /**
     * ????????????????????<code>isValid()</code>??<code>false</code>??
     * <p>
     * ????<code>isValid()</code>??????<code>false</code>??????????????????????????????????????????
     * </p>
     * <p>
     * id??????????????ID????????????form????????????params??????????????????????????
     * </p>
     */
    public void setMessage(String id, Map<String, ?> params) {
        if (isValid()) {
            boolean found = false;

            for (Validator validator : getFieldConfig().getValidators()) {
                if (isEquals(validator.getId(), id)) {
                    MessageContext expressionContext = MessageContextFactory.newInstance(this, validator);
                    expressionContext.putAll(params);
                    valid = false;
                    found = true;
                    message = validator.getMessage(new ValidatorContextImpl(expressionContext, this));

                    if (message == null) {
                        throw new CustomErrorNotFoundException("No message specified for error ID \"" + id + "\" in "
                                + this);
                    }

                    break;
                }
            }

            if (found) {
                ((GroupImpl) getGroup()).setValid(valid);
            } else {
                throw new CustomErrorNotFoundException("Specified error ID \"" + id + "\" was not found in " + this);
            }
        }
    }

    /**
     * ??????field????????????????????????????<code>request</code>??????<code>null</code>??
     */
    public void init(HttpServletRequest request) {
        valid = true;
        attachment = null;

        // request??null????????????????????????????????????????????????
        if (request == null) {
            setValues(getFieldConfig().getDefaultValues());
        } else {
            ParserRequestContext prc = findRequestContext(request, ParserRequestContext.class);

            // ??????????ParserRequestContext????????objects??????????FileItem??????????????????????
            if (prc != null) {
                setValues(prc.getParameters().getObjects(getKey()));
            } else {
                setValues(request.getParameterValues(getKey()));
            }

            // ????field??????????????absent fieldKey??
            if (size() == 0) {
                setValues(request.getParameterValues(getAbsentKey()));
            }

            // ????????attachment??????????
            String attachmentEncoded = trimToNull(request.getParameter(getAttachmentKey()));

            if (attachmentEncoded != null) {
                attachment = new Attachment(attachmentEncoded);
            }
        }
    }

    /**
     * ????????????????????????
     */
    protected void validate() {
        valid = true;

        for (Validator validator : getFieldConfig().getValidators()) {
            MessageContext expressionContext = MessageContextFactory.newInstance(this, validator);
            Context context = new ValidatorContextImpl(expressionContext, this);
            boolean passed = validator.validate(context);

            if (!passed) {
                valid = false;
                message = validator.getMessage(context);
                break;
            }
        }

        ((GroupImpl) getGroup()).setValid(valid);
    }

    /**
     * ????field??????????????????????context??
     */
    protected MessageContext getMessageContext() {
        return messageContext;
    }

    /**
     * ????field name????????<code>getFieldConfig().getName()</code>??
     */
    public String getName() {
        return getFieldConfig().getName();
    }

    /**
     * ????????????????????????????????????????????<code>""</code>??
     */
    @Override
    public String getStringValue() {
        return getStringValue(EMPTY_STRING);
    }

    /**
     * ????????????field??????????????<code>getFieldConfig().getDisplayName()</code>??
     */
    public String getDisplayName() {
        return getFieldConfig().getDisplayName();
    }

    /**
     * ??????????????????<code>getFieldConfig().getDefaultValue()</code>??
     */
    public String getDefaultValue() {
        return getFieldConfig().getDefaultValue();
    }

    /**
     * ??????????????????<code>getFieldConfig().getDefaultValues()</code>??
     */
    public String[] getDefaultValues() {
        return getFieldConfig().getDefaultValues();
    }

    /**
     * ??????????/????????
     */
    @Override
    public void addValue(Object value) {
        if (getFieldConfig().isTrimming() && value instanceof String) {
            value = trimToNull((String) value);
        }

        super.addValue(value);
    }

    /**
     * ??????????
     */
    public Object getAttachment() {
        return attachment == null ? null : attachment.getAttachment();
    }

    /**
     * ??????????????????
     */
    public String getAttachmentEncoded() {
        return attachment == null ? null : attachment.getAttachmentEncoded();
    }

    /**
     * ??????????????
     */
    public boolean hasAttachment() {
        return attachment != null && attachment.getAttachment() != null;
    }

    /**
     * ??????????
     * <p>
     * ????????attachment????????????????????????????????????????????????<code>clearAttachment()</code>??
     * </p>
     */
    public void setAttachment(Object attachment) {
        if (this.attachment == null) {
            this.attachment = new Attachment(attachment);
        }
    }

    /**
     * ??????????
     */
    public void clearAttachment() {
        this.attachment = null;
    }

    /**
     * ????????????????????????
     */
    @Override
    public String toString() {
        return "Field[group: " + getGroup().getGroupConfig().getName() + "." + getGroup().getInstanceKey() + ", name: "
                + getFieldConfig().getName() + ", values: " + ObjectUtil.toString(getValues()) + ", valid: "
                + isValid() + "]";
    }

    /**
     * ??????????????
     */
    private static class Attachment {
        private Object attachment;
        private String attachmentEncoded;

        public Attachment(Object attachment) {
            setAttachment(attachment);
        }

        public Attachment(String attachmentEncoded) {
            setAttachment(decode(attachmentEncoded));
        }

        public Object getAttachment() {
            return attachment;
        }

        public void setAttachment(Object attachment) {
            this.attachment = attachment;
            this.attachmentEncoded = null;
        }

        public String getAttachmentEncoded() {
            if (attachment != null && attachmentEncoded == null) {
                attachmentEncoded = encode(attachment);
            }

            return attachmentEncoded;
        }

        private String encode(Object attachment) {
            if (attachment == null) {
                return null;
            }

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                // 1. ??????
                // 2. ????
                Deflater def = new Deflater(Deflater.BEST_COMPRESSION, false);
                DeflaterOutputStream dos = new DeflaterOutputStream(baos, def);
                ObjectOutputStream oos = null;

                try {
                    oos = new ObjectOutputStream(dos);
                    oos.writeObject(attachment);
                } finally {
                    if (oos != null) {
                        try {
                            oos.close();
                        } catch (IOException e) {
                        }
                    }

                    def.end();
                }

                byte[] plaintext = baos.toByteArray().toByteArray();

                // 3. base64????
                return StringEscapeUtil.escapeURL(new String(Base64.encodeBase64(plaintext, false), "ISO-8859-1"));
            } catch (Exception e) {
                log.error("Failed to encode field attachment", e);
                return "!Failure: " + e;
            }
        }

        private Object decode(String attachmentEncoded) {
            if (attachmentEncoded == null || attachmentEncoded.startsWith("!Failure:")) {
                return null;
            }

            // 1. base64????
            byte[] plaintext = null;

            try {
                String encoded = StringEscapeUtil.unescapeURL(attachmentEncoded);
                plaintext = Base64.decodeBase64(encoded.getBytes("ISO-8859-1"));

                if (ArrayUtil.isEmptyArray(plaintext)) {
                    log.warn("Field attachment content is empty: " + encoded);
                    return null;
                }
            } catch (Exception e) {
                log.warn("Failed to decode field attachment: " + e);
                return null;
            }

            // 2. ??????
            ByteArrayInputStream bais = new ByteArrayInputStream(plaintext);
            Inflater inf = new Inflater(false);
            InflaterInputStream iis = new InflaterInputStream(bais, inf);

            // 3. ????????
            ObjectInputStream ois = null;

            try {
                ois = new ObjectInputStream(iis);
                return ois.readObject();
            } catch (Exception e) {
                log.warn("Failed to parse field attachment", e);
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                    }
                }

                inf.end();
            }

            return null;
        }
    }
}
