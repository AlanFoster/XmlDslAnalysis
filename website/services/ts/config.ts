// Define a config as a global option
var konfig = require("konfig")({
    // Override the default path, relative to the app directory
    path:"../services/config"
}).app;

/**
 * Provides a loader for application configuration
 * @param parent The parent context to bind to
 */
export function loadConfig(parent?: any) {
    /**
     * Explicitly ensure that the config has defined required configuration.
     * This is to avoid 'undefined' being set as required fields, for instance
     * an 'undefined' value may lead to security issues etc.
     */
    var requiredKeys = [
        "realm",
        "port",
        "sessionSecret",
        "databaseUrl"
    ];

    // Ensure that each required field is contained within the configuration
    requiredKeys.forEach((key)  => {
        if (!konfig[key]) {
            throw new Error("The configuration did not contain the required field " + key);
        }
    });

    // Bind to the parent object if required
    if(parent) {
        parent.config = konfig;
    }

    // Set the loaded configuration to be available to the caller
    return konfig;
};