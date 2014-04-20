module.exports = {
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
                    rename: function (dest, src, args) {
                        var path = require("path")
                        var util = require("util")

                        // Normalize the path before attempting to analyse
                        src = path.normalize(src);

                        // Extract the relevant information to create a new file
                        // name format of ${fileName}-${directory}.${fileExtension}
                        var fileExtension = path.extname(src);
                        var dirName = path.dirname(src).split(path.sep).pop();

                        // Provide additional renaming
                        switch (dirName) {
                            // Replace the current directory marker with the actual directory name
                            case ".":
                                dirName = path.resolve(".").split(path.sep).pop();
                                break;
                            // When the dirname is the root directory, ie `..` substitute it
                            case "..":
                                dirName = "core";
                                break;
                            default:
                                break;
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

                        var debug = Object.keys(values).map(function (key) {
                            return key + " = " + values[key];
                        }).join("\n")

                        // grunt.log.debug(debug);

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
                        "./../docs_images/**",
                        "./../plugin/documentation/**"
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
    }
};