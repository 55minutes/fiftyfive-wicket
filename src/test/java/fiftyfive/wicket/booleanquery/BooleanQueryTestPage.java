/*
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
package fiftyfive.wicket.booleanquery;


import fiftyfive.wicket.util.Shortcuts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.WebPage;


public class BooleanQueryTestPage extends WebPage
{
    private IModel _queryModel;
    private List<BooleanQuery> _recentQueries;
    
    public BooleanQueryTestPage()
    {
        _queryModel = new Model(new BooleanQuery());
        _recentQueries = new ArrayList<BooleanQuery>(5);

        Form form = new Form("form");

        final List<String> choices = 
            Arrays.asList(new String[] {"January", "February", "March", 
                                        "April", "May", "June",
                                        "July", "August", "September",
                                        "October", "November", "December"});

        IAbbrevChoiceRenderer renderer = new IAbbrevChoiceRenderer() {
            public Object getDisplayValue(Object object)
            {
                return object;
            }
            
            public String getIdValue(Object object, int index)
            {
                return ((Integer )index).toString();
            }
            
            public Object getAbbrevDisplayValue(Object object)
            {
                return ((String )object).substring(0, 2);
            }
        };
        
        final BooleanQueryBuilder bqb = 
            new BooleanQueryBuilder("boolean-query", 
                                    _queryModel,
                                    Model.ofList(choices), renderer,
                                    Model.ofList(_recentQueries));
        form.add(bqb);
        
        final Label result = Shortcuts.label("result", this, "query");
        result.setOutputMarkupId(true);
        form.add(result);

        form.add(new AjaxButton("submit") {
            public void onSubmit(AjaxRequestTarget target, Form form)
            {
                bqb.updateQuery();
                target.addComponent(bqb);
                target.addComponent(result);
            }
        });

        add(form);
    }

    public String getQuery() 
    {
        return _queryModel.getObject().toString();
    }
}
