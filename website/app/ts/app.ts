"use strict";

declare var angular: any;

/**
 * Main Entry point - The router configuration
 */
var docsApp =
    angular
        .module('docsApp', [
            "ngRoute",
            "ngSanitize",
            "ngResource"
        ])
        .config(function ($routeProvider, $locationProvider) {
            $routeProvider
                .when("/", {
                    template: "It worked!!! :)"
                })
                .when("/overview", {
                    templateUrl: "/templates/overview.html",
                    controller:"overviewController"
                })
                .otherwise({redirectTo: "/"})
            // Enabling html5 routing
            $locationProvider.html5Mode(true)
        })