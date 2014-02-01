/**
 * Create our interface for intellisense within other routes which
 * depend on this service
 */
interface IFeatureService {
    /**
     * This service operation gets all features associated with the
     * application
     */
    getAllFeatures(): ng.IPromise<IFeature[]>

    /**
     * Adds the given feature to the webservice
     */
    addFeature(feature: IFeature): ng.IPromise<IFeature>
};

/**
 * Represents a feature definition
 */
interface IFeature {
    title: string
    images: Image[]
    supportTypes: string[]
    tags: string[]
};

/**
 * Represents the details of an associated image
 */
interface Image {
    title: string
    location: string
    description?: string
};

/**
 * Create a new service for retrieving feature information
 */
docsApp.service("featureService", function($resource: ng.resource.IResourceService, $q: ng.IQService) {
    // Create a resource which we can access via our lexical closure
    // TODO See if AngularJS supports configuration
    var resource:any = $resource("http://localhost:8000/services/features");

    // Define our service
    var service: IFeatureService = {
        getAllFeatures: () => resource.query().$promise,
        addFeature: (feature) => {
            console.log(feature)
            return resource.save(feature)
        }
    };

    return service;
});
