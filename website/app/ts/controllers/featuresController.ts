/**
 * Represents the interface for the feature controller's scope
 */
interface IFeatureScope {
    /**
     * The current list of known features
     */
    features: IFeature[]
    /**
     * Whether or not to show the add feature form
     */
    isAddFeatureCollapsed: boolean
    /**
     * The model associated with the add feature form
     */
    newFeature: IFeature
    /**
     * Creates and persists a new feature definition, if the given form is valid
     *
     * @param newFeature
     * @param newFeatureForm
     */
    submit(newFeature: IFeature, newFeatureForm: ng.IFormController):  void
    /**
     * Cancels the associated new feature addition
     */
    cancel(): void
}

/**
 * Features controller
 */
docsApp.controller("featuresController", function($scope: IFeatureScope, featureService: IFeatureService, $q: ng.IQService, $http: ng.IHttpService) {
    // Access the webservice, and when it update the scope as expected
    featureService.getAllFeatures()
        .then((callback) => $scope.features = callback);

    var result = [
        "Tag #1"
    ];

    (<any> $scope).getSuggestedTags = featureService.getSuggestedTags;
    (<any> $scope).getTagClass = () => "label label-info";

    var createBlankFeature = ():IFeature => {
        return <any> ({ tags: result });
    };

    $scope.newFeature = createBlankFeature();

    // Hide the add feature by default
    $scope.isAddFeatureCollapsed = false;

    /**
     * Creates and persists a new feature definition, if the given form is valid
     *
     * @param newFeature
     * @param newFeatureForm
     */
    $scope.submit = (newFeature: IFeature, newFeatureForm: ng.IFormController) => {
        // Only accept forms which are valid
        if(newFeatureForm.$invalid) return;

        featureService.addFeature(newFeature);
    };

    /**
     * Cancels the associated new feature addition
     */
    $scope.cancel = createBlankFeature;
});