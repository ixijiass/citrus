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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.support.AbstractValve;
import com.alibaba.citrus.service.pipeline.support.AbstractValveDefinitionParser;
import com.alibaba.citrus.webx.util.SetLoggingContextHelper;

/**
 * ���û����logging MDC��valve��
 * 
 * @author Michael Zhou
 */
public class SetLoggingContextValve extends AbstractValve {
    @Autowired
    private HttpServletRequest request;

    public void invoke(PipelineContext pipelineContext) throws Exception {
        SetLoggingContextHelper helper = new SetLoggingContextHelper(request);

        try {
            helper.setLoggingContext();

            pipelineContext.invokeNext();
        } finally {
            helper.clearLoggingContext();
        }
    }

    public static class DefinitionParser extends AbstractValveDefinitionParser<SetLoggingContextValve> {
    }
}
