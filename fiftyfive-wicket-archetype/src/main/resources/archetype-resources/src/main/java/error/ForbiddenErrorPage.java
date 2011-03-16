package ${package}.error;

public class ForbiddenErrorPage extends BaseErrorPage
{
    /**
     * Returns 403.
     */
    protected int getErrorCode()
    {
        return 403;
    }
}
