"use strict";

/**
 * Test to ensure that the features controller works as expected
 */
describe("Features controller", function () {
    /**
     * The features controller
     */
    var featuresController;

    /**
     * The controller's scope
     */
    var scope;

    /**
     * A mock service
     */
    var mockFeaturesService;

    /**
     * The deferred promise associated with the get all features implementation
     * Usable within tests such as: deferred.resolve(...), deferred.reject(...)
     */
    var deferredGetAllFeatures;

    /**
     * Ensure that we have the docs app module loaded
     */
    beforeEach(module("docsApp"));

    var $q;

    /**
     * Extract the controller before each test - providing mocks for the appropriate services
     */
    beforeEach(inject(function ($controller, $rootScope, _$q_) {
        $q = _$q_;

        scope = $rootScope.$new();
        deferredGetAllFeatures = $q.defer();

        // Provide mock service
        mockFeaturesService = {
            getAllFeatures: function() {
                return deferredGetAllFeatures.promise;
            },
            addFeature: function() { },
            getSuggestedTags: function() { }
        };
        spyOn(mockFeaturesService, 'addFeature').andCallThrough();

        featuresController = $controller("featuresController", {
            $scope: scope,
            featureService: mockFeaturesService
        })
    }));

    describe("Cancel functionality", function () {
        it("should exist", function () {
            expect(scope.cancel).toBeDefined();
        })

        it("should reset the current given new feature details when called", function() {
            var changedFeature = {};
            scope.newFeature = changedFeature;
            scope.cancel();
            // The feature should be different not
            expect(scope.newFeature).not.toBe(changedFeature);
        })
    });

    describe("loading the features with a call to getAllFeatures", function() {
        it("should default to an empty list of features", function() {
            expect(scope.features).toBeDefined();
            expect(scope.features.length).toBe(0);
        });

        describe("should populate the list of features when the service operation succeeds", function() {
            it("should handle an empty array", function() {
                // be sure to call the resolve function within the right ng-cycle
                scope.$apply(function() {
                    deferredGetAllFeatures.resolve([]);
                });

                expect(scope.features).toBeDefined();
                expect(scope.features.length).toBe(0);
            });

            it("should handle an array with features inside of it ", function() {
                var fakeData = [{
                    title: "Title 1",
                    images: [
                        {
                            location: "data:....",
                            title: "Java DSL Injection",
                            description: "Simple Language injection supported within Java DSL"
                        }
                    ],
                    tags: ["Custom 1", "Custom 2"]
                }];

                // be sure to call the resolve function within the right ng-cycle
                scope.$apply(function() {
                    deferredGetAllFeatures.resolve(fakeData);
                });

                expect(scope.features).toBeDefined();
                expect(scope.features.length).toBe(1);
                expect(scope.features).toBe(fakeData);
            })
        });
    });

    describe("submit functionality", function () {
        var newFeature;

        /**
         * Submits the given feature within the controller
         * @param feature The new feature
         */
        var submitFeature = function(feature) {
            scope.newFeature = feature;
            scope.submit(newFeature, {$invalid:false});
        };

        beforeEach(function() {
            newFeature = {
                "tags": [
                    "Tag #1"
                ],
                "images": [
                    {
                        "location": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABI4AAAJiCAYAAAChPYlaAAAAAXNSR…X1G/VbCY0QERERERERMa5lHRwhIiIiIiIiImK4iZzoD/P/HbjaxmX8qBcAAAAASUVORK5CYII=",
                        "title": "My Title",
                        "description": "Hello World"
                    }
                ],
                "title": "My Title"
            };
        });

        it("should exist", function () {
            expect(scope.submit).toBeDefined();
        });

        it("should not save if the form is invalid", function () {
            expect(scope.submit({}, {$invalid: true})).toBe(false);
        });

        it("should not successfully return if the form is valid", function () {
            expect(scope.submit({}, {$invalid: false})).toBe(true);
        });

        it("should call the webservice with the correct information", function() {
            submitFeature(newFeature);
            expect(scope.features.length).toBe(1);
            expect(scope.features[0]).toBe(newFeature);
        });

        it("should update a local copy, and not make a second call to the getAllFeatures service", function() {
            submitFeature(newFeature);
            expect(mockFeaturesService.addFeature).toHaveBeenCalledWith(newFeature);
        });

        it("should collapse the open dialog on success", function() {
            submitFeature(newFeature);
            expect(scope.isAddFeatureCollapsed).toBe(true);
        });

        it("should clear the new feature details within the scope", function(){
            submitFeature(newFeature);
            // Ensure the model has been changed from the previously suggested new feature
            expect(scope.newFeature).not.toBe(newFeature);
        });
    });
});