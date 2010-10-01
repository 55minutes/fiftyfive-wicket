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

import fiftyfive.util.Assert;
import fiftyfive.wicket.util.HtmlUtils;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

/**
 * TODO
 * 
 * @since 2.0
 */
public class CountLabel extends Label
{
    private IModel<? extends Number> _countModel;
    private Class<? extends Page> _linkPage;
    private PageParameters _linkParams;
    
    public CountLabel(String id, IModel<? extends Number> count)
    {
        super(id);
        Assert.notNull(count, "count model cannot be null");
        _countModel = count;
    }
    
    @Override
    protected void onDetach()
    {
        _countModel.detach();
        super.onDetach();
    }
    
    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        setEscapeModelStrings(false);
        setDefaultModel(new StringResourceModel(
            getId() + "${resourceSuffix}", getParent(), Model.of(this)
        ));
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
        _linkPage = page;
        _linkParams = params;
        return this;
    }
    
    public int getCount()
    {
        Number num = _countModel.getObject();
        return num != null ? num.intValue() : 0;
    }
    
    public CharSequence getHref()
    {
        if(_linkPage != null)
        {
            return HtmlUtils.escapeAttribute(urlFor(_linkPage, _linkParams));
        }
        return null;
    }
    
    public String getResourceSuffix()
    {
        int count = getCount();
        
        if(0 == count) return ".zero";
        if(1 == count) return ".one";
        return ".many";
    }
}
