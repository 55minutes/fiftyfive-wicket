/**
 * Copyright 2014 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.test.dtd;

import java.io.InputStream;
import java.io.IOException;

import org.apache.wicket.util.string.Strings;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Resolves XHTML entities by using locally installed DTDs. This means that
 * any www.w3.org DTD will be loaded from the filesystem instead of making
 * an HTTP request. This greatly improves the speed of the parser
 * when validating XHTML documents.
 */
public class XHtmlEntityResolver implements EntityResolver
{
    /**
     * Resolves an entity using locally installed DTDs if possible.
     * Otherwise returns <code>null</code> to delegate to the default
     * resolver.
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException
    {
        InputSource src = null;
        
        if(systemId.startsWith("http://www.w3.org/"))
        {
            InputStream in = getClass().getResourceAsStream(filename(systemId));
            if(null == in)
            {
                onNotFound(publicId, systemId);
            }
            else
            {
                src = new InputSource(systemId);
                src.setPublicId(publicId);
                src.setByteStream(in);
            }
        }
        
        return src;
    }
    
    /**
     * This method is called when a w3 entity cannot be found on the local
     * filesystem. Default implementation does nothing. This is a hook for
     * testing purposes.
     */
    protected void onNotFound(String publicId, String systemId)
    {
        // pass
    }
    
    private String filename(String systemId)
    {
        return Strings.lastPathComponent(systemId, '/');
    }
}
