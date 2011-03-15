//= require <jquery>
//= require <55_utils>

// If browser is a flavor of IE, add an appropriate class to the html element.
// This allows conditional styling for IE.
(function($) {
    $(function() {
        $("html").addClass(getIEClass());
    });
})(jQuery);
