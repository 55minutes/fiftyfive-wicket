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

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.time.Time;


/**
 * Handles a request by delegating to {@link ResourceRequestHandler} for each of a list of
 * {@link ResourceReference} objects. In other words, merge the streams of several resources
 * into a single response.
 * <p>
 * Delegating most of the work to each individual resource makes this handler's implementation
 * quite simple, but it results in a few important limitations:
 * <ol>
 * <li><b>All resources must be the same content type.</b> For example, if you mix CSS and JS
 *     resources in a single response, the browser will obviously be very confused. A more subtle
 *     pitfall is if some resources are compressed and others are not: this would result in a
 *     single stream of clear text mixed with gzipped data, which again would confuse the browser.
 *     </li>
 * <li><b>The Content-Length header is not sent.</b> Although it is technically possible to loop
 *     through the resources twice -- once to sum up the total content length, and then again to
 *     respond with the actual content -- for simplicity and performance this implementation does
 *     not calculate the merged content length. Instead, the header is omitted from the response.
 *     </li>
 * <li><b>A failure of one resource to respond will corrupt the merged response.</b> Headers are
 *     committed and bytes are written as soon as the first resource is processed. If a later
 *     resource in the list of resources to merge fails to respond for whatever reason, this will
 *     result in an incomplete merged response.</li>
 * </ol>
 * 
 * @since 3.0
 */
public class MergedResourceRequestHandler implements IRequestHandler
{
    private List<ResourceReference> resources;
    private PageParameters pageParameters;
    private Time lastModified;
    
    public MergedResourceRequestHandler(List<ResourceReference> resources,
                                        PageParameters params,
                                        Time lastModified)
    {
        this.resources = resources;
        this.pageParameters = params;
        this.lastModified = lastModified;
    }
    
    public void respond(IRequestCycle requestCycle)
    {
        WebRequest origRequest = (WebRequest) requestCycle.getRequest();

        // Explicitly set the last modified header of the response based on the last modified
        // time of the aggregate. Do this on the original response because our wrapped response
        // ignores the last modified headers contributed by each individual resource.
        WebResponse origResponse = (WebResponse) requestCycle.getResponse();
        if(this.lastModified != null)
        {
            origResponse.setLastModifiedTime(this.lastModified.getMilliseconds());
        }
        
        try
        {
            // Make a special response object that merges the contributions of each resource,
            // but maintains a single set of headers.
            MergedResponse merged = new MergedResponse(origResponse);
            requestCycle.setResponse(merged);
            
            // Make a special request object that tweaks the If-Modified-Since header to ensure
            // we don't end up in a situation where some resources respond 200 and others 304.
            // Yes, calling RequestCycle#setRequest() is frowned upon so this is a bit of a hack.
            ((RequestCycle)requestCycle).setRequest(new MergedRequest(origRequest));
        
            for(ResourceReference ref : this.resources)
            {
                ResourceRequestHandler handler = new ResourceRequestHandler(
                    ref.getResource(),
                    this.pageParameters);
                handler.respond(requestCycle);
            
                // If first resource sent 304 Not Modified that means all will.
                // We can therefore skip the rest.
                if(304 == merged.status)
                {
                    break;
                }
            }
        }
        finally
        {
            // Restore the original request once we're done. We don't need to restore the
            // original response because Wicket takes care of that automatically.
            ((RequestCycle)requestCycle).setRequest(origRequest);
        }
    }
    
    public void detach(IRequestCycle requestCycle)
    {
        this.resources = null;
        this.pageParameters = null;
        this.lastModified = null;
    }
    
    /**
     * A WebResponse wrapper that allows data to accumulate, but only accepts the
     * headers of the first resource. Headers contributed by subsequent resources are
     * ignored. The content-length header is always ignored, because we don't know it ahead
     * of time.
     */
    private class MergedResponse extends WebResponse
    {
        private int status = 200;
        private WebResponse wrapped;
        private boolean headersOpen = true;
        
        MergedResponse(WebResponse original)
        {
            this.wrapped = original;
        }
        
        @Override
        public void close()
        {
            // ignore
        }
        
        @Override
        public void reset()
        {
            this.headersOpen = true;
            this.wrapped.reset();
        }
        
        @Override
        public void write(CharSequence sequence)
        {
            this.headersOpen = false;
            this.wrapped.write(sequence);
        }
        
        @Override
        public void write(byte[] array)
        {
            this.headersOpen = false;
            this.wrapped.write(array);
        }
        
        @Override
        public String encodeURL(CharSequence url)
        {
            return this.wrapped.encodeURL(url);
        }
        
        @Override
        public Object getContainerResponse()
        {
            return this.wrapped.getContainerResponse();
        }
        
        @Override
        public void addCookie(final Cookie cookie)
        {
            if(this.headersOpen) this.wrapped.addCookie(cookie);
        }

        @Override
        public void clearCookie(final Cookie cookie)
        {
            if(this.headersOpen) this.wrapped.clearCookie(cookie);
        }
        
        @Override
        public void setHeader(String name, String value)
        {
            if(this.headersOpen) this.wrapped.setHeader(name, value);
        }

        @Override
        public void setDateHeader(String name, long date)
        {
            if(this.headersOpen && name != null && !name.equalsIgnoreCase("Last-Modified"))
            {
                this.wrapped.setDateHeader(name, date);
            }
        }

        @Override
        public void setContentLength(final long length)
        {
            // ignore
        }

        @Override
        public void setContentType(final String mimeType)
        {
            if(this.headersOpen) this.wrapped.setContentType(mimeType);
        }
        
        @Override
        public void setAttachmentHeader(final String filename)
        {
            // ignore
        }

        @Override
        public void setInlineHeader(final String filename)
        {
            // ignore
        }

        @Override
        public void setStatus(int sc)
        {
            if(this.headersOpen)
            {
                this.status = sc;
                this.wrapped.setStatus(sc);
            }
        }

        @Override
        public void sendError(int sc, String msg)
        {
            this.wrapped.sendError(sc, msg);
        }

        @Override
        public void sendRedirect(String url)
        {
            this.wrapped.sendRedirect(url);
        }

        @Override
        public boolean isRedirect()
        {
            return this.wrapped.isRedirect();
        }

        @Override
        public void flush()
        {
            this.wrapped.flush();
        }
    }
    
    /**
     * A WebRequest wrapper than allows all method calls to pass through to the wrapped request,
     * except for getDateHeader(). We need to take special action for the If-Modified-Since header
     * to fool the individual resources into behaving as a single resource with a single
     * modification date.
     */
    private class MergedRequest extends WebRequest
    {
        private final WebRequest wrapped;
        
        MergedRequest(WebRequest original)
        {
            this.wrapped = original;
        }
        
        @Override
        public Url getUrl()
        {
            return this.wrapped.getUrl();
        }

        @Override
        public IRequestParameters getPostParameters()
        {
            return this.wrapped.getPostParameters();
        }

        @Override
        public List<Cookie> getCookies()
        {
            return this.wrapped.getCookies();
        }

        @Override
        public long getDateHeader(final String name)
        {
            long headerMillis = this.wrapped.getDateHeader(name);
            if(headerMillis >= 0 && name != null && name.equalsIgnoreCase("If-Modified-Since"))
            {
                // Truncate milliseconds since the modified since header has only second precision
                long modified = lastModified.getMilliseconds() / 1000 * 1000;
                if(headerMillis < modified)
                {
                    // Our merged data in aggregate is newer than what the browser has cached.
                    // Therefore remove the If-Modified-Since header from the request to
                    // force all resources to respond with data.
                    headerMillis = -1;
                }
                else
                {
                    // Our merged data in aggregate has not changed. Set the If-Modified-Since
                    // header to an extremely high value to force all resources to respond 304.
                    headerMillis = Long.MAX_VALUE;
                }
            }
            return headerMillis;
        }

        @Override
        public Locale getLocale()
        {
            return this.wrapped.getLocale();
        }

        @Override
        public String getHeader(final String name)
        {
            return this.wrapped.getHeader(name);
        }

        @Override
        public List<String> getHeaders(final String name)
        {
            return this.wrapped.getHeaders(name);
        }

        @Override
        public Charset getCharset()
        {
            return this.wrapped.getCharset();
        }

        @Override
        public Url getClientUrl()
        {
            return this.wrapped.getClientUrl();
        }

        @Override
        public Object getContainerRequest()
        {
            return this.wrapped.getContainerRequest();
        }
    }
}
