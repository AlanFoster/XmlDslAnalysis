"use strict";

module.exports = function(grunt) {
	// Load Tasks
	grunt.loadNpmTasks("grunt-ts");

    grunt.registerTask("runServer", function() {
        grunt.log.oklns("Starting webserver");
        grunt.util.spawn({cmd: ".\\run.bat"}, function(error, result, code) {
            grunt.log.oklns("Webserver closed");
            if(error) {
                grunt.log.errorlns("Error - " + result + code);
            }
        })
    })

	// Perform configuration
	grunt.initConfig({
		ts: {
			// Note the convention of names "key": => buildConfiguration
			dev: {
				// The source typescript files, http://gruntjs.com/configuring-tasks#files
				src: ["app/ts/**/*.ts", "app/ts.d/**/*.d.ts"],
				// If specified, generate this file that you can use for your reference management
				reference: "./app/reference.ts",  
				// If specified, generate an out.js file which is the merged js file				
				out: 'app/js/gen/out.js',         
				// If specified, watches this directory for changes, and re-runs the current target
				watch: 'app'
			}
		}
	});
	
	// Register the default task
	grunt.registerTask("default", ["runServer", "ts:dev"]);
}