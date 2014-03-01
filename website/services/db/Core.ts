/// <reference path="./../seed-reference.ts" />

var mongo = <any> require("mongodb");
var monk = <any> require("monk");

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
 * Creates a new instance of a Db Connection
 */
export function createDb(config: any) {
    return <any> monk(config.databaseUrl);
}