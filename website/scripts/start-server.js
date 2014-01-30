#!/usr/bin/env node

// Open the restful services main entry point
var restfulService = require("./../services/restful-service.js");

// Start express web server
restfulService.main();