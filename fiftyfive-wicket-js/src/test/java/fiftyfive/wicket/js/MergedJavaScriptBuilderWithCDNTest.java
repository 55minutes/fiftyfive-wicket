/**
 * Copyright 2013 55 Minutes (http://www.55minutes.com)
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

import fiftyfive.wicket.resource.SimpleCDN;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;


/**
 * Make sure JavaScript merging still works as expected even with SimpleCDN added
 * to the mix.
 */
public class MergedJavaScriptBuilderWithCDNTest extends MergedJavaScriptBuilderTest
{
    @Test
    @Override
    public void testRender() throws Exception
    {
        WicketTester tester = doRender(MergedJavaScriptBuilderTestPage.class);
        tester.assertResultPage(
            MergedJavaScriptBuilderTestPage.class,
            "MergedJavaScriptBuilderTestPage-expected-cdn.html"
        );
    }

    @Override
    protected void onAppInit(WebApplication app)
    {
        super.onAppInit(app);
        new SimpleCDN("http://gh23g239adgah.cloudfront.net").install(app);
    }
}
