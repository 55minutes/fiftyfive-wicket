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

/**
 * Represents a <a href="http://getsprockets.org/">sprocket</a>
 * dependency reference. A dependency can either be a path relative to the current file,
 * or it can a path that is resolved relative to the libarary sesarch path.
 * 
 * @since 2.0
 */
public class Sprocket
{
    private boolean library;
    private String path;
    
    public Sprocket(boolean isLibrary, String path)
    {
        this.library = isLibrary;
        this.path = path;
    }
    
    /**
     * Returns {@code true} if this is a library reference, {@code false} if
     * it is a file reference.
     */
    public boolean isLibrary()
    {
        return this.library;
    }
    
    /**
     * The name of the library or filename (the text between the angle brackets
     * or double quotes, respectively).
     */
    public String getPath()
    {
        return this.path;
    }
}
