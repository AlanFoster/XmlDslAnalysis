angular.module('bootstrap-tagsinput', [])
.directive('bootstrapTagsinput', [function() {

  function getItemProperty(scope, property) {
    if (!property)
      return undefined;

    if (angular.isFunction(scope.$parent[property]))
      return scope.$parent[property];

    return function(item) {
      return item[property];
    };
  }

  return {
    restrict: 'EA',
    scope: {
      model: '=ngModel'
    },
    template: '<select multiple></select>',
    replace: false,
    link: function(scope, element, attrs) {
      $(function() {
        if (!angular.isArray(scope.model))
          scope.model = [];

        var select = $('select', element);
        var suggestionUrl = scope.$parent[attrs.suggestionUrl]();

        select.tagsinput({
/*          typeahead : {
            source   : angular.isFunction(scope.$parent[attrs.typeaheadSource]) ? scope.$parent[attrs.typeaheadSource] : null
          },*/
          itemValue: getItemProperty(scope, attrs.itemvalue),
          itemText : getItemProperty(scope, attrs.itemtext),
          tagClass : angular.isFunction(scope.$parent[attrs.tagclass]) ? scope.$parent[attrs.tagclass] : function(item) { return attrs.tagclass; }
        });

          // Provide lookahead if it exists
          if(suggestionUrl) {
              select
                  .tagsinput('input')
                  .typeahead({ prefetch: suggestionUrl, minLength: 0})
                  .bind('typeahead:selected', $.proxy(function (obj, datum) {
                        this.tagsinput('add', datum.value);
                        this.tagsinput('input').typeahead('setQuery', '');
                  }, select));
          }

        for (var i = 0; i < scope.model.length; i++) {
          select.tagsinput('add', scope.model[i]);
        }

        select.on('itemAdded', function(event) {
          if (scope.model.indexOf(event.item) === -1)
            scope.model.push(event.item);
        });

        select.on('itemRemoved', function(event) {
          var idx = scope.model.indexOf(event.item);
          if (idx !== -1)
            scope.model.splice(idx, 1);
        });

          $('.tt-query').on('blur', function() {
              $(this).val('');
          });

        // create a shallow copy of model's current state, needed to determine
        // diff when model changes
        scope.$watch("model", function(newValue, prev) {
          var added = newValue.filter(function(i) {return prev.indexOf(i) === -1;}),
              removed = prev.filter(function(i) {return newValue.indexOf(i) === -1;}),
              i;

          // Remove tags no longer in binded model
          for (i = 0; i < removed.length; i++) {
            select.tagsinput('remove', removed[i]);
          }

          // Refresh remaining tags
          select.tagsinput('refresh');

          // Add new items in model as tags
          for (i = 0; i < added.length; i++) {
            select.tagsinput('add', added[i]);
          }

          select.html("")

        }, false);
      });
    }
  };
}]);