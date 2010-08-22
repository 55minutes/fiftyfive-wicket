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

package fiftyfive.wicket.basic;

import fiftyfive.wicket.util.Shortcuts;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


/**
 * An extension of Wicket's Label component that allows a placeholder value to
 * be specified. If the label's default model is empty, then:
 * <ul>
 * <li>A CSS class of "empty" will be added to the tag</li>
 * <li>The placeholder value (if one is specified) will be emitted as the
 * body of the tag</li>
 * </ul>
 * If the model is not empty, this behaves like the standard Label.
 * @see #setPlaceholder
 * @see Label
 */
public class LabelWithPlaceholder extends Label
{
    private IModel<?> _placeholderValue;
    
    /**
     * @see Label#Label(String)
     */
    public LabelWithPlaceholder(final String id)
    {
        this(id, (IModel) null);
    }

    /**
     * @see Label#Label(String, String)
     */
    public LabelWithPlaceholder(final String id, String label)
    {
        this(id, new Model(label));
    }

    /**
     * @see Label#Label(String, IModel)
     */
    public LabelWithPlaceholder(final String id, IModel<?> model)
    {
        super(id, model);
        add(Shortcuts.cssClassIf("empty", this, "empty"));
    }
    
    /**
     * Sets the value that will be used if the value provided by the
     * model is empty.
     * Note that this String value will <b>not</b> be escaped, so make sure
     * that you provide an HTML-safe value.
     */
    public LabelWithPlaceholder setPlaceholder(String valueIfEmpty)
    {
        return setPlaceholder(Model.of(valueIfEmpty));
    }
    
    /**
     * Sets the value that will be used if the value provided by the label's
     * normal model is empty. For localization, consider passing in an
     * instance of Wicket's StringResourceModel.
     * Note that this value will be escaped based on the label's
     * {@link Label#getEscapeModelStrings() getEscapeModelStrings()} flag.
     */
    public LabelWithPlaceholder setPlaceholder(IModel<?> valueIfEmpty)
    {
        _placeholderValue = valueIfEmpty;
        return this;
    }
    
    /**
     * Returns <code>true</code> if the model of this label is empty.
     * @see Shortcuts#empty
     */
    public boolean isEmpty()
    {
        return Shortcuts.empty(internalGetDefaultModelObjectAsString());
    }
    
    /**
     * Detaches the model that holds the placeholder value.
     */
    @Override
    protected void onDetach()
    {
        if(null != _placeholderValue) _placeholderValue.detach();
        super.onDetach();
    }

    /**
     * Renders the label using the string value of the model as the tag body.
     * If the value of the model is empty and placeholder was provided by
     * {@link #setPlaceholder}, use the placeholder instead.
     * @see Shortcuts#empty
     */
    @Override
    protected void onComponentTagBody(MarkupStream markupStream,
                                      ComponentTag openTag)
    {
        String str = internalGetDefaultModelObjectAsString();
        if(_placeholderValue != null && Shortcuts.empty(str))
        {
            str = getDefaultModelObjectAsString(_placeholderValue.getObject());
        }
        replaceComponentTagBody(markupStream, openTag, str);
    }
    
    /**
     * A wrapper for {@link #getDefaultModelObjectAsString()} that can be
     * overridden by subclasses.
     */
    protected String internalGetDefaultModelObjectAsString()
    {
        return getDefaultModelObjectAsString();
    }
}
