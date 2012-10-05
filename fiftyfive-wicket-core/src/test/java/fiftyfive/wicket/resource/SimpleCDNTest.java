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

import java.io.InputStream;

import fiftyfive.wicket.test.WicketTestUtils;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.tester.DummyHomePage;
import org.apache.wicket.util.tester.WicketTester;

import org.junit.Test;


public class SimpleCDNTest
{
    static final String HOST = "http://abc.cloudfront.net";

    @Test
    public void testCDN() throws Exception
    {
        WicketTester tester = new WicketTester(new CDNApp(HOST));
        assertRendered(tester);
        assertResourcesDownload(tester);
    }

    @Test
    public void testCDN_trailing_slash() throws Exception
    {
        WicketTester tester = new WicketTester(new CDNApp(HOST + "/"));
        assertRendered(tester);
        assertResourcesDownload(tester);
    }

    /**
     * Verify that the SimpleCDNTestPage renders and rewrites the resource URLs
     * as expected.
     */
    void assertRendered(WicketTester tester) throws Exception
    {
        tester.startPage(SimpleCDNTestPage.class);
        tester.assertRenderedPage(SimpleCDNTestPage.class);
        WicketTestUtils.assertValidMarkup(tester);
        tester.assertResultPage(SimpleCDNTestPage.class, "SimpleCDNTestPage-expected.html");
    }

    /**
     * Verify that the rewritten resource URLs still work and download the expected binary data
     * once the CDN host is stripped off (as a reverse-proxy CDN would do).
     */
    void assertResourcesDownload(WicketTester tester) throws Exception
    {
        String[] resources = new String[] { "test.css", "test.js", "test.gif" };
        for(String res : resources)
        {
            InputStream is = getClass().getResourceAsStream(res);
            try
            {
                String uri = "wicket/resource/fiftyfive.wicket.resource.SimpleCDNTestPage/" + res;
                byte[] expected = IOUtils.toByteArray(is);
                WicketTestUtils.assertDownloadEquals(tester, uri, expected);
            }
            finally
            {
                IOUtils.closeQuietly(is);
            }
        }
    }

    class CDNApp extends WebApplication
    {
        final String host;

        CDNApp(String host)
        {
            this.host = host;
        }

        @Override
        public Class<? extends WebPage> getHomePage()
        {
            return DummyHomePage.class;
        }

        @Override
        protected void init()
        {
            super.init();
            getMarkupSettings().setStripWicketTags(true);
            getResourceSettings().setCachingStrategy(NoOpResourceCachingStrategy.INSTANCE);
            new SimpleCDN(this.host).install(this);
        }
    }
}
