/*
 * Copyright 2009 55 Minutes (http://www.55minutes.com)
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
| BooleanQueryBUilder.js
| 55 Minutes Wicket mini-project
| Author: Richa Avasthi
| Created: 2007-09-11
|
| Javascript function for creating mutually-exclusive checkbox pairs.
------------------------------------------------------------------------------*/


/*------------------------------------------------------------------------------
| getElementsByClassName()
|
| Get elements by class name--optimized.
|
|   Written by Jonathan Snook, http://www.snook.ca/jonathan
|   Add-ons by Robert Nyman, http://www.robertnyman.com
------------------------------------------------------------------------------*/
function getElementsByClassName(oElm, strTagName, oClassNames) {
    var arrElements = (strTagName == "*" && oElm.all)? oElm.all : oElm.getElementsByTagName(strTagName);
    var arrReturnElements = new Array();
    var arrRegExpClassNames = new Array();
    if(typeof oClassNames == "object"){
        for(var i=0; i<oClassNames.length; i++){
            arrRegExpClassNames.push(new RegExp("(^|\s)" + oClassNames[i].replace(/-/g, "\-") + "(\s|$)"));
        }
    }
    else{
        arrRegExpClassNames.push(new RegExp("(^|\s)" + oClassNames.replace(/-/g, "\-") + "(\s|$)"));
    }
    var oElement;
    var bMatchesAll;
    for(var j=0; j<arrElements.length; j++){
        oElement = arrElements[j];
        bMatchesAll = true;
        for(var k=0; k<arrRegExpClassNames.length; k++){
            if(!arrRegExpClassNames[k].test(oElement.className)){
                bMatchesAll = false;
                break;
            }
        }
        if(bMatchesAll){
            arrReturnElements.push(oElement);
        }
    }
    return (arrReturnElements)
}
// ---
// Array support for the push method in IE 5
if(typeof Array.prototype.push != "function"){
    Array.prototype.push = ArrayPush;
    function ArrayPush(value){
        this[this.length] = value;
    }
}
// ---


/*------------------------------------------------------------------------------
| toggleCheck()
|
| Replace checkboxes with images, and call this function when one of the images
| is clicked. This function takes care of swapping the images, and setting the
| values of the associated checkboxes, as well as modeling the mutual exclusion
| behavior of the plus-minus checkboxes. Note that this function depends on
| Robert Nyman's optimized getElementsByClassName function, provided above.
------------------------------------------------------------------------------*/
function toggleCheck(chkImg)
{
    /*
    ** Get the parent div, as well as the plus and minus checkboxes in the
    ** plus-minus widget.
    */
    var parentDiv = chkImg.parentNode;
    var plus = getElementsByClassName(parentDiv, "input", "plus")[0];
    var minus = getElementsByClassName(parentDiv, "input", "minus")[0];
    
    // Get the src attribute of this image
    var source = chkImg.attributes.getNamedItem("src").value;

    // The plus icon was clicked
    if(source.match(/add(-grey)?\.gif/))
    {
        // Toggle plus checkbox
        plus.checked = !plus.checked;

        // The box was checked
        if(source.match(/-grey/))
        {
            // Color in the image
            chkImg.setAttribute("src", source.replace(/-grey/, ""));

            // Uncheck minus checkbox
            minus.checked = false;
            var minusImg = getElementsByClassName(parentDiv, "img",
                                                  "check-graphic-minus")[0];
            var minusSrc = minusImg.attributes.getNamedItem("src").value;
            minusImg.setAttribute("src", minusSrc.replace(/remove.gif/, 
                                                          "remove-grey.gif"));
        }
        // The box was unchecked
        else
        {
            // Grey out the image
            chkImg.setAttribute("src", source.replace(/add/, 
                                                      "add-grey"));
        }
    }
    // The minus icon was clicked
    else if(source.match(/remove(-grey)?\.gif/))
    {
        // Toggle the minus checkbox
        minus.checked = !minus.checked;

        // The box was checked
        if(source.match(/-grey/))
        {
            // Color in the image
            chkImg.setAttribute("src", source.replace(/-grey/, ""));

            // Uncheck plus checkbox
            plus.checked = false;
            var plusImg = getElementsByClassName(parentDiv, "img",
                                                 "check-graphic-plus")[0];
            var plusSrc = plusImg.attributes.getNamedItem("src").value;
            plusImg.setAttribute("src", plusSrc.replace(/add.gif/, 
                                                        "add-grey.gif"));
        }
        // The box was unchecked
        else
        {
            // Grey out the image
            chkImg.setAttribute("src", source.replace(/remove/, 
                                                      "remove-grey"));
        }
    }
}

