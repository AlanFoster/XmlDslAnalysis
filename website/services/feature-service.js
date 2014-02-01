var util = require('util');

/**
 * Raw Data - TODO add to database
 */
var TagTypes = {
    CODE_COMPLETION: "CodeCompletion",
    REFACTOR: "Refactor"
};

var SupportTypes = {
    SIMPLE: "Simple",
    CAMEL: "Camel",
    JAVA: "Java",
    XML: "XML"
};

var features = [
    {
        id: 1,
        title: "Simple Language Injection",
        images: [
            {
                location: "images/paramInsight.png",
                title: "Java DSL Injection",
                description: "Simple Language injection supported within Java DSL"
            }
        ],
        supportTypes: [
            SupportTypes.SIMPLE,
            SupportTypes.JAVA,
            SupportTypes.XML
        ],
        tags: [
            TagTypes.CODE_COMPLETION,
            TagTypes.REFACTOR
        ]
    },
    {
        id: 2,
        title: "Simple Function Contribution",
        images: [
            {
                location: "images/contribution.png",
                title: "Simple Function Contribution",
                description: "Simple Function Contribution"
            }
        ],
        supportTypes: [
            SupportTypes.SIMPLE,
            SupportTypes.JAVA,
            SupportTypes.XML
        ],
        tags: [
            TagTypes.CODE_COMPLETION,
            TagTypes.REFACTOR,
            "Custom"
        ]
    }
];

/**
 * Route Creation.
 * Note this assumes the body parser middleware is used.
 *
 * @param app The express JS app
 */
exports.createRoutes = function(app) {
    // Create our basic API web services
    app.get("/services/restfulTest", function (req, res, next) {
        res.json({"Hello": "World"});
    });

    app.get("/services/features", function(req, res, next) {
        res.json(features);
    });

    app.post("/services/features", function(req, res, next) {
        features.push(req.body);
    });

    app.get("/services/features/tags", function(req, res, next) {
        // distinct shim for arrays
        Array.prototype.distinct = function() {
            var distinctList = [];
            for(var i = 0, length = this.length; i < length; i++) {
                var elem = this[i];
                if(distinctList.indexOf(elem) === -1) {
                    distinctList.push(elem)
                }
            }
            return distinctList;
        }

        // Return all default tags which should be suggested, and all custom ones as a JSON array
        var defaultTagValues = [Object.keys(TagTypes).map(function(key) { return TagTypes[key]; })];
        var distinctTagValues = features.map(function(feature) { return feature.tags; });
        var unionTags = defaultTagValues
            .concat(distinctTagValues)
            // flatMap(identity)
            .reduce(function(a,b) { return a.concat(b); })
            .distinct();

        res.json(unionTags)
    })
};