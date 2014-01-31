/**
 * Represents the interface for the feature controller's scope
 */
interface IFeatureScope {
    features: IFeature[]
}

/**
 * Features controller
 */
docsApp.controller("featuresController", function($scope: IFeatureScope, featureService: IFeatureService) {
    // Access the webservice, and when it update the scope as expected
    featureService.getAllFeatures()
        .then((callback) => $scope.features = callback);
});