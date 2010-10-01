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
 * TODO
 * 
 * @since 2.0
 */
public class CountLabel extends Label
{
    private CountMessageModel _messageModel;
    
    public CountLabel(String id, IModel<? extends Number> count)
    {
        super(id);
        setDefaultModel(new CountMessageModel(id, this, count));
        setEscapeModelStrings(false);
    }
    
    /**
     * @return {@code this} to allow chaining
     */
    public CountLabel setLink(Class<? extends Page> page)
    {
        return setLink(page, null);
    }
    
    /**
     * @return {@code this} to allow chaining
     */
    public CountLabel setLink(Class<? extends Page> page, PageParameters params)
    {
        ((CountMessageModel) getDefaultModel()).setLink(page, params);
        return this;
    }
}
