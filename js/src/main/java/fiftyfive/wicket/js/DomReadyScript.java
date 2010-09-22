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

import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequest;

/**
 * Renders a script tag in the &lt;head&gt; that executes specified
 * JavaScript code on DOM ready. During non-ajax responses, jQuery's
 * ready handler is used rather than the default Wicket ready handler, since
 * jQuery has a faster implementation.
 * <p>
 * <b>This behavior will cause jQuery to be added to the &lt;head&gt; if it
 * is not there already.</b>
 * <p>
 * Trivial example:
 * <pre>
 * public MyPanel(String id)
 * {
 *     super(id);
 *     add(new DomReadyScript("alert('page ready!')"));
 * }
 * </pre>
 */
public class DomReadyScript extends AbstractBehavior
{
    private IModel<String> _readyScript;
    
    /**
     * Creates an IBehavior object that will add the given javascript
     * string to the &lt;head&gt; for execution on DOM ready.
     */
    public DomReadyScript(String readyScript)
    {
        this(Model.of(readyScript));
    }

    /**
     * Creates an IBehavior object that will add the given javascript
     * string to the &lt;head&gt; for execution on DOM ready.
     */
    public DomReadyScript(IModel<String> readyScript)
    {
        super();
        _readyScript = readyScript;
    }
    
    /**
     * Detaches the javascript string model that was provided in the
     * constructor.
     */
    @Override
    public void detach(Component component)
    {
        if(_readyScript != null) _readyScript.detach();
    }
    
    /**
     * If we are within an ajax request, use Wicket's standard
     * {@link IHeaderResponse#renderOnDomReadyJavascript renderOnDomReadyJavascript()}
     * method to add javascript to the
     * &lt;head&gt;. During non-ajax requests, instead add the following
     * jQuery snippet to execute the javacript on DOM ready:
     * <code>jQuery(function() { ... });</code>
     */
    @Override
    public void renderHead(IHeaderResponse response)
    {
        if(null == _readyScript) return;
        String script = _readyScript.getObject();
        if(null == script) return;
        
        if(response.wasRendered(script)) return;

        // Ensure that jQuery is present
        response.renderJavascriptReference(settings().getJQueryResource());

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
