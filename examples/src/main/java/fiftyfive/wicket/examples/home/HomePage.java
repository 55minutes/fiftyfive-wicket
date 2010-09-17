/**
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

package fiftyfive.wicket.examples.home;

import fiftyfive.wicket.examples.BasePage;
import fiftyfive.wicket.examples.formtest.FormTestPage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import static fiftyfive.wicket.util.Shortcuts.label;

public class HomePage extends BasePage
{
    public HomePage(PageParameters params)
    {
        super(params);
        
        add(JavascriptPackageResource.getHeaderContribution(
            HomePage.class, "HomePage.js"
        ));

        _body.setMarkupId("home");
        
        PageParameters ftParams = FormTestPage.params("10.2007", "10.03.2007", "11.10.2007");
        add(new BookmarkablePageLink("form-test", FormTestPage.class, ftParams));
    }
}
