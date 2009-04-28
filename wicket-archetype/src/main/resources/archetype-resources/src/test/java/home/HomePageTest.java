#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.home;

import ${package}.BaseWicketTest;
import fiftyfive.wicket.test.XHtmlValidator;
import org.junit.Test;

public class HomePageTest extends BaseWicketTest
{
    @Test
    public void testRender() throws Exception
    {
        _tester.startPage(HomePage.class);
        _tester.assertRenderedPage(HomePage.class);
        XHtmlValidator.assertValidMarkup(_tester);
    }
}
