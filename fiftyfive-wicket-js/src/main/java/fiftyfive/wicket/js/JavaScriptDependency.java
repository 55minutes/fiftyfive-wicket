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
package fiftyfive.wicket.js;

import fiftyfive.wicket.js.locator.DependencyCollection;
import fiftyfive.wicket.js.locator.JavaScriptDependencyLocator;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.util.lang.Args;

/**
 * Represents a JavaScript file, or group of files, that will be injected into
 * the &lt;head&gt;. This is the mechanism for programmatically declaring
 * JavaScript dependencies within Java code.
 * <p>
 * There are three ways to use this class:
 * <ul>
 * <li>Inject a JS file of the same name as your component class, using
 *     {@link #JavaScriptDependency(Class)}.</li>
 * <li>Inject an arbitrary JS classpath resource, using
 *     {@link #JavaScriptDependency(Class,String)}.</li>
 * <li>Inject one of several commonly-used JavaScript libraries, like
 *     jQuery, using {@link #JavaScriptDependency(String)}.</li>
 * </ul>
 * <p>
 * In all cases, the JavaScript files that you specify will be scanned for
 * dependencies. Refer to the <a href="package-summary.html#dependency-resolution">dependency
 * resolution guide</a> for more information.
 * <p>
 * For example, let's say you have {@code MyPanel} which contains custom
 * JavaScript in an accompanying {@code MyPanel.js} file. Your JavaScript
 * in turn depends on jQuery UI. In {@code MyPanel.js} you would add this
 * line:
 * <pre class="example">
 * //= require jquery-ui</pre>
 * <p>
 * This informs fiftyfive-wicket-js of the dependency. Then in your Java code,
 * you just need to add {@code MyPanel.js} like this:
 * <pre class="example">
 * public MyPanel(String id)
 * {
 *     super(id);
 *     add(new JavaScriptDependency(MyPanel.class));
 * }</pre>
 * <p>
 * At runtime, fiftyfive-wicket-js will find {@code MyPanel.js}, scan it
 * for dependencies, and notice that jQuery UI is needed. It is smart enough
 * to realize jQuery is needed as well. When the page renders, jQuery,
 * jQuery UI, and {@code MyPanel.js} will all automatically be included in
 * the &lt;head&gt;, and in the correct order. Futhermore, the necessary
 * jQuery UI CSS will be included as well (the "redmond" theme, by default).
 * <p>
 * Internally this class delegates to {@link JavaScriptDependencyLocator}
 * to find the actual files and their dependencies. The behavior of this
 * dependency discovery can be controlled using the
 * {@link JavaScriptDependencySettings}.
 * 
 * @since 2.0
 */
public class JavaScriptDependency extends AbstractJavaScriptContribution
{
    /**
     * Dependency representing jQuery UI. Automatically injects both jQuery and
     * jQuery UI libraries.
     */
    public static final JavaScriptDependency JQUERY_UI =
        new JavaScriptDependency("jquery-ui");
    
    /**
     * Dependency representing jQuery.
     */
    public static final JavaScriptDependency JQUERY =
        new JavaScriptDependency("jquery");
    
    private Class<?> clazz;
    private String fileName;
    private String libraryName;
    
    /**
     * Creates a JavaScriptDependency for a JavaScript file that accompanies
     * a class of the same name. For example, a dependency created for
     * {@code MyPanel.class} will look for a corresponding file named
     * {@code MyPanel.js} in the same classpath location. Simliar to how Wicket
     * locates HTML files, if {@code MyPanel.js} cannot be found, the
     * superclass of that panel is searched, and so on.
     */
    public JavaScriptDependency(Class<?> cls)
    {
        super();
        Args.notNull(cls, "cls");
        this.clazz = cls;
    }
    
    /**
     * Creates a JavaScriptDependency for a JavaScript library that resides
     * in the library search path. The name should not include the .js
     * extension. For example:
     * <pre class="example">
     * add(new JavaScriptDependency("jquery-ui"));</pre>
     * 
     * @see JavaScriptDependencySettings#addLibraryPath
     */
    public JavaScriptDependency(String libraryName)
    {
        super();
        Args.notNull(libraryName, "libraryName");
        this.libraryName = libraryName;
    }
    
    /**
     * Creates a JavaScriptDependency for a JavaScript file in the classpath,
     * The file name should not include the .js extension.
     * For example:
     * <pre class="example">
     * add(new JavaScriptDependency(MyPanel.class, "jquery.ui.myplugin"));</pre>
     */
    public JavaScriptDependency(Class<?> cls, String fileName)
    {
        super();
        Args.notNull(cls, "cls");
        Args.notNull(fileName, "fileName");
        this.clazz = cls;
        this.fileName = fileName;
    }
    
    /**
     * Injects the JavaScript files into the &lt;head&gt;, using
     * {@link JavaScriptDependencyLocator} to first find the files.
     */
    @Override
    public void renderHead(Component comp, IHeaderResponse response)
    {
        JavaScriptDependencyLocator locator = settings().getLocator();
        DependencyCollection scripts = new DependencyCollection();
        
        if(this.libraryName != null)
        {
            locator.findLibraryScripts(this.libraryName, scripts);
        }
        else if(this.fileName != null)
        {
            locator.findResourceScripts(this.clazz, this.fileName, scripts);
        }
        else
        {
            locator.findAssociatedScripts(this.clazz, scripts);
        }
        
        renderDependencies(response, scripts, null);
    }

    /**
     * Returns the settings to use. This method exists only for overriding
     * during unit tests.
     */
    JavaScriptDependencySettings settings()
    {
        return JavaScriptDependencySettings.get();
    }
}
