package fiftyfive.wicket.spring;

import fiftyfive.wicket.FoundationApplication;

import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * An extension of {@link FoundationApplication} that additionally enables
 * SpringBean annotation support.
 *
 * @see SpringComponentInjector
 */
public abstract class FoundationSpringApplication extends FoundationApplication
{
    /**
     * Delegates to {@link #initSpring} after calling super.
     */
    @Override
    protected void init()
    {
        super.init();
        initSpring();
    }

    /**
     * Initializes the {@link SpringComponentInjector}. This allows you to use
     * SpringBean annotations in your Wicket pages and components, which is
     * the easiest way to integrate Wicket and Spring.
     *
     * @see <a href="http://cwiki.apache.org/WICKET/spring.html#Spring-AnnotationbasedApproach">http://cwiki.apache.org/WICKET/spring.html#Spring-AnnotationbasedApproach</a>
     */
    protected void initSpring()
    {
        addComponentInstantiationListener(new SpringComponentInjector(
            this, getApplicationContext()
        ));
    }

    /**
     * Provides access to the Spring context by calling
     * {@link WebApplicationContextUtils#getRequiredWebApplicationContext}.
     */
    protected ApplicationContext getApplicationContext()
    {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(
            getServletContext()
        );
    }
}
