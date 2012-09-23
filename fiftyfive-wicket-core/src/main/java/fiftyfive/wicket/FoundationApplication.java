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
package fiftyfive.wicket;

import java.util.Date;

import fiftyfive.util.Version;

import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.response.filter.AjaxServerAndClientTimeFilter;
import org.apache.wicket.util.file.Path;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;

/**
 * Useful base class for Wicket applications that implements best practices
 * for Wicket configuration.
 * <ul>
 * <li>Provides a {@link #getStartupDate} method</li>
 * <li>Exposes version and build timestamp information</li>
 * <li>Removes Wicket tags, wicket:id attributes, and other cruft from
 *     generated markup to ensure XHTML compliance</li>
 * <li>Enables Wicket's request logging facility if an appropriate SLF4J
 *     logger is configured</li>
 * <li>In development mode, enable automatic reloading of HTML templates from
 *     the source code directory ("hot deploy")</li>
 * <li>In development mode, turn off the ajax debugger (it is a browser
 *     memory hog)</li>
 * </ul>
 *
 * @author Matt Brictson
 * @see #init
 */
public abstract class FoundationApplication extends WebApplication
{
    private Date    startupDate;
    private Version version;
    
    public FoundationApplication()
    {
        super();
    }
    
    /**
     * Returns the version information for this application, including the
     * version number and the date and time when it was compiled.
     */
    public Version getVersion()
    {
        return this.version;
    }
    
    /**
     * Returns the date and time when this application was initialized
     * by the Wicket framework.
     */
    public Date getStartupDate()
    {
        return this.startupDate;
    }
    
    /**
     * Returns the amount of time elapsed since this application was
     * initialized by the Wicket framework.
     * 
     * @since 2.0
     */
    public Duration getUptime()
    {
        Date start = getStartupDate();
        if(null == start)
        {
            return Duration.milliseconds(0);
        }
        return Duration.elapsed(Time.valueOf(start));
    }
    
    /**
     * Helper method that returns true if {@link #getConfigurationType}
     * is equal to DEVELOPMENT.
     * 
     * @deprecated In Wicket 1.5, this has been superseded by
     *             {@link #usesDevelopmentConfig}.
     */
    public boolean isDevelopmentMode()
    {
        return usesDevelopmentConfig();
    }
    
    /**
     * Initializes the application using best practices for Wicket
     * application development and deployment.
     * <ol>
     * <li>Sets the startupDate property to the current time.</li>
     * <li>Calls {@link WebApplication#init super.init()}.</li>
     * <li>Executes the following regardless of configuration mode:<ul>
     *   <li>{@link #initVersionInformation}</li>
     *   <li>{@link #initCleanMarkup}</li>
     *   <li>{@link #initResources}</li>
     *   <li>{@link #initRequestLogger}</li></ul></li>
     * <li>Executes the following only if the application is in
     *     DEVELOPMENT mode:<ul>
     *   <li>{@link #initHtmlHotDeploy}</li>
     *   <li>{@link #initDebugInformation}</li></ul></li>
     * </ol>
     */
    @Override
    protected void init()
    {
        this.startupDate = new Date();

        super.init();

        initVersionInformation();
        initCleanMarkup();
        initResources();
        initRequestLogger();
        
        if(usesDevelopmentConfig())
        {
            initHtmlHotDeploy();
            initDebugInformation();
        }
    }
    
    /**
     * Enables automatic reloading of HTML templates from your source code
     * directory. This means that whenever you modify an HTML file the
     * changes will appear immediately in your browser without needing to
     * redeploy. However you will still need to redeploy if you recompile
     * a Java class.
     * <p>
     * This method assumes that you are running the webapp "in place"; for
     * example using <code>mvn jetty:run</code>. It
     * will not work if you are running from a WAR or exploded WAR.
     * <p>
     * You must also be using the
     * standard maven project structure for your Wicket project, like so:
     * <pre class="example">
     * pom.xml
     * src/
     *   main/
     *     java/
     *     resources/
     *     webapp/</pre>
     *
     * @see <a href="http://docs.codehaus.org/display/JETTY/Maven+Jetty+Plugin">http://docs.codehaus.org/display/JETTY/Maven+Jetty+Plugin</a>
     */
    protected void initHtmlHotDeploy()
    {
        getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
        String htmlDir = getServletContext().getRealPath("/");
        if(htmlDir != null && !htmlDir.endsWith("/"))
        {
            htmlDir += "/";
        } 
        getResourceSettings().getResourceFinders().add(new Path(htmlDir + "../java"));
        getResourceSettings().getResourceFinders().add(new Path(htmlDir + "../resources"));
    }
    
    /**
     * Turns off Wicket's non-standard markup and sets the default markup
     * encoding to UTF-8. By default, Wicket will add markup to disabled
     * links; for example, it may wrap them in an &lt;em&gt; element. We
     * prefer to control the markup and don't want Wicket adding elements,
     * so we turn this off. We also instruct Wicket to strip the wicket:id
     * attributes and wicket: elements from the markup. This ensures that
     * the markup will be valid (X)HTML.
     */
    protected void initCleanMarkup()
    {
        getMarkupSettings().setDefaultBeforeDisabledLink(null);
        getMarkupSettings().setDefaultAfterDisabledLink(null);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getMarkupSettings().setStripWicketTags(true);
    }
    
    /**
     * Disables ajaxDebugMode (the popup
     * panel in the browser that shows ajax request and response details).
     * It is disabled because it is a memory hog that can bog down the
     * browser. Finally, enables emitting HTML comments that show which Wicket
     * class created each section of HTML.
     */
    protected void initDebugInformation()
    {
        getRequestCycleSettings().addResponseFilter(
            new AjaxServerAndClientTimeFilter()
        );
        getDebugSettings().setOutputMarkupContainerClassName(true);
        // Turn this off explicity because it causes performance problems
        getDebugSettings().setAjaxDebugModeEnabled(false);
    }
    
    /**
     * Loads the version information from the manifest of this web
     * application. To access the version information, use {@link #getVersion}.
     *
     * @see Version
     */
    protected void initVersionInformation()
    {
        this.version = Version.ofWebapp(getServletContext());
    }
    
    /**
     * Set the cache duration for resources to zero if in development mode
     * (discouraging browser cache), or 1 year if in deployment mode.
     */
    protected void initResources()
    {
        getResourceSettings().setDefaultCacheDuration(
            usesDevelopmentConfig() ? Duration.NONE : Duration.days(365)
        );
    }
    
    /**
     * Enables Wicket's request logging facility if an SLF4J logger is
     * configured for {@code INFO} with the category
     * {@code org.apache.wicket.protocol.http.RequestLogger}. For example,
     * if using log4j properties configuration, this would cause the Wicket
     * request logger to be enabled:
     * <pre class="example">
     * log4j.logger.org.apache.wicket.protocol.http.RequestLogger = INFO</pre>
     * 
     * @since 2.0
     */
    protected void initRequestLogger()
    {
        Logger log = LoggerFactory.getLogger(RequestLogger.class);
        if(log.isInfoEnabled())
        {
            getRequestLoggerSettings().setRequestLoggerEnabled(true);
        }
    }
}
