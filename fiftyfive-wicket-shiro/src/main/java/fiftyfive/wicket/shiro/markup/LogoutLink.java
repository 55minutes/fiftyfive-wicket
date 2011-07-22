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

import fiftyfive.wicket.shiro.handler.LogoutRequestHandler;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.wicket.Page;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.UrlEncoder;

/**
 * Logs out the current user and optionally redirects to another page.
 * The current Wicket session will be invalidated. This link is only visible
 * to authenticated/remembered users.
 * <p>
 * In implementation, this isn't a typical Wicket link but is instead a manually assembled href.
 * We do this so that the logout link is truly stateless; even if the user's session has expired
 * we still want the link to work.
 * <p>
 * For example, consider a page that requires authentication.
 * The user authenticates, views the page, allows the session to expire, and then clicks "logout".
 * A normal Wicket link would require that the page be reinstantiated to deal with the click, yet
 * in this case instantiation would fail because the user is no longer authenticated (her session
 * has expired). Thus the login page would be displayed instead! Upon authenticating,
 * <em>then</em> the logout link is finally executed, logging the user out again.
 * <p>
 * To make a long story short, we must avoid the standard technique to make this link work
 * properly. We simply build a href attribute to {@code /logout}, which is handled by the
 * {@link LogoutRequestHandler}.
 * 
 * @author Matt Brictson
 * @since 3.0
 */
@RequiresUser
public class LogoutLink extends WebMarkupContainer
{
    private final Class<? extends Page> destination;
    
    /**
     * Construct a logout link that will cause the user to remain on the
     * current page after the logout operation is performed.
     * <b>Take care when using this constructor for a logout link to be placed on a page
     * that requires authentication.</b> If you do, the user experience might be awkward: the
     * logout link will log the user out, then the current page will be reloaded and the user
     * will be prompted to log in again.
     */
    public LogoutLink(String id)
    {
        this(id, null);
    }
    
    /**
     * Construct a logout link that will cause the user to be redirected to
     * the specified page after the logout operation is performed.
     */
    public LogoutLink(String id, Class<? extends Page> destination)
    {
        super(id);
        this.destination = destination;
    }
    
    /**
     * Generate a URL to the {@link LogoutRequestHandler} and emit it in the {@code href}
     * attribute. This will cause the user to be logged out when the link is clicked.
     */
    @Override
    protected void onComponentTag(final ComponentTag tag)
    {
        super.onComponentTag(tag);
        
        String to = getRequest().getClientUrl().toString();
        if(this.destination != null)
        {
            to = RequestUtils.toAbsolutePath(to, urlFor(this.destination, null).toString());
        }

        CharSequence logoutUrl = urlFor(LogoutRequestHandler.INSTANCE);
        StringBuilder href = new StringBuilder(logoutUrl);
        href.append("?to=");
        href.append(UrlEncoder.QUERY_INSTANCE.encode(to, "utf8"));
        tag.put("href", href);
    }
}
