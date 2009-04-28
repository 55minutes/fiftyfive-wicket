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

        final List choices = 
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
                                    Model.of(choices), renderer,
                                    Model.of(_recentQueries));
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
