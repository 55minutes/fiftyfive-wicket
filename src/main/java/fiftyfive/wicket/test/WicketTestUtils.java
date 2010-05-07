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
package fiftyfive.wicket.test;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.xpath.XPathExpressionException;
import static javax.xml.xpath.XPathConstants.NODESET;
import static javax.xml.xpath.XPathConstants.STRING;

import fiftyfive.wicket.util.XPathHelper;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.tester.WicketTesterHelper;
import org.junit.Assert;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper functions and assertions for easier testing of Wicket pages
 * and components.
 */
public abstract class WicketTestUtils
{
    /**
     * Asserts that an XPath expression can be found in the most recently
     * rendered Wicket page.
     */
    public static void assertXPath(WicketTester wt, String expr)
            throws IOException, SAXException, XPathExpressionException
    {
        if(matchCount(wt, expr) == 0)
        {
            Assert.fail(String.format(
                "XPath expression [%s] could not be found in document:%n%s",
                expr,
                document(wt)
            ));
        }
    }

    /**
     * Asserts that exactly {@code count} number of instances of a given
     * XPath expression can be found in the most recently
     * rendered Wicket page.
     */
    public static void assertXPath(int count, WicketTester wt, String expr)
            throws IOException, SAXException, XPathExpressionException
    {
        // First make sure the expression exists at all
        if(count > 0)
        {
            assertXPath(wt, expr);
        }
        
        // Then do a more exact check
        final int matches = matchCount(wt, expr);
        if(matches != count)
        {
            String s = 1 == count ? "" : "s";
            Assert.fail(String.format(
                "Expected %d occurance%s of XPath expression [%s], but " +
                "found %d in document:%n%s",
                count,
                s,
                expr,
                matches,
                document(wt)
            ));
        }
    }
    
    /**
     * Assert that the last rendered page has a content-type of text/html
     * and is valid markup. Will autodetect whether the document is HTML5 or
     * XHTML and use the appropriate validator. An HTML5 document must start
     * with {@code <!DOCTYPE html>}, anything else is assumed to be XHTML.
     *
     * @param tester A WicketTester object that has just rendered a page.
     */
    public static void assertValidMarkup(WicketTester tester) 
        throws IOException
    {
        assertValidMarkup(
            tester, AbstractDocumentValidator.DEF_NUM_LINES_CONTEXT
        );
    }

    /**
     * Assert that the last rendered page has a content-type of text/html
     * and is valid markup. Will autodetect whether the document is HTML5 or
     * XHTML and use the appropriate validator. An HTML5 document must start
     * with {@code <!DOCTYPE html>}, anything else is assumed to be XHTML.
     *
     * @param tester A WicketTester object that has just rendered a page.
     * @param linesContext The number of lines of context to include around
     *                     each validation error.
     */
    public static void assertValidMarkup(WicketTester tester, int linesContext) 
        throws IOException
    {
        String type = tester.getServletResponse().getContentType();
        Assert.assertTrue(
            type.equals("text/html") ||
            type.startsWith("text/html;")
        );
        
        String document = document(tester);
        AbstractDocumentValidator validator = new XHtmlValidator();
        if(Html5Validator.hasHtml5Doctype(document))
        {
            validator = new Html5Validator();
        }
        validator.setNumLinesContext(linesContext);
        validator.parse(document);
        
        if(!validator.isValid())
        {
            Assert.fail(String.format(
                "Invalid XHTML:%n%s",
                WicketTesterHelper.asLined(validator.getErrors())
            ));
        }
    }
    
    /**
     * Renders a component using a snippet of markup. Example:
     * <pre>
     * startComponentWithMarkup(
     *     tester,
     *     new Label("label", "Hello, world!),
     *     "&lt;span wicket:id=\"label\"&gt;replaced by Wicket&lt;/span&gt;"
     * );
     * </pre>
     * This method will place the component in a simple Page and render it
     * using the normal WicketTester request/response. In the above example,
     * the rendered output will be:
     * <pre>
     * &lt;html&gt;
     * &lt;head&gt;
     * &lt;/head&gt;
     * &lt;body&gt;
     * &lt;span wicket:id=&quot;label&quot;&gt;Hello, world!&lt;/span&gt;
     * &lt;/body&gt;
     * &lt;/html&gt;
     * </pre>
     * You can then use helper method like
     * {@link #assertXPath(WicketTester,String) assertXPath} or
     * {@link WicketTester#assertContains(String)}
     * to verify the component rendered as expected.
     */
    public static void startComponentWithMarkup(WicketTester tester,
                                                Component c,
                                                final String markup)
    {
        WebPage page = new PageWithInlineMarkup(String.format(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " +
            "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">%n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
            "xml:lang=\"en\" lang=\"en\">%n" +
            "<head>%n  <title>untitled</title>%n</head>%n" +
            "<body>%n%s%n</body>%n</html>",
            markup
        ));
        page.add(c);
        tester.startPage(page);
    }
    
    private static String document(WicketTester tester)
    {
        return tester.getServletResponse().getDocument();
    }
    
    private static int matchCount(WicketTester tester, String xPathExpr)
            throws IOException, SAXException, XPathExpressionException
    {
        NodeList nl = XPathHelper.parse(new StringReader(document(tester)))
                                 .findNodes(xPathExpr);
                                 
        return nl != null ? nl.getLength() : 0;
    }
}
