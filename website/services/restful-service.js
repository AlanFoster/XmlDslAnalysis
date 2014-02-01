/**
 * Main entry point for creating the NodeJS webservice/FileServing mechanism
 */
var express = require("express");
var app = express();
var util = require('util');
// Require Path to perform OS specific path manipulation
var path = require('path');

// Known services
var featureService = require("./ts/feature-service.js");
var features = require("./ts/data.js").features;

/**
 * Allow middleware to parse the post data of a body
 * Note - this is defined *before* all routes
 */
app.use(express.bodyParser());

// Load our services
featureService.createRoutes(app, features);

// Create file serving mechanism
app.configure(function () {
    // Path to the running app folder
    var appFolder = path.join(__dirname, "./../app");

    // Register Static Directories under the /static path
    app.use(express.static(appFolder));

    var html5Middleware = function (rootFolder, index) {
        index = index || "index.html";
        var indexLocation = path.join(rootFolder, index)
        return function (req, res, next) {
            res.sendfile(indexLocation)
        };
    }

    // Handles web service requests as a middle ware
    app.use(html5Middleware(appFolder));

    // Allow CORS - Cross Origin Resource Sharing through express middleware
    app.use(function (req, res, next) {
        res.header('Access-Control-Allow-Origin', "*");
        res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
        res.header('Access-Control-Allow-Headers', 'Content-Type');

        next();
    });

    // Create logging middleware
    app.use(function (req, res, next) {
        util.puts("Received request :: ", req);

        // Invoke the next item within the chain
        next();
    })
});

exports.main = function () {
    var port = process.env.PORT || 8000;

    app.listen(port);
    util.puts("REST running on " + port)
}