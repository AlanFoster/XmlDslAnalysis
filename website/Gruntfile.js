"use strict";

module.exports = function(grunt) {
	// Load Tasks
	grunt.loadNpmTasks("grunt-ts");
    grunt.loadNpmTasks('grunt-jasmine-node');
    grunt.loadNpmTasks('grunt-execute');

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
    grunt.registerTask("client", ["ts:client"]);

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
        // Services Jasmine Node
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
        }
	});
	
	// By default grunt will run generate the services and run the web server, and listen to client compilation
    grunt.registerTask("default", [
        // Generate initial TS code for the services
        "services",
        // Listen to client changes
        "client"
    ]);
};