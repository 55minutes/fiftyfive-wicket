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

public class MockJavaScriptDependencyLocator
    implements JavaScriptDependencyLocator
{
    private DependencyCollection libraryScripts;
    private DependencyCollection resourceScripts;
    private DependencyCollection associatedScripts;
    
    public void findLibraryScripts(String libraryName,
                                   DependencyCollection scripts)
    {
        this.libraryScripts.copyTo(scripts);
    }
    
    public void findResourceScripts(Class<?> cls,
                                    String fileName,
                                    DependencyCollection scripts)
    {
        this.resourceScripts.copyTo(scripts);
    }
    
    public void findAssociatedScripts(Class<?> cls,
                                      DependencyCollection scripts)
    {
        this.associatedScripts.copyTo(scripts);
    }
    
    public void setLibraryScripts(DependencyCollection libraryScripts)
    {
        this.libraryScripts = libraryScripts;
    }
    
    public void setResourceScripts(DependencyCollection resourceScripts)
    {
        this.resourceScripts = resourceScripts;
    }
    
    public void setAssociatedScripts(DependencyCollection associatedScripts)
    {
        this.associatedScripts = associatedScripts;
    }
}
