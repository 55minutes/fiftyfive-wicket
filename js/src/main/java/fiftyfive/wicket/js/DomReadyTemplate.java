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

import java.util.HashMap;
import java.util.Map;

import fiftyfive.wicket.js.locator.DependencyCollection;
import fiftyfive.wicket.js.locator.JavaScriptDependencyLocator;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.javascript.DefaultJavascriptCompressor;
import org.apache.wicket.javascript.IJavascriptCompressor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.string.interpolator.PropertyVariableInterpolator;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;


/**
 * Injects DOM-ready Javascript into the {@code <head>} using the
 * interpolated contents of a separate JavaScript template.
 * The code in the associated JavaScript file will run when the page loads,
 * and also every time the component to which it is attached is repainted via
 * Wicket ajax.
 * <p>
 * <b>This behavior will cause jQuery to be added to the &lt;head&gt; if it
 * is not there already.</b> Your script can rely on the {@code jQuery}
 * object being available.
 * <p>
 * Internally Wicket's {@link PropertyVariableInterpolator} is used to
 * perform substitutions in the template using the
 * <code>${propertyExpression}</code> syntax. Two keys are available for you
 * to use in your templates:
 * <ul>
 * <li><b>{@code component}</b> is the component to which the
 *     {@code DomReadyTemplate} is bound. The most common use is to obtain
 *     the markup ID of the component like this:
 *     <code>${component.markupId}</code>.</li>
 * <li><b>{@code behavior}</b> is the {@code DomReadyTemplate} object itself.
 *     If you extend {@code DomReadyTemplate} to create a custom JavaScript
 *     behavior, you can declare properties on your subclass that can then be
 *     used in the JavaScript template like this:
 *     <code>${behavior.propertyName}</code>.</li>
 * </ul>
 * <p>
 * {@code DomReadyTemplate} is ideal for rich Wicket components that are
 * tightly integrated with associated JavaScript or have complex JavaScript
 * initialization code.
 * <p>
 * Other benefits:
 * <ul>
 * <li><a href="http://getsprockets.org/">Sprocket</a> syntax is supported
 *     for dependencies. Your associated JavaScript file can declare the
 *     libraries and files it needs to function, and these dependencies will be
 *     included in the {@code <head>} automatically.</li>
 * <li>Just like a subclass of a panel can override the HTML of its parent,
 *     a subclass of a panel using {@code DomReadyTemplate} can override the
 *     JavaScript. For example, if {@code MyPanel} is subclassed as
 *     {@code MyExtendedPanel}, the associated JavaScript will be loaded
 *     from {@code MyExtendedPanel.js}, and only if that doesn't exist will
 *     the original {@code MyPanel.js} be used. This allows you to subclass
 *     an existing component, completely customize the JavaScript of that
 *     component, but leave the Java code (and HTML) untouched.</li>
 * </li>
 * <p>
 * Here is a trival example:
 * <pre class="example">
 * <b>// HelloPanel.java</b>
 * // Panel that replaces its contents with "Hello" on DOM-ready
 * public class HelloPanel extends DomReadyTemplatePanel
 * {
 *     public HelloPanel(String id)
 *     {
 *         super(id);
 *         add(new DomReadyTemplate(getClass()));
 *     }
 * }</pre>
 * <pre class="example">
 * <b>// HelloPanel.js</b>
 * // Depends on jQuery
 * //= require &lt;jquery&gt;
 * jQuery("#${component.markupId}").text("Hello");</pre>
 * <p>
 * When this panel is used on a page, the {@code <head>} will automatically
 * gain code like this:
 * <pre class="example">
 * &lt;script type="text/javascript"&gt;
 * jQuery(function(){
 *     jQuery("#panel1").text("Hello");
 * });
 * &lt;/script&gt;</pre>
 * 
 * @since 2.0
 */
public class DomReadyTemplate extends AbstractJavaScriptContribution
{
    private static final DefaultJavascriptCompressor DEFAULT_COMPRESSOR = 
        new DefaultJavascriptCompressor();
        
    private Class<?> _templateLocation;
    private Component _component;
    private transient DependencyCollection _dependencies;
    private transient ResourceReference _template;
    private transient String _readyScript;
    
    /**
     * Constructs a {@code DomReadyTemplate} to be used with a panel.
     * Like this:
     * <pre class="example">
     * public class MyPanel extends Panel
     * {
     *     public MyPanel(String id)
     *     {
     *         super(id);
     *         add(new DomReadyTemplate(getClass()));
     *     }
     * }</pre>
     * 
     * @param templateLocation The class that will be used to resolve the
     *                         location of the JavaScript template. For
     *                         example, if the class is {@code MyClass.class},
     *                         the template will be loaded from
     *                         {@code MyClass.js} in the same classpath
     *                         location. In most cases you will pass the result
     *                         of {@code getClass()} so it is possible for
     *                         subclasses to contribute their own template.
     */
    public DomReadyTemplate(Class<?> templateLocation)
    {
        internalInit(templateLocation);
    }
    
    /**
     * Constructor for subclasses that wish to define a custom template-based
     * behavior. In this case the template will be loaded from a JavaScript
     * file that is the same name as the {@code DomReadyTemplate} subclass.
     * <p>
     * In other words:
     * <pre class="example">
     * // JavaScript will be loaded from MyCustomBehavior.js
     * public class MyCustomBehavior extends DomReadyTemplate
     * {
     *     public MyCustomBehavior()
     *     {
     *         super();
     *     }
     * }</pre>
     */
    protected DomReadyTemplate()
    {
        internalInit(getClass());
    }
    
    private void internalInit(Class<?> templateLocation)
    {
        _templateLocation = templateLocation;
    }
    
    /**
     * Returns the {@link IJavascriptCompressor} that will be used to
     * compress the JavaScript before it is added to the {@code <head>}.
     * By default this returns an instance of
     * {@code DefaultJavascriptCompressor}.
     */
    protected IJavascriptCompressor getCompressor()
    {
        return DEFAULT_COMPRESSOR;
    }
    
    /**
     * Stores a reference to the component so that it can be used to
     * substitute <code>${component}</code> expressions in the JavaScript
     * template.
     * <p>
     * Also calls {@code setOutputMarkupId(true)} on the component. We do
     * this because the most common technique for integrating JavaScript
     * handlers with a component is by using its markup ID.
     */
    @Override
    public void bind(Component component)
    {
        _component = component;
        _component.setOutputMarkupId(true);
    }
    
    /**
     * Releases internal caches.
     */
    @Override
    public void detach(Component component)
    {
        super.detach(component);
        _dependencies = null;
        _template = null;
        _readyScript = null;
    }
    
    /**
     * Renders script src tags for all the dependencies that were discovered
     * via <a href="http://getsprockets.org">Sprockets</a> syntax in the
     * JavaScript template, plus a DOM-ready section containing the contents
     * of the template after variable substitutions have been performed.
     */
    @Override
    public void renderHead(IHeaderResponse response)
    {
        if(null == _dependencies) load();
        
        renderDependencies(response, _dependencies, _template);
        renderDomReady(response, _readyScript);
    }
    
    /**
     * Loads the JavaScript template, determines its dependencies, and then
     * uses PropertyVariableInterpolator to perform variable substitutions.
     * The results are cached in member variables that will be cleared when
     * detach() is called.
     */
    private void load()
    {
        JavaScriptDependencyLocator locator = settings().getLocator();
        _dependencies = new DependencyCollection();
        locator.findAssociatedScripts(_templateLocation, _dependencies);
        
        _template = _dependencies.getRootReference();
        if(null == _template)
        {
            throw new WicketRuntimeException(String.format(
                "Failed to locate JavaScript template for %s. The JavaScript file must be " +
                "the same name as the class but with a '.js' extension and be present in " +
                "the same classpath location.",
                _templateLocation));
        }
        
        TextTemplate tt = new PackagedTextTemplate(
            _template.getScope(),
            _template.getName(),
            "application/javascript",
            settings().getEncoding()
        );
        
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("component", _component);
        map.put("behavior", this);
        
        _readyScript = getCompressor().compress(
            PropertyVariableInterpolator.interpolate(tt.getString(), map)
        );
    }
}
