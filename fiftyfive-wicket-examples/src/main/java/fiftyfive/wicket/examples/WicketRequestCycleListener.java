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

package fiftyfive.wicket.examples;

import java.util.Arrays;
import java.util.List;

import fiftyfive.wicket.util.LoggingUtils;

import org.apache.wicket.Page;

import org.apache.wicket.authorization.AuthorizationException;

import org.apache.wicket.protocol.http.PageExpiredException;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.PageProvider;
import org.apache.wicket.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.handler.RenderPageRequestHandler.RedirectPolicy;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.StalePageException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Customization of Wicket's request cycle processing. The superclass provides a template
 * for providing callbacks that will be invoked during Wicket's request-response flow.
 * Consider this as a place to put your application's web "middleware". The current implementation
 * simply does more detailed logging when an exception occurs.
 */
public class WicketRequestCycleListener extends AbstractRequestCycleListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        WicketRequestCycleListener.class
    );
    
    /**
     * Exception types we consider "recoverable", meaning we don't have to
     * log a detailed stack trace for these.
     */
    private List RECOVERABLE_EXCEPTIONS = Arrays.asList(
        StalePageException.class,
        PageExpiredException.class,
        AuthorizationException.class
    );
    
    /**
     * Consider putting custom exception handling logic here.
     * For example, you could catch an {@code ObjectNotFoundException} here and redirect
     * to a 404 page. For now we just log.
     */
    @Override
    public IRequestHandler onException(RequestCycle cycle, Exception ex)
    {
        // Example: show 404 page if ObjectNotFoundException is thrown
        // if(ex instanceof ObjectNotFoundException)
        // {
        //     return createErrorPageHandler(cycle, NonFoundErrorPage.class);
        // }
        if(! RECOVERABLE_EXCEPTIONS.contains(ex.getClass()))
        {
            LoggingUtils.logException(LOGGER, ex);
        }
        // null means we want Wicket's default onException behavior to be used
        return null;
    }

    /**
     * Create a {@link IRequestHandler} for displaying the given error page without performing
     * a redirect. However, if the request is an ajax one, force a redirect, as that is the
     * only way to make the browser show the error page.
     */
    private IRequestHandler createErrorPageHandler(RequestCycle cycle, Class<? extends Page> page)
    {
        RedirectPolicy redirectPolicy = RedirectPolicy.NEVER_REDIRECT;
        
        if(cycle.getRequest() instanceof WebRequest && ((WebRequest) cycle.getRequest()).isAjax())
        {
            redirectPolicy = RedirectPolicy.ALWAYS_REDIRECT;
        }
        return new RenderPageRequestHandler(new PageProvider(page), redirectPolicy);
    }
}
