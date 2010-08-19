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

package fiftyfive.wicket.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fiftyfive.wicket.BaseWicketTest;
import org.apache.wicket.model.IModel;
import org.junit.Assert;
import org.junit.Test;

public class ShortcutsTest extends BaseWicketTest
{
    @Test
    public void testShortcutsPage() throws Exception
    {
        _tester.startPage(ShortcutsTestPage.class);
        _tester.assertRenderedPage(ShortcutsTestPage.class);
        _tester.assertResultPage(
            ShortcutsTestPage.class,
            "ShortcutsTestPage-expected.html"
        );
    }
    
    @Test
    public void testLoadedModel()
    {
        final List<String> values = new ArrayList();        
        IModel model = Shortcuts.loadedModel(new TestBean(values), "load");
        
        List<String> loaded = (List<String>) model.getObject();
        Assert.assertTrue(loaded == values);
        Assert.assertTrue(loaded.size() == 1);
        
        loaded = (List<String>) model.getObject();
        Assert.assertTrue(loaded == values);
        Assert.assertTrue(loaded.size() == 1);
        
        model.detach();
        loaded = (List<String>) model.getObject();
        Assert.assertTrue(loaded == values);
        Assert.assertTrue(loaded.size() == 2);        
    }
    
    @Test
    public void testEmpty()
    {
        Assert.assertTrue(Shortcuts.empty(null));
        Assert.assertTrue(Shortcuts.empty(""));
        Assert.assertTrue(Shortcuts.empty(" "));
        Assert.assertTrue(Shortcuts.empty(Collections.EMPTY_LIST));
        Assert.assertTrue(Shortcuts.empty(new String[0]));
        Assert.assertTrue(Shortcuts.empty(false));

        Assert.assertFalse(Shortcuts.empty(new Object()));
        Assert.assertFalse(Shortcuts.empty("a"));
        Assert.assertFalse(Shortcuts.empty(" a "));
        Assert.assertFalse(Shortcuts.empty(Collections.singleton("a")));
        Assert.assertFalse(Shortcuts.empty(new String[] { "a" }));
        Assert.assertFalse(Shortcuts.empty(true));
    }
    
    private static class TestBean
    {
        private List<String> _values;
        
        public TestBean(List<String> values)
        {
            _values = values;
        }
        
        public List<String> load()
        {
            _values.add("foo");
            return _values;
        }
    }
}
