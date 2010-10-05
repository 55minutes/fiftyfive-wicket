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

package fiftyfive.wicket.basic;

import fiftyfive.wicket.model.CountMessageModel;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Displays a label with up to three different variations based on
 * whether a numeric model is zero, one, or greater than one. This is
 * incredibly useful for printing count messages where the phrasing needs
 * to change according to singular and plural rules, like this:
 * <ul>
 * <li>There are no friends in your network.</li>
 * <li>There is <u>one friend</u> in your network.</li>
 * <li>There are <u>5 friends</u> in your network.</li>
 * </ul>
 * <p>
 * In addition, the label can contain a hyperlink that you specify by
 * calling {@link #setLink(Class,PageParameters) setLink()}.
 * <p>
 * To achieve the output shown in the example above, you would first add
 * these entries to your localizable properties file associated with your
 * page or panel:
 * <pre class="example">
 * friends.zero = There are no friends in your network.
 * friends.one = There is &lt;a href="${href}"&gt;one friend&lt;/a&gt; in your network.
 * friends.many = There are &lt;a href="${href}"&gt;${count} friends&lt;/a&gt; in your network.</pre>
 * <p>
 * Then, construct the label and add it to your page or panel like so:
 * <pre class="example">
 * add(new CountLabel("friends", numFriendsModel)
 *     .setLink(MyFriendsPage.class));</pre>
 * <p>
 * It's that simple.
 * <p>
 * This class is a simple Label wrapper around a CountMessageModel, which
 * does most of the work. Refer to the class documentation of
 * {@link CountMessageModel} for more details.
 * 
 * @since 2.0
 */
public class CountLabel extends Label
{
    private CountMessageModel _messageModel;
    
    /**
     * Constructs a label that will a display zero, singular or plural
     * variation based on the value of the specified {@code count} model.
     * 
     * @param id The wicket:id of the label component. This is will also
     *           be used as the message key to look up the message strings
     *           in the localized properties file.
     * 
     * @param count A number that will be used to choose either the
     *              <code>id.zero</code>, <code>id.one</code> or
     *              <code>id.many</code> string from the properties file. It
     *              will also be made available to the string as an
     *              interpolated <code>${count}</code> variable.
     */
    public CountLabel(String id, IModel<? extends Number> count)
    {
        super(id);
        setDefaultModel(new CountMessageModel(id, this, count));
        setEscapeModelStrings(false);
    }
    
    /**
     * @see CountMessageModel#setLink(Class)
     * @return {@code this} to allow chaining
     */
    public CountLabel setLink(Class<? extends Page> page)
    {
        return setLink(page, null);
    }
    
    /**
     * @see CountMessageModel#setLink(Class,PageParameters)
     * @return {@code this} to allow chaining
     */
    public CountLabel setLink(Class<? extends Page> page, PageParameters params)
    {
        ((CountMessageModel) getDefaultModel()).setLink(page, params);
        return this;
    }
}
