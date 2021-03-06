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
package com.alibaba.citrus.service.upload;

import static com.alibaba.citrus.test.TestEnvStatic.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.FileSystemResource;

import com.alibaba.citrus.service.upload.impl.cfu.DiskFileItem;
import com.alibaba.citrus.springext.support.context.XmlBeanFactory;
import com.alibaba.citrus.util.io.StreamUtil;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import com.meterware.servletunit.UploadServletRunner;

/**
 * ????<code>UploadService</code>??
 * 
 * @author Michael Zhou
 */
public class UploadServiceTests {
    private static File ??????????;
    private static BeanFactory factory;
    private UploadService upload;
    private ServletUnitClient client;
    private HttpServletRequest request;

    @BeforeClass
    public static void initFactory() throws Exception {
        factory = new XmlBeanFactory(new FileSystemResource(new File(srcdir, "services.xml")));

        // ????????????????.txt??
        ?????????? = new File(destdir, "??????????.txt");
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(??????????), "GBK"), true);

        for (int i = 0; i < 16; i++) {
            out.println("??????????????");
        }

        out.flush();
        out.close();
    }

    @Before
    public void init() throws Exception {
        upload = (UploadService) factory.getBean("uploadService");

        // Servlet container
        ServletRunner servletRunner = new FilterServletRunner();

        servletRunner.registerServlet("myservlet", MyServlet.class.getName());

        // Servlet client
        client = servletRunner.newClient();

        // ????????????form.html
        WebResponse response = client.getResponse(new GetMethodWebRequest("http://localhost/myservlet"));

        WebForm form = response.getFormWithName("myform");

        // ????????form??request
        WebRequest request = form.getRequest();

        request.setParameter("myparam", "??????????????");
        request.selectFile("myfile", new File(srcdir, "smallfile.txt"));

        File nonAsciiFile = ??????????;

        if (nonAsciiFile.exists()) {
            request.selectFile("myfile_????", nonAsciiFile);
        } else {
            fail("Could not find non-ascii filename: " + nonAsciiFile.getAbsolutePath()
                    + ".  Please make sure the OS charset is correctly set.");
        }

        InvocationContext invocationContext = client.newInvocation(request);

        this.request = invocationContext.getRequest();

        // ??????????content type??text/html; charset=UTF-8??
        // ??????????UTF-8????????request??
        this.request.setCharacterEncoding("UTF-8");
    }

    @Test
    public void isMultipartContent() throws Exception {
        assertTrue(upload.isMultipartContent(request));

        // ????????????????????????????application/x-www-form-urlencoded????????
        WebRequest request = new GetMethodWebRequest("http://localhost/myservlet");
        InvocationContext invocationContext = client.newInvocation(request);

        assertFalse(upload.isMultipartContent(invocationContext.getRequest()));
    }

    @Test
    public void upload() throws Exception {
        FileItem[] items = upload.parseRequest(request);

        assertEquals(4, items.length);

        // ????????????????form.html????field??????????
        // ????????????<input type="text" name="myparam"/>
        assertEquals("myparam", items[0].getFieldName());
        assertNull(items[0].getName());
        assertTrue(items[0].isFormField());
        assertTrue(items[0].isInMemory());
        assertEquals("??????????????", items[0].getString()); // ??????UTF-8????

        // ????????????<input type="file" name="myfile"/>
        assertEquals("myfile", items[1].getFieldName());
        assertEquals(new File(srcdir, "smallfile.txt"), new File(items[1].getName()));
        assertFalse(items[1].isFormField());

        // ????file????????????????UTF-8??????????????8859_1
        assertEquals(new String("??????????????".getBytes("GBK"), "8859_1"), items[1].getString());
        assertEquals("??????????????", items[1].getString("GBK"));

        // ????????????????????????????
        assertTrue(items[1].isInMemory());

        // ????????????<input type="file" name="myfile_????"/>
        assertEquals("myfile_????", items[2].getFieldName()); // ??????UTF-8????header
        assertEquals(??????????, new File(items[2].getName())); // ??????UTF-8????header
        assertFalse(items[2].isFormField());

        // ????????????????????????????
        assertTrue(items[2].getSize() > 100);
        assertFalse(items[2].isInMemory());

        // ????????????<input type="submit" name="submit" value="upload"/>
        assertEquals("submit", items[3].getFieldName());
        assertNull(items[3].getName());
        assertTrue(items[3].isFormField());
        assertTrue(items[3].isInMemory());
        assertEquals("upload", items[3].getString()); // ??????UTF-8????
    }

    @Test
    public void uploadOverrideRepository() throws Exception {
        File repositoryPath = new File(destdir, "hello");
        UploadParameters params = new UploadParameters();

        params.setRepository(repositoryPath);
        params.setSizeThreshold(0); // ????????????

        FileItem[] items = upload.parseRequest(request, params);

        assertEquals(4, items.length);

        File storeLocation = ((DiskFileItem) items[1]).getStoreLocation();

        assertEquals(repositoryPath, storeLocation.getParentFile());
    }

    @Test(expected = UploadSizeLimitExceededException.class)
    public void uploadOverrideSizeMax() {
        UploadParameters params = new UploadParameters();

        params.setSizeMax(1);

        upload.parseRequest(request, params);
    }

    @Test(expected = UploadSizeLimitExceededException.class)
    public void uploadOverrideFileSizeMax() {
        UploadParameters params = new UploadParameters();

        params.setSizeMax(1000000);
        params.setFileSizeMax(1);

        upload.parseRequest(request, params);
    }

    @Test
    public void uploadOverrideThreshold() throws Exception {
        UploadParameters params = new UploadParameters();

        params.setSizeThreshold(0);

        FileItem[] items = upload.parseRequest(request, params);

        assertEquals(4, items.length);

        // ????????????????form.html????field??????????
        // ????????????<input type="text" name="myparam"/>
        assertEquals("myparam", items[0].getFieldName());
        assertNull(items[0].getName());
        assertTrue(items[0].isFormField());
        assertTrue(items[0].isInMemory()); // ??threshold??0????form field????????????
        assertEquals("??????????????", items[0].getString()); // ??????UTF-8????

        // ????????????<input type="file" name="myfile"/>
        assertEquals("myfile", items[1].getFieldName());
        assertEquals(new File(srcdir, "smallfile.txt"), new File(items[1].getName()));
        assertFalse(items[1].isFormField());

        // ????file????????????????UTF-8??????????????8859_1
        assertEquals(new String("??????????????".getBytes("GBK"), "8859_1"), items[1].getString());
        assertEquals("??????????????", items[1].getString("GBK"));

        // ????threshold??0, ????????????????????????????????
        assertFalse(items[1].isInMemory());

        // ????????????<input type="file" name="myfile_????"/>
        assertEquals("myfile_????", items[2].getFieldName()); // ??????UTF-8????header
        assertEquals(??????????, new File(items[2].getName())); // ??????UTF-8????header
        assertFalse(items[2].isFormField());

        // file????????????????
        assertTrue(items[2].getSize() > 100);
        assertFalse(items[2].isInMemory());

        // ????????????<input type="submit" name="submit" value="upload"/>
        assertEquals("submit", items[3].getFieldName());
        assertNull(items[3].getName());
        assertTrue(items[3].isFormField());
        assertTrue(items[3].isInMemory()); // form field????????????
        assertEquals("upload", items[3].getString()); // ??????UTF-8????
    }

    @Test
    public void fullConfig() {
        upload = (UploadService) factory.getBean("upload2");

        assertEquals(new File("/tmp/upload").toURI().toString(), upload.getRepository().toURI().toString());
        assertEquals("100", upload.getSizeMax().toString());
        assertEquals("200", upload.getFileSizeMax().toString());
        assertEquals("300", upload.getSizeThreshold().toString());
        assertEquals(true, upload.isKeepFormFieldInMemory());
        assertArrayEquals(new String[] { "filename", "fname" }, upload.getFileNameKey());
    }

    @Test
    public void toString_() {
        FileItem[] items = upload.parseRequest(request);

        assertEquals(4, items.length);

        // ????????????????form.html????field??????????
        // ????????????<input type="text" name="myparam"/>
        assertEquals("??????????????", items[0].toString());

        // ????????????<input type="file" name="myfile"/>
        assertEquals(new File(srcdir, "smallfile.txt").getAbsolutePath(), items[1].toString());

        // ????????????<input type="file" name="myfile_????"/>
        assertEquals(??????????.getAbsolutePath(), items[2].toString());

        // ????????????<input type="submit" name="submit" value="upload"/>
        assertEquals("upload", items[3].toString());
    }

    /**
     * ????httpunit??????request content??
     * <ul>
     * <li>??????Content-Type header??????????????????????????????????</li>
     * <li>??filename="...\\..."??????????????????????????????????????????????????????</li>
     * </ul>
     */
    private static final class FilterServletRunner extends UploadServletRunner {
        @Override
        protected byte[] filter(WebRequest request, byte[] messageBody) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                BufferedReader reader = new BufferedReader(new StringReader(new String(messageBody, "ISO-8859-1")));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, "ISO-8859-1"), true);
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Content-Type: text/plain; charset=")) {
                        continue;
                    }

                    if (line.indexOf("filename=") > 0) {
                        line = line.replaceAll("\\\\+", "\\\\");
                    }

                    writer.printf("%s\r\n", line); // ????????????platform-specific??????
                }

                writer.flush();
            } catch (IOException e) {
                fail(e.getMessage());
            }

            return baos.toByteArray();
        }
    }

    public static class MyServlet extends HttpServlet {
        private static final long serialVersionUID = 3258413932522648633L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                IOException {
            response.setContentType("text/html; charset=UTF-8");

            PrintWriter out = response.getWriter();

            String html = StreamUtil.readText(new FileInputStream(new File(srcdir, "form.html")), "GBK", true);

            out.println(html);
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                IOException {
            doGet(request, response);
        }
    }
}
