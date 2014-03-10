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
                    templateUrl: "templates/overview.html",
                    controller:"overviewController"
                })
                .when("/readme", {
                    templateUrl: "docs/readme-core.html"
                })
                .when("/features", {
                    templateUrl: "templates/features.html",
                    controller:"featuresController"
                })
                .when("/contributing", {
                    templateUrl: "templates/contributing.html"
                })
                .when("/login", {
                    templateUrl: "templates/login.html",
                    controller:"loginController"
                })
                /**
                 * Developer documentation resources
                 */
                .when("/pluginDocumentation", {
                    templateUrl: "docs/Developing-plugin.html"
                })
                .when("/continuousIntegration", {
                    templateUrl: "docs/build-plugin.html"
                })
                .when("/websiteDocumentation", {
                    templateUrl: "docs/readme-website.html"
                })
                /**
                 * Fall back value
                 */
                .otherwise({redirectTo: "/"})
            // Enabling html5 routing
            $locationProvider.html5Mode(true)
        });