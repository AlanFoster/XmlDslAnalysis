module.exports = {
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
                reference: "./services/reference.ts",
                // Use CommonJs implementation when generating module definitions for node
                options: {
                module: "commonjs"
            }
        },
        /**
         * TypeScript database seed migration
         */
        databaseMigration: {
            src: ["services/ts.d/**/*.d.ts", "services/db/**/*.ts"],
                reference: "./services/seed-reference.ts",
                // Use CommonJs implementation when generating module definitions for node
                options: {
                module: "commonjs"
            }
        }
    }
}