/**
 * Copyright 2011 55 Minutes (http://www.55minutes.com)
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

import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * Base class for fiftyfive-wicket-js unit tests.
 * Sets up WicketTester and Mockito.
 */
public abstract class BaseJSTest
{
    protected WicketTester _tester;
    
    @Before
    public void setUp()
    {
        _tester = new WicketTester();
        MockitoAnnotations.initMocks(this);
    }
    
    @After
    public void tearDown()
    {
        if(_tester != null) _tester.destroy();
    }
}
