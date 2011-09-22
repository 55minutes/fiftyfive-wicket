package ${package};

import java.util.Arrays;
import java.util.List;

import fiftyfive.wicket.js.JavaScriptDependency;
import fiftyfive.wicket.link.HomeLink;
import static fiftyfive.wicket.util.Shortcuts.*;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * The "bare metal" empty HTML5 template that is used by all pages in the application.
 * Provides {@code <head>} elements like global CSS and JavaScript, plus very
 * minimal markup that every page will need.
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
public abstract class EmptyPage extends WebPage
{
    private final WebMarkupContainer body;
    
    public EmptyPage()
    {
        this(null);
    }
    
    public EmptyPage(PageParameters params)
    {
        super(params);
        
        // Set up <head> elements
        add(new HomeLink("home-link"));
        
        // Set up JS
        add(new JavaScriptDependency(EmptyPage.class));
        
        // Set up CSS
        add(cssResource("styles-compiled/application.css"));
        List<String> breakPoints = Arrays.asList("480", "768", "1024", "1200");
        for(String width : breakPoints)
        {
            add(cssResource(
                "styles-compiled/" + width + ".css",
                "only screen and (min-width: " + width + "px)"));
        }
        add(cssConditionalResource("IE", "styles-compiled/ie.css"));
        add(cssPrintResource("styles-compiled/print.css"));
        
        // Allow subclasses to register CSS classes on the body tag
        this.body = new TransparentWebMarkupContainer("body");
        this.body.setOutputMarkupId(true);
        add(this.body);
        
        this.body.add(new DebugBar("debug"));
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
