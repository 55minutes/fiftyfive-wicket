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

package fiftyfive.wicket.util;

import java.io.Serializable;
import java.util.Collection;

import fiftyfive.util.ReflectUtils;
import fiftyfive.wicket.basic.LabelWithPlaceholder;
import org.apache.wicket.Component;
import org.apache.wicket.Response;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.PropertyResolver;
import org.apache.wicket.util.string.Strings;

/**
 * Helper methods for simplifying common Wicket tasks. Consider adding this
 * in the Java file of your wicket page or component:
 * <pre>
 * import static fiftyfive.wicket.util.Shortcuts.*;
 * </pre>
 *
 * @author Matt Brictson
 */
public class Shortcuts
{
    private static final IBehavior EMPTY_BEHAVIOR = new AbstractBehavior() {};
    
    /**
     * Shortcut for creating a PropertyModel. Equivalent to:
     * <pre>
     * new PropertyModel(bean, propertyExpr)
     * </pre>
     * @see PropertyModel
     */
    public static PropertyModel prop(Object bean, String propertyExpr)
    {
        return new PropertyModel(bean, propertyExpr);
    }
    
    /**
     * Creates a model that loads itself by exeucting the specified method
     * on a bean. The return value of that method will be cached as the value
     * of the model. When the model is detached, the cache will be discarded.
     * This is useful for connecting your Wicket page to your back-end:
     * <pre>
     * public PersonDetailPage(PageParameters params)
     * {
     *     super(params);
     *     setDefaultModel(loadedModel(this, "loadPerson"));
     * }
     * private Person loadPerson()
     * {
     *     return _personService.loadPerson(getPageParameters().get("id"));
     * }
     * </pre>
     * 
     * @throws IllegalArgumentException if the loadMethod does not exist, or
     *                                  takes more than zero arguments
     */
    public static LoadableDetachableModel loadedModel(final Object bean,
                                                      final String loadMethod)
    {
        // assert that method exists
        ReflectUtils.getZeroArgMethod(bean.getClass(), loadMethod);
        
        return new LoadableDetachableModel() {
            @Override protected Object load()
            {
                return ReflectUtils.invokeZeroArgMethod(bean, loadMethod);
            }
        };
    }
    
    /**
     * Shortcut for creating a LabelWithPlaceholder with an implied model.
     * Equivalent to:
     * <pre>
     * new LabelWithPlaceholder("name")
     * </pre>
     * @see LabelWithPlaceholder
     */
    public static LabelWithPlaceholder label(String id)
    {
        return new LabelWithPlaceholder(id);
    }

    /**
     * Shortcut for creating a LabelWithPlaceholder with a PropertyModel.
     * Equivalent to:
     * <pre>
     * new LabelWithPlaceholder("name", new PropertyModel(person, "fullName"))
     * </pre>
     * @see LabelWithPlaceholder
     */
    public static LabelWithPlaceholder label(String id,
                                             Object bean,
                                             String propertyExpr)
    {
        return label(id, prop(bean, propertyExpr));
    }

    /**
     * Shortcut for creating a LabelWithPlaceholder with a hardcoded value or a
     * custom model. Equivalent to:
     * <pre>
     * new LabelWithPlaceholder("name", "Hardcoded string value").
     * </pre>
     * @see LabelWithPlaceholder
     */
    public static LabelWithPlaceholder label(String id,
                                             Serializable valueOrIModel)
    {
        IModel model = valueOrIModel != null ? new Model(valueOrIModel) : null;
        if(valueOrIModel instanceof IModel)
        {
            model = (IModel) valueOrIModel;
        }
        return new LabelWithPlaceholder(id, model);
    }
    
    /**
     * Appends the specified text after the
     * closing tag of the component it decorates. The text will be escaped
     * to be safe HTML. This is very useful for doing comma separated lists.
     * For example:
     * <pre>
     * add(new ListView("list", myList) {
     *     &#064;Override protected void populateItem(ListItem item)
     *     {
     *         if(item.getIndex() &lt; getList().size() - 1)
     *         {
     *             item.add(afterTag(", "));
     *         }
     *     }
     * });
     * </pre>
     */
    public static IBehavior afterTag(final String textToAppend)
    {
        return new AbstractBehavior() {
            @Override
            public void onRendered(Component component)
            {
                Response response = component.getResponse();
                response.write(Strings.escapeMarkup(textToAppend));
            }
        };
    }
    
    /**
     * Adds a CSS class to the component it decorates.
     * Equivalent to:
     * <pre>
     * new AttributeAppender("class", true, new Model(cssClass), " ");
     * </pre>
     */
    public static IBehavior cssClass(String cssClass)
    {
        if(null == cssClass)
        {
            return EMPTY_BEHAVIOR;
        }
        return new AttributeAppender("class", true, new Model(cssClass), " ");
    }
        
    /**
     * Adds a CSS class to the component it decorates
     * when the specified property value is true or not empty.
     * <p>
     * For example, suppose we have the following Person class:
     * <pre>
     * public interface Person
     * {
     *     public boolean isLocked();
     *     public String getFullName();
     * }
     * </pre>
     * We want to conditionally apply a "locked" CSS class to the full name
     * label in the UI. Here's how to do it with shortcuts:
     * <pre>
     * // Create label for the fullName, with a "locked" CSS class if applicable
     * label("wicket-id", person, "fullName").add(cssClassIf("locked", person, "locked"));
     * </pre>
     * @see #empty
     */
    public static IBehavior cssClassIf(final String cssClass,
                                       final Object bean,
                                       final String boolPropertyExpr)
    {
        return new AttributeAppender("class", true, new Model(cssClass), " ") {
            @Override protected String newValue(String curr, String replace)
            {
                Object val = PropertyResolver.getValue(boolPropertyExpr, bean);
                return !empty(val) ? super.newValue(curr, replace) : null;
            }
        };
    }

    /**
     * Shortcut for creating a WebMarkupContainer. Equivalent to:
     * <pre>
     * new WebMarkupContainer("id")
     * </pre>
     * @see WebMarkupContainer
     */
    public static WebMarkupContainer container(String id)
    {
        return new WebMarkupContainer(id);
    }
    
    /**
     * Shortcut for creating a WebMarkupContainer. Equivalent to:
     * <pre>
     * new WebMarkupContainer("id", model)
     * </pre>
     * @see WebMarkupContainer
     */
    public static WebMarkupContainer container(String id, IModel model)
    {
        return new WebMarkupContainer(id, model);
    }
    
    /**
     * Shortcut for creating a WebMarkupContainer with a PropertyModel.
     * Equivalent to:
     * <pre>
     * new WebMarkupContainer("id", new PropertyModel(bean, prop))
     * </pre>
     * @see WebMarkupContainer
     */
    public static WebMarkupContainer container(String id,
                                               Object bean, String propertyExpr)
    {
        return container(id, prop(bean, propertyExpr));
    }
    
    /**
     * Return true if the object is null, zero-length (if array or collection)
     * false (if a boolean) or blank (if a string). Examples:
     * <table>
     *   <tr><th>Object</th><th>Empty?</th></tr>
     *   <tr><td><code>"foo"</code></td><td><code>false</code></td></tr>
     *   <tr><td><code>""</code></td><td><code>true</code></td></tr>
     *   <tr><td><code>"   "</code></td><td><code>true</code></td></tr>
     *   <tr><td><code>null</code></td><td><code>true</code></td></tr>
     *   <tr><td><code>Collections.EMPTY_LIST</code></td><td><code>true</code></td></tr>
     *   <tr><td><code>Collections.singletonList("foo")</code></td><td><code>false</code></td></tr>
     *   <tr><td><code>new String[0]</code></td><td><code>true</code></td></tr>
     *   <tr><td><code>new String[] { "foo" }</code></td><td><code>false</code></td></tr>
     *   <tr><td><code>true</code></td><td><code>false</code></td></tr>
     *   <tr><td><code>false</code></td><td><code>true</code></td></tr>
     * </table>
     */
    public static boolean empty(Object obj)
    {
        boolean empty = false;
        if(null == obj)
        {
            empty = true;
        }
        else if(obj instanceof Boolean)
        {
            empty = ! (Boolean) obj;
        }
        else if(obj instanceof Collection)
        {
            empty = ((Collection) obj).size() == 0;
        }
        else if(obj instanceof String)
        {
            empty = Strings.isEmpty((String) obj);
        }
        else if(obj.getClass().isArray())
        {
            empty = ((Object[])obj).length == 0;
        }
        return empty;
    }
    
    /**
     * This class cannot be instantiated or subclassed.
     */
     private Shortcuts()
     {
     }
}
