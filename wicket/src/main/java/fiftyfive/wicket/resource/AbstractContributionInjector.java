package fiftyfive.wicket.resource;

import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.behavior.HeaderContributor;

public abstract class AbstractContributionInjector
    implements IComponentInstantiationListener
{
    private List<ResourceReference> _references;
    private List<Class<? extends Component>> _components;
    
    protected AbstractContributionInjector(
            List<Class<? extends Component>> components,
            List<ResourceReference> references)
    {
        _components = components;
        _references = references;
    }
    
    public void onInstantiation(Component newComponent)
    {
        for(Class<? extends Component> c : _components)
        {
            if(c.isInstance(newComponent))
            {
                for(ResourceReference ref : _references)
                {
                    newComponent.add(createHeaderContributor(ref));
                }
            }
        }
    }
    
    protected abstract HeaderContributor createHeaderContributor(
            ResourceReference reference);
}
