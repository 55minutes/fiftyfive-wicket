/**
 * Copyright 2013 55 Minutes (http://www.55minutes.com)
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
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ParameterSpecTest
{
    private WicketTester tester;
    
    @Before
    public void createTester()
    {
        this.tester = new WicketTester();
    }
    
    @After
    public void destroyTester()
    {
        this.tester.destroy();
    }
    
    @Test
    public void testCreateLink_indexed()
    {
        TestBean bean = new TestBean(1L, "foo");
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        BookmarkablePageLink link = builder.createLink("bar", new Model(bean));
        this.tester.startComponent(link);
        
        Assert.assertEquals("bar", link.getId());
        Assert.assertEquals(TestPage.class, link.getPageClass());
        
        PageParameters params = link.getPageParameters();
        Assert.assertEquals(bean.getId(), params.get("id").toLongObject());
        Assert.assertEquals(bean.getName(), params.get("name").toString());
    }

    @Test
    public void testCreateParameters_indexed()
    {
        TestBean bean = new TestBean(1L, "foo");
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        PageParameters params = builder.createParameters(bean);
        Assert.assertEquals(bean.getId(), params.get("id").toLongObject());
        Assert.assertEquals(bean.getName(), params.get("name").toString());
    }

    @Test
    public void testParseParameters_indexed()
    {
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        TestBean bean = new TestBean();
        PageParameters params = new PageParameters();
        params.set("id", "5");
        params.set("name", "hello");
        
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
        params.set("id", "notparseableaslong");
        
        try
        {
            builder.parseParameters(params, bean);
            Assert.fail("AbortWithHttpErrorCodeException was not thrown.");
        }
        catch(AbortWithHttpErrorCodeException awhece)
        {
            Assert.assertEquals(404, awhece.getErrorCode());
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
        params.set("id", "notparseableaslong");
        
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
        this.tester.startComponent(link);

        Assert.assertEquals("bar", link.getId());
        Assert.assertEquals(TestPage.class, link.getPageClass());
        
        PageParameters params = link.getPageParameters();
        Assert.assertEquals(bean.getId(), params.get("beanId").toLongObject());
        Assert.assertEquals(bean.getName(), params.get("beanName").toString());
    }

    @Test
    public void testCreateParameters_named()
    {
        TestBean bean = new TestBean(1L, "foo");
        ParameterSpec builder = new ParameterSpec<TestBean>(TestPage.class);
        builder.registerParameter("beanId", "id");
        builder.registerParameter("beanName", "name");
        
        PageParameters params = builder.createParameters(bean);
        Assert.assertEquals(bean.getId(), params.get("beanId").toLongObject());
        Assert.assertEquals(bean.getName(), params.get("beanName").toString());
    }    

    @Test
    public void testParseParameters_named()
    {
        ParameterSpec builder = new ParameterSpec<TestBean>(TestPage.class);
        builder.registerParameter("beanId", "id");
        builder.registerParameter("beanName", "name");
        
        TestBean bean = new TestBean();
        PageParameters params = new PageParameters();
        params.set("beanId", "5");
        params.set("beanName", "hello");
        
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
        WicketTestUtils.startComponentWithXHtml(this.tester, new Link("link") {
            public void onClick()
            {
                spec.redirect(Model.of(new TestBean()));
            }
        }, "<a wicket:id=\"link\">link</a>");
        
        // Click the link that we just rendered
        this.tester.clickLink("link");
        
        // The redirect should have forced wicket to render TestPage
        this.tester.assertRenderedPage(TestPage.class);
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
        private Long id;
        private String name;
        
        public TestBean()
        {
            super();
        }
        
        public TestBean(Long id, String name)
        {
            this.id = id;
            this.name = name;
        }
        
        public Long getId()
        {
            return this.id;
        }

        public void setId(Long newId)
        {
            this.id = newId;
        }
        
        public String getName()
        {
            return this.name;
        }

        public void setName(String newName)
        {
            this.name = newName;
        }
    }
}
