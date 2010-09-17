package ${package}.error;

import org.wicketstuff.annotation.mount.MountPath;

@MountPath(path="error/403")
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
