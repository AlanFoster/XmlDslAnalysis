# Website

The documentation website associated with this plugin.

This application uses

- [NodeJS (v0.10.25)](http://nodejs.org/)
- GruntJS
- Karma
- AngularJS
- Jasmine 
- TypeScript
- MongoDB (2.4.9)
- HTML5 / CSS3
- Twitter Bootstrap
- NPM

# Install Requirements

... 

# Running

This application consists of both a server and client.

## Server

The server hosts RESTful webservices, which persist to mongodb, for the client to interact with. The server also hosts a file server for the client to request files from.

#### Installing Dependencies

Please ensure that you have [NodeJS (v0.10.25)](http://nodejs.org/) installed, as this will allow you to use NPM from the command line.

	> cd .\website
	> npm install

### MongoDB

###### Setup

- Download MongoDB 2.4.9
- Extract to wherever you wish, ie `C:/`
- Execute

    > cd ${YOUR_PATH}\bin
    > mkdir .\..\pluginWebsite\data
    > mongod --dbpath .\..\pluginWebsite\data

- It should be running with `waiting for connections on port XXXXX`

###### Verification

After you have successfully created the database, and the server is waiting for connections, you will now be able to connect through a different command window, via `mongo.exe`

Join the new database

    > use pluginWebsite

Example commands -

	> db.users.insert({....})
	> db.users.find().pretty()


## Client

As this application makes use of GruntJS it is very easy to run the client application once the server has been deployed.

    grunt client