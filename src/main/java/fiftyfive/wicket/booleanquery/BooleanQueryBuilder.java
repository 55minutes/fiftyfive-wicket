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

package fiftyfive.wicket.booleanquery;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This panel constructs a boolean query widget wherein the user can elect to
 * include or exclude specific items from a provided choice list from a query. 
 * In addition to a JavaScript-driven UI widget enabling the user to (mutually 
 * exclusively) include or exclude a choice, a dropdown menu is included which
 * displays the last 5 queries constructed.
 * <p>
 * In order to update the query during form submission with the currently 
 * specified include/exclude data, calling classes should call the updateQuery()
 * method.
 * </p>
 */
public class BooleanQueryBuilder extends Panel
{
    private static final Logger logger = LoggerFactory.getLogger(
        BooleanQueryBuilder.class
    );
    
    // References for plus/minus images
    private static final ResourceReference ADD_IMAGE = 
        new ResourceReference(BooleanQueryBuilder.class, "icon-add.gif");
    private static final ResourceReference ADD_IMAGE_INACTIVE = 
        new ResourceReference(BooleanQueryBuilder.class, "icon-add-grey.gif");
    private static final ResourceReference REMOVE_IMAGE = 
        new ResourceReference(BooleanQueryBuilder.class, "icon-remove.gif");
    private static final ResourceReference REMOVE_IMAGE_INACTIVE = 
        new ResourceReference(BooleanQueryBuilder.class, "icon-remove-grey.gif");
    
    private ListView _lv;
    private IModel _recentQueryModel;
    private IAbbrevChoiceRenderer _renderer;
    
    /**
     * Constructor.
     *
     * @param id                the wicket:id of the element
     * @param queryModel        the model representing the query
     * @param choices           a model representing the choices to display
     * @param choiceRenderer    a choice renderer specifying how to display
     *                          each choice as an abbreviated string
     */
    public BooleanQueryBuilder(String id, final IModel queryModel, 
                               IModel choices, IAbbrevChoiceRenderer choiceRenderer,
                               IModel recentQueryModel)
    {
        super(id, queryModel);
        
        _recentQueryModel = recentQueryModel;
        _renderer = choiceRenderer;
        
        /*
        ** Create container for list view, as we will need to redraw it when
        ** the user selects a choice from the pulldown menu.
        */
        final WebMarkupContainer pmWrap = new WebMarkupContainer("pm-choice-wrapper");
        pmWrap.setOutputMarkupId(true);
        
        /*
        ** TODO: Update this to use the CheckBoxModel instead of BQItem to
        ** store checkbox state.
        */
        // Create list view for choices
        _lv = new ListView("pm-choice", choices) 
        {
            public void populateItem(final ListItem item)
            {
                BooleanQuery bq = (BooleanQuery )BooleanQueryBuilder.this.getDefaultModelObject();
                BooleanChoiceCheckBoxModel plusModel = 
                    new BooleanChoiceCheckBoxModel(bq, item.getModel(), true);
                BooleanChoiceCheckBoxModel minusModel = 
                    new BooleanChoiceCheckBoxModel(bq, item.getModel(), false);

                item.add(new CheckBox("plus", plusModel).setOutputMarkupId(true));
                item.add(new WebMarkupContainer("plus-image")
                    .add(new AttributeModifier("src", 
                                               new BooleanQueryIconModel(plusModel, true))));
                item.add(new CheckBox("minus", minusModel).setOutputMarkupId(true));
                item.add(new WebMarkupContainer("minus-image")
                    .add(new AttributeModifier("src", 
                                               new BooleanQueryIconModel(minusModel, false))));
                item.add(new Label("choice-name", item.getModelObject().toString()));
            }
        };
        pmWrap.add(_lv);
        add(pmWrap);
        
        // Create pulldown menu
        BooleanQuery bq = new BooleanQuery();
        List rl = Arrays.asList(new Component[] { pmWrap });
        final BooleanQueryChoice recents = 
            new BooleanQueryChoice("recents", new Model(bq), _recentQueryModel, 
                                   new BooleanChoiceRenderer(_renderer), 
                                   getDefaultModel(), 5, rl);
        add(recents);

        // Add resources
        add(CSSPackageResource.getHeaderContribution(BooleanQueryBuilder.class,
                                                     "BooleanQueryBuilder.css"));
        add(JavascriptPackageResource.getHeaderContribution(BooleanQueryBuilder.class,
                                                            "BooleanQueryBuilder.js"));
                                            
        setOutputMarkupId(true);
    }
    
    public void onDetach()
    {
        super.onDetach();
        _lv.detach();
        _recentQueryModel.detach();
    }

    /**
     * Update the query based on what's checked.
     */
    public void updateQuery()
    {
        // Add the latest query to the recent queries list
        List recentQueries = (List )_recentQueryModel.getObject();
        BooleanQuery bq = (BooleanQuery )getDefaultModelObject();
        BooleanQuery qc = (BooleanQuery )bq.clone();
        recentQueries.add(qc);
        logger.debug("Query: " + qc.toString());
    }

    /**
     * Return the url for the requested resource reference.
     */
    private String getUrl(ResourceReference ref)
    {
        return getRequestCycle().urlFor(ref).toString();
    }
    
    /**
     * This model returns the URL for a plus or minus icon, depending on the
     * include/exclude values inside the BQItem passed in.
     */
    private class BooleanQueryIconModel extends AbstractReadOnlyModel 
                                        implements IWrapModel
    {
        private boolean _isInclude;
        private BooleanChoiceCheckBoxModel _model;
        
        /**
         * Constructor
         * @param item      A model for the BQItem to check
         * @param isInclude Whether the URL to return is for the plus or minus 
         *                  icon
         */
        public BooleanQueryIconModel(BooleanChoiceCheckBoxModel model, 
                                     boolean isInclude)
        {
            _model = model;
            _isInclude = isInclude;
        }
        
        public Object getObject()
        {
            if(_isInclude)
            {
                if(_model.getObject() == Boolean.TRUE)
                    return getUrl(ADD_IMAGE);
                else
                    return getUrl(ADD_IMAGE_INACTIVE);
            }
            else
            {
                if(_model.getObject() == Boolean.TRUE)
                    return getUrl(REMOVE_IMAGE);
                else
                    return getUrl(REMOVE_IMAGE_INACTIVE);
            }
        }

        public IModel getWrappedModel()
        {
            return _model;
        }
    }
}   
