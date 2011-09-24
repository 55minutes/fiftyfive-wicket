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
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

/**
 * This page is never rendered; it simply performs logout logic and redirects to the
 * home page or other URI as specified in the page parameters.
 * 
 * @author Matt Brictson
 * @since 3.0
 */
public class LogoutPage extends WebPage
{
    /**
     * Bookmarkable constructor. If a {@code "to"} parameter is provided, assume it is a URI
     * and redirect to that URI upon logout. Otherwise redirect to the home page.
     */
    public LogoutPage(PageParameters params)
    {
        super(params);
    }
    
    /**
     * Interrupt the rendering process and perform the logout, then redirect to a different page
     * by throwing an exception. The {@code LogoutPage} itself will therefore never be rendered.
     */
    @Override
    protected void onBeforeRender() throws RedirectToUrlException
    {
        logout();
        redirectAfterLogout();
    }
    
    /**
     * Called by {@link #onBeforeRender} to instruct Shiro to log the current user out, then
     * delegate to {@link ShiroWicketPlugin#onLoggedOut} to place a feedback message in the
     * session.
     */
    protected void logout()
    {
        // Perform Shiro logout
        SecurityUtils.getSubject().logout();
        
        // Delegate to plugin to perform any futher logout tasks
        ShiroWicketPlugin.get().onLoggedOut();
    }
    
    /**
     * Called by {@link #onBeforeRender} after {@link #logout} to redirect to another page.
     * By default this is the application home page. However if a {@code "to"} page parameter
     * was provided, assume it is a URI and redirect to that URI instead. For security reasons,
     * full URLs (i.e. something starting with {@code http}) are ignored.
     *
     * @throws RedirectToUrlException to cause Wicket to perform a 302 redirect
     */
    protected void redirectAfterLogout() throws RedirectToUrlException
    {
        StringValue to = getPageParameters().get("to");
        
        // If "to" param was not specified, or was erroneously set to
        // an absolute URL (i.e. containing a ":" like "http://blah"), then fall back
        // to the home page.
        if(null == to || to.isNull() || to.toString().indexOf(":") >= 0)
        {
            to = StringValue.valueOf(urlFor(getApplication().getHomePage(), null));
        }
        
        throw new RedirectToUrlException(to.toString(), 302);
    }
}
