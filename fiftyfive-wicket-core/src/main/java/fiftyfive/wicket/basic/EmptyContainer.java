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
package fiftyfive.wicket.basic;

import fiftyfive.wicket.util.Shortcuts;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 * A WebMarkupContainer that is visible only if its model object is empty.
 * This is useful for displaying a conditional message when an accompanying
 * list view is empty.
 * <p>
 * For example:
 * <pre class="example">
 * &lt;wicket:enclosure&gt;
 *   &lt;ul&gt;
 *     &lt;li wicket:id="friends"&gt;...&lt;/li&gt;
 *   &lt;/ul&gt;
 * &lt;/wicket:enclosure&gt;
 * &lt;p wicket:id="no-friends"&gt;
 *   Your friends list is empty.
 * &lt;/p&gt;</pre>
 * <pre class="example">
 * IModel&lt;List&lt;Friend&gt;&gt; friendsModel = ...;
 * add(new ConditionalListView("friends", friendsModel) {
 *     &#064;Override
 *     protected void populateItem(ListItem&lt;Friend&gt; item)
 *     {
 *         ...
 *     }
 * });
 * // This will be visible only when friendsModel is empty
 * add(new EmptyContainer("no-friends", friendsModel));</pre>
 * 
 * @since 2.0
 */
public class EmptyContainer extends WebMarkupContainer
{
    /**
     * Constructor with an implied model.
     */
    public EmptyContainer(String id)
    {
        super(id);
    }
    
    /**
     * Constructor with an explicit model.
     */
    public EmptyContainer(String id, IModel<?> model)
    {
        super(id, model);
    }
    
    /**
     * Returns {@code true} if the model object of this component is empty.
     * @see Shortcuts#empty
     */
    public boolean isModelEmpty()
    {
        return Shortcuts.empty(getDefaultModelObject());
    }
    
    /**
     * Sets the visibility of this component to {@code true} if
     * {@link #isModelEmpty} returns {@code true}. Otherwise sets visibility
     * to {@code false}.
     */
    @Override
    protected void onConfigure()
    {
        super.onConfigure();
        setVisible(isModelEmpty());
    }
}
