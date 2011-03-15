package ${package};

import ${package}.error.ForbiddenErrorPage;
import ${package}.error.InternalServerErrorPage;
import ${package}.home.HomePage;

import fiftyfive.wicket.js.JavaScriptDependencySettings;
import fiftyfive.wicket.js.MergedJavaScriptBuilder;
import fiftyfive.wicket.spring.FoundationSpringApplication;

import org.apache.wicket.Application;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.util.ValueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wicket framework configuration for ${project_name}.
 */
public class ${app_classname} extends FoundationSpringApplication
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        ${app_classname}.class
    );
    
    
    /**
     * Returns the instance of {@code ${app_classname}} that is currently
     * running. This method only works inside a Wicket thread.
     */
    public static ${app_classname} get()
    {
        return (${app_classname}) Application.get();
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
        
        // Enable annotations for mounting pages
        // TODO: uncomment once wicketstuff annotation supports Wicket 1.5!!
        // AnnotatedMountScanner scanner = new AnnotatedMountScanner();
        // scanner.scanPackage("${package}").mount(this);
        
        // Configure merged resources
        initMergedResources();
        
        // Configure error pages
        getApplicationSettings().setPageExpiredErrorPage(getHomePage());
        getApplicationSettings().setAccessDeniedPage(ForbiddenErrorPage.class);
        getApplicationSettings().setInternalErrorPage(
            InternalServerErrorPage.class
        );
        
        // Configure exception handling
        setExceptionMapperProvider(new ValueProvider<IExceptionMapper>(
            new ${exceptionmapper_classname}()
        ));

        // Custom initialization goes here
        
        LOGGER.info("Initialized!");
    }
    
    /**
     * Use the wicketstuff mergedresources feature to serve out our common
     * JavaScript in an efficient manner.
     */
    protected void initMergedResources()
    {
        // Tell fiftyfive-wicket-js where to find custom JS libs for this app
        // (i.e. those that can be referenced via //= require <lib>).
        // This corresponds to src/main/resources/.../scripts.
        JavaScriptDependencySettings.get().addLibraryPath(
            WicketApplication.class, "scripts"
        );
        
        // Mount merged JS
        // Not yet compatible with Wicket 1.5!
        // new MergedJavaScriptBuilder()
        //     .setPath("/scripts/all.js")
        //     .addJQueryUI()
        //     .addLibrary("cookies")
        //     .addLibrary("strftime")
        //     .addLibrary("55_utils")
        //     .addLibrary("jquery.55_utils")
        //     .addAssociatedScript(BasePage.class)
        //     .addWicketAjaxLibraries()
        //     .build(this);
    }

    /**
     * Returns our custom {@link ${session_classname}}.
     */
    @Override
    public ${session_classname} newSession(Request request, Response response)
    {
        return new ${session_classname}(request);
    }
}
