"use strict";
var docsApp = angular.module('docsApp', [
    "ui.bootstrap",
    "bootstrap-tagsinput",
    "ngRoute",
    "ngSanitize",
    "ngResource",
    "toaster"
]).config(function ($routeProvider, $locationProvider) {
    $routeProvider.when("/", {
        templateUrl: "/templates/overview.html",
        controller: "overviewController"
    }).when("/readme", {
        templateUrl: "/docs/readme-core.html"
    }).when("/features", {
        templateUrl: "/templates/features.html",
        controller: "featuresController"
    }).when("/contributing", {
        templateUrl: "/templates/contributing.html"
    }).when("/login", {
        templateUrl: "/templates/login.html",
        controller: "loginController"
    }).when("/pluginDocumentation", {
        templateUrl: "/docs/Developing-plugin.html"
    }).when("/continuousIntegration", {
        templateUrl: "/docs/build-plugin.html"
    }).when("/websiteDocumentation", {
        templateUrl: "/docs/readme-website.html"
    }).otherwise({ redirectTo: "/" });

    $locationProvider.html5Mode(true);
});
"use strict";

docsApp.controller("featuresController", function ($scope) {
});
"use strict";

docsApp.controller("featuresController", function ($scope, featureService, toaster) {
    $scope.features = [];

    featureService.getAllFeatures().then(function (callback) {
        return $scope.features = callback;
    });

    $scope.getSuggestedTags = featureService.getSuggestedTags;
    $scope.getTagClass = function (tag) {
        return "label label-info";
    };

    var createBlankFeature = function () {
        $scope.newFeature = ({ tags: [], images: [] });

        if (!$scope.$$phase) {
            $scope.$digest();
        }
    };
    $scope.createBlankFeature = createBlankFeature;
    createBlankFeature();

    var createBlankFeatureAndClose = function () {
        createBlankFeature();
        $scope.isAddFeatureCollapsed = true;
    };

    $scope.isAddFeatureCollapsed = true;

    $scope.submit = function (newFeature, newFeatureForm) {
        if (newFeatureForm.$invalid)
            return false;

        newFeature.date = new Date().getTime();

        featureService.addFeature(newFeature);

        $scope.features.push(newFeature);

        createBlankFeature();

        toaster.pop("success", "Added", "The new feature definition was successfully added");

        return true;
    };

    $scope.cancel = createBlankFeatureAndClose;
});
docsApp.controller("overviewController", function ($scope) {
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

docsApp.service("featureService", function ($resource, $q, appConfig) {
    var resource = $resource(appConfig.serviceUrl + "/services/features");

    var service = {
        getAllFeatures: function () {
            return resource.query().$promise;
        },
        addFeature: function (feature) {
            return resource.save(feature);
        },
        getSuggestedTags: function () {
            return appConfig.serviceUrl + "/services/features/tags";
        }
    };

    return service;
});
docsApp.run(function ($rootScope) {
    hljs.initHighlightingOnLoad();

    $rootScope.$on("$routeChangeSuccess", function (next, current) {
        setTimeout(function () {
            return $('pre code').each(function (i, elem) {
                return hljs.highlightBlock(elem);
            });
        }, 500);
    });
});
docsApp.run(function ($rootScope, $http, appConfig) {
    var userDetailsUrl = appConfig.serviceUrl + "/services/auth/details";
    $http({ method: "get", url: userDetailsUrl }).success(function (serviceResponse) {
        if (serviceResponse && serviceResponse.verified) {
            $rootScope.user = serviceResponse.user;
        }
    });
});
//# sourceMappingURL=out.js.map
