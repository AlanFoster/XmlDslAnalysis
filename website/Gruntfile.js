"use strict";

/**
 * Access path for GruntJS renaming files when copying markdown files
 * @type {"path"}
 */
var path = require("path");
var util = require("util");

/**
 * Load the config at the given path to a local object, synchronously
 */
function loadConfig(grunt, configPath) {
    var config = {};

    require("fs").readdirSync(configPath).forEach(function(fileName) {
        var filePath = path.join(configPath, fileName).toString();
        var fileConfig = require("./" + filePath)

        grunt.util._.extend(config, fileConfig);
    });

    return config;
}
/**
 * Export module definition for grunt to access. Registers tasks and
 * expected configuration for GruntJs to perform actions upon.
 *
 * @param grunt GruntJS reference
 */
module.exports = function(grunt) {
	// Load Tasks
    require("load-grunt-tasks")(grunt);

    // Custom Tasks
    grunt.loadTasks("tasks");

    // Running Server task
    grunt.registerTask("runServer", function() {
        grunt.log.oklns("Starting webserver");
        grunt.util.spawn({cmd: ".\\run.bat"}, function(error, result, code) {
            grunt.log.oklns("Webserver closed");
            if(error) {
                grunt.log.errorlns("Error - " + result + code);
            }
        })
    });

    // Compiling services
    grunt.registerTask("services", ["ts:services", "runServer"]);

    // Client definition - compiles and watches TS code
    grunt.registerTask("client", ["docs", "ts:client"]);

    // Runs tests
    grunt.registerTask("test", ["ts:services", "jasmine_node"]);

    // Performs the task of configuration with the given environment config
    grunt.registerTask("config", function() {
        var env = grunt.option("env") || grunt.config.get("env");
        grunt.log.writeln("Creating configuration for env : " + env);
        grunt.task.run(["ngconstant:" + env + "_env"]);
    });

	var config = {
        /**
         * The default environment that is currently being deployed/ran on
         * Configurable via grunt command line with --env="linux"
         */
        "env": "windows",

        /**
         * Services Jasmine Node
         */
        jasmine_node: {
            projectRoot: "./services",
            requirejs: false,
            forceExit: false,
            jUnit: {
                report: false,
                savePath : "./build/reports/jasmine/",
                useDotNotation: true,
                consolidate: true
            }
        },
        /**
         * Angularjs Configuration - Compiles an AngularJS module to be used for configuration
         */
        ngconstant: {
            // Define the default options for the plugin - overriding the default template and providing default constants
            options: {
                constants: {
                //    appConfig: grunt.file.readJSON("env/constants.json")
                }
            },
            /**
             * Define the environment configuration for both client and server
             */
            windows_env: [
                createClientConfig("windows"),
                createServerConfig("windows")
            ],
            linux_env: [
                createClientConfig("linux"),
                createServerConfig("linux")
            ]
        }
	};

    // var load the tasks option config
    grunt.util._.extend(config, loadConfig(grunt, "./tasks/options"));

    grunt.initConfig(config);

    /**
     * Creates the ngconstant configuration for the given environment location client
     * @param environment The environment folder to create configuration for
     */
    function createClientConfig(environment) {
        return {
            dest: "app/js/gen/constants.js",
            name: "appConfig",
            templatePath: "env/templates/angular.tpl.ejs",
            constants: {
                appConfig: grunt.file.readJSON("env/" + environment + "/client.json")
            }
        };
    };

    /**
     * Creates the ngconstant configuration for the given environment location server
     * @param environment The environment folder to create configuration for
     */
    function createServerConfig(environment) {
        return {
            dest: "services/config/app.json",
            name: "serverConfig",
            templatePath: "env/templates/konfig.tpl.ejs",
            constants: {
                appConfig: grunt.file.readJSON("env/" + environment + "/server.json")
            }
        }
    };

    // generate the services and run the web server, and listen to client compilation
    grunt.registerTask("all", [
        "config",

        // Generate initial TS code for the services
        "services",
        // Listen to client changes
        "client"
    ]);

    // by default grunt will assume a local dev machine of windows if not overridden with --env="linux" in command line
    grunt.registerTask("default", ["all"]);
};