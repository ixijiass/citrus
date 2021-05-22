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
package com.alibaba.citrus.springext.export;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static com.alibaba.citrus.util.io.StreamUtil.*;
import static javax.xml.XMLConstants.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import com.alibaba.citrus.springext.DocumentFilter;
import com.alibaba.citrus.springext.Schema;
import com.alibaba.citrus.springext.Schemas;
import com.alibaba.citrus.springext.impl.ConfigurationPointsImpl;
import com.alibaba.citrus.springext.support.SchemaSet;
import com.alibaba.citrus.springext.support.resolver.SpringPluggableSchemas;

/**
 * װ�غͷ���schemas��������������������
 * 
 * @author Michael Zhou
 */
public class SchemaExporter {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final Entries entries;
    private final SchemaSet schemas;
    private final Map<String, Set<Schema>> nsToSchemas;

    public SchemaExporter() {
        this((ResourceLoader) null);
    }

    public SchemaExporter(ResourceLoader resourceLoader) {
        this(new ConfigurationPointsImpl(resourceLoader == null ? null : resourceLoader.getClassLoader()),
                new SpringPluggableSchemas(resourceLoader));
    }

    public SchemaExporter(Schemas... schemasList) {
        this.entries = new Entries();
        this.schemas = new SchemaSet(schemasList);
        this.nsToSchemas = createHashMap();

        // ��������ͬnamespace��schema����һ�𣬲������Ƶ����򣬼�����beans.xsd��beans-2.5.xsd��beans-2.0.xsd ˳��
        for (Schema schema : schemas.getNamedMappings().values()) {
            this.entries.put(schema.getName(), new Entry(schema.getName(), schema));

            String namespace = schema.getTargetNamespace();

            if (namespace != null) {
                Set<Schema> nsSchemas = nsToSchemas.get(namespace);

                if (nsSchemas == null) {
                    nsSchemas = createTreeSet(new Comparator<Schema>() {
                        public int compare(Schema o1, Schema o2) {
                            return o2.getName().compareTo(o1.getName());
                        }
                    });

                    nsToSchemas.put(namespace, nsSchemas);
                }

                nsSchemas.add(schema);
            }
        }
    }

    private Entries getEntries() {
        return entries;
    }

    public Entry getRootEntry() {
        return getEntries().getRoot();
    }

    public Entry getEntry(String path) {
        return getEntries().get(path);
    }

    public void writeTo(Writer out, Entry entry, String charset) throws IOException {
        writeTo(out, entry, charset, (String) null);
    }

    public void writeTo(Writer out, Entry entry, String charset, String uriPrefix) throws IOException {
        writeTo(out, entry, charset, uriPrefix == null ? null : new AddPrefixFilter(uriPrefix));
    }

    private void writeTo(Writer out, Entry entry, String charset, DocumentFilter filter) throws IOException {
        writeText(entry.getSchema().getText(charset, filter), out, true);
    }

    /**
     * ����һ��schema�ļ���㡣
     */
    public static final class Entry {
        private final String path;
        private final String name;
        private final boolean directory;
        private final boolean root;
        private final Schema schema;
        private final Map<String, Entry> subEntries;

        /**
         * ���������root entry��
         */
        private Entry() {
            this.path = "";
            this.name = "";
            this.directory = true;
            this.root = true;
            this.schema = null;
            this.subEntries = createTreeMap();
        }

        public Entry(String path) {
            this(path, null);
        }

        public Entry(String path, Schema schema) {
            this.path = assertNotNull(trimToNull(path), "path");
            this.directory = path.endsWith("/");

            int fromIndex = this.directory ? path.length() - 2 : path.length();

            this.name = path.substring(path.lastIndexOf("/", fromIndex) + 1);
            this.root = false;
            this.schema = schema;
            this.subEntries = createTreeMap();

            if (this.directory) {
                assertNull(schema, "schema");
            } else {
                assertNotNull(schema, "schema");
            }
        }

        public String getPath() {
            return path;
        }

        public String getId() {
            if (isRoot()) {
                return "ROOT";
            } else {
                return path.replaceFirst("\\.[^-\\./]*$|/+$", "").replace('/', '-').replace('.', '-');
            }
        }

        public String getName() {
            return name;
        }

        public boolean isDirectory() {
            return directory;
        }

        public boolean isRoot() {
            return root;
        }

        public Collection<Entry> getSubEntries() {
            return subEntries.values();
        }

        public Schema getSchema() {
            return schema;
        }

        public boolean containsSchemaWithTargetNamespace() {
            boolean hasNs = false;

            if (isDirectory()) {
                for (Entry subEntry : getSubEntries()) {
                    if (subEntry.containsSchemaWithTargetNamespace()) {
                        hasNs = true;
                        break;
                    }
                }
            } else if (getSchema() != null) {
                hasNs = getSchema().getTargetNamespace() != null;
            }

            return hasNs;
        }

        private final static String PREFIX_ITEM = "+---";
        private final static String PREFIX_LAST_ITEM = "\\---";
        private final static String INDENT_BEFORE_LAST_ITEM = "|   ";
        private final static String INDENT_AFTER_LAST_ITEM = "    ";

        public String tree() {
            StringWriter sw = new StringWriter();

            try {
                tree(sw);
            } catch (IOException e) {
                unexpectedException(e);
            }

            return sw.toString();
        }

        public void tree(Appendable buf) throws IOException {
            tree(buf, "");
        }

        public void tree(Appendable buf, String prefix) throws IOException {
            tree(buf, prefix, prefix);
        }

        private void tree(Appendable buf, String prefix, String indent) throws IOException {
            buf.append(prefix).append(getName()).append("\n");

            for (Iterator<Entry> i = subEntries.values().iterator(); i.hasNext();) {
                Entry subEntry = i.next();
                String subPrefix;
                String subIndent;

                if (i.hasNext()) {
                    subPrefix = indent + PREFIX_ITEM;
                    subIndent = indent + INDENT_BEFORE_LAST_ITEM;
                } else {
                    subPrefix = indent + PREFIX_LAST_ITEM;
                    subIndent = indent + INDENT_AFTER_LAST_ITEM;
                }

                subEntry.tree(buf, subPrefix, subIndent);
            }
        }

        /**
         * Files sort by name without extension, then directories.
         */
        private String getSortKey() {
            String name = getName();

            if (!isDirectory()) {
                int slashIndex = name.lastIndexOf("/");
                int dotIndex = name.lastIndexOf(".");

                if (slashIndex < dotIndex && dotIndex >= 0) {
                    return "0-" + name.substring(0, dotIndex);
                }

                return "0-" + name;
            }

            return "1-" + name;
        }

        @Override
        public String toString() {
            return root ? "/" : path;
        }
    }

    private final class Entries extends HashMap<String, Entry> {
        private static final long serialVersionUID = -4000525580274040823L;

        public Entries() {
            super.put("", new Entry());
        }

        public Entry getRoot() {
            return get("");
        }

        @Override
        public Entry put(String path, Entry entry) {
            assertTrue(path.equals(entry.getPath()));

            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            String parentPath = path.substring(0, path.lastIndexOf("/") + 1);
            Entry parentEntry = get(parentPath);

            if (parentEntry == null) {
                parentEntry = new Entry(parentPath);
                this.put(parentPath, parentEntry); // recursively
            }

            parentEntry.subEntries.put(entry.getSortKey(), entry);

            Entry old = super.put(entry.getPath(), entry);

            log.trace("Added entry: {}", entry.getPath());

            return old;
        }
    }

    /**
     * �����п�ʶ���URI�ϣ�����ָ��ǰ׺��
     */
    private class AddPrefixFilter implements DocumentFilter {
        private final String prefix;

        public AddPrefixFilter(String prefix) {
            if (prefix != null) {
                if (!prefix.endsWith("/")) {
                    prefix += "/";
                }
            }

            this.prefix = prefix;
        }

        public Document filter(Document doc, String systemId) {
            if (prefix != null) {
                Element root = doc.getRootElement();

                // <xsd:schema>
                if (W3C_XML_SCHEMA_NS_URI.equals(root.getNamespaceURI()) && "schema".equals(root.getName())) {
                    Namespace xsd = DocumentHelper.createNamespace("xsd", W3C_XML_SCHEMA_NS_URI);
                    QName includeName = DocumentHelper.createQName("include", xsd);
                    QName importName = DocumentHelper.createQName("import", xsd);

                    // for each <xsd:include>
                    for (Iterator<?> i = root.elementIterator(includeName); i.hasNext();) {
                        Element includeElement = (Element) i.next();
                        String schemaLocation = trimToNull(includeElement.attributeValue("schemaLocation"));

                        if (schemaLocation != null) {
                            schemaLocation = getNewSchemaLocation(schemaLocation, null, systemId);

                            if (schemaLocation != null) {
                                includeElement.addAttribute("schemaLocation", schemaLocation);
                            }
                        }
                    }

                    // for each <xsd:import> 
                    for (Iterator<?> i = root.elementIterator(importName); i.hasNext();) {
                        Element importElement = (Element) i.next();
                        String schemaLocation = importElement.attributeValue("schemaLocation");
                        String namespace = trimToNull(importElement.attributeValue("namespace"));

                        if (schemaLocation != null || namespace != null) {
                            schemaLocation = getNewSchemaLocation(schemaLocation, namespace, systemId);

                            if (schemaLocation != null) {
                                importElement.addAttribute("schemaLocation", schemaLocation);
                            }
                        }
                    }
                }
            }

            return doc;
        }

        private String getNewSchemaLocation(String schemaLocation, String namespace, String systemId) {
            // ����ָ����schemaLocation�ж�
            if (schemaLocation != null) {
                Schema schema = schemas.findSchema(schemaLocation);

                if (schema != null) {
                    return prefix + schema.getName();
                } else {
                    return schemaLocation; // ����ԭ����location���������Ǵ���ģ�
                }
            }

            // �ٸ���namespace�ж�
            if (namespace != null) {
                Set<Schema> nsSchemas = nsToSchemas.get(namespace);

                if (nsSchemas != null && !nsSchemas.isEmpty()) {
                    // ���ȣ���������ͬns��schema�в��Ұ汾��ͬ��schema��
                    String versionedExtension = getVersionedExtension(systemId);

                    if (versionedExtension != null) {
                        for (Schema schema : nsSchemas) {
                            if (schema.getName().endsWith(versionedExtension)) {
                                return prefix + schema.getName();
                            }
                        }
                    }

                    // ��Σ�ѡ���һ��Ĭ�ϵ�schema����˳���ǣ�beans.xsd��beans-2.5.xsd��beans-2.0.xsd
                    return prefix + nsSchemas.iterator().next().getName();
                }
            }

            return null;
        }

        /**
         * ����spring-aop-2.5.xsdȡ��-2.5.xsd��
         */
        private String getVersionedExtension(String systemId) {
            if (systemId != null) {
                int dashIndex = systemId.lastIndexOf("-");
                int slashIndex = systemId.lastIndexOf("/");

                if (dashIndex > slashIndex) {
                    return systemId.substring(dashIndex);
                }
            }

            return null;
        }
    }
}
