package ${package}.home;

import ${package}.BasePage;

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
        getBody().add(cssClass("documentation"));
        // Add your components here
    }
}
