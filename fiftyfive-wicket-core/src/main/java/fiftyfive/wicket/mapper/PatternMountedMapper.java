/**
 * Copyright 2011 55 Minutes (http://www.55minutes.com)
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

package fiftyfive.wicket.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.wicket.request.Request;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.util.ClassProvider;
import org.apache.wicket.util.string.StringValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An improved version of Wicket's standard {@link MountedMapper} that additionally allows
 * regular expressions inside placeholders. This feature is inspired by the pattern matching
 * behavior of the JAX-RS {@code @Path} annotation.
 * <pre class="example">
 * mount(new PatternMountedMapper("people/${personId:\\d+}", PersonPage.class));</pre>
 * This will map URLs like {@code people/12345} but yield a 404 not found for something like
 * {@code people/abc} since {@code abc} doesn't match the {@code \d+} regular expression.
 * 
 * @since 3.0
 */
public class PatternMountedMapper extends MountedMapper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternMountedMapper.class);

    private final int numSegments;
    private final List<PatternPlaceholder> patternPlaceholders;
    private boolean exact = false;

    /**
     * {@inheritDoc}
     */
    public PatternMountedMapper(String mountPath, Class<? extends IRequestablePage> pageClass)
    {
        this(mountPath, pageClass, new PageParametersEncoder());
    }

    /**
    * {@inheritDoc}
     */
    public PatternMountedMapper(String mountPath,
                                ClassProvider<? extends IRequestablePage> pageClassProvider)
    {
        this(mountPath, pageClassProvider, new PageParametersEncoder());
    }


    /**
    * {@inheritDoc}
     */
    public PatternMountedMapper(String mountPath,
                                Class<? extends IRequestablePage> pageClass,
                                IPageParametersEncoder pageParametersEncoder)
    {
        this(mountPath, ClassProvider.of(pageClass), pageParametersEncoder);
    }

    /**
    * {@inheritDoc}
     */
    public PatternMountedMapper(String mountPath,
                                ClassProvider<? extends IRequestablePage> pageClassProvider,
                                IPageParametersEncoder pageParametersEncoder)
    {
        super(removePatternsFromPlaceholders(mountPath), pageClassProvider, pageParametersEncoder);

        String[] segments = getMountSegments(mountPath);
        this.numSegments = segments.length;
        this.patternPlaceholders = new ArrayList<PatternPlaceholder>(1);
        for(String seg: segments)
        {
            String placeholder = getPlaceholder(seg);
            if(placeholder != null)
            {
                this.patternPlaceholders.add(new PatternPlaceholder(placeholder));
            }
        }
    }
    
    /**
     * Set to {@code true}, to force this mapper to strictly match URLs by disallowing any extra
     * path elements that come after the matched pattern.
     * <pre class="example">
     * PatternMountedMapper m = new PatternMountedMapper(MyPage.class, "page/${id:\\d+}");
     * // These will always be matched: "page/1", "page/2", "page/30", etc.
     * // By default, these will be matched as well: "page/1/whatever/foo/bar", "page/2/baz"
     * m.setExact(true);
     * // Now these will not be matched: "page/1/whatever/foo/bar", "page/2/baz"</pre>
     * 
     * In other words, if {@code exact} is set to {@code false}, extra path elements after the
     * specified pattern will be allowed. The default is {@code false}, to match the default
     * behavior of Wicket's {@link MountedMapper}.
     * 
     * @return {@code this} to allow chaining
     */
    public PatternMountedMapper setExact(boolean exact)
    {
        this.exact = exact;
        return this;
    }
    
    /**
     * First delegate to the superclass to parse the request as normal, then additionally
     * verify that all regular expressions specified in the placeholders match.
     */
    @Override
    protected UrlInfo parseRequest(Request request)
    {
        // Parse the request normally. If the standard impl can't parse it, we won't either.
        UrlInfo info = super.parseRequest(request);
        if(null == info || null == info.getPageParameters())
        {
            return info;
        }
        
        // If exact matching, reject URLs that have more than expected number of segments
        if(exact)
        {
            int requestNumSegments = request.getUrl().getSegments().size();
            if(requestNumSegments > this.numSegments)
            {
                return null;
            }
        }
        
        // Loop through each placeholder and verify that the regex of the placeholder matches
        // the value that was provided in the request url. If any of the values don't match,
        // immediately return null signifying that the url is not matched by this mapper.
        PageParameters params = info.getPageParameters();
        for(PatternPlaceholder pp : getPatternPlaceholders())
        {
            List<StringValue> values = params.getValues(pp.getName());
            if(null == values || values.size() == 0)
            {
                values = Arrays.asList(StringValue.valueOf(""));
            }
            for(StringValue val : values)
            {
                if(!pp.matches(val.toString()))
                {
                    if(LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug(String.format(
                            "Parameter \"%s\" did not match pattern placeholder %s", val, pp));
                    }
                    return null;
                }
            }
        }
        return info;
    }
    
    /**
     * The list of placeholders (in other words, the <code>${name:regex}</code> components of the
     * mount path).
     */
    protected List<PatternPlaceholder> getPatternPlaceholders()
    {
        return this.patternPlaceholders;
    }
    
    /**
     * Remove the regular expression portion of all placeholders from the given path so that
     * the standard {@link MountedMapper} isn't confused by them. This allows us to reuse all 
     * the existing code of the superclass. This method must be static because we need to call it
     * before invoking the superclass constructor.
     * <pre class="example">
     * removePatternsFromPlaceholders("people/${personId:\\d+}");
     * // "people/${personId}"</pre>
     */
    protected static String removePatternsFromPlaceholders(String path)
    {
        if(null == path)
        {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String [] segments = path.split("/");
        for(int i=0; i<segments.length; i++)
        {
            String seg = segments[i];
            result.append(seg.replaceAll("^(\\$\\{[^:]+):.+\\}$", "$1}"));
            if(i < segments.length-1 || path.endsWith("/"))
            {
                result.append("/");
            }
        }
        return result.toString();
    }
    
    /**
     * Represents a placeholder that optionally contains a regular expression.
     */
    protected static class PatternPlaceholder
    {
        private final String placeholder;
        private final String pattern;
        private final String name;
        
        public PatternPlaceholder(String placeholder)
        {
            this.placeholder = placeholder;

            int colon = placeholder.indexOf(":");
            if(colon > 0 && colon < placeholder.length() - 2)
            {
                this.name = placeholder.substring(0, colon);
                this.pattern = placeholder.substring(colon + 1);
            }
            else
            {
                this.name = placeholder;
                this.pattern = null;
            }
        }
        
        /**
         * Return {@code true} if this placeholder has a regex pattern and that pattern matches
         * the specified value. If this placeholder doesn't have a regex, always return
         * {@code true} always.
         */
        public boolean matches(CharSequence value)
        {
            return null == this.pattern || Pattern.matches(this.pattern, value);
        }
        
        /**
         * The name of this placeholder with the {@code :regex} portion removed.
         */
        public String getName()
        {
            return this.name;
        }
        
        /**
         * For debugging.
         */
        public String toString()
        {
            return "${" + this.placeholder + "}";
        }
    }
}
