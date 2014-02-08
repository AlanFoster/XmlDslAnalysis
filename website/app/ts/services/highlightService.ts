declare var hljs;

/**
  * Performs highlighting on successful route changes
  */
docsApp.run(($rootScope: ng.IRootScopeService) => {
    hljs.initHighlightingOnLoad();
    // Trigger a refresh of hljs on route change success - unfortunately it requires a slight delay after the event
    $rootScope.$on("$routeChangeSuccess", (next: ng.IAngularEvent, current?: any) => {
        setTimeout(() => $('pre code').each((i, elem) => hljs.highlightBlock(elem)), 500)
    });
});
