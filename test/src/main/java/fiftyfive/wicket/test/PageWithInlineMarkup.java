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
package fiftyfive.wicket.test;

import org.apache.wicket.markup.ContainerInfo;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.StringResourceStream;


/**
 * A WebPage that obtains its markup from a String passed into its
 * constructor, rather than an associated HTML file. Used for testing.
 */
public class PageWithInlineMarkup extends WebPage
{
    private String _htmlMarkup;
    
    public PageWithInlineMarkup(String htmlMarkup)
    {
        this(htmlMarkup, null);
    }
    
    /**
     * @since 2.0.4
     */
    public PageWithInlineMarkup(String htmlMarkup, PageParameters parameters)
    {
        super(parameters);
        _htmlMarkup = htmlMarkup;
    }
    
    @Override
    public Markup getAssociatedMarkup()
    {
        StringResourceStream stream = new StringResourceStream(_htmlMarkup);
        MarkupResourceStream mrs = new MarkupResourceStream(
            stream, new ContainerInfo(this), null
        );
        return MarkupFactory.get().loadMarkup(this, mrs, false);
    }
}
