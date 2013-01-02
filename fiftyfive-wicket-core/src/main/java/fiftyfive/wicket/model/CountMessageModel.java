/**
 * Copyright 2013 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.model;

import fiftyfive.wicket.util.HtmlUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;

/**
 * Selects an appropriate localized string based on whether a count value
 * is zero, one or greater than one. This makes it easier to produce singluar
 * and plural phrases.
 * <p>
 * For example, consider the case where we would like to set one of these
 * three feedback messages. Notice how the phrasing is slightly different
 * depending on the number of items. Furthermore in the latter two examples
 * we would like to embed a hyperlink to a bookmarkable page.
 * <ul>
 * <li>There are no friends in your network.</li>
 * <li>There is <u>one friend</u> in your network.</li>
 * <li>There are <u>5 friends</u> in your network.</li>
 * </ul>
 * <p>
 * Normally in Wicket you would accomplish this using tedious if/else
 * statements and string concatenation, conditionally visible
 * fragments/containers, or the incredibly arcane syntax of Java's
 * {@link java.text.ChoiceFormat ChoiceFormat} (and note that you'd have to
 * carefully escape the "&lt;" and "&gt;" if you want to include HTML markup
 * as we need for the hyperlinks in this example). All of these
 * approaches are clumsy, and it would be hard for a non-programmer to localize
 * or edit the strings.
 * <p>
 * With CountMessageModel there is a much easier way: simply declare three
 * separate localizable strings in the properties file of your page or panel.
 * <pre class="example">
 * friends.zero = There are no friends in your network.
 * friends.one = There is &lt;a href="${href}"&gt;one friend&lt;/a&gt; in your network.
 * friends.many = There are &lt;a href="${href}"&gt;${count} friends&lt;/a&gt; in your network.</pre>
 * <p>
 * And your Java would be:
 * <pre class="example">
 * new CountMessageModel("friends", pageOrPanelObject, numFriends)
 *     setLink(FriendsPage.class);</pre>
 * <p>
 * CountMessageModel selects the appropriate localized string depending on the
 * value of {@code numFriends}, and subtitutes values for
 * <code>${href}</code> and <code>${count}</code> automatically.
 * <p>
 * You can now use this model in a label (also consider
 * {@link fiftyfive.wicket.basic.CountLabel CountLabel} as a shortcut for this
 * common use case), or call {@link #getObject getObject()} to get the
 * interpolated string value for use in a feedback message.
 * 
 * @since 2.0
 */
public class CountMessageModel extends AbstractReadOnlyModel<String>
{
    private Component component;
    private StringResourceModel stringModel;
    private IModel<? extends Number> countModel;
    private Class<? extends Page> linkPage;
    private PageParameters linkParams;
    
    /**
     * Constructs a CountMessageModel that will choose the appropriate
     * localized string from a properties file.
     * 
     * @param messageKey The key that will be consulted in the properties file.
     *                   The key will have ".zero", ".one" or ".many" appended
     *                   to it depending on the value of the {@code count}.
     * @param component The Wicket component that will be used for finding
     *                  the properties file. Usually this is the page or panel
     *                  where you plan to use the model.
     * @param count The number that will be used in the message. This will
     *              be used to substitute any <code>${count}</code> expressions
     *              in the message.
     */
    public CountMessageModel(String messageKey,
                             Component component,
                             IModel<? extends Number> count)
    {
        super();
        Args.notNull(messageKey, "messageKey");
        Args.notNull(component, "component");
        Args.notNull(count, "count");
        
        this.component = component;
        this.countModel = count;
        this.stringModel = new StringResourceModel(
            messageKey + "${resourceSuffix}",
            component,
            new AbstractReadOnlyModel<CountMessageModel>()
            {
                public CountMessageModel getObject()
                {
                    return CountMessageModel.this;
                }
            }
        );
    }
    
    /**
     * Detaches the count model passed into the constructor and other
     * internal state.
     */
    @Override
    public void detach()
    {
        this.countModel.detach();
        this.stringModel.detach();
        super.detach();
    }

    /**
     * Returns the message after all interpolation has been performed.
     */
    public String getObject()
    {
        return this.stringModel.getObject();
    }
    
    /**
     * Sets the bookmarkable page that will be used to generate a link within
     * the message. A special <code>${href}</code> variable will be exposed
     * for use within the message.
     * 
     * @return {@code this} to allow chaining
     */
    public CountMessageModel setLink(Class<? extends Page> page)
    {
        return setLink(page, null);
    }
    
    /**
     * Sets the bookmarkable page class and page parameters that will be used
     * to generate a link within the message. A special <code>${href}</code> 
     * variable will be exposed for use within the message.
     * 
     * @return {@code this} to allow chaining
     */
    public CountMessageModel setLink(Class<? extends Page> page,
                                     PageParameters params)
    {
        this.linkPage = page;
        this.linkParams = params;
        return this;
    }
    
    /**
     * Returns the current value of the count model. This is exposed as
     * <code>${count}</code> within the message.
     */
    public int getCount()
    {
        Number num = this.countModel.getObject();
        return num != null ? num.intValue() : 0;
    }
    
    /**
     * Returns the URL to the bookmarkable page specified in a previous call
     * to {@link #setLink(Class,PageParameters) setLink()}. This is exposed as
     * <code>${href}</code> within the message. If {@code setLink()} was not
     * called, then this value will be {@code null}.
     */
    public CharSequence getHref()
    {
        if(this.linkPage != null)
        {
            return HtmlUtils.escapeAttribute(
                this.component.urlFor(this.linkPage, this.linkParams)
            );
        }
        return null;
    }
    
    /**
     * Returns either ".zero", ".one", or ".many" depending on the current
     * value of the count model.
     */
    public String getResourceSuffix()
    {
        int count = getCount();
        
        if(0 == count) return ".zero";
        if(1 == count) return ".one";
        return ".many";
    }
}
