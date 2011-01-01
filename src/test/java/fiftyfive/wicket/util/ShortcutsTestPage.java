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

package fiftyfive.wicket.util;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import static fiftyfive.wicket.util.Shortcuts.*;

public class ShortcutsTestPage extends WebPage
{
    public ShortcutsTestPage()
    {
        super();
        setDefaultModel(new CompoundPropertyModel(this));
        add(new Label("testProp", prop(this, "testProperty")));
        add(label("testLabel", this, "testProperty"));
        add(label("testLabelString", "string"));
        add(label("testProperty"));
        add(label("testAfterTag", "foo").add(afterTag("!")));
        add(new WebMarkupContainer("testCssClass").add(cssClass("test")));
    }

    public String getTestProperty()
    {
        return "value";
    }
    
    public boolean getTruth()
    {
        return true;
    }
    
    public Object getNone()
    {
        return null;
    }
}
