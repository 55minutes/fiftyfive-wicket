/*
 * Copyright 2012 55 Minutes (http://www.55minutes.com)
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

//= require ./feature-detect

/*------------------------------------------------------------------------------
| jquery.ui.forminputplaceholdertext.js
| 55 Minutes JS utilities v5.0
| Author(s): Richa Avasthi
| Created: 2011-03-21
------------------------------------------------------------------------------*/

(function($) {
    /*--------------------------------------------------------------------------
    | forminputplaceholdertextwidget
    |
    | A jQuery UI-based widget for supporting placeholder text inside text
    | inputs and textareas whether or not the browser supports the HTML5
    | placeholder attribute.
    |
    | To use: $("input[placeholder]").forminputplaceholdertextwidget();
    |         The widget will work on text inputs and textareas as well.
    |         Options may be specified by passing the options dictionary like so:
    |
    |         $("input[placeholder]").forminputplaceholdertextwidget({
    |             options: {
    |                 customLabelStyle: {
    |                     <parameters for jQuery's css() function>
    |                 }
    |             }
    |         });
    | Requirements: jquery-1.5+, jquery-ui-1.8.widget+, feature-detect
    --------------------------------------------------------------------------*/
    $.widget("ui.forminputplaceholdertextwidget", {
        // Set default options
        options: {
            customLabelStyle: {
                display: "inline-block",
                position: "absolute",
                cursor: "text",
                color: "#a0a0a0"
            }
        },

        _create: function() {
            // Cache reference to options
            var opts = this.options;

            this.useJS = false;
            this.useJS = !(fiftyfive.featureDetect.placeholder());

            if(this.useJS)
            {
                this.element.wrap("<div class=\"placeholder-wrapper\" />");
                this.placeholderWrapper = this.element.closest("div.placeholder-wrapper");
                this.placeholderWrapper.css({
                    display: "inline-block",
                    position: "relative",
                    width: this.element.outerWidth(),
                    height: this.element.outerHeight()
                });

                this.element.before(
                    "<label class=\"placeholder-text\">" +
                    this.element.attr("placeholder") +
                    "</label>"
                );
                this.placeholderTextLabel = this.element.prev("label.placeholder-text");

                var borderWidth = this.element.css("border-width").replace(/px/, "") * 1;
                this.placeholderTextLabel.css($.extend({
                    top:  borderWidth +
                          this.element.css("padding-top").replace(/px/, "") * 1,
                    left: borderWidth +
                          this.element.css("padding-left").replace(/px/, "") * 1,
                    fontSize: this.element.css("font-size"),
                    lineHeight: this.element.css("line-height")
                }, opts.customLabelStyle));

                if($.trim(this.element.val()) == "")
                {
                    this.placeholderTextLabel.show();
                }
                else
                {
                    this.placeholderTextLabel.hide();
                }

                var self = this;
                this.element.focus(function() {
                    self.placeholderTextLabel.hide();
                }).blur(function() {
                    if($.trim(self.element.val()) == "")
                    {
                        self.placeholderTextLabel.show();
                    }
                });

                this.placeholderTextLabel.click(function() {
                    self.element.focus();
                });
            }
        }
    });

    $(function() {
        // Stuff to do as soon as the DOM is ready
    });
})(jQuery);
