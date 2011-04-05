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

package fiftyfive.wicket.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.Strings;

/**
 * Provides easy access to the HTTP information that triggered the current
 * Wicket request. Wicket normally hides this information from us, but
 * occassionally we need access to things like the raw URL or HTTP headers.
 * Note that these methods only work when called within a Wicket thread.
 * 
 * @since 2.0
 */
public class HttpUtils
{
    /**
     * Returns the {@link HttpServletRequest} associated with the current
     * Wicket request, or {@code null} one cannot be found.
     */
    public static HttpServletRequest getHttpServletRequest()
    {
        Object req = RequestCycle.get().getRequest().getContainerRequest();
        if(req instanceof HttpServletRequest)
        {
            return (HttpServletRequest) req;
        }
        return null;
    }
    
    /**
     * Returns the {@link HttpServletResponse} associated with the current
     * Wicket request, or {@code null} one cannot be found.
     */
    public static HttpServletResponse getHttpServletResponse()
    {
        Object resp = RequestCycle.get().getResponse().getContainerResponse();
        if(resp instanceof HttpServletResponse)
        {
            return (HttpServletResponse) resp;
        }
        return null;
    }
    
    /**
     * Returns the value of the {@code User-Agent} HTTP header, as provided
     * by the {@link HttpServletRequest}. May be {@code null}.
     */
    public static String getUserAgent()
    {
        HttpServletRequest http = getHttpServletRequest();
        return null == http ? null : http.getHeader("User-Agent");
    }
    
    /**
     * Returns all HTTP headers as key-value pairs. Since headers can be listed
     * more than once, it is possible that a header key can have multiple
     * values. The resulting List may be empty, but never {@code null}.
     */
    public static List<Map.Entry<String,String>> getHeaders()
    {
        List<Map.Entry<String,String>> headers;
        headers = new ArrayList<Map.Entry<String,String>>();
        
        HttpServletRequest http = getHttpServletRequest();

        if(http != null)
        {
            for(Enumeration n=http.getHeaderNames(); n.hasMoreElements();)
            {
                String name = (String) n.nextElement();
                for(Enumeration v=http.getHeaders(name); v.hasMoreElements();)
                {
                    headers.add(new Header(name, (String) v.nextElement()));
                }
            }
        }
        return headers;
    }
    
    /**
     * Returns the entire URL that was used to render the current page,
     * which should match what the user sees in the browser location bar.
     * Includes protocol, host, port absolute path and query string.
     * <p>
     * <b>If the current request is an ajax one, this URL will not match
     * the actual ajax HTTP request being handled by the server.</b> Instead
     * this URL will be most recent non-ajax request, as observed in the
     * browser location bar.
     * 
     * @see org.apache.wicket.request.Request#getClientUrl
     * @since 3.0
     */
    public static String getAbsolutePageUrl()
    {
        String absolute = null;
        RequestCycle rc = RequestCycle.get();
        Url url = rc.getRequest().getClientUrl();
        
        HttpServletRequest http = getHttpServletRequest();
        if(url != null && http != null)
        {
            ServletWebRequest webRequest = (ServletWebRequest) rc.getRequest();
            String port = ":" + http.getServerPort();
            if(http.getServerPort() == 80 && http.getScheme().equals("http"))
            {
                port = "";
            }
            absolute = Strings.join("/",
                http.getScheme() + "://" + http.getServerName() + port,
                http.getServerName(),
                http.getContextPath(),
                webRequest.getFilterPrefix(),
                url.toString()
            );
        }
        return absolute;
    }
    
    /**
     * Returns the entire URL that was used to make the current request,
     * including protocol, host, port, absolute path and query string.
     * May return {@code null} if the current request is not an HTTP one.
     * <p>
     * <b>Note that if the current request is an ajax one, this URL will be
     * different from the URL that the user sees in the browser location
     * bar.</b>
     */
    public static String getAbsoluteRequestUrl()
    {
        HttpServletRequest request = getHttpServletRequest();
        if(null == request) return null;
        
        StringBuffer url = request.getRequestURL();
        String query = request.getQueryString();
        if(query != null)
        {
            url.append("?");
            url.append(query);
        }
        return url.toString();
    }
    
    /**
     * Returns the Wicket portion of the URL that was used to make the current
     * request, including relative path and query string. The protocol, host,
     * port, and base Wicket path are not included.
     * <p>
     * <b>Note that if the current request is an ajax one, this URL will be
     * different from the URL that the user sees in the browser location
     * bar.</b>
     */
    public static String getRelativeRequestUrl()
    {
        return RequestCycle.get().getRequest().getUrl().toString();
    }
    
    /**
     * Not meant to be instantiated.
     */
    private HttpUtils()
    {
        super();
    }
    
    /**
     * An immutable key-value pair.
     */
    private static class Header implements Map.Entry<String,String>
    {
        private String key;
        private String value;
        
        private Header(String key, String value)
        {
            this.key = key;
            this.value = value;
        }
        
        public String getKey()
        {
            return this.key;
        }
        
        public String getValue()
        {
            return this.value;
        }
        
        public String setValue(String value)
        {
            throw new UnsupportedOperationException();
        }
    }
}
