/*
 * Copyright 2009 55 Minutes (http://www.55minutes.com)
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
import org.apache.wicket.markup.html.WicketEventReference;

import org.wicketstuff.annotation.scan.AnnotatedMountScanner;
import org.wicketstuff.mergedresources.ResourceMount;

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
        new MergedResourceBuilder()
            .setPath("/styles/all.css")
            .addCss(WicketApplication.class, "styles/reset.css")
            .addCss(WicketApplication.class, "styles/core.css")
            .addCss(WicketApplication.class, "styles/layout.css")
            .addCss(WicketApplication.class, "styles/content.css")
            .addCss(WicketApplication.class, "styles/forms.css")
            .addCss(WicketApplication.class, "styles/page-specific.css")
            .attachToPage(BasePage.class)
            .build(this);
        
        // Mount merged JS
        new MergedResourceBuilder()
            .setPath("/scripts/all.js")
            .addScript(WicketAjaxReference.INSTANCE)
            .addScript(WicketEventReference.INSTANCE)
            .addScript(WicketApplication.class, "scripts/cookies.js")
            .addScript(
                WicketApplication.class, 
                "scripts/lib/jquery-trunk/jquery" + (dev?".js":".min.js"))
            .attachToPage(BasePage.class)
            .build(this);
    }

    @Override
    public WicketSession newSession(Request request, Response response)
    {
        return new WicketSession(request);
    }
}
