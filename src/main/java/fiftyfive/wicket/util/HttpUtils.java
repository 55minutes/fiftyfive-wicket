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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequest;

/**
 * Provides easy access to the HTTP information that triggered the current
 * Wicket request. Wicket normally hides this information from us, but
 * occassionally we need access to things like the raw URL or HTTP headers.
 * Note that these methods only work when called within a Wicket thread.
 */
public class HttpUtils
{
    /**
     * Returns the {@link HttpServletRequest} associated with the current
     * Wicket request, or {@code null} one cannot be found.
     */
    public static HttpServletRequest getHttpServletRequest()
    {
        HttpServletRequest http = null;
        Request request = RequestCycle.get().getRequest();
        if(request instanceof WebRequest)
        {
            http = ((WebRequest) request).getHttpServletRequest();
        }
        return http;
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
    
    // TODO: does this work during ajax?
    
    /**
     * Returns the entire URL that was used to make the current request,
     * including protocol, host, port, absolute path and query string.
     * May return {@code null} if the current request is not an HTTP one.
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
     * May return {@code null} if the current request is not an HTTP one.
     */
    public static String getRelativeRequestUrl()
    {
        HttpServletRequest http = getHttpServletRequest();
        if(null == http) return null;
        
        String path = RequestCycle.get().getRequest().getPath();
        String query = http.getQueryString();
        
        StringBuffer url = new StringBuffer();

        if(!path.startsWith("/"))
        {
            url.append("/");
        }
        url.append(path);

        if(query != null)
        {
            url.append("?");
            url.append(query);
        }
        
        return url.toString();
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
        private String _key;
        private String _value;
        
        private Header(String key, String value)
        {
            _key = key;
            _value = value;
        }
        
        public String getKey()
        {
            return _key;
        }
        
        public String getValue()
        {
            return _value;
        }
        
        public String setValue(String value)
        {
            throw new UnsupportedOperationException();
        }
    }
}
