#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.home;

import ${package}.BaseWicketUnitTest;
import fiftyfive.wicket.test.WicketTestUtils;
import org.junit.Test;
import org.springframework.web.context.support.StaticWebApplicationContext;

public class HomePageTest extends BaseWicketUnitTest
{
    @Override
    protected void initSpringContext(StaticWebApplicationContext ctx)
    {
        // If HomePage had @SpringBean dependencies, you would mock them here.
        // Like this:
        // MockitoAnnotations.initMocks(this);
        // ctx.getBeanFactory().registerSingleton("svc", _mockSvc);
    }
    
    @Test
    public void testRender() throws Exception
    {
        _tester.startPage(HomePage.class);
        _tester.assertRenderedPage(HomePage.class);
        WicketTestUtils.assertValidMarkup(_tester);
    }
}
