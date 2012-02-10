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

//= require ./55_utils

/*------------------------------------------------------------------------------
| jquery.55_utils.js
| 55 Minutes JS utilities v5.0
| Author(s): Richa Avasthi
| Created: 2009-12-02
|
| Some jQuery-based JavaScript utilities.
------------------------------------------------------------------------------*/


/*------------------------------------------------------------------------------
| dimensions(element, options)
|
| Return the width and height of the given element. If the "includeAll" flag is
| set to true, then return the outer width and height (including padding, border
| and margin) of the element. The "includePaddingAndBorder" flag includes
| padding and border, but not margin. NOTE that this function operates on the
| first matched element, and returns not the jQuery object, but an object
| containing the dimensions of the element.
------------------------------------------------------------------------------*/
jQuery.fn.dimensions = function(options) {
    var settings = jQuery.extend({
        includeAll: false,
        includePaddingAndBorder: false,
        includePadding: false
    }, options);

    var element = this.eq(0);

    // Include margin, border and padding
    if(settings.includeAll)
    {
        return {
            width: element.outerWidth(true),
            height: element.outerHeight(true)
        };
    }
    // Include border and padding
    if(settings.includePaddingAndBorder)
    {
        return {
            width: element.outerWidth(),
            height: element.outerHeight()
        };
    }
    // Include padding only
    if(settings.includePadding)
    {
        return {
            width: element.innerWidth(),
            height: element.innerHeight()
        };
    }
    // Do not include margin, border or padding
    return {
        width: element.width(),
        height: element.height()
    };
};


/*------------------------------------------------------------------------------
| getPositionedParentOffset(element)
|
| Return the offset of the given element's nearest positioned ancestor,
| providing that the ancestor is not the body element. NOTE that this function
| operates on the first matched element, and returns not the jQuery object, but
| an object containing the offset of the element's nearest positioned parent.
| Requirements: 55_utils
------------------------------------------------------------------------------*/
jQuery.fn.getPositionedParentOffset = function() {
    var parentOffset = { left: 0, top: 0 };
    var parent = this.eq(0).offsetParent();

    // If the given element exists and it's not the body element
    if(parent.length > 0 && parent.get(0).tagName.toLowerCase() != "body")
    {
        parentOffset = parent.offset();
        // If this browser is IE7, make sure to subtract the scroll offsets out.
        if(isIE(7))
        {
            parentOffset.left -= parent.scrollLeft();
            parentOffset.top -= parent.scrollTop();
        }
    }
    return parentOffset;
};


/*------------------------------------------------------------------------------
| isWithinBounds(position, element)
|
|   position:   A dictionary containing the coordinates, structured as follows:
|                   {x: ##, y: ##}
|   element:    A DOM element
|
| Return true if the coordinates given are within the bounds of the specified
| element.
------------------------------------------------------------------------------*/
function isWithinBounds(position, element)
{
    element = jQuery(element);
    var elementOffset = element.offset();
    var elementDimensions = element.dimensions({ includeAll: true });

    return (position.y >= elementOffset.top &&
            position.y <= elementOffset.top + elementDimensions.height &&
            position.x >= elementOffset.left &&
            position.x <= elementOffset.left + elementDimensions.width);
}


/*------------------------------------------------------------------------------
| viewportCoordinates()
|
| Return the coordinates of the top, right, bottom and left edges of the
| viewport relative to the page.
------------------------------------------------------------------------------*/
function viewportCoordinates()
{
    var viewport = jQuery(window);
    var viewportTop = viewport.scrollTop();
    var viewportLeft = viewport.scrollLeft();
    return {
        top: viewportTop,
        bottom: viewportTop + viewport.height(),
        left: viewportLeft,
        right: viewportLeft + viewport.width()
    };
}


/*------------------------------------------------------------------------------
| viewportOffset(element)
|
| Return the coordinates of four edges of the given element with respect to the
| viewport. NOTE that this function operates on the first matching element and
| returns not the jQuery object but a dictionary containing the viewport offset
| coordinates of the element.
------------------------------------------------------------------------------*/
jQuery.fn.viewportOffset = function() {
    var offset = this.eq(0).offset();
    var vcoords = viewportCoordinates();
    return {
        top: offset.top - vcoords.top,
        bottom: offset.top + this.outerHeight() - vcoords.bottom,
        left: offset.left - vcoords.left,
        right: offset.left + this.outerWidth() - vcoords.right
    };
};


/*------------------------------------------------------------------------------
| outsideViewport(element)
|
| Return boolean values indicating whether each of the four edges of the given
| element is outside of the viewport. NOTE that this function operates on the
| first matching element and  returns not the jQuery object but a dictionary
| containing the viewport collision status of each edge of the element.
------------------------------------------------------------------------------*/
jQuery.fn.outsideViewport = function() {
    var vOffset = this.eq(0).viewportOffset();
    return {
        top: vOffset.top < 0,
        right: vOffset.right > 0,
        bottom: vOffset.bottom > 0,
        left: vOffset.left < 0
    };
};

