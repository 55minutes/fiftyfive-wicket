package ${package};

import fiftyfive.wicket.util.LoggingUtils;
import org.apache.wicket.Page;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom request cycle implementation for ${project_name}.
 * This is where exception handling and logging logic is defined.
 */
public class ${requestcycle_classname} extends WebRequestCycle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        ${requestcycle_classname}.class
    );
    
    public ${requestcycle_classname}(WebApplication application,
                              WebRequest request,
                              Response response)
    {
        super(application, request, response);
    }
    
    /**
     * Delegate to {@link LoggingUtils} to log detailed troubleshooting
     * information.
     */
    @Override
    protected void logRuntimeException(RuntimeException e)
    {
        LoggingUtils.logRuntimeException(LOGGER, e);
    }
    
    // Consider implementing this override to customize Wicket's exception
    // handling. For example, return a different error page based on the
    // exception type.
    /*
    @Override
    public Page onRuntimeException(Page page, RuntimeException e)
    {
    }
    */
}
