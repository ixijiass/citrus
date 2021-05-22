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
package com.alibaba.citrus.service.velocity;

import static com.alibaba.citrus.test.TestEnvStatic.*;
import static com.alibaba.citrus.test.TestUtil.*;
import static com.alibaba.citrus.util.BasicConstant.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.alibaba.citrus.service.template.Renderable;
import com.alibaba.citrus.service.template.TemplateContext;
import com.alibaba.citrus.service.template.TemplateException;
import com.alibaba.citrus.service.template.TemplateNotFoundException;
import com.alibaba.citrus.service.template.support.MappedTemplateContext;
import com.alibaba.citrus.service.velocity.impl.ConditionalEscapeHandler;
import com.alibaba.citrus.service.velocity.impl.CustomizedUberspectImpl;
import com.alibaba.citrus.service.velocity.impl.PluginDelegator;
import com.alibaba.citrus.service.velocity.impl.PreloadedResourceLoader;
import com.alibaba.citrus.service.velocity.impl.SpringResourceLoaderAdapter;
import com.alibaba.citrus.service.velocity.impl.VelocityEngineImpl;
import com.alibaba.citrus.service.velocity.impl.VelocityEngineImpl.RuntimeServicesExposer;
import com.alibaba.citrus.service.velocity.support.RenderableHandler;

public class VelocityEngineTests extends AbstractVelocityEngineTests {
    @BeforeClass
    public static void initFactory() {
        factory = createFactory("services.xml");
    }

    @Test
    public void defaultSettings() {
        getEngine("default", factory);
        assertArrayEquals(new String[] { "vm" }, velocityEngine.getDefaultExtensions());

        assertEquals(24, velocityEngine.getConfiguration().getProperties().size());

        assertProperty("eventhandler.referenceinsertion.class", RuntimeServicesExposer.class.getName());

        assertProperty("input.encoding", "UTF-8");
        assertProperty("output.encoding", "UTF-8");
        assertProperty("parser.pool.size", "50");
        assertProperty("resource.manager.logwhenfound", "false");
        assertProperty("runtime.introspector.uberspect", CustomizedUberspectImpl.class.getName());
        assertProperty("runtime.log.logsystem", "Slf4jLogChute[" + VelocityEngine.class.getName() + "]");

        assertProperty("velocimacro.arguments.strict", "true");
        assertProperty("velocimacro.library.autoreload", "false");
        assertProperty("velocimacro.permissions.allow.inline.local.scope", "true");
        assertProperty("runtime.references.strict", "true");
        assertProperty("directive.set.null.allowed", "true");

        assertProperty("resource.loader", "spring"); // ����preloaded resources�����ڣ��ʲ�����preloaded

        assertProperty("spring.resource.loader.class", SpringResourceLoaderAdapter.class.getName());
        assertProperty("spring.resource.loader.description", "Spring Resource Loader Adapter");
        assertProperty("spring.resource.loader.modificationCheckInterval", "2");
        assertProperty("spring.resource.loader.path", "/templates");
        assertProperty("spring.resource.loader.cache", "true");

        assertProperty("preloaded.resource.loader.class", PreloadedResourceLoader.class.getName());
        assertProperty("preloaded.resource.loader.description", "Preloaded Resource Loader");
        assertProperty("preloaded.resource.loader.modificationCheckInterval", "2");
        assertProperty("preloaded.resource.loader.cache", "true");
        assertProperty("preloaded.resource.loader.resources", "{}");

        // δָ��macros����VM_global_library.vm������
        assertProperty("velocimacro.library", ""); // no macros
    }

    @Test
    public void defaultProductionMode() throws Exception {
        getEngine("default_productionMode", factory);

        assertTrue(velocityEngine.getConfiguration().isProductionMode());

        assertProperty("resource.loader", "spring"); // ����preloaded resources�����ڣ��ʲ�����preloaded

        // ������ģʽ�£�cacheǿ��Ϊtrue
        assertProperty("spring.resource.loader.cache", "true");
        assertProperty("preloaded.resource.loader.cache", "true");

        // ������ģʽ�£�autoreloadǿ��Ϊfalse
        assertProperty("velocimacro.library.autoreload", "false");
    }

    @Test
    public void defaultDevelopmentMode() throws Exception {
        getEngine("default_devMode", createFactory("services_dev.xml"));

        assertFalse(velocityEngine.getConfiguration().isProductionMode());

        assertProperty("resource.loader", "spring"); // ����preloaded resources�����ڣ��ʲ�����preloaded

        // �ڿ���ģʽ�£�cache������Ϊfalse
        assertProperty("spring.resource.loader.cache", "false");
        assertProperty("preloaded.resource.loader.cache", "false");

        // �ڿ���ģʽ�£�autoreloadǿ��Ϊtrue
        assertProperty("velocimacro.library.autoreload", "true");
    }

    @Test
    public void withArgs() {
        getEngine("with_args", factory);

        assertProperty("spring.resource.loader.modificationCheckInterval", "10");
        assertProperty("runtime.references.strict", "false");
        assertProperty("input.encoding", "ISO-8859-1");
    }

    @Test
    public void defaultMacros() throws Exception {
        getEngine("default_macros", factory);

        // δָ��macros����VM_global_library.vm����
        assertProperty("resource.loader", "spring"); // ����preloaded resources�����ڣ��ʲ�����preloaded
        assertProperty("velocimacro.library", "VM_global_library.vm"); // default macros

        // VM_global_library.vm�Ǵ�ResourceLoadingService��װ�صģ���ȡ��templateName������Ҫpreload
        Map<String, Resource> resources = getProperty("preloaded.resource.loader.resources");
        assertEquals(0, resources.size());

        // ����macro����Ⱦģ��
        String content = velocityEngine.mergeTemplate("test_macros.vm", new VelocityContext(), null);
        assertThat(content, containsAll("haha"));
    }

    @Test
    public void defaultMacros_preloaded() throws Exception {
        getEngine("default_macros", createFactory("services.xml", false));

        // δָ��macros����VM_global_library.vm����
        assertProperty("resource.loader", new String[] { "spring", "preloaded" });
        assertProperty("velocimacro.library", "globalVMs/VM_global_library.vm"); // default macros

        // ���preloaded resources
        Map<String, Resource> resources = getProperty("preloaded.resource.loader.resources");
        assertEquals(1, resources.size());
        assertEquals(new File(srcdir, "templates_with_macros/VM_global_library.vm"),
                resources.get("globalVMs/VM_global_library.vm").getFile());

        // ����macro����Ⱦģ��
        String content = velocityEngine.mergeTemplate("test_macros.vm", new VelocityContext(), null);
        assertThat(content, containsAll("haha"));
    }

    @Test
    public void macros() throws Exception {
        getEngine("with_macros", factory);

        // ָ��macros����VM_global_library.vm������
        assertProperty("resource.loader", "spring"); // ����preloaded resources�����ڣ��ʲ�����preloaded
        assertProperty("velocimacro.library", new String[] { "macros/hello.vm", "macros/inner/hello.vm",
                "macros/world.vm", "test2.vm" }, true);

        // ����macros�ļ����Ǵ�ResourceLoadingService��װ�صģ���ȡ��templateName������Ҫpreload
        Map<String, Resource> resources = getProperty("preloaded.resource.loader.resources");
        assertEquals(0, resources.size());

        // ����macro����Ⱦģ��
        String content = velocityEngine.mergeTemplate("test_macros.vm", new VelocityContext(), null);
        assertThat(content, containsAll("hello, world", "outterHello"));
    }

    @Test
    public void macros_preloaded() throws Exception {
        getEngine("with_macros", createFactory("services.xml", false));

        // ָ��macros����VM_global_library.vm������
        assertProperty("resource.loader", new String[] { "spring", "preloaded" });
        assertProperty("velocimacro.library", new String[] { "globalVMs/globalVM.vm", "globalVMs/hello.vm",
                "globalVMs/hello.vm1", "globalVMs/world.vm", }, true);

        // ���preloaded resources
        Map<String, Resource> resources = getProperty("preloaded.resource.loader.resources");
        assertEquals(4, resources.size());

        assertEquals(new File(srcdir, "templates/macros/inner/hello.vm"), resources.get("globalVMs/hello.vm").getFile());
        assertEquals(new File(srcdir, "templates/macros/hello.vm"), resources.get("globalVMs/hello.vm1").getFile());
        assertEquals(new File(srcdir, "templates/macros/world.vm"), resources.get("globalVMs/world.vm").getFile());

        // �޷�ȡ��URL���Ƶģ�ʹ��Ĭ�ϵ�ģ������
        try {
            resources.get("globalVMs/globalVM.vm").getURL();
            fail();
        } catch (IOException e) {
        }

        // ����macro����Ⱦģ��
        String content = velocityEngine.mergeTemplate("test_macros.vm", new VelocityContext(), null);
        assertThat(content, containsAll("hello, world", "outterHello"));
    }

    @Test
    public void emptyProperty() {
        try {
            createFactory("services_empty_property.xml");
            fail();
        } catch (FatalBeanException e) {
            assertThat(e, exception(IllegalArgumentException.class, "propertyName"));
        }
    }

    @Test
    public void advancedProperties() {
        getEngine("with_props", factory);

        // removed props
        assertProperty("input.encoding", "UTF-8");
        assertProperty("resource.loader", "spring"); // ����preloaded resources�����ڣ��ʲ�����preloaded
        assertNull(getProperty("file.resource.loader.class"));
        assertNull(getProperty("runtime.log"));
        assertProperty("runtime.log.logsystem", "Slf4jLogChute[" + VelocityEngine.class.getName() + "]");
        assertNull(getProperty("runtime.log.logsystem.class"));
        assertProperty("preloaded.resource.loader.resources", "{}");
        assertProperty("eventhandler.referenceinsertion.class",
                VelocityEngineImpl.RuntimeServicesExposer.class.getName()); // runtime services exposer for event cartridge
        assertProperty("velocimacro.library.autoreload", "false");
        assertProperty("runtime.references.strict", "true");

        // overrided props
        assertProperty("output.encoding", "ISO-8859-1");
        assertProperty("parser.pool.size", "100");
        assertProperty("resource.manager.logwhenfound", "true");
        assertProperty("runtime.introspector.uberspect", UberspectImpl.class.getName());
        assertProperty("velocimacro.arguments.strict", "false");
        assertProperty("velocimacro.permissions.allow.inline.local.scope", "false");

        // others
        assertProperty("empty.value", "");
        assertProperty("nonempty.value", "hello");
        assertProperty("eventhandler.escape.html.match", "/hello.*/");
    }

    @Test
    public void handlers() throws TemplateException, IOException {
        getEngine("with_handlers", factory);

        Context ctx = new VelocityContext();

        ctx.put("world", new MyWorld());

        String content = velocityEngine.mergeTemplate("test_handlers.vm", ctx, null);
        assertThat(content, containsAll("<hello name=\"&lt;world's best&gt;\"/>"));
    }

    @Test
    public void handlers_local() throws TemplateException, IOException {
        getEngine("with_local_handlers", factory);

        Iterator<?> i = velocityEngine.getConfiguration().getEventCartridge().getReferenceInsertionEventHandlers();
        assertThat(i.next(), instanceOf(RenderableHandler.class));
        ConditionalEscapeHandler h2 = (ConditionalEscapeHandler) i.next();
        assertFalse(i.hasNext());

        Context ctx = new VelocityContext();

        // no escape
        ctx.put("object", new MyRenderable());

        String content = velocityEngine.mergeTemplate("test_renderable.vm", ctx, null);
        assertThat(content, containsAll("from render()"));
        assertThat(content, not(containsString("escaped")));

        // escape
        ctx.put("escape", "true");

        content = velocityEngine.mergeTemplate("test_renderable.vm", ctx, null);
        assertThat(content, containsAll("escaped from render()"));

        // check handler
        ConditionalEscapeHandler new_h2 = ConditionalEscapeHandler.handlerHolder.get();
        ConditionalEscapeHandler.handlerHolder.remove();

        assertNotNull(new_h2);
        assertNotSame(h2, new_h2);
    }

    @Test
    public void plugins_noMacros() throws TemplateException, IOException {
        getEngine("with_plugins", factory);

        // δָ��macros����VM_global_library.vm������
        assertProperty("resource.loader", "spring"); // ����preloaded resources�����ڣ��ʲ�����preloaded
        assertProperty("velocimacro.library", ""); // default macros

        // û��preloaded resources
        Map<String, Resource> resources = getProperty("preloaded.resource.loader.resources");
        assertEquals(0, resources.size());
    }

    @Test
    public void plugins_withMacros() throws TemplateException, IOException {
        PluginDelegator.pluginHolder.set(new VelocityPlugin() {
            private VelocityConfiguration configuration;

            public void init(VelocityConfiguration configuration) throws Exception {
                this.configuration = configuration;
            }

            public Resource[] getMacros() throws IOException {
                ResourcePatternResolver resolver = (ResourcePatternResolver) configuration.getResourceLoader();
                String pattern = "classpath:" + PluginDelegator.class.getPackage().getName().replace('.', '/')
                        + "/*.vm";
                Resource[] resources = resolver.getResources(pattern);

                return resources;
            }
        });

        try {
            getEngine("with_plugins", createFactory("services.xml"));
        } finally {
            PluginDelegator.pluginHolder.remove();
        }

        // Plugin�ṩ��macros
        assertProperty("resource.loader", new String[] { "spring", "preloaded" });
        assertProperty("velocimacro.library",
                new String[] { "globalVMs/plugin_macro1.vm", "globalVMs/plugin_macro2.vm" }, true);

        // ����preloaded resources
        Map<String, Resource> resources = getProperty("preloaded.resource.loader.resources");
        assertEquals(2, resources.size());

        assertEquals("plugin_macro1.vm", resources.get("globalVMs/plugin_macro1.vm").getFilename());
        assertEquals("plugin_macro2.vm", resources.get("globalVMs/plugin_macro2.vm").getFilename());

        // ����macro����Ⱦģ��
        String content = velocityEngine.mergeTemplate("test_pluginMacros.vm", new VelocityContext(), null);
        assertThat(content, containsAll("macro1", "macro2"));
    }

    @Test
    public void render_byTemplateService() throws Exception {
        getEngine("templateService", factory);
        assertProperty("input.encoding", "GBK");
        assertProperty("output.encoding", "UTF-8");

        TemplateContext ctx = new MappedTemplateContext();
        ctx.put("world", "����");

        String content;

        // TemplateService.getText()
        content = templateService.getText("test_render.vm", ctx);
        assertContent(content);

        // TemplateService.writeTo(OutputStream)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        templateService.writeTo("test_render.vm", ctx, baos);

        content = new String(baos.toByteArray(), "UTF-8");
        assertContent(content);

        // TemplateService.writeTo(Writer)
        StringWriter sw = new StringWriter();
        templateService.writeTo("test_render.vm", ctx, sw);

        content = sw.toString();
        assertContent(content);
    }

    @Test
    public void render_error() throws Exception {
        getEngine("templateService", factory);
        assertProperty("input.encoding", "GBK");
        assertProperty("output.encoding", "UTF-8");

        TemplateContext ctx = new MappedTemplateContext();

        // δ�ҵ�ģ��
        try {
            templateService.getText("notExist.vm", ctx);
            fail();
        } catch (TemplateNotFoundException e) {
            assertThat(e, exception("Could not find template", "/notExist.vm"));
        }

        // ��strictģʽ�У�$worldû�ж���Ҳ�����
        try {
            templateService.getText("test_render.vm", ctx);
            fail();
        } catch (TemplateException e) {
            assertThat(
                    e,
                    exception(MethodInvocationException.class, "Error rendering Velocity template: /test_render.vm",
                            "Parameter 'world' not defined at /test_render.vm"));
        }

        // �﷨��
        try {
            templateService.getText("test_render_error.vm", ctx);
            fail();
        } catch (TemplateException e) {
            assertThat(e,
                    exception(ParseErrorException.class, "Error rendering Velocity template: /test_render_error.vm"));
        }
    }

    @Test
    public void render_directly_velocityContext() throws Exception {
        assertRenderDirectly(new VelocityContext());
    }

    @Test
    public void render_directly_customContext() throws Exception {
        assertRenderDirectly(new Context() {
            private final Map<String, Object> map = createHashMap();

            public boolean containsKey(Object key) {
                return map.containsKey(key);
            }

            public Object get(String key) {
                return map.get(key);
            }

            public Object[] getKeys() {
                return map.keySet().toArray();
            }

            public Object put(String key, Object value) {
                return map.put(key, value);
            }

            public Object remove(Object key) {
                return map.remove(key);
            }
        });
    }

    private void assertRenderDirectly(Context ctx) throws IOException, UnsupportedEncodingException {
        getEngine("templateService", createFactory("services_dev.xml"));

        assertFalse(velocityEngine.getConfiguration().isProductionMode());
        assertProperty("spring.resource.loader.cache", "false");

        assertProperty("input.encoding", "GBK");
        assertProperty("output.encoding", "UTF-8");

        ctx.put("world", "����");

        String content;

        // VelocityEngine.mergeTemplate(): String
        content = velocityEngine.mergeTemplate("test_render.vm", ctx, "GBK");
        assertContent(content);

        // Specific input charset encoding
        ctx.put("world", new String("����".getBytes("GBK"), "ISO-8859-1")); // hack value
        content = velocityEngine.mergeTemplate("test_render.vm", ctx, "ISO-8859-1");
        content = new String(content.getBytes("ISO-8859-1"), "GBK");
        assertContent(content);

        // VelocityEngine.mergeTemplate(OutputStream)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        velocityEngine.mergeTemplate("test_render.vm", ctx, baos, "ISO-8859-1", "ISO-8859-1");
        content = new String(baos.toByteArray(), "GBK");
        assertContent(content);

        // VelocityEngine.mergeTemplate(Writer)
        StringWriter sw = new StringWriter();
        velocityEngine.mergeTemplate("test_render.vm", ctx, sw, "ISO-8859-1");
        content = new String(sw.toString().getBytes("ISO-8859-1"), "GBK");
        assertContent(content);
    }

    @Test
    public void render_local_context() throws Exception {
        getEngine("templateService", factory);

        TemplateContext ctx = new MappedTemplateContext();

        String content = templateService.getText("test_local_context.vm", ctx);

        assertThat(content, containsString("hello"));
        assertTrue(ctx.containsKey("varInContext")); // ģ�������õı����ᱣ����context��
    }

    @Test
    public void render_set_null() throws Exception {
        getEngine("nostrict", factory);

        TemplateContext ctx = new MappedTemplateContext();

        String content = templateService.getText("test_set_null.vm", ctx);

        assertEquals("$a", content);
    }

    private void assertContent(String content) {
        assertThat(content, containsAll(//
                "�Ұ��������дʣ�", //
                "���д���̫������", //
                "ΰ���������дʣ�", //
                "����������ǰ����", //
                "hello, ����"));
    }

    @SuppressWarnings("unchecked")
    private <T> T getProperty(String key) {
        return (T) velocityEngine.getConfiguration().getProperties().get(key);
    }

    private void assertProperty(String key, Object[] value) {
        assertProperty(key, value, false);
    }

    private void assertProperty(String key, Object[] value, boolean sort) {
        Object[] array = ((List<?>) getProperty(key)).toArray(EMPTY_OBJECT_ARRAY);

        if (sort) {
            Arrays.sort(array);
        }

        assertArrayEquals((Object[]) value, array);
    }

    private void assertProperty(String key, Object value) {
        if (value instanceof String) {
            Object v = getProperty(key);
            assertEquals(value, String.valueOf(v));

            if (v instanceof String && !((String) v).contains("$")) {
                assertThat(velocityEngine.getConfiguration().toString(), containsRegex(key + "\\s+= " + value));
            }
        } else if (value instanceof Object[]) {
            assertProperty(key, (Object[]) value, false);
        } else {
            assertEquals(value, getProperty(key));
        }
    }

    public static class MyWorld {
        public String getName() {
            return "<world's best>";
        }
    }

    public static class MyRenderable implements Renderable {
        private final String content;

        public MyRenderable() {
            this("from render()");
        }

        public MyRenderable(String content) {
            this.content = content;
        }

        public String render() {
            return content;
        }

        @Override
        public String toString() {
            return "from toString()";
        }
    }

    public static interface MyProxy {
        Object getInstance();
    }

    public static class Counter implements Renderable {
        private int count = 1;

        public String render() {
            return String.valueOf(count++);
        }
    }
}
