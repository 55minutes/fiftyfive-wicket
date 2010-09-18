package ${package};


import fiftyfive.wicket.css.MergedCssBuilder;
import fiftyfive.wicket.js.MergedJavaScriptBuilder;
import fiftyfive.wicket.spring.FoundationSpringApplication;

import ${package}.error.InternalServerErrorPage;
import ${package}.home.HomePage;

import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.ajax.WicketAjaxReference;
import org.apache.wicket.behavior.AbstractHeaderContributor;
import org.apache.wicket.markup.html.WicketEventReference;
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
    
    
    private AbstractHeaderContributor _mergedCss;
    


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
    
    /**
     * Returns a HeaderContributor for all the common merged CSS for this app.
     * This will typically be added to the base page of the application so it
     * is available from all pages.
     */
    public AbstractHeaderContributor getMergedCssContributor()
    {
        return _mergedCss;
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
        getApplicationSettings().setInternalErrorPage(
            InternalServerErrorPage.class
        );
        
        // Custom initialization goes here
        
        LOGGER.info("Initialized!");
    }
    
    /**
     * Use the wicketstuff mergedresources feature to serve out our common
     * CSS and JS in an efficient manner.
     */
    protected void initMergedResources()
    {
        boolean dev = isDevelopmentMode();
        
        // Mount merged CSS
        _mergedCss = new MergedCssBuilder()
            .setPath("/styles/all.css")
            .addCss(${app_classname}.class, "styles/reset.css")
            .addCss(${app_classname}.class, "styles/core.css")
            .addCss(${app_classname}.class, "styles/layout.css")
            .addCss(${app_classname}.class, "styles/content.css")
            .addCss(${app_classname}.class, "styles/forms.css")
            .addCss(${app_classname}.class, "styles/page-specific.css")
            .build(this);
        
        // Mount merged JS
        new MergedJavaScriptBuilder()
            .setPath("/scripts/all.js")
            .addWicketAjaxLibraries()
            .addJQueryUI()
            .addLibrary("cookies")
            .addLibrary("strftime")
            .addLibrary("55_utils")
            .addLibrary("jquery.55_utils")
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
