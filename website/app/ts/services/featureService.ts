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
    /**
     * Return the REST url for suggested tags
     */
    getSuggestedTags(): string
}

/**
 * Create a new service for retrieving feature information
 */
docsApp.service("featureService", ($resource: ng.resource.IResourceService, $q: ng.IQService, appConfig) => {
    // Create a resource which we can access via our lexical closure
    var resource:any = $resource( appConfig.serviceUrl + "/services/features");

    // Define our service
    var service: IFeatureService = {
        getAllFeatures: () => resource.query().$promise,
        addFeature: (feature) => resource.save(feature),
        getSuggestedTags: () => appConfig.serviceUrl + "/services/features/tags"
    };

    return service;
});

