package ${package}.home;

import ${package}.BasePage;

import fiftyfive.wicket.js.JavaScriptDependency;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import static fiftyfive.wicket.util.Shortcuts.*;

/**
 * Home page for ${project_name}.
 */
public class HomePage extends BasePage
{
    public HomePage(PageParameters parameters)
    {
        super(parameters);
        getBody().setMarkupId("home");
        add(new JavaScriptDependency(HomePage.class));
        // Add your components here
    }
}
