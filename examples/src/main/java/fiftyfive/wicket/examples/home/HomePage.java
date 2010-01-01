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
package fiftyfive.wicket.examples.home;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fiftyfive.wicket.booleanquery.BooleanQuery;
import fiftyfive.wicket.booleanquery.BooleanQueryBuilder;
import fiftyfive.wicket.booleanquery.IAbbrevChoiceRenderer;
import fiftyfive.wicket.examples.BasePage;
import fiftyfive.wicket.examples.formtest.FormTestPage;

import static fiftyfive.wicket.util.Shortcuts.label;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import org.wicketstuff.annotation.mount.MountPath;


@MountPath(path="home")
public class HomePage extends BasePage
{
    private IModel _queryModel;
    private List<BooleanQuery> _recentQueries;
    
    public HomePage(PageParameters params)
    {
        super(params);
        
        add(JavascriptPackageResource.getHeaderContribution(
            HomePage.class, "HomePage.js"
        ));

        _queryModel = new Model(new BooleanQuery());
        _recentQueries = new ArrayList<BooleanQuery>(5);

        _body.setMarkupId("home");
        
        PageParameters ftParams = FormTestPage.params("09.2007", "10.03.2007", "11.10.2007");
        add(new BookmarkablePageLink("form-test", FormTestPage.class, ftParams));
        
        Form form = new Form("form");

        final List<String> choices = 
            Arrays.asList(new String[] {"Alpha", "Bravo", "Charlie", 
                                        "Delta", "Echo"});
        final List<String> abbrevChoices = 
            Arrays.asList(new String[] {"A", "B", "C", "D", "E"});
        
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
                return abbrevChoices.get(choices.indexOf(object));
            }
        };
        
        final BooleanQueryBuilder bqb = 
            new BooleanQueryBuilder("boolean-query", 
                                    _queryModel,
                                    Model.ofList(choices), renderer,
                                    Model.ofList(_recentQueries));
        form.add(bqb);
        
        final Label result = label("result", this, "query");
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
