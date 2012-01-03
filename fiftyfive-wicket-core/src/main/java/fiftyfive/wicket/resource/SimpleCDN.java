/**
 * Copyright 2012 55 Minutes (http://www.55minutes.com)
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
 * by Amazon Cloudfront. This works by intercepting Wicket's default behavior for rendering
 * URLs of resource references, and then rewriting those URLs by prepending a CDN hostname
 * (or any arbitrary URL fragment). The web browser will therefore make requests to the
 * CDN host instead of the Wicket app.
 * <p>
 * Here's an example. Normally a CSS resource reference is rendered by Wicket like this:
 * <pre class="example">
 * /wicket/resource/com.mycompany.WicketApplication/test.css</pre>
 * <p>
 * With {@code SimpleCDN} installed, that resource reference URL is transformed into this:
 * <pre class="example">
 * //age39p8hg23.cloudfront.net/wicket/resource/com.mycompany.WicketApplication/test.css</pre>
 * <p>
 * <b>Please note: {@code SimpleCDN} will not rewrite resource reference URLs that
 * include query string parameters.</b> Our reasoning is that parameterized URLs usually
 * indicate that the resource is dynamic, and therefore not appropriate for serving via
 * CDN. Furthermore it should be noted that Amazon CloudFront will refuse to proxy
 * URLs that contain query string parameters (it strips the parameters off).
 * <p>
 * When configuring the CDN host, the easiest setup is a reverse-proxy. For example, with
 * Amazon CloudFront, you would specify your Wicket app as the <em>custom origin</em>, and specify
 * the CloudFront host when constructing this SimpleCDN. It's that easy.
 * <pre class="example">
 * public class MyApplication extends WebApplication
 * {
 *     &#064;Override
 *     protected void init()
 *     {
 *         super.init();
 *         // Enable CDN when in deployment mode
 *         if(usesDeploymentConfig())
 *         {
 *             new SimpleCDN("//age39p8hg23.cloudfront.net").install(this);
 *         }
 *     }
 * }</pre>
 * <p>
 * Notice in this example that we've used "//" instead of "http://" for the CDN URL.
 * This trick ensures that "http" or "https" will be automatically selected by the
 * browser based on the enclosing web page.
 * <p>
 * <em>For those familiar with Ruby on Rails, {@code SimpleCDN} is inspired by the Rails
 * {@code action_controller.asset_host} configuration setting.</em>
 *
 * @since 3.2
 */
public class SimpleCDN implements IRequestMapper
{
    private String baseUrl;
    private IRequestMapper delegate;
    private boolean delegated = false;
    
    /**
     * Construct a {@code SimpleCDN} that will rewrite resource reference URLs by prepending
     * the given {@code baseUrl}.
     *
     * @param baseUrl For example, "//age39p8hg23.cloudfront.net"
     */
    public SimpleCDN(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }
    
    /**
     * Install this {@code SimpleCDN} into the given application. The {@code SimpleCDN} instance
     * will not have any effect unless it is installed.
     */
    public void install(WebApplication app)
    {
        this.delegate = app.getRootRequestMapperAsCompound();
        app.mount(this);
    }
    
    /**
     * If the {@code requestHandler} is a {@link ResourceReferenceRequestHandler}, delegate to
     * Wicket's default mapper for creating an appropriate URL, and then prepend the CDN
     * {@code baseUrl} that was provided to the {@code SimpleCDN} constructor.
     *
     * @return a rewritten Url to the resource, or {@code null} if {@code requestHandler} is
     *         not for a resource reference
     */
    public Url mapHandler(IRequestHandler requestHandler)
    {
        // CDN doesn't apply to non-resources
        if(!(requestHandler instanceof ResourceReferenceRequestHandler)) return null;
        
        // Prevent infinite recursion in case this SimpleCDN is also contained within the delegate
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
    
    /**
     * Always return {@code null}, since {@code SimpleCDN} does not play any part in handling requests
     * (they will be handled by Wicket's default mechanism).
     */
    public IRequestHandler mapRequest(Request request)
    {
        return null;
    }
    
    /**
     * Always return {@code 0}, since {@code SimpleCDN} does not play any part in handling requests
     * (they will be handled by Wicket's default mechanism).
     */
    public int getCompatibilityScore(Request request)
    {
        return 0;
    }
}
