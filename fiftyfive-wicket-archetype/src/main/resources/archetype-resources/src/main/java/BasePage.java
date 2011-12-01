package ${package};

import java.util.Date;

import fiftyfive.wicket.shiro.markup.AuthenticationStatusPanel;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * The common page template for this application, including a standard header and footer.
 * Special pages that do not require these common elements (like the login page and error
 * pages) should extend from {@link EmptyPage} instead.
 * <p>
 * This template establishes its layout using a responsive
 * <a href="http://cssgrid.net/">CSS Grid</a>.
 * It can be useful to develop your application at first using such a grid
 * (in other words, do rapid prototyping). Later you can revise this markup to match your
 * design requirements.
 * <p>
 * Note that this base class does not provide a page {@code <title>}. It is up to each
 * individual page to provide one, using {@code <wicket:head>}.
 */
public class BasePage extends EmptyPage
{
    public BasePage()
    {
        this(null);
    }
    
    public BasePage(PageParameters params)
    {
        super(params);
        
        // Login/logout link
        add(new AuthenticationStatusPanel("authStatus"));
        
        // For general "you have been logged out", etc. messages
        add(new FeedbackPanel("feedback"));

        // Copyright year in footer
        add(DateLabel.forDatePattern("year", Model.of(new Date()), "yyyy"));
    }
}
