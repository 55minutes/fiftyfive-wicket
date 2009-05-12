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
package fiftyfive.wicket.list;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fiftyfive.wicket.util.Shortcuts.label;
import static fiftyfive.wicket.util.Shortcuts.prop;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.list.ListItem;

public class ConditionalListViewTestPage extends WebPage
{
    public ConditionalListViewTestPage()
    {
        super();
        add(new ConditionalListView<String>("empty", prop(this, "emptyList")) {
            @Override protected void populateItem(ListItem<String> item)
            {
                // pass
            }
        });
        add(new ConditionalListView<String>("null") {
            @Override protected void populateItem(ListItem<String> item)
            {
                // pass
            }
        });
        add(new ConditionalListView<String>("some", Arrays.asList("a", "b")) {
            @Override protected void populateItem(ListItem<String> item)
            {
                item.add(label("label", item.getModel()));
            }
        });
    }
    
    public List<String> getEmptyList()
    {
        return Collections.EMPTY_LIST;
    }
}
