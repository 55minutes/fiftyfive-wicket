/*
 * Copyright 2009 55 Minutes (http://www.55minutes.com)
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.wicket.util.io.IOUtils;

import org.junit.Assert;
import org.junit.Test;


public class XHtmlEntityResolverTest
{
    private List<String> _missing = new ArrayList();
    
    @Test
    public void testXHtml10Frameset() throws Exception
    {
        parse("xhtml-1.0-frameset.html");
    }

    @Test
    public void testXHtml10Strict() throws Exception
    {
        parse("xhtml-1.0-strict.html");
    }

    @Test
    public void testXHtml10Transitional() throws Exception
    {
        parse("xhtml-1.0-transitional.html");
    }

    @Test
    public void testXHtml11() throws Exception
    {
        parse("xhtml-1.1.html");
    }
    
    private void parse(String resource) throws Exception
    {
        InputStream in = getClass().getResourceAsStream(resource);
        try
        {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            fac.setValidating(true);
        
            DocumentBuilder builder = fac.newDocumentBuilder();
            builder.setEntityResolver(new XHtmlEntityResolver() {
                @Override protected void onNotFound(String pubId, String sysId)
                {
                    _missing.add(sysId);
                }
            });
            _missing.clear();
            
            builder.parse(in);
            
            if(_missing.size() > 0)
            {
                Assert.fail("Entities not found: " + _missing);
            }
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }
}
