/**
 * Copyright 2012 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.mapper;

import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.IMapperContext;
import org.apache.wicket.util.tester.DummyHomePage;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;


public class PatternMountedMapperTest
{
    @Test
    public void testMapRequestReturnsNullOnRegexMismatch()
    {
        final String[][] patternAndRequestUrls = new String[][] {
            new String[] { "products/${productId:\\d+}/${slug}", "products/abc/123" },
            new String[] { "products/${productId:\\d+}/${slug}", "products/123abc/123" },
            new String[] { "products/${productId:\\d+}/${slug}", "products/abc" },
            new String[] { "products/${productId:\\d+}/${slug}", "products/12+3" },
            new String[] { "products/${productId:\\d+}/${slug}", "foo/123/abc" },
            new String[] { "products/${productId:\\d+}/${slug}", "123/abc" }
        };
        for(String[] pair : patternAndRequestUrls)
        {
            String pattern = pair[0];
            String requestUrl = pair[1];
            IRequestHandler handler = invokeMapRequest(pattern, requestUrl);
            Assert.assertNull(requestUrl + " should not match pattern " + pattern, handler);
        }
    }
    
    @Test
    public void testMapRequestReturnsHandlerOnRegexMatch()
    {
        final String[][] patternAndRequestUrls = new String[][] {
            new String[] { "products/${productId:\\d+}/${slug}", "products/123/123" },
            new String[] { "products/${productId:\\d+}/${slug}", "products/123/123/456" },
            new String[] { "products/${productId:\\d+}/${slug}", "products/4/abc" },
            new String[] { "products/${productId:\\d+}/${slug}", "products/900000/1" },
            new String[] { "products/${productId}/${slug}", "products/abc/123" }
        };
        for(String[] pair : patternAndRequestUrls)
        {
            String pattern = pair[0];
            String requestUrl = pair[1];
            IRequestHandler handler = invokeMapRequest(pattern, requestUrl);
            Assert.assertNotNull(requestUrl + " should match pattern " + pattern, handler);
        }
    }
    
    @Test
    public void testMapRequestReturnsHandlerExactMatch()
    {
        final String[][] patternAndRequestUrls = new String[][] {
            new String[] { "products/${productId:\\d+}/${slug}", "products/123/123" },
            new String[] { "products/${productId:\\d+}/${slug}", "products/4/abc" },
            new String[] { "products/${productId:\\d+}/${slug}", "products/900000/1" },
            new String[] { "products/${productId}/${slug}", "products/abc/123" }
        };
        for(String[] pair : patternAndRequestUrls)
        {
            String pattern = pair[0];
            String requestUrl = pair[1];
            IRequestHandler handler = invokeMapRequest(pattern, requestUrl, true);
            Assert.assertNotNull(requestUrl + " should match pattern " + pattern, handler);
        }
    }
    
    @Test
    public void testMapRequestReturnsHandlerOptionalMatch()
    {
        final String[][] patternAndRequestUrls = new String[][] {
            new String[] { "products/${productId:\\d+}/#{slug}", "products/123" },
            new String[] { "products/#{productId:\\d+}", "products/4" },
            new String[] { "products/#{productId:\\d+}", "products" },
        };
        for(String[] pair : patternAndRequestUrls)
        {
            String pattern = pair[0];
            String requestUrl = pair[1];
            IRequestHandler handler = invokeMapRequest(pattern, requestUrl, true);
            Assert.assertNotNull(requestUrl + " should match pattern " + pattern, handler);
        }
    }
    
    @Test
    public void testMapRequestReturnsNullOnInexactMatch()
    {
        final String[][] patternAndRequestUrls = new String[][] {
            new String[] { "products/${productId:\\d+}/${slug}", "products/123/123/456" }
        };
        for(String[] pair : patternAndRequestUrls)
        {
            String pattern = pair[0];
            String requestUrl = pair[1];
            IRequestHandler handler = invokeMapRequest(pattern, requestUrl, true);
            Assert.assertNull(requestUrl + " should not match pattern " + pattern, handler);
        }
    }
    
    private IRequestHandler invokeMapRequest(String pattern, String requestUrl)
    {
        return invokeMapRequest(pattern, requestUrl, false);
    }

    private IRequestHandler invokeMapRequest(String pattern, String requestUrl, boolean exact)
    {
        final IMapperContext mockContext = mock(IMapperContext.class);
        PatternMountedMapper mapper = new PatternMountedMapper(pattern, DummyHomePage.class) {
            @Override
            protected IMapperContext getContext()
            {
                return mockContext;
            }
        };
        if(exact)
        {
            mapper.setExact(true);
        }
        
        Url url = Url.parse(requestUrl);
        return mapper.mapRequest(createRequest(url));
    }
    
    private Request createRequest(final Url requestUrl)
    {
        return new Request() {
            @Override
            public Url getUrl()
            {
                return requestUrl;
            }

            @Override
            public Locale getLocale()
            {
                return null;
            }

            @Override
            public Charset getCharset()
            {
                return Charset.forName("UTF-8");
            }

            @Override
            public Url getClientUrl()
            {
                return requestUrl;
            }

            @Override
            public Object getContainerRequest()
            {
                return null;
            }
        };
    }
    
    @Test
    public void testRemovePatternsFromPlaceholders()
    {
        final String[][] inputAndExpectedOutput = new String[][] {
            new String[] { null, null },
            new String[] { "/foo", "/foo" },
            new String[] { "/foo/", "/foo/" },
            new String[] { "foo", "foo" },
            new String[] { "foo/", "foo/" },
            new String[] { "foo/bar", "foo/bar" },
            new String[] { "foo/bar/", "foo/bar/" },
            new String[] { "${foo}", "${foo}" },
            new String[] { "${foo}/bar", "${foo}/bar" },
            new String[] { "${foo}/bar/${baz}", "${foo}/bar/${baz}" },
            new String[] { "products/${productId:\\d+}/${slug:.*}", "products/${productId}/${slug}" },
            new String[] { "packages/${trackingCode:1Z[0-9A-Za-z]{16}}/${slug}", "packages/${trackingCode}/${slug}" }
        };
        for(String[] pair : inputAndExpectedOutput)
        {
            String input = pair[0];
            String expected = pair[1];
            Assert.assertEquals(
                "Failed input: " + input,
                expected,
                PatternMountedMapper.removePatternsFromPlaceholders(input));
        }
    }
}
