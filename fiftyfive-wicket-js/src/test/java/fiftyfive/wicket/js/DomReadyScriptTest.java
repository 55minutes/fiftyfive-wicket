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
package fiftyfive.wicket.js;

import java.util.regex.Pattern;

import fiftyfive.wicket.test.WicketTestUtils;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.http.WebRequest;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

public class DomReadyScriptTest extends BaseJSTest
{
    @Mock WebRequest request;
    @Mock IHeaderResponse response;
    @Mock ResourceReference jquery;
    @Mock JavaScriptDependencySettings settings;

    /**
     * Verify that the ready script is not rendered if wasRendered
     * returns {@code true}.
     */
    @Test
    public void testRenderHead_wasRendered() throws Exception
    {
        final String js = "alert('ready!')";
        when(this.response.wasRendered(js)).thenReturn(true);
        
        mockedDomReadyScript(js).renderHead(null, this.response);
        
        verify(this.response).wasRendered(js);
        verifyNoMoreInteractions(this.response);
    }

    /**
     * Verify nothing is rendered at all if script is {@code null}.
     */
    @Test
    public void testRenderHead_null() throws Exception
    {
        mockedDomReadyScript(null).renderHead(null, this.response);
        verifyNoMoreInteractions(this.response);
    }

    /**
     * Verify that jQuery is used during non-ajax response.
     */
    @Test
    public void testRenderHead_nonAjax() throws Exception
    {
        final String js = "alert('ready!')";
        when(this.response.wasRendered(js)).thenReturn(false);
        when(this.request.isAjax()).thenReturn(false);
        
        mockedDomReadyScript(js).renderHead(null, this.response);
        
        InOrder inOrder = inOrder(this.response);
        inOrder.verify(this.response).renderJavaScriptReference(this.jquery);
        inOrder.verify(this.response).renderJavaScript(
            "jQuery(function(){" + js + ";});", null
        );
        
        verify(this.response).wasRendered(js);
        verify(this.response).markRendered(js);
        verifyNoMoreInteractions(this.response);
    }

    /**
     * Verify that jQuery is not used during ajax response.
     */
    @Test
    public void testRenderHead_ajax() throws Exception
    {
        final String js = "alert('ready!')";
        when(this.response.wasRendered(js)).thenReturn(false);
        when(this.request.isAjax()).thenReturn(true);
        
        mockedDomReadyScript(js).renderHead(null, this.response);
        
        InOrder inOrder = inOrder(this.response);
        inOrder.verify(this.response).renderJavaScriptReference(this.jquery);
        inOrder.verify(this.response).renderOnDomReadyJavaScript(js);

        verify(this.response).wasRendered(js);
        verify(this.response).markRendered(js);
        verifyNoMoreInteractions(this.response);
    }

    /**
     * Render a simple wicket page using DomReadyScript and do some simple
     * checks that the correct markup was emitted.
     */
    @Test
    public void integrationTest() throws Exception
    {
        WebMarkupContainer comp = new WebMarkupContainer("c");
        comp.add(new DomReadyScript("alert('ready!')"));
        
        WicketTestUtils.startComponentWithHtml(
            this.tester, comp, "<span wicket:id=\"c\"></span>"
        );
        // Assert DOM ready script was rendered
        this.tester.assertContains(Pattern.quote(
            "jQuery(function(){alert('ready!');});"
        ));
        // And jQuery was automatically included
        this.tester.assertContains("<script .*src=\".*jquery.*\"></script>");
    }
    
    /**
     * Creates a DomReadyScript with the settings and request mocked.
     */
    private DomReadyScript mockedDomReadyScript(String js)
    {
        when(this.settings.getJQueryResource()).thenReturn(this.jquery);
        
        return new DomReadyScript(js) {
            @Override
            protected JavaScriptDependencySettings settings()
            {
                return DomReadyScriptTest.this.settings;
            }
            @Override
            protected Request request()
            {
                return DomReadyScriptTest.this.request;
            }
        };
    }
}
