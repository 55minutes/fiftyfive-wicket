package ${package};

import ${package}.error.ForbiddenErrorPage;
import ${package}.error.InternalServerErrorPage;
import ${package}.home.HomePage;

import fiftyfive.wicket.js.JavaScriptDependencySettings;
import fiftyfive.wicket.spring.FoundationSpringApplication;

import org.apache.wicket.Application;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.ValueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wicket framework configuration for ${project_name}.
 */
public class WicketApplication extends FoundationSpringApplication
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WicketApplication.class);
    
    
    /**
     * Returns the instance of {@code WicketApplication} that is currently
     * running. This method only works inside a Wicket thread.
     */
    public static WicketApplication get()
    {
        return (WicketApplication) Application.get();
    }

    @Override
    public Class<HomePage> getHomePage()
    {
        return HomePage.class;
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        // -- Configure pretty URL mappings ("routes") --
        mount(new WicketMappings(this));
        
        // -- Configure resources --
        // Wicket will not allow you to use "../" to construct relative paths to resources
        // (e.g. new PackageResourceReference(MyPage.class, "../common/file.png"))
        // unless you specify a parentFolderPlaceholder. This is a security precaution.
        getResourceSettings().setParentFolderPlaceholder("$up$");
        
        // Tell fiftyfive-wicket-js where to find custom JS libs for this app
        // (i.e. those that can be referenced via //= require <lib>).
        // This corresponds to src/main/resources/.../scripts.
        JavaScriptDependencySettings.get().addLibraryPath(WicketApplication.class, "scripts");
        
        // -- Configure error pages --
        getApplicationSettings().setPageExpiredErrorPage(getHomePage());
        getApplicationSettings().setAccessDeniedPage(ForbiddenErrorPage.class);
        getApplicationSettings().setInternalErrorPage(InternalServerErrorPage.class);
        
        // -- Configure exception handling --
        setExceptionMapperProvider(new ValueProvider<IExceptionMapper>(
            new WicketExceptionMapper()
        ));

        // -- Custom initialization goes here --
        
        LOGGER.info("Initialized!");
    }

    /**
     * Returns our custom {@link WicketSession}.
     */
    @Override
    public WicketSession newSession(Request request, Response response)
    {
        return new WicketSession(request);
    }
}
