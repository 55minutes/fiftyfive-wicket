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

package fiftyfive.wicket.js.datetime;

import fiftyfive.util.Assert;
import fiftyfive.wicket.js.DomReadyTemplate;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;

/**
 * A simple jQuery UI-based replacement for Wicket’s YUI-based
 * {@link org.apache.wicket.extensions.yui.calendar.DatePicker DatePicker}.
 * This implementation has the following behavior:
 * <ul>
 * <li>The
 *     <a href="http://www.famfamfam.com/lab/icons/silk/">famfamfam Silk</a>
 *     calendar icon is used as a button to show and hide the date picker.</li>
 * <li>Clicks inside the text field (i.e. giving it focus) also cause the date
 *     picker to appear automatically.</li>
 * <li>The month and year can be selected from drop-down menus.</li>
 * </ul>
 * <p>
 * Here’s how you might use it in practice:
 * <pre class="example">
 * add(DateTextField.forDatePattern("date", "mm/dd/yyyy")
 *     .setRequired(true)
 *     .add(new JQueryDatePicker())
 * );</pre>
 * <p>
 * In keeping with the fiftyfive-wicket-js design philosophy, this Java class
 * is extremely lightweight, allowing programmatic access only to change the
 * button image that is used by the picker. This should suffice for most
 * simple date picker use cases. If you want a different set of
 * date picker behavior, write a subclass and provide your own accompaning
 * JavaScript file to peform the desired initialization.
 * 
 * 
 * @since 2.0
 */
public class JQueryDatePicker extends DomReadyTemplate
{
    private static ResourceReference DEFAULT_IMAGE = new ResourceReference(
        JQueryDatePicker.class,
        "calendar.png"
    );
    
    /**
     * Sets the date picker button image that will be used by default for
     * all date pickers. This static method is convenient because most
     * applications will use the same icon throughout the entire application.
     * To change the image on an individual picker, call
     * {@link #setButtonImage setButtonImage()}.
     */
    public static void setDefaultButtonImage(ResourceReference defaultImage)
    {
        Assert.notNull(defaultImage);
        DEFAULT_IMAGE = defaultImage;
    }
    
    private ResourceReference _buttonImage;
    
    /**
     * Sets the button image (a calendar icon, for example) that will be
     * displayed and used to show and hide the date picker when clicked.
     * By default this is the
     * <a href="http://www.famfamfam.com/lab/icons/silk/">famfamfam Silk</a>
     * famfamfam Silk calendar icon, or whatever was provided to
     * {@link #setDefaultButtonImage setDefaultButtonImage()}.
     * 
     * @return {@code this} to allow chaining
     */
    public JQueryDatePicker setButtonImage(ResourceReference buttonImage)
    {
        Assert.notNull(buttonImage);
        this._buttonImage = buttonImage;
        return this;
    }
    
    public ResourceReference getButtonImage()
    {
        return _buttonImage != null ? _buttonImage : DEFAULT_IMAGE;
    }
    
    /**
     * Returns the URL for the button image, for use inside the JavaScript
     * template as <code>${behavior.buttonImageUrl}</code>.
     */
    public CharSequence getButtonImageUrl()
    {
        return RequestCycle.get().urlFor(getButtonImage());
    }
}
