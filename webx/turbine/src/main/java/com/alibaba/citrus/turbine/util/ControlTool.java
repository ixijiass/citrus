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
package com.alibaba.citrus.turbine.util;

import static com.alibaba.citrus.springext.util.DomUtil.*;
import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.turbine.TurbineConstant.*;
import static com.alibaba.citrus.turbine.util.ControlTool.ErrorDetailLevel.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringEscapeUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Element;

import com.alibaba.citrus.service.configuration.ProductionModeAware;
import com.alibaba.citrus.service.mappingrule.MappingRuleService;
import com.alibaba.citrus.service.moduleloader.Module;
import com.alibaba.citrus.service.moduleloader.ModuleLoaderService;
import com.alibaba.citrus.service.pull.ToolFactory;
import com.alibaba.citrus.service.requestcontext.buffered.BufferedRequestContext;
import com.alibaba.citrus.service.template.Renderable;
import com.alibaba.citrus.service.template.TemplateService;
import com.alibaba.citrus.springext.support.BeanSupport;
import com.alibaba.citrus.springext.support.parser.AbstractSingleBeanDefinitionParser;
import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.TurbineRunDataInternal;
import com.alibaba.citrus.turbine.support.ContextAdapter;
import com.alibaba.citrus.turbine.support.MappedContext;
import com.alibaba.citrus.util.ExceptionUtil;
import com.alibaba.citrus.webx.WebxComponents;
import com.alibaba.citrus.webx.WebxException;

/**
 * ���ú���ʾһ��control module��tool��
 * 
 * @author Michael Zhou
 */
public class ControlTool extends ControlToolConfiguration implements Renderable {
    private static final Logger log = LoggerFactory.getLogger(ControlTool.class);
    private final ErrorHandler errorHandler;
    private LinkedList<ControlParameters> controlParameterStack = createLinkedList();

    public ControlTool() {
        this((ErrorHandler) null);
    }

    public ControlTool(boolean productionMode) {
        this(productionMode ? ErrorDetailLevel.messageOnly : ErrorDetailLevel.stackTrace);
    }

    public ControlTool(ErrorDetailLevel errorDetailLevel) {
        this(errorDetailLevel == null ? null : errorDetailLevel.getHandler());
    }

    public ControlTool(ErrorHandler errorHandler) {
        if (errorHandler == null) {
            errorHandler = messageOnly.getHandler();
        }

        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * ����control��ģ�塣�˷�����<code>setModule</code>ֻ��ִ����һ�����򽫺��Ժ��ߡ�
     * 
     * @param template controlģ����
     * @return <code>ControlTool</code>�����Է���ģ���еĲ���
     */
    public ControlTool setTemplate(String template) {
        ControlParameters params = getControlParameters();

        if (params.module == null) {
            params.template = template;
        }

        return this;
    }

    /**
     * ����control��ģ�顣�˷�����<code>setTemplate</code>ֻ��ִ����һ�����򽫺��Ժ��ߡ�
     * 
     * @param module controlģ����
     * @return <code>ControlTool</code>�����Է���ģ���еĲ���
     */
    public ControlTool setModule(String module) {
        ControlParameters params = getControlParameters();

        if (params.template == null) {
            params.module = module;
        }

        return this;
    }

    /**
     * ����control�Ĳ�����
     * <p>
     * ��Щ��������������һ��һ���Ե�<code>Map</code>�У���render�ɹ��Ժ󣬸�map�ͱ��������Ա��ٴε��ø�control��
     * </p>
     * 
     * @param name ������
     * @param value ����
     * @return <code>ControlTool</code>�����Է���ģ���еĲ���
     */
    public ControlTool setParameter(String name, Object value) {
        ControlParameters params = getControlParameters();

        params.put(name, value);

        return this;
    }

    /**
     * ��Ⱦ����
     * 
     * @return ��Ⱦ�Ľ��
     */
    public String render() {
        assertInitialized();

        ControlParameters params = getControlParameters();
        String componentName;
        String target = null;
        String content;
        boolean isTemplate;

        try {
            if (params.template != null) {
                componentName = parseComponentName(params.template);
                target = parseLocalName(params.template);
                isTemplate = true;
            } else if (params.module != null) {
                componentName = parseComponentName(params.module);
                target = parseLocalName(params.module);
                isTemplate = false;
            } else {
                throw new IllegalArgumentException("Neither template nor module name was specified to render a control");
            }

            // controlTool֧�ֿ�component���ã�����ȡ��ָ��component�µ�service��
            ModuleLoaderService moduleLoaderService = getService("moduleLoaderService", componentName,
                    this.moduleLoaderService, ModuleLoaderService.class);

            MappingRuleService mappingRuleService = getService("mappingRuleService", componentName,
                    this.mappingRuleService, MappingRuleService.class);

            TemplateService templateService = getService("templateService", componentName, this.templateService,
                    TemplateService.class);

            // ȡ��ʵ�ʵ�template/module����
            String templateName = null;
            String moduleName = null;

            if (isTemplate) {
                templateName = mappingRuleService.getMappedName(CONTROL_TEMPLATE, target);
                moduleName = mappingRuleService.getMappedName(CONTROL_MODULE, target);
            } else {
                moduleName = mappingRuleService.getMappedName(CONTROL_MODULE_NO_TEMPLATE, target);
            }

            // ִ��control module
            Module controlModule;

            if (templateName == null) {
                // templateNameδָ��ʱ��������module����û�����׳�ModuleNotFoundException
                controlModule = moduleLoaderService.getModule(CONTROL_MODULE, moduleName);
            } else {
                // ��ָ����templateNameʱ������û�е�control module����������Ⱦģ�塣
                // ������ʵ����page-driven������дģ�壬��Ҫʱ��дһ��module class��֮��Ӧ��
                controlModule = moduleLoaderService.getModuleQuiet(CONTROL_MODULE, moduleName);
            }

            if (log.isTraceEnabled()) {
                if (templateName != null) {
                    log.trace("Rendering control: template=" + templateName + ", control=" + moduleName);
                } else {
                    log.trace("Rendering control without template: control=" + moduleName);
                }
            }

            // ���ò���
            this.bufferedRequestContext.pushBuffer();

            try {
                controlParameterStack.addFirst(new ControlParameters()); // ֧��control��Ƕ��

                TurbineRunDataInternal rundata = (TurbineRunDataInternal) TurbineUtil.getTurbineRunData(this.request);
                rundata.setContextForControl(createContextForControl(params, componentName));

                if (controlModule != null) {
                    controlModule.execute();
                }

                if (templateName != null) {
                    templateService.writeTo(templateName, new ContextAdapter(rundata.getContextForControl()), rundata
                            .getResponse().getWriter());
                }
            } finally {
                controlParameterStack.removeFirst();
                content = this.bufferedRequestContext.popCharBuffer();
            }
        } catch (Exception e) {
            content = null;

            try {
                content = errorHandler.handleException(target, e);
            } catch (RuntimeException ee) {
            }

            // ���handler���ؿգ����׳��쳣���������handler���ص����ݡ�
            if (content == null) {
                throw new WebxException("Failed to execute control module: " + target, e);
            } else {
                log.error("Failed to execute control module: " + target, e);
            }
        } finally {
            // ����������Ա�����
            params.template = null;
            params.module = null;
            params.clear();
        }

        return content;
    }

    /**
     * ����template��module������ǰ�����硰componentName:��ǰ׺���򷵻ش�componentName�����򷵻�null��
     */
    private String parseComponentName(String name) {
        int index = name == null ? -1 : name.indexOf(":");

        if (index >= 0) {
            return trimToNull(name.substring(0, index));
        }

        return null;
    }

    /**
     * ȡ�ò�������componentName:��ǰ׺��template��module�����ơ�
     */
    private String parseLocalName(String name) {
        int index = name == null ? -1 : name.indexOf(":");

        if (index >= 0) {
            return trimToNull(name.substring(index + 1));
        } else {
            return trimToNull(name);
        }
    }

    private <T> T getService(String name, String componentName, T defaultService, Class<T> serviceType) {
        if (componentName == null) {
            return defaultService;
        }

        ApplicationContext context = assertNotNull(this.components.getComponent(componentName),
                "invalid prefix \"%s:\", component does not exist", componentName).getApplicationContext();

        try {
            return serviceType.cast(context.getBean(name, serviceType));
        } catch (BeansException e) {
            throw new IllegalArgumentException(String.format("Could not get service: \"%s:%s\"", componentName,
                    serviceType.getSimpleName()), e);
        }
    }

    private Context createContextForControl(ControlParameters params, String componentName) {
        // get parent context
        TurbineRunDataInternal rundata = (TurbineRunDataInternal) TurbineUtil.getTurbineRunData(this.request);
        Context parentContext = rundata.getContext(componentName);

        // create control context
        MappedContext context = new MappedContext(parentContext);

        // add all params
        context.getMap().putAll(params);

        return context;
    }

    /**
     * ȡ��ջ����control������
     */
    protected ControlParameters getControlParameters() {
        if (controlParameterStack.isEmpty()) {
            controlParameterStack.addFirst(new ControlParameters());
        }

        return controlParameterStack.getFirst();
    }

    /**
     * ����һ��control�ĵ��ò�����
     */
    protected static class ControlParameters extends HashMap<String, Object> {
        private static final long serialVersionUID = 3256721796996084529L;
        private String module;
        private String template;

        public ControlParameters() {
            super(4);
        }
    }

    /**
     * ��ӡ�쳣����ϸ�̶ȡ�
     */
    public static enum ErrorDetailLevel {
        /**
         * �׳��쳣��
         */
        throwException,
        /**
         * ������κδ������ݡ�
         */
        quiet,
        /**
         * ֻ����쳣��Ϣ��
         */
        messageOnly,
        /**
         * ����쳣����ϸ��Ϣ��
         */
        stackTrace,
        /**
         * ���쳣��ӡ��HTMLע���С�
         */
        comment;

        private final ErrorHandler handler = new DefaultErrorHandler(this);

        public ErrorHandler getHandler() {
            return handler;
        }
    }

    /**
     * ��������controlִ�й����е��쳣��
     */
    public static interface ErrorHandler {
        String handleException(String controlTarget, Exception e);
    }

    public static class ThrowError implements ErrorHandler {
        public String handleException(String controlTarget, Exception e) {
            return null;
        }
    }

    public static class DefaultErrorHandler implements ErrorHandler {
        private String errorTagClass;
        private ErrorDetailLevel detailLevel;

        public DefaultErrorHandler() {
        }

        public DefaultErrorHandler(ErrorDetailLevel detailLevel) {
            this.detailLevel = detailLevel;
        }

        public String getErrorTagClass() {
            return defaultIfNull(errorTagClass, "webx.error");
        }

        public void setErrorTagClass(String errorTagClass) {
            this.errorTagClass = trimToNull(errorTagClass);
        }

        public ErrorDetailLevel getDetailLevel() {
            return detailLevel == null ? messageOnly : detailLevel;
        }

        public void setDetailLevel(ErrorDetailLevel detailLevel) {
            this.detailLevel = detailLevel;
        }

        public String handleException(String controlTarget, Exception e) {
            ErrorDetailLevel detailLevel = getDetailLevel();

            switch (detailLevel) {
                case throwException:
                    return null;

                case quiet:
                    return EMPTY_STRING;

                default:
                    break;
            }

            StringWriter buf = new StringWriter();
            PrintWriter pw = new PrintWriter(buf);
            Formatter fmt = new Formatter(pw);

            fmt.format("<!-- control failed: target=%s, exceptionType=%s -->", controlTarget, e.getClass().getName());

            switch (detailLevel) {
                case messageOnly:
                    fmt.format("<div class=\"%s\">", getErrorTagClass());

                    String msg = e.getMessage();

                    if (isEmpty(msg)) {
                        msg = e.getClass().getSimpleName();
                    }

                    pw.append(escapeHtml(msg)); // !!��Ҫ��escapeHtml

                    pw.append("</div>");
                    break;

                case stackTrace:
                    fmt.format("<div class=\"%s\">", getErrorTagClass());
                    pw.append(escapeHtml(ExceptionUtil.getStackTrace(e))); // !!��Ҫ��escapeHtml
                    pw.append("</div>");
                    break;

                case comment:
                    pw.append("<!-- stacktrace: \n");
                    pw.append(escapeHtml(ExceptionUtil.getStackTrace(e))); // !!��Ҫ��escapeHtml
                    pw.append("-->");
                    break;

                default:
                    unreachableCode(detailLevel.name());
            }

            pw.flush();
            return buf.toString();
        }
    }

    public static class DefinitionParser extends AbstractSingleBeanDefinitionParser<Factory> {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            String errorDetailLevel = trimToNull(element.getAttribute("detailLevel"));

            if (errorDetailLevel != null) {
                builder.addPropertyValue("errorDetailLevel", errorDetailLevel);
            } else {
                Element errorHandlerElement = theOnlySubElement(element, and(sameNs(element), name("errorHandler")));

                if (errorHandlerElement != null) {
                    builder.addPropertyValue("errorHandler",
                            parseBean(errorHandlerElement, parserContext, builder.getRawBeanDefinition()));
                }
            }
        }
    }

    /**
     * pull tool factory��
     */
    public static class Factory extends ControlToolConfiguration implements ToolFactory, ProductionModeAware {
        private ErrorDetailLevel errorDetailLevel;
        private ErrorHandler errorHandler;
        private boolean productionMode = true;

        public void setProductionMode(boolean productionMode) {
            this.productionMode = productionMode;
        }

        public void setErrorDetailLevel(ErrorDetailLevel errorDetailLevel) {
            this.errorDetailLevel = errorDetailLevel;
        }

        public void setErrorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
        }

        public boolean isSingleton() {
            return false;
        }

        public Object createTool() throws Exception {
            ControlTool tool;

            if (errorDetailLevel != null) {
                tool = new ControlTool(errorDetailLevel);
            } else if (errorHandler != null) {
                tool = new ControlTool(errorHandler);
            } else {
                tool = new ControlTool(productionMode);
            }

            tool.init(components, moduleLoaderService, mappingRuleService, templateService, request,
                    bufferedRequestContext);

            tool.afterPropertiesSet();

            return tool;
        }
    }
}

class ControlToolConfiguration extends BeanSupport {
    protected WebxComponents components;
    protected ModuleLoaderService moduleLoaderService;
    protected MappingRuleService mappingRuleService;
    protected TemplateService templateService;
    protected HttpServletRequest request;
    protected BufferedRequestContext bufferedRequestContext;

    @Autowired
    protected void init(WebxComponents components, ModuleLoaderService moduleLoaderService,
                        MappingRuleService mappingRuleService, TemplateService templateService,
                        HttpServletRequest request, BufferedRequestContext bufferedRequestContext) {
        this.components = components;
        this.moduleLoaderService = moduleLoaderService;
        this.mappingRuleService = mappingRuleService;
        this.templateService = templateService;
        this.request = request;
        this.bufferedRequestContext = bufferedRequestContext;
    }

    @Override
    protected void init() {
        assertNotNull(components, "no components");
        assertNotNull(moduleLoaderService, "no moduleLoaderService");
        assertNotNull(mappingRuleService, "no mappingRuleService");
        assertNotNull(templateService, "no templateService");
        assertNotNull(request, "no request");
        assertNotNull(bufferedRequestContext, "no bufferedRequestContext");
    }
}
