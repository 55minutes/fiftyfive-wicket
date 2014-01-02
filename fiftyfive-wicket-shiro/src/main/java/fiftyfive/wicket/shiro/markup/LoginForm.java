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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.Model;

/**
 * A very simple login form with two fields: {@code email} and
 * {@code password} (your HTML must have those two {@code wicket:id}s).
 * <p>
 * Upon successful login, the user will be sent along to her original
 * destination (i.e. the page she requested before be intercepted by the
 * login page), or otherwise the home page.
 * <p>
 * If login was unsuccessful, an error feedback message will be attached
 * to the email field: "Invalid email address and/or password." You should
 * therefore ensure that there is a
 * {@link org.apache.wicket.markup.html.panel.FeedbackPanel FeedbackPanel}
 * present on your login page. (If you want a different or localized message,
 * define the key {@code loginFailed} in your page or application properties.)
 * <p>
 * This class is intended to get you started quickly with Shiro. If you need
 * anything more complicated, such as more nuanced error handling or localized
 * error messages, consider writing your own form using {@link AbstractLoginForm}
 * as a starting point.
 * 
 * @author Matt Brictson
 * @since 3.0
 */
public class LoginForm extends AbstractLoginForm
{
    /**
     * Creates a login form that does not have a {@code rememberme} checkbox.
     */
    public LoginForm(String id)
    {
        super(id);
        add(new EmailTextField("email", new Model<String>()).setRequired(true));
        add(new PasswordTextField("password", new Model<String>()));
    }
    
    @Override
    protected Component getEmailField()
    {
        return get("email");
    }
    
    @Override
    protected Component getPasswordField()
    {
        return get("password");
    }
}
