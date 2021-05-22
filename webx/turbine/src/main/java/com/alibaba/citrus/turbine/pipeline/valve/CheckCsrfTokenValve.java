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
package com.alibaba.citrus.turbine.pipeline.valve;

import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.turbine.util.TurbineUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.logconfig.support.SecurityLogger;
import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.support.AbstractValve;
import com.alibaba.citrus.service.pipeline.support.AbstractValveDefinitionParser;
import com.alibaba.citrus.turbine.TurbineRunData;
import com.alibaba.citrus.turbine.util.CsrfToken;
import com.alibaba.citrus.turbine.util.CsrfTokenCheckException;
import com.alibaba.citrus.util.StringUtil;

/**
 * �������<code>CsrfToken</code>��valve��������ֹcsrf�������ظ��ύͬһ����
 * 
 * @author Michael Zhou
 */
public class CheckCsrfTokenValve extends AbstractValve {
    private final SecurityLogger log = new SecurityLogger();

    @Autowired
    private HttpServletRequest request;

    private String tokenKey;
    private int maxTokens;
    private String expiredPage;

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = trimToNull(tokenKey);
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public String getExpiredPage() {
        return expiredPage;
    }

    public void setExpiredPage(String expiredPage) {
        this.expiredPage = expiredPage;
    }

    public String getLogName() {
        return this.log.getLogger().getName();
    }

    public void setLogName(String logName) {
        this.log.setLogName(logName);
    }

    @Override
    protected void init() {
        tokenKey = defaultIfNull(tokenKey, CsrfToken.DEFAULT_TOKEN_KEY);
    }

    /**
     * ���csrf���������ض��򵽳���ҳ�档
     */
    public void invoke(PipelineContext pipelineContext) throws Exception {
        TurbineRunData rundata = getTurbineRunData(request);

        // ��ȡrequest�е�csrfֵ
        String tokenFromRequest = StringUtil.trimToNull(rundata.getParameters().getString(tokenKey));

        if (tokenFromRequest != null) {
            HttpSession session = rundata.getRequest().getSession();

            // �ȼ��longLiveToken�����ƥ�䣬���ü��uniqueToken�ˡ� 
            if (!tokenFromRequest.equals(CsrfToken.getLongLiveTokenInSession(session))) {
                List<String> tokensInSession = CsrfToken.getTokensInSession(session, tokenKey);

                if (!tokensInSession.contains(tokenFromRequest)) {
                    // �����������ֹ����
                    requestExpired(rundata, tokenFromRequest, tokensInSession);
                } else {
                    // ������ϣ������session����Ӧ��token���Է�ֹ�ٴ�ʹ����
                    tokensInSession.remove(tokenFromRequest);

                    CsrfToken.setTokensInSession(session, tokenKey, tokensInSession);
                }
            }
        }

        try {
            // ��thread�������б��浱ǰ��tokenKey���Ա�ʹ����csrfToken�ļ�鶼��ʹ��ͳһ��key��
            CsrfToken.setContextTokenConfiguration(tokenKey, maxTokens);
            pipelineContext.invokeNext();
        } finally {
            CsrfToken.resetContextTokenConfiguration();
        }
    }

    private void requestExpired(TurbineRunData rundata, String tokenFromRequest, List<String> tokensInSession) {
        log.getLogger().warn("CsrfToken \"{}\" does not match: requested token is {}, but the session tokens are {}.",
                new Object[] { tokenKey, tokenFromRequest, tokensInSession });

        // �����ִ�������1. ��ʾexpiredPage��2. �׳��쳣��
        if (expiredPage != null) {
            rundata.setRedirectTarget(expiredPage);
        } else if (expiredPage == null) {
            throw new CsrfTokenCheckException(rundata.getRequest().getRequestURL().toString());
        }
    }

    public static class DefinitionParser extends AbstractValveDefinitionParser<CheckCsrfTokenValve> {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            attributesToProperties(element, builder, "tokenKey", "maxTokens", "expiredPage", "logName");
        }
    }
}
