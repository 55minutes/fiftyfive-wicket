/**
 * Copyright 2013 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.css;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.parser.XmlTag.TagType;
import org.apache.wicket.util.string.Strings;


/**
 * A behavior simliar to {@link org.apache.wicket.AttributeModifier AttributeModifier} but
 * specially tuned for modifying the HTML {@code class} attribute. When this behavior is bound
 * to a component it does the following:
 * <ul>
 * <li>Always adds the {@code class} attribute, even if it did not already exist.</li>
 * <li>Ensures that duplicate CSS classes will not be emitted in the attribute.</li>
 * <li>Each class will be separated by a space character.</li>
 * </ul>
 * Note that this class is abstract. Subclasses must implement the
 * {@link #modifyClasses modifyClasses()} to specify what classes are added (or subtracted!)
 * from the {@code class} attribute.
 * 
 * @since 2.0.4
 * @see fiftyfive.wicket.util.Shortcuts#toggledCssClass(String,org.apache.wicket.model.IModel)
 */
public abstract class CssClassModifier extends Behavior
{
    /**
     * Implemented by subclasses to specify what CSS classes should be added or subtracted
     * to the HTML for this component.
     * 
     * @param component The component whose HTML element is being rendered.
     * 
     * @param cssClasses A set containing all the CSS classes that were declared in the markup
     *                   for this component, if any. If values are added to this set, they will
     *                   be emitted in the {@code class} attribute of the element when the
     *                   component renders, with a space separating each value. Values may also
     *                   be removed in order to prevent them from being emitted.
     */
    protected abstract void modifyClasses(Component component, Set<String> cssClasses);

    /**
     * Parse any existing classes declared in the markup for this component and then delegate
     * to {@link #modifyClasses modifyClasses()}. Set resulting set of classes on the component
     * tag to render its {@code class} attribute with the desired values.
     */
    @Override
    public void onComponentTag(Component component, ComponentTag tag)
    {
        if(tag.getType() != TagType.CLOSE)
        {
            Set<String> values = new LinkedHashSet<String>();
            String existing = tag.getAttribute("class");
            if(existing != null)
            {
                values.addAll(Arrays.asList(existing.split("\\s+")));
            }
            modifyClasses(component, values);
            tag.put("class", Strings.join(" ", values.toArray(new String[0])));
        }
    }
}
