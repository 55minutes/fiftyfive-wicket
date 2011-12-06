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

package fiftyfive.wicket.resource;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.util.string.Strings;

/**
 * Enables a Wicket application to have its static resources proxied by a CDN, for example
 * by Amazon Cloudfront. TODO
 */
public class SimpleCDN implements IRequestMapper
{
    private String baseUrl;
    private IRequestMapper delegate;
    private boolean delegated = false;
    
    /**
     * @param baseUrl For example, "//age39p8hg23.cloudfront.net"
     */
    public SimpleCDN(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }
    
    public void install(WebApplication app)
    {
        this.delegate = app.getRootRequestMapperAsCompound();
        app.mount(this);
    }
    
    public Url mapHandler(IRequestHandler requestHandler)
    {
        // CDN doesn't apply to non-resources
        if(!(requestHandler instanceof ResourceReferenceRequestHandler)) return null;
        
        // Prevent infinite recursion in case this CDN is also contained within the delegate
        if(this.delegated) return null;
        
        Url url = null;
        try
        {
            this.delegated = true;
            url = this.delegate.mapHandler(requestHandler);
            if(url != null && url.getQueryParameters().isEmpty())
            {
                url = Url.parse(Strings.join("/", this.baseUrl, url.toString()));
            }
        }
        finally
        {
            this.delegated = false;
        }
        return url;
    }
    
    public IRequestHandler mapRequest(Request request)
    {
        return null;
    }
    
    public int getCompatibilityScore(Request request)
    {
        return 0;
    }
}
