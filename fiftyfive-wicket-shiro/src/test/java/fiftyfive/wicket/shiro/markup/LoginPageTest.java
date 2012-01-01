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
package fiftyfive.wicket.shiro.markup;

import fiftyfive.wicket.shiro.BaseTest;
import fiftyfive.wicket.test.WicketTestUtils;
import org.junit.Test;

public class LoginPageTest extends BaseTest
{
    @Test
    public void testRedirectToHomeIfAlreadyAuthenticated() throws Exception
    {
        mockAuthenticated();
        this.tester.startPage(LoginPage.class);
        this.tester.assertRenderedPage(this.tester.getApplication().getHomePage());
    }

    @Test
    public void testRender() throws Exception
    {
        mockGuest();
        this.tester.startPage(LoginPage.class);
        this.tester.assertRenderedPage(LoginPage.class);
        WicketTestUtils.assertValidMarkup(this.tester);
    }
}
