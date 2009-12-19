/*
 * Copyright 2009 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.basic;


import fiftyfive.wicket.util.Shortcuts;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


// TODO: use I18N?

/**
 * An extension of Wicket's Label component that allows a default value to be
 * specified. If the model is empty, then:
 * <ul>
 * <li>A CSS class of "empty" will be added to the tag</li>
 * <li>The default value (if one is specified) will be emitted as the
 * body of the tag</li>
 * </ul>
 * If the model is not empty, this behaves like the standard Label.
 * @see #setDefault
 * @see Label
 */
public class LabelWithDefault extends Label
{
    private String _defaultValue;
    
    /**
     * @see Label#Label(String)
     */
    public LabelWithDefault(final String id)
    {
        this(id, (IModel) null);
    }

    /**
     * @see Label#Label(String, String)
     */
    public LabelWithDefault(final String id, String label)
    {
        this(id, new Model(label));
    }

    /**
     * @see Label#Label(String, IModel)
     */
    public LabelWithDefault(final String id, IModel<?> model)
    {
        super(id, model);
        add(Shortcuts.cssClassIf("empty", this, "empty"));
    }
    
    /**
     * Sets the value that will be used if the value provided by the
     * model is empty.
     */
    public LabelWithDefault setDefault(String valueIfEmpty)
    {
        _defaultValue = valueIfEmpty;
        return this;
    }
    
    /**
     * Returns <code>true</code> if the model of this label is empty.
     * @see Shortcuts#empty
     */
    public boolean isEmpty()
    {
        return Shortcuts.empty(getDefaultModelObject());
    }

    /**
     * Renders the label using the string value of the model as the tag body.
     * If the value of the model is empty and default was provided by
     * {@link #setDefault}, use the default instead.
     * @see Shortcuts#empty
     */
    @Override
    protected void onComponentTagBody(MarkupStream markupStream,
                                      ComponentTag openTag)
    {
        String str = getDefaultModelObjectAsString();
        if(_defaultValue != null && isEmpty())
        {
            str = _defaultValue;
        }
        replaceComponentTagBody(markupStream, openTag, str);
    }
}
