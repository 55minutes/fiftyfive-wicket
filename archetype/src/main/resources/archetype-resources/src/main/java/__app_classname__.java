package ${package};


import fiftyfive.wicket.resource.MergedResourceBuilder;
import fiftyfive.wicket.spring.FoundationSpringApplication;

import ${package}.home.HomePage;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.ajax.WicketAjaxReference;
import org.apache.wicket.behavior.AbstractHeaderContributor;
import org.apache.wicket.markup.html.WicketEventReference;

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
    private AbstractHeaderContributor _mergedJs;
    
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
    
    /**
     * Returns a HeaderContributor for all the common merged JS for this app.
     * This will typically be added to the base page of the application so it
     * is available from all pages.
     */
    public AbstractHeaderContributor getMergedJavaScriptContributor()
    {
        return _mergedJs;
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
        _mergedCss = new MergedResourceBuilder()
            .setPath("/styles/all.css")
            .addCss(${app_classname}.class, "styles/reset.css")
            .addCss(${app_classname}.class, "styles/core.css")
            .addCss(${app_classname}.class, "styles/layout.css")
            .addCss(${app_classname}.class, "styles/content.css")
            .addCss(${app_classname}.class, "styles/forms.css")
            .addCss(${app_classname}.class, "styles/page-specific.css")
            .build(this);
        
        // Mount merged JS
        _mergedJs = new MergedResourceBuilder()
            .setPath("/scripts/all.js")
            .addScript(WicketEventReference.INSTANCE)
            .addScript(WicketAjaxReference.INSTANCE)
            .addScript(${app_classname}.class, "scripts/lib/cookies/cookies.js")
            .addScript(
                ${app_classname}.class, 
                "scripts/lib/strftime/strftime" + (dev?".js":"-min.js"))
            .addScript(
                ${app_classname}.class, 
                "scripts/lib/fiftyfive-utils/55_utils.js")
            .addScript(
                ${app_classname}.class, 
                "scripts/lib/jquery-1.4.2/jquery-1.4.2" + (dev?".js":".min.js"))
            .addScript(
                ${app_classname}.class, 
                "scripts/lib/jquery-ui-1.8.1/jquery.ui-1.8.1.widget.js")
            .addScript(
                ${app_classname}.class, 
                "scripts/lib/fiftyfive-utils/jquery.55_utils.js")
            .build(this);
    }

    @Override
    public ${session_classname} newSession(Request request, Response response)
    {
        return new ${session_classname}(request);
    }
}
