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

package fiftyfive.wicket.feedback;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * A markup container intended to hold a label, form field and feedback panel.
 * The container will gain the appropriate CSS class whenever feedback
 * messages are present.
 * <p>
 * Essentially this container takes care of some boilerplate Java code
 * needed for bundling a form field with a feedback panel. This way your form
 * displays validation messages along side each field.
 * <pre class="example">
 * &lt;tr wicket:id="username"&gt;
 *   &lt;th&gt;Username&lt;/th&gt;
 *   &lt;td&gt;
 *     &lt;input type="text" wicket:id="username" /&gt;
 *     &lt;wicket:container wicket:id="feedback"/&gt;
 *   &lt;/td&gt;
 * &lt;/tr&gt;
 * 
 * // The easy way:
 * add(new Prompt(new RequiredTextField("username")));
 * 
 * // Which is equivalent to all this:
 * FeedbackPanel feedback = new FeedbackPanel("feedback");
 * WebMarkupContainer username = new WebMarkupContainer("username")
 *     .add(new RequiredTextField("username").add(FeedbackStyle.INSTANCE))
 *     .add(feedback)
 *     .add(FeedbackStyle.INSTANCE);
 * feedback.setFilter(new ContainerFeedbackMessageFilter(username));</pre>
 * 
 * @since 2.0
 */
public class Prompt extends WebMarkupContainer
{
    public Prompt(String id)
    {
        super(id);
    }
    
    public Prompt(Component child)
    {
        super(child.getId());
        add(child);
    }
    
    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        add(FeedbackStyle.INSTANCE);
        add(newFeedbackPanel("feedback"));
    }
    
    /**
     * Automatically adds FeedbackStyle to all FormComponents that are
     * children of this container.
     */
    @Override
    protected void onBeforeRender()
    {
        visitChildren(FormComponent.class, new IVisitor<FormComponent>()
        {
            public Object component(FormComponent fc)
            {
                fc.add(TempFeedbackStyle.INSTANCE);
                return CONTINUE_TRAVERSAL;
            }
        });
        super.onBeforeRender();
    }
    
    protected FeedbackPanel newFeedbackPanel(String id)
    {
        IFeedbackMessageFilter f = new ContainerFeedbackMessageFilter(this);
        return new FeedbackPanel(id, f);
    }
    
    private static class TempFeedbackStyle extends FeedbackStyle
    {
        private static final TempFeedbackStyle INSTANCE = new TempFeedbackStyle();
        
        @Override
        public boolean isTemporary()
        {
            return true;
        }
    }
}
