package ${package};


import fiftyfive.wicket.header.InternetExplorerCss;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;


/**
 * Base class for all pages. Provides markup for XHTML 1.0 Strict doctype.
 * <p>
 * Also exposes a <code>_body</code> variable to subclasses that can be used
 * to add <code>id</code> or <code>class</code> attributes to the
 * &lt;body&gt; element. For example, to add an <code>id</code>, do this:
 * <pre>
 * _body.setMarkupId("myId");
 * </pre>
 * To add a CSS class (using fiftyfive.wicket.util.Shortcuts.cssClass):
 * <pre>
 * _body.add(cssClass("myClass"));
 * </pre>
 */
public abstract class BasePage extends WebPage
{
    protected final WebMarkupContainer _body;
    
    public BasePage(PageParameters params)
    {
        super(params);
        add(new DebugBar("debug"));
        addCssContributions();
        addJavascriptContributions();

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

    /**
     * Specifies all the JavaScript files that should be included on this page.
     */
    private void addJavascriptContributions()
    {
        for(ResourceReference ref : get${app_classname}().getJsLibraryReferences())
        {
            add(JavascriptPackageResource.getHeaderContribution(ref));
        }
    }

    /**
     * Specifies all the CSS files that should be included on this page.
     */
    private void addCssContributions()
    {
        for(ResourceReference ref : get${app_classname}().getCssReferences())
        {
            add(CSSPackageResource.getHeaderContribution(ref));
        }
        add(InternetExplorerCss.getConditionalHeaderContribution(
            "IE 7", get${app_classname}().getIE7CssReference()
        ));
    }
}
