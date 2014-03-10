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
    // List of known allowed mime types - other files will be ignored
    var allowedMimeTypes = [
        "image/png", "image/jpeg", "image/gif"
    ];

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
        controller: $scope => {

            $scope.images = [ ];

            $scope.deleteImage = (image) => {
                var images = $scope.images;

                // Splice the position of the image if it is contained within our images list
                var index = images.indexOf(image);
                if(index == -1) return;
                images.splice(index, 1);
            };
        },
        // Provide linking mechanism which allows drag/drop functionality
        link: function (scope?:any, element?:ng.IAugmentedJQuery, attrs?:ng.IAttributes) {
            // Target element
            var targetDragDrop = element.find(".target");

            // Binds a class to the css element which matches the passed in boolean argument
            var bindClass = (options: {isAdd: boolean}, e) => {
                var targetClasses = "dragged";
                e.preventDefault();
                targetDragDrop[options.isAdd ? "addClass" : "removeClass"](targetClasses);
            }

            // Define handlers for drag/drop support
            var handlers = {
                "dragover": (e:JQueryEventObject) => {
                    // Bind Class
                    bindClass({isAdd: true}, e);
                },
                "dragleave": (e:JQueryEventObject) => {
                    // Remove class
                    bindClass({isAdd: false}, e);
                },
                "drop": (e:JQueryEventObject) => {
                    // Remove class
                    bindClass({isAdd: false}, e);

                    var handleFile = (file:File) => {
                        // Validate file
                        if(allowedMimeTypes.indexOf(file.type) === -1) {
                            console.log("Error file type :: " + file.type);
                            return;
                        }

                        var fileReader = new FileReader();
                        fileReader.onload = (e) => {
                            var text = e.target.result;

                            console.log("Dropped successfully");

                            scope.$apply(function() {
                                scope.images.push({
                                    location: text,
                                    title: "",
                                    description: ""
                                });
                            });
                        };

                        fileReader.readAsDataURL(file);
                    };

                    var files = (<any> e.originalEvent).dataTransfer.files;

                    for(var i = 0; i < files.length; i++) {
                        var file = files[i];
                        handleFile(file);
                    };
                }
            };

            // Bind known events to the css element
            for(var key in handlers) {
                targetDragDrop.bind(key, handlers[key]);
            }
        }
    };
    return definition;
});