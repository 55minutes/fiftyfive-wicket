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

import java.util.regex.Pattern;
import fiftyfive.wicket.shiro.BaseTest;
import fiftyfive.wicket.test.WicketTestUtils;
import org.junit.Test;

public class AuthenticationStatusPanelTest extends BaseTest
{
    @Test
    public void testDisplaysPrincipalAndLogoutLinkForRemembered() throws Exception
    {
        mockRemembered();
        render();
        assertDisplaysPrincipalAndLogoutLink();
    }
    
    @Test
    public void testDisplaysPrincipalAndLogoutLinkForAuthenticated() throws Exception
    {
        mockAuthenticated();
        render();
        assertDisplaysPrincipalAndLogoutLink();
    }
    
    @Test
    public void testDisplaysCustomUsername() throws Exception
    {
        mockAuthenticated();
        render(new AuthenticationStatusPanel("panel") {
            @Override
            public String getUsername()
            {
                return "hello world!";
            }
        });
        this.tester.assertContains("hello world!");
    }
    
    @Test
    public void testDisplaysLoginLinkForGuest() throws Exception
    {
        mockGuest();
        render();
        this.tester.assertComponent("panel:login", LoginLink.class);
        this.tester.assertVisible("panel:login");
        this.tester.assertInvisible("panel:logout");
    }
    
    private void render() throws Exception
    {
        render(null);
    }

    private void render(AuthenticationStatusPanel panel) throws Exception
    {
        if(null == panel)
        {
            panel = new AuthenticationStatusPanel("panel");
        }
        WicketTestUtils.startComponentWithHtml(
            this.tester,
            panel,
            "<div wicket:id='panel'></div>");
        WicketTestUtils.assertValidMarkup(this.tester);
    }
    
    private void assertDisplaysPrincipalAndLogoutLink()
    {
        this.tester.assertComponent("panel:logout", LogoutLink.class);
        this.tester.assertInvisible("panel:login");
        this.tester.assertVisible("panel:logout");
        this.tester.assertContains(Pattern.quote(MOCK_PRINCIPAL));
    }
}
