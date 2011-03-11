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

package fiftyfive.wicket.basic;

import fiftyfive.wicket.BaseWicketTest;
import org.junit.Test;
import static fiftyfive.wicket.test.WicketTestUtils.*;

public class LabelWithPlaceholderTest extends BaseWicketTest
{
    @Test
    public void testRender_normal() throws Exception
    {
        startComponentWithXHtml(
            this.tester,
            new LabelWithPlaceholder("test", "hello!").setPlaceholder("foo"),
            "<p wicket:id=\"test\">blah</p>"
        );
        this.tester.assertContains("<p>hello!</p>");
    }

    @Test
    public void testRender_emptyNoDefault() throws Exception
    {
        startComponentWithXHtml(
            this.tester,
            new LabelWithPlaceholder("test"),
            "<p wicket:id=\"test\">blah</p>"
        );
        this.tester.assertContains("<p class=\"empty\"></p>");
    }

    @Test
    public void testRender_emptyWithDefault() throws Exception
    {
        startComponentWithXHtml(
            this.tester,
            new LabelWithPlaceholder("test").setPlaceholder("foo & bar"),
            "<p wicket:id=\"test\">blah</p>"
        );
        this.tester.assertContains("<p class=\"empty\">foo &amp; bar</p>");
    }

    @Test
    public void testRender_emptyWithDefaultNoEscape() throws Exception
    {
        startComponentWithXHtml(
            this.tester,
            new LabelWithPlaceholder("test")
                .setPlaceholder("foo & bar")
                .setEscapeModelStrings(false),
            "<p wicket:id=\"test\">blah</p>"
        );
        this.tester.assertContains("<p class=\"empty\">foo & bar</p>");
    }
}
