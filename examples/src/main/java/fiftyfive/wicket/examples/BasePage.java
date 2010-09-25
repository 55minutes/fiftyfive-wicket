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

package fiftyfive.wicket.examples;

import java.util.Date;

import fiftyfive.wicket.js.JavaScriptDependency;
import fiftyfive.wicket.link.HomeLink;
import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;
import static fiftyfive.wicket.util.Shortcuts.*;

/**
 * Base class for all pages. Provides markup for the HTML5 doctype.
 * <p>
 * Also exposes a <code>_body</code> variable to subclasses that can be used
 * to add <code>id</code> or <code>class</code> attributes to the
 * &lt;body&gt; element. For example, to add an <code>id</code>, do this:
 * <pre class="example">
 * _body.setMarkupId("myId");</pre>
 * <p>
 * To add a CSS class (using fiftyfive.wicket.util.Shortcuts.cssClass):
 * <pre class="example">
 * _body.add(cssClass("myClass"));</pre>
 */
public abstract class BasePage extends WebPage
{
    protected final WebMarkupContainer _body;
    
    public BasePage(PageParameters params)
    {
        super(params);
        add(new DebugBar("debug"));
        
        // Set up <head> elements
        add(new HomeLink("home-link"));
        
        // Set up JS
        add(new JavaScriptDependency(BasePage.class));
        
        // Set up CSS
        add(cssResource("styles/screen.css"));
        add(cssConditionalResource("IE", "styles/ie.css"));
        add(cssPrintResource("styles/print.css"));
        
        // Copyright year in footer
        add(DateLabel.forDatePattern("year", Model.of(new Date()), "yyyy"));
        
        // Allow subclasses to register CSS classes on the body tag
        add(_body = new WebMarkupContainer("body") {
            public boolean isTransparentResolver()
            {
                return true;
            }
        });
        _body.setOutputMarkupId(true);
    }
    
    public WicketApplication getWicketApplication()
    {
        return (WicketApplication) getApplication();
    }
    
    public WicketSession getWicketSession()
    {
        return (WicketSession) super.getSession();
    }
}
