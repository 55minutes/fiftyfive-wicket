package ${package}.admin;

import ${package}.BaseWicketUnitTest;
import ${package}.home.HomePage;
import ${package}.security.LoginPage;

import org.apache.shiro.authz.AuthorizationException;
import org.junit.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * A simple demonstration of how to test Wicket pages that are secured via Shiro annotations.
 * We use the {@code mockSubject} provided by {@code BaseWicketUnitTest} to simulate whether the
 * user is currently logged in, and if so what role the user has. We can then verify that
 * the AdministrationPage allows or denies access in these different scenarios.
 */
public class AdministrationPageTest extends BaseWicketUnitTest
{
    @Test
    public void testRender_asUnauthenticated() throws Exception
    {
        doRender(false, false);
        this.tester.assertRenderedPage(LoginPage.class);
    }
    
    @Test
    public void testRender_asUnauthorized() throws Exception
    {
        doRender(true, false);
        this.tester.assertRenderedPage(HomePage.class);
        this.tester.assertErrorMessages("Sorry, you are not allowed to access that page.");
    }
    
    @Test
    public void testRender_asAuthorized() throws Exception
    {
        doRender(true, true);
        this.tester.assertRenderedPage(AdministrationPage.class);
    }
    
    private void doRender(boolean authenticated, boolean isAdmin)
    {
        when(this.mockSubject.isAuthenticated()).thenReturn(authenticated);
        if(!isAdmin)
        {
            doThrow(new AuthorizationException()).when(this.mockSubject).checkRole("admin");
        }
        this.tester.startPage(AdministrationPage.class);
    }
}
