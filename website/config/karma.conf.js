module.exports = function (config) {
    config.set({
        basePath: "../",

        // Template locations
        preprocessors: {
            "**/*.html": ["ng-html2js"]
        },

        ngHtml2JsPreprocessor: {
            // strip app from the file path
            stripPrefix: "app/",
            // Allows access to beforeEach(module("moduleName")) to simply load all templates
            moduleName: "templates"
        },

        files: [
            "app/lib/angular/angular.js",
            "app/lib/angular/angular-*.js",
            "test/lib/angular/angular-mocks.js",
            // Additional module dependency
            "app/lib/ui.bootstrap/**/*.js",
            "app/js/**/*.js",
            "test/unit/**/*.js",

            // Templates
            //"**/*.html"
        ],

        exclude: [
            "app/lib/angular/angular-loader.js",
            "app/lib/angular/*.min.js",
            "app/lib/angular/angular-scenario.js"
        ],

        autoWatch: true,

        frameworks: ["jasmine"],

        browsers: ["Chrome"],

        plugins: [
            "karma-junit-reporter",
            "karma-chrome-launcher",
            "karma-firefox-launcher",
            "karma-jasmine"
        ],

        junitReporter: {
            outputFile: "test_out/unit.xml",
            suite: "unit"
        }
    })
}
