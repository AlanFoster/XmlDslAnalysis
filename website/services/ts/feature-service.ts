/// <reference path="./../reference.ts" />

var util = require('util');
var arrayShim = require("./arrayShim.js");
var passport = require("passport");

// Default list of suggested tags
var DefaultTags = {
    CODE_COMPLETION: "CodeCompletion",
    REFACTOR: "Refactor",
    SIMPLE: "Simple",
    CAMEL: "Camel",
    JAVA: "Java",
    XML: "XML"
};

/**
 * Route Creation.
 * Note this assumes the body parser middleware is used.
 *
 * @param app The express JS app
 * @param features The feature dataset - IE Dependency injection
 */
exports.createRoutes = function(app, features: IFeature[]) {
    app.get("/services/features", (req, res, next) => {
        res.json(features);
    });

    app.post("/services/features", (req, res, next) => {
        var newItem = <IFeature> req.body;
        features.push(newItem);
        // Respond with a success
        res.json({});
    });

    app.get("/services/features/tags", (req, res, next) => {
        // Return all default tags which should be suggested, and all custom ones as a JSON array
        var defaultTagValues = [Object.keys(DefaultTags).map(key => DefaultTags[key])];
        var distinctTagValues = features.map(feature => feature.tags);
        var unionTags = defaultTagValues
            .concat(distinctTagValues)
            // flatMap(identity)
            .reduce((a,b) => a.concat(b))
            .distinct();

        res.json(unionTags)
    })
};
