/*
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
package fiftyfive.wicket.resource;


import java.util.ArrayList;
import java.util.List;

import static org.apache.wicket.Application.DEVELOPMENT;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.behavior.AbstractHeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import org.wicketstuff.mergedresources.ResourceMount;


/**
 * Simplifies usage of the wicket-merged-resource library by mounting merged
 * resources with an easy builder API and a good set defaults.
 * <p>
 * More importantly, this builder applies the DRY principle so that you only
 * have to list your resources once; then you can attach this list of
 * resources to any number of pages or components without redeclaring
 * HeaderContributors for them by hand.
 * See {@link #build} for details.
 * <p>
 * The reasonable defaults provided by this builder are as follows:
 * <ul>
 * <li>Only merge resources in deployment mode. In development, mount
 *     the resources separately to facilitate debugging.</li>
 * <li>Set aggressive cache headers in deployment mode. In development mode
 *     don't send cache headers at all.</li>
 * <li>Always gzip resources.</li>
 * <li>Always disable the resource minify feature. This feature is buggy and
 *     does not add much value over gzip.</li>
 * </ul>
 * Example usage:
 * <pre>
 * new MergedResourceBuilder()
 *     .setPath("/styles/all.css")
 *     .addCss(WicketApplication.class, "styles/reset.css")
 *     .addCss(WicketApplication.class, "styles/core.css")
 *     .addCss(WicketApplication.class, "styles/layout.css")
 *     .addCss(WicketApplication.class, "styles/content.css")
 *     .addCss(WicketApplication.class, "styles/forms.css")
 *     .addCss(WicketApplication.class, "styles/page-specific.css")
 *     .build(this);
 * </pre>
 */
public class MergedResourceBuilder
{
    private Boolean _isCss;
    private String _media;
    
    private String _path;
    private List<ResourceReference> _references;
    
    /**
     * Creates an empty builder object. See the
     * {@link MergedResourceBuilder class documentation} for 
     * example usage.
     */
    public MergedResourceBuilder()
    {
        _references = new ArrayList<ResourceReference>();
    }
    
    /**
     * Sets the path at which the merged resources will be mounted.
     * For example, "/styles/all.css".
     */
    public MergedResourceBuilder setPath(String path)
    {
        _path = path;
        return this;
    }
    
    /**
     * Sets the CSS media type that will be used for the merged CSS resources.
     * By default the merged CSS will not have a media type, meaning it will
     * apply to all media. Alternatively you could specify "screen" or "print"
     * to limit the CSS to those media.
     */
    public MergedResourceBuilder setCssMedia(String media)
    {
        requireCss();
        _media = media;
        return this;
    }
    
    /**
     * Adds a CSS resource to the list of merged resources.
     *
     * @param scope The class relative to which the resource will be resolved.
     * @param path  The path, relative to the scope, where the CSS file is
     *              is located.
     * @throws IllegalStateException if a JavaScript resource was previously
     *         added to this builder. CSS and JavaScript resources cannot be
     *         mixed.
     */
    public MergedResourceBuilder addCss(Class<?> scope, String path)
    {
        return addCss(new ResourceReference(scope, path));
    }

    /**
     * Adds a CSS resource to the list of merged resources.
     *
     * @param ref The CSS resource to add.
     * @throws IllegalStateException if a JavaScript resource was previously
     *         added to this builder. CSS and JavaScript resources cannot be
     *         mixed.
     */
    public MergedResourceBuilder addCss(ResourceReference ref)
    {
        requireCss();
        _references.add(ref);
        return this;
    }
    
    /**
     * Adds a JavaScript resource to the list of merged resources.
     *
     * @param scope The class relative to which the resource will be resolved.
     * @param path  The path, relative to the scope, where the JS file is
     *              is located.
     * @throws IllegalStateException if a CSS resource was previously
     *         added to this builder. CSS and JavaScript resources cannot be
     *         mixed.
     */
    public MergedResourceBuilder addScript(Class<?> scope, String path)
    {
        return addScript(new ResourceReference(scope, path));
    }
    
    /**
     * Adds a JavaScript resource to the list of merged resources.
     *
     * @param ref The JavaScript resource to add.
     * @throws IllegalStateException if a CSS resource was previously
     *         added to this builder. CSS and JavaScript resources cannot be
     *         mixed.
     */
    public MergedResourceBuilder addScript(ResourceReference ref)
    {
        requireScript();
        _references.add(ref);
        return this;
    }

    /**
     * Constructs a special merged resource using the path and resources
     * options specified in this builder, and mounts the result in the
     * application. The resources will remain separate in development mode,
     * but will be merged together into a single file in deployment mode.
     * <p>
     * This method must be called after
     * all of the options have been set. See
     * {@link MergedResourceBuilder class documentation} for example usage.
     * <p>
     * If desired, the HeaderContributor returned by this method can be used
     * to contribute the merged resource to the pages or components of your
     * choice.
     *
     * @return A HeaderContributor that will contribute all the JavaScript
     *         or CSS resources that were specified in this builder.
     *
     * @throws IllegalStateException if a path or resources have not been
     *         specified prior to calling this method.
     */
    public AbstractHeaderContributor build(WebApplication app)
    {
        assertRequiredOptions();
        mountResources(app);
        return createHeaderContributor();
    }
    
    /**
     * Delegate to the wicket-merged-resources library for mounting the
     * mereged resources with reasonable settings.
     */
    private void mountResources(WebApplication app)
    {
        boolean development = app.getConfigurationType().equals(DEVELOPMENT);
        
        // Configure resources to be merged only in production.
        ResourceMount mountConfig = new ResourceMount();
        if(development)
        {
            // Discourage caching in development
            mountConfig.setCacheDuration(0);
        }
        else
        {
            mountConfig.setDefaultAggressiveCacheDuration();
        }
        mountConfig.setMerged(!development);
        mountConfig.setCompressed(true);
        mountConfig.setMinifyCss(false);
        mountConfig.setMinifyJs(false);

        // Apply requested configuration
        mountConfig.setPath(_path);
        for(ResourceReference ref : _references)
        {
            mountConfig.addResourceSpec(ref);
        }
        mountConfig.mount(app);
    }
    
    private AbstractHeaderContributor createHeaderContributor()
    {
        IHeaderContributor[] arr = new IHeaderContributor[_references.size()];
        for(int i=0; i<_references.size(); i++)
        {
            ResourceReference ref = _references.get(i);
            if(_isCss)
            {
                arr[i] = CSSPackageResource.getHeaderContribution(ref, _media);
            }
            else
            {
                arr[i] = JavascriptPackageResource.getHeaderContribution(ref);
            }
        }
        return new HeaderContributions(arr);
    }
    
    private void requireCss()
    {
        if(_isCss != null && !_isCss)
        {
            throw new IllegalStateException(
                "CSS resources and options cannot be added to a merged " +
                "JavaScript resource"
            );
        }
        _isCss = true;
    }

    private void requireScript()
    {
        if(_isCss != null && _isCss)
        {
            throw new IllegalStateException(
                "JavaScript resources cannot be added to a merged " +
                "CSS resource"
            );
        }
        _isCss = false;
    }

    private void assertRequiredOptions()
    {
        if(null == _path)
        {
            throw new IllegalStateException("path must be set");
        }
        if(_references.size() == 0)
        {
            throw new IllegalStateException(
                "at least one resource must be added"
            );
        }
    }
    
    private static class HeaderContributions extends AbstractHeaderContributor
    {
        IHeaderContributor[] _contribs;
        
        private HeaderContributions(IHeaderContributor[] contribs)
        {
            _contribs = contribs;
        }
        
        public IHeaderContributor[] getHeaderContributors()
        {
            return _contribs;
        }
    }
}
