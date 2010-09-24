/**
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

import org.apache.wicket.Resource;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.AbstractHeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketURLEncoder;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.wicketstuff.mergedresources.ResourceMount;
import org.wicketstuff.mergedresources.ResourceSpec;
import static org.apache.wicket.Application.DEVELOPMENT;

/**
 * Simplifies usage of the wicketstuff-merged-resources library by mounting
 * merged resources with an easy builder API and a good set defaults. This
 * is an abstract base class intended to be subclassed for CSS and JavaScript
 * specific uses. See
 * {@link fiftyfive.wicket.css.MergedCssBuilder MergedCssBuilder} for more
 * details and example usage.
 * <p>
 * The reasonable defaults provided by this class are as follows:
 * <ul>
 * <li>Only merge resources in deployment mode. In development, bypass the
 *     wicketstuff-merged-resources facility entirely and mount
 *     the resources separately as Wicket shared resources.</li>
 * <li>Set aggressive cache headers and gzip resources in deployment mode.</li>
 * <li>Disable the resource minify feature. This feature is buggy and
 *     does not add much value over gzip.</li>
 * </ul>
 * <p>
 * Furthermore, this class fixes some shortcomings of the
 * wicketstuff-merged-resources library, namely:
 * <ul>
 * <li>With wicketstuff-merged-resources, if Wicket's
 *     addLastModifiedTimeToResourceReferenceUrl setting is enabled, extra
 *     path elements like {@code /w:lm/123456789} are appended to the
 *     resource URLs. This makes it difficult to use relative paths within
 *     CSS resources, since the presence or absence of the timestamp changes
 *     the depth of the URL. MergedResourceBuilder ensures that the timestamp
 *     is appended as a query string, thereby leaving the path unaltered,
 *     like this: {@code ?w:lm=123456789}.</li>
 * <li>With wicketstuff-merged-resources, in non-merged mode the individual
 *     resource path is appended to the merged mount point. So you might end
 *     up with {@code /styles/all.css} in merged mode, but
 *     {@code /styles/all.css/com.mypackage.Class/layout.css} in non-merged
 *     mode. Again, this makes it very difficult to use relative paths within
 *     CSS files (e.g. for background images). MergedResourceBuilder ensures
 *     that merged and non-merged modes produce paths of the same depth. In
 *     this example the URLs would be:
 *     {@code /styles/all.css} (merged) and
 *     {@code /styles/all.css-com.mypackage.Class-layout.css} (non-merged).
 *     </li>
 * </ul>
 */
public abstract class MergedResourceBuilder
{
    private String _path;
    private List<ResourceReference> _references;
    
    protected MergedResourceBuilder()
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
     * Constructs a special merged resource using the path and resources
     * options specified in this builder, and mounts the result in the
     * application. The resources will remain separate in development mode,
     * but will be merged together into a single file in deployment mode.
     * <p>
     * This method must be called after
     * all of the options have been set.
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
        if(app.getConfigurationType().equals(DEVELOPMENT))
        {
            mountIndividualResources(app);
        }
        else
        {
            mountMergedResources(app);            
        }
        return createHeaderContributor();
    }
    
    /**
     * Add a resource to the list of merged resources.
     * 
     * @since 2.0
     */
    protected void add(ResourceReference ref)
    {
        _references.add(ref);
    }
    
    /**
     * Constructs a header contributor for the given resource.
     * Subclasses should implement the appropriate CSS or JS contributor.
     * 
     * @since 2.0
     */
    protected abstract IHeaderContributor newContributor(ResourceReference ref);
    
    /**
     * Mount each resource using WebApplication.mount(). Choose a mount point
     * for the resource that has the same path depth as the merged path.
     * This will ensure that relative paths in CSS resources resolve the same
     * way in both merged and non-merged scenarios.
     */
    private void mountIndividualResources(WebApplication app)
    {
        int i = 0;
        for(ResourceReference ref : _references)
        {
            String name = ref.getSharedResourceKey().replaceAll("/", "-");
            String uniquePath = String.format(
                "%s-%s",
                _path,
                WicketURLEncoder.PATH_INSTANCE.encode(name)
            );
            // Use query string strategy to ensure that Wicket's resource
            // last modified timestamp is appended as query string parameters
            // rather than additional path elements.
            app.mount(new QueryStringSharedResourceRequestTargetUrlCodingStrategy(
                uniquePath, ref.getSharedResourceKey()
            ));
        }
    }
    
    /**
     * Delegate to the wicketstuff-merged-resources library for mounting the
     * mereged resources with reasonable settings.
     */
    private void mountMergedResources(WebApplication app)
    {
        ResourceMount mountConfig = new CustomizedResourceMount();
        mountConfig.setDefaultAggressiveCacheDuration();

        mountConfig.setMerged(true);
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
            arr[i] = newContributor(ref);
        }
        return new HeaderContributions(arr);
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
    
    /**
     * A customized version of the wicketstuff-merged-resources ResourceMount
     * class that overrides the URL coding strategy that is used for
     * mounting resources.
     * Use a query string strategy to ensure that Wicket's resource
     * last modified timestamp is appended as query string parameters
     * rather than additional path elements.
     */
    private static class CustomizedResourceMount extends ResourceMount
    {
        // The following code is copied from ResourceMount.java with only minor
        // tweaks to replace the default coding strategies with our
        // query string versions.
        // Original code Copyright 2010 Stefan Fußenegger. Licensed under
        // the Apache Software License, Version 2.0.
        @Override
        protected IRequestTargetUrlCodingStrategy newStrategy(String mountPath, final ResourceReference ref, boolean merge) {
            if (merge) {
                final ArrayList<String> mergedKeys = new ArrayList<String>(getResourceSpecs().length);
                for (ResourceSpec spec : getResourceSpecs()) {
                    mergedKeys.add(new ResourceReference(spec.getScope(), spec.getFile()) {

                        private static final long serialVersionUID = 1L;

                        @Override
                        protected Resource newResource() {
                            Resource r = ref.getResource();
                            if (r == null) {
                                throw new WicketRuntimeException("ResourceReference wasn't bound to application yet");
                            }
                            return r;
                        }

                    }.getSharedResourceKey());
                }
                return new QueryStringMergedResourceRequestTargetUrlCodingStrategy(mountPath, ref.getSharedResourceKey(), mergedKeys);
            } else {
                return new QueryStringSharedResourceRequestTargetUrlCodingStrategy(mountPath, ref.getSharedResourceKey());
            }
        }
    }
}
