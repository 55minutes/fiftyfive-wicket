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
package fiftyfive.wicket.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fiftyfive.wicket.test.WicketTestUtils;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.tester.DummyHomePage;
import org.apache.wicket.util.tester.WicketTester;

import static org.junit.Assert.assertArrayEquals;


public abstract class MergedResourceBuilderTest
{
    /**
     * Render the MergedCssBuilderTestPage.
     */
    protected WicketTester doRender(Class<? extends WebPage> page) throws Exception
    {
        WicketTester tester = new WicketTester(new MergedApp());
        tester.startPage(page);
        tester.assertRenderedPage(page);
        WicketTestUtils.assertValidMarkup(tester);
        return tester;
    }
    
    /**
     * Download the resource at the given URI and make sure its contents
     * are identical to a merged list of files from the test fixture.
     */
    protected void assertDownloaded(WicketTester tester, String uri, String... files)
        throws IOException
    {
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        for(String filename : files)
        {
            InputStream is = getClass().getResourceAsStream(filename);
            try
            {
                IOUtils.copy(is, expected);
            }
            finally
            {
                IOUtils.closeQuietly(is);
            }
        }
        
        MockHttpSession session = new MockHttpSession(tester.getApplication().getServletContext());
        MockHttpServletRequest request = new MockHttpServletRequest(
            tester.getApplication(),
            session,
            tester.getApplication().getServletContext()
        );
        request.setURL(uri);
        tester.processRequest(request);
        
        byte[] actual = tester.getLastResponse().getBinaryContent();
        assertArrayEquals(expected.toByteArray(), actual);
    }
    
    protected abstract void onAppInit(WebApplication app);

    /**
     * Test app that mounts merged resources.
     */
    public class MergedApp extends WebApplication
    {
        @Override
        public Class<? extends WebPage> getHomePage()
        {
            return DummyHomePage.class;
        }

        @Override
        protected void init()
        {
            super.init();
            getResourceSettings().setCachingStrategy(NoOpResourceCachingStrategy.INSTANCE);
            onAppInit(this);
        }
    }
}
