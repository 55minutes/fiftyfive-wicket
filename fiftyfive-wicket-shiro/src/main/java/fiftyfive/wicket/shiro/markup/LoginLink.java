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

import fiftyfive.wicket.shiro.ShiroWicketPlugin;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * A link that sends the user to the login page. Upon successful login, the
 * user will be returned to the home page, or to a pre-determined page specified via the
 * constructor. This link is not visible to authenticated/remembered users.
 * 
 * @author Matt Brictson
 * @since 3.0
 */
@RequiresGuest
public class LoginLink extends StatelessLink
{
    private Class<? extends Page> destinationPage;
    private PageParameters destinationParams;
    
    /**
     * Construct a login link that will cause the user to be redirect to the home page
     * after login has succeeded.
     */
    public LoginLink(String id)
    {
        this(id, null, null);
    }

    /**
     * Construct a login link that will cause the user to be redirected to
     * the specified page after login has succeeded.
     */
    public LoginLink(String id, Class<? extends Page> destinationPage)
    {
        this(id, destinationPage, null);
    }
    
    /**
     * Construct a login link that will cause the user to be redirected to
     * the specified page+parameters after login has succeeded.
     */
    public LoginLink(String id,
                     Class<? extends Page> destinationPage,
                     PageParameters destinationParams)
    {
        super(id);
        this.destinationPage = destinationPage;
        this.destinationParams = destinationParams;
    }
    
    /**
     * If a specific destination page was specified in the constructor, throw
     * {@link RestartResponseAtInterceptPageException} to redirect to the login page, after
     * which the flow will continue to that destination. Otherwise simply navigate to the login
     * and follow the standard login-to-home page flow.
     */
    public void onClick()
    {
        // The way RestartResponseAtInterceptPageException works, after the
        // user successfully authenticates she will be redirected back to this
        // onClick() handler to continue the flow. Test whether we are in that
        // continuation be looking at the authenticated status.
        if(!SecurityUtils.getSubject().isAuthenticated())
        {
            // User hasn't logged in yet. Send to login page.
            if(this.destinationPage != null)
            {
                throw new RestartResponseAtInterceptPageException(getLoginPage());
            }
            else
            {
                setResponsePage(getLoginPage());
            }
        }
        else
        {
            // User has completed login. Send to destination or home page.
            if(this.destinationPage != null)
            {
                setResponsePage(this.destinationPage, this.destinationParams);
            }
            else
            {
                setResponsePage(getApplication().getHomePage());
            }
        }
    }
    
    /**
     * Returns {@code true} if {@code Page} is the login page.
     */
    @Override
    protected boolean linksTo(Page page)
    {
        return page.getClass().equals(getLoginPage());
    }

    /**
     * Returns the login page that is configured for this application by
     * asking the {@link ShiroWicketPlugin}.
     */
    protected Class<? extends Page> getLoginPage()
    {
        return ShiroWicketPlugin.get().getLoginPage();
    }
}
