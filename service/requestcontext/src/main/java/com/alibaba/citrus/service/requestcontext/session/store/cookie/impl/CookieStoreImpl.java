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
package com.alibaba.citrus.service.requestcontext.session.store.cookie.impl;

import static com.alibaba.citrus.util.ArrayUtil.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.citrus.service.requestcontext.session.encoder.SessionEncoder;
import com.alibaba.citrus.service.requestcontext.session.encoder.impl.SerializationEncoder;
import com.alibaba.citrus.service.requestcontext.session.store.SessionStoreException;
import com.alibaba.citrus.service.requestcontext.session.store.cookie.AbstractCookieStore;
import com.alibaba.citrus.util.ObjectUtil;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ��Session״̬������cookie�С�
 * <ul>
 * <li>��session������<code>SessionEncoder</code>������ַ�����</li>
 * <li>���ַ������ݷֶα�����cookie�У�<code>cookieName0</code>��<code>cookieName1</code>����
 * <code>cookieNameN</code>��</li>
 * <li>��ѡ����checksum cookie��<code>cookieNamesum</code>��
 * </ul>
 * 
 * @author Michael Zhou
 */
public class CookieStoreImpl extends AbstractCookieStore {
    private static final String NAME_PATTERN_SUFFIX = "(\\d+)";
    private static final Integer MAX_LENGTH_DEFAULT = (int) (4096 - 200);
    private static final Integer MAX_COUNT_DEFAULT = 5;
    private static final Boolean CHECKSUM_DEFAULT = false;
    private static final String CHECKSUM_SEPARATOR = "|";
    private static final int CHECKSUM_LENGTH = 15;
    private Pattern namePattern;
    private Integer maxLength;
    private Integer maxCount;
    private Boolean checksum;
    private String checksumName;
    private SessionEncoder[] encoders;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void setChecksum(boolean checksum) {
        this.checksum = checksum;
    }

    public void setEncoders(SessionEncoder[] encoders) {
        this.encoders = encoders;
    }

    @Override
    protected void init() {
        // ����cookie���ƣ�ȡ��cookie���Ƶ�������ʽ
        namePattern = Pattern.compile(getName() + NAME_PATTERN_SUFFIX);

        // ȡ��cookie���Ⱥ͸���������
        maxLength = defaultIfNull(maxLength, MAX_LENGTH_DEFAULT);
        maxCount = defaultIfNull(maxCount, MAX_COUNT_DEFAULT);

        // ȡ��cookie checksum������
        checksum = defaultIfNull(checksum, CHECKSUM_DEFAULT);
        checksumName = getName() + "sum";

        // ȡ��cookie encoder
        if (isEmptyArray(encoders)) {
            encoders = new SessionEncoder[] { createDefaultSessionEncoder() };
        }
    }

    protected SessionEncoder createDefaultSessionEncoder() {
        SerializationEncoder encoder = new SerializationEncoder();

        try {
            encoder.afterPropertiesSet();
        } catch (Exception e) {
            throw new SessionStoreException("Could not create default session encoder", e);
        }

        return encoder;
    }

    public Iterable<String> getAttributeNames(String sessionID, StoreContext storeContext) {
        State state = getState(storeContext);
        return state.attributes.keySet();
    }

    public Object loadAttribute(String attrName, String sessionID, StoreContext storeContext) {
        State state = getState(storeContext);
        return state.attributes.get(attrName);
    }

    public void invaldiate(String sessionID, StoreContext storeContext) {
        State state = getState(storeContext);

        if (!isSurvivesInInvalidating()) {
            state.attributes.clear();
        }
    }

    public void commit(Map<String, Object> attrs, String sessionID, StoreContext storeContext) {
        State state = getState(storeContext);

        if (state.cookieCommitted) {
            return;
        }

        state.cookieCommitted = true;

        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();

            if (attrValue == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Remove from session: {}", attrName);
                }

                state.attributes.remove(attrName);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Set to session: {} = {}", attrName, attrValue);
                }

                state.attributes.put(attrName, attrValue);
            }
        }

        if (log.isDebugEnabled()) {
            for (String attrName : state.attributes.keySet()) {
                if (!attrs.containsKey(attrName)) {
                    Object attrValue = state.attributes.get(attrName);
                    log.debug("Left unchanged attribute: {} = {}", attrName, attrValue);
                }
            }
        }

        String cookieState = null;

        if (!state.attributes.isEmpty()) {
            try {
                cookieState = encoders[0].encode(state.attributes, storeContext);
            } catch (Exception e) {
                log.warn("Failed to encode session state", e);
            }
        }

        cookieState = trimToEmpty(cookieState);

        if (cookieState.length() > maxLength * maxCount) {
            log.warn("Cookie store full! length {} exceeds the max length: {} * {}.\n"
                    + " All session attributes will be LOST!",
                    new Object[] { cookieState.length(), maxLength, maxCount });
        } else {
            StringBuilder checksumBuf = null;

            if (checksum) {
                checksumBuf = new StringBuilder();
            }

            for (int beginOffset = 0, i = 0; beginOffset < cookieState.length(); beginOffset += maxLength, i++) {
                int endOffset = Math.min(beginOffset + maxLength, cookieState.length());
                String cookieNameWithIndex = getName() + i;
                String cookieValue = cookieState.substring(beginOffset, endOffset);

                writeCookie(storeContext.getSessionRequestContext().getResponse(), cookieNameWithIndex, cookieValue);
                state.requestCookies.remove(cookieNameWithIndex);

                // ����cookie checksum
                if (checksumBuf != null) {
                    if (checksumBuf.length() > 0) {
                        checksumBuf.append(CHECKSUM_SEPARATOR);
                    }

                    checksumBuf.append(StringUtil.substring(cookieValue, 0, CHECKSUM_LENGTH));
                }
            }

            for (String cookieName : state.requestCookies.keySet()) {
                writeCookie(storeContext.getSessionRequestContext().getResponse(), cookieName, null);
            }

            // ���request�а���cookie checksum�����Ҵ˴�cookieΪ�գ������checksum
            // �������cookie checksum���򿪣�������checksum��д��cookie
            if (checksumBuf == null || checksumBuf.length() <= 0) {
                if (state.hasChecksum) {
                    writeCookie(storeContext.getSessionRequestContext().getResponse(), checksumName, null);
                }
            } else {
                writeCookie(storeContext.getSessionRequestContext().getResponse(), checksumName, checksumBuf.toString());
            }
        }
    }

    /**
     * ȡ��cookie store��״̬��
     */
    private State getState(StoreContext storeContext) {
        State state = (State) storeContext.getState();

        if (state == null) {
            state = new State();
            storeContext.setState(state);
        }

        ensureCookieLoading(state, storeContext.getSessionRequestContext().getRequest(), storeContext);
        return state;
    }

    /**
     * ȷ��cookie��װ�ء�
     */
    private void ensureCookieLoading(State state, HttpServletRequest request, StoreContext storeContext) {
        if (state.cookieLoaded) {
            return;
        }

        state.cookieLoaded = true;

        // ��ȡcookies
        CookiesInfo cookiesInfo = readCookies(request);

        state.requestCookies = cookiesInfo.requestCookies;
        state.hasChecksum = cookiesInfo.hasChecksum;

        // ��֤cookies
        state.checksumValid = validateCookies(cookiesInfo.cookieList, cookiesInfo.checksumList);

        // �ϲ�cookies��trimToNull
        state.mergedCookieValue = mergeCookies(cookiesInfo.cookieList, cookiesInfo.checksumList);

        // ����ʹ������encoders�����Ŷ�cookieState���룬���ʧ�ܣ��򷵻ؿձ�
        state.attributes = decodeCookieValue(state.mergedCookieValue, storeContext);
    }

    /**
     * ��ȡcookies��
     */
    private CookiesInfo readCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            cookies = new Cookie[0];
        }

        CookiesInfo cookiesInfo = new CookiesInfo();

        cookiesInfo.requestCookies = createHashMap();
        cookiesInfo.cookieList = createArrayList(cookies.length);
        cookiesInfo.hasChecksum = false;
        cookiesInfo.checksumList = null;

        // ɨ��cookie��
        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName();

            if (isEquals(checksumName, cookieName)) {
                cookiesInfo.hasChecksum = true;
                cookiesInfo.checksumList = StringUtil.split(cookie.getValue(), CHECKSUM_SEPARATOR + " ");
            } else {
                Matcher matcher = namePattern.matcher(cookieName);

                if (matcher.matches()) {
                    int index = Integer.parseInt(matcher.group(1));
                    String cookieValue = trimToNull(cookie.getValue());
                    CookieInfo cookieInfo = new CookieInfo(index, cookieName, cookieValue);

                    cookiesInfo.cookieList.add(cookieInfo);
                    cookiesInfo.requestCookies.put(cookieName, cookieInfo);
                }
            }
        }

        // ����cookie��
        Collections.sort(cookiesInfo.cookieList);

        if (log.isDebugEnabled()) {
            ToStringBuilder buf = new ToStringBuilder();

            buf.format("[%s] Loading cookies: %d cookies found", getStoreName(), cookiesInfo.cookieList.size());

            if (isEmptyArray(cookiesInfo.checksumList)) {
                if (checksum) {
                    buf.append("\n No checksum cookie");
                }
            } else {
                buf.format("\n %s[expected cookies=%d]=%s", checksumName, cookiesInfo.checksumList.length,
                        ObjectUtil.toString(cookiesInfo.checksumList));
            }

            for (CookieInfo cookieInfo : cookiesInfo.cookieList) {
                int length = 0;

                if (cookieInfo.value != null) {
                    length = cookieInfo.value.length();
                }

                buf.format("\n %s[length=%d]=%s", cookieInfo.name, length, cookieInfo.value);
            }

            log.debug(buf.toString());
        }

        return cookiesInfo;
    }

    /**
     * ���cookies��
     */
    private boolean validateCookies(List<CookieInfo> cookieList, String[] checksumList) {
        int checksumListSize = 0;
        int index = 0;

        if (checksumList != null) {
            checksumListSize = checksumList.length;
        }

        for (CookieInfo cookieInfo : cookieList) {
            if (cookieInfo.index != index || cookieInfo.value == null) {
                break; // cookie�е���ű��жϣ����˳�����Ϊ���ݴ���Ȼ�����Ϸ���cookies��ֻ����ǰ�������Ĳ��֡�
            }

            if (index < checksumListSize) {
                if (!cookieInfo.value.startsWith(checksumList[index])) {
                    log.warn("{} does not match the checksum.  "
                            + "Expected prefix: {}[length={}], actually: {}[length={}]", new Object[] {
                            cookieInfo.name, checksumList[index], checksumList[index].length(), cookieInfo.value,
                            cookieInfo.value.length() });

                    return false;
                }
            }

            index++;
        }

        if (checksumList != null && index != checksumListSize) {
            log.warn("Number of cookies {}* does not match checksum.  Expected cookies: {}" + ", actually: {}",
                    new Object[] { getName(), checksumListSize, index });

            return false;
        }

        return true;
    }

    /**
     * ���ͺϲ�cookies��
     */
    private String mergeCookies(List<CookieInfo> cookieList, String[] checksumList) {
        StringBuilder buf = new StringBuilder();
        int index = 0;

        for (CookieInfo cookieInfo : cookieList) {
            if (cookieInfo.index != index || cookieInfo.value == null) {
                break; // cookie�е���ű��жϣ����˳�����Ϊ���ݴ���Ȼ�����Ϸ���cookies��ֻ����ǰ�������Ĳ��֡�
            }

            buf.append(cookieInfo.value);
            index++;
        }

        return trimToNull(buf.toString());
    }

    private Map<String, Object> decodeCookieValue(String cookieValue, StoreContext storeContext) {
        Map<String, Object> attrs = null;

        if (cookieValue == null) {
            return createHashMap();
        }

        List<Exception> encoderExceptions = null;

        for (SessionEncoder encoder : encoders) {
            try {
                attrs = encoder.decode(cookieValue, storeContext);
                log.debug("Succeeded decoding cookieValues using {}", encoder);
                break;
            } catch (Exception e) {
                if (encoderExceptions == null) {
                    encoderExceptions = createLinkedList();
                }

                encoderExceptions.add(e);
                log.trace("Failure decoding cookieValues using {}: {}", encoder, e.getMessage());
            }
        }

        if (attrs == null) {
            attrs = createHashMap();
        }

        // ���ʧ�ܣ���¼��־
        if (attrs.isEmpty() && encoderExceptions != null) {
            if (log.isWarnEnabled()) {
                ToStringBuilder buf = new ToStringBuilder();

                buf.append("Failed to decode cookie value: ").append(cookieValue);

                int i = 0;
                for (Exception e : encoderExceptions) {
                    buf.format("\n  Encoder #%d - %s threw %s", (i + 1), encoders[i].getClass().getSimpleName(), e);
                }

                log.warn(buf.toString());
            }
        } else {
            if (log.isDebugEnabled()) {
                int attrCount = attrs.size();
                ToStringBuilder buf = new ToStringBuilder();

                buf.format("Found %d attributes:", attrCount);

                if (!attrs.isEmpty()) {
                    buf.append(new MapBuilder().setPrintCount(true).setSortKeys(true).appendAll(attrs));
                }

                log.debug(buf.toString());
            }
        }

        return attrs;
    }

    @Override
    protected void toString(MapBuilder mb) {
        super.toString(mb);

        mb.append("maxLength", maxLength);
        mb.append("maxCount", maxCount);
        mb.append("checksum", checksum);
        mb.append("encoders", encoders);
    }

    /**
     * ������������cookies����Ϣ��
     */
    private static class CookiesInfo {
        private Map<String, CookieInfo> requestCookies;
        private List<CookieInfo> cookieList;
        private String[] checksumList;
        private boolean hasChecksum;
    }

    /**
     * ���cookie��״̬��
     */
    private class State {
        private boolean cookieLoaded;
        private boolean cookieCommitted;
        private boolean hasChecksum;
        private Map<String, CookieInfo> requestCookies;
        private String mergedCookieValue;
        private Map<String, Object> attributes;

        @SuppressWarnings("unused")
        private boolean checksumValid; // used by testcase
    }

    /**
     * ����һ��cookie����Ϣ��
     */
    private static class CookieInfo implements Comparable<CookieInfo> {
        public final int index;
        public final String name;
        public final String value;

        public CookieInfo(int index, String name, String value) {
            this.index = index;
            this.name = name;
            this.value = value;
        }

        public int compareTo(CookieInfo o) {
            return index - o.index;
        }
    }
}
