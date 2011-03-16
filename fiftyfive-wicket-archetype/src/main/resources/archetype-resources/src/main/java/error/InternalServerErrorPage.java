package ${package}.error;

public class InternalServerErrorPage extends BaseErrorPage
{
    /**
     * Returns 500.
     */
    protected int getErrorCode()
    {
        return 500;
    }
}
