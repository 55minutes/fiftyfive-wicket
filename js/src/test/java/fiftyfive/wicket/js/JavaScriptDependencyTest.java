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

package fiftyfive.wicket.js;

import fiftyfive.wicket.js.locator.DependencyCollection;
import fiftyfive.wicket.js.locator.MockJavaScriptDependencyLocator;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class JavaScriptDependencyTest extends BaseJSTest
{
    @Mock IHeaderResponse _response;
    @Mock ResourceReference _css;
    @Mock ResourceReference _script1;
    @Mock ResourceReference _script2;
    @Mock ResourceReference _script3;
    @Mock JavaScriptDependencySettings _settings;

    MockJavaScriptDependencyLocator _locator;

    
    @Before
    public void setUpLocator()
    {
        _locator = new MockJavaScriptDependencyLocator();
        when(_settings.getLocator()).thenReturn(_locator);
    }

    @Test
    public void testRenderHead_library() throws Exception
    {
        JavaScriptDependency dep = new MockedJavaScriptDependency("library");
        
        DependencyCollection expected = new DependencyCollection();
        expected.setCss(_css);
        expected.add(_script1);
        expected.add(_script2);
        expected.add(_script3);
        _locator.setLibraryScripts(expected);
        
        dep.renderHead(_response);

        verify(_response).renderCSSReference(_css);

        InOrder inOrder = inOrder(_response);
        inOrder.verify(_response).renderJavascriptReference(_script1);
        inOrder.verify(_response).renderJavascriptReference(_script2);
        inOrder.verify(_response).renderJavascriptReference(_script3);
        
        verifyNoMoreInteractions(_response);
    }

    @Test
    public void testRenderHead_associated() throws Exception
    {
        JavaScriptDependency dep = new MockedJavaScriptDependency(getClass());
        
        DependencyCollection expected = new DependencyCollection();
        expected.add(_script1);
        _locator.setAssociatedScripts(expected);
        
        dep.renderHead(_response);

        verify(_response).renderJavascriptReference(_script1);
        verifyNoMoreInteractions(_response);
    }
    
    @Test
    public void testRenderHead_resource() throws Exception
    {
        JavaScriptDependency dep = new MockedJavaScriptDependency(
            getClass(), "file.js"
        );
        
        DependencyCollection expected = new DependencyCollection();
        expected.add(_script1);
        _locator.setResourceScripts(expected);
        
        dep.renderHead(_response);

        verify(_response).renderJavascriptReference(_script1);
        verifyNoMoreInteractions(_response);
    }
    
    /**
     * Subclass of JavaScriptDependency that uses our settings mock.
     */
    private class MockedJavaScriptDependency extends JavaScriptDependency
    {
        public MockedJavaScriptDependency(String library)
        {
            super(library);
        }
        
        public MockedJavaScriptDependency(Class<?> cls)
        {
            super(cls);
        }

        public MockedJavaScriptDependency(Class<?> cls, String name)
        {
            super(cls, name);
        }

        @Override
        protected JavaScriptDependencySettings settings()
        {
            return _settings;
        }
    }
}
