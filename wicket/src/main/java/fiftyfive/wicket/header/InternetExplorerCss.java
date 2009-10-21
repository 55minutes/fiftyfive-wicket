package fiftyfive.wicket.header;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;


public class InternetExplorerCss
{
    public static HeaderContributor getConditionalHeaderContribution (
        final String condition,
        final ResourceReference cssReference
    )
    {
        return getConditionalHeaderContribution(condition, cssReference, null);
    }

    public static HeaderContributor getConditionalHeaderContribution (
        final String condition,
        final ResourceReference cssReference,
        final String media
    )
    {
        return new HeaderContributor(new IHeaderContributor() {
            public void renderHead(IHeaderResponse response)
            {
                final List<Serializable> token = Arrays.asList(
                    condition, cssReference, media
                );
                if(!response.wasRendered(token))
                {
                    response.getResponse().write("<!--[if ");
                    response.getResponse().write(condition);
                    response.getResponse().println("]>");
                    response.renderCSSReference(cssReference, media);
                    response.getResponse().println("<![endif]-->");
                    response.markRendered(token);
                }
            }
        });
    }
    
    /**
     * Private constructor so this class cannot be instantiated in normal use.
     */
    private InternetExplorerCss()
    {
        super();
    }
}
