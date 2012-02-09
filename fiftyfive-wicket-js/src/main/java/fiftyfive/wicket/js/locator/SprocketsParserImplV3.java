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
package fiftyfive.wicket.js.locator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that parses JavaScript files for special
 * <a href="http://getsprockets.org/">Sprockets</a> comments
 * like these:
 * <pre class="example">
 * //= require &lt;library_name&gt;
 * //= require "file_name"</pre>
 * <p>
 * In this version of the syntax, angle-brackets indicate libraries that should be
 * found by searching the classpath, and double-quotes indicate file paths relative to
 * the current directory.
 * <p>
 * <em>This was the default implementation in fiftyfive-wicket-js 2.x and 3.x.</em>
 * Starting with 4.0, the default is now {@link SprocketsParserImplV4}. To force
 * V3 to be used, call
 * {@link fiftyfive.wicket.js.JavaScriptDependencySettings#setSprocketsParser JavaScriptDependencySettings#setSprocketsParser(new SprocketsParserImplV3())}.
 * 
 * @since 2.0
 */
public class SprocketsParserImplV3 implements SprocketsParser
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        SprocketsParserImplV3.class
    );
    
    static final String PREFIX = "^\\s*//=\\s*require\\s+";
    static final Pattern REQ = Pattern.compile(PREFIX);
    static final Pattern LIB = Pattern.compile(PREFIX + "<([^>]+)>");
    static final Pattern FILE = Pattern.compile(PREFIX + "\"([^\"]+)\"");
    
    /**
     * Parses the given JavaScript file and returns a list of sprocket
     * dependencies that it finds. Does not close the Reader.
     */
    public List<Sprocket> parseSprockets(BufferedReader javascript)
        throws IOException
    {
        List<Sprocket> sprockets = new ArrayList<Sprocket>();
        
        while(true)
        {
            String line = javascript.readLine();
            if(null == line) break;
            
            Matcher require = REQ.matcher(line);
            if(!require.find()) continue;
            
            String name = findName(LIB, line);
            if(name != null)
            {
                LOGGER.debug("Found library: {}", name);
                sprockets.add(new Sprocket(true, name));
            }
            else
            {
                name = findName(FILE, line);
                if(name != null)
                {
                    LOGGER.debug("Found file: {}", name);
                    sprockets.add(new Sprocket(false, name));
                }
            }
        }
        return sprockets;
    }
    
    private String findName(Pattern patt, String line)
    {
        Matcher m = patt.matcher(line);
        if(!m.find()) return null;
        
        return m.group(1);
    }
}
