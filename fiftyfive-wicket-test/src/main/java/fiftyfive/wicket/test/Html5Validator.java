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
package fiftyfive.wicket.test;

import javax.xml.parsers.DocumentBuilder;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

/**
 * Utility for parsing HTML5 documents and verifying that they are valid.
 * Uses code provided by <a href="http://validator.nu/">validator.nu</a>.
 * You should not have to use this class directly; it will be auto-selected
 * by helper methods in {@link WicketTestUtils}.
 */
public class Html5Validator extends AbstractDocumentValidator
{
    /**
     * Constructs and returns an HTML5 HtmlDocumentBuilder.
     */
    protected DocumentBuilder builder()
    {
        HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
        builder.setErrorHandler(this);
        return builder;
    }
}
