/// <reference path="./../seed-reference.ts" />

// Provide access configuration files
import configLoader = require("./../ts/config");
var config = configLoader.loadConfig();

/**
 * Load the required data - Note this scripts contain only JSON definitions
 * and /not/ the concrete database connection methods
 */
import userSeed = require("./UserSeed");
var mongo = <any> require("mongodb");
var monk = <any> require("monk");
var db = <any> monk(config.databaseUrl);

import repo = require("../ts/DataModelTest");

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

/**
 * Populate users
 */
var userRepository = new repo.MongodbUserRepository(db);
userSeed.seedUsers(userRepository);

/**
 * Close the database connection
 */
db.close();