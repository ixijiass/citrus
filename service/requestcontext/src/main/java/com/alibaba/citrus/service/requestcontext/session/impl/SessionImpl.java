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
package com.alibaba.citrus.service.requestcontext.session.impl;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static java.util.Collections.*;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.citrus.service.requestcontext.session.SessionAttributeInterceptor;
import com.alibaba.citrus.service.requestcontext.session.SessionConfig;
import com.alibaba.citrus.service.requestcontext.session.SessionInterceptor;
import com.alibaba.citrus.service.requestcontext.session.SessionLifecycleListener;
import com.alibaba.citrus.service.requestcontext.session.SessionModel;
import com.alibaba.citrus.service.requestcontext.session.SessionModelEncoder;
import com.alibaba.citrus.service.requestcontext.session.SessionRequestContext;
import com.alibaba.citrus.service.requestcontext.session.SessionStore;
import com.alibaba.citrus.service.requestcontext.session.SessionStore.StoreContext;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.util.internal.ToStringBuilder.MapBuilder;

/**
 * ʵ����<code>HttpSession</code>�ӿڡ�
 * <p>
 * ע�⣬ÿ��request���ᴴ��������session���󣬴˶��������̰߳�ȫ�ģ����ܱ����߳�ͬʱ���ʡ�����󱸵�session
 * store���̰߳�ȫ�ġ�
 * </p>
 */
public class SessionImpl implements HttpSession {
    private final static Logger log = LoggerFactory.getLogger(SessionImpl.class);
    private final HttpSessionInternal sessionInternal = new HttpSessionInternal();
    private String sessionID;
    private SessionRequestContext requestContext;
    private String modelKey;
    private SessionModelImpl model;
    private boolean isNew;
    private Map<String, SessionAttribute> attrs = createHashMap();
    private Map<String, Object> storeStates = createHashMap();
    private boolean invalidated = false;
    private boolean cleared = false;

    /**
     * ����һ��session����
     */
    public SessionImpl(String sessionID, SessionRequestContext requestContext, boolean isNew, boolean create) {
        this.sessionID = assertNotNull(sessionID, "no sessionID");
        this.requestContext = requestContext;
        this.modelKey = requestContext.getSessionConfig().getModelKey();

        EventType event;

        // ���������session���������������
        // 1. Requested sessionIDΪ��
        // 2. Requested sessionID����Ӧ��session������
        // 3. Requested sessionID����Ӧ��session�Ѿ�����
        // 3.5 Requested sessionID��model�е�session ID��ƥ�䣬����session����
        // 4. Requested sessionID����Ӧ��session�����ҺϷ�
        if (isNew) {
            event = EventType.CREATED;

            // ���1�������µ�model��������֮��
            log.debug("No session ID was found in cookie or URL.  A new session will be created.");
            sessionInternal.invalidate();
        } else {
            model = (SessionModelImpl) sessionInternal.getAttribute(modelKey);

            if (model == null) {
                event = EventType.CREATED;

                // ���2�������µ�model��������֮��
                log.debug("Session state was not found for sessionID \"{}\".  A new session will be created.",
                        sessionID);
                isNew = true;
                sessionInternal.invalidate();
            } else {
                boolean expired = false;

                String modelSessionID = trimToNull(model.getSessionID());

                // ���SessionID��ֵ�Ƿ���ȣ�������SessionModel��û������SessionID�ĳ���
                // ���������model��δ����session ID����ʱ��������飬��request�е�sessionIDΪ׼
                if (modelSessionID != null && !modelSessionID.equals(sessionID)) {
                    // ���3.5 ��������
                    expired = true;

                    log.warn("Requested session ID \"{}\" does not match the ID in session model \"{}\".  "
                            + "Force expired the session.", sessionID, modelSessionID);
                }

                // Session model������ǰ�����ȱ�decode����ˣ��޸������ص�session model���󣬲�����Ӱ��store�еĶ���ֵ��
                model.setSession(this); // update the session config & session id in model

                expired |= model.isExpired();

                if (expired) {
                    event = EventType.RECREATED;

                    // ���3������model��ͬ�½���һ����ͬʱ��������ݡ�
                    if (log.isDebugEnabled()) {
                        log.debug(
                                "Session has expired: sessionID={}, created at {}, last accessed at {}, "
                                        + "maxInactiveInterval={}, forceExpirationPeriod={}",
                                new Object[] { modelSessionID, new Date(model.getCreationTime()),
                                        new Date(model.getLastAccessedTime()), model.getMaxInactiveInterval(),
                                        getSessionRequestContext().getSessionConfig().getForceExpirationPeriod() });
                    }

                    isNew = true;
                    sessionInternal.invalidate();
                } else {
                    event = EventType.VISITED;

                    // ���4������model���������ʱ�䡣
                    if (log.isTraceEnabled()) {
                        log.trace(
                                "Activate session: sessionID={}, last accessed at {}, maxInactiveInterval={}",
                                new Object[] { modelSessionID, new Date(model.getLastAccessedTime()),
                                        model.getMaxInactiveInterval() });
                    }

                    model.touch();
                }
            }
        }

        this.isNew = isNew;

        // ȷ��model attribute��modified=true
        sessionInternal.setAttribute(modelKey, model);

        // ����session lifecycle listener
        fireEvent(event);
    }

    /**
     * ȡ�ô�����session��request context��
     * 
     * @return request context
     */
    public SessionRequestContext getSessionRequestContext() {
        return requestContext;
    }

    /**
     * ȡ�õ�ǰ��model��
     * 
     * @return model����
     */
    public SessionModel getSessionModel() {
        return model;
    }

    /**
     * ȡ��session ID��
     * 
     * @return session ID
     */
    public String getId() {
        return sessionID;
    }

    /**
     * ȡ��session�Ĵ���ʱ�䡣
     * 
     * @return ����ʱ��¾
     * @throws IllegalStateException ���session�Ѿ�invalidated
     */
    public long getCreationTime() {
        assertValid("getCreationTime");
        return sessionInternal.getCreationTime();
    }

    /**
     * ȡ���������ʱ�䡣
     * 
     * @return �������ʱ��¾
     * @throws IllegalStateException ���session�Ѿ�invalidated
     */
    public long getLastAccessedTime() {
        assertValid("getLastAccessedTime");
        return model.getLastAccessedTime();
    }

    /**
     * ȡ��session����󲻻���ޣ�������ʱ�䣬session�ͻ�ʧЧ��
     * 
     * @return ������޵�����
     */
    public int getMaxInactiveInterval() {
        assertModel("getMaxInactiveInterval");
        return model.getMaxInactiveInterval();
    }

    /**
     * ����session����󲻻���ޣ�������ʱ�䣬session�ͻ�ʧЧ��
     * 
     * @param maxInactiveInterval ������޵�����
     */
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        assertModel("setMaxInactiveInterval");
        model.setMaxInactiveInterval(maxInactiveInterval);
    }

    /**
     * ȡ�õ�ǰsession������servlet context��
     * 
     * @return <code>ServletContext</code>����
     */
    public ServletContext getServletContext() {
        return requestContext.getServletContext();
    }

    /**
     * ȡ��ָ�����Ƶ�attributeֵ��
     * 
     * @param name attribute����
     * @return attribute��ֵ
     * @throws IllegalStateException ���session�Ѿ�invalidated
     */
    public Object getAttribute(String name) {
        assertValid("getAttribute");
        return sessionInternal.getAttribute(name);
    }

    /**
     * ȡ������attributes�����ơ�
     * 
     * @return attribute�����б�
     * @throws IllegalStateException ���session�Ѿ�invalidated
     */
    public Enumeration<String> getAttributeNames() {
        assertValid("getAttributeNames");

        Set<String> attrNames = getAttributeNameSet();

        final Iterator<String> i = attrNames.iterator();

        return new Enumeration<String>() {
            public boolean hasMoreElements() {
                return i.hasNext();
            }

            public String nextElement() {
                return i.next();
            }
        };
    }

    private Set<String> getAttributeNameSet() {
        SessionConfig sessionConfig = requestContext.getSessionConfig();
        String[] storeNames = sessionConfig.getStores().getStoreNames();
        Set<String> attrNames = createLinkedHashSet();

        for (String storeName : storeNames) {
            SessionStore store = sessionConfig.getStores().getStore(storeName);

            for (String attrName : store.getAttributeNames(getId(), new StoreContextImpl(storeName))) {
                if (!isEquals(attrName, modelKey)) {
                    attrNames.add(attrName);
                }
            }
        }

        for (SessionAttribute attr : attrs.values()) {
            if (attr.getValue() == null) {
                attrNames.remove(attr.getName());
            } else {
                attrNames.add(attr.getName());
            }
        }

        attrNames.remove(modelKey);

        return attrNames;
    }

    /**
     * ����ָ�����Ƶ�attributeֵ��
     * 
     * @param name attribute����
     * @param value attribute��ֵ
     * @throws IllegalStateException ���session�Ѿ�invalidated
     * @throws IllegalArgumentException ���ָ����attribute���Ʋ���֧��
     */
    public void setAttribute(String name, Object value) {
        assertValid("setAttribute");
        assertAttributeNameForModification("setAttribute", name);
        sessionInternal.setAttribute(name, value);
    }

    /**
     * ɾ��һ��attribute��
     * 
     * @param name Ҫɾ����attribute����
     * @throws IllegalStateException ���session�Ѿ�invalidated
     */
    public void removeAttribute(String name) {
        assertValid("removeAttribute");
        assertAttributeNameForModification("removeAttribute", name);
        setAttribute(name, null);
    }

    /**
     * ʹһ��session���ϡ�
     * 
     * @throws IllegalStateException ���session�Ѿ�invalidated
     */
    public void invalidate() {
        assertValid("invalidate");
        sessionInternal.invalidate();
        invalidated = true;

        fireEvent(EventType.INVALIDATED);
    }

    /**
     * ���һ��session��
     * 
     * @throws IllegalStateException ���session�Ѿ�invalidated
     */
    public void clear() {
        assertValid("clear");
        sessionInternal.invalidate();
    }

    /**
     * �жϵ�ǰsession�Ƿ�Ƿ���
     */
    public boolean isInvalidated() {
        return invalidated;
    }

    /**
     * ��ǰsession�Ƿ�Ϊ�µģ�
     * 
     * @return ������µģ��򷵻�<code>true</code>
     * @throws IllegalStateException ���session�Ѿ�invalidated
     */
    public boolean isNew() {
        assertValid("isNew");
        return isNew;
    }

    /**
     * ȷ��model�Ѿ���ȡ�ã���session�ѱ���ʼ����
     * 
     * @param methodName ��ǰ��Ҫִ�еķ���
     */
    protected void assertModel(String methodName) {
        if (model == null) {
            throw new IllegalStateException("Cannot call method " + methodName
                    + ": the session has not been initialized");
        }
    }

    /**
     * ȷ��session����valid״̬��
     * 
     * @param methodName ��ǰ��Ҫִ�еķ���
     */
    protected void assertValid(String methodName) {
        assertModel(methodName);

        if (invalidated) {
            throw new IllegalStateException("Cannot call method " + methodName
                    + ": the session has already invalidated");
        }
    }

    /**
     * ��齫Ҫ���ĵ�attr name�Ƿ�Ϸ���
     */
    protected void assertAttributeNameForModification(String methodName, String attrName) {
        if (modelKey.equals(attrName)) {
            throw new IllegalArgumentException("Cannot call method " + methodName + " with attribute " + attrName);
        }
    }

    /**
     * �ύsession�����ݣ�ɾ���ġ������ġ��޸ĵ����ݱ����档
     */
    public void commit() {
        String[] storeNames = requestContext.getSessionConfig().getStores().getStoreNames();
        Map<String, Object[]> stores = createHashMap();

        // ��store��attrs���зֶѡ�
        boolean modified = false;

        for (Map.Entry<String, SessionAttribute> entry : attrs.entrySet()) {
            String attrName = entry.getKey();
            SessionAttribute attr = entry.getValue();

            if (attr.isModified()) {
                String storeName = attr.getStoreName();
                SessionStore store = attr.getStore();
                Object[] storeInfo = stores.get(storeName);

                if (storeInfo == null) {
                    storeInfo = new Object[] { store, createHashMap() };
                    stores.put(storeName, storeInfo);
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> storeAttrs = (Map<String, Object>) storeInfo[1];
                Object attrValue = attr.getValue();

                // ���⴦��model������ת����store�е�ֵ��
                if (attrValue instanceof SessionModel) {
                    attrValue = requestContext.getSessionConfig().getSessionModelEncoders()[0]
                            .encode((SessionModel) attrValue);
                } else {
                    // ֻ����session model�����modified״̬
                    modified = true;
                }

                storeAttrs.put(attrName, attrValue);
            }
        }

        // �����û�в����ı䣨��û�е���setAttribute��removeAttribute����
        // Ҳû�б��������û�е���invalidate��clear��������isKeepInTouch=false��
        // ���ύ�ˣ�ֱ���˳���
        if (!modified && !cleared && !requestContext.getSessionConfig().isKeepInTouch()) {
            return;
        }

        // ��ÿһ��store�ֱ������
        for (Map.Entry<String, Object[]> entry : stores.entrySet()) {
            String storeName = entry.getKey();
            SessionStore store = (SessionStore) entry.getValue()[0];

            @SuppressWarnings("unchecked")
            Map<String, Object> storeAttrs = (Map<String, Object>) entry.getValue()[1];

            store.commit(storeAttrs, getId(), new StoreContextImpl(storeName));
        }

        // �����©��store�����ύ֮
        if (storeNames.length > stores.size()) {
            for (String storeName : storeNames) {
                if (!stores.containsKey(storeName)) {
                    SessionStore store = requestContext.getSessionConfig().getStores().getStore(storeName);
                    Map<String, Object> storeAttrs = emptyMap();

                    store.commit(storeAttrs, sessionID, new StoreContextImpl(storeName));
                }
            }
        }
    }

    /**
     * @deprecated no replacement
     */
    @Deprecated
    public javax.servlet.http.HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("No longer supported method: getSessionContext");
    }

    /**
     * @deprecated use getAttribute instead
     */
    @Deprecated
    public Object getValue(String name) {
        return getAttribute(name);
    }

    /**
     * @deprecated use getAttributeNames instead
     */
    @Deprecated
    public String[] getValueNames() {
        assertValid("getValueNames");

        Set<String> names = getAttributeNameSet();

        return names.toArray(new String[names.size()]);
    }

    /**
     * @deprecated use setAttribute instead
     */
    @Deprecated
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    /**
     * @deprecated use removeAttribute instead
     */
    @Deprecated
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public String toString() {
        MapBuilder mb = new MapBuilder();
        MapBuilder attrsBuilder = new MapBuilder().setPrintCount(true).setSortKeys(true);

        mb.append("sessionID", sessionID);
        mb.append("model", model);
        mb.append("isNew", isNew);
        mb.append("invalidated", invalidated);

        attrsBuilder.appendAll(attrs);
        attrsBuilder.remove(modelKey);

        mb.append("attrs", attrsBuilder);

        return new ToStringBuilder().append("HttpSession").append(mb).toString();
    }

    private void fireEvent(EventType event) {
        for (SessionInterceptor l : getSessionRequestContext().getSessionConfig().getSessionInterceptors()) {
            if (l instanceof SessionLifecycleListener) {
                SessionLifecycleListener listener = (SessionLifecycleListener) l;

                try {
                    switch (event) {
                        case RECREATED:
                            listener.sessionInvalidated(this);

                        case CREATED:
                            listener.sessionCreated(this);

                        case VISITED:
                            listener.sessionVisited(this);
                            break;

                        case INVALIDATED:
                            listener.sessionInvalidated(this);
                            break;

                        default:
                            unreachableCode();
                    }
                } catch (Exception e) {
                    // ������listener������Ӧ�õ��˳���
                    log.error("Listener \"" + listener.getClass().getSimpleName() + "\" failed", e);
                }
            }
        }
    }

    /**
     * Session�¼������͡�
     */
    private enum EventType {
        CREATED,
        RECREATED, // ��invalidateȻ����create
        INVALIDATED,
        VISITED
    }

    /**
     * ���session store��״̬��
     */
    private class StoreContextImpl implements StoreContext {
        private String storeName;

        public StoreContextImpl(String storeName) {
            this.storeName = storeName;
        }

        public Object getState() {
            return storeStates.get(storeName);
        }

        public void setState(Object stateObject) {
            if (stateObject == null) {
                storeStates.remove(storeName);
            } else {
                storeStates.put(storeName, stateObject);
            }
        }

        public StoreContext getStoreContext(String storeName) {
            return new StoreContextImpl(storeName);
        }

        public SessionRequestContext getSessionRequestContext() {
            return SessionImpl.this.getSessionRequestContext();
        }

        public HttpSession getHttpSession() {
            return sessionInternal;
        }
    }

    /**
     * �ڲ�ʹ�õ�session���󣬲����׳�<code>IllegalStateException</code>�쳣��
     */
    private class HttpSessionInternal implements HttpSession {
        public String getId() {
            return SessionImpl.this.getId();
        }

        public long getCreationTime() {
            return model == null ? 0 : model.getCreationTime();
        }

        public long getLastAccessedTime() {
            return SessionImpl.this.getLastAccessedTime();
        }

        public int getMaxInactiveInterval() {
            return SessionImpl.this.getMaxInactiveInterval();
        }

        public void setMaxInactiveInterval(int maxInactiveInterval) {
            SessionImpl.this.setMaxInactiveInterval(maxInactiveInterval);
        }

        public ServletContext getServletContext() {
            return SessionImpl.this.getServletContext();
        }

        public Object getAttribute(String name) {
            SessionAttribute attr = attrs.get(name);
            SessionConfig sessionConfig = requestContext.getSessionConfig();
            Object value;

            if (attr == null) {
                String storeName = sessionConfig.getStoreMappings().getStoreNameForAttribute(name);

                if (storeName == null) {
                    value = null;
                } else {
                    attr = new SessionAttribute(name, SessionImpl.this, storeName, new StoreContextImpl(storeName));
                    value = attr.getValue();

                    // ����session model����Ҫ�������
                    if (value != null && modelKey.equals(name)) {
                        value = decodeSessionModel(value); // �������ʧ�ܣ��򷵻�null
                        attr.updateValue(value);
                    }

                    // ֻ�е�value�ǿգ�store�а����˴˶��󣩣��Ű����ŵ�attrs���У�������ܻ�����ܶ�����attr����
                    if (value != null) {
                        attrs.put(name, attr);
                    }
                }
            } else {
                value = attr.getValue();
            }

            return interceptGet(name, value);
        }

        private Object interceptGet(String name, Object value) {
            for (SessionInterceptor l : getSessionRequestContext().getSessionConfig().getSessionInterceptors()) {
                if (l instanceof SessionAttributeInterceptor) {
                    SessionAttributeInterceptor interceptor = (SessionAttributeInterceptor) l;
                    value = interceptor.onRead(name, value);
                }
            }

            return value;
        }

        private Object decodeSessionModel(Object value) {
            SessionModel.Factory factory = new SessionModel.Factory() {
                public SessionModel newInstance(String sessionID, long creationTime, long lastAccessedTime,
                                                int maxInactiveInterval) {
                    return new SessionModelImpl(sessionID, creationTime, lastAccessedTime, maxInactiveInterval);
                }
            };

            SessionModel model = null;
            SessionModelEncoder[] encoders = requestContext.getSessionConfig().getSessionModelEncoders();

            for (SessionModelEncoder encoder : encoders) {
                model = encoder.decode(value, factory);

                if (model != null) {
                    break;
                }
            }

            if (model == null) {
                log.warn("Could not decode session model {} by {} encoders", value, encoders.length);
            }

            return model;
        }

        public Enumeration<String> getAttributeNames() {
            return SessionImpl.this.getAttributeNames();
        }

        public void setAttribute(String name, Object value) {
            value = interceptSet(name, value);

            SessionAttribute attr = attrs.get(name);
            SessionConfig sessionConfig = requestContext.getSessionConfig();

            if (attr == null) {
                String storeName = sessionConfig.getStoreMappings().getStoreNameForAttribute(name);

                if (storeName == null) {
                    throw new IllegalArgumentException("No storage configured for session attribute: " + name);
                } else {
                    attr = new SessionAttribute(name, SessionImpl.this, storeName, new StoreContextImpl(storeName));
                    attrs.put(name, attr);
                }
            }

            attr.setValue(value);
        }

        private Object interceptSet(String name, Object value) {
            for (SessionInterceptor l : getSessionRequestContext().getSessionConfig().getSessionInterceptors()) {
                if (l instanceof SessionAttributeInterceptor) {
                    SessionAttributeInterceptor interceptor = (SessionAttributeInterceptor) l;
                    value = interceptor.onWrite(name, value);
                }
            }

            return value;
        }

        public void removeAttribute(String name) {
            SessionImpl.this.removeAttribute(name);
        }

        public void invalidate() {
            // ���session����
            attrs.clear();
            cleared = true;

            // ֪ͨ���е�store����������
            SessionConfig sessionConfig = requestContext.getSessionConfig();
            String[] storeNames = sessionConfig.getStores().getStoreNames();

            for (String storeName : storeNames) {
                SessionStore store = sessionConfig.getStores().getStore(storeName);

                store.invaldiate(sessionID, new StoreContextImpl(storeName));
            }

            // ���model
            if (model == null) {
                model = new SessionModelImpl(SessionImpl.this);
            } else {
                model.reset();
            }
        }

        public boolean isNew() {
            return SessionImpl.this.isNew();
        }

        /**
         * @deprecated
         */
        @Deprecated
        public javax.servlet.http.HttpSessionContext getSessionContext() {
            return SessionImpl.this.getSessionContext();
        }

        /**
         * @deprecated
         */
        @Deprecated
        public Object getValue(String name) {
            return SessionImpl.this.getValue(name);
        }

        /**
         * @deprecated
         */
        @Deprecated
        public String[] getValueNames() {
            return SessionImpl.this.getValueNames();
        }

        /**
         * @deprecated
         */
        @Deprecated
        public void putValue(String name, Object value) {
            SessionImpl.this.putValue(name, value);
        }

        /**
         * @deprecated
         */
        @Deprecated
        public void removeValue(String name) {
            SessionImpl.this.removeValue(name);
        }
    }
}
