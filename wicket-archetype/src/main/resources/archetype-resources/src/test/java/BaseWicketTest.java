#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


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
        _tester = new WicketTester(new ${app_classname}() {
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
