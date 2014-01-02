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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;

import org.apache.wicket.behavior.Behavior;

import org.apache.wicket.markup.html.IHeaderResponse;

import org.apache.wicket.protocol.http.WebApplication;

import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;

import org.apache.wicket.util.IProvider;


/**
 * Provides a simple builder API for constructing and mounting a virtual resource that merges
 * together several actual resources. This is a common web optimization technique for reducing
 * browser requests: instead of the browser making several small requests to download various
 * CSS or JavaScript files, the browser can instead make one large request. Since each request
 * brings its own latency and HTTP overhead, merging resources together can therefore lead to a
 * noticable performance improvement.
 * <p>
 * The actual merging is done by {@link MergedResourceMapper} and its helper class,
 * {@link MergedResourceRequestHandler}. This {@code MergedJavaScriptBuilder} class simply provides
 * a builder API that makes constructing and mounting the mapper more self-explanatory. Advanced
 * users may wish to use the mapper directly.
 * <p>
 * Note that this class is abstract. Refer to the concrete subclasses
 * {@link fiftyfive.wicket.js.MergedJavaScriptBuilder MergedJavaScriptBuilder} and
 * {@link fiftyfive.wicket.css.MergedCssBuilder MergedCssBuilder} for example usage.
 * <p>
 * <em>This class was rewritten in fiftyfive-wicket 3.0 to remove all dependencies on
 * third-party libraries. The wicketstuff-merged-resources project is no longer used.
 * Unlike previous versions that merged resources only in deployment mode, this implementation
 * always merges resources, both in deployment and development modes.</em>
 */
public abstract class MergedResourceBuilder
{
    private String path;
    private boolean frozen = false;
    private List<ResourceReference> references;
    
    public MergedResourceBuilder()
    {
        this.references = new ArrayList<ResourceReference>();
    }
    
    /**
     * Sets the path at which the merged resources will be mounted.
     * For example, "styles/all.css".
     * 
     * @return {@code this} for chaining
     */
    public MergedResourceBuilder setPath(String path)
    {
        this.path = path;
        return this;
    }
    
    /**
     * @deprecated Please use {@link #install install()} instead.
     */
    public Behavior build(WebApplication app)
    {
        install(app);
        return buildHeaderContributor();
    }
    
    /**
     * Constructs a special merged resource using the path and resources options specified in this
     * builder, and mounts the result in the application by calling
     * {@link WebApplication#mount(IRequestMapper) WebApplication.mount()}.
     * <p>
     * This method may only be called after all of the options have been set.
     *
     * @return {@code this} for chaining
     *
     * @throws IllegalStateException if a path or resources have not been
     *         specified prior to calling this method.
     * 
     * @since 3.0
     */
    public MergedResourceBuilder install(WebApplication app)
    {
        app.mount(buildRequestMapper(app));
        return this;
    }
    
    /**
     * Constructs and returns a special merged resource request mapper using the path and resources
     * options specified in this builder.
     * <p>
     * This method may only be called after all of the options have been set.
     * <p>
     * Use this method if your application has a complex configuration that requires you to deal
     * with request mappers directly (e.g. you need to wrap or combine them in clever ways).
     * Most applications will be better served by {@link #install install()}, which
     * handles creating the mapper and mounting it in one easy step.
     * 
     * @throws IllegalStateException if a path or resources have not been
     *         specified prior to calling this method.
     *
     * @since 3.0
     */
    public IRequestMapper buildRequestMapper(final WebApplication app)
    {
        if(!this.frozen) assertRequiredOptionsAndFreeze();

        return new MergedResourceMapper(
            this.path,
            this.references,
            new PageParametersEncoder(),
            new IProvider<IResourceCachingStrategy>()
            {
                public IResourceCachingStrategy get()
                {
                    return app.getResourceSettings().getCachingStrategy();
                }
            });
    }
    
    /**
     * Constructs and returns a {@link Behavior} that will contribute all resources of this
     * builder to the {@code <head>}. This could be useful on your base page, for example, to
     * ensure that all pages of your app have a common set of resources.
     * 
     * @since 3.0
     */
    public Behavior buildHeaderContributor()
    {
        if(!this.frozen) assertRequiredOptionsAndFreeze();
        return new Behavior() {
            @Override
            public void renderHead(Component comp, IHeaderResponse response)
            {
                for(int i=0; i<MergedResourceBuilder.this.references.size(); i++)
                {
                    ResourceReference ref = MergedResourceBuilder.this.references.get(i);
                    newContributor(ref).renderHead(comp, response);
                }
            }
        };
    }
    
    /**
     * Add a resource to the list of merged resources.
     * 
     * @since 2.0
     */
    protected void add(ResourceReference ref)
    {
        if(this.frozen)
        {
            throw new IllegalStateException(
                "Resources cannot be added once build() or install() methods have been called.");
        }
        this.references.add(ref);
    }
    
    /**
     * Constructs a header contributor for the given resource.
     * Subclasses should implement the appropriate CSS or JS contributor.
     * 
     * @since 2.0
     */
    protected abstract Behavior newContributor(ResourceReference ref);
    
    /**
     * Called when one of the build or install methods is invoked to verify that all required
     * properties have been provided. After this method is called the builder will be considered
     * "frozen"; that is, no more resources may be added.
     * 
     * @throws IllegalStateException if the path has not been set or if no resources have been
     *                               added to the builder
     */
    protected void assertRequiredOptionsAndFreeze()
    {
        if(null == this.path)
        {
            throw new IllegalStateException("path must be set");
        }
        if(this.references.size() == 0)
        {
            throw new IllegalStateException(
                "at least one resource must be added"
            );
        }
        this.frozen = true;
    }
}
