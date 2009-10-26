package ${package};

import java.util.ArrayList;
import java.util.List;

import fiftyfive.wicket.FoundationApplication;

import ${package}.home.HomePage;

import org.apache.wicket.Request;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.Response;
import org.apache.wicket.javascript.DefaultJavascriptCompressor;
import org.apache.wicket.markup.html.PackageResourceGuard;

import org.wicketstuff.annotation.scan.AnnotatedMountScanner;
import org.wicketstuff.mergedresources.ResourceMount;
import org.wicketstuff.mergedresources.versioning.SimpleResourceVersion;


/**
 * Wicket framework configuration for ${project_name}.
 */
public class ${app_classname} extends FoundationApplication
{
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
    }
    
    /**
     * Use the wicketstuff mergedresources feature to serve out our common
     * CSS and JS in an efficient manner.
     */
    protected void initMergedResources()
    {
        // Mount Wicket's ajax libs with aggressive caching.
        ResourceMount.mountWicketResources("wicket", this);

        // Configure resources to be merged only in production.
        ResourceMount mountConfig = new ResourceMount();
        if(isDevelopmentMode())
        {
            // Discourage caching in development
            mountConfig.setCacheDuration(0);
        }
        else
        {
            mountConfig.setDefaultAggressiveCacheDuration();
        }
        mountConfig.setMerged(!isDevelopmentMode());
        mountConfig.setCompressed(true);
        mountConfig.setMinifyCss(false);
        mountConfig.setMinifyJs(false);
        
        // Mount all CSS files under all.css (split out in dev mode).
        ResourceMount css = mountConfig.clone();
        css.setPath("/styles/all.css");
        for(ResourceReference ref : getCssReferences())
        {
            css.addResourceSpec(ref);
        }
        css.mount(this);

        // Mount all JS files under all.js (split out in dev mode).
        ResourceMount js = mountConfig.clone();
        js.setPath("/scripts/all.js");
        for(ResourceReference ref : getJsLibraryReferences())
        {
            js.addResourceSpec(ref);
        }
        js.mount(this);
    }
    
    /**
     * Returns an array of all JavaScript libraries that should be included
     * in every page. In production mode, the minified versions will be used
     * as appropriate.
     */
    public ResourceReference[] getJsLibraryReferences()
    {
        List<String> libs = new ArrayList<String>();
        
        // Add all JS files we want to include in every page
        libs.add("scripts/cookies.js");

        // Add minified or normal js files as appropriate
        if(isDevelopmentMode())
        {
            libs.add("scripts/lib/jquery-trunk/jquery.js");
        }
        else
        {
            libs.add("scripts/lib/jquery-trunk/jquery.min.js");
        }

        // Turn them into references
        ResourceReference[] refs = new ResourceReference[libs.size()];
        for(int i=0; i<refs.length; i++)
        {
            refs[i] = new ResourceReference(
                ${app_classname}.class, libs.get(i)
            );
        }
        return refs;
    }

    /**
     * Returns an array of all CSS files that should be included
     * in every page. Note that the IE-specific styles are not included.
     */
    public ResourceReference[] getCssReferences()
    {
        return new ResourceReference[] { 
            new ResourceReference(${app_classname}.class, "styles/reset.css"),
            new ResourceReference(${app_classname}.class, "styles/core.css"),
            new ResourceReference(${app_classname}.class, "styles/layout.css"),
            new ResourceReference(${app_classname}.class, "styles/content.css"),
            new ResourceReference(${app_classname}.class, "styles/forms.css"),
            new ResourceReference(${app_classname}.class, "styles/page-specific.css")
        };
    }
    
    /**
     * Returns a reference to the IE7 stylesheet.
     */
    public ResourceReference getIE7CssReference()
    {
        return new ResourceReference(${app_classname}.class, "styles/ie-7-win.css");
    }

    @Override
    public ${session_classname} newSession(Request request, Response response)
    {
        return new ${session_classname}(request);
    }
}
