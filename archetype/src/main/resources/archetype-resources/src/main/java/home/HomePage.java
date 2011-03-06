package ${package}.home;

import ${package}.BasePage;

import fiftyfive.wicket.js.JavaScriptDependency;
import static fiftyfive.wicket.util.Shortcuts.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Home page for ${project_name}.
 */
public class HomePage extends BasePage
{
    public HomePage(PageParameters parameters)
    {
        super(parameters);
        add(new JavaScriptDependency(HomePage.class));
        // Add your components here
    }
}
