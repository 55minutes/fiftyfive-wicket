package ${package};

import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.RequestLogger.ISessionLogInfo;
import org.apache.wicket.protocol.http.WebSession;

/**
 * Session information for ${project_name}.
 * Any variables added to this class will automatically be persisted in
 * the Servlet HttpSession. Each browser session gets its own instance of
 * this class.
 */
public class ${session_classname} extends WebSession implements ISessionLogInfo
{
    public ${session_classname}(Request request)
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
