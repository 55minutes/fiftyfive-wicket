package ${package};

import fiftyfive.wicket.js.JavaScriptDependency;

import static fiftyfive.wicket.util.Shortcuts.*;

import org.apache.wicket.PageParameters;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;

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
        
        // Set up JS
        add(new JavaScriptDependency(BasePage.class));
        
        // Set up CSS
        add(cssResource("styles/screen.css"));
        add(cssConditionalResource("IE", "styles/ie.css"));
        add(cssPrintResource("styles/print.css"));

        add(_body = new WebMarkupContainer("body") {
            public boolean isTransparentResolver()
            {
                return true;
            }
        });
        _body.setOutputMarkupId(true);
    }
    
    public ${app_classname} get${app_classname}()
    {
        return (${app_classname}) getApplication();
    }
    
    public ${session_classname} get${session_classname}()
    {
        return (${session_classname}) super.getSession();
    }
}
