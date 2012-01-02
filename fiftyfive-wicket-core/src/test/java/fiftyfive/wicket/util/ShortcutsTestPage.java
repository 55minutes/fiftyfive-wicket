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
package fiftyfive.wicket.util;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import static fiftyfive.wicket.util.Shortcuts.*;


public class ShortcutsTestPage extends WebPage
{
    public ShortcutsTestPage()
    {
        super();
        
        add(cssResource(ShortcutsTestPage.class));
        add(cssResource(ShortcutsTestPage.class, "all.css"));
        add(cssResource(ShortcutsTestPage.class, "screen.css", "screen"));
        
        setDefaultModel(new CompoundPropertyModel(this));
        add(new Label("testProp", prop(this, "testProperty")));
        add(label("testLabel", this, "testProperty"));
        add(label("testLabelString", "string"));
        add(label("testProperty"));
        add(label("testAfterTag", "foo").add(afterTag("!")));
        add(new WebMarkupContainer("testCssClass").add(cssClass("test")));
        
        add(new WebMarkupContainer("testToggledCssClass1")
            .add(toggledCssClass("active", Model.of(Boolean.TRUE))));
        add(new WebMarkupContainer("testToggledCssClass2")
            .add(toggledCssClass("active", Model.of(Boolean.FALSE))));
        add(new WebMarkupContainer("testToggledCssClass3")
            .add(toggledCssClass("active", Model.of(Boolean.TRUE))));
        add(new WebMarkupContainer("testToggledCssClass4")
            .add(toggledCssClass("active", Model.of(Boolean.FALSE))));
        add(new WebMarkupContainer("testToggledCssClass5")
            .add(toggledCssClass("active", Model.of(Boolean.TRUE))));
        add(new WebMarkupContainer("testToggledCssClass6")
            .add(toggledCssClass("active", Model.of(Boolean.FALSE))));

        add(new WebMarkupContainer("testToggledCssClass7")
            .add(toggledCssClass("active", "inactive", Model.of(Boolean.TRUE))));
        add(new WebMarkupContainer("testToggledCssClass8")
            .add(toggledCssClass("active", "inactive", Model.of(Boolean.FALSE))));
        add(new WebMarkupContainer("testToggledCssClass9")
            .add(toggledCssClass("active", "inactive", Model.of(Boolean.TRUE))));
        add(new WebMarkupContainer("testToggledCssClass10")
            .add(toggledCssClass("active", "inactive", Model.of(Boolean.FALSE))));
        add(new WebMarkupContainer("testToggledCssClass11")
            .add(toggledCssClass("active", "inactive", Model.of(Boolean.TRUE))));
        add(new WebMarkupContainer("testToggledCssClass12")
            .add(toggledCssClass("active", "inactive", Model.of(Boolean.FALSE))));
        add(new WebMarkupContainer("testToggledCssClass13")
            .add(toggledCssClass("active", "inactive", Model.of(Boolean.TRUE))));
        add(new WebMarkupContainer("testToggledCssClass14")
            .add(toggledCssClass("active", "inactive", Model.of(Boolean.FALSE))));
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
