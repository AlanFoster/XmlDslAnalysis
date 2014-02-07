var passport = <any> require("passport");
var GoogleStrategy = require('passport-google').Strategy;
var util = require("util");


var adminId = process.env["ADMIN_ID"];

/**
 * Volatile list of users
 */
var users = {};

// Allow admin mode for this user
users[adminId] = {
    "identifier": adminId,
    "verified": true,
    "isAdmin": true
};


/**
 * Registers security requirements for using the application
 * @param express Express reference
 * @param app An instance of the express application
 */
exports.init = (express, app) => {
    app.configure(function() {

        /**
         * Register serializing/deserializing mechanisms with passportjs.
         *
         * This could interact with a database to store the user across sessions
         */
        (() => {
            passport.serializeUser(function(user, done) {
                util.puts("Invoked - serializeUser");
                done(null, user);
            });
            passport.deserializeUser(function(obj, done) {
                util.puts("Invoked - deserializeUser");
                done(null, obj);
            });
        })();

        /**
         * Defines the google strategy to be used within passportjs
         */
        passport.use(new GoogleStrategy(
            // Configuration
            {
                // The 'callback' URL which will be invoked by google when success/fail
                returnURL: "http://localhost:8000/services/auth/return",
                // The realm that our login applies to
                realm: "http://localhost:8000/"
            },
            // Called only on success
            (identifier, profile, done) => {
                util.puts("Successfully Logged on");
                // Store the user if it doesn't already exist
                if(!users[identifier]) {
                    users[identifier] = { identifier: identifier, verified: true, isAdmin: false};
                }

                var systemUser = users[identifier];
                // Update the profile details for this current logging session
                systemUser.profile = profile;

                // Callback successfully
                done(null, systemUser);
            }
        ));

        app.use(express.cookieParser());
        app.use(express.methodOverride());
        app.use(express.session({ secret: 'yqXk4NRPyikH6x3ZnC7NXNaUKtCKvbxKpV9YIINsAH5w8Av4pGGD9fPrMq' }));

        // passport.session() middleware could be used also - but not required
        app.use(passport.initialize());

        app.use(passport.session());
    });
};

/**
 * Registers the given routes within the application
 * @param app An instance of the express application
 */
exports.createRoutes = (app) => {
    /**
     * Middleware to authenticate requests
     */
    var securityAuthentication = () => (req, res, next) => {
        util.puts("Successfully called google middleware");
        passport.authenticate("google", { failureRedirect: "/login" })(req, res, next)
    }

    /**
     * Middleware to ensure that the call is authenticated before calling the next operation
     * Otherwise the request is redirected away.
     */
    var authenticationRequired = (req, res, next):any => {
        util.puts("Attempting to access restricted area")

        if(req.isAuthenticated()) { return next(); }
        res.redirect("/login")
    };

    app.get("/services/auth", securityAuthentication(), (req, res) => {
        util.puts("Successfully called login");
        res.redirect("/");
    });

    app.get("/services/auth/return", securityAuthentication(), (req, res) => {
        util.puts("Successfully returned and logged in");
        res.redirect("/")
    });

    app.get("/services/auth/logout", (req, res) => {
        util.puts("Calling logout");
        req.logout();
        res.json({success:true});
    });

    /**
     * Provides details regarding the user's logged in status
     */
    app.get("/services/auth/details", (req, res) => {
        var response = {verified: req.isAuthenticated(), user: req.user}
        res.json(response);
    });

    /**
     * Example of access to a secure area
     * Note the use of the authenticationRequired processor within the chain
     */
    app.get("/superSecret", authenticationRequired, (req, res) => res.json({success:true, user: req.user}));
};