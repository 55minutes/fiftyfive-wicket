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

public class MockJavaScriptDependencyLocator
    implements JavaScriptDependencyLocator
{
    private DependencyCollection _libraryScripts;
    private DependencyCollection _resourceScripts;
    private DependencyCollection _associatedScripts;
    
    public void findLibraryScripts(String libraryName,
                                   DependencyCollection scripts)
    {
        _libraryScripts.copyTo(scripts);
    }
    
    public void findResourceScripts(Class<?> cls,
                                    String fileName,
                                    DependencyCollection scripts)
    {
        _resourceScripts.copyTo(scripts);
    }
    
    public void findAssociatedScripts(Class<?> cls,
                                      DependencyCollection scripts)
    {
        _associatedScripts.copyTo(scripts);
    }
    
    public void setLibraryScripts(DependencyCollection libraryScripts)
    {
        this._libraryScripts = libraryScripts;
    }
    
    public void setResourceScripts(DependencyCollection resourceScripts)
    {
        this._resourceScripts = resourceScripts;
    }
    
    public void setAssociatedScripts(DependencyCollection associatedScripts)
    {
        this._associatedScripts = associatedScripts;
    }
}
