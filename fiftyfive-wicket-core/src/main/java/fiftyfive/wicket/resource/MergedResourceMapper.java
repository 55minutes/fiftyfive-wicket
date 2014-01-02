/**
 * Copyright 2014 55 Minutes (http://www.55minutes.com)
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

import java.util.List;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;

import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;

import org.apache.wicket.request.mapper.AbstractMapper;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.IStaticCacheableResource;
import org.apache.wicket.request.resource.caching.ResourceUrl;

import org.apache.wicket.util.IProvider;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Time;


/**
 * Maps a single, static URL to a list of resources. When that URL is requested, respond by
 * merging all the resources together by delegating to {@link MergedResourceRequestHandler}.
 * 
 * @since 3.0
 */
public class MergedResourceMapper extends AbstractMapper implements IRequestMapper
{
    protected final String[] mountSegments;
    protected final List<ResourceReference> resources;
    protected final IPageParametersEncoder parametersEncoder;
    protected final IProvider<? extends IResourceCachingStrategy> cachingStrategy;
    
    public MergedResourceMapper(String path,
                                List<ResourceReference> resources,
                                IPageParametersEncoder parametersEncoder,
                                IProvider<? extends IResourceCachingStrategy> cachingStrategy)
    {
        // TODO: validate arguments
        this.resources = resources;
        this.mountSegments = getMountSegments(path);
        this.parametersEncoder = parametersEncoder;
        this.cachingStrategy = cachingStrategy;
    }
    
    public int getCompatibilityScore(Request request)
    {
        return 0;
    }
    
    public IRequestHandler mapRequest(Request request)
    {
        PageParameters parameters = null;
        List<String> requestSegments = request.getUrl().getSegments();
        
        if(requestSegments.size() < this.mountSegments.length)
        {
            return null;
        }
        for(int i=0; i<this.mountSegments.length; i++)
        {
            String segment = requestSegments.get(i);
            if(i+1 == this.mountSegments.length)
            {
                parameters = extractPageParameters(
                    request,
                    this.mountSegments.length,
                    this.parametersEncoder);
                
                ResourceUrl resourceUrl = new ResourceUrl(segment, parameters);
                this.cachingStrategy.get().undecorateUrl(resourceUrl);
                segment = resourceUrl.getFileName();
            }
            if(!segment.equals(this.mountSegments[i]))
            {
                return null;
            }
        }
        
        return new MergedResourceRequestHandler(
            this.resources,
            parameters,
            getLastModifiedTime(getLastModifiedReference()));
    }

    public Url mapHandler(IRequestHandler requestHandler)
    {
        if(!(requestHandler instanceof ResourceReferenceRequestHandler)) return null;

        boolean matched = false;
        ResourceReferenceRequestHandler handler = (ResourceReferenceRequestHandler) requestHandler;
        for(ResourceReference ref : this.resources)
        {
            if(ref.equals(handler.getResourceReference()))
            {
                matched = true;
                break;
            }
        }
        
        if(!matched) return null;

        Url url = new Url();
        PageParameters parameters = new PageParameters(handler.getPageParameters());
        for(int i=0; i<this.mountSegments.length; i++)
        {
            String segment = mountSegments[i];
            if(i+1 == this.mountSegments.length)
            {
                segment = applyCachingStrategy(segment, parameters);
            }
            url.getSegments().add(segment);
        }
        
        return encodePageParameters(url, parameters, this.parametersEncoder);
    }
    
    protected String applyCachingStrategy(String fileName, PageParameters parameters)
    {
        ResourceUrl resourceUrl = new ResourceUrl(fileName, parameters);
        ResourceReference lastMod = getLastModifiedReference();
        IResource res = lastMod.getResource();
        if(res instanceof IStaticCacheableResource)
        {
            this.cachingStrategy.get().decorateUrl(resourceUrl, (IStaticCacheableResource) res);
        }
        return resourceUrl.getFileName();
    }
    
    protected ResourceReference getLastModifiedReference()
    {
        ResourceReference lastModifiedRef = null;
        long lastMillis = -1;
        for(ResourceReference ref : this.resources)
        {
            Time refModified = getLastModifiedTime(ref);
            if(refModified != null && refModified.getMilliseconds() > lastMillis)
            {
                lastMillis = refModified.getMilliseconds();
                lastModifiedRef = ref;
            }
        }
        return lastModifiedRef != null ? lastModifiedRef : this.resources.get(0);
    }
    
    /**
     * De-reference the resource and open its stream to determine the last modified time.
     */
    protected Time getLastModifiedTime(ResourceReference ref)
    {
        Time modified = null;
        IResource res = ref.getResource();
        if(res instanceof IStaticCacheableResource)
        {
            IResourceStream stream = ((IStaticCacheableResource) res).getCacheableResourceStream();
            modified = stream.lastModifiedTime();
        }
        return modified;
    }
}
