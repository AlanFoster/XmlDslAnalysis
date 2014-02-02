"use strict";

/**
 * Represents the interface for the feature controller's scope
 */
interface IFeatureControllerScope {
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
}

/**
 * Features controller
 */
docsApp.controller("featuresController", function($scope: IFeatureControllerScope, featureService: IFeatureService) {
    // Default to an empty list of features
    $scope.features = [];

    // Access the webservice, and when it suceeds, update the scope as expected
    // This will replace the existing list of features
    featureService.getAllFeatures()
        .then((callback) => $scope.features = callback);

    var result = [
        "Tag #1"
    ];

    $scope.getSuggestedTags = featureService.getSuggestedTags;
    $scope.getTagClass = (tag) => "label label-info";

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
    $scope.submit = (newFeature: IFeature, newFeatureForm: ng.IFormController):boolean => {
        // Only accept forms which are valid
        if(newFeatureForm.$invalid) return false;

        // Call the webservice with our new feature
        // featureService.addFeature(newFeature);

        return true;
    };

    /**
     * Cancels the associated new feature addition
     */
    $scope.cancel = createBlankFeature;
});