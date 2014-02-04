/// <reference path="./../reference.ts" />

interface IUser {
    identity: string
    verified: boolean
    isAdmin: boolean
}

var initialUsers = <any> [{
    identity: "test3",
    verified: true,
    isAdmin: false
}];

exports.initialusers = <any> initialUsers;