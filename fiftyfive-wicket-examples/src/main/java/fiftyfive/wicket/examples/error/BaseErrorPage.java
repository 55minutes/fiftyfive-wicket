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
package fiftyfive.wicket.examples.error;

import fiftyfive.wicket.examples.BasePage;
import org.apache.wicket.request.http.WebResponse;

/**
 * Base class for custom error pages.
 */
public abstract class BaseErrorPage extends BasePage
{
    /**
     * Error pages are not bookmarkable, hence no PageParameters.
     */
    protected BaseErrorPage()
    {
        super(null);
    }
    
    /**
     * Subclasses must implement to provide the HTTP status error code.
     * For example, 404 for the {@link NotFoundErrorPage}.
     */
    protected abstract int getErrorCode();
    
    /**
     * Make sure we emit the proper HTTP status.
     */
    @Override
    protected void configureResponse(final WebResponse response)
    {
        super.configureResponse(response);
        response.setStatus(getErrorCode());
    }
    
    /**
     * Returns {@code true}.
     */
    @Override
    public boolean isErrorPage()
    {
        return true;
    }
}
