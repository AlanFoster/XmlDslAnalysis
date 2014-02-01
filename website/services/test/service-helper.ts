/**
 * Creates a mock app implementation using jasmine
 */
var createMockApp = () => jasmine.createSpyObj("mockApp", ["get", "post"]);
var createResMock = () => jasmine.createSpyObj("res", ["json"]);

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
var testServiceImplementation = (featuresService, features: IFeature[], method: string, servicePath: string): any => {
    // Mock the main application
    var mockApp = createMockApp();
    featuresService.createRoutes(mockApp, features);

    // Extract the route implementation to test a call manually
    var routeImplementation = <any> getRouteImplementation(mockApp[method], servicePath);

    // Invoke the route implementation
    var resMock = createResMock();
    routeImplementation(undefined, resMock, undefined);

    return resMock;
};

/**
 * Export the given objects to be accessible via NodeJS require
 */
exports.createMockApp = createMockApp;
exports.createResMock = createResMock;
exports.testServiceImplementation = testServiceImplementation;