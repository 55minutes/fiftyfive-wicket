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

import static fiftyfive.wicket.util.Shortcuts.*;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.PackageResourceReference;

public class SimpleCDNTestPage extends WebPage
{
    public SimpleCDNTestPage()
    {
    }
    
    @Override
    public void renderHead(IHeaderResponse response)
    {
        response.renderCSSReference(
            new PackageResourceReference(SimpleCDNTestPage.class, "test.css"));
        response.renderJavaScriptReference(
            new PackageResourceReference(SimpleCDNTestPage.class, "test.js"));

        super.renderHead(response);
    }
}
