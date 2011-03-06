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

package fiftyfive.wicket.css;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * This class contains static methods for injecting conditional stylesheet
 * references into the HTML header. These methods will create markup like this:
 * <pre class="example">
 * &lt;!--[if condition]&gt;
 * &lt;link rel="stylesheet" type="text/css" href="..." /&gt;
 * &lt;![endif]--&gt;</pre>
 * <p>
 * In non-Internet Explorer browsers, this stylesheet reference will be
 * ignored. But in IE, the stylesheet will be loaded as long as the
 * <code>condition</code> is met. For example, if the condition is
 * <code>IE 7</code>, the stylesheet will be loaded only in IE7 browsers.
 * Another example: <code>gte IE 7</code> will load in browsers IE7 or greater.
 * <p>
 * Here's how you might use it in your Wicket page:
 * <pre class="example">
 * // Add a &lt;link&gt; to the ie-7.css stylesheet for IE7 browsers only
 * add(InternetExplorerCss.getConditionalHeaderContribution("IE 7", "styles/ie-7.css"));</pre>
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/ms537512%28VS.85%29.aspx">MSDN Reference</a>
 */
public abstract class InternetExplorerCss extends Behavior
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
    public static InternetExplorerCss getConditionalHeaderContribution (
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
    public static InternetExplorerCss getConditionalHeaderContribution (
        final String condition,
        final ResourceReference cssReference,
        final String media
    )
    {
        List<Serializable> token = Arrays.asList(
            condition, cssReference, media
        );
        return new InternetExplorerCss(condition, token) {
            protected void doLinkRender(IHeaderResponse response)
            {
                response.renderCSSReference(cssReference, media);                
            }
        };
    }
    
    
    /**
     * Creates a Wicket HeaderContributor for the specified stylesheet.
     * The stylesheet will only load in Internet Explorer versions that
     * meet the specified condition.
     *
     * @param condition    The IE version for which this stylesheet applies.
     *                     Examples: "IE 7", "gte IE 7".
     * @param contextRelativeUri The URI to the stylesheet. If starting with
     *                           "/", "http:" or "https:", the URI will be
     *                           emitted as-is. Otherwise the URI is prepended
     *                           with the context path to make it absolute.
     *
     * @return A HeaderContributor that should be added to your page. Adding
     *         it to the page will cause the appropriate markup to be
     *         emitted in the HTML header.
     */
    public static InternetExplorerCss getConditionalHeaderContribution(
        final String condition,
        final String contextRelativeUri
    )
    {
        return getConditionalHeaderContribution(
            condition, contextRelativeUri, null
        );
    }
    
    /**
     * Creates a Wicket HeaderContributor for the specified stylesheet and
     * media type.
     * The stylesheet will only load in Internet Explorer versions that
     * meet the specified condition.
     *
     * @param condition    The IE version for which this stylesheet applies.
     *                     Examples: "IE 7", "gte IE 7".
     * @param contextRelativeUri The URI to the stylesheet. If starting with
     *                           "/", "http:" or "https:", the URI will be
     *                           emitted as-is. Otherwise the URI is modified
     *                           so that it resolves relative to the context
     *                           path (/app-name).
     * @param media        The CSS media type, like "screen" or "print".
     *
     * @return A HeaderContributor that should be added to your page. Adding
     *         it to the page will cause the appropriate markup to be
     *         emitted in the HTML header.
     */
    public static InternetExplorerCss getConditionalHeaderContribution(
        final String condition,
        final String contextRelativeUri,
        final String media
    )
    {
        List<String> token = Arrays.asList(
            condition, contextRelativeUri, media
        );
        return new InternetExplorerCss(condition, token) {
            protected void doLinkRender(IHeaderResponse response)
            {
                response.renderCSSReference(contextRelativeUri, media);                
            }
        };
    }
    
    private String _condition;
    private List<? extends Serializable> _token;
    
    /**
     * Internal use only.
     */
    protected InternetExplorerCss(String condition,
                                  List<? extends Serializable> token)
    {
        _condition = condition;
        _token = token;
    }
    
    /**
     * Renders the conditional Internet Explorer comment, delegating to
     * {@link #doLinkRender} to render the actual link element.
     */
    @Override
    public void renderHead(Component comp, IHeaderResponse response)
    {
        if(!response.wasRendered(_token))
        {
            response.getResponse().write(String.format(
                "<!--[if %s]>%n",
                _condition
            ));
            doLinkRender(response);
            response.getResponse().write(String.format("<![endif]-->%n"));
            response.markRendered(_token);
        }
    }
    
    /**
     * Override to render the stylesheet link element.
     */
    protected abstract void doLinkRender(IHeaderResponse response);
}
