/// <reference path="./../seed-reference.ts" />

var initialUsers = <any> [{
    identity: "testAccount",
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