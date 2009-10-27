package fiftyfive.wicket.spring;


import java.io.File;
import java.io.FileNotFoundException;
import javax.servlet.ServletContext;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer;


/**
 * Initializes log4j and replaces placeholders in the Spring context with
 * environment-specific values from an external properties file. Essentially
 * this is a drop-in replacement for 
 * {@link ServletContextPropertyPlaceholderConfigurer}, with log4j support.
 */
public class ConfigurationPostProcessor
    implements BeanFactoryPostProcessor, ServletContextAware
{
    private String _key;
    private ServletContext _servletContext;
    private PropertyPlaceholderConfigurer _configurer;
    
    
    /**
     * Creates a ConfigurationPostProcessor. This is not usually done
     * programmatically, but in the Spring context definition.
     */
    public ConfigurationPostProcessor()
    {
        ServletContextPropertyPlaceholderConfigurer sc =
            new ServletContextPropertyPlaceholderConfigurer();
            
        sc.setIgnoreUnresolvablePlaceholders(false);
        sc.setIgnoreResourceNotFound(false);
        sc.setContextOverride(true);
        
        _configurer = sc;
    }
    
    /**
     * Looks up the configuration file location using the provided key
     * (see {@link #setKey}) in system properties or the servlet context,
     * and uses the resulting file location to initialize log4j and
     * process properties placeholders.
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory)
    {
        String location = resolveLocationFromKey();
        File file = locateValidFile(location);
        
        System.out.println(
            "Initializing log4j and application settings with configuration " +
            "file: " + file
        );
        
        initLog4j(file);
        
        if(null == _configurer)
        {
            throw new IllegalStateException(
                "The \"configurer\" property of this bean must not be null."
            );
        }
        _configurer.setLocation(new FileSystemResource(file));
        _configurer.postProcessBeanFactory(factory);
    }
    
    /**
     * Sets the key that will be searched system properties and
     * the servlet context. This key must have a value of the configuration
     * file location.
     */
    public void setKey(String newKey)
    {
        _key = newKey;
    }
    
    /**
     * Sets the {@link PropertyPlaceholderConfigurer} that this processor
     * will use in order to perform the placeholder replacements.
     * By default this is an instance of
     * {@link ServletContextPropertyPlaceholderConfigurer} with the
     * following options set:
     * <ul>
     * <li>ignoreUnresolvablePlaceholders=false</li>
     * <li>ignoreResourceNotFound=false</li>
     * <li>contextOverride=true</li>
     * </ul>
     */
    public void setConfigurer(PropertyPlaceholderConfigurer newConfigurer)
    {
        _configurer = newConfigurer;
    }
    
    public void setServletContext(ServletContext newServletContext)
    {
        _servletContext = newServletContext;
    }
    
    protected File locateValidFile(final String location)
    {
        File file = null;
        try
        {
            file = ResourceUtils.getFile(location);
        }
        catch(FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
        if(null == file || !file.exists())
        {
            fail(
                "The configuration file for this application could not be " +
                "found at the following location: " + location + ". " +
                "This file is required for the application to start."
            );
        }
        return file;
    }
    
    /**
     * Initializes log4j with the given configuration file.
     */
    protected void initLog4j(File configLocation)
    {
        if(configLocation.getName().toLowerCase().endsWith(".xml"))
        {
            DOMConfigurator.configure(configLocation.getAbsolutePath());
        }
        else
        {
            PropertyConfigurator.configure(configLocation.getAbsolutePath());
        }
    }
    
    
    /**
     * Resolves the key by checking the following locations:
     * <ol>
     * <li>Searches the ServletContext for a context-param with that name.</li>
     * <li>Searches Java system properties for a property with that name.</li>
     * </ol>
     * 
     * @return The value of the matching context param or system property,
     *         translated into an appropriate URL by interpreting the optional
     *         classpath: prefix.
     * @throws RuntimeException if the key could not be found
     */
    protected String resolveLocationFromKey()
    {
        if(null == _key)
        {
            throw new IllegalStateException(
                "The \"key\" property of this bean must be specified."
            );
        }
        String loc = null;
        if(_servletContext != null)
        {
            loc = _servletContext.getInitParameter(_key);
        }
        if(null == loc)
        {
            loc = System.getProperty(_key);
        }
        if(null == loc)
        {
            fail(
                "No \"" + _key + "\" system property was specified. " +
                "You must specify a location for the application " +
                "configuration file using this property, by passing " +
                "-D" + _key + "=[file] in the command line arguments " +
                "for the JVM. Alternatively, you may specify a value " +
                "for " + _key + " as a context-param in the context " +
                "of the servlet container. The application cannot " +
                "start without its configuration file."
            );
        }
        return loc;
    }
    
    /**
     * Throws a RuntimeException with the given message, and also writes
     * the message to stderr.
     */
    protected void fail(String msg)
    {
        System.err.println(msg);
        throw new RuntimeException(msg);
    }
}
