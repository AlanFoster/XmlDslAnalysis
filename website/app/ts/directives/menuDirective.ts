"use strict";

// Directive interface for menu item scope
interface MenuItemScope extends ng.IScope {
    href: string
    text: string
}

/**
 * Directive for creating li menu items
 */
docsApp.directive("menuItem", function($location) {
    var definition: ng.IDirective = {
        // Restrict to elements and attributes
        restrict: 'EA',
        // Scope - Use the attributes provided, as-is
        scope: {
            "href": "@",
            "text": "@"
        },
        // Replace the original DOM element, otherwise we would run into layout issues
        replace: true,
        // Raw template
        template: '<li><a href="{{href}}">{{text}}</a></li>',
        // Provide a linking method, which listens to the route provider's page and
        link: function(scope?: MenuItemScope,
                       element?: ng.IAugmentedJQuery,
                       attrs?: ng.IAttributes
            ) {
            scope.$on("$routeChangeSuccess", function(next: ng.IAngularEvent, current?: any) {
                var newLocation = $location.path();
                var isActive = newLocation === scope.href;
                // Add/Remove the appropriate class when selected or not
                element[isActive ? "addClass" : "removeClass"]("active")
            });
        }
    };
    return definition;
});