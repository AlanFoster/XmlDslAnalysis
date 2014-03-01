/// <reference path="./../reference.ts" />

var util = require("util");
var arrayShim = require("./arrayShim")

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
exports.createRoutes = function(app, features: IFeature[], repository) {
    app.get("/services/features", (req, res, next) => {
        util.debug("Recieved request for operation /services/features");
        repository.all()
            .success((features) => res.json(features))
            .error((err) => {
                util.debug(err);
                res.json({});
            })
    });

    app.post("/services/features", (req, res, next) => {
        util.debug("Recieved request for operation /services/features");
        var newItem = <IFeature> req.body;
        repository.insert(newItem)
            .success(() => res.json({}))
            .error((err) => {
                util.debug(err);
                res.json({});
            })
    });

    app.get("/services/features/tags", (req, res, next) => {
        util.debug("Recieved request for operation /services/features/tags");
        // Return all default tags which should be suggested, and all custom ones as a JSON array
        var defaultTagValues = [Object.keys(DefaultTags).map(key => DefaultTags[key])];

        repository.getTags()
            .success((tags) => {
                var unionTags = defaultTagValues
                    .concat(tags)
                    // flatMap(identity)
                    .reduce((a,b) => a.concat(b))
                    .distinct();

                res.json(unionTags)
            })
            .error((err) => {
                util.debug(err);
                res.json({});
            });
    })
};
