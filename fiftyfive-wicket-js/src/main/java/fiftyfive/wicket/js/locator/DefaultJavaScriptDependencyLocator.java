/**
 * Copyright 2014 55 Minutes (http://www.55minutes.com)
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
import org.apache.wicket.WicketRuntimeException;

import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.util.lang.Packages;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.core.util.resource.locator.IResourceStreamLocator;
import org.apache.wicket.util.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of JavaScriptDependencyLocator. Uses the Wicket
 * application's {@link IResourceStreamLocator} to load JavaScript files,
 * and our {@link SprocketsDependencyCollector} to parse them for dependencies.
 * A {@link ConcurrentHashMap} is used as a simple in-memory cache for the
 * dependency trees that are discovered.
 * 
 * @since 2.0
 */
public class DefaultJavaScriptDependencyLocator
    implements JavaScriptDependencyLocator
{
    static final Pattern JQUERYUI_PATT = Pattern.compile("jquery(\\.|-|_)?ui");
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
        DefaultJavaScriptDependencyLocator.class
    );
    
    
    private Map<ResourceReference,CacheEntry> cache;
    
    public DefaultJavaScriptDependencyLocator()
    {
        super();
        this.cache = new ConcurrentHashMap<ResourceReference,CacheEntry>();
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
            newResourceReference(cls, fileName), scripts
        );
    }
    
    public void findAssociatedScripts(final Class<?> cls,
                                      final DependencyCollection scripts)
    {
        Args.notNull(cls, "cls");
        Args.notNull(scripts, "scripts");
        
        // Traverse up the class hierarchy until we find
        // a valid JavaScript resource or we run out of super classes.
        Class<?> scope = cls;
        while(scope != null)
        {
            ResourceReference reference = newResourceReference(
                scope,
                Classes.simpleName(scope)
            );
            LOGGER.debug("Searching for: {}", reference);
            if(load(reference) != null)
            {
                LOGGER.debug("Found: {}", reference);
                collectResourceAndDependencies(reference, scripts);
                break;
            }
            scope = scope.getSuperclass();
        }
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
    
    /**
     * Adds the resource to the DependencyCollection and recursively traverses
     * all of the sprocket dependencies of that resource (and its dependencies
     * and so forth), until the entire dependency tree has been added to
     * the collection. The cache will first be consulted to avoid the
     * recursion, if possible. Otherwise the result of the recursion is cached
     * for future use.
     */
    private void collectResourceAndDependencies(ResourceReference ref,
                                                DependencyCollection scripts)
    {
        if(scripts.isEmpty() && populateFromCache(ref, scripts)) return;
        if(!scripts.add(ref)) return;
        
        SprocketsParser parser = settings().getSprocketsParser();
        if(parser != null)
        {
            SprocketsDependencyCollector coll = new SprocketsDependencyCollector(this, parser);
            scripts.descend();
            IResourceStream stream = load(ref);
            if(null == stream)
            {
                throw new WicketRuntimeException(
                    "JavaScript file does not exist: " + ref
                );
            }
            coll.collectDependencies(ref, stream, scripts);
            scripts.ascend();
        }
        
        putIntoCache(ref, scripts);
    }

    /**
     * Modifies the given DependencyCollection so that it is identical to the
     * cached copy based on a previous call to putIntoCache() and returns
     * {@code true}.
     * If the cache is disabled or there is no existing cache for the given
     * resource, returns {@code false}.
     */
    private boolean populateFromCache(ResourceReference ref,
                                      DependencyCollection scripts)
    {
        if(null == ref) return false;
        
        CacheEntry ce = this.cache.get(ref);
        if(ce != null && ce.isActive())
        {
            ce.populate(scripts);
            return true;
        }
        return false;
    }
    
    /**
     * Stores the dependencies of the given resource in the cache. If the
     * cache is disabled (i.e. duration of zero), this has no effect.
     */
    private void putIntoCache(ResourceReference ref,
                              DependencyCollection scripts)
    {
        if(null == ref) return;
        
        Duration duration = settings().getTraversalCacheDuration();
        if(duration.getMilliseconds() > 0)
        {
            this.cache.put(ref, new CacheEntry(scripts, duration));
        }
    }
    
    /**
     * Loops through all the library search paths as configured in
     * JavaScriptDependencySettings and looks for the JavaScript library
     * with the specified name, returning a ResourceReference for the first
     * match. If none could be found, throws a WicketRuntimeException.
     */
    private ResourceReference searchForRequiredLibrary(final String name)
    {
        ResourceReference ref = null;
        
        for(SearchLocation loc : settings().getLibraryPaths())
        {
            String path = loc.getPath();
            String absolutePath = String.format(
                "%s%s", path.isEmpty() ? "" : path + "/", name
            );
            ResourceReference testRef = newResourceReference(
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
    
    /**
     * Loads the given ResourceReference as an IResourceStream or returns
     * {@code null} if the resource could not be found.
     */
    private IResourceStream load(ResourceReference ref)
    {
        IResourceStreamLocator locator =
            Application.get().getResourceSettings().getResourceStreamLocator();
        
        Class<?> scope = ref.getScope();
        String path = Packages.absolutePath(scope, ref.getName());
        
        return locator.locate(scope, path);
    }
    
    /**
     * Constructs a new ResourceReference, automatically adding the ".js"
     * extension if needed.
     */
    private ResourceReference newResourceReference(Class<?> scope, String name)
    {
        // TODO: test whether file exists first, just in case it has no
        // extension or a non-js extension.
        
        if(!name.toLowerCase().endsWith(".js"))
        {
            name = name + ".js";
        }
        return new PackageResourceReference(scope, name);
    }
    
    /**
     * Returns the JavaScriptDependencySettings associated with the current
     * Application.
     */
    private JavaScriptDependencySettings settings()
    {
        return JavaScriptDependencySettings.get();
    }
    
    /**
     * A cache entry holds an immutable DependencyCollection and expires
     * after a certain duration.
     */
    private static class CacheEntry
    {
        private long start;
        private long timeToLive;
        private DependencyCollection scripts;
        
        private CacheEntry(DependencyCollection orig, Duration duration)
        {
            super();
            // Make a private copy so that the cached copy is never mutated
            this.scripts = new DependencyCollection();
            orig.copyTo(this.scripts);
            this.scripts.freeze();
            this.start = System.currentTimeMillis();
            this.timeToLive = duration.getMilliseconds();
        }
        
        private void populate(DependencyCollection other)
        {
            this.scripts.copyTo(other);
        }
        
        private boolean isActive()
        {
            return System.currentTimeMillis() - this.start < this.timeToLive;
        }
    }
}
