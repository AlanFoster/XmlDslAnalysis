"use strict";

/**
 * Main Entry point - The router configuration
 */
var docsApp: ng.IModule =
    angular
        .module('docsApp', [
            "ui.bootstrap",
            "bootstrap-tagsinput",
            "ngRoute",
            "ngSanitize",
            "ngResource",
            "toaster"
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
                .when("/login", {
                    templateUrl: "/templates/login.html",
                    controller:"loginController"
                })
                .otherwise({redirectTo: "/"})
            // Enabling html5 routing
            $locationProvider.html5Mode(true)
        })