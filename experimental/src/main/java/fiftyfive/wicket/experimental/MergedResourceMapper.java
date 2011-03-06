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

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.AbstractMapper;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.request.resource.ResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MergedResourceMapper extends AbstractMapper implements IRequestMapper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MergedResourceMapper.class);
    
    private final String[] mountSegments;
    private final List<ResourceReference> resources;
    private final IPageParametersEncoder parametersEncoder;
    
    public MergedResourceMapper(String path, List<ResourceReference> resources)
    {
        // TODO: validate arguments
        // TODO: constructor that takes explicit IPageParametersEncoder
        this.resources = resources;
        this.mountSegments = getMountSegments(path);
        this.parametersEncoder = new PageParametersEncoder();
    }
    
    public int getCompatibilityScore(Request request)
    {
        return 0;
    }
    
    public IRequestHandler mapRequest(Request request)
    {
        if(!urlStartsWith(request.getUrl(), this.mountSegments)) return null;
        
        // TODO: Make sure to override lastUpdatedTime() based on the merged resources, rather than
        //       the timestamp of the temp file.
        PageParameters parameters = extractPageParameters(
            request,
            this.mountSegments.length,
            this.parametersEncoder);
            
        return new MergedResourceRequestHandler(this.resources, parameters);
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
            if(i-1 == this.mountSegments.length)
            {
                segment = applyCachingStrategy(segment, parameters);
            }
            url.getSegments().add(segment);
        }
        
        return encodePageParameters(url, parameters, this.parametersEncoder);
    }
    
    protected String applyCachingStrategy(String fileName, PageParameters parameters)
    {
        // TODO: loop through all resources, determine which has been modified most recently,
        //       and pass that to caching strategy using technique found in
        //       BasicResourceReferenceMapper.java
        return fileName;
    }
}
