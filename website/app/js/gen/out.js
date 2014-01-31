"use strict";
var docsApp = angular.module('docsApp', [
    "ui.bootstrap",
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

docsApp.controller("featuresController", function ($scope, featureService) {
    featureService.getAllFeatures().then(function (callback) {
        return $scope.features = callback;
    });

    $scope.newFeature = {};

    $scope.isAddFeatureCollapsed = false;

    $scope.submit = function (newFeature, newFeatureForm) {
        if (newFeatureForm.$invalid)
            return;

        featureService.addFeature(newFeature);
    };

    $scope.cancel = function () {
        $scope.newFeature = {};
    };
});
docsApp.controller("overviewController", function ($scope) {
});
docsApp.controller("technicalController", function ($scope) {
});
"use strict";

docsApp.directive("imageUploader", function () {
    var definition = {
        restrict: 'EA',
        scope: {
            "images": "="
        },
        replace: true,
        templateUrl: "templates/partials/imageUploader.html",
        link: function (scope, element, attrs) {
            scope.toggleSelected = function (image) {
                scope.currentlySelected = image;
            };

            scope.currentlySelected = undefined;

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

            scope.deleteImage = function (image) {
                var images = scope.images;

                var index = images.indexOf(image);
                if (index == -1)
                    return;
                images.splice(index, 1);
            };
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

;

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
        }
    };

    return service;
});
//# sourceMappingURL=out.js.map
