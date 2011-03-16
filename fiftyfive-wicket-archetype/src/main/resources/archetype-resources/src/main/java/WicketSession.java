package ${package};

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.IRequestLogger.ISessionLogInfo;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

/**
 * Session information for ${project_name}.
 * Any variables added to this class will automatically be persisted in
 * the Servlet HttpSession. Each browser session gets its own instance of
 * this class.
 */
public class WicketSession extends WebSession implements ISessionLogInfo
{
    /**
     * Returns the instance of {@code WicketSession} associated with
     * the current request. This method only works inside a Wicket thread.
     */
    public static WicketSession get()
    {
        return (WicketSession) Session.get();
    }
    
    public WicketSession(Request request)
    {
        super(request);
    }
    
    /**
     * Additional information about this session that will automatically
     * be included in Wicket's request log, as well in troubleshooting
     * information emitted by
     * {@link fiftyfive.wicket.util.LoggingUtils LoggingUtils}. 
     * Consider including things like username, if authenticated.
     */
    public Object getSessionInfo()
    {
        return "TODO: Your session info goes here";
    }
}
