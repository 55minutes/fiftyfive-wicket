/**
 * Copyright 2012 55 Minutes (http://www.55minutes.com)
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

import java.util.Locale;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

// TODO: unit test

/**
 * A label that renders its value using Java's {@code String.format()}.
 * This is useful for printing numeric values, especially floating point.
 * <p>
 * Example usage:
 * <pre class="example">
 * &lt;span wicket:id="balance"&gt;&lt;/span&gt;
 * 
 * IModel&lt;BigDecimal&gt; balance = Model.of(new BigDecimal("1.1"));
 * add(new FormattedLabel("balance", "%.2f", balance);</pre>
 * <p>
 * Output:
 * <pre class="example">
 * &lt;span&gt;1.10&lt;/span&gt;</pre>
 *
 * @since 2.0.4
 */
public class FormattedLabel extends LabelWithPlaceholder
{
    private String format;
    
    /**
     * Construct the label using an implied model.
     * 
     * @param id The {@code wicket:id} in the markup.
     * @param format A Java <a href="http://download.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax">format string</a>.
     */
    public FormattedLabel(String id, String format)
    {
        super(id);
        this.format = format;
    }

    /**
     * Construct the label using an explicit model.
     * 
     * @param id The {@code wicket:id} in the markup.
     * @param format A Java <a href="http://download.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax">format string</a>.
     * @param model The value to be formatted.
     */
    public FormattedLabel(String id, String format, IModel<?> model)
    {
        super(id, model);
        this.format = format;
    }
    
    @Override
    public <C> IConverter<C> getConverter(Class<C> type)
    {
        return new IConverter<C>() {
            public String convertToString(C value, Locale locale)
            {
                if(null == value) return null;
                return String.format(locale, FormattedLabel.this.format, value);
            }
            public C convertToObject(String value, Locale locale)
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
