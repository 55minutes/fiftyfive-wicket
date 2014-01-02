/**
 * Copyright 2014 55 Minutes (http://www.55minutes.com)
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
package fiftyfive.wicket.shiro.markup;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.Model;

/**
 * A variation of {@link LoginForm} that includes a "remember me" checkbox.
 * The checkbox will be unchecked by default. Your markup must contain
 * {@code <input type="checkbox" wicket:id="rememberme"/>}.
 * 
 * @since 3.0
 */
public class RememberMeLoginForm extends LoginForm
{
    private CheckBox rememberCheck;

    /**
     * Create a login form with a "remember me" checkbox.
     */
    public RememberMeLoginForm(String id)
    {
        super(id);
        add(this.rememberCheck = new CheckBox("rememberme", Model.of(false)));
    }
    
    /**
     * Returns {@code true} to enable the "remember me" feature if the
     * checkbox is checked.
     */
    protected boolean remember()
    {
        return this.rememberCheck.getModelObject();
    }
}
