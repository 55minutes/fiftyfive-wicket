/*
 * Copyright 2010 55 Minutes (http://www.55minutes.com)
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
 */
package fiftyfive.wicket.util;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import static javax.xml.xpath.XPathConstants.NODESET;
import static javax.xml.xpath.XPathConstants.STRING;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// TODO: add test coverage!

/**
 * Simplifies access to the Java XPath API.
 * <pre>
 * File file = new File("test.xml");
 * String name = XPathHelper.parse(file).findString("//name/text()");
 * </pre>
 */
public class XPathHelper
{
    /**
     * Creates an XPathHelper by parsing the specified XML file.
     */
    public static XPathHelper parse(File file)
            throws IOException, SAXException
    {
        return new XPathHelper(newBuilder().parse(file));
    }
    
    /**
     * Creates an XPathHelper by parsing the specified Reader.
     */
    public static XPathHelper parse(Reader xml)
            throws IOException, SAXException
    {
        return parse(new InputSource(xml));
    }
    
    /**
     * Creates an XPathHelper by parsing the specified input source.
     */
    public static XPathHelper parse(InputSource is)
            throws IOException, SAXException
    {
        return new XPathHelper(newBuilder().parse(is));
    }
    
    /**
     * Boilerplate code for creating Java's XML DOM parser.
     */
    private static DocumentBuilder newBuilder()
    {
        try
        {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            return fac.newDocumentBuilder();
        }
        catch(ParserConfigurationException pce)
        {
            throw new RuntimeException(pce);
        }
    }
    
    private Document _document;
    
    /**
     * Creates an XPathHelper from a DOM that has already been parsed.
     * It is usually more convenient to use the static {@code parse} methods.
     * @see #parse(File)
     * @see #parse(InputSource)
     */
    public XPathHelper(Document doc)
    {
        _document = doc;
    }
    
    /**
     * Evaluates the given xpath expression and returns the result as a String.
     */
    public String findString(String expr)
            throws XPathExpressionException
    {
        return (String) evaluateXPath(expr, STRING);
    }
    
    /**
     * Evaluates the given xpath expression and returns the result as a
     * List of Strings.
     */
    public List<String> findStrings(String expr)
            throws XPathExpressionException
    {
        List<String> strings = new ArrayList<String>();
        
        NodeList nodes = findNodes(expr);
        for(int i=0; i<nodes.getLength(); i++)
        {
            strings.add(nodes.item(i).getNodeValue());
        }
        return strings;
    }
    
    /**
     * Evaluates the given xpath expression and returns the result as a
     * NodeList.
     */
    public NodeList findNodes(String expr)
            throws XPathExpressionException
    {
        return (NodeList) evaluateXPath(expr, NODESET);
    }

    /**
     * Evaluates the given xpath expression and returns the result as the
     * specified type.
     * @see XPathConstants
     */
    private Object evaluateXPath(String expr, QName type)
            throws XPathExpressionException
    {
        XPathFactory factory = XPathFactory.newInstance();
        return factory.newXPath().evaluate(expr, _document, type);
    }
}
