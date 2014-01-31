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
            TagTypes.REFACTOR
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
};