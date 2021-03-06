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
package com.alibaba.citrus.webx.support;

import static com.alibaba.citrus.service.requestcontext.util.RequestContextUtil.*;
import static com.alibaba.citrus.springext.util.SpringExtUtil.*;
import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ExceptionUtil.*;
import static com.alibaba.citrus.util.FileUtil.*;
import static com.alibaba.citrus.util.ServletUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static java.util.Collections.*;
import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.alibaba.citrus.service.pipeline.Pipeline;
import com.alibaba.citrus.service.requestcontext.RequestContext;
import com.alibaba.citrus.service.requestcontext.RequestContextChainingService;
import com.alibaba.citrus.service.requestcontext.buffered.BufferedRequestContext;
import com.alibaba.citrus.service.requestcontext.lazycommit.LazyCommitRequestContext;
import com.alibaba.citrus.service.requestcontext.util.RequestContextUtil;
import com.alibaba.citrus.util.ClassLoaderUtil;
import com.alibaba.citrus.util.internal.ToStringBuilder;
import com.alibaba.citrus.webx.BadRequestException;
import com.alibaba.citrus.webx.ResourceNotFoundException;
import com.alibaba.citrus.webx.WebxComponents;
import com.alibaba.citrus.webx.WebxException;
import com.alibaba.citrus.webx.WebxRootController;
import com.alibaba.citrus.webx.config.WebxConfiguration;
import com.alibaba.citrus.webx.handler.ErrorHandlerMapping;
import com.alibaba.citrus.webx.handler.RequestHandler;
import com.alibaba.citrus.webx.handler.RequestHandlerContext;
import com.alibaba.citrus.webx.handler.RequestHandlerMapping;
import com.alibaba.citrus.webx.handler.RequestHandlerNameAware;
import com.alibaba.citrus.webx.handler.impl.MainHandler;
import com.alibaba.citrus.webx.handler.impl.error.DetailedErrorHandler;
import com.alibaba.citrus.webx.handler.impl.error.PipelineErrorHandler;
import com.alibaba.citrus.webx.handler.impl.error.SendErrorHandler;
import com.alibaba.citrus.webx.util.ErrorHandlerHelper;
import com.alibaba.citrus.webx.util.ErrorHandlerHelper.ExceptionCodeMapping;
import com.alibaba.citrus.webx.util.WebxUtil;

public abstract class AbstractWebxRootController implements WebxRootController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** ??request??????request context owner???????? */
    private static final String REQUEST_CONTEXT_OWNER_KEY = "_request_context_owner_";

    /** ????????request handler?????????? */
    private static final String REQUEST_HANDLER_LOCATION = "META-INF/webx.internal-request-handlers";

    /** Error???????????? */
    private static final String ERROR_PREFIX = "error";

    private WebxComponents components;
    private InternalRequestHandlerMapping internalHandlerMapping;
    private RequestContextChainingService requestContexts;

    public WebxComponents getComponents() {
        return components;
    }

    public WebxConfiguration getWebxConfiguration() {
        return getComponents().getParentWebxConfiguration();
    }

    public ServletContext getServletContext() {
        return getComponents().getParentApplicationContext().getServletContext();
    }

    /**
     * ????????????controller??????????
     */
    public void init(WebxComponents components) {
        this.components = components;
    }

    /**
     * ??????????????????WebApplicationContext??????????
     */
    public void onRefreshContext() throws BeansException {
        initWebxConfiguration();
        initInternalRequestHandler();
        initRequestContexts();
    }

    private void initWebxConfiguration() {
        WebxConfiguration webxConfiguration = getWebxConfiguration();

        log.debug("Initializing Webx root context in {} mode, according to <webx-configuration>",
                webxConfiguration.isProductionMode() ? "production" : "development");
    }

    private void initInternalRequestHandler() {
        internalHandlerMapping = new InternalRequestHandlerMapping();
    }

    private void initRequestContexts() {
        requestContexts = getWebxConfiguration().getRequestContexts();

        log.debug("Using RequestContextChainingService: {}", requestContexts);
    }

    public void onFinishedProcessContext() {
    }

    public final void service(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws Exception {
        RequestContext requestContext = null;

        try {
            requestContext = assertNotNull(getRequestContext(request, response), "could not get requestContext");

            if (checkRequest(requestContext)) {
                request = requestContext.getRequest();
                response = requestContext.getResponse();

                RequestHandlerContext ctx = internalHandlerMapping.getRequestHandler(request, response);

                if (ctx == null) {
                    boolean requestProcessed = handleRequest(requestContext);

                    if (!requestProcessed) {
                        giveUpControl(requestContext, chain);
                    }
                } else {
                    ctx.getRequestHandler().handleRequest(ctx);
                }
            }
        } catch (Throwable e) {
            // ????????e????????
            //
            // 1. ????????errorHandler????????e??errorHandler??????????????????????
            //    errorHandler?????????????? ?? ??????????????
            // 2. Handler??????????????????????????servlet engine????????????????????????????web.xml??????????????????
            //    ????????????errorHandler????????????????????
            // 3. ????????errorHandler????????????????servlet engine????????????????????????????web.xml??????????????????
            //    ????????????????????????????????????????
            try {
                try {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } catch (Exception ee) {
                    // ignore this exception
                }

                clearBuffer(requestContext, response);

                // ??????????errorHandler
                RequestHandlerContext ctx = internalHandlerMapping.getRequestHandler(request, response, e);

                assertNotNull(ctx, "Could not get exception handler for exception: %s", e);

                // ????????
                ctx.getLogger().error("Error occurred while process request " + request.getRequestURI(), e);

                try {
                    // ????error??????????????component????????root component??
                    WebxUtil.setCurrentComponent(request, components.getComponent(null));
                    ctx.getRequestHandler().handleRequest(ctx);
                } finally {
                    WebxUtil.setCurrentComponent(request, null);
                }
            } catch (Throwable ee) {
                // ????????????
                // 1. ee causedBy e????????????errorHandler??????????????????????????servlet engine??????
                // 2. ee??e????????????????errorHandler??????????????????????????????????????????
                if (!getCauses(ee).contains(e)) {
                    log.error("Another exception occurred while handling exception " + e.getClass().getSimpleName()
                            + ": " + e.getMessage(), ee);
                }

                clearBuffer(requestContext, response);

                if (e instanceof ServletException) {
                    throw (ServletException) e;
                } else if (e instanceof IOException) {
                    throw (IOException) e;
                } else if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (e instanceof Error) {
                    throw (Error) e;
                } else {
                    throw new ServletException(e);
                }
            }
        } finally {
            if (requestContext != null) {
                try {
                    commitRequestContext(requestContext);
                } catch (Exception e) {
                    log.error("Exception occurred while commit rundata", e);
                }
            }
        }
    }

    /**
     * ????????????????????????servlet engine??
     */
    private void giveUpControl(RequestContext requestContext, FilterChain chain) throws IOException, ServletException {
        // 1. ????buffering
        BufferedRequestContext brc = findRequestContext(requestContext, BufferedRequestContext.class);

        if (brc != null) {
            try {
                brc.setBuffering(false);
            } catch (IllegalStateException e) {
                // getInputStream??getWriter??????????????????????buffering??????
            }
        }

        // 2. ????contentType??????
        try {
            requestContext.getResponse().setContentType(null);
        } catch (Exception e) {
            // ignored, ??????????servlet engine??????null????
        }

        // ????filter chain
        chain.doFilter(requestContext.getRequest(), requestContext.getResponse());
    }

    /**
     * ????request??????????<code>true</code>??????????????????????????????????????
     */
    protected boolean checkRequest(RequestContext requestContext) {
        LazyCommitRequestContext lcrc = findRequestContext(requestContext, LazyCommitRequestContext.class);

        if (lcrc != null) {
            return !lcrc.isRedirected();
        } else {
            return true;
        }
    }

    /**
     * ??????????
     */
    protected abstract boolean handleRequest(RequestContext requestContext) throws Exception;

    /**
     * ????buffer??
     */
    private void clearBuffer(RequestContext requestContext, HttpServletResponse response) {
        // ??????????????requestContext????????????requestContext??????
        if (requestContext != null) {
            response = requestContext.getResponse();
        }

        if (!response.isCommitted()) {
            response.resetBuffer();
        }
    }

    /**
     * ????request context??????
     */
    private RequestContext getRequestContext(HttpServletRequest request, HttpServletResponse response) {
        RequestContext requestContext = RequestContextUtil.getRequestContext(request);

        if (requestContext == null) {
            requestContext = requestContexts.getRequestContext(getServletContext(), request, response);

            request.setAttribute(REQUEST_CONTEXT_OWNER_KEY, this);
        }

        return requestContext;
    }

    /**
     * ????request context??
     */
    private void commitRequestContext(RequestContext requestContext) {
        if (this == requestContext.getRequest().getAttribute(REQUEST_CONTEXT_OWNER_KEY)) {
            requestContext.getRequest().removeAttribute(REQUEST_CONTEXT_OWNER_KEY);
            requestContexts.commitRequestContext(requestContext);
        }
    }

    /**
     * ????webx????????????????????
     */
    private class InternalRequestHandlerContext extends RequestHandlerContext {
        private final RequestHandler handler;

        public InternalRequestHandlerContext(HttpServletRequest request, HttpServletResponse response,
                                             String internalBaseURL, String baseURL, String resourceName,
                                             RequestHandler handler) {
            super(request, response, AbstractWebxRootController.this.getServletContext(), internalBaseURL, baseURL,
                    resourceName);
            this.handler = handler;
        }

        @Override
        public RequestHandler getRequestHandler() {
            return handler;
        }

        @Override
        public Logger getLogger() {
            return log;
        }
    }

    /**
     * ????????webx??????????mapping??
     */
    private class InternalRequestHandlerMapping implements RequestHandlerMapping, ErrorHandlerMapping {
        private final Pattern homepagePattern = Pattern.compile("(^|\\?|&)home(=|&|$)");
        private final boolean productionMode;
        private String internalPathPrefix;
        private RequestHandler mainHandler;
        private RequestHandler errorHandler;
        private Map<String, RequestHandler> internalHandlers = emptyMap();

        public InternalRequestHandlerMapping() {
            productionMode = getWebxConfiguration().isProductionMode();

            // ??mapping????application context??????????????handler????
            ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) components
                    .getParentApplicationContext()).getBeanFactory();

            beanFactory.registerResolvableDependency(RequestHandlerMapping.class, this);

            // internalPathPrefix
            internalPathPrefix = getWebxConfiguration().getInternalPathPrefix();
            internalPathPrefix = normalizeAbsolutePath(internalPathPrefix, true); // ????????/internal

            if (isEmpty(internalPathPrefix)) {
                throw new IllegalArgumentException("Invalid internalPathPrefix: "
                        + getWebxConfiguration().getInternalPathPrefix());
            }

            // ????????????errorHandler
            // ??production mode????????config????????exception pipeline????????????
            // ????sendError????web.xml????????????????????????
            if (productionMode) {
                Pipeline exceptionPipeline = getWebxConfiguration().getExceptionPipeline();

                if (exceptionPipeline == null) {
                    log.debug("No exceptionPipeline configured in <webx-configuration>.");
                    errorHandler = new SendErrorHandler();
                } else {
                    errorHandler = new PipelineErrorHandler(exceptionPipeline);
                }
            }

            // ??????????????????????????????????
            else {
                errorHandler = new DetailedErrorHandler();
                ((DetailedErrorHandler) errorHandler).setName(ERROR_PREFIX);
            }

            autowireAndInitialize(errorHandler, components.getParentApplicationContext(), AUTOWIRE_NO, ERROR_PREFIX);
            log.debug("Using Exception Handler: {}.", errorHandler.getClass().getName());

            // ??????????????????????????????handlers
            if (!productionMode) {
                // ??META-INF/webx.internal-request-handlers????????error handler??main handler
                internalHandlers = loadInternalHandlers(REQUEST_HANDLER_LOCATION);

                // ????????????mainHandler
                mainHandler = new MainHandler();
                ((MainHandler) mainHandler).setName(EMPTY_STRING);
                autowireAndInitialize(mainHandler, components.getParentApplicationContext(), AUTOWIRE_NO, ERROR_PREFIX);
            }
        }

        public String[] getRequestHandlerNames() {
            return internalHandlers.keySet().toArray(new String[internalHandlers.size()]);
        }

        public RequestHandlerContext getRequestHandler(HttpServletRequest request, HttpServletResponse response) {
            String baseURL = getBaseURL(request);
            String path = getResourcePath(request).replace(' ', '+'); // ??????????+??????internalHandlers??key??????????????
            String internalBaseURL = baseURL + internalPathPrefix;

            // ??????/??????????mainHandler????????????????????????????????
            if (mainHandler != null && (EMPTY_STRING.equals(path) || "/".equals(path))) {
                // ?????????????????home
                String qs = request.getQueryString();

                if (isEmpty(qs) || !homepagePattern.matcher(qs).find()) {
                    return new InternalRequestHandlerContext(request, response, internalBaseURL, internalBaseURL, path,
                            mainHandler);
                }
            }

            // ??????/internal
            if (startsWithElement(path, internalPathPrefix)) {
                path = removeStartElement(path, internalPathPrefix);

                // ??????/error??????????????????
                if (errorHandler != null && !productionMode && startsWithElement(path, ERROR_PREFIX)) {
                    path = removeStartElement(path, ERROR_PREFIX);
                    return new InternalRequestHandlerContext(request, response, internalBaseURL, internalBaseURL + "/"
                            + ERROR_PREFIX, path, errorHandler);
                }

                // internalHandlers????????????
                for (Map.Entry<String, RequestHandler> entry : internalHandlers.entrySet()) {
                    String prefix = entry.getKey();

                    if (startsWithElement(path, prefix)) {
                        RequestHandler handler = entry.getValue();
                        path = removeStartElement(path, prefix);

                        return new InternalRequestHandlerContext(request, response, internalBaseURL, internalBaseURL
                                + "/" + prefix, path, handler);
                    }
                }

                // ??????main page??????
                if (mainHandler != null) {
                    return new InternalRequestHandlerContext(request, response, internalBaseURL, internalBaseURL, path,
                            mainHandler);
                }

                // ??????????
                throw new ResourceNotFoundException(request.getRequestURI());
            }

            return null;
        }

        public RequestHandlerContext getRequestHandler(HttpServletRequest request, HttpServletResponse response,
                                                       Throwable exception) {
            // servletName == ""
            ErrorHandlerHelper helper = ErrorHandlerHelper.getInstance(request);

            helper.init(EMPTY_STRING, exception, exceptionCodeMapping);
            response.setStatus(helper.getStatusCode());

            String internalBaseURL = getBaseURL(request) + internalPathPrefix;

            return new InternalRequestHandlerContext(request, response, internalBaseURL, internalBaseURL + "/"
                    + ERROR_PREFIX, "", errorHandler);
        }

        /**
         * ??????????????????<code>^element/|^element$</code>??
         */
        private boolean startsWithElement(String path, String element) {
            if (path.equals(element)) {
                return true;
            }

            if (path.startsWith(element) && path.charAt(element.length()) == '/') {
                return true;
            }

            return false;
        }

        /**
         * ??????????<code>^element/|^element$</code>??
         */
        private String removeStartElement(String path, String element) {
            if (path.equals(element)) {
                return EMPTY_STRING;
            }

            return path.substring(element.length() + 1);
        }

        private Map<String, RequestHandler> loadInternalHandlers(String location) {
            ClassLoader loader = components.getParentApplicationContext().getClassLoader();
            Properties handlerNames;

            try {
                handlerNames = PropertiesLoaderUtils.loadAllProperties(location, loader);
            } catch (IOException e) {
                throw new WebxException("Could not load " + location, e);
            }

            // ????handlers
            Map<String, RequestHandler> handlers = createTreeMap(new Comparator<String>() {
                public int compare(String s1, String s2) {
                    int lenDiff = s2.length() - s1.length();

                    if (lenDiff != 0) {
                        return lenDiff; // ??????????????????
                    } else {
                        return s1.compareTo(s2); // ????????????????
                    }
                }
            });

            for (Map.Entry<?, ?> entry : handlerNames.entrySet()) {
                String name = normalizeRelativePath((String) entry.getKey(), true); // ????????xxx/yyy/zzz
                String handlerClass = trimToNull((String) entry.getValue());

                // ??????????
                if (!isEmpty(name) && handlerClass != null) {
                    if (ERROR_PREFIX.equals(name)) {
                        log.warn("Ignored request handler with reserved name [" + ERROR_PREFIX + "]: " + handlerClass);
                        continue;
                    }

                    try {
                        Object handler = ClassLoaderUtil.newInstance(handlerClass, loader);

                        if (handler instanceof RequestHandlerNameAware) {
                            ((RequestHandlerNameAware) handler).setName(name);
                        }

                        autowireAndInitialize(handler, components.getParentApplicationContext(), AUTOWIRE_NO, name);

                        try {
                            handlers.put(name, RequestHandler.class.cast(handler));
                        } catch (ClassCastException e) {
                            // ??????????handler????????????????
                            log.error("Declared internal request handler must implement InternalRequestHandler: "
                                    + name + "=" + handlerClass, e);
                        }
                    } catch (Exception e) {
                        // ??????????handler????????????????
                        log.error("Could not create internal request handler: " + name + "=" + handlerClass, e);
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug(new ToStringBuilder().append("loading internal request handlers:").append(handlers)
                        .toString());
            }

            return handlers;
        }

        /**
         * Exception??statusCode????????
         */
        private final ExceptionCodeMapping exceptionCodeMapping = new ExceptionCodeMapping() {
            public int getExceptionCode(Throwable exception) {
                if (exception instanceof ResourceNotFoundException) {
                    return HttpServletResponse.SC_NOT_FOUND;
                } else if (exception instanceof BadRequestException) {
                    return HttpServletResponse.SC_BAD_REQUEST;
                }

                return 0;
            }
        };
    }
}
