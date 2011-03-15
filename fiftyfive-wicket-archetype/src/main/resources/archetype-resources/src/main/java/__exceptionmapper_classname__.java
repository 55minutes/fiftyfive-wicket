package ${package};

import java.util.Arrays;
import java.util.List;

import fiftyfive.wicket.util.LoggingUtils;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.authorization.AuthorizationException;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.mapper.StalePageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom exception handling for ${project_name}.
 */
public class ${exceptionmapper_classname} extends DefaultExceptionMapper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        ${exceptionmapper_classname}.class
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
     * Consider putting custom exception handling logic here. For now we
     * just log the exception and delegate to the superclass for Wicket's
     * default exception handling.
     */
    @Override
    public IRequestHandler map(Exception e)
    {
        if(! RECOVERABLE_EXCEPTIONS.contains(e.getClass()))
        {
            LoggingUtils.logException(LOGGER, e);
        }
        return super.map(e);
    }
}
