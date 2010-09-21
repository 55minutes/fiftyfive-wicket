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

package fiftyfive.wicket.js;

import java.util.regex.Pattern;

import fiftyfive.wicket.test.WicketTestUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

public class DomReadyScriptTest
{
    @Test
    public void testRender() throws Exception
    {
        WicketTester t = new WicketTester();
        WebMarkupContainer comp = new WebMarkupContainer("c");
        comp.add(new DomReadyScript("alert('ready!')"));
        
        WicketTestUtils.startComponentWithHtml(
            t, comp, "<span wicket:id=\"c\"></span>"
        );
        // Assert DOM ready script was rendered
        t.assertContains(Pattern.quote(
            "jQuery(function(){alert('ready!');});"
        ));
        // And jQuery was automatically included
        t.assertContains("<script .*src=\".*jquery.*\"></script>");
    }
}
