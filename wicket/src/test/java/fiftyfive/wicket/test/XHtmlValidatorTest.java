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
package fiftyfive.wicket.test;


import java.io.InputStream;
import java.io.IOException;

import org.apache.wicket.util.io.IOUtils;

import org.junit.Assert;
import org.junit.Test;


public class XHtmlValidatorTest
{
    @Test
    public void valid() throws IOException
    {
        XHtmlValidator validator = validator("xhtml-valid.html");
        Assert.assertTrue(validator.isValid());
    }

    @Test
    public void invalid() throws IOException
    {
        XHtmlValidator validator = validator("xhtml-invalid.html");
        Assert.assertFalse(validator.isValid());
    }
    
    private XHtmlValidator validator(String resource) throws IOException
    {
        InputStream in = null;
        try
        {
            in = getClass().getResourceAsStream(resource);
            String doc = IOUtils.toString(in, "UTF-8");
            XHtmlValidator validator = new XHtmlValidator();
            validator.parse(doc);
            return validator;
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }
}
