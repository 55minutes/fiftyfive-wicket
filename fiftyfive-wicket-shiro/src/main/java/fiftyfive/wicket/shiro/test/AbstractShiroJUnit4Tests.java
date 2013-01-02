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
package fiftyfive.wicket.shiro.test;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadState;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

/**
 * Enables {@link org.apache.wicket.util.tester.WicketTester WicketTester} to be used with
 * a Shiro-enabled Wicket application by mocking
 * the Shiro infrastructure. The code is not Wicket-specific, so you can use this for testing
 * non-Wicket code as well. Just make this the base class for your JUnit tests.
 * <b>Requires JUnit 4, Mockito.</b>
 * <p>
 * During your tests, mock the authentication state like this:
 * <pre class="example">
 * // Simulate authenticated user
 * when(this.mockSubject.isAuthenticated()).thenReturn(true);
 * 
 * // Simulate absence of "admin" role
 * doThrow(new AuthorizationException()).when(this.mockSubject).checkRole("admin");</pre>
 * <p>
 * If you're not using JUnit 4 and Mockito or otherwise can't subclass this class,
 * you can implement the same technique that this class uses with the testing frameworks of your
 * choice. This class simply does the following:
 * <pre class="example">
 * // Run before every test so that SecurityUtils.getSubject() returns a mock:
 * this.mockSubject = Mockito.mock(org.apache.shiro.subject.Subject.class);
 * this.threadState = new org.apache.shiro.subject.support.SubjectThreadState(this.mockSubject);
 * this.threadState.bind();
 * 
 * // Run after every test to clear the thread local:
 * this.threadState.clear();</pre>
 * 
 * @since 3.0
 */
public abstract class AbstractShiroJUnit4Tests
{
    private ThreadState threadState;
    
    /**
     * The Mockito mock that will be returned by
     * {@link org.apache.shiro.SecurityUtils#getSubject() SecurityUtils.getSubject()}.
     * Use this mock in your tests to mock authentication and authorization prerequisites.
     */
    protected Subject mockSubject;
    
    /**
     * The Mockito mock that will be returned by
     * {@link Subject#getSession() SecurityUtils.getSubject().getSession()}.
     */
    protected Session mockShiroSession;
    
    /**
     * Install a Mockito mock of {@link Subject} in Shiro's thread local. This causes the mock
     * to be returned when application code (and fiftyfive-wicket-shiro code) calls
     * {@link org.apache.shiro.SecurityUtils#getSubject() SecurityUtils.getSubject()}.
     */
    @Before
    public void attachSubject()
    {
        this.mockShiroSession = Mockito.mock(Session.class);
        this.mockSubject = Mockito.mock(Subject.class);
        Mockito.when(this.mockSubject.getSession()).thenReturn(this.mockShiroSession);
        this.threadState = new SubjectThreadState(this.mockSubject);
        this.threadState.bind();
    }
    
    /**
     * Clear Shiro's thread local so that any subject mocking we've done during the test does
     * not pollute subsequent tests.
     */
    @After
    public void detachSubject()
    {
        this.threadState.clear();
    }
}
