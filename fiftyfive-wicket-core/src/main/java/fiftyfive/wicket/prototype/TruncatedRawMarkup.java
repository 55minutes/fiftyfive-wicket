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

package fiftyfive.wicket.prototype;

import fiftyfive.util.TruncateHelper;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.RawMarkup;
import org.apache.wicket.markup.html.WebComponent;

/**
 * Truncates placeholder HTML markup to a specified maximum number of
 * characters. This is only useful during prototyping, where you would like
 * to simulate and document the truncation rules for sections of your HTML
 * mockup.
 * <p>
 * In production code, to truncate dynamic data rather than placeholder HTML,
 * you would use {@link fiftyfive.wicket.basic.TruncatedLabel TruncatedLabel}.
 * <p>
 * Example usage:
 * <pre class="example">
 * &lt;p wicket:id="truncate"&gt;
 *   Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
 *   tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim
 *   veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea
 *   commodo consequat. Duis aute irure dolor in reprehenderit in voluptate
 *   velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint
 *   occaecat cupidatat non proident, sunt in culpa qui officia deserunt
 *   mollit anim id est laborum.
 * &lt;/p&gt;
 * 
 * add(new TruncatedRawMarkup("truncate", 100));</pre>
 * <p>
 * Would result in the following output:
 * <pre class="example">
 * &lt;p&gt;
 *   Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod
 *   tempor incididunt ut…
 * &lt;/p&gt;</pre>
 * 
 * @since 2.0
 */
public class TruncatedRawMarkup extends WebComponent
{
    private int length;
    private TruncateHelper helper = new TruncateHelper();
    
    public TruncatedRawMarkup(String id, int length)
    {
        super(id);
        this.length = length;
    }
    
    @Override
    public void onComponentTagBody(MarkupStream markup, ComponentTag tag)
    {
        // We assume the body of the component is raw HTML
        // (i.e. not nested wicket components or wicket tags).
        RawMarkup raw = (RawMarkup) markup.get();
        getResponse().write(
            getTruncateHelper().truncate(raw.toString(), this.length)
        );
        markup.next();
    }
    
    protected TruncateHelper getTruncateHelper()
    {
        return this.helper;
    }
}
