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

package fiftyfive.wicket.head;

import fiftyfive.wicket.util.HtmlUtils;
import org.apache.wicket.Application;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * Adds a {@code <link rel="home">} tag to the head of the page.
 * Will automatically point to the URL of the home page of the application.
 * This is sometimes useful when using client-side JavaScript to produce URLs.
 * <pre class="example">
 * public class BasePage extends WebPage
 * {
 *     public BasePage()
 *     {
 *         add(new HomeLink());
 *     }
 * }</pre>
 * <p>
 * Now, for example, you can use jQuery in the browser to gain access to
 * the home page URL:
 * <pre class="example">
 * jQuery("link[rel=home]").attr("href")</pre>
 * 
 * @since 2.0
 */
public class HomeLink extends AbstractBehavior
{
    public void renderHead(IHeaderResponse response)
    {
        response.renderString(String.format(
            "<link rel=\"home\" href=\"%s\" />%n",
            HtmlUtils.escapeAttribute(getHref())
        ));
    }
    
    protected CharSequence getHref()
    {
        return RequestCycle.get().urlFor(Application.get().getHomePage(), null);
    }
}
