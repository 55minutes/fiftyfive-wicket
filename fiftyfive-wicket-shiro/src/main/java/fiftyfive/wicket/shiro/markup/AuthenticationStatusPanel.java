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
package fiftyfive.wicket.shiro.markup;

import org.apache.shiro.SecurityUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * Displays a login or logout link based on whether the current user is
 * known. If the user is known, also display the username. Consider placing
 * this panel on your base page.
 * <p>
 * This class is intended to get you started quickly with Shiro.
 * Any non-trivial application should probably reimplement this panel with an
 * appropriate look and feel.
 * 
 * @author Matt Brictson
 * @since 3.0
 */
public class AuthenticationStatusPanel extends Panel
{
    public AuthenticationStatusPanel(String id)
    {
        super(id);
        add(new Label("user", new PropertyModel<Object>(this, "username")));
        add(new LoginLink("login"));
        add(new LogoutLink("logout"));
    }
    
    /**
     * Returns the current Shiro principal (in other words, username).
     * Will be {@code null} if the user is unknown. You may override this if
     * the principal is not appropriate for display
     * (e.g. if it is a database identifier).
     */
    public String getUsername()
    {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        return principal != null ? principal.toString() : null;
    }
}
