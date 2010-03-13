/*
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
package fiftyfive.wicket.spring;

import fiftyfive.wicket.FoundationApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * An extension of {@link FoundationApplication} that additionally enables
 * SpringBean annotation support.
 *
 * @see SpringComponentInjector
 */
public abstract class FoundationSpringApplication extends FoundationApplication
{
    /**
     * Delegates to {@link #initSpring} after calling super.
     */
    @Override
    protected void init()
    {
        super.init();
        initSpring();
    }

    /**
     * Initializes the {@link SpringComponentInjector}. This allows you to use
     * SpringBean annotations in your Wicket pages and components, which is
     * the easiest way to integrate Wicket and Spring.
     *
     * @see <a href="http://cwiki.apache.org/WICKET/spring.html#Spring-AnnotationbasedApproach">http://cwiki.apache.org/WICKET/spring.html#Spring-AnnotationbasedApproach</a>
     */
    protected void initSpring()
    {
        addComponentInstantiationListener(new SpringComponentInjector(
            this, getApplicationContext(), true
        ));
    }

    /**
     * Override this method to change how the Spring context is located,
     * for example during unit tests. The default implementation simply
     * locates the Spring context by calling
     * {@link WebApplicationContextUtils#getRequiredWebApplicationContext}.
     */
    protected ApplicationContext getApplicationContext()
    {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(
            getServletContext()
        );
    }
}
