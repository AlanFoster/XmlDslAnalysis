# Website

This application is used to view the E2E support provided by this plugin.

In terms of technologies the following tools are used 

- [NodeJS (v0.10.25)](http://nodejs.org/)
- GruntJS
- Karma
- AngularJS
- Jasmine 
- TypeScript
- MongoDB (2.4.9)
- HTML5 / CSS3
- Twitter Bootstrap
- npm


## Set-up

### Node

Please ensure that you have [NodeJS (v0.10.25)](http://nodejs.org/) installed, as this will allow you to use NPM from the command line.

	> cd .\website
	> npm install

Ensure that GruntJS cli has been additionally installed globally via NPM, so that it can be used as a global tool via command line

	npm install -g grunt-cli@0.1.13

### MongoDB

###### Setup

######### Windows
- Download MongoDB 2.4.9
- Extract to wherever you wish, ie `C:/`
- Execute

    > cd ${YOUR_PATH}\bin
    > mkdir .\..\pluginWebsite\data
    > mongod --dbpath .\..\pluginWebsite\data

- It should be running with `waiting for connections on port XXXXX`

######### Linux
- Follow the instructions from http://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/
  The following commands are useful

        sudo service mongod start
        sudo service mongod stop
        sudo service mongod restart
- Remember that the security config is in `/etc/mongodb.conf` by default

###### Verification

After you have successfully created the database, and the server is waiting for connections, you will now be able to connect through a different command window, via `mongo.exe`

Join the new database

    > use pluginWebsite

Example commands -

	> db.users.insert({....})
	> db.users.find().pretty()
	
###### Seeding

In order to seed mongodb successfully run the following gruntJS task

	> cd .\website
	> grunt seed
	> ...
	> Done, without errors.

# Running

As this application makes use of GruntJS it is very easy to run both the server/services and the the client application

## Server

The server hosts RESTful webservices, which persist to mongodb, for the client to interact with. The server also hosts a file server for the client to request files from.

It can be started with the command

	> cd .\website
	> grunt services


#### Developing Webstorm

To develop/run/debug the server the following Node configuration can be used.

- `Node Intepreter` - The path to Nodejs
- `Working Directory` - This should point to the `app` directory, ie `.\website\app`
- `JavaScript file` - The main entry point for the server application is `.\website\scripts\start-server.js`

*Note* - Ensure that the grunt task `grunt ts:services` has been called prior to running the node server. For instance this may be done as a simple pre-launch execution step using

    cmd.exe /c "grunt ts:services"

## Client

The client is written as as Single Page Application which uses AngularJS. This can be started with the folowing command

	> cd .\website
	> grunt client

Note - this will trigger `grunt-ts` to continually watch the TypeScript files for changes, there is no need for a manual retrigger when developing additional files.

Once successfully hosted the application should be available via

	http://localhost:8000

Note this port is also configurable via `process.env.PORT` 