/// <reference path="./../reference.ts" />

var util = require("util");
var arrayShim = require("./arrayShim")
import repo = require("./DataModelTest")

/**
 * Default list of suggested tags
 * These are unioned with the tag service response
 */
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
exports.createRoutes = function(app, repository: repo.IFeatureRepository) {
    /**
     * Represents a generic error handler which can be used within this
     * service. Errors are logged to debug console and an error response
     * is simply propagated to the calling client. Provided via currying.
     * @param res The express response
     */
    var errorHandler = (res) => (err) => {
        util.error(err);
        res.json({});
    };

    /**
     * This service operation gets all features associated with the
     * application
     */
    app.get("/services/features", (req, res, next) => {
        util.debug("Recieved request for operation /services/features");
        repository.all()
            .success((features) => res.json(features))
            .error(errorHandler(res))
    });

    /**
     * Adds the given feature to the webservice
     */
    app.post("/services/features", (req, res, next) => {
        util.debug("Recieved request for operation /services/features");
        var newItem = <IFeature> req.body;
        repository.insert(newItem)
            .success(() => res.json({}))
            .error(errorHandler(res))
    });

    /**
     * Returns all distinct tags within this web service
     */
    app.get("/services/features/tags", (req, res, next) => {
        util.debug("Recieved request for operation /services/features/tags");
        // Return all default tags which should be suggested, and all custom ones as a JSON array
        var defaultTagValues = [Object.keys(DefaultTags).map(key => DefaultTags[key])];

        repository.getTags()
            .success((tags) => {
                var unionTags = defaultTagValues
                    .concat(tags)
                    .reduce((a,b) => a.concat(b)) // flatMap(identity)
                    .distinct();

                res.json(unionTags)
            })
            .error(errorHandler(res));
    })
};
