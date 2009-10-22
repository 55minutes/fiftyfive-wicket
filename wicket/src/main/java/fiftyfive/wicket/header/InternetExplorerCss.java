/*
 * Copyright 2009 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.header;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;


/**
 * Static methods for injecting conditional stylesheet references into
 * the HTML header. These methods will create markup like this:
 * <pre>
 * &lt;!--[if condition]&gt;
 * &lt;link rel="stylesheet" type="text/css" href="..." /&gt;
 * &lt;![endif]--&gt;
 * </pre>
 * In non-Internet Explorer browsers, this stylesheet reference will be
 * ignored. But in IE, the stylesheet will be loaded as long as the
 * <code>condition</code> is met. For example, if the condition is
 * <code>IE 7</code>, the stylesheet will be loaded only in IE7 browsers.
 * Another example: <code>gte IE 7</code> will load in browsers IE7 or greater.
 * <p>
 * Here's how you might use it in your Wicket page:
 * <pre>
 * // Add a &lt;link&gt; to the ie-7.css stylesheet for IE7 browsers only
 * add(InternetExplorerCss.getConditionalHeaderContribution(
 *     "IE 7", new ResourceReference(getClass(), "ie-7.css")
 * ));
 * </pre>
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/ms537512%28VS.85%29.aspx">MSDN Reference</a>
 */
public class InternetExplorerCss
{
    /**
     * Creates a Wicket HeaderContributor for the specified stylesheet.
     * The stylesheet will only load in Internet Explorer versions that
     * meet the specified condition.
     *
     * @param condition    The IE version for which this stylesheet applies.
     *                     Examples: "IE 7", "gte IE 7".
     * @param cssReference A reference to the stylesheet.
     *
     * @return A HeaderContributor that should be added to your page. Adding
     *         it to the page will cause the appropriate markup to be
     *         emitted in the HTML header.
     */
    public static HeaderContributor getConditionalHeaderContribution (
        final String condition,
        final ResourceReference cssReference
    )
    {
        return getConditionalHeaderContribution(condition, cssReference, null);
    }

    /**
     * Creates a Wicket HeaderContributor for the specified stylesheet and
     * media type.
     * The stylesheet will only load in Internet Explorer versions that
     * meet the specified condition.
     *
     * @param condition    The IE version for which this stylesheet applies.
     *                     Examples: "IE 7", "gte IE 7".
     * @param cssReference A reference to the stylesheet.
     * @param media        The CSS media type, like "screen" or "print".
     *
     * @return A HeaderContributor that should be added to your page. Adding
     *         it to the page will cause the appropriate markup to be
     *         emitted in the HTML header.
     */
    public static HeaderContributor getConditionalHeaderContribution (
        final String condition,
        final ResourceReference cssReference,
        final String media
    )
    {
        return new HeaderContributor(new IHeaderContributor() {
            public void renderHead(IHeaderResponse response)
            {
                final List<Serializable> token = Arrays.asList(
                    condition, cssReference, media
                );
                if(!response.wasRendered(token))
                {
                    response.getResponse().write("<!--[if ");
                    response.getResponse().write(condition);
                    response.getResponse().println("]>");
                    response.renderCSSReference(cssReference, media);
                    response.getResponse().println("<![endif]-->");
                    response.markRendered(token);
                }
            }
        });
    }
    
    /**
     * Private constructor so this class cannot be instantiated in normal use.
     */
    private InternetExplorerCss()
    {
        super();
    }
}
