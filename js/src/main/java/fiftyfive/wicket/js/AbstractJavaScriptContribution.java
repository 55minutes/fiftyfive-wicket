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

package fiftyfive.wicket.js;

import fiftyfive.wicket.js.locator.DependencyCollection;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.protocol.http.WebRequest;

/**
 * Base class for fiftyfive-wicket-js JavaScript {@code <head>} behaviors.
 * Provides methods for rendering dependencies as script src tags, and
 * DOM-ready scripts as the appropriate wicket-event.js or jQuery DOM-ready
 * declaration.
 * <p>
 * You will most likely never need to use this class or subclass it
 * directly.
 * 
 * @since 2.0
 */
public abstract class AbstractJavaScriptContribution extends AbstractBehavior
{
    /**
     * Renders a collection of JavaScript dependencies to the {@code <head>}
     * as script src tags. If the dependencies include a CSS resource, that
     * is rendered as well, using an appropriate {@code <link>} tag.
     * 
     * @param response The header object provided by Wicket, into which the
     *                 {@code <script>} and {@code <link>} tags will be
     *                 written.
     * 
     * @param dependencies The dependencies to render. May be empty.
     * 
     * @param exclude If not {@code null}, this particular dependency will be
     *                skipped and not rendered when traversing the dependency
     *                collection.
     */
    protected void renderDependencies(IHeaderResponse response,
                                      DependencyCollection dependencies,
                                      ResourceReference exclude)
    {
        ResourceReference css = dependencies.getCss();
        if(css != null) response.renderCSSReference(css);
        
        for(ResourceReference ref : dependencies)
        {
            if(ref != null && (exclude == null || !ref.equals(exclude)))
            {
                response.renderJavascriptReference(ref);
            }
        }
    }
    
    /**
     * Injects the given JavaScript string into a DOM-ready code block within
     * the {@code <head>}.
     * <p>
     * If we are within an ajax request, use Wicket's standard
     * {@link IHeaderResponse#renderOnDomReadyJavascript renderOnDomReadyJavascript()}
     * method to add javascript to the
     * &lt;head&gt;. During non-ajax requests, instead add the following
     * jQuery snippet to execute the javacript on DOM ready:
     * <code>jQuery(function() { ... });</code>
     * <p>
     * In either case, a script src tag for jQuery will be added to the
     * {@code <head>} if it was not there already.
     */
    protected void renderDomReady(IHeaderResponse response, String script)
    {
        if(null == script) return;
        
        if(response.wasRendered(script)) return;

        // Ensure that jQuery is present
        ResourceReference jQuery = settings().getJQueryResource();
        if(jQuery != null) response.renderJavascriptReference(jQuery);

        Request request = request();
        if((request instanceof WebRequest) && ((WebRequest)request).isAjax())
        {
            response.renderOnDomReadyJavascript(script);
        }
        else
        {
            response.renderJavascript(
                String.format("jQuery(function(){%s;});", script), null
            );
        }
        
        response.markRendered(script);
    }
    
    /**
     * Returns the settings to use. This method exists only for overriding
     * during unit tests.
     */
    JavaScriptDependencySettings settings()
    {
        return JavaScriptDependencySettings.get();
    }

    /**
     * Returns the current request. This method exists only for overriding
     * during unit tests.
     */
    Request request()
    {
        return RequestCycle.get().getRequest();
    }
}
