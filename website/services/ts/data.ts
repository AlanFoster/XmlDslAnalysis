/**
 * Raw Data - TODO add to database
 */
var DefaultTags = {
    CODE_COMPLETION: "CodeCompletion",
    REFACTOR: "Refactor",
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
        tags: [
            DefaultTags.CODE_COMPLETION,
            DefaultTags.REFACTOR
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
        tags: [
            DefaultTags.CODE_COMPLETION,
            DefaultTags.REFACTOR,
            DefaultTags.SIMPLE,
            DefaultTags.JAVA,
            DefaultTags.XML
        ]
    }
];

exports.features = features;