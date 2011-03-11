/**
 * Copyright 2011 55 Minutes (http://www.55minutes.com)
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

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Base class for XHTML and HTML5 validators.
 */
public abstract class AbstractDocumentValidator implements ErrorHandler
{
    static final int DEF_NUM_LINES_CONTEXT = 10;
    
    private int     numLinesContext = DEF_NUM_LINES_CONTEXT;
    private boolean parsed = false;
    private String[] lines = null;
    private List<String> errors = new ArrayList();
    private Document doc;
    
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
        this.numLinesContext = newNumLinesContext;
    }
    
    /**
     * Parses a document provided as a String. To test whether the
     * document was parsed successfully and is valid, subsequently call
     * {@link #isValid}.
     */
    public void parse(String document) throws IOException
    {
        if(this.parsed)
        {
            throw new IllegalStateException(
                "parse() can only be used once. " +
                "Create a new instance for each document you wish to parse."
            );
        }
        try
        {
            this.lines = document.split("\\r?\\n");
            this.doc = builder().parse(new InputSource(new StringReader(document)));
            this.parsed = true;
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
        return this.errors.size() == 0 && this.parsed;
    }
    
    public Document getDocument()
    {
        if(!this.parsed)
        {
            throw new IllegalStateException(
                "A document does not exist because parse() has not yet been " +
                "called. Parse first and then call getDocument()."
            );
        }
        return this.doc;
    }
    
    /**
     * Returns the errors and warnings that were encountered in the previous
     * call to {@link #parse}, if any.
     */
    public List<String> getErrors()
    {
        return Collections.unmodifiableList(this.errors);
    }
    
    
    // ==== ErrorHandler callbacks===========================================
    
    public void error(SAXParseException exception)
    {
        this.errors.add(format(exception));
    }
    
    public void fatalError(SAXParseException exception)
    {
        this.errors.add(format(exception));
    }
    
    public void warning(SAXParseException exception)
    {
        this.errors.add(format(exception));
    }

    // ==== End ErrorHandler callbacks ======================================
    
    /**
     * Construct a validating DocumentBuilder with <code>this</code>
     * registered as the error handler. This is the parsing and validation
     * strategy that will be used.
     * Subclasses should provide the appropriate XHTML or HTML5 implementation.
     */
    protected abstract DocumentBuilder builder();
    
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
        
        final int ctx = this.numLinesContext;
        for(int offset = -1 * ctx/2; offset <= ctx/2; offset++)
        {
            int line = ex.getLineNumber() + offset;
            if(line >= 1 && line <= this.lines.length)
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
                buf.append(this.lines[line-1]);
                buf.append("\n");
            }
        }
        return buf.toString();
    }
}
