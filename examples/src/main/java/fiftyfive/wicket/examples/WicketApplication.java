/**
 * Copyright 2010 55 Minutes (http://www.55minutes.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fiftyfive.wicket.examples;

import fiftyfive.wicket.css.MergedCssBuilder;
import fiftyfive.wicket.examples.error.InternalServerErrorPage;
import fiftyfive.wicket.examples.home.HomePage;
import fiftyfive.wicket.js.MergedJavaScriptBuilder;
import fiftyfive.wicket.spring.FoundationSpringApplication;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.behavior.AbstractHeaderContributor;
import org.apache.wicket.protocol.http.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

/**
 * Wicket framework configuration for 55 Minutes Wicket Examples.
 */
public class WicketApplication extends FoundationSpringApplication
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        WicketApplication.class
    );
    
    
    private AbstractHeaderContributor _mergedCss;
    
    
    /**
     * Returns the instance of {@code WicketApplication} that is currently
     * running. This method only works inside a Wicket thread.
     */
    public static WicketApplication get()
    {
        return (WicketApplication) Application.get();
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
        scanner.scanPackage("fiftyfive.wicket.examples").mount(this);
        
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
            .addCss(WicketApplication.class, "styles/reset.css")
            .addCss(WicketApplication.class, "styles/core.css")
            .addCss(WicketApplication.class, "styles/layout.css")
            .addCss(WicketApplication.class, "styles/content.css")
            .addCss(WicketApplication.class, "styles/forms.css")
            .addCss(WicketApplication.class, "styles/page-specific.css")
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
     * Returns our custom {@link WicketRequestCycle}.
     */
    @Override
    public WicketRequestCycle newRequestCycle(Request request, Response response)
    {
        return new WicketRequestCycle(this, (WebRequest) request, response);
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
