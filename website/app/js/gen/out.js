"use strict";
var docsApp = angular.module('docsApp', [
    "ngRoute",
    "ngSanitize",
    "ngResource"
]).config(function ($routeProvider, $locationProvider) {
    $routeProvider.when("/", {
        template: "It worked!!! :)"
    }).when("/overview", {
        templateUrl: "/templates/overview.html",
        controller: "overviewController"
    }).otherwise({ redirectTo: "/" });

    $locationProvider.html5Mode(true);
});
docsApp.controller("overviewController", function ($scope) {
});
//# sourceMappingURL=out.js.map
