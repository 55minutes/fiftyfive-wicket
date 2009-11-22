package fiftyfive.wicket.resource;

import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.JavascriptPackageResource;

public class JavaScriptContributionInjector extends AbstractContributionInjector
{
    public JavaScriptContributionInjector(
            List<Class<? extends Component>> components,
            List<ResourceReference> references)
    {
        super(components, references);
    }
    
    protected HeaderContributor createHeaderContributor(ResourceReference ref)
    {
        return JavascriptPackageResource.getHeaderContribution(ref);
    }
}
