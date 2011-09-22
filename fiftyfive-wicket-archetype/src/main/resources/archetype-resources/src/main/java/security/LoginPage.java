package ${package}.security;

import ${package}.EmptyPage;
import fiftyfive.wicket.shiro.markup.LoginForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * A simple login page containing a {@link LoginForm} and {@link FeedbackPanel}
 * that uses HTML5 markup. Customize the look and feel to meet the needs of your app.
 */
public class LoginPage extends EmptyPage
{
    public LoginPage()
    {
        super();
        getBody().setMarkupId("login");
        add(new FeedbackPanel("feedback"));
        add(new LoginForm("login"));
    }
}
