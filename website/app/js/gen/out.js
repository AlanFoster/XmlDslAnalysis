"use strict";
var docsApp = angular.module('docsApp', [
    "ui.bootstrap",
    "bootstrap-tagsinput",
    "ngRoute",
    "ngSanitize",
    "ngResource"
]).config(function ($routeProvider, $locationProvider) {
    $routeProvider.when("/", {
        templateUrl: "/templates/overview.html",
        controller: "overviewController"
    }).when("/features", {
        templateUrl: "/templates/features.html",
        controller: "featuresController"
    }).when("/technical", {
        templateUrl: "/templates/technical.html",
        controller: "technicalController"
    }).otherwise({ redirectTo: "/" });

    $locationProvider.html5Mode(true);
});
"use strict";

docsApp.controller("featuresController", function ($scope, featureService) {
    $scope.features = [];

    featureService.getAllFeatures().then(function (callback) {
        return $scope.features = callback;
    });

    var result = [
        "Tag #1"
    ];

    $scope.getSuggestedTags = featureService.getSuggestedTags;
    $scope.getTagClass = function (tag) {
        return "label label-info";
    };

    var createBlankFeature = function () {
        $scope.isAddFeatureCollapsed = true;
        $scope.newFeature = ({ tags: result });
    };
    createBlankFeature();

    $scope.isAddFeatureCollapsed = false;

    $scope.submit = function (newFeature, newFeatureForm) {
        if (newFeatureForm.$invalid)
            return false;

        newFeature.date = new Date().getTime();

        featureService.addFeature(newFeature);

        $scope.features.push(newFeature);

        createBlankFeature();

        return true;
    };

    $scope.cancel = createBlankFeature;
});
docsApp.controller("overviewController", function ($scope) {
});
docsApp.controller("technicalController", function ($scope) {
});
"use strict";

docsApp.directive("imageUploader", function () {
    var allowedMimeTypes = [
        "image/png", "image/jpeg", "image/gif"
    ];

    var definition = {
        restrict: 'EA',
        scope: {
            "images": "="
        },
        replace: true,
        templateUrl: "templates/partials/imageUploader.html",
        controller: function ($scope) {
            $scope.images = [];

            $scope.deleteImage = function (image) {
                var images = $scope.images;

                var index = images.indexOf(image);
                if (index == -1)
                    return;
                images.splice(index, 1);
            };
        },
        link: function (scope, element, attrs) {
            var targetDragDrop = element.find(".target");

            var bindClass = function (options, e) {
                var targetClasses = "dragged";
                e.preventDefault();
                targetDragDrop[options.isAdd ? "addClass" : "removeClass"](targetClasses);
            };

            var handlers = {
                "dragover": function (e) {
                    bindClass({ isAdd: true }, e);
                },
                "dragleave": function (e) {
                    bindClass({ isAdd: false }, e);
                },
                "drop": function (e) {
                    bindClass({ isAdd: false }, e);

                    var handleFile = function (file) {
                        if (allowedMimeTypes.indexOf(file.type) === -1) {
                            console.log("Error file type :: " + file.type);
                            return;
                        }

                        var fileReader = new FileReader();
                        fileReader.onload = function (e) {
                            var text = e.target.result;

                            console.log("Dropped successfully");

                            scope.$apply(function () {
                                scope.images.push({
                                    location: text,
                                    title: "",
                                    description: ""
                                });
                            });
                        };

                        fileReader.readAsDataURL(file);
                    };

                    var files = e.originalEvent.dataTransfer.files;

                    for (var i = 0; i < files.length; i++) {
                        var file = files[i];
                        handleFile(file);
                    }
                    ;
                }
            };

            for (var key in handlers) {
                targetDragDrop.bind(key, handlers[key]);
            }
        }
    };
    return definition;
});
"use strict";

docsApp.directive("menuItem", function ($location) {
    var definition = {
        restrict: 'EA',
        scope: {
            "href": "@",
            "text": "@"
        },
        replace: true,
        template: '<li><a href="{{href}}">{{text}}</a></li>',
        link: function (scope, element, attrs) {
            scope.$on("$routeChangeSuccess", function (next, current) {
                var newLocation = $location.path();
                var isActive = newLocation === scope.href;

                element[isActive ? "addClass" : "removeClass"]("active");
            });
        }
    };
    return definition;
});
"use strict";
docsApp.directive("tags", function ($location) {
    var definition = {
        restrict: 'EA',
        scope: {
            "source": "="
        },
        templateUrl: "templates/partials/tagPartial.html"
    };
    return definition;
});
;

docsApp.service("featureService", function ($resource, $q) {
    var resource = $resource("http://localhost:8000/services/features");

    var service = {
        getAllFeatures: function () {
            return resource.query().$promise;
        },
        addFeature: function (feature) {
            console.log(feature);
            return resource.save(feature);
        },
        getSuggestedTags: function () {
            return "services/features/tags";
        }
    };

    return service;
});
//# sourceMappingURL=out.js.map
