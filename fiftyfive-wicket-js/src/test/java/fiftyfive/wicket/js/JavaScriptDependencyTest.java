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
package fiftyfive.wicket.js;

import fiftyfive.wicket.js.locator.DependencyCollection;
import fiftyfive.wicket.js.locator.MockJavaScriptDependencyLocator;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class JavaScriptDependencyTest extends BaseJSTest
{
    @Mock IHeaderResponse response;
    @Mock ResourceReference css;
    @Mock ResourceReference script1;
    @Mock ResourceReference script2;
    @Mock ResourceReference script3;
    @Mock JavaScriptDependencySettings settings;

    MockJavaScriptDependencyLocator locator;

    
    @Before
    public void setUpLocator()
    {
        this.locator = new MockJavaScriptDependencyLocator();
        when(this.settings.getLocator()).thenReturn(this.locator);
    }

    @Test
    public void testRenderHead_library() throws Exception
    {
        JavaScriptDependency dep = new MockedJavaScriptDependency("library");
        
        DependencyCollection expected = new DependencyCollection();
        expected.setCss(this.css);
        expected.add(this.script1);
        expected.add(this.script2);
        expected.add(this.script3);
        this.locator.setLibraryScripts(expected);
        
        dep.renderHead(null, this.response);

        verify(this.response).renderCSSReference(this.css);

        InOrder inOrder = inOrder(this.response);
        inOrder.verify(this.response).renderJavaScriptReference(this.script1);
        inOrder.verify(this.response).renderJavaScriptReference(this.script2);
        inOrder.verify(this.response).renderJavaScriptReference(this.script3);
        
        verifyNoMoreInteractions(this.response);
    }

    @Test
    public void testRenderHead_associated() throws Exception
    {
        JavaScriptDependency dep = new MockedJavaScriptDependency(getClass());
        
        DependencyCollection expected = new DependencyCollection();
        expected.add(this.script1);
        this.locator.setAssociatedScripts(expected);
        
        dep.renderHead(null, this.response);

        verify(this.response).renderJavaScriptReference(this.script1);
        verifyNoMoreInteractions(this.response);
    }
    
    @Test
    public void testRenderHead_resource() throws Exception
    {
        JavaScriptDependency dep = new MockedJavaScriptDependency(
            getClass(), "file.js"
        );
        
        DependencyCollection expected = new DependencyCollection();
        expected.add(this.script1);
        this.locator.setResourceScripts(expected);
        
        dep.renderHead(null, this.response);

        verify(this.response).renderJavaScriptReference(this.script1);
        verifyNoMoreInteractions(this.response);
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
            return JavaScriptDependencyTest.this.settings;
        }
    }
}
