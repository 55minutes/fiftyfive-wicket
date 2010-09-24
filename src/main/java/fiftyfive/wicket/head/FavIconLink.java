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
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * Adds a {@code <link rel="shortcut icon">} tag to the head of the page.
 * The icon must be in {@code .ico} format, for compatibility with Internet
 * Explorer.
 * <pre class="example">
 * public class BasePage extends WebPage
 * {
 *     public BasePage()
 *     {
 *         add(new FavIconLink("images/myfavicon.ico"));
 *     }
 * }</pre>
 * 
 * @since 2.0
 */
public class FavIconLink extends AbstractBehavior
{
    protected ResourceReference _icon;
    
    /**
     * Creates a link tag for the given icon resource.
     */
    public FavIconLink(ResourceReference icon)
    {
        super();
        this._icon = icon;
    }
    
    /**
     * Creates a link tag for an icon located at the given path relative to
     * the classpath location of your application class.
     */
    public FavIconLink(String iconPathRelativeToApp)
    {
        this(new ResourceReference(
            Application.get().getClass(), iconPathRelativeToApp
        ));
    }
    
    public void renderHead(IHeaderResponse response)
    {
        response.renderString(String.format(
            "<link rel=\"%s\" type=\"%s\" href=\"%s\" />%n",
            HtmlUtils.escapeAttribute(getRel()),
            HtmlUtils.escapeAttribute(getType()),
            HtmlUtils.escapeAttribute(getHref())
        ));
    }
    
    protected String getType()
    {
        return "image/x-icon";
    }
    
    protected String getRel()
    {
        return "shortcut icon";
    }
    
    protected CharSequence getHref()
    {
        return RequestCycle.get().urlFor(_icon);
    }
}
