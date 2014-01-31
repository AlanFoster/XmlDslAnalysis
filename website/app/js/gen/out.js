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
var TagTypes = {
    CODE_COMPLETION: "CodeCompletion",
    REFACTOR: "Refactor"
};

var SupportTypes = {
    SIMPLE: "Simple",
    CAMEL: "Camel",
    JAVA: "Java",
    XML: "XML"
};

;

docsApp.controller("featuresController", function ($scope) {
    var features = [
        {
            title: "Simple Language Injection",
            images: [
                {
                    location: "images/paramInsight.png",
                    title: "Java DSL Injection",
                    description: "Simple Language injection supported within Java DSL"
                }
            ],
            supportTypes: [
                SupportTypes.SIMPLE,
                SupportTypes.JAVA,
                SupportTypes.XML
            ],
            tags: [
                TagTypes.CODE_COMPLETION,
                TagTypes.REFACTOR
            ]
        },
        {
            title: "Simple Function Contribution",
            images: [
                {
                    location: "images/contribution.png",
                    title: "Simple Function Contribution",
                    description: "Simple Function Contribution"
                }
            ],
            supportTypes: [
                SupportTypes.SIMPLE,
                SupportTypes.JAVA,
                SupportTypes.XML
            ],
            tags: [
                TagTypes.CODE_COMPLETION,
                TagTypes.REFACTOR
            ]
        }
    ];

    $scope.features = features;
});
docsApp.controller("overviewController", function ($scope) {
});
docsApp.controller("technicalController", function ($scope) {
});

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
//# sourceMappingURL=out.js.map
