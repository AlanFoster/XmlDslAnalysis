/// <reference path="./../seed-reference.ts" />

var AsyncSpec = require("jasmine-async")(jasmine);
import dbPool = require("./../db/Core");
import repo = require("./../ts/DataModelTest");

/**
 * Tests to ensure that the repository pattern for MongoDB works as expected
 */
describe("MongoDB Repositorys", () => {
    /**
     * Grants access to the DB as expected
     */
    var db;

    /**
     * Error handler to be used during tests in which the failure scenario
     * should never occurr
     * @param done The async callback function to invoke under this scenario
     */
    var errorHandler = (done) => (err) => {
        console.log(err);
        expect(err).toBeUndefined();
        expect("This scenario to never happen").toBeUndefined();
        done()
    };

    /**
     * Create a connection to the database as expected
     */
    beforeEach(() => {
        db = dbPool.createDb({ databaseUrl: "127.0.0.1/testDb" });
    });

    /**
     * Close the DB connection
     */
    afterEach(() => {
        db.close();
    });

    describe("Feature Repository", function() {
        var async = new AsyncSpec(this);
        var featureRepository : repo.MongoDbFeatureRepository;

        /**
         * Create a new instance of the feature repository for each test
         */
        beforeEach(() => {
            featureRepository = new repo.MongoDbFeatureRepository(db);
        });

        /**
         * Tear down as expected in order to remove any stale state within
         * the tests
         */
        afterEach(() => {
            db.get(featureRepository.collectionName).drop();
        });

        (<any> async).it("should be empty by default", function(done) {
            featureRepository.all()
                .success((documents) => {
                    expect(documents).toBeDefined();
                    expect(documents.length).toEqual(0);
                    done();
                })
                .error(errorHandler(done))
        });

        (<any> async).it("should allow for new elements to be inserted successfully", (done) => {
            var exampleFeature1 = {
                title: "Title 1",
                date: 1391305155486,
                images: [
                    {
                        location: "data:....",
                        title: "Java DSL Injection",
                        description: "Simple Language injection supported within Java DSL"
                    }
                ],
                tags: ["Custom 1", "Custom 2"]
            };
             featureRepository.insert(exampleFeature1);
             featureRepository.all()
                 .success((features) => {
                     expect(features).toEqual([exampleFeature1])
                     done();
                 })
                 .error(errorHandler(done))
        });
    })
});