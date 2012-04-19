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

//= require ./cookies

/*------------------------------------------------------------------------------
| jquery.ui.unsupportedbrowserwarning.js
| 55 Minutes JS utilities v5.0
| Author(s): Richa Avasthi
| Created: 2011-11-02
------------------------------------------------------------------------------*/

(function($) {
    /*--------------------------------------------------------------------------
    | unsupportedbrowserwarningwidget
    |
    | A jQuery UI-based widget for managing the unsupported browser warning. To
    | use, simply initialize this widget on #unsupported-browser-warning.
    | Requirements: jquery-1.6.4, jquery-ui-1.8.16.widget, cookies
    --------------------------------------------------------------------------*/
    $.widget("ui.unsupportedbrowserwarningwidget", {
        // Default options
        options: {
            // The name of the cookie to be saved.
            cookieName: "unsupportedBrowserWarningDismissed",
            // The number of days the cookie will remain valid.
            duration: 1,
            // The path for which the cookie applies
            path: "/"
        },

        _create: function() {
            this.dismissLink = this.element.find("a.dismiss");

            /*
            ** Set the dismissal cookie if the user clicks the dismiss link.
            */
            var self = this;
            this.dismissLink.click(function(event) {
                setCookie(self.options.cookieName, true, self.options.duration, self.options.path);
                self.element.fadeOut("slow");
            });

            /*
            ** If the dismissal cookie is set, hide the warning.
            */
            if(getCookie(this.options.cookieName))
            {
                this.element.hide();
            }
        }
    });
})(jQuery);

