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
package fiftyfive.wicket.css;

import fiftyfive.wicket.resource.MergedResourceBuilder;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.IHeaderContributor;

/**
 * Instructs Wicket to merge a list of CSS resources into a single file
 * when the application is in deployment mode. Consider using this in your
 * application as a performance optimization.
 * <p>
 * Example usage:
 * <pre class="example">
 * public class MyApplication extends WebApplication
 * {
 *     &#064;Override
 *     protected void init()
 *     {
 *         super.init();
 * 
 *         new MergedCssBuilder()
 *             .setPath("/styles/all.css")
 *             .addCss(WicketApplication.class, "styles/reset.css")
 *             .addCss(WicketApplication.class, "styles/core.css")
 *             .addCss(WicketApplication.class, "styles/layout.css")
 *             .addCss(WicketApplication.class, "styles/content.css")
 *             .addCss(WicketApplication.class, "styles/forms.css")
 *             .addCss(WicketApplication.class, "styles/page-specific.css")
 *             .build(this);
 * 
 *         // The return value from build() can be used on your base page to
 *         // inject all these CSS resources in one shot.
 *     }
 * }</pre>
 * 
 * @since 2.0
 */
public class MergedCssBuilder extends MergedResourceBuilder
{
    private String _media;
    
    /**
     * Creates an empty builder object. See the
     * {@link MergedCssBuilder class documentation} for 
     * example usage.
     */
    public MergedCssBuilder()
    {
        super();
    }
    
    public MergedCssBuilder setPath(String path)
    {
       return (MergedCssBuilder) super.setPath(path);
    }
    
    /**
     * Sets the CSS media type that will be used for the merged CSS resources.
     * By default the merged CSS will not have a media type, meaning it will
     * apply to all media. Alternatively you could specify "screen" or "print"
     * to limit the CSS to those media.
     */
    public MergedCssBuilder setCssMedia(String media)
    {
        _media = media;
        return this;
    }
    
    /**
     * Adds a CSS resource to the list of merged resources.
     *
     * @param scope The class relative to which the resource will be resolved.
     * @param path  The path, relative to the scope, where the CSS file is
     *              is located.
     */
    public MergedCssBuilder addCss(Class<?> scope, String path)
    {
        return addCss(new ResourceReference(scope, path));
    }

    /**
     * Adds a CSS resource to the list of merged resources.
     *
     * @param ref The CSS resource to add.
     */
    public MergedCssBuilder addCss(ResourceReference ref)
    {
        add(ref);
        return this;
    }
    
    protected IHeaderContributor newContributor(ResourceReference ref)
    {
        return CSSPackageResource.getHeaderContribution(ref, _media);
    }
}
