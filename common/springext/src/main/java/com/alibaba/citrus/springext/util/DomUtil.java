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
package com.alibaba.citrus.springext.util;

import static com.alibaba.citrus.util.Assert.*;
import static com.alibaba.citrus.util.CollectionUtil.*;
import static com.alibaba.citrus.util.ObjectUtil.*;
import static com.alibaba.citrus.util.StringUtil.*;
import static org.springframework.beans.factory.xml.BeanDefinitionParserDelegate.*;

import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import com.alibaba.citrus.util.Assert;

/**
 * �������dom�Ĺ��ߡ�
 * 
 * @author Michael Zhou
 */
public class DomUtil {
    /**
     * ��W3C elementת����DOM4j element��
     */
    public static org.dom4j.Element convertElement(Element element) {
        Document doc;

        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException("Failed to create dom4j document", e);
        }

        Element clonedElement = (Element) doc.importNode(element, true);

        doc.appendChild(clonedElement);

        DOMReader reader = new DOMReader();
        org.dom4j.Document dom4jDoc = reader.read(doc);

        return dom4jDoc.getRootElement();
    }

    /**
     * ��W3C elementת����SAX�¼���
     */
    public static void convertElement(Element element, ContentHandler contentHandler) throws SAXException {
        SAXWriter writer = new SAXWriter(contentHandler);

        if (contentHandler instanceof ErrorHandler) {
            writer.setErrorHandler((ErrorHandler) contentHandler);
        }

        if (contentHandler instanceof LexicalHandler) {
            writer.setLexicalHandler((LexicalHandler) contentHandler);
        }

        writer.write(convertElement(element));
    }

    /**
     * ȡ��������elements��
     */
    public static List<Element> subElements(Element element) {
        return subElements(element, null);
    }

    /**
     * ȡ��������elements�����δָ��selector���򷵻�����elements��
     */
    public static List<Element> subElements(Element element, ElementSelector selector) {
        NodeList nodes = element.getChildNodes();
        List<Element> subElements = createArrayList(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node instanceof Element) {
                Element subElement = (Element) node;

                if (selector == null || selector.accept(subElement)) {
                    subElements.add(subElement);
                }
            }
        }

        return subElements;
    }

    public static Element theOnlySubElement(Element element, ElementSelector selector) {
        List<Element> subElements = subElements(element, selector);

        switch (subElements.size()) {
            case 1:
                return subElements.get(0);

            case 0:
                return null;

            default:
                fail("too more sub-elements of %s", element.getNodeName());
                return null;
        }
    }

    /**
     * Elementѡ������
     */
    public static interface ElementSelector {
        boolean accept(Element element);
    }

    /**
     * ���˳�ָ��namespace�µ�elements��
     */
    public static ElementSelector sameNs(Element element) {
        return ns(assertNotNull(element, "element").getNamespaceURI());
    }

    /**
     * ���˳�Ĭ��namespace����beans���ֿռ��µ�elements��
     */
    public static ElementSelector beansNs() {
        return ns(BEANS_NAMESPACE_URI);
    }

    /**
     * ƥ������element��
     */
    public static ElementSelector any() {
        return new ElementSelector() {
            public boolean accept(Element element) {
                return true;
            }

            @Override
            public String toString() {
                return "any";
            }
        };
    }

    /**
     * ��ƥ������element��
     */
    public static ElementSelector none() {
        return new ElementSelector() {
            public boolean accept(Element element) {
                return false;
            }

            @Override
            public String toString() {
                return "none";
            }
        };
    }

    /**
     * ���˳�ָ��namespace�µ�elements��
     */
    public static ElementSelector ns(String nsUri) {
        final String trimmedNsUri = trimToNull(nsUri);

        return new ElementSelector() {
            public boolean accept(Element element) {
                return isEquals(element.getNamespaceURI(), trimmedNsUri);
            }

            @Override
            public String toString() {
                return "ns[" + (trimmedNsUri == null ? "no namespace" : trimmedNsUri) + "]";
            }
        };
    }

    /**
     * ����ָ�����Ƶ�elements��
     */
    public static ElementSelector name(String name) {
        final String trimmedName = assertNotNull(trimToNull(name), "elementName");

        return new ElementSelector() {
            public boolean accept(Element element) {
                String name = element.getNamespaceURI() == null ? element.getNodeName() : element.getLocalName();
                return isEquals(name, trimmedName);
            }

            @Override
            public String toString() {
                return "name[" + trimmedName + "]";
            }
        };
    }

    /**
     * ��������һ��selector������������֮��
     */
    public static ElementSelector or(final ElementSelector... selectors) {
        return new ElementSelector() {
            public boolean accept(Element element) {
                if (selectors != null) {
                    for (ElementSelector selector : selectors) {
                        if (selector.accept(element)) {
                            return true;
                        }
                    }

                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public String toString() {
                return "or" + asList(selectors);
            }
        };
    }

    /**
     * ��������selector�������Ž���֮��
     */
    public static ElementSelector and(final ElementSelector... selectors) {
        return new ElementSelector() {
            public boolean accept(Element element) {
                if (selectors != null) {
                    for (ElementSelector selector : selectors) {
                        if (!selector.accept(element)) {
                            return false;
                        }
                    }
                }

                return true;
            }

            @Override
            public String toString() {
                return "and" + asList(selectors);
            }
        };
    }

    /**
     * ������selector�������Ž���֮��
     */
    public static ElementSelector not(final ElementSelector selector) {
        assertNotNull(selector, "selector");
        return new ElementSelector() {
            public boolean accept(Element element) {
                return !selector.accept(element);
            }

            @Override
            public String toString() {
                return "not[" + selector + "]";
            }
        };
    }

    /**
     * �׳��쳣��ͨ����or�Ӿ���ϡ�
     */
    public static ElementSelector error() {
        return error(null, null);
    }

    /**
     * �׳��쳣��ͨ����or�Ӿ���ϡ�
     */
    public static ElementSelector error(final Assert.ExceptionType type, final String message) {
        return new ElementSelector() {
            public boolean accept(Element element) {
                assertTrue(false, type, defaultIfEmpty(message, "Unexpected element: " + element.getNodeName()));
                return false;
            }

            @Override
            public String toString() {
                return "error";
            }
        };
    }
}
