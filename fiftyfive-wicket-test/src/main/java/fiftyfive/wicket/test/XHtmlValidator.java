/**
 * Copyright 2013 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import fiftyfive.wicket.test.dtd.XHtmlEntityResolver;

/**
 * Utility for parsing XHTML documents and verifying that they are valid
 * according to the DTD.
 * You should not have to use this class directly; it will be auto-selected
 * by helper methods in {@link WicketTestUtils}.
 */
public class XHtmlValidator extends AbstractDocumentValidator
{
    /**
     * Constructs and returns a standard Java XML DocumentBuilder with
     * validation enabled. Uses
     * {@link XHtmlEntityResolver XHtmlEntityResolver} to resolve DTDs
     * locally for best performance.
     */
    protected DocumentBuilder builder()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(this);
            builder.setEntityResolver(new XHtmlEntityResolver());
            return builder;
        }
        catch(ParserConfigurationException pce)
        {
            throw new RuntimeException(pce);
        }
    }
}
