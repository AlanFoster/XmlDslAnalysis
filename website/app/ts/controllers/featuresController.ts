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

interface IFeature {
    title: string
    images: Image[]
    supportTypes: string[]
    tags: string[]
};

interface Image {
    title: string
    location: string
    description?: string
}

docsApp.controller("featuresController", function($scope) {
    // TODO Exteranlise into a testable/mockable service
    var features: IFeature[] = [
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

    $scope.features = features;
});