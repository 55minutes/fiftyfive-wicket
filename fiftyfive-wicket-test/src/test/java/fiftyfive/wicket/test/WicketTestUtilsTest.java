/**
 * Copyright 2013 55 Minutes (http://www.55minutes.com)
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

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

public class WicketTestUtilsTest
{
    /**
     * Test that an HTML document containing HTML entities like
     * {@code &nbsp;} and {@code &copy;} are parsed with the xpath helper
     * method even though they aren't formal XML.
     */
    @Test
    public void testAssertXPath_htmlEntities() throws Exception
    {
        WicketTester tester = new WicketTester();
        final String html =
            "<!DOCTYPE html><html>" +
            "<head><title>&ldquo;title&rdquo; &raquo; test</title></head>" +
            "<body><section><p>&copy;<img></p></section></body>" +
            "</html>";
        tester.startPage(new PageWithInlineMarkup(html));
        WicketTestUtils.assertXPath(tester, "/html/body/section/p");
    }
}
