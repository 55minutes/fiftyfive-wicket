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

/**
 * Locates JavaScript files and their dependencies. 
 * This is what is used internally by
 * {@link fiftyfive.wicket.js.JavaScriptDependency JavaScriptDependency} and
 * {@link fiftyfive.wicket.js.MergedJavaScriptBuilder MergedJavaScriptBuilder};
 * you should never need to use this API directly.
 * To provide a custom implementation, call
 * {@link fiftyfive.wicket.js.JavaScriptDependencySettings#setLocator JavaScriptDependencySettings.setLocator()}.
 * 
 * @since 2.0
 */
public interface JavaScriptDependencyLocator
{
    /**
     * Locates the JavaScript library with the given name and adds it, along
     * with all of its dependencies, to the specified DependencyCollection.
     * This will search the
     * {@link fiftyfive.wicket.js.JavaScriptDependencySettings#addLibraryPath library paths}
     * that have been configured for the application.
     * 
     * @param libraryName Name of the JavaScript library, like "jquery-ui".
     *                    The name should not include the ".js" extension.
     * @param scripts The library and all of its dependencies will be added
     *                to this collection in the order that they should appear
     *                in the HTML &lt;head&gt;.
     */
    void findLibraryScripts(String libraryName, DependencyCollection scripts);

    /**
     * Locates a JavaScript file in the classpath relative to a class, and
     * adds it, along with of its dependencies, to the specified
     * DependencyCollection.
     * 
     * @param cls The JavaScript file will be located relative to this class.
     * @param fileName Exact name of the JavaScript file as it appears in
     *                 the classpath relative to the specified class, like
     *                 "myfile.js".
     * @param scripts The script and all of its dependencies will be added
     *                to this collection in the order that they should appear
     *                in the HTML &lt;head&gt;.
     */
    void findResourceScripts(Class<?> cls, String fileName, DependencyCollection scripts);
    
    /**
     * Locates a JavaScript file in the classpath with the same name and
     * location as a given class, and adds it, along with of its dependencies,
     * to the specified DependencyCollection.
     * 
     * @param cls Specifies the name and location of the JavaScript file.
     *            For example, if the class is {@code MyPanel.class}, the
     *            JavaScript file that will be located will be
     *            {@code MyPanel.js} in the same classpath location.
     * @param scripts The script and all of its dependencies will be added
     *                to this collection in the order that they should appear
     *                in the HTML &lt;head&gt;.
     */
    void findAssociatedScripts(Class<?> cls, DependencyCollection scripts);
}
