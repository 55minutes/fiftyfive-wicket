/*
 * Copyright 2009 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.header;

import fiftyfive.wicket.BaseWicketTest;
import org.junit.Test;

public class InternetExplorerCssTest extends BaseWicketTest
{
    @Test
    public void testRender() throws Exception
    {
        _tester.startPage(InternetExplorerCssTestPage.class);
        _tester.assertRenderedPage(InternetExplorerCssTestPage.class);
        _tester.assertResultPage(
            InternetExplorerCssTestPage.class,
            "InternetExplorerCssTestPage-expected.html"
        );
    }
}
