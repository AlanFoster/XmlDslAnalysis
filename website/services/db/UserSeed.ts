/// <reference path="./../seed-reference.ts" />

var initialUsers = <any> [{
    identity: "test3",
    verified: true,
    isAdmin: false
}];

/**
 * Allows for the creation of user details within the database system
 * @param repository The repository to populate
 */
export function seedUsers(repository) {
    repository.insert({
        identity: "test5",
        verified: true,
        isAdmin: false
    });
}