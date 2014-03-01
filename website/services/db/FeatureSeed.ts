/// <reference path="./../seed-reference.ts" />

/**
 * list of default tags
 */
var DefaultTags = {
    CODE_COMPLETION: "CodeCompletion",
    REFACTOR: "Refactor",
    SIMPLE: "Simple",
    CAMEL: "Camel",
    JAVA: "Java",
    XML: "XML"
};

var features = <any> [
    {
        title: "Simple Language Injection",
        date: 1391305155486,
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
        title: "Simple Function Contribution",
        date: 1391305294892,
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

/**
 * Allows for the creation of user details within the database system
 * @param repository The repository to populate
 */
export function seed(repository) {
    repository.insert(features);
}