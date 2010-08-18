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

package fiftyfive.wicket.util;

import java.io.Serializable;

import fiftyfive.wicket.test.PageWithInlineMarkup;
import fiftyfive.wicket.test.WicketTestUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ParameterSpecTest
{
    private WicketTester _tester;
    
    @Before
    public void createTester()
    {
        _tester = new WicketTester();
    }
    
    @After
    public void destroyTester()
    {
        _tester.destroy();
    }
    
    @Test
    public void testCreateLink_indexed()
    {
        TestBean bean = new TestBean(1L, "foo");
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        BookmarkablePageLink link = builder.createLink("bar", new Model(bean));
        _tester.startComponent(link);
        
        Assert.assertEquals("bar", link.getId());
        Assert.assertEquals(TestPage.class, link.getPageClass());
        
        PageParameters params = link.getPageParameters();
        Assert.assertEquals(bean.getId(), (Long) params.getLong("id"));
        Assert.assertEquals(bean.getName(), params.getString("name"));
    }

    @Test
    public void testCreateParameters_indexed()
    {
        TestBean bean = new TestBean(1L, "foo");
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        PageParameters params = builder.createParameters(bean);
        Assert.assertEquals(bean.getId(), (Long) params.getLong("id"));
        Assert.assertEquals(bean.getName(), params.getString("name"));
    }

    @Test
    public void testParseParameters_indexed()
    {
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        TestBean bean = new TestBean();
        PageParameters params = new PageParameters();
        params.put("id", "5");
        params.put("name", "hello");
        
        builder.parseParameters(params, bean);
        
        Assert.assertEquals((Long) 5L, (Long) bean.getId());
        Assert.assertEquals("hello", bean.getName());
    }

    /**
     * Asserts that badly formatted page parameter value causes an abort
     * with 404.
     */
    @Test
    public void testParseParameters_badformat404()
    {
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        TestBean bean = new TestBean();
        PageParameters params = new PageParameters();
        params.put("id", "notparseableaslong");
        
        try
        {
            builder.parseParameters(params, bean);
            Assert.fail("AbortWithWebErrorCodeException was not thrown.");
        }
        catch(AbortWithWebErrorCodeException awwece)
        {
            Assert.assertEquals(404, awwece.getErrorCode());
        }
    }

    /**
     * Asserts that badly formatted page parameter value is ignored when
     * {@code throw404OnParseError} is {@code false}.
     */
    @Test
    public void testParseParameters_badformatnull()
    {
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        TestBean bean = new TestBean();
        PageParameters params = new PageParameters();
        params.put("id", "notparseableaslong");
        
        builder.parseParameters(params, bean, false);
        
        Assert.assertNull(bean.getId());
    }

    /**
     * Asserts that missing parameter values are gracefully ignored.
     */
    @Test
    public void testParseParameters_missing()
    {
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        TestBean bean = new TestBean();
        PageParameters params = new PageParameters();
        
        builder.parseParameters(params, bean);

        Assert.assertNull(bean.getId());
        Assert.assertNull(bean.getName());
    }


    @Test
    public void testCreateLink_named()
    {
        TestBean bean = new TestBean(1L, "foo");
        ParameterSpec builder = new ParameterSpec<TestBean>(TestPage.class);
        builder.registerParameter("beanId", "id");
        builder.registerParameter("beanName", "name");
        
        BookmarkablePageLink link = builder.createLink("bar", new Model(bean));
        _tester.startComponent(link);

        Assert.assertEquals("bar", link.getId());
        Assert.assertEquals(TestPage.class, link.getPageClass());
        
        PageParameters params = link.getPageParameters();
        Assert.assertEquals(bean.getId(), (Long) params.getLong("beanId"));
        Assert.assertEquals(bean.getName(), params.getString("beanName"));
    }

    @Test
    public void testCreateParameters_named()
    {
        TestBean bean = new TestBean(1L, "foo");
        ParameterSpec builder = new ParameterSpec<TestBean>(TestPage.class);
        builder.registerParameter("beanId", "id");
        builder.registerParameter("beanName", "name");
        
        PageParameters params = builder.createParameters(bean);
        Assert.assertEquals(bean.getId(), (Long) params.getLong("beanId"));
        Assert.assertEquals(bean.getName(), params.getString("beanName"));
    }    

    @Test
    public void testParseParameters_named()
    {
        ParameterSpec builder = new ParameterSpec<TestBean>(TestPage.class);
        builder.registerParameter("beanId", "id");
        builder.registerParameter("beanName", "name");
        
        TestBean bean = new TestBean();
        PageParameters params = new PageParameters();
        params.put("beanId", "5");
        params.put("beanName", "hello");
        
        builder.parseParameters(params, bean);
        
        Assert.assertEquals((Long) 5L, bean.getId());
        Assert.assertEquals("hello", bean.getName());
    }
    
    /**
     * Verify that the redirect method actually redirects to a new page as
     * we expect.
     */
    @Test
    public void testRedirect()
    {
        final ParameterSpec spec = new ParameterSpec<TestBean>(TestPage.class);
        
        // Force the redirect during wicket's normal request processing.
        // For example, when a link is clicked.
        WicketTestUtils.startComponentWithXHtml(_tester, new Link("link") {
            public void onClick()
            {
                spec.redirect(Model.of(new TestBean()));
            }
        }, "<a wicket:id=\"link\">link</a>");
        
        // Click the link that we just rendered
        _tester.clickLink("link");
        
        // The redirect should have forced wicket to render TestPage
        _tester.assertRenderedPage(TestPage.class);
    }
    
    public static class TestPage extends PageWithInlineMarkup
    {
        public TestPage(PageParameters params)
        {
            super("<html><head></head><body>hello</body></html>");
        }
    }
    
    public static class TestBean implements Serializable
    {
        private Long _id;
        private String _name;
        
        public TestBean()
        {
            super();
        }
        
        public TestBean(Long id, String name)
        {
            _id = id;
            _name = name;
        }
        
        public Long getId()
        {
            return _id;
        }

        public void setId(Long newId)
        {
            _id = newId;
        }
        
        public String getName()
        {
            return _name;
        }

        public void setName(String newName)
        {
            _name = newName;
        }
    }
}
