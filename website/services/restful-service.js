/**
 * Main entry point for creating the NodeJS webservice/FileServing mechanism
 */
var express = require("express");
var app = express();
var util = require("util");
var path = require("path");

// Define a config as a global option
global.config = require("konfig")({
    // Override the default path, relative to the app directory
    path:"../services/config"
}).app;

/**
 * Explicitly ensure that the config has defined required configuration.
 * This is to avoid 'undefined' being set as required fields, for instance
 * an 'undefined' value may lead to security issues etc.
 */
(function(config) {
    // The list of required fields which should be defined
    var requiredKeys = [
        "realm",
        "port",
        "sessionSecret"
    ];

    // Ensure that each required field is contained within the configuration
    requiredKeys.forEach(function(key) {
        if(!config[key]) {
            throw new Error("The configuration did not contain the required field " + key);
        }
    })
})(global.config);

// Known services
var featureService = require("./ts/feature-service.js");
var securityService = require("./ts/security-service.js");
var featuresData = require("./ts/data.js").features;

/**
 * Allow middleware to parse the post data of a body
 * Note - this is defined *before* all routes
 */
app.configure(function() {
    app.use(express.bodyParser());
})
app.configure("debug", function() {
    app.use(express.logger());
})

// Load and init our services
securityService.init(express, app);
securityService.createRoutes(app);

featureService.createRoutes(app, featuresData);

// Create file serving mechanism
app.configure(function () {
    // Path to the running app folder
    var appFolder = path.join(__dirname, "./../app");

    // Register Static Directories under the /static path
    app.use(express.static(appFolder));

    var html5Middleware = function (rootFolder, index) {
        index = index || "index.html";
        var indexLocation = path.join(rootFolder, index);
        return function (req, res, next) {
            res.sendfile(indexLocation)
        };
    };

    // Handles web service requests as a middle ware
    app.use(html5Middleware(appFolder));
});

/**
 * Main entry point for creating the webserver and RESTful services
 */
exports.main = function () {
    var port = process.env.PORT || config.port;

    app.listen(port);
    util.puts("REST running on " + port)
};