Setting up Mongodb

    - Install
    - Extract to wherever you wish, ie `C:/`
    - Execute

        cd ${YOUR_PATH}\bin
        mkdir ${YOUR_PATH}\pluginWebsite\data
        mongod --dbpath ${YOUR_PATH}\pluginWebsite\data

    - It should be running with `waiting for connections on port XXXXX`

Testing MongoDB

    After you have successfully created the database, and the server is waiting for connections
    Connect through a different command window, via calling mongo.exe

    Join the new database

        use pluginWebsite

    Example commands -
        db.users.insert({....})
        db.users.find().pretty()