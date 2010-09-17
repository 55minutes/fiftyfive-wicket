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

package fiftyfive.wicket.js;

import fiftyfive.util.Assert;
import fiftyfive.wicket.js.locator.DependencyCollection;
import fiftyfive.wicket.js.locator.JavaScriptDependencyLocator;
import fiftyfive.wicket.resource.MergedResourceBuilder;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.WicketAjaxReference;
import org.apache.wicket.behavior.AbstractHeaderContributor;
import org.apache.wicket.markup.html.WicketEventReference;
import org.apache.wicket.protocol.http.WebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example usage:
 * <pre>
 * new MergedJavaScriptBuilder()
 *     .setPath("/scripts/all.js")
 *     .addWicketAjaxLibraries()
 *     .addJQueryUI()
 *     .addLibrary("jquery.scrollTo")
 *     .addLibrary("jquery.55_utils")
 *     .addLibrary("55_utils")
 *     .addLibrary("strftime")
 *     .build(this);
 * </pre>
 */
public class MergedJavaScriptBuilder extends MergedResourceBuilder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        MergedJavaScriptBuilder.class
    );
    
    private DependencyCollection _deps;
    
    /**
     * Creates an empty builder object. See the
     * {@link MergedJavaScriptBuilder class documentation} for 
     * example usage.
     */
    public MergedJavaScriptBuilder()
    {
        super();
        _deps = new DependencyCollection();
    }
    
    public MergedJavaScriptBuilder setPath(String path)
    {
        return (MergedJavaScriptBuilder) super.setPath(path);
    }
    
    public MergedJavaScriptBuilder addScript(Class<?> scope, String path)
    {
        getDependencyLocator().findResourceScripts(scope, path, _deps);
        return this;
    }
    
    public MergedJavaScriptBuilder addScript(ResourceReference ref)
    {
        getDependencyLocator().findResourceScripts(
            ref.getScope(),
            ref.getName(),
            _deps
        );
        return this;
    }
    
    public MergedJavaScriptBuilder addJQueryUI()
    {
        return addLibrary("jquery-ui");
    }
    
    public MergedJavaScriptBuilder addWicketAjaxLibraries()
    {
        addScript(WicketEventReference.INSTANCE);
        addScript(WicketAjaxReference.INSTANCE);
        return this;
    }
    
    /**
     * Adds a JavaScript library to the list of merged resources. The
     * dependencies of the script, if declared using Sprockets syntax within
     * the JS file, will also be added automatically.
     *
     * @see JavaScriptDependencySettings
     */
    public MergedJavaScriptBuilder addLibrary(String libraryName)
    {
        Assert.notNull(libraryName);
        getDependencyLocator().findLibraryScripts(libraryName, _deps);
        return this;
    }
    
    @Override
    public AbstractHeaderContributor build(WebApplication app)
    {
        for(ResourceReference ref : _deps)
        {
            LOGGER.debug("Added script to merged builder: {}", ref);
            super.addScript(ref);
        }
        return super.build(app);
    }
    
    protected JavaScriptDependencyLocator getDependencyLocator()
    {
        return JavaScriptDependencySettings.get().getLocator();
    }
}
