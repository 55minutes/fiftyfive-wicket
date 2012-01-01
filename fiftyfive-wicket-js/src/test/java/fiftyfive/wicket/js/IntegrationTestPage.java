/**
 * Copyright 2012 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.js;

import java.util.Date;

import fiftyfive.wicket.js.datetime.JQueryDatePicker;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

public class IntegrationTestPage extends BaseIntegrationTestPage
{
    public IntegrationTestPage()
    {
        super();
        add(new DomReadyScript("alert('page init')"));
        add(new IntegrationTestPanel("panel1"));
        add(new IntegrationTestTemplatePanel("panel2"));
        
        add(new Form("form")
            .add(DateTextField.forDatePattern("date",
                                              new Model<Date>(),
                                              "MM/dd/yyyy")
                .setRequired(true)
                .add(new JQueryDatePicker()))
        );
    }
}
