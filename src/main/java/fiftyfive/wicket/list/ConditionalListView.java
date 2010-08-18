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

package fiftyfive.wicket.list;

import java.util.List;

import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

/**
 * A {@link ListView} that returns <code>false</code> for {@link #isVisible}
 * if its list is empty. This allows you to use
 * the <code>&lt;wicket:enclosure&gt;</code> tag to hide surrounding markup.
 * Therefore you can use this component to ensure that, for example,
 * the <code>&lt;ul&gt;</code> is ommitted when you have zero
 * <code>&lt;li&gt;</code> elements.
 * <em>This is necessary for valid XHTML markup.</em>
 * <pre>
 * &lt;wicket:enclosure child="item"&gt;
 *   &lt;ul&gt;
 *     &lt;li wicket:id=&quot;item&quot;&gt;...&lt;/li&gt;
 *   &lt;/ul&gt;
 * &lt;/wicket:enclosure&gt;
 *
 * add(new ConditionalListView&lt;Person&gt;("people", personList) {
 *     &#064;Override protected void populateItem(ListItem&lt;Person&gt; item)
 *     {
 *         item.add(...);
 *     }
 * });
 * </pre>
 */
public abstract class ConditionalListView<T> extends ListView<T>
{
    public ConditionalListView(String id)
    {
        super(id);
    }
    
    public ConditionalListView(String id,
                               IModel<? extends List<? extends T>> model)
    {
        super(id, model);
    }
    
    public ConditionalListView(String id, List<? extends T> list)
    {
        super(id, list);
    }
    
    /**
     * Returns <code>false</code> if the list is empty.
     */
    @Override
    public boolean isVisible()
    {
        return getViewSize() > 0;
    }
}
