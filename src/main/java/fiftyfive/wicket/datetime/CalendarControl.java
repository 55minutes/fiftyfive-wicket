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

package fiftyfive.wicket.datetime;

import java.util.Date;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import static fiftyfive.wicket.util.Shortcuts.*;

/**
 * This class creates a date picker widget based on the
 * {@link RestrictedDatePicker} class.
 * 
 * @deprecated Consider writing jQuery UI JavaScript in your markup to handle
 *             all of your date picker needs; this is more powerful and
 *             customizable than what the Wicket DatePicker provides.
 *             See {@link fiftyfive.wicket.js.datetime.JQueryDatePicker JQueryDatePicker}
 *             for a simple example.
 */
public class CalendarControl extends Panel
{
    private DateTextField _field;
    private RestrictedDatePicker _picker;
    
    /**
     * CalendarControl constructor
     */
    public CalendarControl(String id) 
    {
        super(id);
        internalInit();
    }
    
    /**
     * CalendarControl constructor
     */
    public CalendarControl(String id, IModel model) 
    {
        super(id, model);
        internalInit();
    }
    
    /**
     * Returns the start date of the widget. Subclasses should override this
     * function to provide the start date.
     */
    public Date getStartDate()
    {
        return null;
    }
    
    /**
     * Returns the end date of the widget. Subclasses should override this
     * function to provide the end date.
     */
    public Date getEndDate()
    {
        return null;
    }
    
    /**
     * Returns the private RestrictedDatePicker.
     */
    public RestrictedDatePicker getDatePicker()
    {
        return _picker;
    }
    
    /**
     * Returns the private DateTextField. Only accessible to subclasses.
     */
    protected DateTextField getDateField()
    {
        return _field;
    }
    
    /*
    ** Initialize the component: create the date text field and the restricted
    ** date picker and link them together.
    */
    private void internalInit()
    {
        _field = new DateTextField("date-text-field", (IModel)getDefaultModel());
                              
        _picker = new RestrictedDatePicker(
                null, prop(this, "startDate"), prop(this, "endDate")
        );
        
        _field.add(_picker);
        add(_field);
    }
}
