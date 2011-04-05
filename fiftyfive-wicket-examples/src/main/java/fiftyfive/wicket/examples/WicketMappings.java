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
import fiftyfive.wicket.examples.error.NotFoundErrorPage;
import fiftyfive.wicket.js.MergedJavaScriptBuilder;
import fiftyfive.wicket.mapper.PatternMountedMapper;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.CompoundRequestMapper;


/**
 * All custom mappings (in other words, "mount points" or "routes")
 * for the Wicket examples project.
 * This includes merged JavaScript and pretty URLs for all bookmarkable pages.
 */
public class WicketMappings extends CompoundRequestMapper
{
    public WicketMappings(WebApplication app)
    {
        // Pretty URLs for bookmarkable pages
        addPage("error/403", ForbiddenErrorPage.class);
        addPage("error/404", NotFoundErrorPage.class);
        addPage("error/500", InternalServerErrorPage.class);
        
        // Common JavaScript merged together and mapped to scripts/all.js
        add(new MergedJavaScriptBuilder()
            .setPath("/scripts/all.js")
            .addJQueryUI()
            .addLibrary("cookies")
            .addLibrary("strftime")
            .addLibrary("55_utils")
            .addLibrary("jquery.55_utils")
            .addAssociatedScript(BasePage.class)
            .addWicketAjaxLibraries()
            .buildRequestMapper(app));
    }
    
    private void addPage(String path, Class<? extends Page> page)
    {
        add(new PatternMountedMapper(path, page));
    }
}
