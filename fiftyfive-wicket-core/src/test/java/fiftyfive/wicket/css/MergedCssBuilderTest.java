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
package fiftyfive.wicket.css;

import fiftyfive.wicket.resource.MergedResourceBuilderTest;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;


public class MergedCssBuilderTest extends MergedResourceBuilderTest
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
        WicketTester tester = doRender(MergedCssBuilderTestPage.class);
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
        WicketTester tester = doRender(MergedCssBuilderTestPage.class);
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
    
    protected void onAppInit(WebApplication app)
    {
        new MergedCssBuilder().setPath("/static/styles.css")
                              .addCss(CSS_1)
                              .addCss(CSS_2)
                              .install(app);
        new MergedCssBuilder().setPath("/static/styles-print.css")
                              .addCss(CSS_PRINT_1)
                              .addCss(CSS_PRINT_2)
                              .install(app);
    }
}
