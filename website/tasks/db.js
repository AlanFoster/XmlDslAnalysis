module.exports = function(grunt) {
    // Initial data migration / seed
    grunt.registerTask("seed", ["config", "ts:services", "ts:databaseMigration", "execute:seedDatabase"]);
}
