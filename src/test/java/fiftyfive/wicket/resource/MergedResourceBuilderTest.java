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
package fiftyfive.wicket.resource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.StringList;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import static org.apache.wicket.Application.DEPLOYMENT;
import static org.apache.wicket.Application.DEVELOPMENT;
import static org.junit.Assert.assertEquals;

public class MergedResourceBuilderTest
{
    static final ResourceReference CSS_1 = new ResourceReference(
        MergedResourceBuilderTest.class, "1.css"
    );
    static final ResourceReference CSS_2 = new ResourceReference(
        MergedResourceBuilderTest.class, "2.css"
    );
    static final ResourceReference CSS_PRINT_1 = new ResourceReference(
        MergedResourceBuilderTest.class, "1-print.css"
    );
    static final ResourceReference CSS_PRINT_2 = new ResourceReference(
        MergedResourceBuilderTest.class, "2-print.css"
    );
    static final ResourceReference JS_1 = new ResourceReference(
        MergedResourceBuilderTest.class, "1.js"
    );
    static final ResourceReference JS_2 = new ResourceReference(
        MergedResourceBuilderTest.class, "2.js"
    );
    
    /**
     * Verify that the test page renders as expected (i.e. with each resource
     * listed separately) during development.
     */
    @Test
    public void testRender_development() throws Exception
    {
        WicketTester tester = doRender(DEVELOPMENT);
        tester.assertResultPage(
            MergedResourceBuilderTestPage.class,
            "MergedResourceBuilderTestPage-development-expected.html"
        );
    }

    /**
     * Verify that the test page renders as expected (i.e. with merged resource
     * href and src attributes) during deployment.
     */
    @Test
    public void testRender_deployment() throws Exception
    {
        WicketTester tester = doRender(DEPLOYMENT);
        tester.assertResultPage(
            MergedResourceBuilderTestPage.class,
            "MergedResourceBuilderTestPage-deployment-expected.html"
        );
    }
    
    /**
     * Verify that individual, non-merged resources can be succesfully
     * downloaded in development mode.
     */
    @Test
    public void testDownload_development() throws IOException
    {
        WicketTester tester = doRender(DEVELOPMENT);
        assertDownloaded(
            tester,
            "static/styles.css-fiftyfive.wicket.resource.MergedResourceBuilderTest%2F1.css",
            "1.css"
        );
        assertDownloaded(
            tester,
            "static/styles.css-fiftyfive.wicket.resource.MergedResourceBuilderTest%2F2.css",
            "2.css"
        );
        assertDownloaded(
            tester,
            "static/styles-print.css-fiftyfive.wicket.resource.MergedResourceBuilderTest%2F1-print.css",
            "1-print.css"
        );
        assertDownloaded(
            tester,
            "static/styles-print.css-fiftyfive.wicket.resource.MergedResourceBuilderTest%2F2-print.css",
            "2-print.css"
        );
        assertDownloaded(
            tester,
            "static/scripts.js-fiftyfive.wicket.resource.MergedResourceBuilderTest%2F1.js",
            "1.js"
        );
        assertDownloaded(
            tester,
            "static/scripts.js-fiftyfive.wicket.resource.MergedResourceBuilderTest%2F2.js",
            "2.js"
        );
    }

    /**
     * Verify that resources are merged during deployment and can be
     * successfully downloaded.
     */
    @Test
    public void testDownload_deployment() throws IOException
    {
        WicketTester tester = doRender(DEPLOYMENT);
        assertDownloaded(tester, "static/styles.css", "1.css", "2.css");
        assertDownloaded(
            tester, 
            "static/styles-print.css",
            "1-print.css", "2-print.css"
        );
        assertDownloaded(tester, "static/scripts.js", "1.js", "2.js");
    }
    
    /**
     * Verify that an exception is thrown if we execute build() without
     * specifying a path first.
     */
    @Test(expected=IllegalStateException.class)
    public void testMissingPath()
    {
        MergedResourceBuilder b = new MergedResourceBuilder();
        b.addCss(getClass(), "1.css");
        b.build(new WicketTester().getApplication());
    }

    /**
     * Verify that an exception is thrown if we execute build() without
     * specifying a resource first.
     */
    @Test(expected=IllegalStateException.class)
    public void testMissingResource()
    {
        MergedResourceBuilder b = new MergedResourceBuilder();
        b.setPath("/styles/all.css");
        b.build(new WicketTester().getApplication());
    }
    
    /**
     * Verify that an exception is thrown if we add CSS to the builder
     * followed by JS.
     */
    @Test(expected=IllegalStateException.class)
    public void testCssThenJavaScript()
    {
        MergedResourceBuilder b = new MergedResourceBuilder();
        b.addCss(getClass(), "1.css");
        b.addScript(getClass(), "1.js");
    }

    /**
     * Verify that an exception is thrown if we add JS to the builder
     * followed by CSS.
     */
    @Test(expected=IllegalStateException.class)
    public void testJavaScriptThenCss()
    {
        MergedResourceBuilder b = new MergedResourceBuilder();
        b.addScript(getClass(), "1.js");
        b.addCss(getClass(), "1.css");
    }

    /**
     * Verify that an exception is thrown if we add JS to the builder
     * and then specify CSS media.
     */
    @Test(expected=IllegalStateException.class)
    public void testJavaScriptThenCssMedia()
    {
        MergedResourceBuilder b = new MergedResourceBuilder();
        b.addScript(getClass(), "1.js");
        b.setCssMedia("print");
    }
    
    /**
     * Render the MergedResourceBuilderTestPage in either
     * DEVELOPMENT or DEPLOYMENT mode.
     */
    private WicketTester doRender(final String mode)
    {
        WicketTester tester = new WicketTester(new MergedApp() {
            @Override
            public String getConfigurationType()
            {
                return mode;
            }
        });
        tester.startPage(MergedResourceBuilderTestPage.class);
        tester.assertRenderedPage(MergedResourceBuilderTestPage.class);
        return tester;
    }
    
    /**
     * Download the resource at the given URI and make sure its contents
     * are identical to a merged list of files from the test fixture.
     */
    private void assertDownloaded(WicketTester tester,
                                  String uri,
                                  String... files)
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
        WebRequestCycle wrc = tester.setupRequestAndResponse(false);
        tester.getServletRequest().setURL(uri);
        tester.processRequestCycle(wrc);
        
        // Note: merging adds two newlines between each merged file
        assertEquals(
            expected.join("\n\n"),
            tester.getServletResponse().getDocument()
        );
    }

    /**
     * Test app that mounts merged resources.
     */
    static class MergedApp extends WebApplication
    {
        @Override
        public Class<? extends WebPage> getHomePage()
        {
            return MergedResourceBuilderTestPage.class;
        }

        @Override
        protected void init()
        {
            super.init();
            new MergedResourceBuilder().setPath("/static/styles.css")
                                       .addCss(CSS_1)
                                       .addCss(CSS_2)
                                       .build(this);
            new MergedResourceBuilder().setPath("/static/styles-print.css")
                                       .addCss(CSS_PRINT_1)
                                       .addCss(CSS_PRINT_2)
                                       .build(this);
            new MergedResourceBuilder().setPath("/static/scripts.js")
                                       .addScript(JS_1)
                                       .addScript(JS_2)
                                       .build(this);
        }
    }
}
