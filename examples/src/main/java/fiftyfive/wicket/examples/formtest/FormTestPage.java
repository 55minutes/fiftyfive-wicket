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
package fiftyfive.wicket.examples.formtest;


import fiftyfive.wicket.datetime.CalendarControl;
import fiftyfive.wicket.examples.BasePage;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.annotation.strategy.MountQueryString;


@MountPath(path="form")
@MountQueryString
public class FormTestPage extends BasePage
{
    // The page parameter keys
    private static final String INITIAL_MONTH_PARAM = "initialMonth";
    private static final String START_DATE_PARAM = "startDate";
    private static final String END_DATE_PARAM = "endDate";
    
    // SimpleDateFormats to format initial, start and end dates.
    private static final SimpleDateFormat PARAM_FORMAT_DATE = new SimpleDateFormat("MM.dd.yyyy");
    private static final SimpleDateFormat PARAM_FORMAT_PAGEDATE = new SimpleDateFormat("MM.yyyy");
    
    /**
     * Return the parameters needed to construct a new FormTest page.
     *
     * @param initialDate   The month to display in the date widget on load.
     * @param startDate     The minimum valid date for the date widget.
     * @param endDate       The maximum valid date for the date widget.
     */
    public static PageParameters params(String initialDate, String startDate,
                                        String endDate)
    {
        PageParameters params = new PageParameters();
        params.put(INITIAL_MONTH_PARAM, initialDate);
        params.put(START_DATE_PARAM, startDate);
        params.put(END_DATE_PARAM, endDate);
        return params;
    }

    /**
     * FormTest constructor
     */
    public FormTestPage(final PageParameters params) throws Exception
    {
        super(params);
        
        _body.setMarkupId("form-test");
        
        Form form = new Form("form");
        
        CalendarControl cc = new CalendarControl("calendar-control")
        {
            public Date getStartDate()
            {
                try
                {
                    String startDate = params.getString(START_DATE_PARAM);
                    if(startDate == null || startDate.equals(""))
                    {
                        return null;
                    }
                    else
                    {
                        return PARAM_FORMAT_DATE.parse(startDate);
                    }
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            public Date getEndDate()
            {
                try
                {
                    String endDate = params.getString(END_DATE_PARAM);
                    if(endDate == null || endDate.equals(""))
                    {
                        return null;
                    }
                    else
                    {
                        return PARAM_FORMAT_DATE.parse(endDate);
                    }
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        };

        String initialDate = params.getString(INITIAL_MONTH_PARAM);
        if(initialDate != null && !initialDate.equals(""))
        {
            cc.getDatePicker().setInitialDateModel(
                new Model(PARAM_FORMAT_PAGEDATE.parse(initialDate)));            
        }
        
        form.add(cc);
        add(form);
    }
}
