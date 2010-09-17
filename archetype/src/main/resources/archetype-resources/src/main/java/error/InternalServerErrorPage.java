package ${package}.error;

import org.wicketstuff.annotation.mount.MountPath;

@MountPath(path="error/500")
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
