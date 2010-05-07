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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import fiftyfive.wicket.test.dtd.XHtmlEntityResolver;
import org.apache.wicket.util.tester.WicketTester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Utility for parsing XHTML documents and verifying that they are valid
 * according to the DTD. See {@link WicketTestUtils} for typical usage.
 */
public class XHtmlValidator implements ErrorHandler
{
    static final int DEF_NUM_LINES_CONTEXT = 10;

    /**
     * @deprecated Use {@link WicketTestUtils#assertValidMarkup(WicketTester)}
     */
    public static void assertValidMarkup(WicketTester tester) 
        throws IOException
    {
        WicketTestUtils.assertValidMarkup(tester);
    }

    /**
     * @deprecated Use {@link WicketTestUtils#assertValidMarkup(WicketTester,int)}
     */
    public static void assertValidMarkup(WicketTester tester, int linesContext) 
        throws IOException
    {
        WicketTestUtils.assertValidMarkup(tester, linesContext);
    }
    
    
    private int     _numLinesContext = DEF_NUM_LINES_CONTEXT;
    private boolean _parsed = false;
    private String[] _lines = null;
    private List<String> _errors = new ArrayList();
    
    /**
     * Sets the number of lines of context that will be listed along with
     * the line that has the validation error. Must be positive.
     * The default is 10.
     */
    public void setNumLinesContext(int newNumLinesContext)
    {
        if(newNumLinesContext < 0)
        {
            throw new IllegalArgumentException(
                "numLinesContext cannot be zero or negative"
            );
        }
        _numLinesContext = newNumLinesContext;
    }
    
    /**
     * Parses an XHTML document provided as a String. To test whether the
     * document was parsed successfully and is valid XHTML, subsequently call
     * {@link #isValid}.
     */
    public void parse(String xhtmlContent) throws IOException
    {
        try
        {
            _lines = xhtmlContent.split("\\r?\\n");
            builder().parse(new InputSource(new StringReader(xhtmlContent)));
        }
        catch(SAXException saxe)
        {
            // ignore
        }
    }
    
    /**
     * Returns <code>true</code> if the previous call to {@link #parse}
     * completed without errors or warnings.
     */
    public boolean isValid()
    {
        return _errors.size() == 0;
    }
    
    /**
     * Returns the errors and warnings that were encountered in the previous
     * call to {@link #parse}, if any.
     */
    public List<String> getErrors()
    {
        return Collections.unmodifiableList(_errors);
    }
    
    
    // ==== ErrorHandler callbacks===========================================
    
    public void error(SAXParseException exception)
    {
        _errors.add(format(exception));
    }
    
    public void fatalError(SAXParseException exception)
    {
        _errors.add(format(exception));
    }
    
    public void warning(SAXParseException exception)
    {
        _errors.add(format(exception));
    }

    // ==== End ErrorHandler callbacks ======================================
    
    /**
     * Construct a validating DocumentBuilder with <code>this</code>
     * registered as the error handler. May only be called once.
     */
    private DocumentBuilder builder()
    {
        if(_parsed)
        {
            throw new IllegalStateException(
                "XHtmlValidator.parse() can only be used once. " +
                "Create a new instance for each document you wish to parse."
            );
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        
        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(this);
            builder.setEntityResolver(new XHtmlEntityResolver());
            _parsed = true;
            return builder;
        }
        catch(ParserConfigurationException pce)
        {
            throw new RuntimeException(pce);
        }
    }
    
    /**
     * Formats a SAXParseException as a string: error message, followed
     * by five lines of the markup centered around the line where the error
     * occurs.
     */
    private String format(SAXParseException ex)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(ex.getMessage());
        buf.append("\n");
        
        final int ctx = _numLinesContext;
        for(int offset = -1 * ctx/2; offset <= ctx/2; offset++)
        {
            int line = ex.getLineNumber() + offset;
            if(line >= 1 && line <= _lines.length)
            {
                buf.append("   ");
                if(line == ex.getLineNumber())
                {
                    buf.append("*");
                }
                else
                {
                    buf.append(" ");
                }
                buf.append(String.format("%-5d", line));
                buf.append("| ");
                buf.append(_lines[line-1]);
                buf.append("\n");
            }
        }
        return buf.toString();
    }
}
