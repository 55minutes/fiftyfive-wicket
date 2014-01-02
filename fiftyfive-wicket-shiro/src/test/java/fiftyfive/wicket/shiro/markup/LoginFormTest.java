/**
 * Copyright 2014 55 Minutes (http://www.55minutes.com)
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
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Mockito.*;

public class LoginFormTest extends BaseTest
{
    @Test
    public void testRender() throws Exception
    {
        render();
    }

    @Test
    public void testLoginWithEmptyFieldsShowsErrors() throws Exception
    {
        render();
        FormTester form = render();
        form.submit();
        Assert.assertEquals(2, this.tester.getMessages(FeedbackMessage.ERROR).size());
    }

    @Test
    public void testLoginWithInvalidEmailShowsError() throws Exception
    {
        render();
        FormTester form = render();
        form.setValue("email", "agawegaeg");
        form.setValue("password", "secret");
        form.submit();
        Assert.assertEquals(1, this.tester.getMessages(FeedbackMessage.ERROR).size());
    }

    @Test
    public void testLoginWithIncorrectPasswordShowsError() throws Exception
    {
        doThrow(new IncorrectCredentialsException())
            .when(this.mockSubject).login(any(UsernamePasswordToken.class));
        
        render();
        FormTester form = render();
        form.setValue("email", "test@55minutes.com");
        form.setValue("password", "secret");
        form.submit();
        
        verify(this.mockSubject).login(any(UsernamePasswordToken.class));
        Assert.assertEquals(1, this.tester.getMessages(FeedbackMessage.ERROR).size());
    }

    @Test
    public void testLoginRedirectsToHomePage() throws Exception
    {
        render();
        FormTester form = render();
        form.setValue("email", "test@55minutes.com");
        form.setValue("password", "secret");
        form.submit();
        
        assertLoginWithArguments(this.mockSubject, "test@55minutes.com", "secret", false);
        this.tester.assertNoErrorMessage();
        this.tester.assertRenderedPage(this.tester.getApplication().getHomePage());
    }

    private FormTester render() throws Exception
    {
        WicketTestUtils.startComponentWithHtml(
            this.tester,
            new LoginForm("form"),
            "<form wicket:id='form'>" +
            "<input wicket:id='email' type='email'/><input wicket:id='password' type='password'/>" +
            "</form>");
        WicketTestUtils.assertValidMarkup(this.tester);
        return this.tester.newFormTester("form");
    }
    
    static void assertLoginWithArguments(Subject mockSubject,
                                         final String email,
                                         final String password,
                                         final boolean remember)
    {
        verify(mockSubject).login(argThat(new ArgumentMatcher<UsernamePasswordToken>() {
            public boolean matches(Object obj)
            {
                UsernamePasswordToken token = (UsernamePasswordToken) obj;
                return token.getUsername().equals(email) &&
                       String.valueOf(token.getPassword()).equals(password) &&
                       token.isRememberMe() == remember;
            }
        }));
    }
}
