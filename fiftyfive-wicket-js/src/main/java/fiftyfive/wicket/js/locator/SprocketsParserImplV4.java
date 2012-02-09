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
 * //= require library_name
 * //= require ./file_name</pre>
 * <p>
 * Paths that begin with {@code ./} are considered to be relative to the current
 * directory. If the path does not begin with {@code ./}, then it is considered
 * a path to a "library", meaning the classpath will be searched for a matching
 * JavaScript file.
 * <p>
 * Note that paths can also be surrounded by quotes, but they are completely optional:
 * <pre class="example">
 * //= require "library_name"
 * //= require "./file_name"</pre>
 * <p>
 * For backwards-compatibility with {@link SprocketsParserImplV3},
 * angle-brackets are also permitted.
 * <pre class="example">
 * //= require &lt;library_name&gt;</pre>
 * <p>
 * <em>SprocketsParserImplV4 is the default implementation for fiftyfive-wicket-js 4.0.</em>
 * 
 * @since 4.0
 */
public class SprocketsParserImplV4 implements SprocketsParser
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        SprocketsParserImplV4.class
    );
    
    static final String PREFIX = "^\\s*//=\\s*require\\s+";
    static final Pattern[] PATTERNS = new Pattern[] {
        Pattern.compile(PREFIX + "\"(.*)\"\\s*$"),
        Pattern.compile(PREFIX + "'(.*)'\\s*$"),
        Pattern.compile(PREFIX + "<(.*)>\\s*$"),
        Pattern.compile(PREFIX + "(.*)\\s*$")
    };
    
    public List<Sprocket> parseSprockets(BufferedReader javascript)
        throws IOException
    {
        List<Sprocket> sprockets = new ArrayList<Sprocket>();
        
        while(true)
        {
            String line = javascript.readLine();
            if(null == line) break;
            
            for(Pattern p : PATTERNS)
            {
                Matcher require = p.matcher(line);
                if(require.find())
                {
                    String path = require.group(1);
                    boolean isLibrary = !(path.startsWith("./") || path.startsWith("../"));
                    sprockets.add(new Sprocket(isLibrary, path));
                    break;
                }
            }
        }
        return sprockets;
    }
}
