#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

/**
 * Session information for ${project_name}.
 * Any variables added to this class will automatically be persisted in
 * the Servlet HttpSession. Each browser session gets its own instance of
 * this class.
 */
public class ${session_classname} extends WebSession
{
    public ${session_classname}(Request request)
    {
        super(request);
    }
}
