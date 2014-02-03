/// <reference path="./../reference.ts" />

var mongo = <any> require("mongodb");
var monk = <any> require("monk");
// TODO Provide configuration for the database URL
var db = monk("http://localhost:27017/pluginWebsite");

// User population
var initialUsers = [{
    identity: "test2",
    verified: true,
    isAdmin: false
}];


var initialInsertErrorHandler = (err, doc) => {
    if(err) {
        throw err;
    }
};

var users = db.get("users")
initialUsers.forEach(user => users.insert(user, initialInsertErrorHandler));


db.close();