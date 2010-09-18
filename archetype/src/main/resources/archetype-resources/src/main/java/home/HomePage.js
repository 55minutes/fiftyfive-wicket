//= require <jquery>
//= require <strftime>

/*------------------------------------------------------------------------------
| HomePage.js
| Project name
| Author: Name
| Created: yyyy-mm-dd
|
| Add javascript utilities for the home page here.  File naming should match
| the Java/HTML file names for the page these scripts are used in.
------------------------------------------------------------------------------*/

function updateTimestamp(event)
{
  d = new Date();
  jQuery("#timestamp").text(d.strftime("%a %D %b %Y %H:%M:%S"));
  if(event)
  {
    event.preventDefault();
    event.stopPropagation();
  }
  return false;
}

(function($) {
    $(function() {
        updateTimestamp();
        $("#update-timestamp").click(updateTimestamp);
    });
})(jQuery);
