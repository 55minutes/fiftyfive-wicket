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
package fiftyfive.wicket.prototype;

import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.model.IModel;

/**
 * Repeats a section of raw markup N times. This is identical to Wicket's
 * built-in {@link Loop} component, except you do not have to provide a
 * boilerplate implementation of {@code populateItem()}. This is useful for
 * very quickly simulating a large number of repeated items in an HTML mockup
 * before real data and Wicket components have been wired up.
 * <p>
 * For example:
 * <pre class="example">
 * &lt;ul&gt;
 *   &lt;li wicket:id="repeat"&gt;Hello&lt;li/&gt;
 * &lt;/ul&gt;
 * 
 * add(new RawMarkupLoop("repeat", 5));</pre>
 * <p>
 * Would result in the following output:
 * <pre class="example">
 * &lt;ul&gt;
 *   &lt;li&gt;Hello&lt;li/&gt;
 *   &lt;li&gt;Hello&lt;li/&gt;
 *   &lt;li&gt;Hello&lt;li/&gt;
 *   &lt;li&gt;Hello&lt;li/&gt;
 *   &lt;li&gt;Hello&lt;li/&gt;
 * &lt;/ul&gt;</pre>
 * 
 * @since 2.0
 */
public class RawMarkupLoop extends Loop
{
    public RawMarkupLoop(String id, IModel<Integer> repetitions)
    {
        super(id, repetitions);
    }

    public RawMarkupLoop(String id, int repetitions)
    {
        super(id, repetitions);
    }
    
    /**
     * Empty.
     */
    protected void populateItem(LoopItem item)
    {
        // pass
    }
}
