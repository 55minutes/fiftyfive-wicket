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
import org.apache.wicket.util.string.StringValue;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;


public class LogoutLinkTest extends BaseTest
{
    @Test
    public void testLinksToLogoutPage() throws Exception
    {
        mockAuthenticated();
        LogoutLink link = render();
        
        Assert.assertEquals(LogoutPage.class, link.getPageClass());
        Assert.assertTrue(link.getPageParameters().isEmpty());
    }
    
    @Test
    public void testHiddenIfNotAuthenticated() throws Exception
    {
        mockGuest();
        render();
        this.tester.assertInvisible("link");
    }
    
    @Test
    public void testVisibleIfAuthenticated() throws Exception
    {
        mockAuthenticated();
        render();
        this.tester.assertVisible("link");
    }

    @Test
    public void testVisibleIfRemembered() throws Exception
    {
        mockRemembered();
        render();
        this.tester.assertVisible("link");
    }
    
    private LogoutLink render() throws Exception
    {
        LogoutLink link = new LogoutLink("link");
        WicketTestUtils.startComponentWithHtml(
            this.tester,
            link,
            "<a wicket:id='link'>Log out</a>");
        WicketTestUtils.assertValidMarkup(this.tester);
        return link;
    }
}
