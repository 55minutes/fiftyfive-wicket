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

package fiftyfive.wicket.form;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

/**
 * A base ListView that provides conveniences for rendering its items using
 * an IChoiceRenderer.
 * 
 * @since 2.0
 */
public abstract class ChoicesListView<T> extends ListView<T>
{
    private IChoiceRenderer<? super T> choiceRenderer;
    
    /**
     * Construct a list view that will expose the specified IChoiceRenderer for
     * rendering its list items.
     */
    public ChoicesListView(String id,
                           IModel<? extends List<? extends T>> choices,
                           IChoiceRenderer<? super T> renderer)
    {
        super(id, choices);
        this.choiceRenderer = renderer;
    }
    
    /**
     * Returns the IChoiceRenderer that was passed to the constructor.
     */
    public IChoiceRenderer<? super T> getChoiceRenderer()
    {
        return this.choiceRenderer;
    }
    
    /**
     * Convenience method exposed to subclasses for determining the display
     * value of a given list item. This delegates to
     * {@link IChoiceRenderer#getDisplayValue getDisplayValue()} on the
     * IChoiceRenderer. The display value will be coverted to a String if
     * necessary using Wicket's {@link IConverter} system.
     * 
     * @param choice The current value of the list that is being rendered.
     */
    protected String getChoiceLabel(T choice)
    {
        // This code was copied from Wicket's CheckBoxMultipleChoice.java
        Object displayValue = getChoiceRenderer().getDisplayValue(choice);
        Class<?> objectClass = displayValue == null ? null : displayValue.getClass();
        // Get label for choice
        String label = "";
        if (objectClass != null && objectClass != String.class)
        {
            IConverter converter = getConverter(objectClass);
            label = converter.convertToString(displayValue, getLocale());
        }
        else if (displayValue != null)
        {
            label = displayValue.toString();
        }
        return label;
    }
    
    /**
     * Convenience method exposed to subclasses for determining the unique
     * ID of the given list item. This delegates to
     * {@link IChoiceRenderer#getIdValue getIdValue()} on the
     * IChoiceRenderer.
     * 
     * @param choice The current value of the list that is being rendered.
     * @param index The zero-indexed position of that value in the list.
     */
    protected String getChoiceValue(T choice, int index)
    {
        return getChoiceRenderer().getIdValue(choice, index);
    }
}
