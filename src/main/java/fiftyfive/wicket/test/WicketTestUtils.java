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
package fiftyfive.wicket.test;

import java.io.IOException;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.settings.IResourceSettings;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.tester.WicketTesterHelper;
import org.junit.Assert;


/**
 * Helper functions for simplifying testing of Wicket pages and components.
 */
public abstract class WicketTestUtils
{
    /**
     * Asserts that an XPath expression can be found in the most recently
     * rendered Wicket page.
     */
    public static void assertXPath(WicketTester wt, String expr)
    {
        // TODO
    }

    /**
     * Asserts that exactly {@code count} number of instances of a given
     * XPath expression can be found in the most recently
     * rendered Wicket page.
     */
    public static void assertXPath(int count, WicketTester wt, String expr)
    {
        // TODO
    }
    
    /**
     * Assert that the last rendered page has a content-type of text/html
     * and is valid XHTML markup.
     *
     * @param tester A WicketTester object that has just rendered a page.
     */
    public static void assertValidMarkup(WicketTester tester) 
        throws IOException
    {
        assertValidMarkup(tester, XHtmlValidator.DEF_NUM_LINES_CONTEXT);
    }

    /**
     * Assert that the last rendered page has a content-type of text/html
     * and is valid XHTML markup.
     *
     * @param tester A WicketTester object that has just rendered a page.
     * @param linesContext The number of lines of context to include around
     *                     each validation error.
     */
    public static void assertValidMarkup(WicketTester tester, int linesContext) 
        throws IOException
    {
        String type = tester.getServletResponse().getContentType();
        Assert.assertTrue(
            type.equals("text/html") ||
            type.startsWith("text/html;")
        );
        
        String document = tester.getServletResponse().getDocument();
        XHtmlValidator validator = new XHtmlValidator();
        validator.setNumLinesContext(linesContext);
        validator.parse(document);
        
        if(!validator.isValid())
        {
            Assert.fail(
                "Invalid XHTML:\n" +
                WicketTesterHelper.asLined(validator.getErrors())
            );
        }
    }
    
    /**
     * Renders a component using a snippet of markup. Example:
     * <pre>
     * startComponentWithMarkup(
     *     tester,
     *     new Label("label", "Hello, world!),
     *     "&lt;span wicket:id=\"label\"&gt;replaced by Wicket&lt;/span&gt;"
     * );
     * </pre>
     */
    public static void startComponentWithMarkup(WicketTester wt,
                                                final Component c,
                                                final String markup)
    {
        IResourceSettings rs = wt.getApplication().getResourceSettings();
        final IResourceFinder origFinder = rs.getResourceFinder();
        
        // When markup is requested via the resource finder system, we'll
        // provide the markup string if the request is for the component we
        // are trying to render.
        rs.setResourceFinder(new IResourceFinder() {
            public IResourceStream find(Class<?> clazz, String pathname)
            {
                if(clazz.equals(TestPanel.class))
                {
                    return new StringResourceStream(String.format(
                        "<wicket:panel>%s</wicket:panel>", markup
                    ));
                }
                return origFinder.find(clazz, pathname);
            }
        });
        
        // Render a panel with the component under test inside of it. The
        // panel will obtain its markup via the system declared above.
        wt.startPanel(new TestPanelSource() {
            public Panel getTestPanel(String panelId)
            {
                Panel p = new TestPanel(panelId);
                p.add(c);
                return p;
            }
        });
    }
    
    private static class TestPanel extends Panel
    {
        public TestPanel(String id)
        {
            super(id);
        }
    }
}
