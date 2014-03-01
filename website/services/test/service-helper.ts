/// <reference path="./../seed-reference.ts" />

import repo = require("./../ts/DataModelTest");

/**
 * Creates a mock app implementation using jasmine
 */
var createMockApp = () => jasmine.createSpyObj("mockApp", ["get", "post"]);

/**
 * Creates a mock response object
 */
var createResMock = () => jasmine.createSpyObj("res", ["json"]);

/**
 * Creates a mocked request object which returns the given value input
 * @param value The given value to return
 */
var createReqMock = (value?) => ({"body": value});

/**
 * Extracts the given function implementation of the given application mock
 */
var getRouteImplementation = (spy, routeName: String) => {
    // Find the matching definition
    var matchingCall = spy.calls.find(call => call.args[0] === routeName);
    return matchingCall && matchingCall.args[1];
};

/**
 * Calls the given service operation on the application
 *
 * @param mockApp The mocked app
 * @param method The method type, ie post/get/put etc
 * @param servicePath The service path to test
 * @param input The input of the given method, which will be sent as the request body
 */
var callService = (mockApp, method: string, servicePath: string, input?) => {
    // Extract the route implementation to test a call manually
    var routeImplementation = <any> getRouteImplementation(mockApp[method], servicePath);

    // Ensure that the given route exists as expected
    if(!routeImplementation) {
        throw Error("The given route did not exist :: " + servicePath);
    }

    // Invoke the route implementation
    var resMock = createResMock();
    var reqMock = <any> createReqMock(input);
    routeImplementation(reqMock, resMock, undefined);

    return resMock;
}

/**
 * Tests a service route entirely in isolation
 *
 * @param featuresService The features service to test
 * @param features The initially constructed features list
 * @param method The method type, ie post/get/put etc
 * @param servicePath The service path to test
 * @param testTuple The testing tuple which will contain additional testing information
 * @returns {*|T}
 */
var testServiceImplementation = (featuresService, features: IFeature[], method: string, servicePath: string): any => {
    // Mock the main application
    var mockApp = createMockApp();
    featuresService(mockApp, features);

    var resMock = callService(mockApp, method, servicePath)

    return resMock;
};

/**
 * Export the given functions to be accessible via NodeJS require
 */
exports.createMockApp = createMockApp;
exports.callService = callService;
exports.createResMock = createResMock;
exports.getRouteImplementation = getRouteImplementation;
exports.testServiceImplementation = testServiceImplementation;