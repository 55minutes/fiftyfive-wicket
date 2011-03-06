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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.resource.ResourceReference;
import org.wicketstuff.mergedresources.ResourceMount;

/**
 * <b>This class is not yet compatible with Wicket 1.5.</b>
 * <p>
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
        throw new UnsupportedOperationException(
            "MergedResourceBuilder is not yet compatible with Wicket 1.5.");
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
    public Behavior build(WebApplication app)
    {
        assertRequiredOptions();
        mountIndividualResources(app);
        // TODO: uncomment onse mountMergedResources() is working again
        /*
        if(app.usesDevelopmentConfig())
        {
            mountIndividualResources(app);
        }
        else
        {
            mountMergedResources(app);            
        }
        */
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
    protected abstract Behavior newContributor(ResourceReference ref);
    
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
            ResourceReference.Key key = new ResourceReference.Key(ref);
            String name = key.toString().replaceAll("/", "-");
            String uniquePath = String.format(
                "%s-%s",
                _path,
                UrlEncoder.PATH_INSTANCE.encode(name, "UTF-8")
            );
            app.mountResource(uniquePath, ref);
        }
    }
    
    /**
     * Delegate to the wicketstuff-merged-resources library for mounting the
     * mereged resources with reasonable settings.
     */
    private void mountMergedResources(WebApplication app)
    {
        // TODO: We can't use ResourceMount yet because it isn't compiled for
        //       Wicket 1.5!!
        throw new UnsupportedOperationException();
        /*
        ResourceMount mountConfig = new ResourceMount();
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
        */
    }
    
    private Behavior createHeaderContributor()
    {
        return new Behavior() {
            @Override
            public void renderHead(Component comp, IHeaderResponse response)
            {
                for(int i=0; i<_references.size(); i++)
                {
                    ResourceReference ref = _references.get(i);
                    newContributor(ref).renderHead(comp, response);
                }
            }
        };
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
}
