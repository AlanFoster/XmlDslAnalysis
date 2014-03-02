/// <reference path="./../seed-reference.ts" />

// Allow a single admin user within this application by default
var ADMIN_ID = process.env["ADMIN_ID"] || (() => { throw new Error("ADMIN_ID missing environment variable") })();

var initialUsers = <any> [{
    identity: ADMIN_ID,
    verified: true,
    isAdmin: false
}];

/**
 * Allows for the creation of user details within the database system
 * @param repository The repository to populate
 */
export function seed(repository) {
    repository.insert(initialUsers);
}