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
package com.alibaba.citrus.service.form.impl.validation;

import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.citrus.service.form.support.AbstractOptionalValidator;
import com.alibaba.citrus.service.form.support.AbstractValidatorDefinitionParser;

/**
 * �������ֵ�����ڸ�ʽ�Ƿ����ָ���ĸ�ʽ���Ƿ����趨�������ڡ�
 * �磺ָ�����ڸ�ʽDateFromatΪ"yyyy-MM-dd"��������������ֵ��ʽ�����ϣ��򲻺Ϸ���
 * ��ͬʱָ������СֵΪ"2000-12-31"�����ֵΪ"2005-12-31"������������ڲ��ڴ����� �ڣ��򲻺Ϸ���Ҳ����ָֻ�����е�ĳһ����
 * 
 * @author Michael Zhou
 */
public class DateValidator extends AbstractOptionalValidator {
    private final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private String dateFormatStr;
    private Date minDate;
    private String minDateStr;
    private Date maxDate;
    private String maxDateStr;

    /**
     * ȡ���ڸ�ʽ���μ�<code>SimpleDateFormat</code>��ע�͡�
     */
    public String getFormat() {
        return dateFormatStr;
    }

    /**
     * �������ڸ�ʽ���μ�<code>SimpleDateFormat</code>��ע�͡�
     */
    public void setFormat(String formatStr) {
        this.dateFormatStr = trimToNull(formatStr);
    }

    /**
     * ȡ��С���ڣ��ַ�����ʽ����ʽ����setDateFormat()���á�
     */
    public String getMinDate() {
        return minDateStr;
    }

    /**
     * ������С���ڣ��ַ�����ʽ����ʽ����setDateFormat()���á�
     */
    public void setMinDate(String minDate) {
        this.minDateStr = trimToNull(minDate);
    }

    /**
     * ȡ��С���ڣ��ַ�����ʽ����ʽ����setDateFormat()���á�
     */
    public String getMaxDate() {
        return maxDateStr;
    }

    /**
     * ����������ڣ��ַ�����ʽ����ʽ����setDateFormat()���á�
     */
    public void setMaxDate(String maxDate) {
        this.maxDateStr = trimToNull(maxDate);
    }

    private DateFormat getDateFormat() {
        return new SimpleDateFormat(dateFormatStr);
    }

    @Override
    protected void init() throws Exception {
        super.init();

        if (dateFormatStr == null) {
            dateFormatStr = DEFAULT_DATE_FORMAT;
        }

        DateFormat format = getDateFormat();

        if (minDateStr != null) {
            minDate = format.parse(minDateStr);
            minDateStr = format.format(minDate);
        }

        if (maxDateStr != null) {
            maxDate = format.parse(maxDateStr);
            maxDateStr = format.format(maxDate);
        }
    }

    /**
     * У����������ֵ�Ƿ����ָ���ĸ�ʽ���Ƿ����趨�������ڡ�
     * �磺ָ�����ڸ�ʽDateFromatΪ"yyyy-MM-dd"��������������ֵ��ʽ�����ϣ��򲻺Ϸ���
     * ��ͬʱָ������СֵΪ"2000-12-31"�����ֵΪ"2005-12-31"������������ڲ��ڴ����� �ڣ��򲻺Ϸ���Ҳ����ָֻ�����е�ĳһ��
     */
    @Override
    protected boolean validate(Context context, String value) {
        DateFormat format = getDateFormat();
        Date inputDate;

        try {
            inputDate = format.parse(value);
        } catch (ParseException e) {
            return false;
        }

        // ���ϸ�ؼ�飬���磺2007-02-29�Ǵ���ģ��������ϢҲ�Ǵ����
        String normInput = normalizeDateString(value);
        String normParsed = normalizeDateString(format.format(inputDate));

        if (!isEquals(normInput, normParsed)) {
            return false;
        } else {
            if (minDate != null && inputDate.before(minDate)) {
                return false;
            }

            if (maxDate != null && inputDate.after(maxDate)) {
                return false;
            }
        }

        return true;
    }

    private static final Pattern spaceDigitsPattern = Pattern.compile("(\\s+)|0*(\\d+)");

    /**
     * ������ǰ��0ȥ����������Ŀո�ת��һ���ո�trim��
     */
    static String normalizeDateString(String s) {
        if (s == null) {
            return null;
        }

        Matcher matcher = spaceDigitsPattern.matcher(s);
        StringBuffer buf = new StringBuffer(s.length());
        int index = 0;

        while (matcher.find()) {
            String spaces = matcher.group(1);
            String numbers = matcher.group(2);

            buf.append(s.substring(index, matcher.start()));

            if (!isEmpty(spaces)) {
                buf.append(" ");
            }

            if (!isEmpty(numbers)) {
                buf.append(numbers);
            }

            index = matcher.end();
        }

        buf.append(s.substring(index, s.length()));

        return trimToNull(buf.toString());
    }

    public static class DefinitionParser extends AbstractValidatorDefinitionParser<DateValidator> {
    }
}
