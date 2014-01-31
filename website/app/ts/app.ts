"use strict";

/**
 * Main Entry point - The router configuration
 */
var docsApp: ng.IModule =
    angular
        .module('docsApp', [
            "ngRoute",
            "ngSanitize",
            "ngResource"
        ])
        .config(function ($routeProvider, $locationProvider) {
            $routeProvider
                .when("/", {
                    templateUrl: "/templates/overview.html",
                    controller:"overviewController"
                })
                .when("/features", {
                    templateUrl: "/templates/features.html",
                    controller:"featuresController"
                })
                .when("/technical", {
                    templateUrl: "/templates/technical.html",
                    controller:"technicalController"
                })
                .otherwise({redirectTo: "/"})
            // Enabling html5 routing
            $locationProvider.html5Mode(true)
        })