package ${package}.admin;

import ${package}.BasePage;
import static fiftyfive.wicket.util.Shortcuts.*;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;

/**
 * An example of a page that is secured via Shiro annotations.
 * This page can only be accessed by an authenticated user that has the "admin" role.
 */
@RequiresAuthentication
@RequiresRoles("admin")
public class AdministrationPage extends BasePage
{
    public AdministrationPage()
    {
        super();
    }
}
