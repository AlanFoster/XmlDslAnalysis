/// <reference path="./../reference.ts" />

var passport = <any> require("passport");
var GoogleStrategy = require('passport-google').Strategy;
var util = require("util");
import repo = require("./DataModelTest");
var config = global.config;

var realm = config.realm;
var sessionSecret =  config.sessionSecret;

/**
 * Registers security requirements for using the application
 * @param express Express reference
 * @param app An instance of the express application
 */
exports.init = <any> ((express, app, userRepository: repo.IUserRepository) => {
    app.configure(function() {

        /**
         * Register serializing/deserializing mechanisms with passportjs.
         *
         * This could interact with a database to store the user across sessions
         */
        (() => {
            passport.serializeUser(function(user, done) {
                util.debug("Invoked - serializeUser");
                done(null, user);
            });
            passport.deserializeUser(function(obj, done) {
                util.debug("Invoked - deserializeUser");
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
                returnURL: realm + "/services/auth/return",
                // The realm that our login applies to
                realm: realm
            },
            // Called only on success
            (identifier, profile, done) => {
                util.debug("Successfully Logged on");

                userRepository.all()
                    .success((users) => {
                        console.log("Got All users :: " + users);
                        done(null, profile);
                    })

                // Store the user if it doesn't already exist
/*                if(!users[identifier]) {
                    users[identifier] = { identifier: identifier, verified: true, isAdmin: false};
                }

                var systemUser = users[identifier];
                // Update the profile details for this current logging session
                systemUser.profile = profile;*/
        }
        ));

        app.use(express.cookieParser());
        app.use(express.methodOverride());
        app.use(express.session({ secret: sessionSecret }));

        // passport.session() middleware could be used also - but not required
        app.use(passport.initialize());

        app.use(passport.session());
    });
});

/**
 * Registers the given routes within the application
 * @param app An instance of the express application
 */
exports.createRoutes = (app) => {
    /**
     * Middleware to authenticate requests
     */
    var securityAuthentication = () => (req, res, next) => {
        util.debug("Successfully called google middleware");
        passport.authenticate("google", { failureRedirect: "/login" })(req, res, next)
    };

    /**
     * Middleware to ensure that the call is authenticated before calling the next operation
     * Otherwise the request is redirected away.
     */
    var authenticationRequired = (req, res, next):any => {
        util.debug("Attempting to access restricted area")

        if(req.isAuthenticated()) { return next(); }
        res.redirect("/login")
    };

    /**
     * Attempts to log in the current user - this will be handled by the
     * passportjs middleware, and subsequently the redirect will occur
     * as expected
     */
    app.get("/services/auth", securityAuthentication(), (req, res) => {
        util.debug("Successfully called login");
        res.redirect("/");
    });

    /**
     * Called as a get request when Google successfully logs in as expected
     */
    app.get("/services/auth/return", securityAuthentication(), (req, res) => {
        util.debug("Successfully returned and logged in");
        res.redirect("/")
    });

    /**
     * Logs out the currently logged in user within the system
     */
    app.get("/services/auth/logout", (req, res) => {
        util.debug("Calling logout");
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
};