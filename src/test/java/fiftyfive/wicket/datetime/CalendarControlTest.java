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

import fiftyfive.wicket.BaseWicketTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarControlTest extends BaseWicketTest
{
    private static final Logger logger = LoggerFactory.getLogger(
        CalendarControlTest.class
    );

    @Test
    public void testCalendarControl() throws Exception
    {
        _tester.startPage(CalendarControlTestPage.class);
        _tester.assertRenderedPage(CalendarControlTestPage.class);

        /*
        ** Assert that the options for the minimimum, maximum and initial dates
        ** are present in the generated javascript.
        */
        _tester.assertContains("calendarInit:\\s+\\{.*mindate:\"05/03/2009\".*\\},");
        _tester.assertContains("calendarInit:\\s+\\{.*maxdate:\"06/14/2009\".*\\},");
        _tester.assertContains("calendarInit:\\s+\\{.*pagedate:\"05/2009\".*\\},");
    }
}
