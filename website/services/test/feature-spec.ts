/// <reference path="./../reference.ts" />

var featuresService = require("./../ts/feature-service.js");
var arrayShim = require("./../ts/arrayShim.js");

/**
 * Tests for ensuring that the features webservice works as expected
 */
describe("Features webservice tests", function() {
    /**
     * Creates a mock app implementation using jasmine
     */
    var createMockApp = () => jasmine.createSpyObj("mockApp", ["get", "post"]);
    var createResMock = () => jasmine.createSpyObj("res", ["json"]);

    /**
     * Ensure that the webservices register themselves correctly with the express app
     */
    describe("registering services as expected", function() {
        var mockApp;

        beforeEach(function() {
            mockApp = createMockApp();
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
         * Extracts the given function implementation of the given application mock
         */
        var getRouteImplementation = (spy, routeName: String) => {
            // Find the matching definition
            var matchingCall = spy.calls.find(call => call.args[0] === routeName);
            return matchingCall && matchingCall.args[1];
        };

        /**
         * Tests a service route
         *
         * @param features The initially constructed features list
         * @param method The method type, ie post/get/put etc
         * @param servicePath The service path to test
         * @returns {*|T}
         */
        var testServiceImplementation = (features: IFeature[], method: string, servicePath: string): any => {
            // Mock the main application
            var mockApp = createMockApp();
            featuresService.createRoutes(mockApp, features);

            // Extract the route implementation to test a call manually
            var routeImplementation = <any> getRouteImplementation(mockApp[method], servicePath);

            // Invoke the route implementation
            var resMock = createResMock();
            routeImplementation(undefined, resMock, undefined);

            return resMock;
        }


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
                var resMock = testServiceImplementation(features, "get", "/services/features")

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
                var resMock = testServiceImplementation(features, "get", "/services/features/tags")

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
        })
    })
});