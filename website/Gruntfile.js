"use strict";

/**
 * Access path for GruntJS renaming files when copying markdown files
 * @type {"path"}
 */
var path = require("path");
var util = require("util");

/**
 * Export module definition for grunt to access. Registers tasks and
 * expected configuration for GruntJs to perform actions upon.
 *
 * @param grunt GruntJS reference
 */
module.exports = function(grunt) {
	// Load Tasks
    require("load-grunt-tasks")(grunt);

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

    // Markdown -> html
    grunt.registerTask("docs", ["copy:docs.images", "copy:docs.md5", "markdown:docs"])

    // Compiling services
    grunt.registerTask("services", ["ts:services", "runServer"]);

    // Client definition - compiles and watches TS code
    grunt.registerTask("client", ["docs", "ts:client"]);

    // Runs tests
    grunt.registerTask("test", ["ts:services", "jasmine_node"]);

    // Initial data migration / seed
    grunt.registerTask("seed", ["ts:databaseMigration", "execute:seedDatabase"]);

    // Perform configuration
	grunt.initConfig({
        /**
         * Grunt execute, which is used for invoking node.js processes
         */
        execute: {
            seedDatabase: {
                src: ['services/db/seed.js']
            }
        },
        // Note the convention of names "key": => buildConfiguration
		ts: {
            /**
             * TypeScript Client compilation
             */
			client: {
				// The source typescript files, http://gruntjs.com/configuring-tasks#files
				src: ["app/ts/**/*.ts", "app/ts.d/**/*.d.ts"],
				// If specified, generate this file that you can use for your reference management
				reference: "./app/reference.ts",  
				// If specified, generate an out.js file which is the merged js file				
				out: 'app/js/gen/out.js',
                watch: "app"
			},
            /**
             * TypeScript services compilation
             */
            services: {
                src: ["!services/reference.ts", "services/ts.d/**/*.d.ts", "services/test/**/*.ts", "services/ts/**/*.ts"],
                reference: "./services/reference.ts"
            },
            /**
             * TypeScript datbase seed migration
             */
            databaseMigration: {
                src: ["!services/reference.ts", "services/ts.d/**/*.d.ts", "services/db/**/*.ts"],
                reference: "./services/reference.ts"
            }
		},
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
         * Copy task - Used to move defined markdown documentation + images into the website on deploy
         */
        copy: {
            // Copying markdown files into the appropriate website location
            "docs.md5": {
                files: [
                    // Include all markdown files which aren't in the website docs folder already, to stop recursion :)
                    {
                        expand: true,
                        // Note - we don't allow grunt-contrib to flatten our path, as we need that information
                        // to rename in the format ${fileName}-${directory}.${fileExtension}
                        flatten: false,
                        src: [
                            // MD files
                            "!./apps/docs/*",
                            "./readme.md",
                            "./../plugin/**/*.md",
                            "./../readme.md"
                        ],
                        dest: "./app/docs/",
                        filter: "isFile",
                        /**
                         *  Note - we require additional renaming when copying files, as
                         *  there may be multiple readme.md files - which will simply replace
                         *  the existing files if there are not renamed properly.
                         */
                        rename: function(dest, src, args) {
                            // Normalize the path before attempting to analyse
                            src = path.normalize(src);

                            // Extract the relevant information to create a new file
                            // name format of ${fileName}-${directory}.${fileExtension}
                            var fileExtension = path.extname(src);
                            var dirName = path.dirname(src).split(path.sep).pop();

                            // Provide additional renaming
                            switch(dirName) {
                                // Replace the current directory marker with the actual directory name
                                case ".": dirName = path.resolve(".").split(path.sep).pop(); break;
                                // When the dirname is the root directory, ie `..` substitute it
                                case "..": dirName = "core"; break;
                                default: break;
                            }

                            var previousFileName = path.basename(src, ".md");
                            var newFileName = util.format("%s-%s%s", previousFileName, dirName, fileExtension);

                            // Construct the new path from the union of the original destination file
                            // And the newly created file name
                            var newPath = path.join(dest, newFileName);

                            // Provide statistics when copying our files
                            var values = {
                                fileExtension: fileExtension,
                                dirName: dirName,
                                previousFileName: previousFileName,
                                newFileName: newFileName,
                                newPath: newPath
                            };

                            var debug = Object.keys(values).map(function(key) {
                                return key + " = " + values[key];
                            }).join("\n")

                            grunt.log.debug(debug);

                            return newPath;
                        }
                    }
                ]
            },
            "docs.images": {
                files: [
                    // Include all markdown files which aren't in the website docs folder already, to stop recursion :)
                    {
                        expand: true,
                        flatten: true,
                        src: [
                            // Images
                            "./../docs_images/**"
                        ],
                        dest: "./app/docs_images/",
                        filter: "isFile"
                    }
                ]
            }
        },
        /**
         * Compiling markdown
         */
        markdown: {
            docs: {
                files: [
                    {
                        expand: true,
                        src: "./app/docs/*.md",
                        ext: ".html"
                    }
                ],
                options: {
                    template: "./app/markdownTemplate.jst"
                }
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
	});

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

    // B generate the services and run the web server, and listen to client compilation
    grunt.registerTask("windowsEnv", [
        "ngconstant:windows_env",

        // Generate initial TS code for the services
        "services",
        // Listen to client changes
        "client"
    ]);

    // by default grunt will assume a local dev machine of windows
    grunt.registerTask("default", ["windowsEnd"]);
};