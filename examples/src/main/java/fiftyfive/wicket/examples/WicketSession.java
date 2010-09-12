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

import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.RequestLogger.ISessionLogInfo;
import org.apache.wicket.protocol.http.WebSession;

/**
 * Session information for 55 Minutes Wicket Examples.
 * Any variables added to this class will automatically be persisted in
 * the Servlet HttpSession. Each browser session gets its own instance of
 * this class.
 */
public class WicketSession extends WebSession implements ISessionLogInfo
{
    public WicketSession(Request request)
    {
        super(request);
    }
    
    /**
     * Additional information about this session that will automatically
     * be included in Wicket's request log, as well in troubleshooting
     * information emitted by
     * {@link fiftyfive.wicket.util.LoggingUtils LoggingUtils}. 
     * Consider including things like username, if authenticated.
     */
    public Object getSessionInfo()
    {
        return "TODO: Your session info goes here";
    }
}
