/**
 * Main entry point for creating the NodeJS webservice/FileServing mechanism
 */
var express = require("express");
var app = express();
var util = require("util");
var path = require("path");

// Provide access to global config
var configLoader = require("./ts/config.js");
configLoader.loadConfig(global);

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