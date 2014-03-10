"use strict";

/**
 * Represents the interface for the feature controller's scope
 */
interface ILoginControllerScope extends ng.IScope {
    loginUrl: string
}

/**
 * Login controller
 */
docsApp.controller("loginController", function($scope: ILoginControllerScope, appConfig) {
    $scope.loginUrl = appConfig.serviceUrl + "/services/auth";
});