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

package fiftyfive.wicket.js;

import fiftyfive.wicket.resource.MergedResourceBuilderTest;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;


public class MergedJavaScriptBuilderTest extends MergedResourceBuilderTest
{
    /**
     * Verify that the test page renders as expected (i.e. with merged resource
     * href and src attributes).
     */
    @Test
    public void testRender() throws Exception
    {
        WicketTester tester = doRender(MergedJavaScriptBuilderTestPage.class);
        tester.assertResultPage(
            MergedJavaScriptBuilderTestPage.class,
            "MergedJavaScriptBuilderTestPage-expected.html"
        );
    }
    
    /**
     * Verify that merged resources can be successfully downloaded.
     */
    @Test
    public void testMergedResourcesCanBeDownloaded() throws Exception
    {
        WicketTester tester = doRender(MergedJavaScriptBuilderTestPage.class);
        assertDownloaded(
            tester,
            "scripts/all.js",
            "/fiftyfive/wicket/js/lib/jquery-1.6.1/jquery.noconflict.min.js",
            "/fiftyfive/wicket/js/lib/jquery-ui-1.8.12/jquery-ui.min.js",
            "/fiftyfive/wicket/js/lib/cookies/cookies.js",
            "/fiftyfive/wicket/js/lib/strftime/strftime.js",
            "/fiftyfive/wicket/js/lib/fiftyfive-utils/55_utils.js",
            "/fiftyfive/wicket/js/lib/fiftyfive-utils/jquery.55_utils.js",
            "/org/apache/wicket/markup/html/wicket-event.js",
            "/org/apache/wicket/ajax/wicket-ajax.js");
    }
    
    /**
     * Verify that an exception is thrown if we execute install() without
     * specifying a path first.
     */
    @Test(expected=IllegalStateException.class)
    public void testMissingPathThrowsException()
    {
        MergedJavaScriptBuilder b = new MergedJavaScriptBuilder();
        b.addLibrary("jquery");
        b.install(new WicketTester().getApplication());
    }

    /**
     * Verify that an exception is thrown if we execute install() without
     * specifying a resource first.
     */
    @Test(expected=IllegalStateException.class)
    public void testMissingResourceThrowsException()
    {
        MergedJavaScriptBuilder b = new MergedJavaScriptBuilder();
        b.setPath("/scripts/all.js");
        b.install(new WicketTester().getApplication());
    }
    
    protected void onAppInit(WebApplication app)
    {
        new MergedJavaScriptBuilder().setPath("/scripts/all.js")
                                     .addJQueryUI()
                                     .addLibrary("cookies")
                                     .addLibrary("strftime")
                                     .addLibrary("55_utils")
                                     .addLibrary("jquery.55_utils")
                                     .addWicketAjaxLibraries()
                                     .install(app);
    }
}
