#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.home;

import ${package}.BaseWicketUnitTest;
import fiftyfive.wicket.test.WicketTestUtils;
import org.junit.Test;
import org.apache.wicket.spring.test.ApplicationContextMock;

public class HomePageTest extends BaseWicketUnitTest
{
    protected void initSpringContext(ApplicationContextMock ctx)
    {
        // If HomePage had @SpringBean dependencies, you would mock them here.
        // Like this:
        // MockitoAnnotations.initMocks(this);
        // ctx.putBean(_mockSvc);
    }
    
    @Test
    public void testRender() throws Exception
    {
        _tester.startPage(HomePage.class);
        _tester.assertRenderedPage(HomePage.class);
        WicketTestUtils.assertValidMarkup(_tester);
    }
}
