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

package fiftyfive.wicket.shiro.markup;

import fiftyfive.wicket.shiro.BaseTest;
import fiftyfive.wicket.test.WicketTestUtils;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.verify;

public class LogoutPageTest extends BaseTest
{
    @Test
    public void testLogoutAndRedirectToHomeWithFeedback() throws Exception
    {
        this.tester.startPage(LogoutPage.class);
        verify(this.mockSubject).logout();
        this.tester.assertRenderedPage(this.tester.getApplication().getHomePage());
        Assert.assertEquals(1, this.tester.getMessages(FeedbackMessage.INFO).size());
    }

    @Test
    public void testRedirectToUri() throws Exception
    {
        this.tester.startPage(LogoutPage.class, new PageParameters().add("to", "login"));
        this.tester.assertRenderedPage(LoginPage.class);
        Assert.assertEquals(1, this.tester.getMessages(FeedbackMessage.INFO).size());
    }
}
