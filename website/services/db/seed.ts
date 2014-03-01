/// <reference path="./../seed-reference.ts" />

// Provide access configuration files
import configLoader = require("./../ts/config");
var config = configLoader.loadConfig();

// Access seed repositories
import repo = require("../ts/DataModelTest");
import userSeed = require("./UserSeed");
import featureSeed = require("./FeatureSeed");

// Access the database
var mongo = <any> require("mongodb");
var monk = <any> require("monk");
var db = <any> monk(config.databaseUrl);

/**
 * Generic error handler - IE stop on any failure.
 * MongoDB does not allow transactions/ACID, so there isnt much
 * that we can do here unfortunately...
 */
var errorHandler = (err, doc) => {
    if(err) {
        throw err;
    }
};


/**
 * Teardown all existing DBs
 */
db.get("users").drop();
db.get("features").drop();

/**
 * Populate users
 */
var userRepository = new repo.MongodbUserRepository(db);
userSeed.seed(userRepository);

/**
 * Populate features
 */
var featureRepository = new repo.MongodbFeatureRepository(db);
featureSeed.seed(featureRepository);

/**
 * Close the database connection
 */
db.close();