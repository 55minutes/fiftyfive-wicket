package ${package};

import java.util.Date;

import fiftyfive.wicket.js.JavaScriptDependency;
import fiftyfive.wicket.link.HomeLink;
import fiftyfive.wicket.shiro.markup.AuthenticationStatusPanel;
import static fiftyfive.wicket.util.Shortcuts.*;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Base class for all pages. Provides markup for the HTML5 doctype.
 * <p>
 * Also exposes a {@link #getBody} method to subclasses that can be used
 * to add <code>id</code> or <code>class</code> attributes to the
 * {@code <body>} element. For example, to add an {@code id}, do this:
 * <pre class="example">
 * getBody().setMarkupId("myId");</pre>
 * <p>
 * To add a CSS class (using {@link fiftyfive.wicket.util.Shortcuts#cssClass}):
 * <pre class="example">
 * getBody().add(cssClass("myClass"));</pre>
 */
public abstract class BasePage extends WebPage
{
    private final WebMarkupContainer body;
    
    public BasePage()
    {
        this(null);
    }
    
    public BasePage(PageParameters params)
    {
        super(params);
        
        // Set up <head> elements
        add(new HomeLink("home-link"));
        
        // Set up JS
        add(new JavaScriptDependency(BasePage.class));
        
        // Set up CSS
        add(cssResource("styles/screen.css"));
        add(cssConditionalResource("IE", "styles/ie.css"));
        add(cssPrintResource("styles/print.css"));
        
        // Allow subclasses to register CSS classes on the body tag
        this.body = new TransparentWebMarkupContainer("body");
        this.body.setOutputMarkupId(true);
        add(this.body);
        
        this.body.add(new DebugBar("debug"));

        // Login/logout link
        this.body.add(new AuthenticationStatusPanel("authStatus"));

        // Copyright year in footer
        this.body.add(DateLabel.forDatePattern("year", Model.of(new Date()), "yyyy"));
    }
    
    /**
     * Return a component that represents the {@code <body>} of the page.
     * Use this to add CSS classes or set the markup ID for styling purposes.
     */
    public WebMarkupContainer getBody()
    {
        return this.body;
    }
}
