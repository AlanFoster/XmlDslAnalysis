"use strict";

// Directive interface for menu item scope
interface IFileDropScope extends ng.IScope {
    images: string[]
    currentlySelected: any
}

/**
 * Directive for uploading multiple images from the browser
 */
docsApp.directive("imageUploader", function () {
    var definition:ng.IDirective = {
        // Restrict to elements and attributes
        restrict: 'EA',
        // Scope - Use two-way binding
        scope: {
            "images": "="
        },
        // Replace the original DOM element, otherwise we would run into layout issues
        replace: true,
        // Raw template
        templateUrl: "templates/partials/imageUploader.html",
        // Provide linking mechanism which allows drag/drop functionality
        link: function (scope?:any, element?:ng.IAugmentedJQuery, attrs?:ng.IAttributes) {

            scope.toggleSelected = (image) => {
                scope.currentlySelected = image;
            }

            scope.currentlySelected = undefined;

            // Hardcode path for testing
            scope.images = [
                {
                    location: "images/contribution.png",
                    title: "Title 1",
                    description: "Description 1"
                },
                {
                    location: "images/paramInsight.png",
                    title: "Param Insight",
                    description: "Param Insight Description"
                }
            ];

            scope.deleteImage = (image) => {
                var images = scope.images;

                // Splice the position of the image if it is contained within our images list
                var index = images.indexOf(image);
                if(index == -1) return;
                images.splice(index, 1);
            };
        }
    };
    return definition;
});