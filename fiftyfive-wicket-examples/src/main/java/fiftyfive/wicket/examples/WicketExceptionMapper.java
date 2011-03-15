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

import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.authorization.AuthorizationException;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.mapper.StalePageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Custom exception handling for 55 Minutes Wicket Examples.
 */
public class WicketExceptionMapper extends DefaultExceptionMapper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        WicketExceptionMapper.class
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
     * Consider putting custom exception handling logic here. For now we
     * just log the exception and delegate to the superclass for Wicket's
     * default exception handling.
     */
    @Override
    public IRequestHandler map(Exception e)
    {
        if(! RECOVERABLE_EXCEPTIONS.contains(e.getClass()))
        {
            LoggingUtils.logException(LOGGER, e);
        }
        return super.map(e);
    }
}
