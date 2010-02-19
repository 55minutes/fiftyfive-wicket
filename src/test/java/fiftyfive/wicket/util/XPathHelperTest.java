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

import java.io.StringReader;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NodeList;

public class XPathHelperTest
{
    private static final String XML =
        "<html><head><title>hello</title></head><body>" +
        "<div id=\"main\"><h1>hello</h1><p>this is a test</p></div>" +
        "<p>another</p>" +
        "</body></html>";
    
    private XPathHelper _xpath;
    
    @Before
    public void setup() throws Exception
    {
        _xpath = XPathHelper.parse(new StringReader(XML));
    }
    
    @Test
    public void testFindString() throws Exception
    {
        Assert.assertEquals(
            "this is a test",
            _xpath.findString("//div[@id='main']/p/text()")
        );
    }

    @Test
    public void testFindString_fail() throws Exception
    {
        Assert.assertNull(_xpath.findString("//div[@id='wrongid']/p/text()"));
    }

    @Test
    public void testFindStrings() throws Exception
    {
        Assert.assertEquals(
            Arrays.asList("this is a test", "another"),
            _xpath.findStrings("//p/text()")
        );
    }

    @Test
    public void testFindStrings_empty() throws Exception
    {
        Assert.assertEquals(
            Arrays.asList(),
            _xpath.findStrings("//li/text()")
        );
    }

    @Test
    public void testFindNodes() throws Exception
    {
        NodeList nodes = _xpath.findNodes("//p");
        Assert.assertEquals(2, nodes.getLength());
        for(int i=0; i<nodes.getLength(); i++)
        {
            Assert.assertEquals("p", nodes.item(i).getNodeName());
        }
    }

    @Test
    public void testFindNodes_empty() throws Exception
    {
        NodeList nodes = _xpath.findNodes("//li");
        Assert.assertEquals(0, nodes.getLength());
    }
}
