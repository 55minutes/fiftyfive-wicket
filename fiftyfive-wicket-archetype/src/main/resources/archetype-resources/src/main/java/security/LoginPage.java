package ${package}.security;

import ${package}.BasePage;
import fiftyfive.wicket.shiro.markup.LoginForm;

/**
 * A simple login page containing a {@link LoginForm} and {@link FeedbackPanel}
 * that uses HTML5 markup. Customize the look and feel to meet the needs of your app.
 */
public class LoginPage extends BasePage
{
    public LoginPage()
    {
        super();
        getBody().setMarkupId("login");
        add(new LoginForm("login"));
    }

    /**
     * Hide the BasePage's "log in" link, as it is redundant.
     */
    @Override
    protected void onConfigure()
    {
        getBody().get("authStatus").setVisible(false);
        super.onConfigure();
    }
}
