package fiftyfive.wicket.resource;


import java.util.ArrayList;
import java.util.List;

import static org.apache.wicket.Application.DEVELOPMENT;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import org.wicketstuff.mergedresources.ResourceMount;


/**
 * Simplifies usage of the wicket-merged-resource library by mounting merged
 * resources with an easy builder API. Applies the following best practices
 * to merged resources:
 * <ul>
 * <li>Only merges resources in deployment mode. In development the resources
 *     will be mounted separately to facilitate debugging.</li>
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
 *     .attachToPage(BasePage.class)
 *     .build(this);
 * </pre>
 */
public class MergedResourceBuilder
{
    private Boolean _isCss;
    private String _media;
    
    private String _path;
    private List<ResourceReference> _references;
    private List<Class<? extends Component>> _components;
    
    public MergedResourceBuilder()
    {
        _references = new ArrayList<ResourceReference>();
        _components = new ArrayList<Class<? extends Component>>();
    }
    
    public MergedResourceBuilder setPath(String path)
    {
        _path = path;
        return this;
    }
    
    public MergedResourceBuilder setCssMedia(String media)
    {
        requireCss();
        _media = media;
        return this;
    }
    
    public MergedResourceBuilder addCss(Class<?> scope, String path)
    {
        return addCss(new ResourceReference(scope, path));
    }

    public MergedResourceBuilder addCss(ResourceReference ref)
    {
        requireCss();
        _references.add(ref);
        return this;
    }
    
    public MergedResourceBuilder addScript(Class<?> scope, String path)
    {
        return addScript(new ResourceReference(scope, path));
    }
    
    public MergedResourceBuilder addScript(ResourceReference ref)
    {
        requireScript();
        _references.add(ref);
        return this;
    }

    public MergedResourceBuilder attachToPage(Class<? extends WebPage> page)
    {
        return attachToComponent(page);
    }
    
    public MergedResourceBuilder attachToComponent(Class<? extends Component> c)
    {
        _components.add(c);
        return this;
    }
    
    public void build(WebApplication app)
    {
        assertRequiredOptions();
        mountResources(app);
        injectHeaderContributions(app);
    }
    
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
    
    private void injectHeaderContributions(WebApplication app)
    {
        if(_isCss)
        {
            app.addComponentInstantiationListener(
                new CssContributionInjector(_media, _components, _references)
            );
        }
        else
        {
            app.addComponentInstantiationListener(
                new JavaScriptContributionInjector(_components, _references)
            );
        }
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
    
}
