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
package fiftyfive.wicket.shiro;

import fiftyfive.wicket.shiro.markup.LoginPage;
import fiftyfive.wicket.test.WicketTestUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.wicket.Page;
import org.apache.wicket.feedback.FeedbackMessage;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ShiroWicketPluginTest extends BaseTest
{
    @Test
    public void testUnauthenticatedRedirectsToLogin_annotated() throws Exception
    {
        mockGuest();
        this.tester.startPage(AuthenticationRequiredPage.class);
        assertRedirectsToLogin();
    }
    
    @Test
    public void testUnauthenticatedRedirectsToLogin_exception() throws Exception
    {
        mockGuest();
        this.tester.startPage(new ExceptionalPage(new UnauthenticatedException()));
        assertRedirectsToLogin();
    }
    
    @Test
    public void testUnauthorizedRedirectsToHome_exception() throws Exception
    {
        mockAuthenticated();
        this.tester.startPage(new ExceptionalPage(new UnauthorizedException()));
        assertRedirectsToHome();
    }
    
    @Test
    public void testAuthorizedRenders_annotatedChild() throws Exception
    {
        mockAuthenticated();
        assertRenders(AnnotatedUnauthorizedChildPage.class);
    }

    @Test
    public void testUnauthorizedRedirectsToHome_annotatedChild() throws Exception
    {
        mockAuthenticated();
        doThrow(new UnauthorizedException())
            .when(this.mockSubject).checkRole("test-role");
        
        this.tester.startPage(AnnotatedUnauthorizedChildPage.class);
        assertRedirectsToHome();
    }
    
    @Test
    public void testAuthorizedRenders_annotated() throws Exception
    {
        mockAuthenticated();
        assertRenders(AnnotatedUnauthorizedPage.class);
    }

    @Test
    public void testUnauthorizedRedirectsToHome_annotated() throws Exception
    {
        mockAuthenticated();
        doThrow(new UnauthorizedException())
            .when(this.mockSubject).checkRole("test-role");
        
        this.tester.startPage(AnnotatedUnauthorizedPage.class);
        assertRedirectsToHome();
    }
    
    private void assertRenders(Class<? extends Page> page) throws Exception
    {
        this.tester.startPage(page);
        this.tester.assertRenderedPage(page);
        this.tester.assertNoErrorMessage();
    }
    
    private void assertRedirectsToHome() throws Exception
    {
        this.tester.assertRenderedPage(this.tester.getApplication().getHomePage());
        Assert.assertEquals(1, this.tester.getMessages(FeedbackMessage.ERROR).size());
    }

    private void assertRedirectsToLogin() throws Exception
    {
        this.tester.assertRenderedPage(LoginPage.class);
        Assert.assertEquals(1, this.tester.getMessages(FeedbackMessage.INFO).size());
    }
}
