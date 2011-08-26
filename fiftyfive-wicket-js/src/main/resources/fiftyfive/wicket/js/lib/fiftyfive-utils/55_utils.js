/*
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

/*------------------------------------------------------------------------------
| 55_utils.js
| 55 Minutes JS utilities v2.5-pre
| Author: Richa Avasthi
| Created: 2010-01-26
|
| Some JS utility functions.
------------------------------------------------------------------------------*/


/*------------------------------------------------------------------------------
| isFirefox()
|
| The bare fact is that Firefox does not, either by mistake or by choice,
| support some CSS properties the way all (including IE) other browsers do.
| Use this function to sniff Firefox and add a class to your page, so you can
| write Firefox-only CSS hacks.      
------------------------------------------------------------------------------*/
function isFirefox()
{
    var m = navigator.userAgent.match(/Firefox\/(\d\.\d+)/);
    if(m)
    {
        return true;
    }
}

/*------------------------------------------------------------------------------
| isIE(threshold)
|
| Return true if the browser's user agent string indicates that it is a
| Microsoft Internet Explorer version. If a threshold version number parameter
| (optional) is passed in, return true if IE is that version or less.
------------------------------------------------------------------------------*/
function isIE(threshold)
{
    var m = navigator.userAgent.match(/MSIE (\d\.\d+)/);
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
}


/*------------------------------------------------------------------------------
| getIEClass()
|
| Return a list of classes to add to the body tag depending on the browser 
| version if the browser is Internet Explorer.
| Requirements: isIE()
| Inspired by:
|   <paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/>
------------------------------------------------------------------------------*/
function getIEClass()
{
    if(isIE(6))
        return "ie ie6 lte9 lte8 lte7 lte6";
    if(isIE(7))
        return "ie ie7 lte9 lte8 lte7";
    if(isIE(8))
        return "ie ie8 lte9 lte8";
    if(isIE(9))
        return "ie ie9 lte9";
    if(isIE())
        return "ie";
    return "";
}


/*------------------------------------------------------------------------------
| isMobile()
|
| Returns true if the browser is on an Android, PalmOS or iOS device. 
| Based on:
|   <http://stackoverflow.com/questions/3514784/best-way-to-detect-handheld-device-in-jquery>  
------------------------------------------------------------------------------*/
function isMobile()
{
    if( navigator.userAgent.match(/Android/i) ||
        navigator.userAgent.match(/webOS/i)   ||
        navigator.userAgent.match(/iPad/i)    ||
        navigator.userAgent.match(/iPhone/i)  ||
        navigator.userAgent.match(/iPod/i)       )
    {
        return true;
    }
    return false;
}

