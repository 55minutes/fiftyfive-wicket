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

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;

import static fiftyfive.wicket.css.MergedCssBuilderTest.*;


public class MergedCssBuilderTestPage extends WebPage
{
    public MergedCssBuilderTestPage()
    {
        super();
    }
    
    @Override
    public void renderHead(IHeaderResponse response)
    {
        super.renderHead(response);
        response.renderCSSReference(CSS_1);
        response.renderCSSReference(CSS_2);
        response.renderCSSReference(CSS_PRINT_1, "print");
        response.renderCSSReference(CSS_PRINT_2, "print");
    }
}
