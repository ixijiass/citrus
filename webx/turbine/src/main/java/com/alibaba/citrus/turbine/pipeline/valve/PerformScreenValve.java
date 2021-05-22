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

import static com.alibaba.citrus.turbine.TurbineConstant.*;
import static com.alibaba.citrus.turbine.util.TurbineUtil.*;
import static com.alibaba.citrus.util.Assert.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.mappingrule.MappingRuleService;
import com.alibaba.citrus.service.moduleloader.Module;
import com.alibaba.citrus.service.moduleloader.ModuleLoaderException;
import com.alibaba.citrus.service.moduleloader.ModuleLoaderService;
import com.alibaba.citrus.service.moduleloader.ModuleNotFoundException;
import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.support.AbstractValve;
import com.alibaba.citrus.service.pipeline.support.AbstractValveDefinitionParser;
import com.alibaba.citrus.turbine.TurbineRunData;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.citrus.webx.WebxException;

/**
 * ִ��screen��
 * 
 * @author Michael Zhou
 */
public class PerformScreenValve extends AbstractValve {
    @Autowired
    private ModuleLoaderService moduleLoaderService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MappingRuleService mappingRuleService;

    public MappingRuleService getMappingRuleService() {
        return mappingRuleService;
    }

    public void invoke(PipelineContext pipelineContext) throws Exception {
        TurbineRunData rundata = getTurbineRunData(request);

        // ����ض����־��������ض�������Ҫ��ҳ�������
        if (!rundata.isRedirected()) {
            setContentType(rundata);
            performScreenModule(rundata);
        }

        pipelineContext.invokeNext();
    }

    /**
     * ����content type��
     */
    protected void setContentType(TurbineRunData rundata) {
        // ����content type������Ҫ����charset����ΪSetLocaleRequestContext�Ѿ�������charset��
        // ���⸲�Ǳ������õ�contentType��
        if (StringUtil.isEmpty(rundata.getResponse().getContentType())) {
            rundata.getResponse().setContentType("text/html");
        }
    }

    /**
     * ִ��screenģ�顣
     */
    protected void performScreenModule(TurbineRunData rundata) {
        String target = assertNotNull(rundata.getTarget(), "Target was not specified");

        // ��target��ȡ��screen module����
        String moduleName = getModuleName(target);

        // ���������template����Ĭ�ϴ�layout
        rundata.setLayoutEnabled(true);

        try {
            Module module = moduleLoaderService.getModuleQuiet(SCREEN_MODULE, moduleName);

            // ��ָ����templateNameʱ������û�е�screen module����������Ⱦģ�塣
            // ������ʵ����page-driven������дģ�壬��Ҫʱ��дһ��module class��֮��Ӧ��
            if (module != null) {
                module.execute();
            } else {
                if (isScreenModuleRequired()) {
                    throw new ModuleNotFoundException("Could not find screen module: " + moduleName);
                }
            }
        } catch (ModuleLoaderException e) {
            throw new WebxException("Failed to load screen module: " + moduleName, e);
        } catch (Exception e) {
            throw new WebxException("Failed to execute screen: " + moduleName, e);
        }
    }

    /**
     * �������<code>true</code>����ô��ģ���Ҳ���ʱ�������쳣��������Ը��Ǵ˷������Ըı���Ϊ��
     */
    protected boolean isScreenModuleRequired() {
        return true;
    }

    /**
     * ����targetȡ��screenģ��������������޸�ӳ�����
     */
    protected String getModuleName(String target) {
        return mappingRuleService.getMappedName(SCREEN_MODULE_NO_TEMPLATE, target);
    }

    public static class DefinitionParser extends AbstractValveDefinitionParser<PerformScreenValve> {
    }
}
