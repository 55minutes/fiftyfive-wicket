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

package fiftyfive.wicket.js.locator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import fiftyfive.wicket.js.JavaScriptDependencySettings;
import org.apache.wicket.Application;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.util.lang.Packages;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.locator.IResourceStreamLocator;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJavaScriptDependencyLocator
    implements JavaScriptDependencyLocator
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        DefaultJavaScriptDependencyLocator.class
    );
    
    static final Pattern JQUERYUI_PATT = Pattern.compile("jquery(\\.|-|_)?ui");
    
    private Map<ResourceReference,CacheEntry> _cache;
    private SprocketDependencyCollector _collector;
    
    public DefaultJavaScriptDependencyLocator()
    {
        super();
        _cache = new ConcurrentHashMap<ResourceReference,CacheEntry>();
        _collector = new SprocketDependencyCollector(this);
    }
    
    public void findLibraryScripts(String libraryName,
                                   DependencyCollection scripts)
    {
        List<ResourceReference> refs = new ArrayList<ResourceReference>();
        if("jquery".equalsIgnoreCase(libraryName))
        {
            scripts.add(settings().getJQueryResource());
        }
        else if(JQUERYUI_PATT.matcher(libraryName.toLowerCase()).matches())
        {
            scripts.add(settings().getJQueryResource());
            scripts.add(settings().getJQueryUIResource());
            scripts.setCss(getJQueryUITheme());
        }
        else
        {
            collectResourceAndDependencies(
                searchForRequiredLibrary(libraryName),
                scripts
            );
        }
    }
    
    public void findResourceScripts(Class<?> cls, String fileName,
                                    DependencyCollection scripts)
    {
        collectResourceAndDependencies(
            new JavascriptResourceReference(cls, fileName), scripts
        );
    }
    
    public void findAssociatedScripts(Class<?> cls,
                                      DependencyCollection scripts)
    {
        collectResourceAndDependencies(
            new JavascriptResourceReference(cls, Classes.simpleName(cls)+".js"),
            scripts
        );
    }
    
    /**
     * Returns a reference to the CSS file that should be used to style
     * jQuery UI widgets. The default implementation simply delegates to
     * {@link JavaScriptDependencySettings#getJQueryUICSSResource JavaScriptDependencySettings.getJQueryUICSSResource()}.
     * This means that one style is used for the entire application.
     * If you want something more advanced, for example to choose a theme
     * based on user preferences or a session value, override this method for
     * your custom logic.
     */
    protected ResourceReference getJQueryUITheme()
    {
        return settings().getJQueryUICSSResource();
    }
    
    private void collectResourceAndDependencies(ResourceReference ref,
                                                DependencyCollection scripts)
    {
        if(scripts.isEmpty() && populateFromCache(ref, scripts)) return;
        if(!scripts.add(ref)) return;
        
        scripts.descend();
        _collector.collectDependencies(ref, load(ref), scripts);
        scripts.ascend();
        
        putIntoCache(ref, scripts);
    }

    private boolean populateFromCache(ResourceReference ref,
                                      DependencyCollection scripts)
    {
        if(null == ref) return false;
        
        CacheEntry ce = _cache.get(ref);
        if(ce != null && ce.isActive())
        {
            ce.populate(scripts);
            return true;
        }
        return false;
    }
    
    private void putIntoCache(ResourceReference ref,
                              DependencyCollection scripts)
    {
        if(null == ref) return;
        
        Duration duration = settings().getTraversalCacheDuration();
        if(duration.getMilliseconds() > 0)
        {
            _cache.put(ref, new CacheEntry(scripts, duration));
        }
    }
    
    private ResourceReference searchForRequiredLibrary(String name)
    {
        ResourceReference ref = null;
        
        for(SearchLocation loc : settings().getLibraryPaths())
        {
            String path = loc.getPath();
            String absolutePath = String.format(
                "%s%s.js",
                path.isEmpty() ? "" : path + "/", 
                name
            );
            ResourceReference testRef = new JavascriptResourceReference(
                loc.getScope(), absolutePath
            );
            if(load(testRef) != null)
            {
                ref = testRef;
                break;
            }
        }
        
        if(null == ref)
        {
            throw new WicketRuntimeException(
                "Could not find JavaScript library named: " + name
            );
        }
        
        return ref;
    }
    
    private IResourceStream load(ResourceReference ref)
    {
        IResourceStreamLocator locator =
            Application.get().getResourceSettings().getResourceStreamLocator();
        
        Class<?> scope = ref.getScope();
        String path = Packages.absolutePath(scope, ref.getName());
        
        return locator.locate(scope, path);
    }
    
    /**
     * Returns the JavaScriptDependencySettings associated with the current
     * Application.
     */
    private JavaScriptDependencySettings settings()
    {
        return JavaScriptDependencySettings.get();
    }
    
    private static class CacheEntry
    {
        private long _start;
        private long _timeToLive;
        private DependencyCollection _scripts;
        
        private CacheEntry(DependencyCollection scripts, Duration duration)
        {
            super();
            // Make a private copy so that the cached copy is never mutated
            _scripts = new DependencyCollection();
            scripts.copyTo(_scripts);
            _scripts.freeze();
            _start = System.currentTimeMillis();
            _timeToLive = duration.getMilliseconds();
        }
        
        private void populate(DependencyCollection other)
        {
            _scripts.copyTo(other);
        }
        
        private boolean isActive()
        {
            return System.currentTimeMillis() - _start < _timeToLive;
        }
    }
}
