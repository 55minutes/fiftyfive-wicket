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
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Mockito.*;


public class RememberMeLoginFormTest extends BaseTest
{
    @Test
    public void testRender() throws Exception
    {
        render();
    }

    @Test
    public void testLoginWithRememberMeUnchecked() throws Exception
    {
        render();
        FormTester form = render();
        form.setValue("email", "test@55minutes.com");
        form.setValue("password", "secret");
        form.setValue("rememberme", false);
        form.submit();
        
        LoginFormTest.assertLoginWithArguments(
            this.mockSubject, "test@55minutes.com", "secret", false);
    }

    @Test
    public void testLoginWithRememberMeChecked() throws Exception
    {
        render();
        FormTester form = render();
        form.setValue("email", "test@55minutes.com");
        form.setValue("password", "secret");
        form.setValue("rememberme", true);
        form.submit();
        
        LoginFormTest.assertLoginWithArguments(
            this.mockSubject, "test@55minutes.com", "secret", true);
    }

    private FormTester render() throws Exception
    {
        WicketTestUtils.startComponentWithHtml(
            this.tester,
            new RememberMeLoginForm("form"),
            "<form wicket:id='form'>" +
            "<input wicket:id='email' type='email'/><input wicket:id='password' type='password'/>" +
            "<input wicket:id='rememberme' type='checkbox'/>" +
            "</form>");
        WicketTestUtils.assertValidMarkup(this.tester);
        return this.tester.newFormTester("form");
    }
}
