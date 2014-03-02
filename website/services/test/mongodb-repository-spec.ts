/// <reference path="./../seed-reference.ts" />

var AsyncSpec = require("jasmine-async")(jasmine);
import dbPool = require("./../db/Core");
import repo = require("./../ts/DataModelTest");

/**
 * Tests to ensure that the repository pattern for MongoDB works as expected
 */
describe("MongoDB Repositories", () => {
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

    /**
     * Splices a mongo db id for testing
     * @param obj The mongodb entity
     * @returns The mongodb entity
     */
    var spliceId = function(obj) {
        expect(obj["_id"]).toBeDefined();
        delete obj["_id"];
        return obj;
    };

    describe("User Repository", function() {
        var async = new AsyncSpec(this);
        var userRepository : repo.MongodbUserRepository;

        /**
         * Create a new instance of the feature repository for each test
         */
        beforeEach(() => {
            userRepository = new repo.MongodbUserRepository(db);
        });

        /**
         * Tear down as expected in order to remove any stale state within
         * the tests
         */
        afterEach(() => {
            db.get(userRepository.collectionName).drop();
        });

        /**
         * Creates a new instance of a basic user within the system
         */
        var createExpectedUser = (id?) => JSON.parse(JSON.stringify({
            identity: "UniqueId" + (id || "1"),
            verified: true,
            isAdmin: false
        }));

        (<any> async).it("should allow a new user to be added for the first time", function(done) {
            userRepository.insertIfNew(createExpectedUser())
                .success((user) => {
                    expect((<any> user)._id).toBeDefined();
                    expect(spliceId(user)).toEqual(createExpectedUser());
                    done();
                })
                .error(errorHandler(done))
        });

        (<any> async).it("should upsert if it exists already", function(done) {
            userRepository.insertIfNew(createExpectedUser())
                .success((user) => {
                    var initialId = (<any> user)._id;
                    expect(initialId).toBeDefined();
                    expect(spliceId(user)).toEqual(createExpectedUser());

                   userRepository.insertIfNew(createExpectedUser())
                        .success((user) => {
                            var newId = (<any> user)._id;
                            expect(newId).toEqual(initialId);
                            expect(spliceId(user)).toEqual(createExpectedUser());
                            done();
                        })
                        .error(errorHandler(done))

                    done()
                })
                .error(errorHandler(done))
        });

        (<any> async).it("should allow multiple inserts", function(done) {
            userRepository.insertIfNew(createExpectedUser(1))
                .success((user) => {
                    var initialId = (<any> user)._id;
                    expect(initialId).toBeDefined();
                    expect(spliceId(user)).toEqual(createExpectedUser());

                    userRepository.insertIfNew(createExpectedUser(2))
                        .success((user) => {
                            var newId = (<any> user)._id;
                            expect(newId).not.toEqual(initialId);
                            expect(spliceId(user)).toEqual(createExpectedUser(2));
                            done();
                        })
                        .error(errorHandler(done))

                    done()
                })
                .error(errorHandler(done))
        });
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