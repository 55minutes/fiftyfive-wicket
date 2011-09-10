package ${package}.security;

import ${package}.BasePage;
import fiftyfive.wicket.shiro.markup.LoginForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;


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
        add(new FeedbackPanel("feedback"));
    }

    /**
     * Hide the "log in" link on this page, as it would be redundant.
     */
    @Override
    protected void onConfigure()
    {
        getBody().get("authStatus").setVisible(false);
        super.onConfigure();
    }
}
