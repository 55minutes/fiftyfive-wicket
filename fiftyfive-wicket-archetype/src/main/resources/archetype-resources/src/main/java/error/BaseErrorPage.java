package ${package}.error;

import ${package}.BasePage;
import fiftyfive.wicket.util.HttpUtils;

/**
 * Base class for custom error pages.
 */
public abstract class BaseErrorPage extends BasePage
{
    /**
     * Error pages are not bookmarkable, hence no PageParameters.
     */
    protected BaseErrorPage()
    {
        super(null);
    }
    
    /**
     * Subclasses must implement to provide the HTTP status error code.
     * For example, 404 for the {@link NotFoundErrorPage}.
     */
    protected abstract int getErrorCode();
    
    /**
     * Make sure we emit the proper HTTP status.
     */
    @Override
    protected void configureResponse()
    {
        super.configureResponse();
        HttpUtils.getHttpServletResponse().setStatus(getErrorCode());
    }

    /**
     * Returns {@code true}.
     */
    @Override
    public boolean isErrorPage()
    {
        return true;
    }
}
