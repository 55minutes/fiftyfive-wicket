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

/**
 * A link that sends the user to the login page. Upon successful login, the
 * user will be returned to the page that contained the login link, or
 * to a pre-determined page. This link is not visible to
 * authenticated/remembered users.
 * 
 * @author Matt Brictson
 * @since 3.0
 */
@RequiresGuest
public class LoginLink extends StatelessLink
{
    private Class<? extends Page> destination;
    
    /**
     * Construct a login link that will cause the user to remain on the
     * current page after login has succeeded.
     */
    public LoginLink(String id)
    {
        this(id, null);
    }

    /**
     * Construct a login link that will cause the user to be redirected to
     * the specified page after login has succeeded.
     */
    public LoginLink(String id, Class<? extends Page> destinationPage)
    {
        super(id);
        this.destination = destinationPage;
    }
    
    /**
     * Throws {@link RestartResponseAtInterceptPageException} to redirect
     * the user to the login page. If the user is already authenticated,
     * do nothing.
     */
    public void onClick()
    {
        // The way RestartResponseAtInterceptPageException works, after the
        // user successfully authenticates she will be redirected back to this
        // onClick() handler. This if-statement ensures that on the second
        // trip the user won't be sent to the login page again.
        if(!SecurityUtils.getSubject().isAuthenticated())
        {
            throw new RestartResponseAtInterceptPageException(getLoginPage());
        }
        if(this.destination != null)
        {
            setResponsePage(this.destination);
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
