/*
 * Copyright 2009 55 Minutes (http://www.55minutes.com)
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

import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
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
        TestBean bean = new TestBean("1", "foo");
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        BookmarkablePageLink link = builder.createLink("bar", new Model(bean));
        _tester.startComponent(link);
        
        Assert.assertEquals("bar", link.getId());
        Assert.assertEquals(TestPage.class, link.getPageClass());
        
        PageParameters params = link.getPageParameters();
        Assert.assertEquals(bean.getId(), params.getString("id"));
        Assert.assertEquals(bean.getName(), params.getString("name"));
    }

    @Test
    public void testCreateParameters_indexed()
    {
        TestBean bean = new TestBean("1", "foo");
        ParameterSpec builder =
            new ParameterSpec<TestBean>(TestPage.class, "id", "name");
        
        PageParameters params = builder.createParameters(bean);
        Assert.assertEquals(bean.getId(), params.getString("id"));
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
        
        Assert.assertEquals("5", bean.getId());
        Assert.assertEquals("hello", bean.getName());
    }

    @Test
    public void testCreateLink_named()
    {
        TestBean bean = new TestBean("1", "foo");
        ParameterSpec builder = new ParameterSpec<TestBean>(TestPage.class);
        builder.registerParameter("beanId", "id");
        builder.registerParameter("beanName", "name");
        
        BookmarkablePageLink link = builder.createLink("bar", new Model(bean));
        _tester.startComponent(link);

        Assert.assertEquals("bar", link.getId());
        Assert.assertEquals(TestPage.class, link.getPageClass());
        
        PageParameters params = link.getPageParameters();
        Assert.assertEquals(bean.getId(), params.getString("beanId"));
        Assert.assertEquals(bean.getName(), params.getString("beanName"));
    }

    @Test
    public void testCreateParameters_named()
    {
        TestBean bean = new TestBean("1", "foo");
        ParameterSpec builder = new ParameterSpec<TestBean>(TestPage.class);
        builder.registerParameter("beanId", "id");
        builder.registerParameter("beanName", "name");
        
        PageParameters params = builder.createParameters(bean);
        Assert.assertEquals(bean.getId(), params.getString("beanId"));
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
        
        Assert.assertEquals("5", bean.getId());
        Assert.assertEquals("hello", bean.getName());
    }
    
    public static class TestPage extends WebPage
    {
        public TestPage(PageParameters params)
        {
            super(params);
        }
    }
    
    public static class TestBean implements Serializable
    {
        private String _id;
        private String _name;
        
        public TestBean()
        {
            super();
        }
        
        public TestBean(String id, String name)
        {
            _id = id;
            _name = name;
        }
        
        public String getId()
        {
            return _id;
        }

        public void setId(String newId)
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
