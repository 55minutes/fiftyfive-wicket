package ${package}.home;

import ${package}.BasePage;

import static fiftyfive.wicket.util.Shortcuts.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.JavascriptPackageResource;

/**
 * Home page for ${project_name}.
 */
public class HomePage extends BasePage
{
    public HomePage(PageParameters parameters)
    {
        super(parameters);
        add(JavascriptPackageResource.getHeaderContribution(
            HomePage.class, "HomePage.js"
        ));
        // Add your components here
    }
}
