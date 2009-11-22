package fiftyfive.wicket.resource;

import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;

public class CssContributionInjector extends AbstractContributionInjector
{
    private String _media;
    
    public CssContributionInjector(String media,
                                   List<Class<? extends Component>> components,
                                   List<ResourceReference> references)
    {
        super(components, references);
        _media = media;
    }
    
    protected HeaderContributor createHeaderContributor(ResourceReference ref)
    {
        return CSSPackageResource.getHeaderContribution(ref, _media);
    }
}
