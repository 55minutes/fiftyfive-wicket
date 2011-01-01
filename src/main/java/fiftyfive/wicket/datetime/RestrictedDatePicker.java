/**
 * Copyright 2011 55 Minutes (http://www.55minutes.com)
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.model.IModel;

/**
 * This class is based on {@link org.apache.wicket.extensions.yui.calendar.DatePicker},
 * with the added functionality that a minimum and/or maximum date can be
 * specified by the caller.
 * 
 * @deprecated Consider writing jQuery UI JavaScript in your markup to handle
 *             all of your date picker needs; this is more powerful and
 *             customizable than what the Wicket DatePicker provides.
 *             See {@link fiftyfive.wicket.js.datetime.JQueryDatePicker JQueryDatePicker}
 *             for a simple example.
 */
public class RestrictedDatePicker extends DatePicker
{
    /*
    ** Models representing the start and end date to restrict the date picker
    ** to, and an initial date that determines which month is displayed at
    ** load time.
    */
    private IModel _initialDateModel;
    private IModel _startDateModel;
    private IModel _endDateModel;

    /**
     * RestrictedDatePicker constructor
     */
    public RestrictedDatePicker()
    {
    }

    /**
     * RestrictedDatePicker constructor
     *
     * @param initialDateModel  A model representing the month to show when
     *                          the widget is first loaded.
     * @param startDateModel    A model representing the start date to restrict
     *                          the date picker to.
     * @param endDateModel      A model representing the end date to restrict
     *                          the date picker to.
     */
    public RestrictedDatePicker(IModel initialDateModel, IModel startDateModel, 
                                IModel endDateModel)
    {
        if(startDateModel == null && endDateModel == null)
        {
            throw new IllegalArgumentException("Both startDateModel and " +
                                               "endDateModel cannot be null.");
        }
        
        if(initialDateModel != null)
        {
            _initialDateModel = initialDateModel;
        }
        
        if(startDateModel != null)
        {
            _startDateModel = startDateModel;
        }
        
        if(endDateModel != null)
        {
            _endDateModel = endDateModel;
        }
    }
    
    /**
     * Detaches the various initial, start and end date models
     * that are associated with this picker.
     */
    @Override
    public void detach(Component component)
    {
        super.detach(component);
        if(_initialDateModel != null) _initialDateModel.detach();
        if(_startDateModel != null) _startDateModel.detach();
        if(_endDateModel != null) _endDateModel.detach();
    }

    /**
     * Set the initial date model.
     *
     * @param newInitialDateModel   A model representing the initial date for
     *                              this date picker; the widget will show the
     *                              month specified when loaded.
     */
    public void setInitialDateModel(IModel newInitialDateModel)
    {
        _initialDateModel = newInitialDateModel;
    }

    /**
     * Set the start date model.
     *
     * @param newStartDateModel     A model representing the start date for this
     *                              date picker; the user will be unable to
     *                              select a date prior to this date, if it is
     *                              specified.
     */
    public void setStartDateModel(IModel newStartDateModel)
    {
        _startDateModel = newStartDateModel;
    }

    /**
     * Set the end date model.
     *
     * @param newEndDateModel   A model representing the end date for this date
     *                          picker; the user will be unable to select a
     *                          date following this date, if it is specified.
     *                          
     */
    public void setEndDateModel(IModel newEndDateModel)
    {
        _endDateModel = newEndDateModel;
    }
    
    /**
     * Configure the widget with the start and end dates specified.
     * @param widgetProperties  the current widget properties
     */
    protected void configure(Map widgetProperties)
    {
        super.configure(widgetProperties);
        
        /*
        ** If the initial, start and end date model objects exist, add pagedate,
        ** mindate and maxdate properties to the widget configuration.
        */
        if(_initialDateModel != null)
        {
            Date id = (Date )_initialDateModel.getObject();
            if(id != null)
            {
                widgetProperties.put(
                    "pagedate",
                    new SimpleDateFormat(FORMAT_PAGEDATE).format(id)
                );
            }
        }

        if(_startDateModel != null)
        {
            Date sd = (Date )_startDateModel.getObject();
            if(sd != null)
            {
                widgetProperties.put(
                    "mindate",
                    new SimpleDateFormat(FORMAT_DATE).format(sd)
                );
            }
        }

        if(_endDateModel != null)
        {
            Date ed = (Date )_endDateModel.getObject();
            if(ed != null)
            {
                widgetProperties.put(
                    "maxdate",
                    new SimpleDateFormat(FORMAT_DATE).format(ed)
                );
            }
        }
    }
}
