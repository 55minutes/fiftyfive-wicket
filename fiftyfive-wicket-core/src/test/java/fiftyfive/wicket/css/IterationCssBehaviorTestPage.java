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
import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;


public class IterationCssBehaviorTestPage extends WebPage
{
    public IterationCssBehaviorTestPage()
    {
        List<String> list = Arrays.asList("1", "2", "3", "4", "5", "6");
        ListView<String> listView = new ListView<String>("listView", list) {
            @Override
            protected void populateItem(ListItem<String> it)
            {
                it.add(new IterationCssBehavior("odd", "even", "first", "last", "iteration"));
            }
        };
        listView.setViewSize(5);
        add(listView);
        
        DataView<String> dataView = new DataView<String>("dataView", new ListDataProvider<String>(list)) {
            @Override
            protected void populateItem(Item<String> it)
            {
                it.add(new IterationCssBehavior("odd", "even", "first", "last", "iteration"));
            }
        };
        dataView.setItemsPerPage(5);
        add(dataView);

        add(newLoop("loop0", 0));
        add(newLoop("loop1", 1));
        add(newLoop("loop2", 2));
    }
    
    private Loop newLoop(String id, int iterations)
    {
        return new Loop(id, iterations) {
            @Override
            protected void populateItem(LoopItem it)
            {
                it.add(new IterationCssBehavior("odd", "even", "first", "last", "iteration"));
            }
        };
    }
}
