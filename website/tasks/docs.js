/**
 * Doc Creation is performed in two main stages
 *      - Copying to website
 *      - Compiling Markdown
 */
module.exports = function(grunt) {
    // Markdown -> html
    grunt.registerTask("docs", ["copy:docs.images", "copy:docs.md5", "markdown:docs"]);
};