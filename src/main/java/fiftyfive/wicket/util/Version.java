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
package fiftyfive.wicket.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.servlet.ServletContext;

/**
 * Loads version information from the JAR or WAR manifest, and also infers a
 * build timestamp based on the modification time of the manifest. Useful for
 * printing version information in an application for QA purposes.
 * <p>
 * Usage:
 * <pre>
 * Version v = Version.ofJar(MyApplication.class);
 * v.getVersion() // "1.0"
 * v.getModifiedDate() // Wed Feb 25 13:51:35 PST 2009
 * v.getHudsonBuildNumber() // 34 (assuming built with Hudson)
 * </pre>
 * For this to work, the version number needs to be included in the JAR
 * metadata. Maven will do this for you, but you must add the following
 * snippet to your POM:
 * <pre>
 * &lt;build&gt;
 *   &lt;plugins&gt;
 *     &lt;plugin&gt;
 *       &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
 *       &lt;artifactId&gt;maven-jar-plugin&lt;/artifactId&gt;
 *       &lt;configuration&gt;
 *         &lt;archive&gt;
 *           &lt;manifest&gt;
 *             &lt;addDefaultImplementationEntries&gt;
 *               true
 *             &lt;/addDefaultImplementationEntries&gt;
 *           &lt;/manifest&gt;
 *         &lt;/archive&gt;
 *       &lt;/configuration&gt;
 *     &lt;/plugin&gt;
 *   &lt;/plugins&gt;
 * &lt;/build&gt;
 * </pre>
 * In a WAR project, use the same snippet, except replace
 * <code>maven-jar-plugin</code> with <code>maven-war-plugin</code>.
 *
 * @author Matt Brictson
 */
public class Version
{
    private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";
    
    /**
     * Creates a Version object by loading the manifest for this webapp.
     * @throws RuntimeException if there is an error parsing the manfest
     */
    public static Version ofWebapp(ServletContext context)
    {
        try
        {
            return fromUrl(context.getResource(MANIFEST_PATH));
        }
        catch(MalformedURLException murle)
        {
            throw new RuntimeException(murle);
        }
    }

    /**
     * Creates a Version object by loading the manifest from the JAR
     * that contains the specified class.
     * @throws RuntimeException if there is an error parsing the manfest
     */
    public static Version ofJar(Class cls)
    {
        String url = null;
        
        // Construct the path to the class file and get its URL.
        // E.g. /org/apache/wicket/Component.class
        String path = "/" + cls.getName().replaceAll("\\.", "/") + ".class";
        URL classRsrc = cls.getResource(path);

        if(classRsrc != null)
        {
            // If if the class was in a JAR, its URL should look like this:
            // jar:file:/path/to/the/jar!/org/apache/wicket/Component.class
            
            // Replace the part after the ! with the manifest path.
            // That way we open the manifest stored in that particular JAR.
            
            if(classRsrc.toString().startsWith("jar:") &&
               classRsrc.toString().indexOf(path) > 0)
            {
                url = classRsrc.toString().replace(path, MANIFEST_PATH);
            }
        }
        try
        {
            return fromUrl(url != null ? new URL(url) : null);
        }
        catch(MalformedURLException ignore)
        {
            return fromUrl(null);
        }
    }

    private static Version fromUrl(URL url)
    {
        Version v = new Version();
        if(url != null)
        {
            InputStream stream = null;
            try
            {
                URLConnection conn = url.openConnection();
                v._modified = new Date(conn.getLastModified());
                
                stream = conn.getInputStream();
                v._manifest = new Manifest(stream);
            }
            catch(IOException ioe)
            {
                throw new RuntimeException(
                    "Failed to load manifest from " + url, ioe
                );
            }
            finally
            {
                try { if(stream != null) stream.close(); }
                catch(IOException e) {}
            }
        }
        return v;
    }
    
    private Manifest _manifest = new Manifest();
    private Date     _modified;
    
    
    /**
     * Returns the date when the application was built. This is determined
     * by looking at the modification time of the JAR/WAR.
     */
    public Date getModifiedDate()
    {
        return _modified;
    }
    
    /**
     * Returns the version number of the application, taken from the
     * Implementation-Version entry of the manifest. If the version is
     * not present or the manifest cannot be located, returns "unknown".
     */
    public String getVersion()
    {
        return getManifestMainAttribute(
            Attributes.Name.IMPLEMENTATION_VERSION, "unknown"
        );
    }
    
    /**
     * Returns the title of the application, taken from the
     * Implementation-Title entry of the manifest. If the name is
     * not present or the manifest cannot be located, return "unknown".
     */
    public String getTitle()
    {
        return getManifestMainAttribute(
            Attributes.Name.IMPLEMENTATION_TITLE, "unknown"
        );
    }
    
    /**
     * Returns the Hudson build number. Hudson automatically adds a build
     * number to the manifest under the entry Hudson-Build-Number.
     * If there is no Hudson build number present, return "unknown".
     */
    public String getHudsonBuildNumber()
    {
        return getManifestMainAttribute(
            new Attributes.Name("Hudson-Build-Number"), "unknown"
        );
    }
    
    /**
     * Returns the manifest where the version information is held. May be
     * empty.
     */
    public Manifest getManifest()
    {
        return _manifest;
    }
    
    /**
     * Returns the string value of the main attribute that has the specified
     * name. If the attribute is not present in the manifest, return the
     * defaultValue instead.
     */
    public String getManifestMainAttribute(Attributes.Name name,
                                           String defaultValue)
    {
        Attributes atts = _manifest.getMainAttributes();
        Object value = atts.get(name);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * Private constructor.
     * @see #ofClass
     */
    private Version()
    {
    }
}
