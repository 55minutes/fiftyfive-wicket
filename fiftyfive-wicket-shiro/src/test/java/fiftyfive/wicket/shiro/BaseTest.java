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
package fiftyfive.wicket.shiro;

import fiftyfive.wicket.shiro.ShiroWicketPlugin;
import fiftyfive.wicket.shiro.test.AbstractShiroJUnit4Tests;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.DummyHomePage;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import static org.mockito.Mockito.*;

public abstract class BaseTest extends AbstractShiroJUnit4Tests
{
    public static final String MOCK_PRINCIPAL = "Mr. Mock User";
    
    protected WicketTester tester;
    
    @Before
    public void createTester()
    {
        this.tester = new WicketTester(new WebApplication() {
            @Override
            public Class getHomePage()
            {
                return DummyHomePage.class;
            }
            @Override
            protected void init()
            {
                super.init();
                getMarkupSettings().setStripWicketTags(true);
                new ShiroWicketPlugin().install(this);
            }
        });
    }
    
    @After
    public void destroyTester()
    {
        if(this.tester != null)
        {
            this.tester.destroy();
        }
    }
    
    protected void mockAuthenticated()
    {
        when(this.mockSubject.isAuthenticated()).thenReturn(true);
        when(this.mockSubject.getPrincipal()).thenReturn(MOCK_PRINCIPAL);
    }
    
    protected void mockRemembered()
    {
        when(this.mockSubject.isAuthenticated()).thenReturn(false);
        when(this.mockSubject.getPrincipal()).thenReturn(MOCK_PRINCIPAL);
    }
    
    protected void mockGuest()
    {
        when(this.mockSubject.isAuthenticated()).thenReturn(false);
    }
}
