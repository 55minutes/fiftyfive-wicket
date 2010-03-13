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

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 * A customized DropDownChoice used with the 
 * {@link BooleanQueryBuilder} that will update the
 * associated query list model upon picking a recent query from the recent
 * queries list.
 *
 * @author Richa Avasthi
 */
public class BooleanQueryChoice extends DropDownChoice
{
    // The model of the parent BooleanQueryBuilder
    private IModel _queryModel;
    // The number of choices to display
    private int _numChoices;
    // A list of the components to repaint on selection change
    private List _repaintList;
    
    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#DropDownChoice(String)
     */
    public BooleanQueryChoice(final String id, IModel queryModel, int num, 
                              List repaintList)
    {
        super(id);
        init(queryModel, num, repaintList);
    }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#DropDownChoice(String, List)
     */
    public BooleanQueryChoice(final String id, final List choices, 
                              IModel queryModel, int num, List repaintList)
    {
        super(id, choices);
        init(queryModel, num, repaintList);
    }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#DropDownChoice(String,
     *      java.util.List,
     *      org.apache.wicket.markup.html.form.IChoiceRenderer)
     */
    public BooleanQueryChoice(final String id, final List data, 
                              final BooleanChoiceRenderer renderer, 
                              IModel queryModel, int num, List repaintList)
    {
        super(id, data, renderer);
        init(queryModel, num, repaintList);
    }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#DropDownChoice(String,
     *      IModel, List)
     */
    public BooleanQueryChoice(final String id, IModel model, final List choices,
                              IModel queryModel, int num, List repaintList)
    {
        super(id, model, choices);
        init(queryModel, num, repaintList);
    }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#DropDownChoice(String,
     *      org.apache.wicket.model.IModel,
     *      java.util.List,
     *      org.apache.wicket.markup.html.form.IChoiceRenderer)
     */
    public BooleanQueryChoice(final String id, IModel model, final List data,
                              final BooleanChoiceRenderer renderer, 
                              IModel queryModel, int num, List repaintList)
    {
        super(id, model, data, renderer);
        init(queryModel, num, repaintList);
    }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#DropDownChoice(String,
     *      IModel)
     */
    public BooleanQueryChoice(final String id, IModel choices, 
                              IModel queryModel, int num, List repaintList) 
    {
        super(id, choices);
        init(queryModel, num, repaintList);
    }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#DropDownChoice(String,
     *      IModel,IModel)
     */
    public BooleanQueryChoice(String id, IModel model, IModel choices, 
                              IModel queryModel, int num, List repaintList)
    {
        super(id, model, choices);
        init(queryModel, num, repaintList);
    }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#DropDownChoice(String,
     *      org.apache.wicket.model.IModel,
     *      org.apache.wicket.markup.html.form.IChoiceRenderer)
     */
    public BooleanQueryChoice(String id, IModel choices, 
                              BooleanChoiceRenderer renderer, IModel queryModel, 
                              int num, List repaintList)
    {
        super(id, choices, renderer);
        init(queryModel, num, repaintList);
    }

    /**
     * @see org.apache.wicket.markup.html.form.DropDownChoice#DropDownChoice(String,
     *      org.apache.wicket.model.IModel,
     *      org.apache.wicket.markup.html.form.IChoiceRenderer)
     */
    public BooleanQueryChoice(String id, IModel model, IModel choices, 
                              BooleanChoiceRenderer renderer, IModel queryModel, 
                              int num, List repaintList)
    {
        super(id, model, choices, renderer);
        init(queryModel, num, repaintList);
    }
    
    public void onDetach()
    {
        super.onDetach();
        _queryModel.detach();
    }

    /**
     * Handle the container's body.
     * 
     * @param markupStream
     *            The markup stream
     * @param openTag
     *            The open tag for the body
     * @see org.apache.wicket.Component#onComponentTagBody(MarkupStream, ComponentTag)
     */
    protected void onComponentTagBody(final MarkupStream markupStream, 
                                      final ComponentTag openTag)
    {
        List choices = super.getChoices();
        final AppendingStringBuffer buffer = 
            new AppendingStringBuffer((choices.size() * 50) + 16);
        final String selected = getValue();

        // Append default option
        buffer.append(getDefaultChoice(selected));

        int startIndex;
        int cSize = choices.size();
        if(cSize < _numChoices)
        {
            startIndex = 0;
        }
        else
        {
            startIndex = cSize - _numChoices;
        }
        
        for (int index = startIndex; index < cSize; index++)
        {
            final Object choice = choices.get(index);
            appendOptionHtml(buffer, choice, index, selected);
        }

        buffer.append("\n");
        replaceComponentTagBody(markupStream, openTag, buffer);
    }

    /*
    ** Private method called from the constructors; initializes internal
    ** number of choices and model properties.
    */
    private void init(IModel model, int num, List repaintList)
    {
        if(num <= 0 || model == null || repaintList == null)
            throw new IllegalArgumentException();
            
        _queryModel = model;
        _numChoices = num;
        _repaintList = repaintList;
        
        // Allow a null choice
        setNullValid(true);
        
        add(new AjaxFormComponentUpdatingBehavior("onchange")
        {
            protected void onUpdate(AjaxRequestTarget target)
            {
                /*
                ** Copy the currently selected query into the model for the
                ** BooleanQueryBuilder's list view.
                */
                BooleanQuery bq2 = (BooleanQuery )getModelObject();

                // If the null choice was selected, create a new, blank query.
                if(bq2 == null)
                {
                    bq2 = new BooleanQuery();
                }
                
                // Update the query model with the merged choice list
                _queryModel.setObject(bq2);
                
                for(Object repaintItem : _repaintList)
                {
                    target.addComponent((Component )repaintItem);
                }
            }
        });
        
        setOutputMarkupId(true);
    }
}
