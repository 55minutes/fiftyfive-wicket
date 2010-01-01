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


import fiftyfive.wicket.BaseWicketTest;
import fiftyfive.wicket.FoundationApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BooleanQueryTest extends BaseWicketTest
{
    private static final List _CHOICES = 
        Arrays.asList(new String[] {"January", "February", "March", 
                                    "April", "May", "June",
                                    "July", "August", "September",
                                    "October", "November", "December"});
    
    private static final String _EXPECTED_QUERY_1 = 
        "+January, +April, +June, -February, -May";
        
    private static final String _EXPECTED_QUERY_2 = 
        "+September, -June";
        
    private static final Logger logger = LoggerFactory.getLogger(
        BooleanQueryBuilder.class
    );

    @Test
    public void testBooleanQueryBuilder()
    {
        _tester.startPage(BooleanQueryTestPage.class);
        _tester.assertRenderedPage(BooleanQueryTestPage.class);
        
        // Test that choices rendered are same as the ones passed in.
        List<TagTester> renderedChoices = 
            _tester.getTagsByWicketId(_tester, "choice-name");
        
        for(TagTester choice : renderedChoices)
        {
            Assert.assertEquals(_CHOICES.get(renderedChoices.indexOf(choice)), 
                                choice.getValue());
        }
        
        // Set query: +January, +April, +June, -February, -May
        FormTester ft = _tester.newFormTester("form", false);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:0:plus", true);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:3:plus", true);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:5:plus", true);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:1:minus", true);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:4:minus", true);

        // Get the button to submit the form
        AjaxButton submit = 
            (AjaxButton )_tester.getComponentFromLastRenderedPage(
                "form:submit");
            
        // Submit the form
        _tester.executeAjaxEvent(submit, "onclick");
        
        /*
        ** Now check that the current query is what was just submitted, and that
        ** the recents list contains the latest query.
        */
        BooleanQueryBuilder bqb = 
            (BooleanQueryBuilder )_tester.getComponentFromLastRenderedPage(
                "form:boolean-query");
            
        BooleanQueryChoice bqc = 
            (BooleanQueryChoice )_tester.getComponentFromLastRenderedPage(
                "form:boolean-query:recents");

        List<BooleanQuery> recents = bqc.getChoices();
        
        String query = bqb.getDefaultModelObjectAsString();
        
        Assert.assertEquals(_EXPECTED_QUERY_1, query);
        Assert.assertEquals(_EXPECTED_QUERY_1, recents.get(0).toString());
        
        /*
        ** Now execute a different query. First, reset the checkboxes from the
        ** previous query.
        */
        // Reset the checkboxes
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:0:plus", false);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:3:plus", false);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:5:plus", false);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:1:minus", false);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:4:minus", false);
        // Set new query: +September, -June
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:8:plus", true);
        ft.setValue("boolean-query:pm-choice-wrapper:pm-choice:5:minus", true);

        // Submit the form
        _tester.executeAjaxEvent(submit, "onclick");
        
        recents = bqc.getChoices();
        
        query = bqb.getDefaultModelObjectAsString();
        
        Assert.assertEquals(_EXPECTED_QUERY_2, query);
        Assert.assertEquals(_EXPECTED_QUERY_1, recents.get(0).toString());
        Assert.assertEquals(_EXPECTED_QUERY_2, recents.get(1).toString());
        
    }
}