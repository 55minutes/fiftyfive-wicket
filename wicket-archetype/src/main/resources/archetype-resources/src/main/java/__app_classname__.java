package ${package};


import fiftyfive.wicket.resource.MergedResourceBuilder;
import fiftyfive.wicket.spring.FoundationSpringApplication;

import ${package}.home.HomePage;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.ajax.WicketAjaxReference;
import org.apache.wicket.markup.html.WicketEventReference;

import org.wicketstuff.annotation.scan.AnnotatedMountScanner;
import org.wicketstuff.mergedresources.ResourceMount;

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
        new MergedResourceBuilder()
            .setPath("/styles/all.css")
            .addCss(${app_classname}.class, "styles/reset.css")
            .addCss(${app_classname}.class, "styles/core.css")
            .addCss(${app_classname}.class, "styles/layout.css")
            .addCss(${app_classname}.class, "styles/content.css")
            .addCss(${app_classname}.class, "styles/forms.css")
            .addCss(${app_classname}.class, "styles/page-specific.css")
            .attachToPage(BasePage.class)
            .build(this);
        
        // Mount merged JS
        new MergedResourceBuilder()
            .setPath("/scripts/all.js")
            .addScript(WicketAjaxReference.INSTANCE)
            .addScript(WicketEventReference.INSTANCE)
            .addScript(${app_classname}.class, "scripts/cookies.js")
            .addScript(
                ${app_classname}.class, 
                "scripts/lib/jquery-trunk/jquery" + (dev?".js":".min.js"))
            .attachToPage(BasePage.class)
            .build(this);
    }

    @Override
    public ${session_classname} newSession(Request request, Response response)
    {
        return new ${session_classname}(request);
    }
}
