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

import fiftyfive.wicket.js.JavaScriptDependency;
import org.apache.wicket.markup.html.WebPage;

public class MergedJavaScriptBuilderTestPage extends WebPage
{
    public MergedJavaScriptBuilderTestPage()
    {
        super();
        add(new JavaScriptDependency("cookies"));
    }
}
