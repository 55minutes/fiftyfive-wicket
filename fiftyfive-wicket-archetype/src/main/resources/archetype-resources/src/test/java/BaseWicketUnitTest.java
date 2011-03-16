package ${package};

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

/**
 * Base class for Wicket unit tests. Allows Wicket pages and components to be
 * easily tested in isolation. For pages or components that rely on Spring
 * dependency injection, consider overriding {@link #initSpringContext}.
 */
public abstract class BaseWicketUnitTest
{
    protected WicketSession session;
    protected WicketTester tester;
    
    @Before
    public void createTester()
    {
        this.tester = new WicketTester(new WicketApplication() {
            @Override
            public RuntimeConfigurationType getConfigurationType()
            {
                // Don't test in development mode, since debug utilities
                // can break XHTML compliance.
                return RuntimeConfigurationType.DEPLOYMENT;
            }
            @Override
            protected ApplicationContext getApplicationContext()
            {
                // Provide a static Spring context that can be configured
                // with mock beans for testing purposes.
                StaticWebApplicationContext context;
                context = new StaticWebApplicationContext();
                initSpringContext(context);
                return context;
            }
            @Override
            public WicketSession newSession(Request request, Response response)
            {
                // Enforce a singleton session object to be used for the entire
                // test. This allows us to modify the contents of the session
                // with prerequisite values before starting a test.
                if(null == BaseWicketUnitTest.this.session)
                {
                    BaseWicketUnitTest.this.session = super.newSession(request, response);
                }
                return BaseWicketUnitTest.this.session;
            }
        });
    }
    
    @After
    public void destroyTester()
    {
        if(this.tester != null) this.tester.destroy();
        this.tester = null;
        this.session = null;
    }
    
    /**
     * Subclasses should override this method to register mock Spring beans
     * in the Spring context. This will allow Wicket components that contain
     * SpringBean annotations to use these mock beans during testing.
     * <p>
     * The default implementation of this method calls
     * {@link MockitoAnnotations#initMocks MockitoAnnotations.initMocks()} to
     * initialize any <code>&#064;Mock</code> variables that have been
     * declared in the test class.
     * <p>
     * Example usage:
     * <pre class="example">
     * &#064;Mock MyService myService;
     * 
     * protected void initSpringContext(StaticWebApplicationContext ctx)
     * {
     *     super.initSpringContext(ctx);
     *     ctx.getBeanFactory().registerSingleton("myService", myService);
     * }</pre>
     */
    protected void initSpringContext(StaticWebApplicationContext ctx)
    {
        MockitoAnnotations.initMocks(this);
    }
}
