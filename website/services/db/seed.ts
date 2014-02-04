/// <reference path="./../reference.ts" />

/**
 * Load the required data - Note this scripts contain only JSON definitions
 * and /not/ the concrete database connection methods
 */
var userSeed = require("./UserSeed.js");
var mongo = <any> require("mongodb");
var monk = <any> require("monk");
var db = <any> monk("http://localhost:27017/pluginWebsite"); // TODO Provide configuration for the database URL

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
 * Define our known collections
 */
var usersCollection = db.get("users");

/**
 * Teardown
 */
usersCollection.drop();

/**
 * User population
 */
var initialUsers = userSeed.initialusers;
initialUsers.forEach(user => usersCollection.insert(user, errorHandler));

/**
 * Close the database connection
 */
db.close();