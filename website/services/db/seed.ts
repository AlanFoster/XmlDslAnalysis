/// <reference path="./../seed-reference.ts" />

// Provide access configuration files
import configLoader = require("./../ts/config");
var config = configLoader.loadConfig();

// Create a DB Connection
import dbPool = require("./Core")
var db = dbPool.createDb(config);

// Access seed repositories
import repo = require("../ts/DataModelTest");
import userSeed = require("./UserSeed");
import featureSeed = require("./FeatureSeed");

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
var featureRepository = new repo.MongoDbFeatureRepository(db);
featureSeed.seed(featureRepository);

/**
 * Close the database connection
 */
db.close();