module.exports = {
    /**
     * Grunt execute, which is used for invoking node.js processes
     */
    execute: {
        seedDatabase: {
            options: {
                cwd: "./app"
            },
            src: ['services/db/seed.js']
        }
    }
};