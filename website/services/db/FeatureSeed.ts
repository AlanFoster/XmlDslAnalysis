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
    XML: "XML",
    GRAPHING: "Graphing"
};

var features = <any> [
    {
        title: "Simple - Parameter Handler",
        date: 1,
        images: [
            {
                location: "images/HeaderAsParamHandler.png",
                title: "Parameter Handling",
                description: "The possible arguments are shown to the user"
            }
        ],
        tags: [
            DefaultTags.CODE_COMPLETION,
            DefaultTags.REFACTOR
        ]
    },
    {
        title: "Simple - Language Injection",
        date: 2,
        images: [
            {
                location: "images/JavaDSLSimpleInjectionContribution.png",
                title: "Java Injection for Simple",
                description: "Java DSL Example with Simple language injected"
            }
        ],
        tags: [
            DefaultTags.CODE_COMPLETION,
            DefaultTags.SIMPLE,
            DefaultTags.JAVA,
            DefaultTags.XML
        ]
    },
    {
        title: "Blueprint Bean Method Completion",
        date: 3,
        images: [
            {
                location: "images/BlueprintBeanMethodCompletion.gif",
                title: "Blueprint Bean Method Completion",
                description: "Ctrl+Space within attributes"
            }
        ],
        tags: [
            DefaultTags.CODE_COMPLETION,
            DefaultTags.SIMPLE,
            DefaultTags.JAVA,
            DefaultTags.XML
        ]
    },
    {
        title: "Simple - Semantic Highlighting",
        date: 4,
        images: [
            {
                location: "images/HeaderAsSemanticHighlighting.png",
                title: "Header As",
                description: "Invalid types as arguments are highlighted"
            }
        ],
        tags: [
            DefaultTags.CODE_COMPLETION,
            DefaultTags.SIMPLE,
            DefaultTags.JAVA,
            DefaultTags.XML
        ]
    },
    {
        title: "Simple - Header Contribution",
        date: 5,
        images: [
            {
                location: "images/PipelineHeaderContribution.png",
                title: "Pipeline Header Contribution",
                description: "Locally declared headers in a pipeline are inferred"
            }
        ],
        tags: [
            DefaultTags.CODE_COMPLETION,
            DefaultTags.SIMPLE,
            DefaultTags.JAVA,
            DefaultTags.XML
        ]
    },
    {
        title: "Blueprint - Remove Header Contribution + Validation",
        date: 6,
        images: [
            {
                location: "images/VideoRemoveHeaderPipeline.gif",
                title: "Pipeline Header Contribution",
                description: "Removing header intellisense, and warning highlighting"
            }
        ],
        tags: [
            DefaultTags.CODE_COMPLETION,
            DefaultTags.SIMPLE,
            DefaultTags.JAVA,
            DefaultTags.XML
        ]
    },
    {
        title: "Body Code Completion",
        date: 7,
        images: [
            {
                location: "images/BodySuggestion.png",
                title: "Body Code Completion",
                description: "Code inference in a body pipeline"
            }
        ],
        tags: [
            DefaultTags.CODE_COMPLETION,
            DefaultTags.SIMPLE,
            DefaultTags.JAVA,
            DefaultTags.XML
        ]
    },
    {
        title: "Graph Creation",
        date: 8,
        images: [
            {
                location: "images/GraphExample.png",
                title: "Graph Creation",
                description: "Graph Creation under the EIP Editor Tab"
            }
        ],
        tags: [
            DefaultTags.XML,
            DefaultTags.GRAPHING
        ]
    },
    {
        title: "Graph Type Hints",
        date: 8,
        images: [
            {
                location: "images/GraphExampleStaticInformation.png",
                title: "Graph Creation - Type Hinting",
                description: "Static Information displayed under tooltips"
            }
        ],
        tags: [
            DefaultTags.XML,
            DefaultTags.GRAPHING
        ]
    },
    {
        title: "Graph Type Hints",
        date: 8,
        images: [
            {
                location: "images/GraphExampleTypeUnion.png",
                title: "Graph Creation - Unioned Type Hinting",
                description: "Example of unioned type information"
            }
        ],
        tags: [
            DefaultTags.XML,
            DefaultTags.GRAPHING
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