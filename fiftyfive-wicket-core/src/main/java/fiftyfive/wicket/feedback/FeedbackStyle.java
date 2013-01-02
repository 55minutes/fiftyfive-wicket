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
package fiftyfive.wicket.feedback;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.markup.ComponentTag;

/**
 * Automatically adds an appropriate CSS feedback class to a component if
 * that component or any of its descendants have feedback messages.
 * <p>
 * For example, let's say our Java looks like this:
 * <pre class="example">
 * add(new RequiredTextField("username").add(FeedbackStyle.INSTANCE));</pre>
 * <p>
 * Our HTML is:
 * <pre class="example">
 * &lt;input type="text" wicket:id="username" /&gt;</pre>
 * <p>
 * Now, when our text field has a validation message to report, for example
 * when the user submits the form without filling in the required value, our
 * component will render like this:
 * <pre class="example">
 * &lt;input type="text" class="feedbackPanelERROR" /&gt;</pre>
 * <p>
 * Notice how the {@code <input>} gains the appropriate CSS class.
 * 
 * @since 2.0
 */
public class FeedbackStyle extends Behavior
{
    public static final FeedbackStyle INSTANCE = new FeedbackStyle();
    
    public void onComponentTag(Component c, ComponentTag tag)
    {
        List<FeedbackMessage> msgs = newFeedbackMessagesModel(c).getObject();
        if(msgs.size() > 0)
        {
            StringBuffer newClassValue = new StringBuffer(
                tag.getAttributes().getString("class", "")
            );
            Set<String> classes = new HashSet<String>();
            for(FeedbackMessage m : msgs)
            {
                String cssClass = getCssClass(m);
                if(classes.add(cssClass))
                {
                    if(newClassValue.length() > 0)
                    {
                        newClassValue.append(" ");
                    }
                    newClassValue.append(cssClass);
                }
            }
            tag.put("class", newClassValue.toString());
            tag.setModified(true);
        }
    }
    
    protected FeedbackMessagesModel newFeedbackMessagesModel(Component c)
    {
        FeedbackMessagesModel model = new FeedbackMessagesModel(c);
        if(c instanceof MarkupContainer)
        {
            model.setFilter(new ContainerFeedbackMessageFilter(
                (MarkupContainer) c)
            );
        }
        else
        {
            model.setFilter(new ComponentFeedbackMessageFilter(c));
        }
        return model;
    }
    
    protected String getCssClass(FeedbackMessage msg)
    {
        return "feedbackPanel" + msg.getLevelAsString();
    }
}
