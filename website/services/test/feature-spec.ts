/// <reference path="./../reference.ts" />

var featuresService = require("./../ts/feature-service.js");
var arrayShim = require("./../ts/arrayShim.js");
var serviceHelpers = require("./service-helper.js");

/**
 * Tests for ensuring that the features webservice works as expected
 */
describe("Features webservice tests", function() {
    /**
     * Ensure that the webservices register themselves correctly with the express app
     */
    describe("registering services as expected", function() {
        var mockApp;

        beforeEach(function() {
            mockApp = serviceHelpers.createMockApp();
            featuresService.createRoutes(mockApp, []);
        });

        it("should a GET: features service", function() {
            expect(mockApp.get).toHaveBeenCalledWith("/services/features", jasmine.any(Function));
        });

        it("should a GET: features tag service", function() {
            expect(mockApp.get).toHaveBeenCalledWith("/services/features/tags", jasmine.any(Function));
        });

        it("should a POST: features service", function() {
            expect(mockApp.post).toHaveBeenCalledWith("/services/features", jasmine.any(Function));
        })
    });

    describe("Service implementations", function() {

        /**
         * Testing the GET: features service
         */
        describe("The GET: features service", function() {
            /**
             * Tests the get features service
             * @param features The initially constructed features list
             * @param expected The list of expected features when the service is called
             */
            var testGetFeaturesService = (features: IFeature[], expected: IFeature[]) => {
                var resMock = serviceHelpers.testServiceImplementation(featuresService, features, "get", "/services/features")

                // Assert expectations
                expect(resMock.json).toHaveBeenCalledWith(expected)
            };

            it("should handle empty arrays", function() {
                testGetFeaturesService([], [])
            });

            it("should handle non-empty arrays", function() {
                var features = [
                    {
                        id: 1,
                        title: "Title 1",
                        images: [],
                        tags: []
                    },
                    {
                        id: 2,
                        title: "Title 2",
                        images: [],
                        tags: []
                    }
                ];
                testGetFeaturesService(features, features)
            })
        });

        describe("The GET: features tag service", function() {

            /**
             * Tests the tag service
             * @param features The initially constructed features list
             * @param expectedTags The expected tags to use
             */
            var testFeaturesTagService = (features: IFeature[], expectedTags: string[]) => {
                var resMock = serviceHelpers.testServiceImplementation(featuresService, features, "get", "/services/features/tags")

                // Assert expectations
                expect(resMock.json).toHaveBeenCalledWith(expectedTags)
            };

            it("should give a list of default suggestions", function() {
                var initialFeatures = [];
                var expectedTags = ["CodeCompletion", "Refactor", "Simple", "Camel", "Java", "XML"];
                testFeaturesTagService(initialFeatures, expectedTags);
            })

            it("should union the default suggestions with tags within existing features", function() {
                var initialFeatures = [{
                    id: 1,
                    title: "Title 1",
                    images: [],
                    tags: ["Custom 1", "Custom 2"]
                }];
                var expectedTags = ["CodeCompletion", "Refactor", "Simple", "Camel", "Java", "XML", "Custom 1", "Custom 2"];
                testFeaturesTagService(initialFeatures, expectedTags);
            })

            it("should remove duplicates", function() {
                var initialFeatures = [{
                    id: 1,
                    title: "Title 1",
                    images: [],
                    tags: ["Custom 1", "foo", "CodeCompletion", "Custom 2", "Refactor", "Custom 3", "bar"]
                },
                {
                    id: 1,
                    title: "Title 1",
                    images: [],
                    tags: ["Custom 1", "foo", "Refactor", "Custom 3", "bar"]
                }];
                var expectedTags = ["CodeCompletion", "Refactor", "Simple", "Camel", "Java", "XML", "Custom 1", "foo", "Custom 2", "Custom 3", "bar"];
                testFeaturesTagService(initialFeatures, expectedTags);
            })
        });

        describe("The POST: features", function() {
            /**
             * Tests the get features service
             * @param features The initially constructed features list
             * @param feature The new feature to add
             * @param expectedFeatures The list of expected features when the service is called
             */
            var testPostFeaturesService = (features: IFeature[], feature: IFeature, expectedFeatures: IFeature[]) => {
                // Mock the main application
                var mockApp = serviceHelpers.createMockApp();
                featuresService.createRoutes(mockApp, features);

                // Call the post service
                var addServiceMock = serviceHelpers.callService(mockApp, "post", "/services/features", feature);
                expect(addServiceMock.json).toHaveBeenCalled();

                // Ensure that the features service returns the new items
                var serviceFeaturesMock = serviceHelpers.callService(mockApp, "get", "/services/features");
                expect(serviceFeaturesMock.json).toHaveBeenCalledWith(expectedFeatures);
            };

            it("Should add an empty list of features successfully", function () {
                var newFeature = {
                    title: "Title 1",
                    images: [
                        {
                            location: "data:....",
                            title: "Java DSL Injection",
                            description: "Simple Language injection supported within Java DSL"
                        }
                    ],
                    tags: ["Custom 1", "Custom 2"]
                };

                testPostFeaturesService([], newFeature, [newFeature]);
            })
        })
    })
});