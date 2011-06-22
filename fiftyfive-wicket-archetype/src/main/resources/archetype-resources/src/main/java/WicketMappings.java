package ${package};

import ${package}.error.ForbiddenErrorPage;
import ${package}.error.InternalServerErrorPage;
import ${package}.error.NotFoundErrorPage;
import ${package}.home.HomePage;

import fiftyfive.wicket.js.MergedJavaScriptBuilder;
import fiftyfive.wicket.mapper.PatternMountedMapper;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.CompoundRequestMapper;


/**
 * All custom mappings (in other words, "mount points" or "routes")
 * for ${project_name}.
 * This includes merged JavaScript and pretty URLs for all bookmarkable pages.
 */
public class WicketMappings extends CompoundRequestMapper
{
    public WicketMappings(WebApplication app)
    {
        // Pretty URLs for bookmarkable pages
        addPage("error/403", ForbiddenErrorPage.class);
        addPage("error/404", NotFoundErrorPage.class);
        addPage("error/500", InternalServerErrorPage.class);
        
        // Common JavaScript merged together and mapped to scripts/all.js
        add(new MergedJavaScriptBuilder()
            .setPath("/scripts/all.js")
            .addJQueryUI()
            .addLibrary("cookies")
            .addLibrary("strftime")
            .addLibrary("55_utils")
            .addLibrary("jquery.55_utils")
            .addAssociatedScript(BasePage.class)
            .addWicketAjaxLibraries()
            .buildRequestMapper(app));
    }
    
    private void addPage(String path, Class<? extends Page> page)
    {
        add(new PatternMountedMapper(path, page).setExact(true));
    }
}
