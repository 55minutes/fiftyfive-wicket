/**
 * Copyright 2011 55 Minutes (http://www.55minutes.com)
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

import fiftyfive.wicket.examples.error.ForbiddenErrorPage;
import fiftyfive.wicket.examples.error.InternalServerErrorPage;
import fiftyfive.wicket.examples.home.HomePage;
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
 * Wicket framework configuration for 55 Minutes Wicket Examples.
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
    public Class getHomePage()
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
        
        // -- Configure custom request cycle handling --
        getRequestCycleListeners().add(new WicketRequestCycleListener());

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
