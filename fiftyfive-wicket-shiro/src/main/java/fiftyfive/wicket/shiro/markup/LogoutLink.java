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

import fiftyfive.wicket.shiro.ShiroWicketPlugin;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * A simple bookmarkable link to the {@link LogoutPage} that is hidden for users who aren't
 * logged in. Upon logout, the user will be sent to the home page or another page as specified
 * by the {@code LogoutLink} constructor.
 * 
 * @author Matt Brictson
 * @since 3.0
 */
@RequiresUser
public class LogoutLink extends BookmarkablePageLink
{
    private final Class<? extends Page> destinationPage;
    private final PageParameters destinationParams;
    
    /**
     * Construct a logout link that will cause the user to be redirected to
     * the home page after the logout operation is performed.
     */
    public LogoutLink(String id)
    {
        this(id, null, null);
    }
    
    /**
     * Construct a logout link that will cause the user to be redirected to
     * the specified page after the logout operation is performed.
     */
    public LogoutLink(String id, Class<? extends Page> destination)
    {
        this(id, destination, null);
    }
    
    /**
     * Construct a logout link that will cause the user to be redirected to
     * the specified page and parameters after the logout operation is performed.
     */
    public LogoutLink(String id,
                      Class<? extends Page> destinationPage,
                      PageParameters destinationParams)
    {
        super(id, ShiroWicketPlugin.get().getLogoutPage());
        this.destinationPage = destinationPage;
        this.destinationParams = destinationParams;
    }
    
    /**
     * If a destination page was specified in the constructor, determine the absolute URI for
     * that page and add it to this link as the {@code "to"} parameter. This instructs the
     * {@link LogoutPage} to redirect to that URI upon logout.
     */
    @Override
    protected void onInitialize()
    {
        super.onInitialize();

        Class<? extends Page> dest = this.destinationPage;
        if(dest != null)
        {
            getPageParameters().set("to", RequestUtils.toAbsolutePath(
                getRequest().getClientUrl().toString(),
                urlFor(dest, this.destinationParams).toString()));
        }
    }
}
