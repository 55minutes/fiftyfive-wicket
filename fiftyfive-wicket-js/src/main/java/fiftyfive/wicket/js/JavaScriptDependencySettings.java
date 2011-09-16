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
package fiftyfive.wicket.js;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fiftyfive.util.Assert;
import fiftyfive.wicket.js.locator.DefaultJavaScriptDependencyLocator;
import fiftyfive.wicket.js.locator.JavaScriptDependencyLocator;
import fiftyfive.wicket.js.locator.SearchLocation;
import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.time.Duration;

/**
 * Settings that affect how JavaScript dependencies are located. To obtain an 
 * instance, use the static {@link #get get()} method.
 * <p>
 * Most applications using out-of-the-box JavaScript widgets will be served
 * well by the default settings. However there are three important scenarios
 * to consider:
 * <ol>
 * <li><b>If your application already has a mechanism for including jQuery
 *     and/or jQuery UI in the &lt;head&gt;</b> (e.g. via your base page),
 *     then you should take care that fiftyfive-wicket-js is not including its
 *     own copies of these libraries. In this scenario, call
 *     {@link #setJQueryResource setJQueryResource()} and
 *     {@link #setJQueryUIResource setJQueryUIResource()} with references to
 *     the files that your application is already using (or {@code null},
 *     if you don't want fiftyfive-wicket-js to manage these resources at all).
 *     </li>
 * <li><b>If you have your own library of custom JavaScript files</b>,
 *     consider placing those files inside your classpath, and then use
 *     {@link #addLibraryPath addLibraryPath()} to make fiftyfive-wicket-js
 *     aware of them. Then you can use the angle-bracket dependency syntax
 *     or the single argument constructor for
 *     {@link JavaScriptDependency JavaScriptDependency}, and those files will
 *     be found automatically.</li>
 * <li><b>If you'd like to change the jQuery UI CSS theme</b>, call
 *     {@link #setJQueryUICSSResource setJQueryUICSSResource()} with a
 *     reference to the desired CSS file, or {@code null} if you don't want
 *     fiftyfive-wicket-js to manage this for you.</li>
 * </ol>
 * 
 * @since 2.0
 */
public class JavaScriptDependencySettings
{
    private static final MetaDataKey<JavaScriptDependencySettings> SETTINGS_KEY
        = new MetaDataKey<JavaScriptDependencySettings>() {};
    
    private Application app;
    private List<SearchLocation> locations;
    private ResourceReference jQueryResource;
    private ResourceReference jQueryUIResource;
    private ResourceReference jQueryUICSSResource;
    private Duration traversalCacheDuration;
    private String encoding;
    private JavaScriptDependencyLocator locator;
    
    /**
     * Returns the JavaScriptDependencySettings associated with the current
     * Wicket Application, creating a new default settings object if one does
     * not yet exist. This method can only be called within a Wicket thread.
     */
    public static JavaScriptDependencySettings get()
    {
        Application app = Application.get();
        if(null == app)
        {
            throw new IllegalStateException(
                "No thread-local Wicket Application object was found. " +
                "JavaScriptDependencySettings.get() can only be called " +
                "within a Wicket request."
            );
        }
        JavaScriptDependencySettings settings = app.getMetaData(SETTINGS_KEY);
        if(null == settings)
        {
            settings = new JavaScriptDependencySettings(app);
            app.setMetaData(SETTINGS_KEY, settings);
        }
        return settings;
    }
    
    /**
     * Constructs a settings object for the given application with a reasonable
     * set of defaults.
     */
    protected JavaScriptDependencySettings(Application app)
    {
        super();
        this.app = app;
        this.locator = new DefaultJavaScriptDependencyLocator();

        this.locations = new ArrayList<SearchLocation>();

        Class<?> c = JavaScriptDependencySettings.class;
        addLibraryPath(c, "");
        addLibraryPath(c, "lib/cookies");
        addLibraryPath(c, "lib/dump");
        addLibraryPath(c, "lib/fiftyfive-utils");
        addLibraryPath(c, "lib/modernizr/2.0.6");
        addLibraryPath(c, "lib/strftime");
        
        this.jQueryResource = new PackageResourceReference(
            JavaScriptDependencySettings.class,
            "lib/jquery/1.6.4/jquery.min.js"
        );
        this.jQueryUIResource = new PackageResourceReference(
            JavaScriptDependencySettings.class,
            "lib/jquery-ui/1.8.16/jquery-ui.min.js"
        );
        this.jQueryUICSSResource = new PackageResourceReference(
            JavaScriptDependencySettings.class,
            "lib/jquery-ui/1.8.16/themes/redmond/jquery-ui.redmond.css"
        );
    }
    
    /**
     * Returns the JavaScriptDependencyLocator for this application.
     */
    public JavaScriptDependencyLocator getLocator()
    {
        return this.locator;
    }
    
    /**
     * Sets the JavaScriptDependencyLocator that should be used for this
     * application. By default this is
     * {@link DefaultJavaScriptDependencyLocator}.
     * 
     * @return {@code this} to allow chaining
     */
    public JavaScriptDependencySettings setLocator(JavaScriptDependencyLocator loc)
    {
        Assert.notNull(loc);
        this.locator = loc;
        return this;
    }
    
    /**
     * Returns the list of classpath locations that will be used to find
     * JavaScript libraries.
     */
    public List<SearchLocation> getLibraryPaths()
    {
        return Collections.unmodifiableList(this.locations);
    }

    /**
     * Adds a classpath location to be used for locating JavaScript libraries.
     * A default list of locations from the fiftyfive-wicket-js JAR contains
     * jQuery, jQuery UI, cookies, fiftyfive-utils, jquery-scrollto and
     * strftime.
     * Call this method if you wish to add more libraries to this list, or if
     * you want your versions of the previously mentioned libraries to take
     * precedence. The paths that you add will be consulted before the
     * defaults.
     * <p>
     * Library paths are searched whenever you use the angle-bracket syntax
     * for JavaScript dependencies, like this:
     * <pre class="example">
     * //= require &lt;libraryname&gt;</pre>
     * <p>
     * Or when you use
     * {@link JavaScriptDependency#JavaScriptDependency(String)} or
     * {@link MergedJavaScriptBuilder#addLibrary(String)}.
     * 
     * @return {@code this} to allow chaining
     */
    public JavaScriptDependencySettings addLibraryPath(Class<?> cls, String path)
    {
        this.locations.add(0, new SearchLocation(cls, path));
        return this;
    }
    
    /**
     * Returns the ResourceReference for the jQuery JavaScript file.
     */
    public ResourceReference getJQueryResource()
    {
        return this.jQueryResource;
    }
    
    /**
     * Sets the ResourceReference of the jQuery JavaScript file that should
     * be used whenever the dependency locator determines that jQuery is
     * needed. Pass {@code null} if you do not want the framework to include
     * jQuery automatically.
     * By default this refers to a copy of jQuery bundled in the
     * fiftyfive-wicket-js JAR.
     * 
     * @return {@code this} to allow chaining
     */
    public JavaScriptDependencySettings setJQueryResource(ResourceReference r)
    {
        this.jQueryResource = r;
        return this;
    }
    
    /**
     * Returns the ResourceReference for the jQuery UI JavaScript file.
     */
    public ResourceReference getJQueryUIResource()
    {
        return this.jQueryUIResource;
    }

    /**
     * Sets the ResourceReference of the jQuery UI JavaScript file that should
     * be used whenever the dependency locator determines that jQuery UI is
     * needed. Pass {@code null} if you do not want the framework to include
     * jQuery UI automatically.
     * By default this refers to a copy of jQuery UI bundled in the
     * fiftyfive-wicket-js JAR.
     * 
     * @return {@code this} to allow chaining
     */
    public JavaScriptDependencySettings setJQueryUIResource(ResourceReference r)
    {
        this.jQueryUIResource = r;
        return this;
    }
    
    /**
     * Returns the ResourceReference for the jQuery UI CSS theme file.
     */
    public ResourceReference getJQueryUICSSResource()
    {
        return this.jQueryUICSSResource;
    }

    /**
     * Sets the ResourceReference of the jQuery UI CSS theme file that should
     * be used whenever the dependency locator determines that jQuery UI is
     * needed. Pass {@code null} if you do not want the framework to include
     * jQuery UI CSS automatically.
     * By default this refers to a copy of jQuery UI "redmond" theme bundled
     * in the fiftyfive-wicket-js JAR.
     * 
     * @return {@code this} to allow chaining
     */
    public JavaScriptDependencySettings setJQueryUICSSResource(ResourceReference r)
    {
        this.jQueryUICSSResource = r;
        return this;
    }
    
    /**
     * Returns the duration that JavaScript dependency traversal results
     * (i.e. the tree of dependencies as determined by parsing sprockets
     * directives) are allowed to be stored in cache.
     */
    public Duration getTraversalCacheDuration()
    {
        // Interpret null based on application mode
        if(null == this.traversalCacheDuration)
        {
            if(this.app.usesDeploymentConfig())
            {
                // Cache indefinitely
                return Duration.MAXIMUM;
            }
            // Disable cache
            return Duration.NONE;
        }
        return this.traversalCacheDuration;
    }

    /**
     * Sets the duration that JavaScript dependency traversal results
     * (i.e. the tree of dependencies as determined by parsing sprockets
     * directives) are allowed to be stored in cache.
     * <p>
     * If this is set to {@code null} (the default), at runtime this will be
     * interpreted as a value of zero (cache disabled) if the application is
     * in development mode, and will be interpreted as the maximum duration
     * (effectively indefinite) if the application is in deployment mode.
     * 
     * @return {@code this} to allow chaining
     */
    public JavaScriptDependencySettings setTraversalCacheDuration(Duration d)
    {
        this.traversalCacheDuration = d;
        return this;
    }
    
    /**
     * Returns the character encoding that will be used when parsing
     * JavaScript files.
     */
    public String getEncoding()
    {
        if(null == this.encoding)
        {
            String enc = this.app.getMarkupSettings().getDefaultMarkupEncoding();
            if(null == enc)
            {
                enc = Charset.defaultCharset().name();
            }
            return enc;
        }
        return this.encoding;
    }

    /**
     * Sets the character encoding that will be used when parsing
     * JavaScript files. If this is set to {@code null} (the default), then
     * Wicket's markup encoding setting will be used.
     * 
     * @return {@code this} to allow chaining
     * @see org.apache.wicket.settings.IMarkupSettings#getDefaultMarkupEncoding()
     */
    public JavaScriptDependencySettings setEncoding(String encoding)
    {
        this.encoding = encoding;
        return this;
    }
}
