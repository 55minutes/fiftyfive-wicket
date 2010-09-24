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

package fiftyfive.wicket.util;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import fiftyfive.util.Assert;
import fiftyfive.wicket.FoundationApplication;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.RequestLogger.ISessionLogInfo;
import org.apache.wicket.protocol.http.RequestLogger.SessionData;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.component.IBookmarkablePageRequestTarget;
import org.apache.wicket.request.target.component.IPageRequestTarget;
import org.apache.wicket.request.target.component.listener.IListenerInterfaceRequestTarget;
import org.apache.wicket.request.target.resource.ISharedResourceRequestTarget;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for logging detailed information about Wicket's internal
 * state for troubleshooting purposes. Most of these methods work only when
 * called within a Wicket thread.
 * 
 * @since 2.0
 */
public class LoggingUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        LoggingUtils.class
    );
    
    /** Human-readable descriptions of Wicket's request listener types. */
    private static final Map<String,String> LISTENER_DESCRIPTIONS;
    
    static
    {
        LISTENER_DESCRIPTIONS = new HashMap<String,String>();
        LISTENER_DESCRIPTIONS.put(
            "IFormSubmitListener",
            "Processing Form Submission");
        LISTENER_DESCRIPTIONS.put(
            "ILinkListener",
            "Processing Link Click"
        );
        LISTENER_DESCRIPTIONS.put(
            "IOnChangeListener",
            "Processing Form Element OnChange Event"
        );
    }
    
    /**
     * Writes to the logger a best-guess at the most concise description of
     * the exception by first unwrapping it (see {@link #unwrap upwrap}), then
     * writes a large amount of information regarding the current Wicket state
     * (see {@link #dumpWicketState dumpWicketState}), and finally writes the
     * full stack traces of the entire exception chain.
     * <p>
     * Example logger output:
     * <pre class="example">
     * IllegalStateException: Attempt to set model object on null model of component: form:calendar-control:date-text-field
     * 
     * Request:
     *   URL      = /form?wicket:interface=:0:form::IFormSubmitListener::
     *   Step     = Processing Form Submission
     *   Target   = FormTestPage > Form [form]
     *   Duration = 9 milliseconds
     * Session:
     *   ID       = wvz6xha0fvdzte1x36zzbz4a
     *   Info     = Custom session information
     *   Duration = 16 seconds
     *   Size     = 13.5K
     * Application:
     *   Active Sessions = 1 (6 peak)
     *   Memory Usage    = 36M used, 29M free, 533M max
     *   IP Address      = 172.16.1.14
     *   Uptime          = 33.5 seconds
     * Headers:
     *   Host            = localhost:8080
     *   User-Agent      = Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-us) AppleWebKit/533.18.1 (KHTML, like Gecko) Version/5.0.2 Safari/533.18.5
     *   Accept          = application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*&#047;*;q=0.5
     *   Referer         = http://localhost:8080/form?initialMonth=10.2007&startDate=10.01.2007&endDate=11.10.2007
     *   Accept-Language = en-us
     *   Accept-Encoding = gzip, deflate
     *   Cookie          = JSESSIONID=wvz6xha0fvdzte1x36zzbz4a
     *   Connection      = keep-alive
     *   Origin          = http://localhost:8080
     *   Content-Type    = application/x-www-form-urlencoded
     *   Content-Length  = 47
     * 
     * WicketMessage: Method onFormSubmitted of interface org.apache.wicket.markup.html.form.IFormSubmitListener targeted at component [MarkupContainer [Component id = form]] threw an exception
     * 
     * Root cause:
     * 
     * java.lang.IllegalStateException: Attempt to set model object on null model of component: form:calendar-control:date-text-field
     *      at org.apache.wicket.Component.setDefaultModelObject(Component.java:3111)
     *      at org.apache.wicket.markup.html.form.FormComponent.updateModel(FormComponent.java:1168)
     *      at org.apache.wicket.markup.html.form.Form$FormModelUpdateVisitor.component(Form.java:229)
     *      at org.apache.wicket.markup.html.form.FormComponent.visitComponentsPostOrderHelper(FormComponent.java:514)
     *      at org.apache.wicket.markup.html.form.FormComponent.visitComponentsPostOrderHelper(FormComponent.java:493)
     *      at org.apache.wicket.markup.html.form.FormComponent.visitComponentsPostOrderHelper(FormComponent.java:493)
     *      at org.apache.wicket.markup.html.form.FormComponent.visitComponentsPostOrder(FormComponent.java:465)
     *      at org.apache.wicket.markup.html.form.Form.internalUpdateFormComponentModels(Form.java:2110)
     *      at org.apache.wicket.markup.html.form.Form.updateFormComponentModels(Form.java:2078)
     *      at org.apache.wicket.markup.html.form.Form.process(Form.java:1028)
     *      at org.apache.wicket.markup.html.form.Form.process(Form.java:955)
     *      at org.apache.wicket.markup.html.form.Form.onFormSubmitted(Form.java:920)
     *      at java.lang.reflect.Method.invoke(Method.java:597)
     *      at org.apache.wicket.RequestListenerInterface.invoke(RequestListenerInterface.java:182)
     *      at org.apache.wicket.request.target.component.listener.ListenerInterfaceRequestTarget.processEvents(ListenerInterfaceRequestTarget.java:73)
     *      at org.apache.wicket.request.AbstractRequestCycleProcessor.processEvents(AbstractRequestCycleProcessor.java:92)
     *      at org.apache.wicket.RequestCycle.processEventsAndRespond(RequestCycle.java:1250)
     *      at org.apache.wicket.RequestCycle.step(RequestCycle.java:1329)
     *      at org.apache.wicket.RequestCycle.steps(RequestCycle.java:1436)
     *      at org.apache.wicket.RequestCycle.request(RequestCycle.java:545)
     *      at org.apache.wicket.protocol.http.WicketFilter.doGet(WicketFilter.java:484)
     *      at org.apache.wicket.protocol.http.WicketFilter.doFilter(WicketFilter.java:317)
     *      at org.mortbay.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1157)
     *      at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:83)
     *      at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:76)
     *      at org.mortbay.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1157)
     *      at org.mortbay.jetty.servlet.ServletHandler.handle(ServletHandler.java:388)
     *      at org.mortbay.jetty.security.SecurityHandler.handle(SecurityHandler.java:216)
     *      at org.mortbay.jetty.servlet.SessionHandler.handle(SessionHandler.java:182)
     *      at org.mortbay.jetty.handler.ContextHandler.handle(ContextHandler.java:765)
     *      at org.mortbay.jetty.webapp.WebAppContext.handle(WebAppContext.java:440)
     *      at org.mortbay.jetty.handler.ContextHandlerCollection.handle(ContextHandlerCollection.java:230)
     *      at org.mortbay.jetty.handler.HandlerCollection.handle(HandlerCollection.java:114)
     *      at org.mortbay.jetty.handler.HandlerWrapper.handle(HandlerWrapper.java:152)
     *      at org.mortbay.jetty.Server.handle(Server.java:326)
     *      at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:542)
     *      at org.mortbay.jetty.HttpConnection$RequestHandler.content(HttpConnection.java:943)
     *      at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:756)
     *      at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:218)
     *      at org.mortbay.jetty.HttpConnection.handle(HttpConnection.java:404)
     *      at org.mortbay.io.nio.SelectChannelEndPoint.run(SelectChannelEndPoint.java:410)
     *      at org.mortbay.thread.QueuedThreadPool$PoolThread.run(QueuedThreadPool.java:582)
     * 
     * 
     * Complete stack:
     * 
     * org.apache.wicket.WicketRuntimeException: Method onFormSubmitted of interface org.apache.wicket.markup.html.form.IFormSubmitListener targeted at component [MarkupContainer [Component id = form]] threw an exception
     *      at org.apache.wicket.RequestListenerInterface.invoke(RequestListenerInterface.java:193)
     *      at org.apache.wicket.request.target.component.listener.ListenerInterfaceRequestTarget.processEvents(ListenerInterfaceRequestTarget.java:73)
     *      at org.apache.wicket.request.AbstractRequestCycleProcessor.processEvents(AbstractRequestCycleProcessor.java:92)
     *      at org.apache.wicket.RequestCycle.processEventsAndRespond(RequestCycle.java:1250)
     *      at org.apache.wicket.RequestCycle.step(RequestCycle.java:1329)
     *      at org.apache.wicket.RequestCycle.steps(RequestCycle.java:1436)
     *      at org.apache.wicket.RequestCycle.request(RequestCycle.java:545)
     *      at org.apache.wicket.protocol.http.WicketFilter.doGet(WicketFilter.java:484)
     * 
     * java.lang.reflect.InvocationTargetException
     *      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
     *      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
     *      at java.lang.reflect.Method.invoke(Method.java:597)
     *      at org.apache.wicket.RequestListenerInterface.invoke(RequestListenerInterface.java:182)
     *      at org.apache.wicket.request.target.component.listener.ListenerInterfaceRequestTarget.processEvents(ListenerInterfaceRequestTarget.java:73)
     *      at org.apache.wicket.request.AbstractRequestCycleProcessor.processEvents(AbstractRequestCycleProcessor.java:92)
     *      at org.apache.wicket.RequestCycle.processEventsAndRespond(RequestCycle.java:1250)
     *      at org.apache.wicket.RequestCycle.step(RequestCycle.java:1329)
     *      at org.apache.wicket.RequestCycle.steps(RequestCycle.java:1436)
     *      at org.apache.wicket.RequestCycle.request(RequestCycle.java:545)
     *      at org.apache.wicket.protocol.http.WicketFilter.doGet(WicketFilter.java:484)</pre>
     * 
     */
    public static void logRuntimeException(Logger logger, RuntimeException e)
    {
        Assert.notNull(logger, "logger cannot be null");
        Assert.notNull(e, "exception cannot be null");
        
        try
        {
            Throwable unwrapped = unwrap(e);
        
            logger.error(String.format(
                "%s: %s%n%n%s%n%n%s",
                Classes.simpleName(unwrapped.getClass()),
                unwrapped.getMessage(),
                dumpWicketState(),
                Strings.toString(e)
            ));
        }
        catch(Exception loggingEx)
        {
            // We should never arrive here, because it means that something
            // went terribly wrong in our logging code. Since our code failed,
            // fall back to simple logging so that Wicket error handling
            // can continue uninterrupted.
            LOGGER.error("Unexpected exception during logging", loggingEx);
            logger.error("RuntimeException", e);
        }
    }
    
    /**
     * Attempts to find the most meaningful exception in a runtime exception
     * chain by stripping away the exceptions commonly used as "wrappers",
     * namely: RuntimeException, WicketRuntimeException,
     * InvocationTargetException and ExecutionException. These four exception
     * types are usually language cruft that don't add much desciptive value.
     * <p>
     * For example, if the exception chain is:
     * <pre class="example">
     * WicketRuntimeException
     * -> InvocationTargetException
     *    -> MyBusinessException
     *       -> SQLException</pre>
     * 
     * Then the unwrapped exception would be {@code MyBusinessException}. 
     */
    public static Throwable unwrap(RuntimeException e)
    {
        Assert.notNull(e);
        
        Throwable unwrapped = e;
        while(true)
        {
            Throwable cause = null;
            
            if(unwrapped instanceof WicketRuntimeException ||
               unwrapped instanceof InvocationTargetException ||
               unwrapped instanceof ExecutionException ||
               unwrapped.getClass().equals(RuntimeException.class))
            {
                cause = unwrapped.getCause();
            }
            if(null == cause || unwrapped == cause)
            {
                break;
            }
            unwrapped = cause;
        }
        return unwrapped;
    }
    
    /**
     * Returns a multi-line string that describes the current Wicket request
     * cycle in great detail. Example output:
     * <pre class="example">
     * Request:
     *   URL      = /form?wicket:interface=:0:form::IFormSubmitListener::
     *   Step     = Processing Form Submission
     *   Target   = FormTestPage > Form [form]
     *   Duration = 9 milliseconds
     * Session:
     *   ID       = wvz6xha0fvdzte1x36zzbz4a
     *   Info     = Custom session information
     *   Duration = 16 seconds
     *   Size     = 13.5K
     * Application:
     *   Active Sessions = 1 (6 peak)
     *   Memory Usage    = 36M used, 29M free, 533M max
     *   IP Address      = 172.16.1.14
     *   Uptime          = 33.5 seconds
     * Headers:
     *   Host            = localhost:8080
     *   User-Agent      = Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-us) AppleWebKit/533.18.1 (KHTML, like Gecko) Version/5.0.2 Safari/533.18.5
     *   Accept          = application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*&#047;*;q=0.5
     *   Referer         = http://localhost:8080/form?initialMonth=10.2007&startDate=10.01.2007&endDate=11.10.2007
     *   Accept-Language = en-us
     *   Accept-Encoding = gzip, deflate
     *   Cookie          = JSESSIONID=wvz6xha0fvdzte1x36zzbz4a
     *   Connection      = keep-alive
     *   Origin          = http://localhost:8080
     *   Content-Type    = application/x-www-form-urlencoded
     *   Content-Length  = 47</pre>
     * 
     * Note that session duration and application active sessions are only
     * available if Wicket's request logging facility is enabled.
     */
    public static String dumpWicketState()
    {
        return String.format(
            "Request:%n%s%nSession:%n%s%nApplication:%n%s%nHeaders:%n%s",
            formatMapEntries(getRequestInfo().entrySet(), "  "),
            formatMapEntries(getSessionInfo().entrySet(), "  "),
            formatMapEntries(getApplicationInfo().entrySet(), "  "),
            formatMapEntries(HttpUtils.getHeaders(), "  ")
        );
    }
    
    /**
     * Returns a Map with information associated with the following keys:
     * <ul>
     * <li>{@code URL}</li>
     * <li>{@code Step}</li>
     * <li>{@code Target}</li>
     * <li>{@code Duration}</li>
     * </ul>
     */
    public static Map<String,Object> getRequestInfo()
    {
        Map<String,Object> info = new LinkedHashMap<String,Object>();
        info.put("URL", HttpUtils.getRelativeRequestUrl());
        info.put("Step", describeRequestStep());
        info.put("Target", describeRequestTarget());
        info.put("Duration", getRequestDuration());
        return info;
    }
    
    /**
     * Returns a Map with information associated with the following keys:
     * <ul>
     * <li>{@code ID}</li>
     * <li>{@code Info} (if session implements {@link ISessionLogInfo})</li>
     * <li>{@code Size}</li>
     * <li>{@code Duration} (if {@link IRequestLogger} is enabled)</li>
     * </ul>
     */
    public static Map<String,Object> getSessionInfo()
    {
        Session sess = Session.get();
        Object detail = "--ISessionLogInfo not implemented--";
        
        if(sess instanceof ISessionLogInfo)
        {
            detail = ((ISessionLogInfo) sess).getSessionInfo();
        }
        
        Map<String,Object> info = new LinkedHashMap<String,Object>();
        if(sess != null)
        {
            info.put("ID", sess.getId());
            info.put("Info", detail);
            info.put("Size", Bytes.bytes(sess.getSizeInBytes()));

            Duration dur = getSessionDuration();
            if(dur != null)
            {
                info.put("Duration", getSessionDuration());
            }
        }
        return info;
    }
    
    /**
     * Returns a Map with information associated with the following keys:
     * <ul>
     * <li>{@code Active Sessions} (if {@link IRequestLogger} is enabled)</li>
     * <li>{@code Memory Usage}</li>
     * <li>{@code IP Address}</li>
     * <li>{@code Uptime} (if app is a {@link FoundationApplication})</li>
     * </ul>
     */
    public static Map<String,Object> getApplicationInfo()
    {
        Map<String,Object> info = new LinkedHashMap<String,Object>();
        
        String active = describeActiveSessions();
        if(active != null)
        {
            info.put("Active Sessions", active);
        }
        info.put("Memory Usage", describeMemoryUsage());
        info.put("IP Address", getHostIpAddress());
        
        Application app = Application.get();
        if(app instanceof FoundationApplication)
        {
            info.put("Uptime", ((FoundationApplication) app).getUptime());
        }
        return info;
    }
    
    /**
     * Returns a human-readable description of the task currently being
     * performed by the Wicket framework. The result will be one of these
     * strings:
     * <ul>
     * <li>{@code Processing Form Submission}</li>
     * <li>{@code Processing Link Click}</li>
     * <li>{@code Processing Form Element OnChange Event}</li>
     * <li>{@code Processing Ajax Event}</li>
     * <li>{@code Rendering Ajax Response}</li>
     * <li>{@code Rendering Stateful Page}</li>
     * <li>{@code Rendering Bookmarkable Page}</li>
     * <li>{@code Serving Shared Resource}</li>
     * <li>Class name of the current {@link IRequestTarget}, if a description
     *     is not available.</li>
     * </ul>
     */
    public static String describeRequestStep()
    {
        RequestCycle cycle = RequestCycle.get();
        IRequestTarget target = cycle.getRequestTarget();
        Response resp = cycle.getResponse();
        
        String desc = null;
        
        if(resp instanceof WebResponse && ((WebResponse) resp).isAjax())
        {
            desc = "Rendering Ajax Response";
        }
        else if(AjaxRequestTarget.get() != null)
        {
            desc = "Processing Ajax Event";
        }
        else if(target instanceof ISharedResourceRequestTarget)
        {
            desc = "Serving Shared Resource";
        }
        else if(target instanceof IListenerInterfaceRequestTarget)
        {
            IListenerInterfaceRequestTarget listener;
            listener = (IListenerInterfaceRequestTarget) target;
            String name = listener.getRequestListenerInterface().getName();
            if(LISTENER_DESCRIPTIONS.containsKey(name))
            {
                desc = LISTENER_DESCRIPTIONS.get(name);
            }
            else
            {
                desc = String.format(
                    "%s#%s",
                    name,
                    listener.getRequestListenerInterface().getMethod().getName()
                );
            }
        }
        else if(target instanceof IPageRequestTarget)
        {
            desc = "Rendering Stateful Page";
        }
        else if(target instanceof IBookmarkablePageRequestTarget)
        {
            desc = "Rendering Bookmarkable Page";
        }
        else if(target != null)
        {
            desc = Classes.simpleName(target.getClass());
        }
        
        return desc;
    }
    
    /**
     * Returns a description of component or page that is currently being
     * processed by the Wicket framework. If a component is being executed, the
     * description will be in the form:
     * <pre class="example">
     * PageClass &gt; ComponentClass (superclass if component is anonymous) [wicket:id path]</pre>
     * 
     * Example:
     * <pre class="example">
     * MyPage &gt; MyPanel$1 (Link) [path:to:link]</pre>
     * 
     * If a page is being rendered, the description will be the page class.
     */
    public static String describeRequestTarget()
    {
        RequestCycle cycle = RequestCycle.get();
        IRequestTarget target = cycle.getRequestTarget();
        String desc = null;
        
        if(target instanceof IListenerInterfaceRequestTarget)
        {
            String classDesc = null;
            Component comp;
            comp = ((IListenerInterfaceRequestTarget) target).getTarget();
            
            if(comp != null)
            {
                if(comp.getClass().isAnonymousClass())
                {
                    classDesc = String.format(
                        "%s (%s)",
                        Classes.simpleName(comp.getClass()),
                        Classes.simpleName(comp.getClass().getSuperclass())
                    );
                }
                else
                {
                    classDesc = Classes.simpleName(comp.getClass());
                }
                
                Class pageClass = null;
                if(comp.getPage() != null)
                {
                    pageClass = comp.getPage().getClass();
                }
            
                desc = String.format(
                    "%s > %s [%s]",
                    Classes.simpleName(pageClass),
                    classDesc,
                    comp.getPageRelativePath()
                );
            }
        }
        else
        {
            Class<?> page = null;
            if(target instanceof IPageRequestTarget)
            {
                page = ((IPageRequestTarget) target).getPage().getClass();
            }
            else if(target instanceof IBookmarkablePageRequestTarget)
            {
                page = ((IBookmarkablePageRequestTarget) target).getPageClass();
            }
            else
            {
                Page obj = cycle.getResponsePage();
                if(null == obj)
                {
                    obj = cycle.getRequest().getPage();
                }
                page = obj != null ? obj.getClass() : null;
            }
            desc = null == page ? null : Classes.simpleName(page);
        }
        
        return desc;
    }
    
    /**
     * Returns the amount of time the current request has taken up until
     * this point.
     */
    public static Duration getRequestDuration()
    {
        Time start = Time.valueOf(RequestCycle.get().getStartTime());
        return Duration.elapsed(start);
    }
    
    /**
     * Returns the amount of time the currently session has been active.
     * Depends on Wicket's {@link IRequestLogger} being enabled. If it is not,
     * returns {@code null}.
     */
    public static Duration getSessionDuration()
    {
        Date start = null;
        Session currSession = Session.get();
        IRequestLogger log = Application.get().getRequestLogger();
        
        if(log != null && currSession != null && currSession.getId() != null)
        {
            String sessionId = currSession.getId();
            SessionData[] sessions = log.getLiveSessions();
            if(sessions != null)
            {
                for(SessionData sess : sessions)
                {
                    if(sessionId.equals(sess.getSessionId()))
                    {
                        start = sess.getStartDate();
                        break;
                    }
                }
            }
        }
        return nullSafeElapsed(start);
    }
    
    /**
     * Returns the IP address hosting this JVM, or {@code null} if it could
     * not be determined.
     */
    public static String getHostIpAddress()
    {
        try
        {
            InetAddress host = InetAddress.getLocalHost();
            return host.getHostAddress();
        }
        catch (Exception ex)
        {
            // ignore
        }
        return null;
    }
    
    /**
     * Returns a string that describes the JVM's memory conditions in this
     * format: {@code 36M used, 29M free, 533M max}.
     */
    public static String describeMemoryUsage()
    {
        Runtime runtime = Runtime.getRuntime();
        long free = runtime.freeMemory();
        
        return String.format(
            "%sM used, %sM free, %sM max",
            (runtime.totalMemory() - free) / 1000000,
            free / 1000000,
            runtime.maxMemory() / 1000000
        );
    }
    
    /**
     * Returns a string that describes the active sessions in this format:
     * {@code 5 (16 peak)}. This information comes from the application's
     * {@link IRequestLogger}. If the logger is not enabled, returns
     * {@code null}.
     */
    public static String describeActiveSessions()
    {
        IRequestLogger log = Application.get().getRequestLogger();
        if(null == log) return null;
        
        SessionData[] sessions = log.getLiveSessions();
        if(null == sessions) return null;
        
        return String.format(
            "%d (%d peak)",
            sessions.length,
            log.getPeakSessions()
        );
    }
    
    /**
     * Returns a Duration of time elapsed since the specified date, or
     * {@code null} or the date is {@code null}.
     */
    private static Duration nullSafeElapsed(Date since)
    {
        return null == since ? null : Duration.elapsed(Time.valueOf(since));
    }

    /**
     * Formats a Map with on entry per line plus a specified indent.
     * Keys are padded and left-aligned so that they are all the same width.
     */
    private static String formatMapEntries(Collection entries,
                                          String indent)
    {
        StringBuffer buf = new StringBuffer();
        int width = 1;
        
        for(Map.Entry e : (Collection<Map.Entry>) entries)
        {
            int length = e.getKey().toString().length();
            if(length > width)
            {
                width = length;
            }
        }
        for(Map.Entry e : (Collection<Map.Entry>) entries)
        {
            if(buf.length() > 0)
            {
                buf.append(String.format("%n"));
            }
            buf.append(String.format(
                "%s%-" + width + "s = %s",
                indent,
                e.getKey(),
                e.getValue() == null ? "N/A" : e.getValue()
            ));
        }
        return buf.toString();
    }

    /**
     * Not meant to be instantiated.
     */
    private LoggingUtils()
    {
        super();
    }
}
