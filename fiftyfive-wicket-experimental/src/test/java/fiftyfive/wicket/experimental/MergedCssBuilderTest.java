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

package fiftyfive.wicket.experimental;

import java.io.IOException;
import java.io.InputStream;

import fiftyfive.wicket.test.WicketTestUtils;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpSession;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.StringList;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class MergedCssBuilderTest
{
    static final ResourceReference CSS_1 = new PackageResourceReference(
        MergedCssBuilderTest.class, "1.css"
    );
    static final ResourceReference CSS_2 = new PackageResourceReference(
        MergedCssBuilderTest.class, "2.css"
    );
    static final ResourceReference CSS_PRINT_1 = new PackageResourceReference(
        MergedCssBuilderTest.class, "1-print.css"
    );
    static final ResourceReference CSS_PRINT_2 = new PackageResourceReference(
        MergedCssBuilderTest.class, "2-print.css"
    );
    
    /**
     * Verify that the test page renders as expected (i.e. with merged resource
     * href and src attributes).
     */
    @Test
    public void testRender() throws Exception
    {
        WicketTester tester = doRender();
        tester.assertResultPage(
            MergedCssBuilderTestPage.class,
            "MergedCssBuilderTestPage-expected.html"
        );
    }
    
    /**
     * Verify that merged resources can be successfully downloaded.
     */
    @Test
    public void testMergedResourcesCanBeDownloaded() throws Exception
    {
        WicketTester tester = doRender();
        assertDownloaded(tester, "static/styles.css", "1.css", "2.css");
        assertDownloaded(tester, "static/styles-print.css", "1-print.css", "2-print.css");
    }
    
    /**
     * Verify that an exception is thrown if we execute install() without
     * specifying a path first.
     */
    @Test(expected=IllegalStateException.class)
    public void testMissingPathThrowsException()
    {
        MergedCssBuilder b = new MergedCssBuilder();
        b.addCss(getClass(), "1.css");
        b.install(new WicketTester().getApplication());
    }

    /**
     * Verify that an exception is thrown if we execute install() without
     * specifying a resource first.
     */
    @Test(expected=IllegalStateException.class)
    public void testMissingResourceThrowsException()
    {
        MergedCssBuilder b = new MergedCssBuilder();
        b.setPath("/styles/all.css");
        b.install(new WicketTester().getApplication());
    }
    
    /**
     * Render the MergedCssBuilderTestPage.
     */
    private WicketTester doRender() throws Exception
    {
        WicketTester tester = new WicketTester(new MergedApp());
        tester.startPage(MergedCssBuilderTestPage.class);
        tester.assertRenderedPage(MergedCssBuilderTestPage.class);
        WicketTestUtils.assertValidMarkup(tester);
        return tester;
    }
    
    /**
     * Download the resource at the given URI and make sure its contents
     * are identical to a merged list of files from the test fixture.
     */
    private void assertDownloaded(WicketTester tester, String uri, String... files)
        throws IOException
    {
        StringList expected = new StringList();
        for(String filename : files)
        {
            InputStream is = getClass().getResourceAsStream(filename);
            try
            {
                expected.add(IOUtils.toString(is, "UTF-8"));
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
        
        assertEquals(expected.join(""), tester.getLastResponseAsString());
    }

    /**
     * Test app that mounts merged resources.
     */
    static class MergedApp extends WebApplication
    {
        @Override
        public Class<? extends WebPage> getHomePage()
        {
            return MergedCssBuilderTestPage.class;
        }

        @Override
        protected void init()
        {
            super.init();
            getResourceSettings().setCachingStrategy(NoOpResourceCachingStrategy.INSTANCE);
            new MergedCssBuilder().setPath("/static/styles.css")
                                  .addCss(CSS_1)
                                  .addCss(CSS_2)
                                  .install(this);
            new MergedCssBuilder().setPath("/static/styles-print.css")
                                  .addCss(CSS_PRINT_1)
                                  .addCss(CSS_PRINT_2)
                                  .install(this);
        }
    }
}
