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

package fiftyfive.wicket.experimental;

import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;


public class MergedResourceRequestHandler implements IRequestHandler
{
    private List<ResourceReference> resources;
    private PageParameters pageParameters;
    
    public MergedResourceRequestHandler(List<ResourceReference> resources, PageParameters params)
    {
        this.resources = resources;
        this.pageParameters = params;
    }
    
    // TODO: calculate total content-length?
    
    public void respond(IRequestCycle requestCycle)
    {
        requestCycle.setResponse(new MergedResponse((WebResponse) requestCycle.getResponse()));
        for(ResourceReference ref : this.resources)
        {
            ResourceRequestHandler handler = new ResourceRequestHandler(
                ref.getResource(),
                this.pageParameters);
            handler.respond(requestCycle);
        }
    }
    
    public void detach(IRequestCycle requestCycle)
    {
        this.resources = null;
        this.pageParameters = null;
    }
    
    /**
     * A WebResponse wrapper that allows data to accumulate, but only accepts the
     * headers of the first resource. Headers contributed by subsequent resources are
     * ignored. The content-length header is always ignored, because we don't know it ahead
     * of time.
     */
    private class MergedResponse extends WebResponse
    {
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
            if(this.headersOpen) this.wrapped.setDateHeader(name, date);
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
            if(this.headersOpen) this.wrapped.setStatus(sc);
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
}
