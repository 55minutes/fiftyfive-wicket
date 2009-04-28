#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import fiftyfive.wicket.FoundationApplication;

import ${package}.home.HomePage;

import org.apache.wicket.Request;
import org.apache.wicket.Response;

import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

/**
 * Wicket framework configuration for ${project_name}.
 */
public class ${app_classname} extends FoundationApplication
{
    @Override
    public Class getHomePage()
    {
        return HomePage.class;
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        // Enable annotations for mounting pages
        AnnotatedMountScanner scanner = new AnnotatedMountScanner();
        scanner.scanPackage("${package}").mount(this);

        // Custom initialization goes here
    }
    
    @Override
    public ${session_classname} newSession(Request request, Response response)
    {
        return new ${session_classname}(request);
    }
}
