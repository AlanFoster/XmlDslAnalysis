/**
 * Create a new service for retrieving feature information.
 * Attempts to verify the status of a logged in user, and assigns the
 * details to the root scope of the application if it is valid
 */
docsApp.run(($rootScope: ng.IRootScopeService, $http: ng.IHttpService) => {
    // TODO See if AngularJS supports configuration - https://npmjs.org/package/grunt-ng-constant
    $http({method: "get", url: "/services/auth/details"})
        .success((serviceResponse) => {
            if(serviceResponse && serviceResponse.verified) {
                (<any> $rootScope).user = serviceResponse.user;
            }
        });
});