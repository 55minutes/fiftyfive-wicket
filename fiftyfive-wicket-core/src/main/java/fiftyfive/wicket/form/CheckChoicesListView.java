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
package fiftyfive.wicket.form;

import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A list view that renders a list of checkboxes with appropriate labels
 * based on an {@link IChoiceRenderer}. When used in combination with a
 * {@link org.apache.wicket.markup.html.form.CheckGroup}, this a good
 * alternative to Wicket's built-in
 * {@link org.apache.wicket.markup.html.form.CheckBoxMultipleChoice},
 * because it gives you full control over the markup and is extensible.
 * <p>
 * Your markup must contain the following:
 * <ul>
 * <li>{@code <input type="checkbox" wicket:id="check" />} where you
 *     want the checkbox to appear.</li>
 * <li>A component with {@code wicket:id="label"} where you want the display
 *     value to appear.</li>
 * </ul>
 * <p>
 * For example:
 * <pre class="example">
 * &lt;wicket:container wicket:id="group"&gt;
 *   &lt;label wicket:id="choices"&gt;
 *     &lt;input type="checkbox" wicket:id="check" /&gt;
 *     &lt;wicket:container wicket:id="label"&gt;Label&lt;/wicket:container&gt;
 *   &lt;/label&gt;
 * &lt;/wicket:container&gt;
 * 
 * add(new CheckGroup("group", selectedItemsModel)
 *     .add(new CheckChoicesListView("choices", choicesModel, renderer)));</pre>
 * <p>
 * You can also override {@link #populateItem(ListItem) populateItem()}
 * if you want to display additional data per checkbox choice, like a 
 * description paragraph.
 * 
 * @since 2.0
 */
public class CheckChoicesListView<T> extends ChoicesListView<T>
{
    public CheckChoicesListView(String id,
                                IModel<? extends List<? extends T>> choices,
                                IChoiceRenderer<? super T> renderer)
    {
        super(id, choices, renderer);
    }
    
    protected void populateItem(ListItem<T> it)
    {
        final int index = it.getIndex();
        it.add(new Label("label", getChoiceLabel(it.getModelObject())));
        it.add(new Check<T>("check", it.getModel()) {
            
            @Override
            public String getValue()
            {
                return getChoiceValue(getModelObject(), index);
            }
            
            @Override
            public IModel<String> getLabel()
            {
                return Model.of(getChoiceLabel(getModelObject()));
            }
            
            @Override
            protected boolean getStatelessHint()
            {
                return true;
            }
        });
    }
}
