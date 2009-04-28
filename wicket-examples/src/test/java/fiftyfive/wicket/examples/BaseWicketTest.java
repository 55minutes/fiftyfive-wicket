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
package fiftyfive.wicket.examples;


import org.apache.wicket.util.tester.WicketTester;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = {
    "classpath:/spring/configuration.xml", 
    "classpath:/spring/beans.xml"
})
public abstract class BaseWicketTest extends AbstractJUnit4SpringContextTests
{
    protected WicketTester _tester;
    
    @Before
    public void createTester()
    {
        _tester = new WicketTester(new ExampleApplication() {
            @Override protected ApplicationContext getApplicationContext()
            {
                return applicationContext;
            }
            @Override protected void outputDevelopmentModeWarning()
            {
                // do nothing
            }
        });
    }
    
    @After
    public void destroyTester()
    {
        _tester.destroy();
    }
}
