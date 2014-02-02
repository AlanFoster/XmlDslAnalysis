"use strict";

/**
 * Represents the interface for the feature controller's scope
 */
interface IFeatureControllerScope extends ng.IScope {
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
     * @returns true if the operation was successful, false otherwise
     */
    submit(newFeature: IFeature, newFeatureForm: ng.IFormController):  boolean
    /**
     * Cancels the associated new feature addition
     */
    cancel(): void

    /**
     * Returns suggested tags RESTful service location - Unfortunate coincidence of using typeahead.js
     */
    getSuggestedTags():string

    /**
     * Associates a given tag with a class value
     */
    getTagClass(tag: string): string

    /**
     * Creates a new blank feature
     */
    createBlankFeature(): void
}

/**
 * Features controller
 */
docsApp.controller("featuresController", function($scope: IFeatureControllerScope, featureService: IFeatureService, toaster) {
    // Default to an empty list of features
    $scope.features = [];

    // Access the webservice, and when it suceeds, update the scope as expected
    // This will replace the existing list of features
    featureService.getAllFeatures()
        .then((callback) => $scope.features = callback);

    // Functions required for for bootstrap-tags
    $scope.getSuggestedTags = featureService.getSuggestedTags;
    $scope.getTagClass = (tag) => "label label-info";

    // Creates a new blank feature
    var createBlankFeature = ():void => {
        $scope.newFeature = <IFeature> ({ tags: [], images: [] });
        // trigger a digest if required
        if(!$scope.$$phase) {
            $scope.$digest();
        }
    };
    $scope.createBlankFeature = createBlankFeature;
    createBlankFeature();

    var createBlankFeatureAndClose = () => {
        createBlankFeature();
        $scope.isAddFeatureCollapsed = true;
    };

    // Hide the add feature by default
    $scope.isAddFeatureCollapsed = true;

    /**
     * Creates and persists a new feature definition, if the given form is valid
     *
     * @param newFeature
     * @param newFeatureForm
     */
    $scope.submit = (newFeature: IFeature, newFeatureForm: ng.IFormController):boolean => {
        // Only accept forms which are valid
        if(newFeatureForm.$invalid) return false;

        // Inject the desired creation date
        newFeature.date = new Date().getTime();

        // Call the webservice with our new feature
        featureService.addFeature(newFeature);

        // Add the feature locally for an immediate update to the UI
        $scope.features.push(newFeature);

        // Clean up the new feature information
        createBlankFeature();

        // Notify the user
        toaster.pop("success", "Added", "The new feature definition was succesfully added");

        return true;
    };

    /**
     * Cancels the associated new feature addition
     */
    $scope.cancel = createBlankFeatureAndClose;
});