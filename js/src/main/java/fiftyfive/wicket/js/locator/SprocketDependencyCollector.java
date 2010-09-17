/**
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

package fiftyfive.wicket.js.locator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SprocketDependencyCollector extends SprocketParser
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
        SprocketDependencyCollector.class
    );
    
    private JavaScriptDependencyLocator _locator;
    
    public SprocketDependencyCollector(JavaScriptDependencyLocator locator)
    {
        super();
        _locator = locator;
    }
    
    public void collectDependencies(ResourceReference ref,
                                    IResourceStream stream,
                                    DependencyCollection dependencies)
    {
        if(null == stream) return;
        
        // Parse the resource, looking for Sprocket dependency declarations
        LOGGER.debug("Parsing: {}", ref.getName());
        List<Sprocket> sprockets = parseSprockets(stream);
        Collections.reverse(sprockets);
        
        // After parsing is complete, loop through what we found and
        // process their dependencies recursively.
        for(Sprocket sp : sprockets)
        {
            if(sp.isLibrary())
            {
                _locator.findLibraryScripts(sp.getPath(), dependencies);
            }
            else
            {
                Class<?> scope = ref.getScope();
                _locator.findResourceScripts(
                    scope,
                    concatPaths(ref.getName(), sp.getPath()),
                    dependencies
                );
            }
        }
    }
    
    private List<Sprocket> parseSprockets(IResourceStream stream)
    {
        // TODO: allow encoding to be customized
        try
        {
            InputStream is = stream.getInputStream();
            return parseSprockets(
                new BufferedReader(new InputStreamReader(is, "UTF-8"))
            );
        }
        catch(IOException ioe)
        {
            throw new WicketRuntimeException(ioe);
        }
        catch(ResourceStreamNotFoundException rsnfe)
        {
            throw new WicketRuntimeException(rsnfe);
        }
        finally
        {
            try { stream.close(); } catch(Exception ignore) {}
        }
    }
    
    private String concatPaths(String orig, String relative)
    {
        if(null == orig || orig.indexOf("/") == -1)
        {
            return relative;
        }
        return orig.substring(0, orig.lastIndexOf("/") + 1) + relative;
    }
}
