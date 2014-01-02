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
package fiftyfive.wicket.js.locator;

import org.apache.wicket.util.lang.Args;

/**
 * Holds an immutable classpath search location, consisting of a class
 * (the scope) and a path relative to that class.
 * 
 * @since 2.0
 */
public class SearchLocation
{
    private Class<?> scope;
    private String path;
    
    public SearchLocation(Class<?> scope, String path)
    {
        Args.notNull(scope, "scope");
        Args.notNull(path, "path");
        Args.isFalse(path.startsWith("/"), "path cannot start with \"/\": %s", path);
        
        this.scope = scope;
        this.path = path;
    }
    
    public Class<?> getScope()
    {
        return this.scope;
    }
    
    public String getPath()
    {
        return this.path;
    }
}
