package ${package}.error;

import org.wicketstuff.annotation.mount.MountPath;

@MountPath(path="error/404")
public class NotFoundErrorPage extends BaseErrorPage
{
    /**
     * Returns 404.
     */
    protected int getErrorCode()
    {
        return 404;
    }
}
