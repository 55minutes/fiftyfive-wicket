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
package fiftyfive.wicket;


import org.apache.wicket.util.tester.DummyHomePage;
import org.apache.wicket.util.tester.WicketTester;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public abstract class BaseWicketTest
{
    protected WicketTester _tester;
    
    @Before
    public void createTester()
    {
        _tester = new WicketTester(new FoundationApplication() {
            public Class getHomePage()
            {
                return DummyHomePage.class;
            }
            @Override protected void init()
            {
                super.init();
                getResourceSettings().setAddLastModifiedTimeToResourceReferenceUrl(false);
            }
            @Override protected void initSpring()
            {
                // disable for testing
            }
        });
    }
    
    @After
    public void destroyTester()
    {
        if(_tester != null)
        {
            _tester.destroy();
        }
    }
}
