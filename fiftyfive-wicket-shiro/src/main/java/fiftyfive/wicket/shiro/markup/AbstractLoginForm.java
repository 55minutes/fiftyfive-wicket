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
package fiftyfive.wicket.shiro.markup;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A form that authenticates the user via Shiro when an email and password are submitted.
 * Also supports "remember me". Subclasses are expected to provide the actual markup and Wicket
 * components for the email and password fields. Refer to {@link LoginForm} for a reference
 * implementation.
 *
 * @see #getEmailField
 * @see #getPasswordField
 * @see #remember
 * @since 3.0
 */
public abstract class AbstractLoginForm extends StatelessForm<Void>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginForm.class);
    
    public AbstractLoginForm(String id)
    {
        super(id);
    }

    /**
     * Returns the email form field component. It is the responsibility of the subclass to
     * construct this component, give it a String model, and insert it into the component
     * hierarchy according to the desired markup. When the form is submitted the model of
     * this email field component will be used as the email address for authentication.
     */
    protected abstract Component getEmailField();

    /**
     * Returns the password form field component. It is the responsibility of the subclass to
     * construct this component, give it a String model, and insert it into the component
     * hierarchy according to the desired markup. When the form is submitted the model of
     * this password field component will be used as the password for authentication.
     */
    protected abstract Component getPasswordField();
    
    /**
     * Delegate to {@link #loginShiro loginShiro()} to peform the authentication; if it
     * succeeds, redirect to the user's original intended destination or to the
     * application home page.
     */
    @Override
    protected void onSubmit()
    {
        String email = getEmailField().getDefaultModelObjectAsString();
        String password = getPasswordField().getDefaultModelObjectAsString();
        
        // Convert email to lowercase just in case the backend authentication system
        // is case sensitive and the user accidentally typed in uppercase.
        if(email != null)
        {
            email = email.toLowerCase();
        }

        if(loginShiro(email, password, remember()))
        {
            if(!continueToOriginalDestination())
            {
                setResponsePage(getApplication().getHomePage());
            }
        }
    }
    
    /**
     * Peform the actual authentication using Shiro's {@link Subject#login login()}.
     * <p>
     * <b>Important:</b> this method is written to ensure that the user's session
     * is replaced with a new session before authentication is performed. This is to
     * prevent a <a href="https://www.owasp.org/index.php/Session_Fixation">session fixation</a>
     * attack. As a side effect, any existing session data will therefore be lost.
     * 
     * @return {@code true} if authentication succeeded
     */
    protected boolean loginShiro(String email, String password, boolean remember)
    {
        Subject currentUser = SecurityUtils.getSubject();

        // Force a new session to prevent fixation attack.
        // We have to invalidate via both Shiro and Wicket; otherwise it doesn't work.
        currentUser.getSession().stop(); // Shiro
        getSession().replaceSession();   // Wicket
        
        UsernamePasswordToken token;
        token = new UsernamePasswordToken(email, password, remember);
        try
        {
            currentUser.login(token);
            return true;
        }
        catch (AuthenticationException ae)
        {
            onAuthenticationException(ae);
        }
        return false;
    }
    
    /**
     * Handle any exceptions that are thrown upon login failure by setting an appropriate
     * feedback message. The default implemention adds an error feedback message with the key
     * {@code loginFailed} to the email field.
     */
    protected void onAuthenticationException(AuthenticationException ae)
    {
        LOGGER.debug("Shiro Subject.login() failed", ae);
        getEmailField().error(getString("loginFailed", null, "Invalid email and/or password."));
    }
    
    /**
     * Override this method to return {@code true} if you want to enable
     * Shiro's "remember me" feature. By default this returns {@code false}.
     */
    protected boolean remember()
    {
        return false;
    }
}
