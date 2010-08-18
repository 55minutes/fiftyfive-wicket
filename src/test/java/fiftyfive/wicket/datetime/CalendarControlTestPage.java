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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

public class CalendarControlTestPage extends WebPage
{
    // SimpleDateFormats to format initial, start and end dates.
    private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("MM.dd.yyyy");
    private static final SimpleDateFormat FORMAT_PAGEDATE = new SimpleDateFormat("MM.yyyy");
    
    /**
     * CalendarControlTestPage constructor
     */
    public CalendarControlTestPage() throws Exception
    {
        super();
        
        Form form = new Form("form");
        
        CalendarControl cc = new CalendarControl("calendar-control")
        {
            public Date getStartDate()
            {
                try
                {
                    return FORMAT_DATE.parse("05.03.2009");
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
                    return FORMAT_DATE.parse("06.14.2009");
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        };

        cc.getDatePicker().setInitialDateModel(
            new Model(FORMAT_PAGEDATE.parse("05.2009")));            
        
        form.add(cc);
        add(form);
    }
}
