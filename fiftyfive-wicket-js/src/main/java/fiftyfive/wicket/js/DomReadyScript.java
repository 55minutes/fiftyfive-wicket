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
package fiftyfive.wicket.js;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Renders a script tag in the {@code <head>} that executes specified
 * JavaScript code on DOM ready.
 * The code will run when the page loads, and also every time the component to
 * which this behavior is attached is repainted via Wicket ajax.
 * <p>
 * <b>This behavior will cause jQuery to be added to the &lt;head&gt; if it
 * is not there already.</b> Your script can rely on the {@code jQuery}
 * object being available.
 * <p>
 * The script will <em>not</em> be scanned for dependencies declared using the
 * sprocket syntax. This behavior is intended for short one-liner
 * initialization scripts.
 * <p>
 * Trivial example:
 * <pre class="example">
 * public MyPanel(String id)
 * {
 *     super(id);
 *     add(new DomReadyScript("alert('page ready!')"));
 * }</pre>
 * 
 * @since 2.0
 */
public class DomReadyScript extends AbstractJavaScriptContribution
{
    private IModel<String> readyScript;
    
    /**
     * Creates a Behavior object that will add the given javascript
     * string to the &lt;head&gt; for execution on DOM ready.
     */
    public DomReadyScript(String readyScript)
    {
        this(Model.of(readyScript));
    }

    /**
     * Creates a Behavior object that will add the given javascript
     * string to the &lt;head&gt; for execution on DOM ready.
     */
    public DomReadyScript(IModel<String> readyScript)
    {
        super();
        this.readyScript = readyScript;
    }
    
    /**
     * Detaches the javascript string model that was provided in the
     * constructor.
     */
    @Override
    public void detach(Component component)
    {
        if(this.readyScript != null) this.readyScript.detach();
    }
    
    /**
     * Renders the DOM-ready script in the {@code <head>}.
     * If we are within an ajax request, use Wicket's standard
     * {@link IHeaderResponse#renderOnDomReadyJavaScript renderOnDomReadyJavaScript()}
     * method to add javascript to the
     * &lt;head&gt;. During non-ajax requests, instead add the following
     * jQuery snippet to execute the javacript on DOM ready:
     * <code>jQuery(function() { ... });</code>
     */
    @Override
    public void renderHead(Component comp, IHeaderResponse response)
    {
        if(this.readyScript != null)
        {
            renderDomReady(response, this.readyScript.getObject());
        }
    }
}
