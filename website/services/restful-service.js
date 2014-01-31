var express = require("express");
var app = express();
var util = require('util');
// Require Path to perform OS specific path manipulation
var path = require('path');

// Create our basic API web services
app.get("/services/restfulTest", function (req, res, next) {
    res.json({"Hello": "World"});
});

// TODO add to a database
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

app.get("/services/features", function(req, res, next) {
    res.json(features);
});

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
});

exports.main = function () {
    var port = process.env.PORT || 8000;

    app.listen(port);
    util.puts("REST running on " + port)
}