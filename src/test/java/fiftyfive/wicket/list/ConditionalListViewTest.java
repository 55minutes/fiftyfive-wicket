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
package fiftyfive.wicket.list;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fiftyfive.wicket.BaseWicketTest;
import org.apache.wicket.markup.html.list.ListItem;
import org.junit.Test;
import static fiftyfive.wicket.test.WicketTestUtils.*;
import static fiftyfive.wicket.util.Shortcuts.*;

public class ConditionalListViewTest extends BaseWicketTest
{
    public static final String HTML =
        "<wicket:enclosure child=\"c\">" +
        "<ul><li wicket:id=\"c\"><span wicket:id=\"label\"></span></li></ul>" +
        "</wicket:enclosure>";
    
    @Test
    public void testRender_null() throws Exception
    {
        doRender(null);
        assertXPath(0, _tester, "//ul");
    }

    @Test
    public void testRender_empty() throws Exception
    {
        doRender(Collections.EMPTY_LIST);
        assertXPath(0, _tester, "//ul");
    }

    @Test
    public void testRender_some() throws Exception
    {
        doRender(Arrays.asList("a", "b", "c"));
        assertXPath(3, _tester, "//li");
        assertXPath(_tester, "//ul/li/span[text()='a']");
        assertXPath(_tester, "//ul/li/span[text()='b']");
        assertXPath(_tester, "//ul/li/span[text()='c']");
    }
    
    private void doRender(List<String> list) throws Exception
    {
        startComponentWithXHtml(_tester, new TestListView("c", list), HTML);
    }
    
    private static class TestListView extends ConditionalListView<String>
    {
        TestListView(String id, List<String> list)
        {
            super(id, list);
        }
        
        @Override
        protected void populateItem(ListItem<String> item)
        {
            item.add(label("label", item.getModel()));
        }
    }
}
