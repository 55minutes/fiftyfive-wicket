package ${package};

import java.util.Date;

import fiftyfive.wicket.shiro.markup.AuthenticationStatusPanel;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

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
