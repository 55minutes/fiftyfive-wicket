package ${package};

import java.util.Arrays;
import java.util.List;

import fiftyfive.wicket.util.LoggingUtils;

import org.apache.wicket.authorization.AuthorizationException;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.StalePageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Customization of Wicket's request cycle processing. The superclass provides a template
 * for providing callbacks that will be invoked during Wicket's request-response flow.
 * Consider this as a place to put your application's web "middleware". The current implementation
 * simply does more detailed logging when an exception occurs.
 */
public class WicketRequestCycleListener extends AbstractRequestCycleListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        WicketRequestCycleListener.class
    );
    
    /**
     * Exception types we consider "recoverable", meaning we don't have to
     * log a detailed stack trace for these.
     */
    private List RECOVERABLE_EXCEPTIONS = Arrays.asList(
        StalePageException.class,
        PageExpiredException.class,
        AuthorizationException.class
    );
    
    /**
     * Consider putting custom exception handling logic here.
     * For example, you could catch an {@code ObjectNotFoundException} here and redirect
     * to a 404 page. For now we just log.
     */
    @Override
    public IRequestHandler onException(RequestCycle cycle, Exception ex)
    {
        if(! RECOVERABLE_EXCEPTIONS.contains(ex.getClass()))
        {
            LoggingUtils.logException(LOGGER, ex);
        }
        // null means we want Wicket's default onException behavior to be used
        return null;
    }
}
