#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.home;

import ${package}.BaseWicketUnitTest;
import fiftyfive.wicket.test.XHtmlValidator;
import org.junit.Test;
import org.springframework.web.context.support.StaticWebApplicationContext;

public class HomePageTest extends BaseWicketUnitTest
{
    protected void initSpringContext(StaticWebApplicationContext ctx)
    {
        // If HomePage had @SpringBean dependencies, you would mock them here.
        // Like this:
        // MockitoAnnotations.initMocks(this);
        // ctx.getBeanFactory().registerSingleton("serviceBeanId", _mockSvc);
    }
    
    @Test
    public void testRender() throws Exception
    {
        _tester.startPage(HomePage.class);
        _tester.assertRenderedPage(HomePage.class);
        XHtmlValidator.assertValidMarkup(_tester);
    }
}
