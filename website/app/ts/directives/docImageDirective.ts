/**
 * Represents a directive which should replace all doc-images with the correct path
 */
docsApp.directive("docImg", function($location) {
    var definition: ng.IDirective = {
        // Restrict to attributes
        restrict: 'A',
        scope: {},
        link: function (scope?:any, element?:ng.IAugmentedJQuery, attrs?:ng.IAttributes) {
            var src = element.attr("src");

            // Extract the file name relative to our document image source directory
            var newSrc = "docs_images/" + src.replace(/^.*[\\\/]/, '')
            element.attr("src", newSrc)
        }
    };
    return definition;
});