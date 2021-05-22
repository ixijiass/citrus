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
package com.alibaba.citrus.service.uribroker.uri;

import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.Assert.ExceptionType.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.citrus.service.template.Renderable;
import com.alibaba.citrus.service.uribroker.interceptor.URIBrokerInterceptor;
import com.alibaba.citrus.util.StringEscapeUtil;

/**
 * ����ཫURIBroker�У���URL�Ľṹû��ֱ�ӹ�ϵ��һЩ�������Է��������ʹ�����������
 * 
 * @author Michael Zhou
 */
public abstract class URIBrokerFeatures implements Renderable {
    protected final Renderer renderer = new Renderer();
    private boolean requestAware = true;
    private HttpServletRequest request;
    private URIBroker parent;
    private boolean initialized;
    private String charset;
    private boolean autoReset;
    private Map<URIBrokerInterceptor, Integer> interceptors;

    /**
     * ��������ʱ��Ϣ����Spring�Զ�װ�롣
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * �Ƿ��Զ���request�����ֵ��
     */
    public boolean isRequestAware() {
        return requestAware;
    }

    /**
     * �����Ƿ��Զ���request�����ֵ��
     */
    public void setRequestAware(boolean requestAware) {
        this.requestAware = requestAware;
    }

    /**
     * ȡ��parent URI broker��
     */
    public URIBroker getParent() {
        return parent;
    }

    /**
     * ����parent URI broker��
     * <p>
     * ÿ��reset����ǰURIBroker��״̬����ָ��ɺ�����parent��ͬ��
     * </p>
     */
    public void setParent(URIBroker parent) {
        assertTrue(!initialized, ILLEGAL_STATE, "already initialized");

        if (parent != null) {
            this.parent = parent;
        }
    }

    /**
     * ȡ��URL encoding�ı����ַ�����
     * <p>
     * ����ֵΪ<code>null</code>������<code>LocaleUtil</code>��ȡ�á�
     * </p>
     */
    public String getCharset() {
        return charset;
    }

    /**
     * ����URL encoding�ı����ַ�����
     */
    public void setCharset(String charset) {
        this.charset = trimToNull(charset);
    }

    /**
     * �Ƿ��Զ�reset��
     * <p>
     * URI broker����״̬�ġ������Զ�reset��Ϊ<code>true</code>������״̬��ִ��<code>render()</code>
     * ����֮�󽫻ָ�ԭ״�� �������ļ������ɵ�uri broker����autoResetΪ<code>false</code>��˵����״̬���ܱ��ı䡣
     * </p>
     */
    public boolean isAutoReset() {
        return autoReset;
    }

    /**
     * �Ƿ���interceptor��
     */
    public boolean hasInterceptors() {
        return interceptors != null && !interceptors.isEmpty();
    }

    /**
     * ȡ�����е�interceptors����״̬��
     */
    protected Map<URIBrokerInterceptor, Integer> getInterceptorStates() {
        if (interceptors == null) {
            interceptors = createLinkedHashMap();
        }

        return interceptors;
    }

    /**
     * ȡ�����е�interceptors��
     */
    public Collection<URIBrokerInterceptor> getInterceptors() {
        return getInterceptorStates().keySet();
    }

    /**
     * ȡ�����е�interceptors��
     */
    public void setInterceptors(Collection<URIBrokerInterceptor> interceptors) {
        clearInterceptors();

        for (URIBrokerInterceptor interceptor : interceptors) {
            addInterceptor(interceptor);
        }
    }

    /**
     * ���һ��interceptor��
     */
    public void addInterceptor(URIBrokerInterceptor interceptor) {
        getInterceptorStates().put(assertNotNull(interceptor, "interceptor"), null);
    }

    /**
     * ���interceptors��
     */
    public void clearInterceptors() {
        if (interceptors != null) {
            interceptors.clear();
        }
    }

    /**
     * ��ʼ��URI broker������ǰbroker��parent�ϲ���
     */
    public final void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        if (parent != null) {
            // ȷ��parent�Ѿ���ʼ��
            parent.init();

            // charset
            if (charset == null) {
                charset = parent.getCharset();
            }

            // init interceptors����parent interceptors����ǰ��
            if (parent.hasInterceptors()) {
                mergeLinkedHashMap(parent.getInterceptorStates(), getInterceptorStates());
            }

            // init others
            initDefaults(parent);
        }

        renderer.prerender();
    }

    /**
     * ����parent broker�е�ֵ��ΪĬ��ֵ���������ǵ�ǰbroker�����е�ֵ��
     */
    protected abstract void initDefaults(URIBroker parent);

    /**
     * ��λ��parent��״̬��
     */
    public final void reset() {
        URIBroker parent = this.parent;

        if (parent == null) {
            parent = newInstanceInternal();
        }

        // reset charset
        charset = parent.getCharset();

        // reset interceptors
        clearInterceptors();

        if (parent.hasInterceptors()) {
            for (URIBrokerInterceptor interceptor : parent.getInterceptors()) {
                addInterceptor(interceptor);
            }
        }

        // reset others
        copyFrom(parent);

        // ����renderer��
        // ȷ��ͬһ���͵�broker���Ÿ���Ԥ��Ⱦbuffer����Ϊ��ͬ��broker�п�����Ⱦ�����ͬ��
        if (parent.getClass().equals(getClass())) {
            renderer.copyFrom(parent.renderer);
        }

        // read request
        HttpServletRequest realRequest = getRealRequest();

        if (realRequest != null) {
            populateWithRequest(realRequest);
        }
    }

    /**
     * ����parent��״̬��
     * <p>
     * ����Ӧ�ø��Ǵ˷�������ʵ���ض���reset���ܡ�
     * </p>
     */
    protected abstract void copyFrom(URIBroker parent);

    /**
     * ��request�е�����ʱ��Ϣ��䵽uri broker�С�
     */
    protected abstract void populateWithRequest(HttpServletRequest request);

    /**
     * ��������������ʱ��������ʵ��request����
     * <ul>
     * <li>isRequestAware == true</li>
     * <li>request != null</li>
     * <li>����web������request proxy��ȡ����ʵ��request����</li>
     * </ul>
     */
    protected final HttpServletRequest getRealRequest() {
        if (isRequestAware()) {
            return getProxyTarget(request);
        }

        return null;
    }

    /**
     * �Ե�ǰURI brokerΪģ��, ����һ���µ�URI broker���µ�broker��ִ��<code>render()</code>
     * ��������Զ���λ��
     * <p>
     * �˷�����<code>render()</code>������ͬ�ĸ����ã�����<code>autoReset == true</code>
     * ����ô����״̬�Զ���λ����Ӧ�������³�����velocity����
     * </p>
     * 
     * <pre>
     * #set ($sub_uri = $uri.addPath("xxx/yyy").fork())
     * 
     * #foreach (...)
     *    &lt;a href="$sub_uri.addQueryData(...)"&gt;...&lt;/a&gt;
     * #end
     * </pre>
     */
    public final URIBroker fork() {
        return fork(true);
    }

    /**
     * �Ե�ǰURI brokerΪģ��, ����һ���µ�URI broker��
     */
    public final URIBroker fork(boolean autoReset) {
        URIBroker parentBroker;
        URIBroker broker = null;

        // ȷ����ǰbroker����autoReset��, ����ǰbroker��reset��Ӱ�������ɵ�broker
        if (autoReset && isAutoReset()) {
            parentBroker = fork(false);
            parentBroker.renderer.prerender();

            // ��λ��ǰ��broker, ����ִ�й�renderһ��
            reset();
        } else {
            parentBroker = (URIBroker) this;
        }

        // �����µĻ���parentBroker��broker
        // ȷ���½���broker�Ƿǿգ���Ϊͬһ���͡�
        broker = newInstanceInternal();

        ((URIBrokerFeatures) broker).autoReset = autoReset;
        broker.setRequestAware(parentBroker.isRequestAware());
        broker.setRequest(((URIBrokerFeatures) parentBroker).request);
        broker.setParent(parentBroker);
        broker.reset();

        return broker;
    }

    /**
     * ����һ���µĿհ�broker��
     */
    protected final URIBroker newInstanceInternal() {
        URIBroker instance = assertNotNull(newInstance(), "%s.newInstance() returns null", getClass().getName());

        assertTrue(instance != this, "%s.newInstance() returns itself", getClass().getName());

        assertTrue(instance.getClass().equals(getClass()), "%s.newInstance() returns wrong type: %s", getClass()
                .getName(), instance.getClass().getName());

        return instance;
    }

    /**
     * �����µ�ʵ����
     */
    protected abstract URIBroker newInstance();

    /**
     * ��˳��ִ������interceptors��
     */
    protected void processInterceptors() {
        if (hasInterceptors()) {
            for (Map.Entry<URIBrokerInterceptor, Integer> entry : getInterceptorStates().entrySet()) {
                if (entry.getValue() == null) {
                    entry.setValue(1);
                    entry.getKey().perform((URIBroker) this);
                }
            }
        }
    }

    /**
     * ��Ⱦuri��
     */
    public final String render() {
        return render(autoReset);
    }

    private String render(boolean reset) {
        processInterceptors();

        StringBuilder buf = new StringBuilder();
        render(buf);

        if (reset) {
            reset();
        }

        return buf.toString();
    }

    protected abstract void render(StringBuilder buf);

    protected abstract void renderServer(StringBuilder buf);

    protected abstract void renderPath(StringBuilder buf);

    protected abstract void renderQuery(StringBuilder buf);

    /**
     * ���߷���������URL���룬ʹ��uribroker��ָ����charset��
     */
    protected final String escapeURL(String str) {
        String charset = trimToNull(getCharset());

        try {
            return StringEscapeUtil.escapeURL(str, charset);
        } catch (UnsupportedEncodingException e) {
            return StringEscapeUtil.escapeURL(str);
        }
    }

    /**
     * ��parent map�е�ֵ���뵽��ǰmap��ǰ�档
     */
    protected final <K, V> void mergeLinkedHashMap(Map<K, V> parentMap, Map<K, V> thisMap) {
        assertNotNull(thisMap, "thisMap");

        Map<K, V> thisMapCopy = createLinkedHashMap();
        thisMapCopy.putAll(thisMap);
        thisMap.clear();

        if (parentMap != null) {
            thisMap.putAll(parentMap);
        }

        thisMap.putAll(thisMapCopy);
    }

    /**
     * ȡ��URI�ַ���������reset��
     */
    @Override
    public String toString() {
        return render(false);
    }

    /**
     * ������ȾURL������buffer�ĸ����ࡣ
     * <p>
     * Ϊ�˼ӿ���ȾURL���ٶȣ���init��ʱ������Ԥ��Ⱦ�������<code>serverBuffer</code>��
     * <code>pathBuffer</code>��<code>queryBuffer</code>
     * ������buffer����fork��ʱ��ֱ�ӽ�buffer���Ƹ��µĶ���
     * �������µ�brokerû�о����ܴ���޸ľ�render�Ļ�����Ⱦ�ٶȻ���ӿ졣
     * </p>
     */
    public final class Renderer {
        protected final StringBuilder serverBuffer = new StringBuilder();
        protected final StringBuilder pathBuffer = new StringBuilder();
        protected final StringBuilder queryBuffer = new StringBuilder();

        public boolean isServerRendered() {
            return serverBuffer.length() > 0;
        }

        public boolean isPathRendered() {
            return pathBuffer.length() > 0;
        }

        public boolean isQueryRendered() {
            return queryBuffer.length() > 0;
        }

        public void clearServerBuffer() {
            serverBuffer.setLength(0);
        }

        public void clearPathBuffer() {
            pathBuffer.setLength(0);
        }

        public void updatePathBuffer(String path) {
            if (isPathRendered()) {
                pathBuffer.append("/").append(escapeURL(path));
            }
        }

        public void truncatePathBuffer(int removedCount) {
            if (isPathRendered()) {
                int index = pathBuffer.length();

                for (int i = 0; i < removedCount && index >= 0; i++) {
                    index = pathBuffer.lastIndexOf("/", index - 1);
                }

                if (index >= 0) {
                    pathBuffer.setLength(index);
                } else {
                    pathBuffer.setLength(0);
                }
            }
        }

        public void clearQueryBuffer() {
            queryBuffer.setLength(0);
        }

        public void updateQueryBuffer(String id, String value) {
            if (isQueryRendered()) {
                queryBuffer.append("&").append(escapeURL(id)).append("=").append(escapeURL(value));
            }
        }

        private void prerender() {
            if (!isServerRendered()) {
                renderServer(serverBuffer);
            }

            if (!isPathRendered()) {
                renderPath(pathBuffer);
            }

            if (!isQueryRendered()) {
                renderQuery(queryBuffer);
            }
        }

        private void copyFrom(Renderer parent) {
            // server info
            clearServerBuffer();

            if (parent.isServerRendered()) {
                serverBuffer.append(parent.serverBuffer);
            }

            // path info
            clearPathBuffer();

            if (parent.isPathRendered()) {
                pathBuffer.append(parent.pathBuffer);
            }

            // query info
            clearQueryBuffer();

            if (parent.isQueryRendered()) {
                queryBuffer.append(parent.queryBuffer);
            }
        }
    }
}
