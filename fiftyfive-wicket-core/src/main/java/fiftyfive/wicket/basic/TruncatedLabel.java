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
package fiftyfive.wicket.basic;

import fiftyfive.util.TruncateHelper;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

/**
 * A Label that truncates its output to the specified maximum number of
 * characters.
 * <p>
 * The string value is tested and truncated <em>before</em> the string
 * is escaped into HTML entities. After the string is truncated <em>then</em>
 * the resulting value is escaped. The
 * {@link #setEscapeModelStrings escapeModelStrings} property is honored, so
 * you just as with a normal Label, you can disable escaping entirely if you
 * want.
 * <p>
 * Note that if you provide a {@link #setPlaceholder placeholder}, the
 * truncation logic will <em>not</em> be applied to the placeholder value.
 * <p>
 * Although the default truncation algorithm should be sufficient, you can
 * customize it if you wish by setting the
 * {@link #setTruncateHelper truncateHelper} property. Refer to
 * {@link TruncateHelper#truncate TruncateHelper.truncate()} for complete
 * details on how the default truncation rules are applied.
 * <p>
 * Usage:
 * <pre class="example">
 * &lt;span wicket:id="label"&gt;Dummy&lt;/span&gt;
 * 
 * add(new TruncatedLabel("label", 50, "My really long string that needs to be cut down to size."));</pre>
 * <p>
 * Produces:
 * <pre class="example">
 * &lt;span&gt;My really long string that needs to be cut down…&lt;/span&gt;</pre>
 * 
 * @since 2.0
 */
public class TruncatedLabel extends LabelWithPlaceholder
{
    private int length;
    private TruncateHelper helper = new TruncateHelper();
    
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
        this.length = length;
    }
    
    // Override to change return type.
    @Override
    public TruncatedLabel setPlaceholder(String valueIfEmpty)
    {
        return (TruncatedLabel) super.setPlaceholder(valueIfEmpty);
    }

    // Override to change return type.
    @Override
    public TruncatedLabel setPlaceholder(IModel<?> valueIfEmpty)
    {
        return (TruncatedLabel) super.setPlaceholder(valueIfEmpty);
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
        String trunc = getTruncateHelper().truncate(value, this.length);
        
        // Escape the truncated value if desired.
        if(trunc != null && origEscape)
        {
            trunc = Strings.escapeMarkup(trunc, false, false).toString();
        }
        return trunc;
    }
    
    public TruncateHelper getTruncateHelper()
    {
        return this.helper;
    }

    /**
     * Sets the TruncateHelper that will be used to shorten the string
     * value. Use the properties of the helper object to customize the
     * truncation algorithm. The default helper is the one provided by
     * the TruncateHelper empty constructor.
     * 
     * @see TruncateHelper#TruncateHelper
     */
    public TruncatedLabel setTruncateHelper(TruncateHelper helper)
    {
        this.helper = helper;
        return this;
    }
}
