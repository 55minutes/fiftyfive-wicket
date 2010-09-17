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

package fiftyfive.wicket.js.locator;

import java.util.List;

import org.apache.wicket.ResourceReference;

public interface JavaScriptDependencyLocator
{
    void findLibraryScripts(String libraryName, DependencyCollection scripts);
    void findResourceScripts(Class<?> cls, String fileName, DependencyCollection scripts);
    void findAssociatedScripts(Class<?> cls, DependencyCollection scripts);
}
