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

import org.apache.shiro.SecurityUtils;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * A basic login page that contains a login form and feedback panel.
 * <p>
 * This class is intended to get you started quickly with Shiro.
 * Any non-trivial application should implement its own login page with an
 * appropriate look and feel.
 * 
 * @author Matt Brictson
 * @since 3.0
 */
public class LoginPage extends WebPage
{
    public LoginPage()
    {
        super();
        add(new LoginForm("login"));
        add(new FeedbackPanel("feedback"));
    }
    
    /**
     * If the user is already authenticated, don't bother displaying the
     * login page; instead redirect to home page.
     */
    @Override
    protected void onBeforeRender()
    {
        // Redirect to home page if user already authenticated
        if(SecurityUtils.getSubject().isAuthenticated())
        {
            // Prevent redirecting to ourself if home page == LoginPage
            Class<? extends Page> home = getApplication().getHomePage();
            if(!getClass().equals(home))
            {
                throw new RestartResponseException(home);
            }
        }
        super.onBeforeRender();
    }
}
