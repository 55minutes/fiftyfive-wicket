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
package fiftyfive.wicket.shiro;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fiftyfive.wicket.shiro.handler.LogoutRequestHandler;
import fiftyfive.wicket.shiro.markup.LoginPage;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.aop.AuthenticatedAnnotationHandler;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.authz.aop.GuestAnnotationHandler;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.authz.aop.RoleAnnotationHandler;
import org.apache.shiro.authz.aop.UserAnnotationHandler;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;

import org.apache.wicket.markup.html.pages.AccessDeniedPage;

import org.apache.wicket.protocol.http.WebApplication;

import org.apache.wicket.request.IRequestHandler;

import org.apache.wicket.request.component.IRequestableComponent;

import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;

import org.apache.wicket.request.flow.ResetResponseException;

import org.apache.wicket.request.handler.PageProvider;
import org.apache.wicket.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.handler.RenderPageRequestHandler.RedirectPolicy;

import org.apache.wicket.request.http.WebRequest;

import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.request.mapper.mount.MountMapper;

import org.apache.wicket.settings.ISecuritySettings;


/**
 * Enhances Wicket to integrate closely with the Apache Shiro security
 * framework. With the {@code ShiroWicketPlugin} installed in your Wicket
 * application, you will gain the following features:
 * <ul>
 * <li>You can use all of Shiro's authorization annotations
 *     (like
 *     {@link org.apache.shiro.authz.annotation.RequiresAuthentication @RequiresAuthentication}
 *     and
 *     {@link org.apache.shiro.authz.annotation.RequiresPermissions @RequiresPermissions})
 *     on Wicket Pages. The {@code ShiroWicketPlugin} will ensure that only
 *     authorized users can access these pages, and will show an appropriate
 *     error page or login page otherwise.
 *     See {@link #isInstantiationAuthorized isInstantiationAuthorized()}.
 * </li>
 * <li>You can also use the same Shiro annotations on individual components,
 *     like Links and Panels. The {@code ShiroWicketPlugin} will automatically
 *     hide these components from unauthorized users.
 *     See {@link #isActionAuthorized isActionAuthorized()}.
 * </li>
 * <li>You can access Shiro directly at any time in your Wicket code
 *     by calling
 *     {@link org.apache.shiro.SecurityUtils#getSubject SecurityUtils.getSubject()}.
 *     This gives you access to the rich set of security operations on the
 *     Shiro {@link org.apache.shiro.subject.Subject Subject} that represents
 *     the current user.
 * </li>
 * <li>Any uncaught Shiro
 *     {@link AuthorizationException AuthorizationExceptions}
 *     will be handled gracefully by redirecting the user to the
 *     login page or an unauthorized error page. This allows you to implement
 *     comprehensive security rules using Shiro at any tier of your
 *     application and be confident that your UI will handle them
 *     appropriately.
 *     See {@link #onException onException()}.
 * </li>
 * </ul>
 * <h2>Installation</h2>
 * Before you can use the {@code ShiroWicketPlugin}, you must have Shiro
 * properly added to your application's {@code web.xml} file. Refer to the
 * <a href="../../../overview-summary.html">overview section</a> of this Javadoc
 * for a brief tutorial.
 * <h3>{@code Application.init()}</h3>
 * Once Shiro itself is installed, adding {@code ShiroWicketPlugin} can be as
 * simple as adding one line to your Wicket application {@code init()}:
 * <pre class="example">
 * public class MyApplication extends WebApplication
 * {
 *     &#064;Override
 *     protected void init()
 *     {
 *         super.init();
 *         new ShiroWicketPlugin().install(this);
 *     }
 * }</pre>
 * Most developers will want to customize the login page and error pages.
 * The more complex real-world installation is thus:
 * <pre class="example">
 * public class MyApplication extends WebApplication
 * {
 *     &#064;Override
 *     protected void init()
 *     {
 *         super.init();
 *         new ShiroWicketPlugin()
 *             .mountLoginPage("login", MyLoginPage.class)
 *             .setUnauthorizedPage(MyAccessDeniedPage.class)
 *             .install(this);
 *     }
 * }</pre>
 * 
 * @author Matt Brictson
 * @since 3.0
 */
public class ShiroWicketPlugin
    extends AbstractRequestCycleListener
    implements IAuthorizationStrategy,
               IUnauthorizedComponentInstantiationListener
{
    /**
     * The key that will be used to obtain a localized message
     * when access is denied due to the user be unauthenticated.
     */
    public static final String LOGIN_REQUIRED_MESSAGE_KEY = "loginRequired";

    private static final MetaDataKey<AuthorizationException> EXCEPTION_KEY =
        new MetaDataKey<AuthorizationException>() {};
    
    private static final MetaDataKey<ShiroWicketPlugin> PLUGIN_KEY =
        new MetaDataKey<ShiroWicketPlugin>() {};

    private static final AuthorizingAnnotationHandler[] HANDLERS =
        new AuthorizingAnnotationHandler[] {
            new AuthenticatedAnnotationHandler(),
            new GuestAnnotationHandler(),
            new PermissionAnnotationHandler(),
            new RoleAnnotationHandler(),
            new UserAnnotationHandler()
        };

    /**
     * Returns the {@code ShiroWicketPlugin} instance that has been installed
     * in the current Wicket application. This is a convenience method that
     * only works within a Wicket thread, and it assumes that
     * {@link #install install()} has already been called.
     * 
     * @throws IllegalStateException if there is no Wicket application bound
     *                               to the current thread, or if a
     *                               {@code ShiroWicketPlugin} has not been
     *                               installed.
     */
    public static ShiroWicketPlugin get()
    {
        Application app = Application.get();
        if(null == app)
        {
            throw new IllegalStateException(
                "No wicket application is bound to the current thread."
            );
        }
        ShiroWicketPlugin plugin = app.getMetaData(PLUGIN_KEY);
        if(null == plugin)
        {
            throw new IllegalStateException(
                "A ShiroWicketPlugin has not been installed in this Wicket " +
                "application. You must call ShiroWicketPlugin.install() in " +
                "your application init()."
            );
        }
        return plugin;
    }
    
    
    private String loginPath = "login";
    private String logoutPath = "logout";
    private Class<? extends Page> loginPage = LoginPage.class;
    private Class<? extends Page> unauthorizedPage = AccessDeniedPage.class;
    
    public Class<? extends Page> getLoginPage()
    {
        return loginPage;
    }

    /**
     * Set the bookmarkable page that will be displayed when an <em>unauthenticated</em> user
     * attempts to access a page that requires authentication.
     * 
     * @param mountPath The bookmarkable URI where the login page will be mounted when
     *                  {@link #install install} is called. The default is {@code "/login"}.
     *                  May be {@code null}, in which case the login page will not be mounted.
     *                  You would want to pass {@code null}, for example, if you use your home
     *                  page as the login page, since in that case the home page is already
     *                  implicitly mounted on {@code "/"}.
     * 
     * @param loginPage The page to use as the login page when the user needs to be
     *                  authenticated. Cannot be {@code null}. The default is a simple
     *                  out-of-the-box {@link LoginPage}.
     * 
     * @return {@code this} to allow chaining
     */
    public ShiroWicketPlugin mountLoginPage(String mountPath, Class<? extends Page> loginPage)
    {
        this.loginPath = mountPath;
        this.loginPage = loginPage;
        return this;
    }
    
    public Class<? extends Page> getUnauthorizedPage()
    {
        return unauthorizedPage;
    }
    
    /**
     * Set the bookmarkable page that will be displayed when an <em>authenticated</em> user
     * attempts to access a page that they are not allowed to see.
     * 
     * @return {@code this} to allow chaining
     */
    public ShiroWicketPlugin setUnauthorizedPage(Class<? extends Page> page)
    {
        this.unauthorizedPage = page;
        return this;
    }
    
    public String getLoginPath()
    {
        return loginPath;
    }

    public String getLogoutPath()
    {
        return logoutPath;
    }

    /**
     * Set the path where the logout action is mounted. The default is {@code /logout}.
     * May not be {@code null}. The mounting occurs
     * when {@link #install install()} is invoked. Changing this value after calling
     * {@code install()} will therefore have no effect.
     * 
     * @return {@code this} to allow chaining
     */
    public ShiroWicketPlugin setLogoutPath(String logoutPath)
    {
        this.logoutPath = logoutPath;
        return this;
    }
    
    /**
     * Installs this {@code ShiroWicketPlugin} by doing the following:
     * <ul>
     * <li>Sets itself as the {@link IAuthorizationStrategy}</li>
     * <li>And as the {@link IUnauthorizedComponentInstantiationListener}</li>
     * <li>And as a {@link IRequestCycleListener}</li>
     * <li>Mounts the login page</li>
     * <li>Mounts the logout action</li>
     * </ul>
     */
    public void install(WebApplication app)
    {
        ISecuritySettings settings = app.getSecuritySettings();
        settings.setAuthorizationStrategy(this);
        settings.setUnauthorizedComponentInstantiationListener(this);
        app.getRequestCycleListeners().add(this);
        
        // Mount bookmarkable URLs
        if(this.loginPath != null)
        {
            app.mount(new MountedMapper(this.loginPath, this.loginPage));
        }
        app.mount(new MountMapper(this.logoutPath, LogoutRequestHandler.INSTANCE));
        
        // Install self in app metadata so that static get() can work
        app.setMetaData(PLUGIN_KEY, this);
    }
    
    // Start IRequestCycleListener methods -----------------------------------
    
    /**
     * React to an uncaught Exception by redirecting the browser to
     * the unauthorized page or login page if appropriate. This method will automatically be
     * called by Wicket if this plugin was installed by the standard {@link #install install()}
     * mechanism, via the {@link IRequestCycleListener} system.
     * This allows uncaught Shiro exceptions thrown by the backend to be
     * handled gracefully by the Wicket layer.
     * <p>
     * If the exception is a Shiro {@link AuthorizationException}, redirect
     * to the unauthorized page or login page depending on the type of error.
     * If the exception is not a Shiro {@link AuthorizationException}
     * return {@code null}.
     * 
     * @param cycle The current request cycle, as provided by Wicket.
     * 
     * @param error The exception to handle. If it is not a subclass of
     *              Shiro's {@link AuthorizationException}, this method will
     *              not have any effect.
     * 
     * @return A {@link RenderPageRequestHandler} redirect to the login
     *         page if the error is due to the user being
     *         <em>unauthenticated</em>;
     *         {@link RenderPageRequestHandler} to render the unauthorized page
     *         if the error is due to the user being
     *         <em>unauthorized</em>.
     */
    @Override
    public IRequestHandler onException(RequestCycle cycle, Exception error)
    {
        Class<? extends Page> respondWithPage = null;
        RedirectPolicy redirectPolicy = RedirectPolicy.NEVER_REDIRECT;
        
        // TODO: how to determine what page caused the exception?
        Component component = null;
        
        if(error instanceof AuthorizationException)
        {
            AuthorizationException ae = (AuthorizationException) error;
            if(authenticationNeeded(ae))
            {
                if(loginPage != null)
                {
                    Session.get().error(getLoginRequiredMessage(component));
                    // Create a RestartResponseAtInterceptPageException to set the intercept,
                    // even though we don't throw the exception. (The magic happens in the
                    // RestartResponseAtInterceptPageException constructor.)
                    new RestartResponseAtInterceptPageException(loginPage);
                    respondWithPage = loginPage;
                    redirectPolicy = RedirectPolicy.ALWAYS_REDIRECT;
                }
            }
            else if(unauthorizedPage != null)
            {
                if(cycle.getRequest() instanceof WebRequest &&
                   ((WebRequest) cycle.getRequest()).isAjax())
                {
                    redirectPolicy = RedirectPolicy.ALWAYS_REDIRECT;
                }
                respondWithPage = unauthorizedPage;
            }
        }
        if(respondWithPage != null)
        {
            return new RenderPageRequestHandler(new PageProvider(respondWithPage), redirectPolicy);
        }
        return null;
    }

    // End IRequestCycleListener methods -------------------------------------

    // Start IUnauthorizedComponentInstantiationListener methods -------------

    /**
     * Determine what caused the unauthorized instantiation of the given
     * component. If access was denied due to being unauthenticated, and
     * the login page specified in the constructor was not {@code null},
     * redirect to the login page. Place a localized error feedback message
     * in the Session using the key {@code loginRequired}.
     * <p>
     * Otherwise, access was denied due to authorization failure (e.g. insufficient privileges),
     * render the unauthorized error page.
     * 
     * @param component The component that failed to initialize due to 
     *                  authorization or authentication failure
     * 
     * @throws {@link ResetResponseException} to render the login page or unauthorized page
     * 
     * @throws UnauthorizedInstantiationException the login page or unauthorized page
     *                                            has not been configured (i.e. is {@code null})
     */
    public void onUnauthorizedInstantiation(Component component)
    {
        AuthorizationException cause;
        RequestCycle rc = RequestCycle.get();
        cause = rc.getMetaData(EXCEPTION_KEY);
        
        // Show appropriate login or error page if possible
        IRequestHandler handler = onException(rc, cause);
        if(handler != null)
        {
            throw new ResetResponseException(handler) {};
        }
        
        // Otherwise bubble up the error
        UnauthorizedInstantiationException ex;
        ex = new UnauthorizedInstantiationException(component.getClass());
        ex.initCause(cause);
        throw ex;
    }

    // End IUnauthorizedComponentInstantiationListener methods ---------------

    // Start IAuthorizationStrategy methods ----------------------------------
    
    /**
     * Performs authorization checks for the {@link Component#RENDER RENDER}
     * action only. Other actions are always allowed.
     * <p>
     * If the action is {@code RENDER}, the component class <em>and its
     * superclasses</em> are checked for the presence of
     * {@link org.apache.shiro.authz.annotation Shiro annotations}.
     * <p>
     * The absence of any Shiro annotation means that the component may be
     * rendered, and {@code true} is returned. Otherwise, each annotation is
     * evaluated against the current Shiro Subject. If any of the requirements
     * dictated by the annotations fail, {@code false} is returned and
     * rendering for the component will be skipped.
     * <p>
     * For example, this link will be hidden if the user is already
     * authenticated:
     * <pre class="example">
     * &#064;RequiresGuest
     * public class LoginLink extends StatelessLink
     * {
     *     ...
     * }</pre>
     */
    public boolean isActionAuthorized(Component component, Action action)
    {
        if(Component.RENDER.equals(action))
        {
            try
            {
                assertAuthorized(component.getClass());
            }
            catch(AuthorizationException ae)
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * If {@code componentClass} is a subclass of {@link Page},
     * return {@code true} or {@code false} based on evaluation of any
     * {@link org.apache.shiro.authz.annotation Shiro annotations}
     * that are present on the page class declaration, <em>plus any annotations
     * present on its superclasses</em>.
     * <p>
     * The absence of any Shiro annotation means that the page can always be
     * instantiated, meaning {@code true} will always be returned. Otherwise,
     * each annotation is evaluated against the current Shiro Subject. If any
     * of the requirements dictated by the annotations fail, {@code false} will
     * be returned and instantiation will be denied.
     * <p>
     * For example, this page may only be instantiated if the user has
     * explictly authenticated (i.e. not just "remembered" via cookie) and
     * additionally has the "admin" role:
     * <pre class="example">
     * &#064;RequiresAuthentication
     * &#064;RequiresRoles("admin")
     * public class TopSecretPage extends WebPage
     * {
     *     ...
     * }</pre>
     * If {@code componentClass} is not a subclass of Page, always return
     * {@code true}. Non-page components may always be instantiated; however
     * their rendering can be controlled via annotations. See
     * {@link #isActionAuthorized isActionAuthorized()}.
     */
    public <T extends IRequestableComponent> boolean isInstantiationAuthorized(
        Class<T> componentClass)
    {
        if(Page.class.isAssignableFrom(componentClass))
        {
            try
            {
                assertAuthorized(componentClass);
            }
            catch(AuthorizationException ae)
            {
                // Store exception for use later in the request by onUnauthorizedInstantiation()
                RequestCycle.get().setMetaData(EXCEPTION_KEY, ae);
                return false;
            }
        }
        return true;
    }

    // End IAuthorizationStrategy methods ------------------------------------

    /**
     * Returns the localized message for the {@code loginRequired} key.
     */
    protected String getLoginRequiredMessage(Component component)
    {
        return Application.get().getResourceSettings().getLocalizer().getString(
            LOGIN_REQUIRED_MESSAGE_KEY,
            component,
            null,
            "You need to be logged in to continue."
        );
    }
    
    /**
     * Returns {@code true} if the reason the user was denied access is
     * because she needs to authenticate.
     */
    protected boolean authenticationNeeded(AuthorizationException cause)
    {
        // IMPLEMENTATION NOTE - A simple solution would be:
        //   return ! SecurityUtils.getSubject().isAuthenticated();
        // In other words, the user needs to authenticate if she is not
        // already logged in (this is how it is done in Wicket auth-roles).
        // However this does not take into account the subtle difference in
        // Shiro between "authenticated" and "remembered" states. To ensure the
        // correct behavior we have to inspect the actual exception to see what
        // action to take.
        
        boolean needLogin = false;
        
        // Check if Shiro blocked access due to authentication
        if(cause instanceof UnauthenticatedException)
        {
            needLogin = true;
            
            // But... there is a rare case where Shiro can throw an
            // UnauthenticatedException even when the user is already logged
            // in. If the user is logged in and the page was annotated with
            // @RequiresGuest, Shiro throws an UnauthenticatedException, which
            // which is very misleading. Our only way to detect this scenario
            // is to parse the exception message. Yes, this is a hack.
            
            String msg = cause.getMessage();
            String guestError = "Attempting to perform a guest-only operation.";
            if(msg != null && msg.startsWith(guestError))
            {
                needLogin = false;
            }
        }
        return needLogin;
    }

    /**
     * @throws AuthorizationException if the given class, or any of its
     *         superclasses, has a Shiro annotation that fails its
     *         authorization check.
     */
    private void assertAuthorized(final Class<?> cls)
        throws AuthorizationException
    {
        Collection<Annotation> annotations = findAnnotations(cls);
        for(Annotation annot : annotations)
        {
            for(AuthorizingAnnotationHandler h : HANDLERS)
            {
                h.assertAuthorized(annot);
            }
        }
    }
    
    /**
     * Returns all annotations present on the given class and all of its
     * superclasses.
     */
    private Collection<Annotation> findAnnotations(final Class<?> cls)
    {
        List<Annotation> annots = new ArrayList<Annotation>(5);
        Class<?> currClass = cls;
        while(currClass != null)
        {
            annots.addAll(Arrays.asList(currClass.getDeclaredAnnotations()));
            currClass = currClass.getSuperclass();
        }
        return annots;
    }
}
