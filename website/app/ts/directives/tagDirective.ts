/**
 * Directive for outputting tag information
 */
docsApp.directive("tags", function($location) {
    var definition: ng.IDirective = {
        // Restrict to elements and attributes
        restrict: 'EA',
        // Scope - Evaluate the source value
        scope: {
            "source": "="
        },
        // Template Url
        templateUrl: "templates/partials/tagPartial.html"
    };
    return definition;
});