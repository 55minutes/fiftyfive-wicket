/*
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


import fiftyfive.wicket.resource.MergedResourceBuilder;
import fiftyfive.wicket.spring.FoundationSpringApplication;

import fiftyfive.wicket.examples.home.HomePage;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.ajax.WicketAjaxReference;
import org.apache.wicket.behavior.AbstractHeaderContributor;
import org.apache.wicket.markup.html.WicketEventReference;

import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wicket framework configuration for 55 Minutes Wicket Examples.
 */
public class WicketApplication extends FoundationSpringApplication
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        WicketApplication.class
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
        scanner.scanPackage("fiftyfive.wicket.examples").mount(this);
        
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
            .addCss(WicketApplication.class, "styles/reset.css")
            .addCss(WicketApplication.class, "styles/core.css")
            .addCss(WicketApplication.class, "styles/layout.css")
            .addCss(WicketApplication.class, "styles/content.css")
            .addCss(WicketApplication.class, "styles/forms.css")
            .addCss(WicketApplication.class, "styles/page-specific.css")
            .build(this);

        // Mount merged JS
        _mergedJs = new MergedResourceBuilder()
            .setPath("/scripts/all.js")
            .addScript(WicketEventReference.INSTANCE)
            .addScript(WicketAjaxReference.INSTANCE)
            .addScript(
                WicketApplication.class, 
                "scripts/lib/cookies/cookies.js")
            .addScript(
                WicketApplication.class, 
                "scripts/lib/strftime/strftime" + (dev?".js":"-min.js"))
            .addScript(
                WicketApplication.class, 
                "scripts/lib/fiftyfive-utils/55_utils.js")
            .addScript(
                WicketApplication.class, 
                "scripts/lib/jquery-1.4.2/jquery-1.4.2" + (dev?".js":".min.js"))
            .addScript(
                WicketApplication.class, 
                "scripts/lib/jquery-ui-1.7.2/jquery-ui-1.7.2.core.min.js")
            .addScript(
                WicketApplication.class, 
                "scripts/lib/fiftyfive-utils/jquery.55_utils.js")
            .build(this);
    }

    @Override
    public WicketSession newSession(Request request, Response response)
    {
        return new WicketSession(request);
    }
}
