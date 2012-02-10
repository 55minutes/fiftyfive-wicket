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

/*------------------------------------------------------------------------------
| 55_utils.js
| 55 Minutes JS utilities v5.0
| Author: Richa Avasthi
| Created: 2010-01-26
|
| Some JS utility functions.
------------------------------------------------------------------------------*/


// Establish namespace
var fiftyfive = window.fiftyfive = window.fiftyfive ? window.fiftyfive : {};
fiftyfive.util = fiftyfive.util ? fiftyfive.util : {};

/*------------------------------------------------------------------------------
| isIE(threshold)
|
| Return true if the browser's user agent string indicates that it is a
| Microsoft Internet Explorer version. If a threshold version number parameter
| (optional) is passed in, return true if IE is that version or less.
------------------------------------------------------------------------------*/
fiftyfive.util.isIE = function(threshold) {
    var m = navigator.userAgent.match(/MSIE (\d+\.\d+)/);
    if(m)
    {
        if(threshold)
        {
            if(parseFloat(m[1]) < Math.floor(threshold + 1.0))
            {
                return true;
            }
            return false;
        }
        return true;
    }
    return false;
};


/*------------------------------------------------------------------------------
| getIEClass()
|
| Return a list of classes to add to the body tag depending on the browser
| version if the browser is Internet Explorer.
| Requirements: isIE()
| Inspired by:
|   <paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/>
------------------------------------------------------------------------------*/
fiftyfive.util.getIEClass = function() {
    var fu = fiftyfive.util;
    if(fu.isIE(6))
        return "ie ie6 lte9 lte8 lte7 lte6";
    if(fu.isIE(7))
        return "ie ie7 lte9 lte8 lte7";
    if(fu.isIE(8))
        return "ie ie8 lte9 lte8";
    if(fu.isIE(9))
        return "ie ie9 lte9";
    if(fu.isIE())
        return "ie";
    return "";
};


/*------------------------------------------------------------------------------
| isMobile()
|
| Returns true if the browser is on an Android, PalmOS or iOS device.
| Based on:
|   <http://stackoverflow.com/questions/3514784/best-way-to-detect-handheld-device-in-jquery>
------------------------------------------------------------------------------*/
fiftyfive.util.isMobile = function() {
    if( navigator.userAgent.match(/Android/i) ||
        navigator.userAgent.match(/webOS/i)   ||
        navigator.userAgent.match(/iPad/i)    ||
        navigator.userAgent.match(/iPhone/i)  ||
        navigator.userAgent.match(/iPod/i)       )
    {
        return true;
    }
    return false;
};


/*------------------------------------------------------------------------------
| deviceOrientation()
|
| Return "portrait" or "landscape" based on the screen orientation.
------------------------------------------------------------------------------*/
fiftyfive.util.deviceOrientation = function() {
    var orientation = window.orientation;
    if(orientation == 0 || orientation == 180)
    {
        return "portrait";
    }
    else if(orientation == 90 || orientation == -90)
    {
        return "landscape";
    }
    else
    {
        return null;
    }
};


