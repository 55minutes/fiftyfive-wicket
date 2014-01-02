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
package fiftyfive.wicket.util;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import fiftyfive.wicket.FoundationApplication;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;

import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.IRequestLogger.ISessionLogInfo;
import org.apache.wicket.protocol.http.IRequestLogger.SessionData;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.IComponentRequestHandler;
import org.apache.wicket.request.handler.IPageClassRequestHandler;

import org.apache.wicket.util.lang.Args;
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
     * ParseException: Unparseable date: "1xxx07"
     *
     * Request:
     *   URL       = wicket/bookmarkable/fiftyfive.wicket.examples.formtest.FormTestPage?0&initialMonth=10.2007&startDate=1xxx07&endDate=11.10.2007
     *   Handler   = RenderPageRequestHandler
     *   Component = FormTestPage
     *   Duration  = 485 milliseconds
     * Session:
     *   ID       = 1sxy938y7qoqq942z4pnuqdqt
     *   Info     = TODO: Your session info goes here
     *   Size     = 716 bytes
     *   Duration = 310 milliseconds
     * Application:
     *   Active Sessions = 1 (1 peak)
     *   Memory Usage    = 25M used, 40M free, 533M max
     *   IP Address      = 172.16.1.14
     *   Uptime          = 10.9 seconds
     * Headers:
     *   Host            = localhost:8080
     *   User-Agent      = Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-us) AppleWebKit/533.18.1 (KHTML, like Gecko) Version/5.0.2 Safari/533.18.5
     *   Accept          = application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,&#042;/&#042;;q=0.5
     *   Cache-Control   = max-age=0
     *   Accept-Language = en-us
     *   Accept-Encoding = gzip, deflate
     *   Cookie          = JSESSIONID=1217fv4qjpnbnv1ewzzmonevh
     *   Connection      = keep-alive
     * 
     * Message: Error calling method: public java.util.Date fiftyfive.wicket.examples.formtest.FormTestPage$1.getStartDate() on object: [ [Component id = calendar-control]]
     * 
     * Root cause:
     * 
     * java.text.ParseException: Unparseable date: "1xxx07"
     *      at java.text.DateFormat.parse(DateFormat.java:337)
     *      at fiftyfive.wicket.examples.formtest.FormTestPage$1.getStartDate(FormTestPage.java:82)
     *      at java.lang.reflect.Method.invoke(Method.java:597)
     *      at org.apache.wicket.util.lang.PropertyResolver$MethodGetAndSet.getValue(PropertyResolver.java:1112)
     *      at org.apache.wicket.util.lang.PropertyResolver$ObjectAndGetSetter.getValue(PropertyResolver.java:637)
     *      at org.apache.wicket.util.lang.PropertyResolver.getValue(PropertyResolver.java:96)
     *      at org.apache.wicket.model.AbstractPropertyModel.getObject(AbstractPropertyModel.java:122)
     *      at fiftyfive.wicket.datetime.RestrictedDatePicker.configure(RestrictedDatePicker.java:168)
     *      at org.apache.wicket.extensions.yui.calendar.DatePicker.renderHead(DatePicker.java:260)
     *      at org.apache.wicket.Component.renderHead(Component.java:2627)
     *      at org.apache.wicket.markup.renderStrategy.ParentFirstHeaderRenderStrategy$1.component(ParentFirstHeaderRenderStrategy.java:70)
     *      at org.apache.wicket.markup.renderStrategy.ParentFirstHeaderRenderStrategy$1.component(ParentFirstHeaderRenderStrategy.java:66)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:143)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:161)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:161)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:161)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:117)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:193)
     *      at org.apache.wicket.MarkupContainer.visitChildren(MarkupContainer.java:941)
     *      at org.apache.wicket.markup.renderStrategy.ParentFirstHeaderRenderStrategy.renderChildHeaders(ParentFirstHeaderRenderStrategy.java:64)
     *      at org.apache.wicket.markup.renderStrategy.AbstractHeaderRenderStrategy.renderHeader(AbstractHeaderRenderStrategy.java:125)
     *      at org.apache.wicket.markup.html.internal.HtmlHeaderContainer.onComponentTagBody(HtmlHeaderContainer.java:140)
     *      at org.apache.wicket.Component.renderComponent(Component.java:2518)
     *      at org.apache.wicket.MarkupContainer.onRender(MarkupContainer.java:1527)
     *      at org.apache.wicket.Component.render_(Component.java:2380)
     *      at org.apache.wicket.Component.render(Component.java:2307)
     *      at org.apache.wicket.MarkupContainer.renderNext(MarkupContainer.java:1466)
     *      at org.apache.wicket.MarkupContainer.renderAll(MarkupContainer.java:1589)
     *      at org.apache.wicket.Page.onRender(Page.java:1139)
     *      at org.apache.wicket.Component.render_(Component.java:2380)
     *      at org.apache.wicket.Component.render(Component.java:2307)
     *      at org.apache.wicket.Page.renderPage(Page.java:1289)
     *      at org.apache.wicket.request.handler.render.WebPageRenderer.renderPage(WebPageRenderer.java:131)
     *      at org.apache.wicket.request.handler.render.WebPageRenderer.respond(WebPageRenderer.java:199)
     *      at org.apache.wicket.request.handler.RenderPageRequestHandler.respond(RenderPageRequestHandler.java:149)
     *      at org.apache.wicket.request.RequestHandlerStack.executeRequestHandler(RequestHandlerStack.java:84)
     *      at org.apache.wicket.request.cycle.RequestCycle.processRequest(RequestCycle.java:206)
     *      at org.apache.wicket.request.cycle.RequestCycle.processRequestAndDetach(RequestCycle.java:248)
     *      at org.apache.wicket.protocol.http.WicketFilter.processRequest(WicketFilter.java:131)
     *      at org.apache.wicket.protocol.http.WicketFilter.doFilter(WicketFilter.java:184)
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
     *      at org.mortbay.jetty.HttpConnection$RequestHandler.headerComplete(HttpConnection.java:926)
     *      at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:549)
     *      at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:212)
     *      at org.mortbay.jetty.HttpConnection.handle(HttpConnection.java:404)
     *      at org.mortbay.io.nio.SelectChannelEndPoint.run(SelectChannelEndPoint.java:410)
     *      at org.mortbay.thread.QueuedThreadPool$PoolThread.run(QueuedThreadPool.java:582)
     * 
     * 
     * Complete stack:
     * 
     * org.apache.wicket.WicketRuntimeException: Error calling method: public java.util.Date fiftyfive.wicket.examples.formtest.FormTestPage$1.getStartDate() on object: [ [Component id = calendar-control]]
     *      at org.apache.wicket.util.lang.PropertyResolver$MethodGetAndSet.getValue(PropertyResolver.java:1116)
     *      at org.apache.wicket.util.lang.PropertyResolver$ObjectAndGetSetter.getValue(PropertyResolver.java:637)
     *      at org.apache.wicket.util.lang.PropertyResolver.getValue(PropertyResolver.java:96)
     *      at org.apache.wicket.model.AbstractPropertyModel.getObject(AbstractPropertyModel.java:122)
     *      at fiftyfive.wicket.datetime.RestrictedDatePicker.configure(RestrictedDatePicker.java:168)
     *      at org.apache.wicket.extensions.yui.calendar.DatePicker.renderHead(DatePicker.java:260)
     *      at org.apache.wicket.Component.renderHead(Component.java:2627)
     *      at org.apache.wicket.markup.renderStrategy.ParentFirstHeaderRenderStrategy$1.component(ParentFirstHeaderRenderStrategy.java:70)
     *      at org.apache.wicket.markup.renderStrategy.ParentFirstHeaderRenderStrategy$1.component(ParentFirstHeaderRenderStrategy.java:66)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:143)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:161)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:161)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:161)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:117)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:193)
     *      at org.apache.wicket.MarkupContainer.visitChildren(MarkupContainer.java:941)
     *      at org.apache.wicket.markup.renderStrategy.ParentFirstHeaderRenderStrategy.renderChildHeaders(ParentFirstHeaderRenderStrategy.java:64)
     *      at org.apache.wicket.markup.renderStrategy.AbstractHeaderRenderStrategy.renderHeader(AbstractHeaderRenderStrategy.java:125)
     *      at org.apache.wicket.markup.html.internal.HtmlHeaderContainer.onComponentTagBody(HtmlHeaderContainer.java:140)
     *      at org.apache.wicket.Component.renderComponent(Component.java:2518)
     *      at org.apache.wicket.MarkupContainer.onRender(MarkupContainer.java:1527)
     *      at org.apache.wicket.Component.render_(Component.java:2380)
     *      at org.apache.wicket.Component.render(Component.java:2307)
     *      at org.apache.wicket.MarkupContainer.renderNext(MarkupContainer.java:1466)
     *      at org.apache.wicket.MarkupContainer.renderAll(MarkupContainer.java:1589)
     *      at org.apache.wicket.Page.onRender(Page.java:1139)
     *      at org.apache.wicket.Component.render_(Component.java:2380)
     *      at org.apache.wicket.Component.render(Component.java:2307)
     *      at org.apache.wicket.Page.renderPage(Page.java:1289)
     *      at org.apache.wicket.request.handler.render.WebPageRenderer.renderPage(WebPageRenderer.java:131)
     *      at org.apache.wicket.request.handler.render.WebPageRenderer.respond(WebPageRenderer.java:199)
     *      at org.apache.wicket.request.handler.RenderPageRequestHandler.respond(RenderPageRequestHandler.java:149)
     *      at org.apache.wicket.request.RequestHandlerStack.executeRequestHandler(RequestHandlerStack.java:84)
     *      at org.apache.wicket.request.cycle.RequestCycle.processRequest(RequestCycle.java:206)
     *      at org.apache.wicket.request.cycle.RequestCycle.processRequestAndDetach(RequestCycle.java:248)
     *      at org.apache.wicket.protocol.http.WicketFilter.processRequest(WicketFilter.java:131)
     * 
     * java.lang.RuntimeException: java.text.ParseException: Unparseable date: "1xxx07"
     *      at fiftyfive.wicket.examples.formtest.FormTestPage$1.getStartDate(FormTestPage.java:87)
     *      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
     *      at java.lang.reflect.Method.invoke(Method.java:597)
     *      at org.apache.wicket.util.lang.PropertyResolver$MethodGetAndSet.getValue(PropertyResolver.java:1112)
     *      at org.apache.wicket.util.lang.PropertyResolver$ObjectAndGetSetter.getValue(PropertyResolver.java:637)
     *      at org.apache.wicket.util.lang.PropertyResolver.getValue(PropertyResolver.java:96)
     *      at org.apache.wicket.model.AbstractPropertyModel.getObject(AbstractPropertyModel.java:122)
     *      at fiftyfive.wicket.datetime.RestrictedDatePicker.configure(RestrictedDatePicker.java:168)
     *      at org.apache.wicket.extensions.yui.calendar.DatePicker.renderHead(DatePicker.java:260)
     *      at org.apache.wicket.Component.renderHead(Component.java:2627)
     *      at org.apache.wicket.markup.renderStrategy.ParentFirstHeaderRenderStrategy$1.component(ParentFirstHeaderRenderStrategy.java:70)
     *      at org.apache.wicket.markup.renderStrategy.ParentFirstHeaderRenderStrategy$1.component(ParentFirstHeaderRenderStrategy.java:66)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:143)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:161)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:161)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:161)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:117)
     *      at org.apache.wicket.util.visit.Visits.visitChildren(Visits.java:193)
     *      at org.apache.wicket.MarkupContainer.visitChildren(MarkupContainer.java:941)
     *      at org.apache.wicket.markup.renderStrategy.ParentFirstHeaderRenderStrategy.renderChildHeaders(ParentFirstHeaderRenderStrategy.java:64)
     *      at org.apache.wicket.markup.renderStrategy.AbstractHeaderRenderStrategy.renderHeader(AbstractHeaderRenderStrategy.java:125)
     *      at org.apache.wicket.markup.html.internal.HtmlHeaderContainer.onComponentTagBody(HtmlHeaderContainer.java:140)
     *      at org.apache.wicket.Component.renderComponent(Component.java:2518)
     *      at org.apache.wicket.MarkupContainer.onRender(MarkupContainer.java:1527)
     *      at org.apache.wicket.Component.render_(Component.java:2380)
     *      at org.apache.wicket.Component.render(Component.java:2307)
     *      at org.apache.wicket.MarkupContainer.renderNext(MarkupContainer.java:1466)
     *      at org.apache.wicket.MarkupContainer.renderAll(MarkupContainer.java:1589)
     *      at org.apache.wicket.Page.onRender(Page.java:1139)
     *      at org.apache.wicket.Component.render_(Component.java:2380)
     *      at org.apache.wicket.Component.render(Component.java:2307)
     *      at org.apache.wicket.Page.renderPage(Page.java:1289)
     *      at org.apache.wicket.request.handler.render.WebPageRenderer.renderPage(WebPageRenderer.java:131)
     *      at org.apache.wicket.request.handler.render.WebPageRenderer.respond(WebPageRenderer.java:199)
     *      at org.apache.wicket.request.handler.RenderPageRequestHandler.respond(RenderPageRequestHandler.java:149)
     *      at org.apache.wicket.request.RequestHandlerStack.executeRequestHandler(RequestHandlerStack.java:84)
     *      at org.apache.wicket.request.cycle.RequestCycle.processRequest(RequestCycle.java:206)
     *      at org.apache.wicket.request.cycle.RequestCycle.processRequestAndDetach(RequestCycle.java:248)
     *      at org.apache.wicket.protocol.http.WicketFilter.processRequest(WicketFilter.java:131)</pre>
     */
    public static void logException(Logger logger, Exception e)
    {
        Args.notNull(logger, "logger");
        Args.notNull(e, "e");
        
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
     * <p>
     * Then the unwrapped exception would be {@code MyBusinessException}. 
     */
    public static Throwable unwrap(Throwable e)
    {
        Args.notNull(e, "e");
        
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
     *   URL       = wicket/bookmarkable/fiftyfive.wicket.examples.formtest.FormTestPage?0&initialMonth=10.2007&startDate=1xxx07&endDate=11.10.2007
     *   Handler   = RenderPageRequestHandler
     *   Component = FormTestPage
     *   Duration  = 485 milliseconds
     * Session:
     *   ID       = 1sxy938y7qoqq942z4pnuqdqt
     *   Info     = TODO: Your session info goes here
     *   Size     = 716 bytes
     *   Duration = 310 milliseconds
     * Application:
     *   Active Sessions = 1 (1 peak)
     *   Memory Usage    = 25M used, 40M free, 533M max
     *   IP Address      = 172.16.1.14
     *   Uptime          = 10.9 seconds
     * Headers:
     *   Host            = localhost:8080
     *   User-Agent      = Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; en-us) AppleWebKit/533.18.1 (KHTML, like Gecko) Version/5.0.2 Safari/533.18.5
     *   Accept          = application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,&#042;/&#042;;q=0.5
     *   Cache-Control   = max-age=0
     *   Accept-Language = en-us
     *   Accept-Encoding = gzip, deflate
     *   Cookie          = JSESSIONID=1217fv4qjpnbnv1ewzzmonevh
     *   Connection      = keep-alive</pre>
     * <p>
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
     * <li>{@code Handler}</li>
     * <li>{@code Component}</li>
     * <li>{@code Duration}</li>
     * </ul>
     */
    public static Map<String,Object> getRequestInfo()
    {
        Map<String,Object> info = new LinkedHashMap<String,Object>();
        info.put("URL", HttpUtils.getRelativeRequestUrl());
        info.put("Handler", describeRequestHandler());
        info.put("Component", describeRequestComponent());
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
     * Returns a human-readable description of the original handler of the
     * current request. This is simply the name of the
     * {@link IRequestHandler} class, like {@code RenderPageRequestHandler}.
     */
    public static String describeRequestHandler()
    {
        IRequestHandler handler = guessOriginalRequestHandler();
        // TODO: more descriptive than this?
        return handler != null ? Classes.simpleName(handler.getClass()) : null;
    }
    
    /**
     * Returns a description of component or page that is currently being
     * processed by the Wicket framework. If a component is being executed, the
     * description will be in the form:
     * <pre class="example">
     * PageClass &gt; ComponentClass (superclass if component is anonymous) [wicket:id path]</pre>
     * <p>
     * Example:
     * <pre class="example">
     * MyPage &gt; MyPanel$1 (Link) [path:to:link]</pre>
     * <p>
     * If a page is being rendered, the description will be the page class.
     */
    public static String describeRequestComponent()
    {
        IRequestHandler handler = guessOriginalRequestHandler();
        Class<? extends IRequestablePage> pageClass = null;
        IRequestableComponent component = null;
        
        if(handler instanceof IPageClassRequestHandler)
        {
            pageClass = ((IPageClassRequestHandler) handler).getPageClass();
        }
        if(handler instanceof IComponentRequestHandler)
        {
            component = ((IComponentRequestHandler) handler).getComponent();
        }
        
        StringBuffer desc = new StringBuffer();
        
        if(pageClass != null)
        {
            desc.append(Classes.simpleName(pageClass));
        }
        if(component != null)
        {
            String classDesc = null;
            if(component.getClass().isAnonymousClass())
            {
                classDesc = String.format(
                    "%s (%s)",
                    Classes.simpleName(component.getClass()),
                    Classes.simpleName(component.getClass().getSuperclass())
                );
            }
            else
            {
                classDesc = Classes.simpleName(component.getClass());
            }
            
            desc.append(String.format(
                " > %s [%s]",
                classDesc,
                component.getPageRelativePath()
            ));
        }

        return desc.length() > 0 ? desc.toString() : null;
    }
    
    /**
     * Returns the amount of time the current request has taken up until
     * this point.
     */
    public static Duration getRequestDuration()
    {
        Time start = Time.millis(RequestCycle.get().getStartTime());
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
    
    private static IRequestHandler guessOriginalRequestHandler()
    {
        IRequestMapper mapper = Application.get().getRootRequestMapper();
        return mapper.mapRequest(RequestCycle.get().getRequest());
    }

    /**
     * Not meant to be instantiated.
     */
    private LoggingUtils()
    {
        super();
    }
}
