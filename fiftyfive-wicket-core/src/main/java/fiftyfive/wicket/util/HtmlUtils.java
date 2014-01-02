/**
 * Copyright 2014 55 Minutes (http://www.55minutes.com)
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

import org.apache.wicket.util.string.Strings;

/**
 * Utility methods for dealing with HTML.
 * 
 * @since 2.0
 */
public class HtmlUtils
{
    /**
     * Returns a value safe for using inside an (X)HTML attribute.
     * Replaces {@code "} with {@code &quot;} and {@code &} with {@code &amp;}.
     */
    public static CharSequence escapeAttribute(CharSequence value)
    {
        return Strings.replaceAll(
            Strings.replaceAll(value, "&", "&amp;"),
            "\"", "&quot;"
        );
    }
    
    /**
     * Not intented to be instantiated.
     */
    private HtmlUtils()
    {
        super();
    }
}
