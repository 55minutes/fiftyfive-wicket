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

package fiftyfive.wicket.data;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import static fiftyfive.wicket.util.Shortcuts.label;

public class DtoDataProviderTestPage extends WebPage
{
    private BeanResultProvider _provider;
    
    public DtoDataProviderTestPage()
    {
        super();
        
        _provider = new BeanResultProvider();
        DataView<Bean> dataView = new DataView<Bean>("beans", _provider) {
            protected void populateItem(Item<Bean> item)
            {
                item.add(label("label", item.getModel()));
            }
        };
        _provider.setPageableView(dataView);
        dataView.setItemsPerPage(10);
        add(dataView);
        
        add(new PagingNavigator("paging", dataView));
    }
    
    public int getLoadCount()
    {
        return _provider.getLoadCount();
    }
}
