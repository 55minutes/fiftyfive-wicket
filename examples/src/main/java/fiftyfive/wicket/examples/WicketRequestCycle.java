/**
 * Copyright 2010 55 Minutes (http://www.55minutes.com)
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

import fiftyfive.wicket.util.LoggingUtils;
import org.apache.wicket.Page;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom request cycle implementation for 55 Minutes Wicket Examples.
 * This is where exception handling and logging logic is defined.
 */
public class WicketRequestCycle extends WebRequestCycle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        WicketRequestCycle.class
    );
    
    public WicketRequestCycle(WebApplication application,
                              WebRequest request,
                              Response response)
    {
        super(application, request, response);
    }
    
    /**
     * Delegate to {@link LoggingUtils} to log detailed troubleshooting
     * information.
     */
    @Override
    protected void logRuntimeException(RuntimeException e)
    {
        LoggingUtils.logRuntimeException(LOGGER, e);
    }
    
    // Consider implementing this override to customize Wicket's exception
    // handling. For example, return a different error page based on the
    // exception type.
    /*
    @Override
    public Page onRuntimeException(Page page, RuntimeException e)
    {
    }
    */
}
