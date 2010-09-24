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

import java.net.URLConnection;

import org.apache.wicket.ResourceReference;

/**
 * Adds a {@code <link rel="apple-touch-icon">} tag to the head of the page.
 * This is an image used by Safari and other applications on
 * Apple iOS devices. It is recommended that you use a 114x114 PNG.
 * <pre class="example">
 * public class BasePage extends WebPage
 * {
 *     public BasePage()
 *     {
 *         add(new AppleTouchIconLink("images/mytouchicon.png"));
 *     }
 * }</pre>
 * 
 * @since 2.0
 * @see <a href="http://developer.apple.com/safari/library/documentation/appleapplications/reference/safariwebcontent/ConfiguringWebApplications/ConfiguringWebApplications.html">Safari Web Content Guide</a>
 */
public class AppleTouchIconLink extends FavIconLink
{
    /**
     * Creates a link tag for the given icon resource.
     */
    public AppleTouchIconLink(ResourceReference icon)
    {
        super(icon);
    }

    /**
     * Creates a link tag for an icon located at the given path relative to
     * the classpath location of your application class.
     */
    public AppleTouchIconLink(String iconPathRelativeToApp)
    {
        super(iconPathRelativeToApp);
    }

    @Override
    protected String getType()
    {
        // TODO: test this
        String type = URLConnection.guessContentTypeFromName(_icon.getName());
        if(null == type)
        {
            type = "image/png";
        }
        return type;
    }
    
    @Override
    protected String getRel()
    {
        return "apple-touch-icon";
    }
}
