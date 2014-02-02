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
    })


    describe("submit functionality", function () {
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

        });

        it("should update a local copy, and not make a second call to the getAllFeatures service", function() {

        });
    });
});