//= require <jquery>
//= require <55_utils>

// If browser is a flavor of IE, add an appropriate class to the body element.
// This allows conditional styling for IE.
(function($) {
    $(function() {
        $("body").addClass(getIEClass());
    });
})(jQuery);
