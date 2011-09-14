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
package fiftyfive.wicket.shiro.handler;

import fiftyfive.wicket.shiro.ShiroWicketPlugin;

import org.apache.shiro.SecurityUtils;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.StringValue;

/**
 * Handles a request by logging the current user out via Shiro, then redirecting to a path
 * specified by the {@code to} query string parameter. You should not need to use this class
 * directly: this handler is mounted by the
 * {@link fiftyfive.wicket.shiro.ShiroWicketPlugin ShiroWicketPlugin} (on {@code /logout} by
 * default), and you can link to it by using the
 * {@link fiftyfive.wicket.shiro.markup.LogoutLink LogoutLink}.
 * <p> 
 * Upon successful logout, a message will be added as a feedback message to the
 * session that says "you have been logged out". To override or localize this message,
 * define the {@link ShiroWicketPlugin#LOGGED_OUT_MESSAGE_KEY LOGGED_OUT_MESSAGE_KEY} in
 * your application.properties.
 * 
 * @author Matt Brictson
 * @since 3.0
 */
public class LogoutRequestHandler implements IRequestHandler
{
    public static final LogoutRequestHandler INSTANCE = new LogoutRequestHandler();
    
    public void respond(IRequestCycle requestCycle)
    {
        SecurityUtils.getSubject().logout();
        
        // Invalidate current session and create a new one.
        // We need a new session because otherwise our feedback message won't "stick".
        Session session = Session.get();
        session.replaceSession();
        
        // Add localized "you have been logged out" message to session
        session.info(Application.get().getResourceSettings().getLocalizer().getString(
            ShiroWicketPlugin.LOGGED_OUT_MESSAGE_KEY,
            null,
            null,
            "You have been logged out."
        ));

        StringValue to = requestCycle.getRequest().getQueryParameters().getParameterValue("to");
        
        // If "to" query string param was not specified, or was erroneously set to
        // an absolute URL (i.e. containing a ":" like "http://blah"), then fall back
        // to the home page.
        if(to.isNull() || to.toString().indexOf(":") >= 0)
        {
            RequestCycle rc = (RequestCycle) requestCycle;
            to = StringValue.valueOf(rc.urlFor(Application.get().getHomePage(), null));
        }
        ((WebResponse) requestCycle.getResponse()).sendRedirect(to.toString());
    }
    
    public void detach(IRequestCycle requestCycle)
    {
        // pass
    }
}
