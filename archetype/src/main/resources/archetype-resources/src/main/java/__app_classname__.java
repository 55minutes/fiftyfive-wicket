package ${package};


import fiftyfive.wicket.js.JavaScriptDependencySettings;
import fiftyfive.wicket.js.MergedJavaScriptBuilder;
import fiftyfive.wicket.spring.FoundationSpringApplication;

import ${package}.error.ForbiddenErrorPage;
import ${package}.error.InternalServerErrorPage;
import ${package}.home.HomePage;

import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebRequest;

import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

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
    public Class getHomePage()
    {
        return HomePage.class;
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        // Enable annotations for mounting pages
        AnnotatedMountScanner scanner = new AnnotatedMountScanner();
        scanner.scanPackage("${package}").mount(this);
        
        // Configure merged resources
        initMergedResources();
        
        // Configure error pages
        getApplicationSettings().setPageExpiredErrorPage(getHomePage());
        getApplicationSettings().setAccessDeniedPage(ForbiddenErrorPage.class);
        getApplicationSettings().setInternalErrorPage(
            InternalServerErrorPage.class
        );
        
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
        new MergedJavaScriptBuilder()
            .setPath("/scripts/all.js")
            .addJQueryUI()
            .addLibrary("cookies")
            .addLibrary("strftime")
            .addLibrary("55_utils")
            .addLibrary("jquery.55_utils")
            .addAssociatedScript(BasePage.class)
            .addWicketAjaxLibraries()
            .build(this);
    }

    /**
     * Returns our custom {@link ${requestcycle_classname}}.
     */
    @Override
    public ${requestcycle_classname} newRequestCycle(Request request, Response response)
    {
        return new ${requestcycle_classname}(this, (WebRequest) request, response);
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
