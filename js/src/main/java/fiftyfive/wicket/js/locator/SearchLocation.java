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
package fiftyfive.wicket.js.locator;

import fiftyfive.util.Assert;

/**
 * Holds an immutable classpath search location, consisting of a class
 * (the scope) and a path relative to that class.
 * 
 * @since 2.0
 */
public class SearchLocation
{
    private Class<?> _scope;
    private String _path;
    
    public SearchLocation(Class<?> scope, String path)
    {
        Assert.notNull(scope, "scope cannot be null");
        Assert.notNull(path, "path cannot be null");
        Assert.isFalse(path.startsWith("/"), "path cannot start with \"/\"");
        
        _scope = scope;
        _path = path;
    }
    
    public Class<?> getScope()
    {
        return _scope;
    }
    
    public String getPath()
    {
        return _path;
    }
}
