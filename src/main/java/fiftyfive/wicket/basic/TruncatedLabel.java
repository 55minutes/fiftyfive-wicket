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

import fiftyfive.util.TruncateHelper;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

/**
 * A Label that truncates is output to the specified maximum number of
 * characters.
 */
public class TruncatedLabel extends LabelWithPlaceholder
{
    private int _length;
    private TruncateHelper _helper = new TruncateHelper();
    
    /**
     * @see LabelWithPlaceholder#LabelWithPlaceholder(String)
     */
    public TruncatedLabel(final String id, int length)
    {
        this(id, length, (IModel) null);
    }

    /**
     * @see LabelWithPlaceholder#LabelWithPlaceholder(String, String)
     */
    public TruncatedLabel(final String id, int length, String label)
    {
        this(id, length, new Model(label));
    }

    /**
     * @see LabelWithPlaceholder#LabelWithPlaceholder(String, IModel)
     */
    public TruncatedLabel(final String id, int length, IModel<?> model)
    {
        super(id, model);
        _length = length;
    }
    
    /**
     * Returns a truncated version of {@link #getDefaultModelObjectAsString()}.
     */
    @Override
    protected String internalGetDefaultModelObjectAsString()
    {
        boolean origEscape = getEscapeModelStrings();

        // We want to truncate the original, unescaped string. So temporarily
        // set escaping to false, get the string, then return the escaping flag
        // back to its original value.
        setEscapeModelStrings(false);
        String value = getDefaultModelObjectAsString();
        setEscapeModelStrings(origEscape);
        
        // Do the truncation
        String trunc = getTruncateHelper().truncate(value, _length);
        
        // Escape the truncated value if desired.
        if(trunc != null && origEscape)
        {
            trunc = Strings.escapeMarkup(trunc, false, false).toString();
        }
        return trunc;
    }
    
    /**
     * Override this method if you wish to customize the truncation algorithm.
     */
    protected TruncateHelper getTruncateHelper()
    {
        return _helper;
    }
}
